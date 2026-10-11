package application

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"net/http/httptest"
	"os"
	"path/filepath"
	"testing"
	"time"

	"bingchunmoli.com/moliapi/internal/store"
)

func TestImagesUseConfiguredDirectories(t *testing.T) {
	cfg := map[string]string{}
	for _, key := range []string{"pcPath", "mobilePath", "path1080"} {
		dir := t.TempDir()
		cfg[key] = dir
		if err := os.WriteFile(filepath.Join(dir, "image.png"), []byte(key), 0600); err != nil {
			t.Fatal(err)
		}
		if err := os.Mkdir(filepath.Join(dir, "directory.jpg"), 0700); err != nil {
			t.Fatal(err)
		}
	}
	_, r := testServer(t, Options{Config: cfg})
	for _, tc := range []struct{ path, expected string }{
		{"/img/pc", "pcPath"}, {"/img/mobile", "mobilePath"}, {"/img/1080", "path1080"},
	} {
		response := request(t, r, "GET", tc.path, nil, nil, nil)
		if response.Code != 200 || response.Body.String() != tc.expected {
			t.Fatal(tc.path, response.Code, response.Body.String())
		}
	}
}

func TestIPLookupAndWeatherLocateClient(t *testing.T) {
	calls := 0
	upstream := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path == "/lookup" {
			calls++
			fmt.Fprint(w, `{"success":true,"latitude":31.2,"longitude":121.5}`)
			return
		}
		if r.URL.Query().Get("location") != "121.5,31.2" {
			t.Error("weather did not use geolocation")
		}
		fmt.Fprint(w, `{"code":"200","now":{"text":"晴"}}`)
	}))
	defer upstream.Close()
	cfg := map[string]string{"ipLookupURL": upstream.URL + "/lookup", "weatherKey": "test", "weatherUri": upstream.URL}
	server, r := testServer(t, Options{Config: cfg, Client: upstream.Client()})
	data := responseData(t, request(t, r, "GET", "/address", nil, nil, nil), 200)
	var address map[string]any
	json.Unmarshal(data, &address)
	if address["lat"] != 31.2 || address["lng"] != 121.5 {
		t.Fatal(address)
	}
	responseData(t, request(t, r, "GET", "/weather/now", nil, nil, nil), 200)
	before := calls
	local, err := server.lookupAddress(context.Background(), "127.0.0.1", cfg)
	if err != nil || local["local"] != true || calls != before {
		t.Fatal("private IP was queried externally")
	}
}

func TestCrossOriginHTTPSCookieAndSessionExpiry(t *testing.T) {
	now := time.Now()
	_, r := testServer(t, Options{Now: func() time.Time { return now },
		Config: map[string]string{"publicURL": "https://api.example"}, Origins: []string{"https://admin.example"}})
	response := request(t, r, "PUT", "/user/register", store.User{Name: "admin", Password: "password"}, nil,
		map[string]string{"Origin": "https://admin.example"})
	responseData(t, response, 200)
	cookie := response.Result().Cookies()[0]
	if !cookie.Secure || cookie.SameSite != http.SameSiteNoneMode {
		t.Fatal("cross-origin secure cookie missing")
	}
	now = now.Add(13 * time.Hour)
	responseData(t, request(t, r, "GET", "/system", nil, cookie, nil), 401)
}
