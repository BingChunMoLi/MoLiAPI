package application

import (
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
)

func (s *Server) fetch(ctx context.Context, target string, headers map[string]string) ([]byte, string, error) {
	request, err := http.NewRequestWithContext(ctx, http.MethodGet, target, nil)
	if err != nil {
		return nil, "", err
	}
	for key, value := range headers {
		request.Header.Set(key, value)
	}
	response, err := s.Client.Do(request)
	if err != nil {
		return nil, "", err
	}
	defer response.Body.Close()
	if response.StatusCode < 200 || response.StatusCode >= 300 {
		return nil, "", fmt.Errorf("upstream HTTP %d", response.StatusCode)
	}
	data, err := io.ReadAll(io.LimitReader(response.Body, 8<<20+1))
	if err != nil {
		return nil, "", err
	}
	if len(data) > 8<<20 {
		return nil, "", fmt.Errorf("upstream response too large")
	}
	return data, response.Header.Get("Content-Type"), nil
}

func (s *Server) getJSON(ctx context.Context, target string, headers map[string]string) (map[string]any, error) {
	data, _, err := s.fetch(ctx, target, headers)
	if err != nil {
		return nil, err
	}
	var result map[string]any
	if err = json.Unmarshal(data, &result); err != nil {
		return nil, err
	}
	if result == nil {
		return nil, fmt.Errorf("empty upstream JSON")
	}
	return result, nil
}
