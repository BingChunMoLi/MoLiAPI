package main

import (
	"net/http/httptest"
	"os"
	"path/filepath"
	"strings"
	"testing"

	"bingchunmoli.com/moliapi/internal/application"
	"bingchunmoli.com/moliapi/internal/store"
	"github.com/gin-gonic/gin"
)

func TestRouterAndAdminFallback(t *testing.T) {
	gin.SetMode(gin.TestMode)
	dir := t.TempDir()
	t.Setenv("ADMIN_DIST", dir)
	if err := os.WriteFile(filepath.Join(dir, "index.html"), []byte("<html>admin</html>"), 0600); err != nil {
		t.Fatal(err)
	}
	if err := os.Mkdir(filepath.Join(dir, "assets"), 0700); err != nil {
		t.Fatal(err)
	}
	if err := os.WriteFile(filepath.Join(dir, "assets", "app.js"), []byte("const app = true"), 0600); err != nil {
		t.Fatal(err)
	}
	database, _ := store.Open("")
	app, err := application.New(application.Options{Store: database})
	if err != nil {
		t.Fatal(err)
	}
	r := newRouter(app)
	for _, tc := range []struct {
		path   string
		status int
		body   string
	}{
		{"/", 200, "Welcome"}, {"/admin/system", 200, "admin"}, {"/admin/assets/missing.js", 404, ""},
		{"/api/user/init", 200, `"data":true`}, {"/api/system", 401, "请先登录"},
		{"/api/shici/1", 200, "天阶夜色凉如水"}, {"/api/yiyan/1", 200, "hitokoto"},
		{"/actuator/health", 200, "UP"}, {"/actuator/prometheus", 200, "moli_http_requests_total"},
		{"/ip", 200, `"ip"`}, {"/api/ip", 200, `"code":"00000"`},
		{"/admin/assets/app.js", 200, "const app"}, {"/admin/%5C..%5Csecret", 400, ""},
	} {
		response := httptest.NewRecorder()
		r.ServeHTTP(response, httptest.NewRequest("GET", tc.path, nil))
		if response.Code != tc.status || !strings.Contains(response.Body.String(), tc.body) {
			t.Errorf("%s: HTTP %d %s", tc.path, response.Code, response.Body.String())
		}
	}
}
