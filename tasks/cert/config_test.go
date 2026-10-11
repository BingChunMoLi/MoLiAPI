package cert

import (
	"path/filepath"
	"testing"
)

func TestLegacyCertificateConfigAndExplicitTaskPriority(t *testing.T) {
	t.Setenv("CONFIG_PATH", filepath.Join(t.TempDir(), "missing.yaml"))
	t.Setenv("CERT_TASKS_JSON", "")
	values := map[string]string{"certificatePath": "cert.pem", "privateKeyPath": "key.pem", "domain": "example.com"}
	SetLegacyConfigProvider(func() map[string]string { return values })
	defer SetLegacyConfigProvider(nil)
	cfg := LoadConfig()
	if len(cfg.Tasks) != 1 || cfg.Tasks[0].CertPath != "cert.pem" || cfg.Tasks[0].Targets[0].Domain != "example.com" {
		t.Fatal("legacy backend certificate fields not applied")
	}
	values["domain"] = "updated.example.com"
	if LoadConfig().Tasks[0].Targets[0].Domain != "updated.example.com" {
		t.Fatal("updated config was not reloaded")
	}
	t.Setenv("CERT_TASKS_JSON", `[{"cert_path":"explicit.pem","key_path":"explicit.key","targets":[]}]`)
	cfg = LoadConfig()
	if len(cfg.Tasks) != 1 || cfg.Tasks[0].CertPath != "explicit.pem" {
		t.Fatal("explicit tasks were overridden")
	}
}

func TestEmptyCertificateConfigCanBeFilledAfterStartup(t *testing.T) {
	t.Setenv("CONFIG_PATH", filepath.Join(t.TempDir(), "missing.yaml"))
	t.Setenv("CERT_TASKS_JSON", "")
	t.Setenv("DISABLE_CRON", "false")
	t.Setenv("AWS_LAMBDA_FUNCTION_NAME", "")
	t.Setenv("VERCEL", "")
	scheduler := InitCron()
	defer scheduler.Stop()
	if len(scheduler.Entries()) != 1 {
		t.Fatal("certificate reload scheduler was not registered")
	}
}
