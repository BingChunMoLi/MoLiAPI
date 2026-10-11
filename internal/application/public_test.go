package application

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"mime/multipart"
	"net/http"
	"net/http/httptest"
	"net/url"
	"strings"
	"testing"
	"time"

	"bingchunmoli.com/moliapi/internal/store"
)

func TestPublicTools(t *testing.T) {
	_, r := testServer(t, Options{Config: map[string]string{"quotePath": "../../yiyan"}})
	cases := []struct {
		path   string
		status int
		want   string
	}{
		{"/emoji/alise?emoji=" + url.QueryEscape("😄"), 200, `":smile:"`},
		{"/emoji/unicode?emoji=:smile:", 200, `"😄"`},
		{"/emoji/isEmoji?emoji=" + url.QueryEscape("😄"), 200, `true`},
		{"/thunder/toThunder?rawURL=" + url.QueryEscape("https://example.com"), 200,
			`"thunder://QUFodHRwczovL2V4YW1wbGUuY29tWlo="`},
		{"/thunder/toRaw?thunderURL=invalid", 400, ""},
		{"/rent?month=2&rentPrice=1234.56&waterConsumption=3.1&waterCostRate=2.5", 200, ""},
		{"/rent?month=NaN", 400, ""},
		{"/yiyan/1", 200, ""},
		{"/shici/1", 200, ""},
		{"/shici/random", 200, ""},
		{"/ua/userAgentInfoByParam", 400, ""},
		{"/ua/userAgentInfoByParam?userAgent=Mozilla/5.0", 200, ""},
	}
	for _, tc := range cases {
		t.Run(tc.path, func(t *testing.T) {
			data := responseData(t, request(t, r, "GET", tc.path, nil, nil, nil), tc.status)
			if tc.want != "" && string(data) != tc.want {
				t.Fatalf("got %s, wanted %s", data, tc.want)
			}
			if strings.HasPrefix(tc.path, "/rent?month=2") {
				var totals map[string]string
				json.Unmarshal(data, &totals)
				if totals["totalPrice"] != "2476.87" {
					t.Fatal(totals)
				}
			}
		})
	}
}

func TestQRCodeRoundTrip(t *testing.T) {
	_, r := testServer(t, Options{})
	image := request(t, r, "GET", "/generate?text="+url.QueryEscape("https://example.com/中文")+
		"&width=400&height=600", nil, nil, nil)
	if image.Code != 200 || image.Header().Get("Content-Type") != "image/jpeg" {
		t.Fatal(image.Body.String())
	}
	var body bytes.Buffer
	writer := multipart.NewWriter(&body)
	part, err := writer.CreateFormFile("file", "qr.jpg")
	if err != nil {
		t.Fatal(err)
	}
	part.Write(image.Body.Bytes())
	writer.Close()
	req := httptest.NewRequest("POST", "/decode", &body)
	req.Header.Set("Content-Type", writer.FormDataContentType())
	response := httptest.NewRecorder()
	r.ServeHTTP(response, req)
	if string(responseData(t, response, 200)) != `"https://example.com/中文"` {
		t.Fatal(response.Body.String())
	}
	responseData(t, request(t, r, "GET", "/generate?width=99999", nil, nil, nil), 400)
	responseData(t, request(t, r, "POST", "/decode", nil, nil, nil), 400)
}

func TestBingArchiveMusicAndHosts(t *testing.T) {
	calls := 0
	upstream := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		switch r.URL.Path {
		case "/HPImageArchive.aspx":
			calls++
			fmt.Fprint(w, `{"images":[{"startdate":"20261031","url":"/last.jpg","urlbase":"/last"},`+
				`{"startdate":"20261030","url":"/previous.jpg","urlbase":"/previous"}],"tooltips":{}}`)
		case "/api/playlist/detail":
			fmt.Fprint(w, `{"code":200,"result":{"id":123,"tracks":[{"id":456,"name":"song",`+
				`"album":{"id":7,"picUrl":"image"},"artists":[{"id":8,"name":"artist"}]}]}}`)
		case "/hosts":
			fmt.Fprint(w, "#comment\n0.0.0.0 ads.example another.example #comment\n0.0.0.0 ads.example\n")
		default:
			w.WriteHeader(404)
		}
	}))
	defer upstream.Close()
	server, r := testServer(t, Options{Client: upstream.Client(),
		Config: map[string]string{"bingURL": upstream.URL, "musicURL": upstream.URL, "hostURL": upstream.URL + "/hosts"}})
	data := responseData(t, request(t, r, "GET", "/bing/all", nil, nil, nil), 200)
	var image map[string]any
	json.Unmarshal(data, &image)
	if image["url"] != "/last.jpg" || image["urlEn"] != "/last.jpg" {
		t.Fatal(image)
	}
	data = responseData(t, request(t, r, "GET", "/bing/date?date=2026-10-30", nil, nil, nil), 200)
	json.Unmarshal(data, &image)
	if image["url"] != "/previous.jpg" || calls != 2 {
		t.Fatal("archive not reused or wrong date", image, calls)
	}
	responseData(t, request(t, r, "GET", "/bing/2026/2/31", nil, nil, nil), 400)
	responseData(t, request(t, r, "GET", "/bing/random", nil, nil, nil), 200)
	responseData(t, request(t, r, "GET", "/music/playlist?id=123", nil, nil, nil), 200)
	data = responseData(t, request(t, r, "GET", "/music/123", nil, nil, nil), 200)
	if !strings.Contains(string(data), `"nickname":"artist"`) || !strings.Contains(string(data), `"thirdId":456`) {
		t.Fatal(string(data))
	}
	response := request(t, r, "GET", "/music/random", nil, nil, nil)
	if response.Code != 302 ||
		response.Header().Get("Location") != "https://music.163.com/song/media/outer/url?id=456.mp3" {
		t.Fatal(response.Code, response.Header())
	}
	response = request(t, r, "GET", "/host/raw?type=3", nil, nil, nil)
	if response.Code != 200 || strings.Count(response.Body.String(), "ads.example") != 1 ||
		!strings.Contains(response.Body.String(), "another.example") {
		t.Fatal(response.Body.String())
	}
	if err := server.RunTask(context.Background(), "host"); err != nil {
		t.Fatal(err)
	}
}

type fakeMailer struct {
	sent    []string
	failure error
}

func (m *fakeMailer) Send(_ context.Context, to, title, body string) error {
	m.sent = append(m.sent, to+"\n"+title+"\n"+body)
	return m.failure
}

func TestWeatherSubscriptionAndNotification(t *testing.T) {
	geoCalls := 0
	upstream := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		switch r.URL.Path {
		case "/v2/city/lookup":
			geoCalls++
			fmt.Fprint(w, `{"code":"200","location":[{"id":"101010100"}]}`)
		case "/v7/weather/3d":
			if r.URL.Query().Get("location") != "101010100" {
				t.Errorf("wrong location: %s", r.URL.Query().Get("location"))
			}
			fmt.Fprint(w, `{"code":"200","daily":[{"iconDay":"305","iconNight":"100"}]}`)
		default:
			w.WriteHeader(502)
		}
	}))
	defer upstream.Close()
	mailer := &fakeMailer{}
	server, r := testServer(t, Options{Client: upstream.Client(), Mailer: mailer,
		Config: map[string]string{"weatherKey": "test-signing-key", "weatherUri": upstream.URL,
			"weatherGeoUri": upstream.URL, "publicURL": "https://api.example"}})
	responseData(t, request(t, r, "GET", "/weather/byDay?location="+url.QueryEscape("北京"), nil, nil, nil), 200)
	if geoCalls != 1 {
		t.Fatal("city lookup was skipped")
	}
	responseData(t, request(t, r, "GET", "/weather/byDay?location=101010100&day=15", nil, nil, nil), 400)
	sub := store.Subscription{Email: "reader@example.com", Location: "101010100"}
	responseData(t, request(t, r, "POST", "/weather/sub", sub, nil, nil), 200)
	if len(mailer.sent) != 1 || !strings.Contains(mailer.sent[0], "https://api.example/weather/callback?param=") {
		t.Fatal(mailer.sent)
	}
	responseData(t, request(t, r, "POST", "/weather/sub", sub, nil, nil), 429)
	confirm, _ := signClaims(subscriptionClaims{Email: sub.Email, Location: sub.Location,
		Action: "confirm", Expiry: server.now().Add(time.Hour).Unix()}, "test-signing-key")
	for i := 0; i < 2; i++ {
		responseData(t, request(t, r, "GET", "/weather/callback?param="+confirm, nil, nil, nil), 200)
	}
	server.Store.View(func(st *store.State) error {
		if len(st.Subscriptions) != 1 {
			t.Fatal("duplicate subscription")
		}
		return nil
	})
	if err := server.RunTask(context.Background(), "weather-notification"); err != nil {
		t.Fatal(err)
	}
	if len(mailer.sent) != 2 || !strings.Contains(mailer.sent[1], "带伞") {
		t.Fatal(mailer.sent)
	}
	cancel, _ := signClaims(subscriptionClaims{Email: sub.Email, Location: sub.Location,
		Action: "unsubscribe", Expiry: server.now().Add(time.Hour).Unix()}, "test-signing-key")
	responseData(t, request(t, r, "GET", "/weather/unSub?param="+confirm, nil, nil, nil), 400)
	responseData(t, request(t, r, "GET", "/weather/unSub?param="+cancel, nil, nil, nil), 200)
	server.Store.View(func(st *store.State) error {
		if len(st.Subscriptions) != 0 {
			t.Fatal("unsubscribe failed")
		}
		return nil
	})
	expired, _ := signClaims(subscriptionClaims{Email: sub.Email, Location: sub.Location,
		Action: "confirm", Expiry: server.now().Add(-time.Second).Unix()}, "test-signing-key")
	responseData(t, request(t, r, "GET", "/weather/callback?param="+expired, nil, nil, nil), 400)
	responseData(t, request(t, r, "GET", "/weather/callback?param="+confirm+"tampered", nil, nil, nil), 400)
}

func TestUpstreamErrorsDoNotReturnSuccess(t *testing.T) {
	for _, body := range []string{`not json`, `{"images":[]}`, `{"code":"401"}`} {
		t.Run(body, func(t *testing.T) {
			upstream := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter,
				r *http.Request) {
				fmt.Fprint(w, body)
			}))
			defer upstream.Close()
			_, r := testServer(t, Options{Client: upstream.Client(),
				Config: map[string]string{"bingURL": upstream.URL, "weatherKey": "test", "weatherUri": upstream.URL}})
			responseData(t, request(t, r, "GET", "/bing/cn", nil, nil, nil), 502)
			responseData(t, request(t, r, "GET", "/weather/byDay?location=101010100", nil, nil, nil), 502)
		})
	}
}
