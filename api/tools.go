package api

import (
	"encoding/base64"
	"fmt"
	"github.com/gin-gonic/gin"
	"github.com/mssola/user_agent"
	"github.com/skip2/go-qrcode"
	"log"
	"net/http"
	"strings"
)

// QrCode 生成二维码
func QrCode(c *gin.Context) {
	text := c.Query("text")
	if text == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "参数 text 不能为空"})
		return
	}

	size := 256
	png, err := qrcode.Encode(text, qrcode.Medium, size)
	if err != nil {
		log.Println(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "生成二维码失败"})
		return
	}

	c.Data(http.StatusOK, "image/png", png)
}

// IpQuery 获取公网IP
func IpQuery(c *gin.Context) {
	ip := c.ClientIP()
	c.JSON(http.StatusOK, gin.H{
		"ip": ip,
	})
}

// XunleiConvert 迅雷链接转换
func XunleiConvert(c *gin.Context) {
	url := c.Query("url")
	if url == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "参数 url 不能为空"})
		return
	}

	if strings.HasPrefix(url, "thunder://") {
		// 解码
		encodedStr := strings.TrimPrefix(url, "thunder://")
		decodedBytes, err := base64.StdEncoding.DecodeString(encodedStr)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "无效的迅雷链接"})
			return
		}
		decodedStr := string(decodedBytes)
		if strings.HasPrefix(decodedStr, "AA") && strings.HasSuffix(decodedStr, "ZZ") {
			decodedStr = decodedStr[2 : len(decodedStr)-2]
		}
		c.JSON(http.StatusOK, gin.H{
			"type": "decode",
			"url":  decodedStr,
		})
	} else {
		// 编码
		encodedStr := base64.StdEncoding.EncodeToString([]byte("AA" + url + "ZZ"))
		c.JSON(http.StatusOK, gin.H{
			"type": "encode",
			"url":  "thunder://" + encodedStr,
		})
	}
}

// UserAgent 解析 UserAgent 信息
func UserAgent(c *gin.Context) {
	uaStr := c.Query("ua")
	if uaStr == "" {
		uaStr = c.Request.UserAgent()
	}

	ua := user_agent.New(uaStr)
	
	engineName, engineVersion := ua.Engine()
	browserName, browserVersion := ua.Browser()
	
	osInfo := ua.OS()

	// 专门处理 Windows 11 的识别逻辑
	if c.Query("ua") == "" && strings.Contains(osInfo, "Windows 10") {
		platformVersion := c.GetHeader("Sec-CH-UA-Platform-Version")
		if platformVersion != "" {
			parts := strings.Split(strings.Trim(platformVersion, "\""), ".")
			if len(parts) > 0 {
				var major int
				fmt.Sscanf(parts[0], "%d", &major)
				if major >= 13 {
					osInfo = "Windows 11"
				}
			}
		}
	}

	c.JSON(http.StatusOK, gin.H{
		"ua":              uaStr,
		"platform":        ua.Platform(),
		"os":              osInfo,
		"mobile":          ua.Mobile(),
		"bot":             ua.Bot(),
		"browser_name":    browserName,
		"browser_version": browserVersion,
		"engine_name":     engineName,
		"engine_version":  engineVersion,
	})
}