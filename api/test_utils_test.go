package api

import (
	"github.com/gin-gonic/gin"
)

// setupRouter 初始化一个用于测试的 Gin 引擎
func setupRouter() *gin.Engine {
	gin.SetMode(gin.TestMode)
	r := gin.Default()
	return r
}