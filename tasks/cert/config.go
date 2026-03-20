package cert

import (
	"encoding/json"
	"log"
	"os"

	"gopkg.in/yaml.v3"
)

type TargetConfig struct {
	Provider string `json:"provider" yaml:"provider"`
	Service  string `json:"service" yaml:"service"`
	Domain   string `json:"domain,omitempty" yaml:"domain,omitempty"`
	Bucket   string `json:"bucket,omitempty" yaml:"bucket,omitempty"`
	Endpoint string `json:"endpoint,omitempty" yaml:"endpoint,omitempty"`
	Region   string `json:"region,omitempty" yaml:"region,omitempty"`
	ZoneID   string `json:"zone_id,omitempty" yaml:"zone_id,omitempty"`
}

type CertTask struct {
	CertPath string         `json:"cert_path" yaml:"cert_path"`
	KeyPath  string         `json:"key_path" yaml:"key_path"`
	Targets  []TargetConfig `json:"targets" yaml:"targets"`
}

type GlobalConfig struct {
	AliyunAccessKeyID     string     `yaml:"aliyun_access_key_id"`
	AliyunAccessKeySecret string     `yaml:"aliyun_access_key_secret"`
	TencentSecretID       string     `yaml:"tencent_secret_id"`
	TencentSecretKey      string     `yaml:"tencent_secret_key"`
	Cron                  string     `yaml:"cron"`
	ApiKey                string     `yaml:"api_key"`
	Tasks                 []CertTask `yaml:"tasks"`
}

func LoadConfig() *GlobalConfig {
	cfg := &GlobalConfig{
		Cron: "0 0 * * *",
	}

	// 1. Try loading from yaml
	configPath := os.Getenv("CONFIG_PATH")
	if configPath == "" {
		configPath = "config.yaml"
	}
	if data, err := os.ReadFile(configPath); err == nil {
		if err := yaml.Unmarshal(data, cfg); err != nil {
			log.Printf("Failed to parse %s: %v", configPath, err)
		}
	}

	// 2. Override with Env Vars
	if ak := os.Getenv("ALIYUN_ACCESS_KEY_ID"); ak != "" {
		cfg.AliyunAccessKeyID = ak
	}
	if sk := os.Getenv("ALIYUN_ACCESS_KEY_SECRET"); sk != "" {
		cfg.AliyunAccessKeySecret = sk
	}
	if tid := os.Getenv("TENCENT_SECRET_ID"); tid != "" {
		cfg.TencentSecretID = tid
	}
	if tsk := os.Getenv("TENCENT_SECRET_KEY"); tsk != "" {
		cfg.TencentSecretKey = tsk
	}
	if cron := os.Getenv("CERT_TASKS_CRON"); cron != "" {
		cfg.Cron = cron
	}
	if apiKey := os.Getenv("API_KEY"); apiKey != "" {
		cfg.ApiKey = apiKey
	}

	// 3. Load tasks from JSON Env if present
	if jsonStr := os.Getenv("CERT_TASKS_JSON"); jsonStr != "" {
		var envTasks []CertTask
		if err := json.Unmarshal([]byte(jsonStr), &envTasks); err == nil {
			cfg.Tasks = append(cfg.Tasks, envTasks...)
		} else {
			log.Printf("Failed to parse CERT_TASKS_JSON: %v", err)
		}
	}

	return cfg
}