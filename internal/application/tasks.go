package application

import (
	"context"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net/http"
	"net/url"
	"os"
	"os/exec"
	"path/filepath"
	"strconv"
	"strings"
	"time"

	"bingchunmoli.com/moliapi/internal/store"
	"github.com/gin-gonic/gin"
	"github.com/robfig/cron/v3"
)

var schedules = map[string]string{"bing": "0 0 * * *", "host": "0 0 * * *", "netease-music": "0 0 * * *",
	"daily": "0 8 * * *", "weather-notification": "30 18 * * *", "bilibili": "0 0 */3 * *", "image": "0 0 1 * *"}

func (s *Server) StartTasks(ctx context.Context) (*cron.Cron, error) {
	scheduler := cron.New(cron.WithLocation(s.now().Location()),
		cron.WithChain(cron.SkipIfStillRunning(cron.DefaultLogger)))
	if os.Getenv("DISABLE_CRON") == "true" || os.Getenv("AWS_LAMBDA_FUNCTION_NAME") != "" || os.Getenv("VERCEL") != "" {
		return scheduler, nil
	}
	cfg, err := s.config()
	if err != nil {
		return nil, err
	}
	for _, value := range strings.Split(cfg["tasks"], ",") {
		name := strings.TrimSpace(value)
		if name == "" {
			continue
		}
		schedule, exists := schedules[name]
		if !exists {
			return nil, fmt.Errorf("unknown task %q", name)
		}
		if _, err = scheduler.AddFunc(schedule, func() {
			jobCtx, cancel := context.WithTimeout(ctx, 30*time.Minute)
			defer cancel()
			var err error
			if name == "daily" {
				err = s.dailyAccounts(jobCtx, "bili")
			} else {
				err = s.RunTask(jobCtx, name)
			}
			if err != nil {
				log.Printf("task %s failed: %v", name, err)
			}
		}); err != nil {
			return nil, err
		}
		if name == "daily" {
			if _, err = scheduler.AddFunc("0 9 * * *", func() {
				jobCtx, cancel := context.WithTimeout(ctx, 30*time.Minute)
				defer cancel()
				if err := s.dailyAccounts(jobCtx, "52pojie"); err != nil {
					log.Printf("daily 52pojie task failed: %v", err)
				}
			}); err != nil {
				return nil, err
			}
		}
	}
	scheduler.Start()
	return scheduler, nil
}

func (s *Server) taskList(c *gin.Context) {
	cfg, err := s.config()
	if err != nil {
		internal(c, err)
		return
	}
	list := []gin.H{}
	for name, schedule := range schedules {
		enabled := false
		for _, item := range strings.Split(cfg["tasks"], ",") {
			if strings.TrimSpace(item) == name {
				enabled = true
			}
		}
		list = append(list, gin.H{"name": name, "cron": schedule, "enabled": enabled && os.Getenv("DISABLE_CRON") != "true"})
	}
	ok(c, list)
}

func (s *Server) taskRun(c *gin.Context) {
	name := c.Param("name")
	if _, exists := schedules[name]; !exists {
		fail(c, 404, "任务不存在")
		return
	}
	ctx, cancel := context.WithTimeout(c.Request.Context(), 30*time.Minute)
	defer cancel()
	if err := s.RunTask(ctx, name); err != nil {
		log.Printf("manual task %s: %v", name, err)
		fail(c, 502, "任务执行失败，请检查任务配置和服务器日志")
		return
	}
	ok(c, true)
}

func (s *Server) RunTask(ctx context.Context, name string) error {
	s.jobs.Lock()
	defer s.jobs.Unlock()
	switch name {
	case "bing":
		return s.refreshBing(ctx)
	case "host":
		return s.refreshHosts(ctx)
	case "netease-music":
		cfg, err := s.config()
		if err != nil {
			return err
		}
		if cfg["playListId"] == "" {
			return fmt.Errorf("PLAYLIST_ID is required")
		}
		_, err = s.playlist(ctx, cfg["playListId"], cfg["cookies"])
		return err
	case "weather-notification":
		return s.notifyWeather(ctx)
	case "daily":
		return s.dailyAccounts(ctx, "")
	case "bilibili":
		return s.refreshFavorites(ctx)
	case "image":
		return s.refreshImages(ctx)
	default:
		return fmt.Errorf("unknown task %q", name)
	}
}

type Account struct {
	Type    string `json:"type"`
	URL     string `json:"url"`
	Method  string `json:"method"`
	Cookies string `json:"cookies"`
}

func (s *Server) dailyAccounts(ctx context.Context, kind string) error {
	data := []byte(os.Getenv("DAILY_ACCOUNTS_JSON"))
	if path := os.Getenv("DAILY_ACCOUNTS_FILE"); path != "" {
		var err error
		data, err = os.ReadFile(path)
		if err != nil {
			return err
		}
	}
	var accounts []Account
	if len(data) == 0 {
		return fmt.Errorf("DAILY_ACCOUNTS_FILE or DAILY_ACCOUNTS_JSON is required")
	}
	if err := json.Unmarshal(data, &accounts); err != nil {
		return fmt.Errorf("invalid daily accounts JSON")
	}
	for _, account := range accounts {
		if kind != "" && account.Type != kind {
			continue
		}
		if !validHTTP(account.URL) {
			return fmt.Errorf("invalid daily account URL")
		}
		method := account.Method
		if method == "" {
			method = http.MethodGet
		}
		request, err := http.NewRequestWithContext(ctx, method, account.URL, nil)
		if err != nil {
			return err
		}
		request.Header.Set("Cookie", account.Cookies)
		request.Header.Set("User-Agent", "Mozilla/5.0")
		response, err := s.Client.Do(request)
		if err != nil {
			return err
		}
		_, err = io.Copy(io.Discard, io.LimitReader(response.Body, 1<<20))
		response.Body.Close()
		if err != nil {
			return err
		}
		if response.StatusCode < 200 || response.StatusCode >= 300 {
			return fmt.Errorf("daily account %s HTTP %d", account.Type, response.StatusCode)
		}
		if err = s.Store.Update(func(st *store.State) error {
			st.Logs = append(st.Logs, store.DailyLog{ID: st.ID(), URL: account.URL, Type: 1, Tenant: 1, Created: s.now()})
			return nil
		}); err != nil {
			return err
		}
	}
	return nil
}

func (s *Server) notify(ctx context.Context, title, body string) error {
	cfg, err := s.config()
	if err != nil {
		return err
	}
	if cfg["notifyEmail"] == "" && s.Notifier == nil && cfg["serverSauceKey"] == "" {
		return fmt.Errorf("a notification channel must be configured")
	}
	if cfg["notifyEmail"] != "" {
		if err = s.sendMail(ctx, cfg["notifyEmail"], title, body); err != nil {
			return err
		}
	}
	if s.Notifier != nil {
		d := store.Device{PushType: "fcm"}
		if err = s.Store.View(func(st *store.State) error {
			for _, device := range st.Devices {
				if device.Token != "" || device.HMSToken != "" {
					d = device
					break
				}
			}
			return nil
		}); err != nil {
			return err
		}
		err = s.Notifier.Send(ctx, d, title, body)
		if err = s.logPush(d.PushType, d.Name, title, err); err != nil {
			return err
		}
	}
	if cfg["serverSauceKey"] != "" {
		target := "https://sctapi.ftqq.com/" + url.PathEscape(cfg["serverSauceKey"]) + ".send"
		form := url.Values{"title": {title}, "desp": {body}}
		request, err := http.NewRequestWithContext(ctx, http.MethodPost, target, strings.NewReader(form.Encode()))
		if err != nil {
			return err
		}
		request.Header.Set("Content-Type", "application/x-www-form-urlencoded")
		response, err := s.Client.Do(request)
		if err == nil {
			defer response.Body.Close()
			var result struct {
				Code int `json:"code"`
			}
			err = json.NewDecoder(io.LimitReader(response.Body, 1<<20)).Decode(&result)
			if response.StatusCode != 200 || result.Code != 0 {
				err = fmt.Errorf("ServerChan rejected notification")
			}
		}
		if err = s.logPush("serverChan", "default", title, err); err != nil {
			return err
		}
	}
	return nil
}

func (s *Server) refreshImages(ctx context.Context) error {
	cfg, err := s.config()
	if err != nil {
		return err
	}
	counts := map[string]int{}
	for _, key := range []string{"pcPath", "mobilePath", "path1080"} {
		entries, err := os.ReadDir(cfg[key])
		if err != nil {
			return err
		}
		for _, entry := range entries {
			if entry.Type().IsRegular() {
				counts[key]++
			}
		}
	}
	return s.notify(ctx, "随机图更新成功", fmt.Sprint(counts))
}

func command(ctx context.Context, definition, target, dir string) error {
	var args []string
	if err := json.Unmarshal([]byte(definition), &args); err != nil || len(args) == 0 || args[0] == "" {
		return fmt.Errorf("command must be a JSON array of executable and arguments")
	}
	for i := range args {
		args[i] = strings.ReplaceAll(strings.ReplaceAll(args[i], "{url}", target), "{dir}", dir)
	}
	cmd := exec.CommandContext(ctx, args[0], args[1:]...)
	cmd.Stdout = io.Discard
	cmd.Stderr = io.Discard
	if err := cmd.Run(); err != nil {
		return fmt.Errorf("external command failed: %w", err)
	}
	return nil
}

func (s *Server) refreshFavorites(ctx context.Context) error {
	cfg, err := s.config()
	if err != nil {
		return err
	}
	id := cfg["biliFavId"]
	if _, err = strconv.ParseUint(id, 10, 64); err != nil || id == "0" {
		return fmt.Errorf("BILI_FAV_ID is required")
	}
	media := []store.Media{}
	complete := false
	for page := 1; page <= 1000; page++ {
		target := endpoint(cfg["bilibiliURL"]) + "/x/v3/fav/resource/list?media_id=" +
			url.QueryEscape(id) + "&pn=" + strconv.Itoa(page) + "&ps=20"
		data, err := s.getJSON(ctx, target, map[string]string{"Cookie": cfg["biliCookie"],
			"Referer": "https://www.bilibili.com/", "User-Agent": "Mozilla/5.0"})
		if err != nil {
			return err
		}
		if data["code"] != float64(0) {
			return fmt.Errorf("Bilibili favorites request rejected")
		}
		body, valid := data["data"].(map[string]any)
		if !valid {
			return fmt.Errorf("invalid favorites response")
		}
		entries, _ := body["medias"].([]any)
		for _, item := range entries {
			entry, valid := item.(map[string]any)
			if !valid {
				return fmt.Errorf("invalid media")
			}
			numeric, valid := entry["id"].(float64)
			if !valid {
				return fmt.Errorf("invalid media ID")
			}
			title, _ := entry["title"].(string)
			bvid, _ := entry["bvid"].(string)
			media = append(media, store.Media{ID: int64(numeric), BVID: bvid, Title: title})
		}
		more, _ := body["has_more"].(bool)
		if !more {
			complete = true
			break
		}
		if len(entries) == 0 {
			return fmt.Errorf("favorites pagination returned no items")
		}
	}
	if !complete {
		return fmt.Errorf("favorites pagination limit reached")
	}
	for _, entry := range media {
		key := strconv.FormatInt(entry.ID, 10)
		invalid := strings.Contains(entry.Title, "已失效视频")
		if err = s.Store.Update(func(st *store.State) error {
			if old, exists := st.Media[key]; exists {
				old.BVID = entry.BVID
				if !invalid {
					old.Title = entry.Title
				}
				entry = old
			}
			st.Media[key] = entry
			return nil
		}); err != nil {
			return err
		}
		if invalid {
			if !entry.InvalidNotified {
				if err = s.notify(ctx, "发现已失效视频", fmt.Sprintf("av%d %s %s", entry.ID, entry.BVID, entry.Title)); err != nil {
					return err
				}
				entry.InvalidNotified = true
				if err = s.Store.Update(func(st *store.State) error { st.Media[key] = entry; return nil }); err != nil {
					return err
				}
			}
			continue
		}
		if cfg["downloadCommand"] == "" {
			continue
		}
		root := cfg["downloadDir"]
		if root == "" {
			root = "data/downloads"
		}
		dir, err := filepath.Abs(filepath.Join(root, key))
		if err != nil {
			return err
		}
		if err = os.MkdirAll(dir, 0700); err != nil {
			return err
		}
		target := "https://www.bilibili.com/video/av" + key
		if !entry.Downloaded {
			if err = command(ctx, cfg["downloadCommand"], target, dir); err != nil {
				return err
			}
			entry.Downloaded = true
			if err = s.Store.Update(func(st *store.State) error { st.Media[key] = entry; return nil }); err != nil {
				return err
			}
		}
		if cfg["uploadCommand"] != "" && !entry.Uploaded {
			if err = command(ctx, cfg["uploadCommand"], target, dir); err != nil {
				return err
			}
			entry.Uploaded = true
			if err = s.Store.Update(func(st *store.State) error { st.Media[key] = entry; return nil }); err != nil {
				return err
			}
		}
	}
	return nil
}
