package delivery

import (
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
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"

	"bingchunmoli.com/moliapi/internal/store"
)

func TestHMSDeliveryCachesTokenAndRejectsProviderError(t *testing.T) {
	tokens, messages := 0, 0
	rejected := false
	upstream := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path == "/oauth" {
			tokens++
			r.ParseForm()
			if r.Form.Get("client_id") != "test-id" || r.Form.Get("grant_type") != "client_credentials" {
				t.Error("invalid OAuth request")
			}
			fmt.Fprint(w, `{"access_token":"test-access","expires_in":3600}`)
			return
		}
		messages++
		if r.Header.Get("Authorization") != "Bearer test-access" {
			t.Error("missing authorization")
		}
		var payload struct {
			Message struct {
				Token []string `json:"token"`
			} `json:"message"`
		}
		if err := json.NewDecoder(r.Body).Decode(&payload); err != nil ||
			len(payload.Message.Token) != 1 || payload.Message.Token[0] != "device-token" {
			t.Error("invalid HMS payload")
		}
		if rejected {
			fmt.Fprint(w, `{"code":"bad"}`)
		} else {
			fmt.Fprint(w, `{"code":"80000000"}`)
		}
	}))
	defer upstream.Close()
	push := &Push{Client: upstream.Client(), HMSClientID: "test-id", HMSSecret: "test-secret",
		HMSOAuthURL: upstream.URL + "/oauth", HMSSendURL: upstream.URL + "/send"}
	for i := 0; i < 2; i++ {
		if err := push.Send(context.Background(), store.Device{PushType: "hms", HMSToken: "device-token"},
			"title", "body"); err != nil {
			t.Fatal(err)
		}
	}
	if tokens != 1 || messages != 2 {
		t.Fatal("token caching failed", tokens, messages)
	}
	rejected = true
	if err := push.Send(context.Background(), store.Device{PushType: "hms", HMSToken: "device-token"},
		"title", "body"); err == nil {
		t.Fatal("provider error ignored")
	}
}

func TestFCMServiceAccountSignsAndSends(t *testing.T) {
	key, err := rsa.GenerateKey(rand.Reader, 2048)
	if err != nil {
		t.Fatal(err)
	}
	encoded, err := x509.MarshalPKCS8PrivateKey(key)
	if err != nil {
		t.Fatal(err)
	}
	tokens := 0
	upstream := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path == "/oauth" {
			tokens++
			r.ParseForm()
			parts := strings.Split(r.Form.Get("assertion"), ".")
			if len(parts) != 3 {
				t.Error("invalid assertion")
				w.WriteHeader(400)
				return
			}
			digest := sha256.Sum256([]byte(parts[0] + "." + parts[1]))
			signature, _ := base64.RawURLEncoding.DecodeString(parts[2])
			if err := rsa.VerifyPKCS1v15(&key.PublicKey, crypto.SHA256, digest[:], signature); err != nil {
				t.Error("invalid JWT signature", err)
			}
			payload, _ := base64.RawURLEncoding.DecodeString(parts[1])
			if !strings.Contains(string(payload), "firebase.messaging") || !strings.Contains(string(payload),
				"test@example.com") {
				t.Error("invalid JWT claims")
			}
			fmt.Fprint(w, `{"access_token":"fcm-access","expires_in":3600}`)
			return
		}
		if r.Header.Get("Authorization") != "Bearer fcm-access" {
			t.Error("missing FCM authorization")
		}
		var payload struct {
			Message struct {
				Topic string `json:"topic"`
			} `json:"message"`
		}
		if err := json.NewDecoder(r.Body).Decode(&payload); err != nil || payload.Message.Topic != "api" {
			t.Error("default topic missing")
		}
		fmt.Fprint(w, `{"name":"projects/test/messages/1"}`)
	}))
	defer upstream.Close()
	push := &Push{Client: upstream.Client(), FCMSendURL: upstream.URL + "/send",
		Account: &ServiceAccount{Project: "test", Email: "test@example.com", TokenURI: upstream.URL +
			"/oauth", PrivateKey: string(pem.EncodeToMemory(&pem.Block{Type: "PRIVATE KEY", Bytes: encoded}))}}
	for i := 0; i < 2; i++ {
		if err = push.Send(context.Background(), store.Device{PushType: "fcm"}, "title", "body"); err != nil {
			t.Fatal(err)
		}
	}
	if tokens != 1 {
		t.Fatal("FCM token not cached")
	}
}
