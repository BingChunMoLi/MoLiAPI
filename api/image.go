package api

import (
	"encoding/json"
	"github.com/gin-gonic/gin"
	"io"
	"log"
	"math/rand"
	"net/http"
	"os"
	"path/filepath"
	"strings"
	"time"
)

var bingClient = &http.Client{Timeout: 20 * time.Second}
var bingBaseURL = "https://cn.bing.com"

func Img(c *gin.Context) {
	userHomeDir, err := os.UserHomeDir()
	if err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal Server Error"})
		return
	}
	prefixPath := filepath.Join(userHomeDir, ".api", "img", "pc")
	if configured := os.Getenv("PC_PATH"); configured != "" {
		prefixPath = configured
	}
	dir, err := os.ReadDir(prefixPath)
	if err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal Server Error"})
		return
	}
	files := []string{}
	for _, entry := range dir {
		ext := strings.ToLower(filepath.Ext(entry.Name()))
		if entry.Type().IsRegular() && (ext == ".jpg" || ext == ".png" || ext == ".webp" || ext == ".jpeg" || ext == ".gif") {
			files = append(files, entry.Name())
		}
	}
	if len(files) == 0 {
		log.Print("没有图片")
		c.JSON(http.StatusNotFound, gin.H{"error": "Not Found"})
		return
	}
	random := rand.Intn(len(files))
	c.File(filepath.Join(prefixPath, files[random]))
}

// BingImg 获取Bing每日美图
func BingImg(c *gin.Context) {
	request, err := http.NewRequestWithContext(c.Request.Context(), http.MethodGet,
		bingBaseURL+"/HPImageArchive.aspx?format=js&idx=0&n=1", nil)
	if err != nil {
		c.JSON(500, gin.H{"error": "获取Bing图片信息失败"})
		return
	}
	resp, err := bingClient.Do(request)
	if err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "获取Bing图片信息失败"})
		return
	}
	defer resp.Body.Close()
	if resp.StatusCode != http.StatusOK {
		c.JSON(502, gin.H{"error": "获取Bing图片信息失败"})
		return
	}

	body, err := io.ReadAll(io.LimitReader(resp.Body, 2<<20))
	if err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "读取响应内容失败"})
		return
	}

	var data struct {
		Images []struct {
			Url string `json:"url"`
		} `json:"images"`
	}

	err = json.Unmarshal(body, &data)
	if err != nil || len(data.Images) == 0 {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "解析Bing图片数据失败"})
		return
	}

	imgUrl := data.Images[0].Url
	if !strings.HasPrefix(imgUrl, "https://") && !strings.HasPrefix(imgUrl, "http://") {
		imgUrl = bingBaseURL + imgUrl
	}

	format := c.Query("format")
	if format == "json" {
		c.JSON(http.StatusOK, gin.H{"url": imgUrl})
		return
	}

	// 重定向到图片地址
	c.Redirect(http.StatusFound, imgUrl)
}
