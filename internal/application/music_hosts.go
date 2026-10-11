package application

import (
	"context"
	"encoding/json"
	"fmt"
	"math/rand/v2"
	"net"
	"net/url"
	"sort"
	"strconv"
	"strings"

	"bingchunmoli.com/moliapi/internal/store"
	"github.com/gin-gonic/gin"
)

func (s *Server) playlist(ctx context.Context, id, cookie string) (map[string]any, error) {
	if _, err := strconv.ParseUint(id, 10, 64); err != nil || id == "0" {
		return nil, fmt.Errorf("invalid playlist ID")
	}
	cfg, err := s.config()
	if err != nil {
		return nil, err
	}
	data, err := s.getJSON(ctx, endpoint(cfg["musicURL"])+"/api/playlist/detail?id="+url.QueryEscape(id),
		map[string]string{"Cookie": cookie, "Referer": "https://music.163.com/", "User-Agent": "Mozilla/5.0"})
	if err != nil {
		return nil, err
	}
	if data["code"] != float64(200) {
		return nil, fmt.Errorf("playlist request rejected")
	}
	if _, valid := playlistResult(data); !valid {
		return nil, fmt.Errorf("invalid playlist response")
	}
	raw, err := json.Marshal(data)
	if err != nil {
		return nil, err
	}
	err = s.Store.Update(func(st *store.State) error { st.Playlists[id] = raw; return nil })
	return data, err
}

func playlistResult(data map[string]any) (map[string]any, bool) {
	result, valid := data["result"].(map[string]any)
	if !valid {
		result, valid = data["playlist"].(map[string]any)
	}
	return result, valid
}

func (s *Server) musicPlaylist(c *gin.Context) {
	id := c.Query("id")
	if _, err := strconv.ParseUint(id, 10, 64); err != nil || id == "0" {
		fail(c, 400, "id必须是正整数")
		return
	}
	data, err := s.playlist(c.Request.Context(), id, c.Query("cookie"))
	if err != nil {
		fail(c, 502, "获取网易云歌单失败")
		return
	}
	ok(c, data)
}

func playlistSongs(raw json.RawMessage) ([]gin.H, error) {
	var data map[string]any
	if err := json.Unmarshal(raw, &data); err != nil {
		return nil, err
	}
	playlist, valid := playlistResult(data)
	if !valid {
		return nil, fmt.Errorf("invalid cached playlist")
	}
	tracks, _ := playlist["tracks"].([]any)
	list := []gin.H{}
	for _, item := range tracks {
		track, valid := item.(map[string]any)
		if !valid {
			return nil, fmt.Errorf("invalid song")
		}
		album, _ := track["album"].(map[string]any)
		if album == nil {
			album, _ = track["al"].(map[string]any)
		}
		artists, _ := track["artists"].([]any)
		if artists == nil {
			artists, _ = track["ar"].([]any)
		}
		names := []string{}
		for _, artist := range artists {
			if a, valid := artist.(map[string]any); valid {
				if name, valid := a["name"].(string); valid {
					names = append(names, name)
				}
			}
		}
		list = append(list, gin.H{"id": track["id"], "thirdId": track["id"], "name": track["name"], "albumId": album["id"],
			"playlistId": playlist["id"], "picUrl": album["picUrl"], "publishTime": album["publishTime"],
			"type": album["type"], "nickname": strings.Join(names, " / "), "artists": artists})
	}
	return list, nil
}

func (s *Server) musicSongs(c *gin.Context) {
	id := c.Param("id")
	if _, err := strconv.ParseUint(id, 10, 64); err != nil || id == "0" {
		fail(c, 400, "无效的歌单ID")
		return
	}
	var raw json.RawMessage
	if err := s.Store.View(func(st *store.State) error { raw = st.Playlists[id]; return nil }); err != nil {
		internal(c, err)
		return
	}
	if raw == nil {
		ok(c, []gin.H{})
		return
	}
	list, err := playlistSongs(raw)
	if err != nil {
		internal(c, err)
		return
	}
	ok(c, list)
}

func (s *Server) musicRandom(c *gin.Context) {
	songs := []gin.H{}
	err := s.Store.View(func(st *store.State) error {
		for _, raw := range st.Playlists {
			list, err := playlistSongs(raw)
			if err != nil {
				return err
			}
			songs = append(songs, list...)
		}
		return nil
	})
	if err != nil {
		internal(c, err)
		return
	}
	if len(songs) == 0 {
		fail(c, 404, "暂无歌曲，请先获取歌单或启用音乐任务")
		return
	}
	target := fmt.Sprintf("https://music.163.com/song/media/outer/url?id=%.0f.mp3",
		songs[rand.IntN(len(songs))]["thirdId"])
	c.Header("Cache-Control", "no-store")
	c.Redirect(302, target)
}

func (s *Server) refreshHosts(ctx context.Context) error {
	cfg, err := s.config()
	if err != nil {
		return err
	}
	data, _, err := s.fetch(ctx, cfg["hostURL"], nil)
	if err != nil {
		return err
	}
	lines := map[string]bool{}
	for _, line := range strings.Split(string(data), "\n") {
		fields := strings.Fields(strings.SplitN(line, "#", 2)[0])
		if len(fields) < 2 || net.ParseIP(fields[0]) == nil {
			continue
		}
		for _, domain := range fields[1:] {
			lines[fields[0]+"\t"+domain] = true
		}
	}
	result := []string{}
	for line := range lines {
		result = append(result, line)
	}
	sort.Strings(result)
	if len(result) == 0 {
		return fmt.Errorf("empty hosts subscription")
	}
	return s.Store.Update(func(st *store.State) error {
		st.Hosts["neohostsBase"] = strings.Join(result,
			"\n")
		return nil
	})
}

func (s *Server) hosts(c *gin.Context) {
	types := c.QueryArray("type")
	if len(types) == 0 {
		types = []string{"0"}
	}
	selected := map[string]bool{}
	names := map[string]string{"1": "local", "2": "myAd", "3": "neohostsBase"}
	all := false
	for _, value := range types {
		for _, kind := range strings.Split(value, ",") {
			if kind == "0" {
				all = true
				continue
			}
			name, valid := names[kind]
			if !valid {
				fail(c, 400, "不支持的hosts类型")
				return
			}
			selected[name] = true
		}
	}
	cache := map[string]string{}
	read := func(st *store.State) error { cache = st.Hosts; return nil }
	if err := s.Store.View(read); err != nil {
		internal(c, err)
		return
	}
	if len(cache) == 0 && (all || selected["neohostsBase"]) {
		if err := s.refreshHosts(c.Request.Context()); err != nil {
			fail(c, 502, "获取hosts订阅失败")
			return
		}
		if err := s.Store.View(read); err != nil {
			internal(c, err)
			return
		}
	}
	keys := []string{}
	for key := range cache {
		if all || selected[key] {
			keys = append(keys, key)
		}
	}
	sort.Strings(keys)
	var text strings.Builder
	text.WriteString("#start\n#@author BingChunMoLi\n")
	for _, key := range keys {
		text.WriteString("#" + key + "--start\n" + cache[key] + "\n")
	}
	text.WriteString("#end\n")
	switch {
	case strings.HasSuffix(c.FullPath(), "/json"):
		ok(c, text.String())
	case strings.HasSuffix(c.FullPath(), "/file"):
		c.Header("Content-Disposition", `attachment; filename="hosts"`)
		c.Data(200, "application/octet-stream", []byte(text.String()))
	default:
		c.Data(200, "text/plain; charset=utf-8", []byte(text.String()))
	}
}
