package api

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestQrCode(t *testing.T) {
	r := setupRouter()
	r.GET("/qrcode", QrCode)

	req, _ := http.NewRequest("GET", "/qrcode", nil)
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusBadRequest {
		t.Errorf("Expected status %d, got %d", http.StatusBadRequest, w.Code)
	}

	req, _ = http.NewRequest("GET", "/qrcode?text=hello", nil)
	w = httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("Expected status %d, got %d", http.StatusOK, w.Code)
	}
	if w.Header().Get("Content-Type") != "image/png" {
		t.Errorf("Expected Content-Type image/png, got %s", w.Header().Get("Content-Type"))
	}
}

func TestIpQuery(t *testing.T) {
	r := setupRouter()
	r.GET("/ip", IpQuery)

	req, _ := http.NewRequest("GET", "/ip", nil)
	req.RemoteAddr = "192.168.1.1:1234"
	
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("Expected status %d, got %d", http.StatusOK, w.Code)
	}

	var response map[string]string
	err := json.Unmarshal(w.Body.Bytes(), &response)
	if err != nil {
		t.Fatalf("Failed to parse response: %v", err)
	}

	if response["ip"] != "192.168.1.1" {
		t.Errorf("Expected IP 192.168.1.1, got %s", response["ip"])
	}
}

func TestXunleiConvert(t *testing.T) {
	r := setupRouter()
	r.GET("/xunlei", XunleiConvert)

	req1, _ := http.NewRequest("GET", "/xunlei?url=http://example.com/file.zip", nil)
	w1 := httptest.NewRecorder()
	r.ServeHTTP(w1, req1)

	if w1.Code != http.StatusOK {
		t.Errorf("Expected status %d, got %d", http.StatusOK, w1.Code)
	}

	req2, _ := http.NewRequest("GET", "/xunlei?url=thunder://QUFodHRwOi8vZXhhbXBsZS5jb20vZmlsZS56aXBaWg==", nil)
	w2 := httptest.NewRecorder()
	r.ServeHTTP(w2, req2)

	if w2.Code != http.StatusOK {
		t.Errorf("Expected status %d, got %d", http.StatusOK, w2.Code)
	}
}

func TestUserAgent(t *testing.T) {
	r := setupRouter()
	r.GET("/ua", UserAgent)

	req, _ := http.NewRequest("GET", "/ua", nil)
	req.Header.Set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
	w := httptest.NewRecorder()
	r.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("Expected status %d, got %d", http.StatusOK, w.Code)
	}

	var response map[string]interface{}
	json.Unmarshal(w.Body.Bytes(), &response)
	
	if response["browser_name"] != "Chrome" {
		t.Errorf("Expected browser Chrome, got %v", response["browser_name"])
	}
}