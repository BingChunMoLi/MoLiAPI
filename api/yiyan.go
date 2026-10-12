package api

import (
	"encoding/json"
	"log"
	"math/rand"
	"net/http"
	"strconv"

	"bingchunmoli.com/moliapi/internal/quotes"
	"github.com/gin-gonic/gin"
)

type YiYanEntity struct {
	Id         int64  `json:"id,omitempty"`
	Uuid       string `json:"uuid,omitempty"`
	Hitokoto   string `json:"hitokoto,omitempty"`
	Type       string `json:"type,omitempty"`
	From       string `json:"from,omitempty"`
	FromWho    string `json:"from_who,omitempty"`
	Creator    string `json:"creator,omitempty"`
	CreatorUid int64  `json:"creator_uid,omitempty"`
	Reviewer   int64  `json:"reviewer,omitempty"`
	CommitFrom string `json:"commit_from,omitempty"`
	CreatedAt  string `json:"created_at,omitempty"`
	Length     int16  `json:"length,omitempty"`
}

func YiYan(c *gin.Context) {
	yiYanType := c.Query("type")
	if yiYanType == "" || len(yiYanType) != 1 || yiYanType[0] < 'a' || yiYanType[0] > 'l' {
		yiYanType = string(rune(rand.Intn(12) + 97))
	}
	yiYans, err := quotes.Default.Category(yiYanType[0])
	if err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal Server Error"})
		return
	}
	idStr := c.Query("id")
	if idStr != "" {
		for _, y := range yiYans {
			if strconv.FormatInt(y.ID, 10) == idStr {
				writeQuote(c, y.JSON, false)
				return
			}
		}
		c.JSON(http.StatusNotFound, gin.H{"error": "Not Found"})
		return
	}

	if len(yiYans) == 0 {
		c.JSON(http.StatusNotFound, gin.H{"error": "Not Found"})
		return
	}
	randYiYanIndex := rand.Intn(len(yiYans))
	writeQuote(c, yiYans[randYiYanIndex].JSON, false)
}

// Poetry 随机诗词或指定
func Poetry(c *gin.Context) {
	// 读取诗词库，即 yiyan/i.json
	yiYans, err := quotes.Default.Category('i')
	if err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal Server Error"})
		return
	}
	idStr := c.Query("id")
	if idStr != "" {
		for _, y := range yiYans {
			if strconv.FormatInt(y.ID, 10) == idStr {
				writeQuote(c, y.JSON, false)
				return
			}
		}
		c.JSON(http.StatusNotFound, gin.H{"error": "Not Found"})
		return
	}

	if len(yiYans) == 0 {
		c.JSON(http.StatusNotFound, gin.H{"error": "Not Found"})
		return
	}
	randIndex := rand.Intn(len(yiYans))
	writeQuote(c, yiYans[randIndex].JSON, c.Query("type") == "text")
}

// Decode only the selected record, keeping the legacy omitempty response format.
func writeQuote(c *gin.Context, data json.RawMessage, text bool) {
	var y YiYanEntity
	if err := json.Unmarshal(data, &y); err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal Server Error"})
		return
	}
	if text {
		c.String(http.StatusOK, "%s —— %s", y.Hitokoto, y.From)
		return
	}

	c.JSON(http.StatusOK, y)
}
