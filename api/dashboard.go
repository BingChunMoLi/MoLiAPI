package api

import (
	"crypto/x509"
	"encoding/pem"
	"net/http"
	"os"
	"time"

	"bingchunmoli.com/moliapi/internal/metrics"
	"bingchunmoli.com/moliapi/tasks/cert"
	"github.com/gin-gonic/gin"
)

type CertStatus struct {
	Domain     string    `json:"domain"`
	CertPath   string    `json:"cert_path"`
	Expiry     time.Time `json:"expiry"`
	DaysLeft   int       `json:"days_left"`
	Exists     bool      `json:"exists"`
	Provider   string    `json:"provider"`
	Service    string    `json:"service"`
}

func GetStats(c *gin.Context) {
	c.JSON(http.StatusOK, metrics.GetMetrics())
}

func GetCertStatus(c *gin.Context) {
	cfg := cert.LoadConfig()
	var statuses []CertStatus

	for _, task := range cfg.Tasks {
		expiry, exists := getCertExpiry(task.CertPath)
		daysLeft := 0
		if exists {
			daysLeft = int(time.Until(expiry).Hours() / 24)
		}

		for _, target := range task.Targets {
			statuses = append(statuses, CertStatus{
				Domain:   target.Domain,
				CertPath: task.CertPath,
				Expiry:   expiry,
				DaysLeft: daysLeft,
				Exists:   exists,
				Provider: target.Provider,
				Service:  target.Service,
			})
		}
	}
	c.JSON(http.StatusOK, statuses)
}

func getCertExpiry(path string) (time.Time, bool) {
	data, err := os.ReadFile(path)
	if err != nil {
		return time.Time{}, false
	}

	block, _ := pem.Decode(data)
	if block == nil {
		return time.Time{}, false
	}

	cert, err := x509.ParseCertificate(block.Bytes)
	if err != nil {
		return time.Time{}, false
	}

	return cert.NotAfter, true
}
