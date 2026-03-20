package api

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"os"
	"testing"
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