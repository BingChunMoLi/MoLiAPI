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
)

func Img(c *gin.Context) {
	userHomeDir, err := os.UserHomeDir()
	if err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal Server Error"})
		return
	}
	prefixPath := filepath.Join(userHomeDir, ".api", "img", "pc")
	dir, err := os.ReadDir(prefixPath)
	if err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Internal Server Error"})
		return
	}
	if len(dir) == 0 {
		log.Print("没有图片")
		c.JSON(http.StatusNotFound, gin.H{"error": "Not Found"})
		return
	}
	random := rand.Intn(len(dir))
	c.File(filepath.Join(prefixPath, dir[random].Name()))
}

// BingImg 获取Bing每日美图
func BingImg(c *gin.Context) {
	resp, err := http.Get("https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1")
	if err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "获取Bing图片信息失败"})
		return
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
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

	imgUrl := "https://cn.bing.com" + data.Images[0].Url

	format := c.Query("format")
	if format == "json" {
		c.JSON(http.StatusOK, gin.H{"url": imgUrl})
		return
	}

	// 重定向到图片地址
	c.Redirect(http.StatusFound, imgUrl)
}