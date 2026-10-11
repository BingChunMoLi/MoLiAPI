package application

import (
	"context"
	"crypto/rand"
	"encoding/hex"
	"errors"
	"fmt"
	"log"
	"net/http"
	"os"
	"strings"
	"sync"
	"time"

	"bingchunmoli.com/moliapi/internal/store"
	"github.com/gin-gonic/gin"
)

type Mailer interface {
	Send(context.Context, string, string, string) error
}
type Notifier interface {
	Send(context.Context, store.Device, string, string) error
}

type Options struct {
	Store    store.Store
	Client   *http.Client
	Mailer   Mailer
	Notifier Notifier
	Config   map[string]string
	Origins  []string
	Now      func() time.Time
}

type Server struct {
	Store     store.Store
	Client    *http.Client
	Mailer    Mailer
	Notifier  Notifier
	defaults  map[string]string
	origins   []string
	now       func() time.Time
	mu        sync.Mutex
	sessions  map[string]time.Time
	pending   map[string]time.Time
	jobs      sync.Mutex
	quoteOnce sync.Once
	quotes    []map[string]any
	quoteErr  error
}

func New(o Options) (*Server, error) {
	if o.Store == nil {
		return nil, errors.New("store is required")
	}
	if o.Client == nil {
		o.Client = &http.Client{Timeout: 20 * time.Second}
	}
	if o.Now == nil {
		o.Now = time.Now
	}
	defaults := map[string]string{"weatherUri": "devapi.qweather.com", "weatherGeoUri": "geoapi.qweather.com",
		"bingURL": "https://www.bing.com", "musicURL": "https://music.163.com",
		"bilibiliURL": "https://api.bilibili.com",
		"hostURL":     "https://raw.githubusercontent.com/neoFelhz/neohosts/gh-pages/basic/hosts"}
	for key, value := range o.Config {
		defaults[key] = value
	}
	s := &Server{Store: o.Store, Client: o.Client, Mailer: o.Mailer, Notifier: o.Notifier,
		defaults: defaults, origins: o.Origins, now: o.Now, sessions: map[string]time.Time{}, pending: map[string]time.Time{}}
	if err := s.Store.View(func(_ *store.State) error { return nil }); err != nil {
		return nil, err
	}
	return s, nil
}

func (s *Server) config() (map[string]string, error) {
	cfg := map[string]string{}
	for k, v := range s.defaults {
		cfg[k] = v
	}
	err := s.Store.View(func(state *store.State) error {
		for k, v := range state.Config {
			cfg[k] = v
		}
		return nil
	})
	return cfg, err
}

func (s *Server) Configuration() (map[string]string, error) { return s.config() }

func ok(c *gin.Context, data any) {
	c.JSON(http.StatusOK, gin.H{"code": "00000", "msg": "一切OK", "data": data})
}
func fail(c *gin.Context, status int, message string) {
	code := "A0001"
	if status >= 500 {
		code = "B0001"
	}
	if status == http.StatusBadGateway {
		code = "C0001"
	}
	c.AbortWithStatusJSON(status, gin.H{"code": code, "msg": message, "data": nil})
}
func internal(c *gin.Context, err error) {
	log.Printf("%s: %v", c.FullPath(), err)
	fail(c, 500, "数据操作失败")
}

func token() (string, error) {
	b := make([]byte, 32)
	if _, err := rand.Read(b); err != nil {
		return "", err
	}
	return hex.EncodeToString(b), nil
}

func bind(c *gin.Context, target any) bool {
	c.Request.Body = http.MaxBytesReader(c.Writer, c.Request.Body, 2<<20)
	if err := c.ShouldBindJSON(target); err != nil {
		fail(c, 400, "请求体格式错误")
		return false
	}
	return true
}

func (s *Server) RequireLogin(c *gin.Context) {
	id, err := c.Cookie("MOLI_SESSION")
	s.mu.Lock()
	expiry, exists := s.sessions[id]
	if exists && !expiry.After(s.now()) {
		delete(s.sessions, id)
		exists = false
	}
	s.mu.Unlock()
	if err != nil || !exists {
		fail(c, 401, "请先登录")
		return
	}
	c.Next()
}

func (s *Server) Middleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		origin := c.GetHeader("Origin")
		sameOrigin := origin == "http://"+c.Request.Host || origin == "https://"+c.Request.Host
		allowed := sameOrigin
		for _, item := range s.origins {
			if origin == item {
				allowed = true
			}
		}
		if origin != "" && allowed {
			c.Header("Vary", "Origin")
			c.Header("Access-Control-Allow-Origin", origin)
			c.Header("Access-Control-Allow-Credentials", "true")
			c.Header("Access-Control-Allow-Headers", "Content-Type, Authorization, tenant, X-API-Key")
			c.Header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
		}
		if c.Request.Method == http.MethodOptions && origin != "" {
			if !allowed {
				fail(c, 403, "跨域来源未允许")
				return
			}
			c.AbortWithStatus(204)
			return
		}
		if origin != "" && !allowed && c.Request.Method != http.MethodGet && c.Request.Method != http.MethodHead {
			fail(c, 403, "跨域来源未允许")
			return
		}
		c.Next()
	}
}

func (s *Server) Register(r gin.IRouter) {
	r.GET("/user/init", s.userInit)
	r.PUT("/user/register", s.register)
	r.POST("/user/login", s.login)
	r.POST("/user/logout", s.logout)
	r.GET("/system", s.RequireLogin, s.system)
	r.POST("/system", s.RequireLogin, s.updateSystem)
	r.GET("/navigation", s.navigationList)
	r.PUT("/navigation", s.RequireLogin, s.navigationAdd)
	r.POST("/navigation/:id", s.RequireLogin, s.navigationUpdate)
	r.DELETE("/navigation/:id", s.RequireLogin, s.navigationDelete)
	r.GET("/navigation/export", s.RequireLogin, s.navigationList)
	r.PUT("/daily", s.dailyAdd)
	r.PUT("/daily/all", s.dailyAddAll)
	r.GET("/daily", s.dailyList)
	r.POST("/daily/signed", s.dailySigned)
	r.GET("/daily/check", s.dailyCheck)
	r.GET("/daily/param", s.dailyParam)
	r.GET("/daily/query", s.dailyQuery)
	r.POST("/app", s.deviceSave)
	r.GET("/app/:name/:model/:androidId", s.deviceFind)
	s.registerPublic(r)
	s.registerWeather(r)
	r.GET("/tasks", s.RequireLogin, s.taskList)
	r.POST("/tasks/:name", s.RequireLogin, s.taskRun)
	r.GET("/push/logs", s.RequireLogin, s.pushLogs)
}

func EnvironmentConfig() map[string]string {
	names := map[string]string{"WEATHER_KEY": "weatherKey", "WEATHER_URL": "weatherUri",
		"WEATHER_GEO_URL": "weatherGeoUri",
		"PUBLIC_URL":      "publicURL", "PC_PATH": "pcPath", "MOBILE_PATH": "mobilePath", "IMAGE_1080_PATH": "path1080",
		"PLAYLIST_ID": "playListId", "NETEASE_COOKIE": "cookies", "BILI_FAV_ID": "biliFavId", "BILI_COOKIE": "biliCookie",
		"DOWNLOAD_DIR": "downloadDir", "DOWNLOAD_COMMAND": "downloadCommand", "UPLOAD_COMMAND": "uploadCommand",
		"NOTIFY_EMAIL": "notifyEmail", "IP_LOOKUP_URL": "ipLookupURL", "TASKS": "tasks"}
	cfg := map[string]string{}
	for env, key := range names {
		if value := os.Getenv(env); value != "" {
			cfg[key] = value
		}
	}
	home, _ := os.UserHomeDir()
	for key, folder := range map[string]string{"pcPath": "pc", "mobilePath": "mobile", "path1080": "1080"} {
		if cfg[key] == "" {
			cfg[key] = home + "/.api/img/" + folder
		}
	}
	return cfg
}

func endpoint(base string) string {
	if !strings.Contains(base, "://") {
		base = "https://" + base
	}
	return strings.TrimRight(base, "/")
}

func validHTTP(value string) bool {
	return strings.HasPrefix(value, "https://") || strings.HasPrefix(value, "http://")
}

func (s *Server) logPush(kind, target, title string, sendErr error) error {
	err := s.Store.Update(func(st *store.State) error {
		st.PushLogs = append(st.PushLogs, store.PushLog{Created: s.now(), Type: kind, Target: target,
			Title: title, Success: sendErr == nil})
		if len(st.PushLogs) > 1000 {
			st.PushLogs = st.PushLogs[len(st.PushLogs)-1000:]
		}
		return nil
	})
	if sendErr != nil {
		return fmt.Errorf("%s delivery: %w", kind, sendErr)
	}
	return err
}
