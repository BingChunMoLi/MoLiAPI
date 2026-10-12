package application

import (
	_ "embed"
	"encoding/json"
	"math/rand/v2"
	"strconv"
	"sync"

	"bingchunmoli.com/moliapi/internal/quotes"
	"github.com/gin-gonic/gin"
)

//go:embed data/poems.json
var poemData []byte

func (s *Server) loadQuotes() ([]quotes.Entry, error) {
	s.quoteOnce.Do(func() {
		library := quotes.Default
		if path := s.defaults["quotePath"]; path != "" {
			library = quotes.New(path)
		}
		s.quotes, s.quoteErr = library.All()
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
		if quote.ID == int64(id) {
			ok(c, quote.JSON)
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
		// Reservoir sampling avoids allocating a second slice of matching quotes.
		var selected json.RawMessage
		count := 0
		for _, quote := range list {
			if quote.Kind == kind {
				count++
				if rand.IntN(count) == 0 {
					selected = quote.JSON
				}
			}
		}
		if count == 0 {
			fail(c, 400, "不支持的一言类型")
			return
		}
		ok(c, selected)
		return
	}
	ok(c, list[rand.IntN(len(list))].JSON)
}

var loadPoems = sync.OnceValues(func() ([]quotes.Entry, error) { return quotes.Parse(poemData) })

func (s *Server) poem(c *gin.Context) {
	list, err := loadPoems()
	if err != nil {
		internal(c, err)
		return
	}
	if len(list) == 0 {
		ok(c, nil)
		return
	}
	if c.Param("id") == "" {
		ok(c, list[rand.IntN(len(list))].JSON)
		return
	}
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil || id <= 0 {
		fail(c, 400, "无效的诗词ID")
		return
	}
	for _, p := range list {
		if p.ID == int64(id) {
			ok(c, p.JSON)
			return
		}
	}
	ok(c, nil)
}
