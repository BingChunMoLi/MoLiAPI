package application

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"net/http/httptest"
	"os"
	"path/filepath"
	"strconv"
	"strings"
	"testing"

	"bingchunmoli.com/moliapi/internal/store"
)

func TestCommandHelper(t *testing.T) {
	if os.Getenv("MOLI_TEST_COMMAND") != "1" {
		return
	}
	if os.Getenv("MOLI_TEST_COMMAND_FAIL") == "1" {
		os.Exit(1)
	}
	dir := os.Args[len(os.Args)-2]
	name := os.Args[len(os.Args)-1]
	file := filepath.Join(dir, name)
	data, _ := os.ReadFile(file)
	count, _ := strconv.Atoi(string(data))
	if err := os.WriteFile(file, []byte(strconv.Itoa(count+1)), 0600); err != nil {
		os.Exit(2)
	}
	os.Exit(0)
}

func helperCommand(t *testing.T, name string) string {
	t.Helper()
	value, err := json.Marshal([]string{os.Args[0], "-test.run=^TestCommandHelper$", "--", "{dir}", name})
	if err != nil {
		t.Fatal(err)
	}
	return string(value)
}

func TestFavoriteDownloadRetriesAndDeduplicates(t *testing.T) {
	t.Setenv("MOLI_TEST_COMMAND", "1")
	upstream := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Query().Get("media_id") != "77" {
			t.Error("wrong favorite ID")
		}
		fmt.Fprint(w, `{"code":0,"data":{"medias":[{"id":123,"bvid":"BV123","title":"video"}],"has_more":false}}`)
	}))
	defer upstream.Close()
	dir := t.TempDir()
	server, _ := testServer(t, Options{Client: upstream.Client(),
		Config: map[string]string{"bilibiliURL": upstream.URL, "biliFavId": "77",
			"downloadDir": dir, "downloadCommand": helperCommand(t, "download"), "uploadCommand": helperCommand(t, "upload")}})
	t.Setenv("MOLI_TEST_COMMAND_FAIL", "1")
	if err := server.RunTask(context.Background(), "bilibili"); err == nil {
		t.Fatal("expected download failure")
	}
	server.Store.View(func(st *store.State) error {
		if st.Media["123"].Downloaded {
			t.Fatal("failed download marked successful")
		}
		return nil
	})
	t.Setenv("MOLI_TEST_COMMAND_FAIL", "0")
	for i := 0; i < 2; i++ {
		if err := server.RunTask(context.Background(), "bilibili"); err != nil {
			t.Fatal(err)
		}
	}
	for _, name := range []string{"download", "upload"} {
		data, err := os.ReadFile(filepath.Join(dir, "123", name))
		if err != nil || string(data) != "1" {
			t.Fatal("duplicate command execution", name, string(data), err)
		}
	}
	server.Store.View(func(st *store.State) error {
		item := st.Media["123"]
		if !item.Downloaded || !item.Uploaded {
			t.Fatal(item)
		}
		return nil
	})
}

func TestInvalidFavoriteNotificationRetries(t *testing.T) {
	upstream := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprint(w, `{"code":0,"data":{"medias":[{"id":42,"bvid":"BV42","title":"已失效视频"}],"has_more":false}}`)
	}))
	defer upstream.Close()
	mailer := &fakeMailer{failure: fmt.Errorf("temporary delivery failure")}
	server, _ := testServer(t, Options{Client: upstream.Client(), Mailer: mailer,
		Config: map[string]string{"bilibiliURL": upstream.URL, "biliFavId": "77", "notifyEmail": "reader@example.com"}})
	if err := server.RunTask(context.Background(), "bilibili"); err == nil {
		t.Fatal("expected mail failure")
	}
	server.Store.View(func(st *store.State) error {
		if st.Media["42"].InvalidNotified {
			t.Fatal("failed notification marked delivered")
		}
		return nil
	})
	mailer.failure = nil
	for i := 0; i < 2; i++ {
		if err := server.RunTask(context.Background(), "bilibili"); err != nil {
			t.Fatal(err)
		}
	}
	if len(mailer.sent) != 2 {
		t.Fatal("duplicate successful notification", len(mailer.sent))
	}
	server.Store.View(func(st *store.State) error {
		if !st.Media["42"].InvalidNotified || len(st.PushLogs) != 2 || st.PushLogs[0].Success || !st.PushLogs[1].Success {
			t.Fatal("notification status not persisted")
		}
		return nil
	})
}

func TestDailyAccountTask(t *testing.T) {
	calls := 0
	upstream := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		calls++
		if r.Method != "POST" || r.Header.Get("Cookie") != "test-cookie" {
			t.Error("account request was not preserved")
		}
		w.WriteHeader(204)
	}))
	defer upstream.Close()
	accounts, _ := json.Marshal([]Account{{Type: "bili", URL: upstream.URL, Method: "POST", Cookies: "test-cookie"}})
	t.Setenv("DAILY_ACCOUNTS_JSON", string(accounts))
	server, _ := testServer(t, Options{Client: upstream.Client()})
	if err := server.RunTask(context.Background(), "daily"); err != nil {
		t.Fatal(err)
	}
	if calls != 1 {
		t.Fatal(calls)
	}
	server.Store.View(func(st *store.State) error {
		if len(st.Logs) != 1 || st.Logs[0].URL != upstream.URL {
			t.Fatal(st.Logs)
		}
		return nil
	})
	t.Setenv("DISABLE_CRON", "true")
	scheduler, err := server.StartTasks(context.Background())
	if err != nil {
		t.Fatal(err)
	}
	defer scheduler.Stop()
	if len(scheduler.Entries()) != 0 {
		t.Fatal("disabled cron registered jobs")
	}
	t.Setenv("DISABLE_CRON", "false")
	server.defaults["tasks"] = "unknown"
	if _, err = server.StartTasks(context.Background()); err == nil || !strings.Contains(err.Error(), "unknown task") {
		t.Fatal(err)
	}
}
