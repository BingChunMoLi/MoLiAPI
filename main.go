package main

import (
	"context"
	"errors"
	"log"
	"net/http"
	"os"
	"os/signal"
	"path"
	"path/filepath"
	"strings"
	"syscall"
	"time"
	_ "time/tzdata"

	"bingchunmoli.com/moliapi/api"
	"bingchunmoli.com/moliapi/internal/application"
	"bingchunmoli.com/moliapi/internal/delivery"
	"bingchunmoli.com/moliapi/internal/metrics"
	"bingchunmoli.com/moliapi/internal/store"
	"bingchunmoli.com/moliapi/tasks/cert"
	"github.com/gin-gonic/gin"
)

func newRouter(app *application.Server) *gin.Engine {
	r := gin.New()
	proxies := []string{}
	for _, item := range strings.Split(os.Getenv("TRUSTED_PROXIES"),
		",") {
		if strings.TrimSpace(item) != "" {
			proxies = append(proxies, strings.TrimSpace(item))
		}
	}
	if err := r.SetTrustedProxies(proxies); err != nil {
		panic(err)
	}
	r.Use(gin.Recovery(), metrics.Middleware(), app.Middleware())
	r.GET("/", func(c *gin.Context) { c.String(200, "Welcome to moliapi!") })
	r.StaticFile("/dashboard", "./ui/dashboard.html")
	r.GET("/api/stats", api.GetStats)
	r.GET("/api/certs", api.GetCertStatus)
	r.GET("/yiyan/random", api.YiYan)
	r.GET("/img/random", api.Img)
	r.GET("/qrcode", api.QrCode)
	r.GET("/ip", api.IpQuery)
	r.GET("/bing/img", api.BingImg)
	r.GET("/poetry", api.Poetry)
	r.GET("/qq/avatar", api.QqAvatar)
	r.GET("/qq/qzone_avatar", api.QzoneAvatar)
	r.GET("/xunlei", api.XunleiConvert)
	r.GET("/ua", api.UserAgent)
	r.POST("/cert/update", api.UpdateCert)
	app.Register(r)
	compat := r.Group("/api")
	app.Register(compat)
	compat.GET("/yiyan/random", app.QuoteRandom)
	compat.GET("/ip", func(c *gin.Context) { c.JSON(200, gin.H{"code": "00000", "msg": "一切OK", "data": c.ClientIP()}) })
	r.GET("/actuator/health", func(c *gin.Context) {
		if err := app.Store.View(func(_ *store.State) error { return nil }); err != nil {
			c.JSON(503, gin.H{"status": "DOWN"})
			return
		}
		c.JSON(200, gin.H{"status": "UP"})
	})
	r.GET("/actuator/prometheus", metrics.Prometheus)
	r.GET("/admin", func(c *gin.Context) { c.Redirect(302, "/admin/") })
	r.GET("/admin/*file", func(c *gin.Context) {
		root := os.Getenv("ADMIN_DIST")
		if root == "" {
			root = "web/admin/dist"
		}
		if strings.Contains(c.Param("file"), `\`) {
			c.Status(400)
			return
		}
		requested := path.Clean("/" + c.Param("file"))
		target := filepath.Join(root, filepath.FromSlash(strings.TrimPrefix(requested, "/")))
		info, err := os.Stat(target)
		if err != nil || info.IsDir() {
			if filepath.Ext(requested) != "" {
				c.Status(404)
				return
			}
			target = filepath.Join(root, "index.html")
		}
		if _, err = os.Stat(target); err != nil {
			c.String(503, "请先构建 web/admin，或使用包含前端的 Docker 镜像")
			return
		}
		c.File(target)
	})
	return r
}

func run() error {
	if os.Getenv("GIN_MODE") == "" {
		gin.SetMode(gin.ReleaseMode)
	}
	zone := os.Getenv("TZ")
	if zone == "" {
		zone = "Asia/Shanghai"
	}
	location, err := time.LoadLocation(zone)
	if err != nil {
		return err
	}
	var database store.Store
	if dsn := os.Getenv("MYSQL_DSN"); dsn != "" {
		database, err = store.OpenMySQL(dsn)
	} else {
		path := os.Getenv("DATA_PATH")
		if path == "" {
			path = "data/state.json"
		}
		database, err = store.Open(path)
	}
	if err != nil {
		return err
	}
	defer database.Close()
	push, err := delivery.PushFromEnvironment()
	if err != nil {
		return err
	}
	options := application.Options{Store: database, Config: application.EnvironmentConfig(),
		Origins: strings.FieldsFunc(os.Getenv("CORS_ORIGINS"), func(r rune) bool { return r == ',' }),
		Now:     func() time.Time { return time.Now().In(location) }}
	if mail := delivery.MailFromEnvironment(); mail != nil {
		options.Mailer = mail
	}
	if push != nil {
		options.Notifier = push
	}
	app, err := application.New(options)
	if err != nil {
		return err
	}
	cert.SetLegacyConfigProvider(func() map[string]string {
		cfg, err := app.Configuration()
		if err != nil {
			log.Printf("read certificate configuration: %v", err)
			return nil
		}
		return cfg
	})
	defer cert.SetLegacyConfigProvider(nil)
	ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer stop()
	scheduler, err := app.StartTasks(ctx)
	if err != nil {
		return err
	}
	defer scheduler.Stop()
	certScheduler := cert.InitCron()
	certScheduler.Start()
	defer certScheduler.Stop()
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}
	server := &http.Server{Addr: ":" + port, Handler: newRouter(app), ReadHeaderTimeout: 5 * time.Second,
		ReadTimeout: 30 * time.Second, WriteTimeout: 30 * time.Minute, IdleTimeout: 60 * time.Second}
	done := make(chan error, 1)
	go func() { log.Printf("MoLiAPI listening on %s", server.Addr); done <- server.ListenAndServe() }()
	select {
	case err = <-done:
		if !errors.Is(err, http.ErrServerClosed) {
			return err
		}
	case <-ctx.Done():
		shutdown, cancel := context.WithTimeout(context.Background(), 30*time.Second)
		defer cancel()
		return server.Shutdown(shutdown)
	}
	return nil
}

func main() {
	if err := run(); err != nil {
		log.Fatal(err)
	}
}
