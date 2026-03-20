package metrics

import (
	"sync"
	"time"

	"github.com/gin-gonic/gin"
)

type RequestMetric struct {
	Timestamp time.Time `json:"timestamp"`
	Path      string    `json:"path"`
	Duration  float64   `json:"duration"` // in milliseconds
	Status    int       `json:"status"`
}

var (
	metrics     []RequestMetric
	metricsLock sync.RWMutex
	maxMetrics  = 1000
)

func Middleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		start := time.Now()
		c.Next()
		duration := time.Since(start).Seconds() * 1000

		metricsLock.Lock()
		defer metricsLock.Unlock()

		m := RequestMetric{
			Timestamp: time.Now(),
			Path:      c.Request.URL.Path,
			Duration:  duration,
			Status:    c.Writer.Status(),
		}

		metrics = append(metrics, m)
		if len(metrics) > maxMetrics {
			metrics = metrics[1:]
		}
	}
}

func GetMetrics() []RequestMetric {
	metricsLock.RLock()
	defer metricsLock.RUnlock()
	
	// Return a copy
	res := make([]RequestMetric, len(metrics))
	copy(res, metrics)
	return res
}
