package delivery

import (
	"bytes"
	"context"
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/base64"
	"encoding/json"
	"encoding/pem"
	"fmt"
	"io"
	"net/http"
	"net/url"
	"os"
	"strings"
	"sync"
	"time"

	"bingchunmoli.com/moliapi/internal/store"
)

type ServiceAccount struct {
	Project    string `json:"project_id"`
	Email      string `json:"client_email"`
	PrivateKey string `json:"private_key"`
	TokenURI   string `json:"token_uri"`
}

type Push struct {
	Client                                                                *http.Client
	Account                                                               *ServiceAccount
	HMSClientID, HMSSecret, HMSAppID, HMSOAuthURL, HMSSendURL, FCMSendURL string
	mu                                                                    sync.Mutex
	fcmToken, hmsToken                                                    string
	fcmExpiry, hmsExpiry                                                  time.Time
}

func PushFromEnvironment() (*Push, error) {
	p := &Push{Client: &http.Client{Timeout: 20 * time.Second},
		HMSClientID: os.Getenv("HMS_CLIENT_ID"), HMSSecret: os.Getenv("HMS_CLIENT_SECRET"),
		HMSAppID: os.Getenv("HMS_APP_ID"), HMSOAuthURL: "https://oauth-login.cloud.huawei.com/oauth2/v3/token"}
	if p.HMSAppID == "" {
		p.HMSAppID = p.HMSClientID
	}
	p.HMSSendURL = "https://push-api.cloud.huawei.com/v1/" + url.PathEscape(p.HMSAppID) + "/messages:send"
	if path := os.Getenv("FCM_CREDENTIALS_FILE"); path != "" {
		data, err := os.ReadFile(path)
		if err != nil {
			return nil, fmt.Errorf("read FCM credentials: %w", err)
		}
		p.Account = &ServiceAccount{}
		if err = json.Unmarshal(data, p.Account); err != nil {
			return nil, fmt.Errorf("invalid FCM credentials")
		}
		if p.Account.Project == "" || p.Account.Email == "" || p.Account.PrivateKey == "" {
			return nil, fmt.Errorf("incomplete FCM credentials")
		}
		if p.Account.TokenURI == "" {
			p.Account.TokenURI = "https://oauth2.googleapis.com/token"
		}
		p.FCMSendURL = "https://fcm.googleapis.com/v1/projects/" + url.PathEscape(p.Account.Project) + "/messages:send"
	}
	if p.Account == nil && (p.HMSClientID == "" || p.HMSSecret == "") {
		return nil, nil
	}
	return p, nil
}

func (p *Push) post(ctx context.Context, target, contentType, authorization string,
	body []byte) (map[string]any, error) {
	request, err := http.NewRequestWithContext(ctx, http.MethodPost, target, bytes.NewReader(body))
	if err != nil {
		return nil, err
	}
	request.Header.Set("Content-Type", contentType)
	if authorization != "" {
		request.Header.Set("Authorization", "Bearer "+authorization)
	}
	response, err := p.Client.Do(request)
	if err != nil {
		return nil, err
	}
	defer response.Body.Close()
	if response.StatusCode < 200 || response.StatusCode >= 300 {
		return nil, fmt.Errorf("push provider HTTP %d", response.StatusCode)
	}
	var result map[string]any
	if err = json.NewDecoder(io.LimitReader(response.Body, 1<<20)).Decode(&result); err != nil {
		return nil, err
	}
	return result, nil
}

func (p *Push) accessToken(ctx context.Context, hms bool) (string, error) {
	p.mu.Lock()
	defer p.mu.Unlock()
	if hms && p.hmsExpiry.After(time.Now()) {
		return p.hmsToken, nil
	}
	if !hms && p.fcmExpiry.After(time.Now()) {
		return p.fcmToken, nil
	}
	var target string
	values := url.Values{}
	if hms {
		if p.HMSClientID == "" || p.HMSSecret == "" {
			return "", fmt.Errorf("HMS is not configured")
		}
		target = p.HMSOAuthURL
		values = url.Values{"grant_type": {"client_credentials"}, "client_id": {p.HMSClientID},
			"client_secret": {p.HMSSecret}}
	} else {
		if p.Account == nil {
			return "", fmt.Errorf("FCM is not configured")
		}
		block, _ := pem.Decode([]byte(p.Account.PrivateKey))
		if block == nil {
			return "", fmt.Errorf("invalid FCM private key")
		}
		parsed, err := x509.ParsePKCS8PrivateKey(block.Bytes)
		if err != nil {
			parsed, err = x509.ParsePKCS1PrivateKey(block.Bytes)
		}
		if err != nil {
			return "", fmt.Errorf("invalid FCM private key")
		}
		key, valid := parsed.(*rsa.PrivateKey)
		if !valid {
			return "", fmt.Errorf("FCM requires RSA private key")
		}
		now := time.Now().Unix()
		claims, _ := json.Marshal(map[string]any{"iss": p.Account.Email,
			"scope": "https://www.googleapis.com/auth/firebase.messaging",
			"aud":   p.Account.TokenURI, "iat": now, "exp": now + 3600})
		assertion := base64.RawURLEncoding.EncodeToString([]byte(`{"alg":"RS256","typ":"JWT"}`)) + "." +
			base64.RawURLEncoding.EncodeToString(claims)
		digest := sha256.Sum256([]byte(assertion))
		signature, err := rsa.SignPKCS1v15(rand.Reader, key, crypto.SHA256, digest[:])
		if err != nil {
			return "", err
		}
		assertion += "." + base64.RawURLEncoding.EncodeToString(signature)
		target = p.Account.TokenURI
		values = url.Values{"grant_type": {"urn:ietf:params:oauth:grant-type:jwt-bearer"}, "assertion": {assertion}}
	}
	data, err := p.post(ctx, target, "application/x-www-form-urlencoded", "", []byte(values.Encode()))
	if err != nil {
		return "", err
	}
	value, valid := data["access_token"].(string)
	if !valid || strings.TrimSpace(value) == "" {
		return "", fmt.Errorf("provider returned no access token")
	}
	seconds, _ := data["expires_in"].(float64)
	if seconds <= 0 {
		seconds = 3600
	}
	expiry := time.Now().Add(time.Duration(seconds-120) * time.Second)
	if hms {
		p.hmsToken = value
		p.hmsExpiry = expiry
	} else {
		p.fcmToken = value
		p.fcmExpiry = expiry
	}
	return value, nil
}

func (p *Push) Send(ctx context.Context, device store.Device, title, body string) error {
	hms := device.PushType == "hms"
	access, err := p.accessToken(ctx, hms)
	if err != nil {
		return err
	}
	message := map[string]any{"notification": map[string]string{"title": title, "body": body}}
	target := p.FCMSendURL
	payload := map[string]any{"message": message}
	if hms {
		if device.HMSToken == "" {
			return fmt.Errorf("HMS device token is required")
		}
		message["token"] = []string{device.HMSToken}
		message["android"] = map[string]any{"notification": map[string]any{"click_action": map[string]int{"type": 3}}}
		payload["validate_only"] = false
		target = p.HMSSendURL
	} else if device.Token != "" {
		message["token"] = device.Token
	} else {
		message["topic"] = "api"
	}
	encoded, err := json.Marshal(payload)
	if err != nil {
		return err
	}
	result, err := p.post(ctx, target, "application/json", access, encoded)
	if err != nil {
		return err
	}
	if hms && result["code"] != "80000000" {
		return fmt.Errorf("HMS rejected notification")
	}
	if !hms {
		if name, valid := result["name"].(string); !valid || name == "" {
			return fmt.Errorf("FCM rejected notification")
		}
	}
	return nil
}
