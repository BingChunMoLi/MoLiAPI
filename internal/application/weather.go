package application

import (
	"context"
	"crypto/hmac"
	"crypto/sha256"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"net/mail"
	"net/url"
	"strconv"
	"strings"
	"time"

	"bingchunmoli.com/moliapi/internal/store"
	"github.com/gin-gonic/gin"
)

func (s *Server) registerWeather(r gin.IRouter) {
	r.GET("/weather/byDay", s.weather)
	r.GET("/weather/now", s.weather)
	r.POST("/weather/sub", s.subscribe)
	r.GET("/weather/callback", s.subscriptionCallback)
	r.GET("/weather/unSub", s.unsubscribe)
	r.GET("/weather/unsub", s.unsubscribe)
}

func (s *Server) weatherData(ctx context.Context, day, location string) (map[string]any, error) {
	cfg, err := s.config()
	if err != nil {
		return nil, err
	}
	if cfg["weatherKey"] == "" {
		return nil, fmt.Errorf("weather not configured")
	}
	if !strings.Contains(location, ",") {
		if _, err := strconv.ParseUint(location, 10, 64); err != nil {
			result, err := s.getJSON(ctx, endpoint(cfg["weatherGeoUri"])+"/v2/city/lookup?key="+
				url.QueryEscape(cfg["weatherKey"])+"&location="+url.QueryEscape(location), nil)
			if err != nil {
				return nil, err
			}
			cities, valid := result["location"].([]any)
			if result["code"] != "200" || !valid || len(cities) == 0 {
				return nil, fmt.Errorf("location not found")
			}
			city, valid := cities[0].(map[string]any)
			if !valid {
				return nil, fmt.Errorf("invalid location")
			}
			location, _ = city["id"].(string)
			if location == "" {
				return nil, fmt.Errorf("invalid location ID")
			}
		}
	}
	path := "now"
	if day != "now" {
		path = day + "d"
	}
	result, err := s.getJSON(ctx, endpoint(cfg["weatherUri"])+"/v7/weather/"+path+"?key="+
		url.QueryEscape(cfg["weatherKey"])+"&location="+url.QueryEscape(location), nil)
	if err != nil {
		return nil, err
	}
	if result["code"] != "200" {
		return nil, fmt.Errorf("weather upstream rejected request")
	}
	return result, nil
}

func (s *Server) weather(c *gin.Context) {
	day := c.DefaultQuery("day", "3")
	if strings.HasSuffix(c.FullPath(), "/now") {
		day = "now"
	}
	if day != "3" && day != "7" && day != "now" {
		fail(c, 400, "day仅支持3或7")
		return
	}
	cfg, err := s.config()
	if err != nil {
		internal(c, err)
		return
	}
	if cfg["weatherKey"] == "" {
		fail(c, 503, "天气服务未配置")
		return
	}
	location := strings.TrimSpace(c.Query("location"))
	if location == "" {
		address, err := s.lookupAddress(c.Request.Context(), c.ClientIP(), cfg)
		if err != nil {
			fail(c, 502, "IP定位失败")
			return
		}
		lng, lngOK := address["lng"]
		lat, latOK := address["lat"]
		if lngOK && latOK {
			location = fmt.Sprint(lng) + "," + fmt.Sprint(lat)
		}
	}
	if location == "" {
		fail(c, 400, "请提供location或配置IP定位服务")
		return
	}
	data, err := s.weatherData(c.Request.Context(), day, location)
	if err != nil {
		fail(c, 502, "天气查询失败")
		return
	}
	ok(c, data)
}

type subscriptionClaims struct {
	Email    string `json:"email"`
	Location string `json:"location"`
	Action   string `json:"action"`
	Expiry   int64  `json:"exp"`
}

func signClaims(claims subscriptionClaims, key string) (string, error) {
	payload, err := json.Marshal(claims)
	if err != nil {
		return "", err
	}
	header := base64.RawURLEncoding.EncodeToString([]byte(`{"alg":"HS256","typ":"JWT"}`))
	body := header + "." + base64.RawURLEncoding.EncodeToString(payload)
	mac := hmac.New(sha256.New, []byte(key))
	mac.Write([]byte(body))
	return body + "." + base64.RawURLEncoding.EncodeToString(mac.Sum(nil)), nil
}

func (s *Server) verifyClaims(value, key, action string) (subscriptionClaims, error) {
	claims := subscriptionClaims{}
	parts := strings.Split(value, ".")
	if key == "" || len(parts) != 3 {
		return claims, fmt.Errorf("invalid subscription token")
	}
	mac := hmac.New(sha256.New, []byte(key))
	mac.Write([]byte(parts[0] + "." + parts[1]))
	signature, err := base64.RawURLEncoding.DecodeString(parts[2])
	if err != nil || !hmac.Equal(signature, mac.Sum(nil)) {
		return claims, fmt.Errorf("invalid signature")
	}
	data, err := base64.RawURLEncoding.DecodeString(parts[1])
	if err != nil {
		return claims, err
	}
	if err = json.Unmarshal(data, &claims); err != nil {
		return claims, err
	}
	if claims.Expiry <= s.now().Unix() || claims.Action != action || claims.Email == "" || claims.Location == "" {
		return claims, fmt.Errorf("expired or invalid token")
	}
	return claims, nil
}

func (s *Server) sendMail(ctx context.Context, email, title, body string) error {
	if s.Mailer == nil {
		return fmt.Errorf("mail not configured")
	}
	err := s.Mailer.Send(ctx, email, title, body)
	return s.logPush("mail", email, title, err)
}

func (s *Server) subscribe(c *gin.Context) {
	var input store.Subscription
	if !bind(c, &input) {
		return
	}
	address, err := mail.ParseAddress(input.Email)
	if err != nil || address.Address != input.Email || strings.TrimSpace(input.Location) == "" ||
		len(input.Location) > 200 {
		fail(c, 400, "请提供有效的邮箱和地点")
		return
	}
	cfg, err := s.config()
	if err != nil {
		internal(c, err)
		return
	}
	if cfg["weatherKey"] == "" || s.Mailer == nil || !validHTTP(cfg["publicURL"]) {
		fail(c, 503, "天气、邮件或PUBLIC_URL未配置")
		return
	}
	s.mu.Lock()
	for email, expiry := range s.pending {
		if !expiry.After(s.now()) {
			delete(s.pending, email)
		}
	}
	if s.pending[input.Email].After(s.now()) {
		s.mu.Unlock()
		fail(c, 429, "已经发送过确认邮件，请稍后重试")
		return
	}
	s.pending[input.Email] = s.now().Add(15 * time.Minute)
	s.mu.Unlock()
	confirm, err := signClaims(subscriptionClaims{Email: input.Email, Location: input.Location,
		Action: "confirm", Expiry: s.now().Add(24 * time.Hour).Unix()}, cfg["weatherKey"])
	if err != nil {
		internal(c, err)
		return
	}
	cancel, _ := signClaims(subscriptionClaims{Email: input.Email, Location: input.Location,
		Action: "unsubscribe", Expiry: s.now().AddDate(1, 0, 0).Unix()}, cfg["weatherKey"])
	base := strings.TrimRight(cfg["publicURL"], "/")
	body := "订阅地址: " + input.Location + "\n请在24小时内确认: " + base + "/weather/callback?param=" + url.QueryEscape(confirm) +
		"\n取消订阅: " + base + "/weather/unSub?param=" + url.QueryEscape(cancel) + "\n若非本人操作，请忽略此邮件。"
	if err = s.sendMail(c.Request.Context(), input.Email, "MoLiAPI天气订阅确认", body); err != nil {
		s.mu.Lock()
		delete(s.pending, input.Email)
		s.mu.Unlock()
		fail(c, 502, "确认邮件发送失败")
		return
	}
	ok(c, true)
}

func (s *Server) subscriptionCallback(c *gin.Context) {
	cfg, err := s.config()
	if err != nil {
		internal(c, err)
		return
	}
	claims, err := s.verifyClaims(c.Query("param"), cfg["weatherKey"], "confirm")
	if err != nil {
		fail(c, 400, "订阅确认链接无效或已过期")
		return
	}
	err = s.Store.Update(func(st *store.State) error {
		for _, sub := range st.Subscriptions {
			if sub.Email == claims.Email && sub.Location == claims.Location {
				return nil
			}
		}
		st.Subscriptions = append(st.Subscriptions, store.Subscription{Email: claims.Email, Location: claims.Location})
		return nil
	})
	if err != nil {
		internal(c, err)
		return
	}
	ok(c, true)
}

func (s *Server) unsubscribe(c *gin.Context) {
	cfg, err := s.config()
	if err != nil {
		internal(c, err)
		return
	}
	claims, err := s.verifyClaims(c.Query("param"), cfg["weatherKey"], "unsubscribe")
	if err != nil {
		fail(c, 400, "退订链接无效或已过期")
		return
	}
	err = s.Store.Update(func(st *store.State) error {
		list := []store.Subscription{}
		for _, sub := range st.Subscriptions {
			if sub.Email != claims.Email || sub.Location != claims.Location {
				list = append(list, sub)
			}
		}
		st.Subscriptions = list
		return nil
	})
	if err != nil {
		internal(c, err)
		return
	}
	ok(c, true)
}

func (s *Server) notifyWeather(ctx context.Context) error {
	subs := []store.Subscription{}
	if err := s.Store.View(func(st *store.State) error { subs = st.Subscriptions; return nil }); err != nil {
		return err
	}
	weather := map[string]map[string]any{}
	for _, sub := range subs {
		data := weather[sub.Location]
		if data == nil {
			var err error
			data, err = s.weatherData(ctx, "3", sub.Location)
			if err != nil {
				return err
			}
			weather[sub.Location] = data
		}
		daily, valid := data["daily"].([]any)
		if !valid {
			return fmt.Errorf("invalid forecast")
		}
		rain := false
		for _, entry := range daily {
			if day, valid := entry.(map[string]any); valid {
				for _, key := range []string{"iconDay", "iconNight"} {
					icon, _ := strconv.Atoi(fmt.Sprint(day[key]))
					if icon >= 300 && icon < 500 {
						rain = true
					}
				}
			}
		}
		if !rain {
			continue
		}
		body, _ := json.MarshalIndent(data, "", "  ")
		if err := s.sendMail(ctx, sub.Email, "天气不好，记得带伞", sub.Location+"\n"+string(body)); err != nil {
			return err
		}
	}
	return nil
}
