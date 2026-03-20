package api

import (
	"encoding/json"
	"fmt"
	"github.com/gin-gonic/gin"
	"log"
	"math/rand"
	"net/http"
	"os"
	"path/filepath"
)

type YiYanEntity struct {
	Id         int16  `json:"id,omitempty"`
	Uuid       string `json:"uuid,omitempty"`
	Hitokoto   string `json:"hitokoto,omitempty"`
	Type       string `json:"type,omitempty"`
	From       string `json:"from,omitempty"`
	FromWho    string `json:"from_who,omitempty"`
	Creator    string `json:"creator,omitempty"`
	CreatorUid int16  `json:"creator_uid,omitempty"`
	Reviewer   int16  `json:"reviewer,omitempty"`
	CommitFrom string `json:"commit_from,omitempty"`
	CreatedAt  string `json:"created_at,omitempty"`
	Length     int16  `json:"length,omitempty"`
}

func YiYan(c *gin.Context) {
	yiYanType := c.Query("type")
	if yiYanType == "" || len(yiYanType) != 1 || yiYanType[0] < 'a' || yiYanType[0] > 'l' {
		yiYanType = string(rune(rand.Intn(12) + 97))
	}
	file, err := os.ReadFile(filepath.Join("yiyan", yiYanType+".json"))
	if err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal Server Error"})
		return
	}
	var yiYans []YiYanEntity
	err = json.Unmarshal(file, &yiYans)
	if err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal Server Error"})
		return
	}

	idStr := c.Query("id")
	if idStr != "" {
		for _, y := range yiYans {
			if fmt.Sprintf("%d", y.Id) == idStr {
				c.JSON(http.StatusOK, y)
				return
			}
		}
		c.JSON(http.StatusNotFound, gin.H{"error": "Not Found"})
		return
	}

	randYiYanIndex := rand.Intn(len(yiYans))
	c.JSON(http.StatusOK, yiYans[randYiYanIndex])
}

// Poetry 随机诗词或指定
func Poetry(c *gin.Context) {
	// 读取诗词库，即 yiyan/i.json
	file, err := os.ReadFile(filepath.Join("yiyan", "i.json"))
	if err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal Server Error"})
		return
	}
	var yiYans []YiYanEntity
	err = json.Unmarshal(file, &yiYans)
	if err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal Server Error"})
		return
	}

	idStr := c.Query("id")
	if idStr != "" {
		for _, y := range yiYans {
			if fmt.Sprintf("%d", y.Id) == idStr {
				c.JSON(http.StatusOK, y)
				return
			}
		}
		c.JSON(http.StatusNotFound, gin.H{"error": "Not Found"})
		return
	}

	randIndex := rand.Intn(len(yiYans))
	y := yiYans[randIndex]

	if c.Query("type") == "text" {
		c.String(http.StatusOK, "%s —— %s", y.Hitokoto, y.From)
		return
	}

	c.JSON(http.StatusOK, y)
}