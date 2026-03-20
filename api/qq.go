package api

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"net/http"
)

// QqAvatar QQ头像获取(支持多种方式)
func QqAvatar(c *gin.Context) {
	qq := c.Query("qq")
	if qq == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "参数 qq 不能为空"})
		return
	}
	size := c.DefaultQuery("size", "640")
	resType := c.DefaultQuery("type", "image")

	url := fmt.Sprintf("https://q1.qlogo.cn/g?b=qq&nk=%s&s=%s", qq, size)

	if resType == "json" {
		c.JSON(http.StatusOK, gin.H{"url": url})
		return
	}
	c.Redirect(http.StatusFound, url)
}

// QzoneAvatar QQ空间头像获取(支持多种方式)
func QzoneAvatar(c *gin.Context) {
	qq := c.Query("qq")
	if qq == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "参数 qq 不能为空"})
		return
	}
	resType := c.DefaultQuery("type", "image")

	url := fmt.Sprintf("https://qlogo.store.qq.com/qzone/%s/%s/100", qq, qq)

	if resType == "json" {
		c.JSON(http.StatusOK, gin.H{"url": url})
		return
	}
	c.Redirect(http.StatusFound, url)
}