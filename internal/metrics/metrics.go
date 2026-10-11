package metrics

import (
	"fmt"
	"net/http"
	"sort"
	"strconv"
	"strings"
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
	totals      = map[string]*total{}
)

type total struct {
	Count   uint64
	Seconds float64
}

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
		path := c.FullPath()
		if path == "" {
			path = "unmatched"
		}
		key := strconv.Quote(path) + ",status=" + strconv.Quote(strconv.Itoa(c.Writer.Status()))
		value := totals[key]
		if value == nil {
			value = &total{}
			totals[key] = value
		}
		value.Count++
		value.Seconds += time.Since(start).Seconds()
		if len(metrics) > maxMetrics {
			metrics = metrics[1:]
		}
	}
}

func Prometheus(c *gin.Context) {
	metricsLock.RLock()
	defer metricsLock.RUnlock()
	var body strings.Builder
	body.WriteString("# TYPE moli_http_requests_total counter\n# TYPE moli_http_request_duration_seconds_sum counter\n")
	keys := make([]string, 0, len(totals))
	for key := range totals {
		keys = append(keys, key)
	}
	sort.Strings(keys)
	for _, key := range keys {
		value := totals[key]
		fmt.Fprintf(&body, "moli_http_requests_total{path=%s} %d\n", key, value.Count)
		fmt.Fprintf(&body, "moli_http_request_duration_seconds_sum{path=%s} %f\n", key, value.Seconds)
	}
	c.Data(http.StatusOK, "text/plain; version=0.0.4; charset=utf-8", []byte(body.String()))
}

func GetMetrics() []RequestMetric {
	metricsLock.RLock()
	defer metricsLock.RUnlock()

	// Return a copy
	res := make([]RequestMetric, len(metrics))
	copy(res, metrics)
	return res
}
