package application

import (
	"context"
	"fmt"
	"net"
	"net/url"
	"strings"
)

func (s *Server) lookupAddress(ctx context.Context, ip string, cfg map[string]string) (map[string]any, error) {
	parsed := net.ParseIP(ip)
	if parsed == nil {
		return nil, fmt.Errorf("invalid client IP")
	}
	if parsed.IsLoopback() || parsed.IsPrivate() || parsed.IsLinkLocalUnicast() || parsed.IsUnspecified() {
		return map[string]any{
			"ip": ip, "countryLong": "内网", "countryShort": "", "province": "", "city": "", "local": true,
		}, nil
	}
	base := cfg["ipLookupURL"]
	if base == "" {
		base = "https://ipwho.is/{ip}"
	}
	target := strings.ReplaceAll(base, "{ip}", url.PathEscape(ip))
	if !strings.Contains(base, "{ip}") {
		u, err := url.Parse(endpoint(base))
		if err != nil {
			return nil, err
		}
		query := u.Query()
		query.Set("ip", ip)
		u.RawQuery = query.Encode()
		target = u.String()
	}
	data, err := s.getJSON(ctx, target, nil)
	if err != nil {
		return nil, err
	}
	if success, exists := data["success"].(bool); exists && !success {
		return nil, fmt.Errorf("IP lookup rejected")
	}
	for from, to := range map[string]string{"latitude": "lat", "longitude": "lng", "country": "countryLong",
		"country_code": "countryShort", "region": "province"} {
		if data[to] == nil && data[from] != nil {
			data[to] = data[from]
		}
	}
	data["ip"] = ip
	if connection, valid := data["connection"].(map[string]any); valid {
		data["isp"] = connection["isp"]
	}
	return data, nil
}
