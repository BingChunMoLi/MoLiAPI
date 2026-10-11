package api

import (
	"net/http"
	"net/http/httptest"
	"os"
	"path/filepath"
	"testing"
)

func mockBing(t *testing.T) {
	t.Helper()
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.Write([]byte(`{"images":[{"url":"/image.jpg"}]}`))
	}))
	previousClient, previousURL := bingClient, bingBaseURL
	bingClient, bingBaseURL = server.Client(), server.URL
	t.Cleanup(func() { bingClient, bingBaseURL = previousClient, previousURL; server.Close() })
}

func TestBingImg(t *testing.T) {
	mockBing(t)
	r := setupRouter()
	r.GET("/bing/img", BingImg)

	req, _ := http.NewRequest("GET", "/bing/img", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusFound {
		t.Errorf("Expected status %d (Redirect), got %d", http.StatusFound, w.Code)
	}

	location := w.Header().Get("Location")
	if location == "" {
		t.Errorf("Expected Location header to be set")
	}
}

func TestBingImgJson(t *testing.T) {
	mockBing(t)
	r := setupRouter()
	r.GET("/bing/img", BingImg)

	req, _ := http.NewRequest("GET", "/bing/img?format=json", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("Expected status %d (OK), got %d", http.StatusOK, w.Code)
	}

	contentType := w.Header().Get("Content-Type")
	if contentType != "application/json; charset=utf-8" {
		t.Errorf("Expected Content-Type application/json; charset=utf-8, got %s", contentType)
	}
}

func TestImg(t *testing.T) {
	dir := t.TempDir()
	t.Setenv("PC_PATH", dir)
	if err := os.WriteFile(filepath.Join(dir, "image.png"), []byte("test-image"), 0600); err != nil {
		t.Fatal(err)
	}
	if err := os.Mkdir(filepath.Join(dir, "directory.jpg"), 0700); err != nil {
		t.Fatal(err)
	}
	r := setupRouter()
	r.GET("/img/random", Img)

	req, _ := http.NewRequest("GET", "/img/random", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusOK || w.Body.String() != "test-image" {
		t.Errorf("Expected image body with status 200, got %d %s", w.Code, w.Body.String())
	}
}
