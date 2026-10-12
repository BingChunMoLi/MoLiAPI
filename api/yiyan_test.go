package api

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"os"
	"testing"

	"github.com/gin-gonic/gin"
)

func TestYiYan(t *testing.T) {
	err := os.Chdir("..")
	if err != nil {
		t.Fatalf("Failed to change working directory: %v", err)
	}
	defer os.Chdir("api")

	r := setupRouter()
	r.GET("/yiyan/random", YiYan)

	req, _ := http.NewRequest("GET", "/yiyan/random", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("Expected status %d, got %d", http.StatusOK, w.Code)
	}

	var yiyan YiYanEntity
	err = json.Unmarshal(w.Body.Bytes(), &yiyan)
	if err != nil {
		t.Fatalf("Failed to parse response: %v", err)
	}
	if yiyan.Hitokoto == "" {
		t.Errorf("Expected a valid YiYanEntity, got %+v", yiyan)
	}
}

func TestPoetryText(t *testing.T) {
	err := os.Chdir("..")
	if err != nil {
		t.Fatalf("Failed to change working directory: %v", err)
	}
	defer os.Chdir("api")

	r := setupRouter()
	r.GET("/poetry", Poetry)

	req, _ := http.NewRequest("GET", "/poetry?type=text", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("Expected status %d, got %d", http.StatusOK, w.Code)
	}

	body := w.Body.String()
	if body == "" {
		t.Error("Expected non-empty body")
	}
}

func TestPoetry(t *testing.T) {
	err := os.Chdir("..")
	if err != nil {
		t.Fatalf("Failed to change working directory: %v", err)
	}
	defer os.Chdir("api")

	r := setupRouter()
	r.GET("/poetry", Poetry)

	req, _ := http.NewRequest("GET", "/poetry", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("Expected status %d, got %d", http.StatusOK, w.Code)
	}
}

func BenchmarkYiYan(b *testing.B) {
	b.Chdir("..")
	gin.SetMode(gin.TestMode)
	r := gin.New()
	r.GET("/yiyan/random", YiYan)
	request := httptest.NewRequest("GET", "/yiyan/random?type=d", nil)
	// Warm any lazy data before measuring steady-state requests.
	r.ServeHTTP(httptest.NewRecorder(), request)
	b.ReportAllocs()
	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		w := httptest.NewRecorder()
		r.ServeHTTP(w, request)
		if w.Code != 200 {
			b.Fatal(w.Body.String())
		}
	}
}
