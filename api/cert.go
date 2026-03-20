package api

import (
	"net/http"

	"bingchunmoli.com/moliapi/tasks/cert"
	"github.com/gin-gonic/gin"
)

func UpdateCert(c *gin.Context) {
	cfg := cert.LoadConfig()

	// Simple API Key check
	if cfg.ApiKey != "" {
		providedKey := c.GetHeader("X-API-Key")
		if providedKey == "" {
			providedKey = c.Query("api_key")
		}
		if providedKey != cfg.ApiKey {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Unauthorized"})
			return
		}
	}

	// Trigger tasks in a goroutine so we can respond immediately to the caller
	// (Especially useful for acme.sh or webhooks with timeouts)
	go cert.RunTasks(cfg)

	c.JSON(http.StatusOK, gin.H{"status": "Update task triggered"})
}