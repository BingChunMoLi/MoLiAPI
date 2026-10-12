package application

import (
	"net/http/httptest"
	"os"
	"path/filepath"
	"strings"
	"testing"

	"github.com/gin-gonic/gin"
)

func BenchmarkQuoteLoad(b *testing.B) {
	b.ReportAllocs()
	for i := 0; i < b.N; i++ {
		s := &Server{defaults: map[string]string{"quotePath": "../../yiyan"}}
		if _, err := s.loadQuotes(); err != nil {
			b.Fatal(err)
		}
	}
}

func BenchmarkQuoteRandomType(b *testing.B) {
	gin.SetMode(gin.TestMode)
	s := &Server{defaults: map[string]string{"quotePath": "../../yiyan"}}
	if _, err := s.loadQuotes(); err != nil {
		b.Fatal(err)
	}
	b.ReportAllocs()
	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		w := httptest.NewRecorder()
		c, _ := gin.CreateTestContext(w)
		c.Request = httptest.NewRequest("GET", "/api/yiyan/random?type=d", nil)
		s.QuoteRandom(c)
		if w.Code != 200 {
			b.Fatal(w.Body.String())
		}
	}
}

func TestQuoteResponseCompatibility(t *testing.T) {
	root := t.TempDir()
	for kind := byte('a'); kind <= 'l'; kind++ {
		data := `[]`
		if kind == 'a' {
			data = `[{"id":1,"type":"a","hitokoto":"hello","from_who":null,"extra":{"key":"value"}}]`
		}
		if err := os.WriteFile(filepath.Join(root, string(kind)+".json"), []byte(data), 0600); err != nil {
			t.Fatal(err)
		}
	}
	s, r := testServer(t, Options{Config: map[string]string{"quotePath": root}})
	r.GET("/quote-random", s.QuoteRandom)
	for _, path := range []string{"/yiyan/1", "/quote-random", "/quote-random?type=a"} {
		data := responseData(t, request(t, r, "GET", path, nil, nil, nil), 200)
		for _, want := range []string{`"id":1`, `"from_who":null`, `"extra":{"key":"value"}`} {
			if !strings.Contains(string(data), want) {
				t.Fatalf("%s: missing %s in %s", path, want, data)
			}
		}
	}
	responseData(t, request(t, r, "GET", "/quote-random?type=z", nil, nil, nil), 400)
	data := responseData(t, request(t, r, "GET", "/yiyan/999", nil, nil, nil), 200)
	if string(data) != "null" {
		t.Fatal(string(data))
	}
}
