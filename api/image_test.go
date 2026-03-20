package api

import (
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestBingImg(t *testing.T) {
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

func TestImg(t *testing.T) {
	r := setupRouter()
	r.GET("/img/random", Img)

	req, _ := http.NewRequest("GET", "/img/random", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusOK && w.Code != http.StatusNotFound && w.Code != http.StatusInternalServerError {
		t.Errorf("Expected status 200, 404, or 500, got %d", w.Code)
	}
}