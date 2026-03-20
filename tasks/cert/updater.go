package cert

import (
	"log"
	"os"

	"github.com/robfig/cron/v3"
)

type CertUpdater interface {
	UpdateCert(cert []byte, key []byte, target TargetConfig) error
}

func InitCron() *cron.Cron {
	c := cron.New()
	
	// Skip cron if we are in a serverless environment (often detected by specific env vars)
	if os.Getenv("AWS_LAMBDA_FUNCTION_NAME") != "" || os.Getenv("VERCEL") != "" || os.Getenv("DISABLE_CRON") == "true" {
		log.Println("Cron disabled (Serverless or DISABLE_CRON=true)")
		return c
	}

	cfg := LoadConfig()
	if len(cfg.Tasks) == 0 {
		log.Println("No certificate tasks configured.")
		return c
	}

	_, err := c.AddFunc(cfg.Cron, func() {
		log.Println("Running scheduled certificate update task...")
		// Reload config on each run to pick up changes without restart
		RunTasks(LoadConfig())
	})
	if err != nil {
		log.Fatalf("Failed to add cron job: %v", err)
	}

	return c
}

func RunTasks(cfg *GlobalConfig) {
	var aliyunUpdater CertUpdater
	if cfg.AliyunAccessKeyID != "" && cfg.AliyunAccessKeySecret != "" {
		aliyunUpdater = NewAliyunUpdater(cfg.AliyunAccessKeyID, cfg.AliyunAccessKeySecret)
	}

	var tencentUpdater CertUpdater
	if cfg.TencentSecretID != "" && cfg.TencentSecretKey != "" {
		tencentUpdater = NewTencentUpdater(cfg.TencentSecretID, cfg.TencentSecretKey)
	}

	for _, task := range cfg.Tasks {
		certData, err := os.ReadFile(task.CertPath)
		if err != nil {
			log.Printf("Failed to read cert file %s: %v", task.CertPath, err)
			continue
		}
		keyData, err := os.ReadFile(task.KeyPath)
		if err != nil {
			log.Printf("Failed to read key file %s: %v", task.KeyPath, err)
			continue
		}

		for _, target := range task.Targets {
			var err error
			switch target.Provider {
			case "aliyun":
				if aliyunUpdater == nil {
					log.Println("Aliyun credentials not set, skipping target:", target.Domain)
					continue
				}
				err = aliyunUpdater.UpdateCert(certData, keyData, target)
			case "tencent":
				if tencentUpdater == nil {
					log.Println("Tencent credentials not set, skipping target:", target.Domain)
					continue
				}
				err = tencentUpdater.UpdateCert(certData, keyData, target)
			default:
				log.Printf("Unknown provider: %s", target.Provider)
			}

			if err != nil {
				log.Printf("Failed to update cert for %s %s (%s): %v", target.Provider, target.Service, target.Domain, err)
			} else {
				log.Printf("Successfully updated cert for %s %s (%s)", target.Provider, target.Service, target.Domain)
			}
		}
	}
}