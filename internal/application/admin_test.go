package application

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	"net/http/httptest"
	"path/filepath"
	"strings"
	"testing"
	"time"

	"bingchunmoli.com/moliapi/internal/store"
	"github.com/gin-gonic/gin"
)

func testServer(t *testing.T, options Options) (*Server, *gin.Engine) {
	t.Helper()
	gin.SetMode(gin.TestMode)
	if options.Store == nil {
		options.Store, _ = store.Open("")
	}
	if options.Now == nil {
		options.Now = func() time.Time { return time.Date(2026, 10, 31, 23, 59, 59, 0, time.FixedZone("CST", 8*3600)) }
	}
	server, err := New(options)
	if err != nil {
		t.Fatal(err)
	}
	router := gin.New()
	router.Use(server.Middleware())
	server.Register(router)
	return server, router
}

func request(t *testing.T, router http.Handler, method, path string, body any, cookie *http.Cookie,
	headers map[string]string) *httptest.ResponseRecorder {
	t.Helper()
	var data []byte
	if body != nil {
		var err error
		data, err = json.Marshal(body)
		if err != nil {
			t.Fatal(err)
		}
	}
	req := httptest.NewRequest(method, path, bytes.NewReader(data))
	req.Header.Set("Content-Type", "application/json")
	if cookie != nil {
		req.AddCookie(cookie)
	}
	for key, value := range headers {
		req.Header.Set(key, value)
	}
	response := httptest.NewRecorder()
	router.ServeHTTP(response, req)
	return response
}

func responseData(t *testing.T, response *httptest.ResponseRecorder, status int) json.RawMessage {
	t.Helper()
	if response.Code != status {
		t.Fatalf("HTTP %d, wanted %d: %s", response.Code, status, response.Body.String())
	}
	var envelope struct {
		Code string          `json:"code"`
		Data json.RawMessage `json:"data"`
	}
	if err := json.Unmarshal(response.Body.Bytes(), &envelope); err != nil {
		t.Fatal(err)
	}
	if status < 300 && envelope.Code != "00000" {
		t.Fatalf("unexpected result code %s", envelope.Code)
	}
	return envelope.Data
}

func registerAdmin(t *testing.T, r http.Handler) *http.Cookie {
	t.Helper()
	response := request(t, r, "PUT", "/user/register", store.User{Name: "admin", Password: "test-password"}, nil, nil)
	responseData(t, response, 200)
	cookies := response.Result().Cookies()
	if len(cookies) != 1 || !cookies[0].HttpOnly {
		t.Fatal("missing secure session cookie")
	}
	return cookies[0]
}

func TestAdminAuthenticationAndConfigPersistence(t *testing.T) {
	path := filepath.Join(t.TempDir(), "state.json")
	database, _ := store.Open(path)
	_, r := testServer(t, Options{Store: database})
	responseData(t, request(t, r, "GET", "/system", nil, nil, nil), 401)
	responseData(t, request(t, r, "PUT", "/user/register", store.User{}, nil, nil), 400)
	cookie := registerAdmin(t, r)
	responseData(t, request(t, r, "PUT", "/user/register", store.User{Name: "another",
		Password: "password"}, nil, nil), 409)
	responseData(t, request(t, r, "POST", "/system", map[string]string{"weatherKey": "first"}, cookie, nil), 200)
	responseData(t, request(t, r, "POST", "/system", map[string]string{"weatherKey": "second"}, cookie, nil), 200)
	restored, err := store.Open(path)
	if err != nil {
		t.Fatal(err)
	}
	server, r2 := testServer(t, Options{Store: restored})
	cfg, err := server.config()
	if err != nil || cfg["weatherKey"] != "second" {
		t.Fatalf("persisted config: %v %v", cfg, err)
	}
	responseData(t, request(t, r2, "POST", "/user/login", store.User{Name: "admin", Password: "wrong"}, nil, nil), 401)
	login := request(t, r2, "POST", "/user/login", store.User{Name: "admin", Password: "test-password"}, nil, nil)
	responseData(t, login, 200)
	cookie = login.Result().Cookies()[0]
	responseData(t, request(t, r2, "GET", "/system", nil, cookie, nil), 200)
	responseData(t, request(t, r2, "POST", "/user/logout", nil, cookie, nil), 200)
	responseData(t, request(t, r2, "GET", "/system", nil, cookie, nil), 401)
	restored.View(func(st *store.State) error {
		if strings.Contains(st.User.Password, "test-password") {
			t.Fatal("plaintext password stored")
		}
		return nil
	})
}

func TestNavigationTagsAndTenants(t *testing.T) {
	_, r := testServer(t, Options{})
	responseData(t, request(t, r, "PUT", "/navigation", store.Navigation{URL: "https://example.com"}, nil, nil), 401)
	cookie := registerAdmin(t, r)
	payload := store.Navigation{Title: "site", URL: "https://example.com", Tenant: "private",
		Tags: []store.Tag{{Name: "one"}, {Name: "two", Open: true}}}
	responseData(t, request(t, r, "PUT", "/navigation", payload, cookie, nil), 200)
	headers := map[string]string{"tenant": "private"}
	var list []store.Navigation
	json.Unmarshal(responseData(t, request(t, r, "GET", "/navigation", nil, nil, headers), 200), &list)
	if len(list) != 1 || len(list[0].Tags) != 2 || list[0].Tags[0].ID == list[0].Tags[1].ID || list[0].Tags[0].ID == 0 {
		t.Fatalf("bad navigation: %#v", list)
	}
	id := list[0].ID
	tag := list[0].Tags[0]
	tag.Name = "renamed"
	tag.Private = true
	route := fmt.Sprintf("/navigation/%d", id)
	responseData(t, request(t, r, "POST", route, map[string]any{"tagList": []store.Tag{tag,
		{Name: "new"}}}, cookie, nil), 200)
	json.Unmarshal(responseData(t, request(t, r, "GET", "/navigation", nil, nil, headers), 200), &list)
	if len(list[0].Tags) != 2 || list[0].Tags[0].Name != "renamed" || !list[0].Tags[0].Private {
		t.Fatal("tag update or association lost")
	}
	responseData(t, request(t, r, "POST", route, map[string]any{"title": "changed"}, cookie, nil), 200)
	json.Unmarshal(responseData(t, request(t, r, "GET", "/navigation", nil, nil, headers), 200), &list)
	if len(list[0].Tags) != 2 {
		t.Fatal("omitted tags removed associations")
	}
	responseData(t, request(t, r, "POST", route, map[string]any{"tagList": []store.Tag{}}, cookie, nil), 200)
	json.Unmarshal(responseData(t, request(t, r, "GET", "/navigation", nil, nil, headers), 200), &list)
	if len(list[0].Tags) != 0 {
		t.Fatal("empty tags did not clear associations")
	}
	if string(responseData(t, request(t, r, "GET", "/navigation", nil, nil, nil), 200)) != "[]" {
		t.Fatal("tenant data leaked")
	}
	responseData(t, request(t, r, "POST", "/navigation/999", map[string]any{"title": "missing"}, cookie, nil), 404)
	responseData(t, request(t, r, "DELETE", route, nil, cookie, nil), 200)
	if string(responseData(t, request(t, r, "GET", "/navigation", nil, nil, headers), 200)) != "[]" {
		t.Fatal("delete failed")
	}
}

func TestDailyMonthEndAndDefaultGroup(t *testing.T) {
	server, r := testServer(t, Options{})
	response := request(t, r, "PUT", "/daily", dailyInput{Key: "moli", Value: "https://month-end.example"}, nil, nil)
	var urls []string
	json.Unmarshal(responseData(t, response, 200), &urls)
	if len(urls) != 4 {
		t.Fatal("default group cannot be appended")
	}
	responseData(t, request(t, r, "POST", "/daily/signed", []string{"https://month-end.example"}, nil, nil), 200)
	server.Store.Update(func(st *store.State) error {
		st.Logs = append(st.Logs, store.DailyLog{ID: st.ID(), URL: "next-month", Tenant: 1,
			Created: server.now().Add(time.Second)},
			store.DailyLog{ID: st.ID(), URL: "other-tenant", Tenant: 0, Created: server.now()})
		return nil
	})
	result := responseData(t, request(t, r, "GET",
		"/daily/query?startDate=2026-10-01&endDate=2026-10-31", nil, nil, nil), 200)
	var days map[string][]gin.H
	json.Unmarshal(result, &days)
	if len(days) != 1 || len(days["2026-10-31"]) != 1 || days["2026-10-31"][0]["url"] != "https://month-end.example" {
		t.Fatalf("bad month boundary: %s", result)
	}
	responseData(t, request(t, r, "GET", "/daily/query?startDate=bad&endDate=2026-10-31", nil, nil, nil), 400)
	if string(responseData(t, request(t, r, "GET", "/daily/check", nil, nil, nil),
		200)) != `["https://month-end.example"]` {
		t.Fatal("today check failed")
	}
	responseData(t, request(t, r, "GET", "/daily/param", nil, nil, nil), 200)
	responseData(t, request(t, r, "POST", "/daily/signed", []string{}, nil, nil), 400)
}

func TestCORSPreflightAllowsTenant(t *testing.T) {
	_, r := testServer(t, Options{Origins: []string{"https://admin.example"}})
	response := request(t, r, "OPTIONS", "/daily/query", nil, nil,
		map[string]string{"Origin": "https://admin.example", "Access-Control-Request-Method": "GET",
			"Access-Control-Request-Headers": "tenant"})
	if response.Code != 204 || !strings.Contains(response.Header().Get("Access-Control-Allow-Headers"), "tenant") {
		t.Fatal(response.Code, response.Header())
	}
	responseData(t, request(t, r, "OPTIONS", "/daily/query", nil, nil,
		map[string]string{"Origin": "https://unknown.example"}), 403)
}

func TestDeviceMergePreservesTokens(t *testing.T) {
	server, r := testServer(t, Options{})
	id, err := server.saveDevice(store.Device{Name: "name", Model: "model", AndroidID: "id", HMSToken: "hms-token"})
	if err != nil {
		t.Fatal(err)
	}
	responseData(t, request(t, r, "POST", "/app", store.Device{ID: id, Token: "fcm-token"}, nil, nil), 201)
	server.Store.View(func(st *store.State) error {
		d := st.Devices[0]
		if d.HMSToken != "hms-token" || d.Token != "fcm-token" || d.PushType != "hms" || d.Name != "name" {
			t.Fatalf("device merge: %#v", d)
		}
		return nil
	})
	found := responseData(t, request(t, r, "GET", "/app/name/model/id", nil, nil, nil), 200)
	if string(found) != fmt.Sprint(id) {
		t.Fatal("device identity created duplicate")
	}
}
