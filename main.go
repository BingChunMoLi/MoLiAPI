package main

import (
	"fmt"
	"os"

	"bingchunmoli.com/moliapi/api"
	"github.com/gin-gonic/gin"
)

func main() {
	r := gin.Default()

	r.GET("/", func(c *gin.Context) {
		c.String(200, "Welcome to moliapi!")
	})

	r.GET("/yiyan/random", api.YiYan)
	r.GET("/img/random", api.Img)
	r.GET("/qrcode", api.QrCode)
	r.GET("/ip", api.IpQuery)
	r.GET("/bing/img", api.BingImg)
	r.GET("/poetry", api.Poetry)
	r.GET("/qq/avatar", api.QqAvatar)
	r.GET("/qq/qzone_avatar", api.QzoneAvatar)
	r.GET("/xunlei", api.XunleiConvert)
	r.GET("/ua", api.UserAgent)

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}
	err := r.Run(":" + port)
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Println("启动成功")
}
