package api

import (
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestQqAvatar(t *testing.T) {
	r := setupRouter()
	r.GET("/qq/avatar", QqAvatar)

	req, _ := http.NewRequest("GET", "/qq/avatar?qq=12345", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusFound {
		t.Errorf("Expected status %d, got %d", http.StatusFound, w.Code)
	}

	reqJson, _ := http.NewRequest("GET", "/qq/avatar?qq=12345&type=json", nil)
	wJson := httptest.NewRecorder()
	r.ServeHTTP(wJson, reqJson)

	if wJson.Code != http.StatusOK {
		t.Errorf("Expected status %d, got %d", http.StatusOK, wJson.Code)
	}
}

func TestQzoneAvatar(t *testing.T) {
	r := setupRouter()
	r.GET("/qq/qzone_avatar", QzoneAvatar)

	req, _ := http.NewRequest("GET", "/qq/qzone_avatar?qq=12345", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusFound {
		t.Errorf("Expected status %d, got %d", http.StatusFound, w.Code)
	}

	reqJson, _ := http.NewRequest("GET", "/qq/qzone_avatar?qq=12345&type=json", nil)
	wJson := httptest.NewRecorder()
	r.ServeHTTP(wJson, reqJson)

	if wJson.Code != http.StatusOK {
		t.Errorf("Expected status %d, got %d", http.StatusOK, wJson.Code)
	}
}