package application

import (
	_ "embed"
	"encoding/json"
	"fmt"
	"math/rand/v2"
	"os"
	"path/filepath"
	"strconv"

	"github.com/gin-gonic/gin"
)

//go:embed data/poems.json
var poemData []byte

func (s *Server) loadQuotes() ([]map[string]any, error) {
	s.quoteOnce.Do(func() {
		path := s.defaults["quotePath"]
		if path == "" {
			path = "yiyan"
		}
		for kind := 'a'; kind <= 'l'; kind++ {
			data, err := os.ReadFile(filepath.Join(path, string(kind)+".json"))
			if err != nil {
				s.quoteErr = err
				return
			}
			var list []map[string]any
			if err = json.Unmarshal(data, &list); err != nil {
				s.quoteErr = err
				return
			}
			s.quotes = append(s.quotes, list...)
		}
		if len(s.quotes) == 0 {
			s.quoteErr = fmt.Errorf("empty quote library")
		}
	})
	return s.quotes, s.quoteErr
}

func (s *Server) quoteByID(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil || id <= 0 {
		fail(c, 400, "无效的一言ID")
		return
	}
	list, err := s.loadQuotes()
	if err != nil {
		internal(c, err)
		return
	}
	for _, quote := range list {
		if quote["id"] == float64(id) {
			ok(c, quote)
			return
		}
	}
	ok(c, nil)
}

func (s *Server) QuoteRandom(c *gin.Context) {
	list, err := s.loadQuotes()
	if err != nil {
		internal(c, err)
		return
	}
	if kind := c.Query("type"); kind != "" {
		filtered := []map[string]any{}
		for _, quote := range list {
			if quote["type"] == kind {
				filtered = append(filtered, quote)
			}
		}
		if len(filtered) == 0 {
			fail(c, 400, "不支持的一言类型")
			return
		}
		list = filtered
	}
	ok(c, list[rand.IntN(len(list))])
}

func (s *Server) poem(c *gin.Context) {
	var list []map[string]any
	if err := json.Unmarshal(poemData, &list); err != nil {
		internal(c, err)
		return
	}
	if len(list) == 0 {
		ok(c, nil)
		return
	}
	if c.Param("id") == "" {
		ok(c, list[rand.IntN(len(list))])
		return
	}
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil || id <= 0 {
		fail(c, 400, "无效的诗词ID")
		return
	}
	for _, p := range list {
		if p["id"] == float64(id) {
			ok(c, p)
			return
		}
	}
	ok(c, nil)
}
