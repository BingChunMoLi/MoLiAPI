package main

import (
	"fmt"
	"os"

	"bingchunmoli.com/moliapi/api"
	"bingchunmoli.com/moliapi/internal/metrics"
	"bingchunmoli.com/moliapi/tasks/cert"
	"github.com/gin-gonic/gin"
)

func main() {
	r := gin.Default()
	r.Use(metrics.Middleware())

	r.GET("/", func(c *gin.Context) {
		c.String(200, "Welcome to moliapi!")
	})

	r.StaticFile("/dashboard", "./ui/dashboard.html")
	r.GET("/api/stats", api.GetStats)
	r.GET("/api/certs", api.GetCertStatus)

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
	r.POST("/cert/update", api.UpdateCert)

	cCron := cert.InitCron()
	cCron.Start()
	defer cCron.Stop()

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
