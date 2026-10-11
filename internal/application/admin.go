package application

import (
	"errors"
	"net/http"
	"net/url"
	"strconv"
	"strings"
	"time"

	"bingchunmoli.com/moliapi/internal/store"
	"github.com/gin-gonic/gin"
	"golang.org/x/crypto/bcrypt"
)

var errConflict = errors.New("conflict")
var errMissing = errors.New("missing")

func (s *Server) userInit(c *gin.Context) {
	initialized := false
	if err := s.Store.View(func(st *store.State) error { initialized = st.User == nil; return nil }); err != nil {
		internal(c, err)
		return
	}
	ok(c, initialized)
}

func (s *Server) newSession(c *gin.Context) {
	id, err := token()
	if err != nil {
		internal(c, err)
		return
	}
	s.mu.Lock()
	for key, expiry := range s.sessions {
		if !expiry.After(s.now()) {
			delete(s.sessions, key)
		}
	}
	s.sessions[id] = s.now().Add(12 * time.Hour)
	s.mu.Unlock()
	c.SetSameSite(http.SameSiteLaxMode)
	cfg, err := s.config()
	if err != nil {
		internal(c, err)
		return
	}
	secure := c.Request.TLS != nil || strings.HasPrefix(cfg["publicURL"], "https://")
	origin := c.GetHeader("Origin")
	if secure && origin != "" && origin != "http://"+c.Request.Host && origin != "https://"+c.Request.Host {
		c.SetSameSite(http.SameSiteNoneMode)
	}
	c.SetCookie("MOLI_SESSION", id, 12*3600, "/", "", secure, true)
	ok(c, true)
}

func (s *Server) register(c *gin.Context) {
	var user store.User
	if !bind(c, &user) {
		return
	}
	if strings.TrimSpace(user.Name) == "" || user.Password == "" || len(user.Password) > 72 {
		fail(c, 400, "用户名和密码不能为空，密码不能超过72字节")
		return
	}
	hash, err := bcrypt.GenerateFromPassword([]byte(user.Password), bcrypt.DefaultCost)
	if err != nil {
		internal(c, err)
		return
	}
	user.Password = string(hash)
	err = s.Store.Update(func(st *store.State) error {
		if st.User != nil {
			return errConflict
		}
		st.User = &user
		return nil
	})
	if errors.Is(err, errConflict) {
		fail(c, 409, "管理员已经初始化")
		return
	}
	if err != nil {
		internal(c, err)
		return
	}
	s.newSession(c)
}

func (s *Server) login(c *gin.Context) {
	var input store.User
	if !bind(c, &input) {
		return
	}
	var user *store.User
	if err := s.Store.View(func(st *store.State) error { user = st.User; return nil }); err != nil {
		internal(c, err)
		return
	}
	if user == nil || input.Name != user.Name || bcrypt.CompareHashAndPassword([]byte(user.Password),
		[]byte(input.Password)) != nil {
		fail(c, 401, "用户名或密码错误")
		return
	}
	s.newSession(c)
}

func (s *Server) logout(c *gin.Context) {
	id, _ := c.Cookie("MOLI_SESSION")
	s.mu.Lock()
	delete(s.sessions, id)
	s.mu.Unlock()
	c.SetCookie("MOLI_SESSION", "", -1, "/", "", c.Request.TLS != nil, true)
	ok(c, true)
}

func (s *Server) system(c *gin.Context) {
	cfg, err := s.config()
	if err != nil {
		internal(c, err)
		return
	}
	ok(c, cfg)
}

func (s *Server) updateSystem(c *gin.Context) {
	var cfg map[string]string
	if !bind(c, &cfg) {
		return
	}
	allowed := map[string]bool{}
	for _, key := range strings.Fields("weatherKey weatherUri weatherGeoUri serverSauceKey pcPath mobilePath " +
		"path1080 uploadTempPath uploadTempSecret certificatePath privateKeyPath domain playListId cookies publicURL") {
		allowed[key] = true
	}
	for key := range cfg {
		if !allowed[key] {
			fail(c, 400, "未知配置项: "+key)
			return
		}
	}
	if err := s.Store.Update(func(st *store.State) error {
		for k, v := range cfg {
			st.Config[k] = v
		}
		return nil
	}); err != nil {
		internal(c, err)
		return
	}
	ok(c, true)
}

func tenant(c *gin.Context, fallback string) string {
	value := strings.TrimSpace(c.GetHeader("tenant"))
	if value == "" {
		return fallback
	}
	return value
}

func (s *Server) navigationList(c *gin.Context) {
	list := []store.Navigation{}
	err := s.Store.View(func(st *store.State) error {
		for _, n := range st.Navigations {
			if n.Tenant == tenant(c, "public") {
				list = append(list, n)
			}
		}
		return nil
	})
	if err != nil {
		internal(c, err)
		return
	}
	ok(c, list)
}

func validNavigation(n store.Navigation) bool {
	u, err := url.Parse(n.URL)
	return err == nil && u.Host != "" && (u.Scheme == "http" || u.Scheme == "https") && len(n.Tags) <= 100
}

func assignTags(st *store.State, n *store.Navigation) error {
	if n.Tags == nil {
		n.Tags = []store.Tag{}
	}
	for i := range n.Tags {
		tag := &n.Tags[i]
		if tag.ID == 0 {
			tag.ID = st.ID()
			continue
		}
		found := false
		for ni := range st.Navigations {
			for ti := range st.Navigations[ni].Tags {
				existing := &st.Navigations[ni].Tags[ti]
				if existing.ID == tag.ID {
					*existing = *tag
					found = true
				}
			}
		}
		if !found {
			return errMissing
		}
	}
	return nil
}

func (s *Server) navigationAdd(c *gin.Context) {
	var n store.Navigation
	if !bind(c, &n) {
		return
	}
	if !validNavigation(n) {
		fail(c, 400, "导航地址必须是 HTTP(S) 网址，标签最多100个")
		return
	}
	if n.Tenant == "" {
		n.Tenant = tenant(c, "public")
	}
	err := s.Store.Update(func(st *store.State) error {
		n.ID = st.ID()
		if err := assignTags(st, &n); err != nil {
			return err
		}
		st.Navigations = append(st.Navigations, n)
		return nil
	})
	if errors.Is(err, errMissing) {
		fail(c, 400, "标签不存在")
		return
	}
	if err != nil {
		internal(c, err)
		return
	}
	ok(c, true)
}

func (s *Server) navigationUpdate(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil || id <= 0 {
		fail(c, 400, "无效的导航ID")
		return
	}
	var patch map[string]any
	if !bind(c, &patch) {
		return
	}
	err = s.Store.Update(func(st *store.State) error {
		for i := range st.Navigations {
			if st.Navigations[i].ID != id {
				continue
			}
			n := st.Navigations[i]
			for key, target := range map[string]*string{"title": &n.Title, "des": &n.Description,
				"url": &n.URL, "icon": &n.Icon, "tenant": &n.Tenant} {
				if value, exists := patch[key]; exists && value != nil {
					text, valid := value.(string)
					if !valid {
						return errConflict
					}
					*target = text
				}
			}
			if tags, exists := patch["tagList"]; exists && tags != nil {
				converted, convertErr := decodeTags(st, tags)
				if convertErr != nil {
					return convertErr
				}
				n.Tags = converted
				if err := assignTags(st, &n); err != nil {
					return err
				}
			}
			if !validNavigation(n) {
				return errConflict
			}
			st.Navigations[i] = n
			return nil
		}
		return errMissing
	})
	if errors.Is(err, errMissing) {
		fail(c, 404, "导航或标签不存在")
		return
	}
	if errors.Is(err, errConflict) {
		fail(c, 400, "无法更新导航")
		return
	}
	if err != nil {
		internal(c, err)
		return
	}
	ok(c, true)
}

func (s *Server) navigationDelete(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil || id <= 0 {
		fail(c, 400, "无效的导航ID")
		return
	}
	err = s.Store.Update(func(st *store.State) error {
		for i, n := range st.Navigations {
			if n.ID == id {
				st.Navigations = append(st.Navigations[:i], st.Navigations[i+1:]...)
				return nil
			}
		}
		return errMissing
	})
	if errors.Is(err, errMissing) {
		ok(c, false)
		return
	}
	if err != nil {
		internal(c, err)
		return
	}
	ok(c, true)
}

func (s *Server) pushLogs(c *gin.Context) {
	var logs []store.PushLog
	if err := s.Store.View(func(st *store.State) error { logs = st.PushLogs; return nil }); err != nil {
		internal(c, err)
		return
	}
	ok(c, logs)
}
