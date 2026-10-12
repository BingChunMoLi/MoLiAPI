package store

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"testing"
	"time"
)

func TestSnapshotDeepCopyAndRollback(t *testing.T) {
	s := NewState()
	s.User = &User{Name: "admin"}
	s.Config["key"] = "value"
	s.Navigations = []Navigation{{Title: "nav", Tags: []Tag{{Name: "tag"}}}}
	s.Logs = []DailyLog{{URL: "log"}}
	s.Devices = []Device{{Token: "token"}}
	s.Subscriptions = []Subscription{{Email: "reader@example.com"}}
	s.Bing["day"] = map[string]any{"nested": []any{map[string]any{"title": "image"}}}
	s.Hosts["host"] = "0.0.0.0"
	s.Playlists["1"] = json.RawMessage(`{"id":1}`)
	s.Media["1"] = Media{Title: "song"}
	s.PushLogs = []PushLog{{Title: "push"}}
	f := &File{state: s}
	before, _ := json.Marshal(s)
	failure := errors.New("rollback")
	mutate := func(snapshot *State) error {
		snapshot.User.Name = "changed"
		snapshot.Config["key"] = "changed"
		snapshot.Navigations[0].Title = "changed"
		snapshot.Navigations[0].Tags[0].Name = "changed"
		snapshot.Daily["moli"][0] = "changed"
		snapshot.Logs[0].URL = "changed"
		snapshot.Devices[0].Token = "changed"
		snapshot.Subscriptions[0].Email = "changed"
		snapshot.Bing["day"]["nested"].([]any)[0].(map[string]any)["title"] = "changed"
		snapshot.Hosts["host"] = "changed"
		snapshot.Playlists["1"][6] = '2'
		snapshot.Media["1"] = Media{Title: "changed"}
		snapshot.PushLogs[0].Title = "changed"
		return failure
	}
	for _, operation := range []func(func(*State) error) error{f.View, f.Update} {
		if err := operation(mutate); !errors.Is(err, failure) {
			t.Fatal(err)
		}
		after, _ := json.Marshal(f.state)
		if !bytes.Equal(before, after) {
			t.Fatal("snapshot mutation changed committed state")
		}
	}
}

func TestClonePreservesJSONAndRejectsInvalidValues(t *testing.T) {
	s := benchmarkState()
	s.Bing["day"] = map[string]any{"id": 1, "tags": []string{"image"}, "null": nil}
	want, _ := json.Marshal(s)
	next, err := clone(s)
	if err != nil {
		t.Fatal(err)
	}
	got, _ := json.Marshal(next)
	if !bytes.Equal(got, want) {
		t.Fatal("snapshot changed JSON representation")
	}
	s.Playlists["invalid"] = json.RawMessage(`{`)
	if _, err := clone(s); err == nil {
		t.Fatal("accepted invalid raw JSON")
	}
	delete(s.Playlists, "invalid")
	s.Bing["day"]["invalid"] = make(chan int)
	if _, err := clone(s); err == nil {
		t.Fatal("accepted non-JSON Bing value")
	}
}

func benchmarkState() *State {
	s := NewState()
	for i := 0; i < 10000; i++ {
		s.Logs = append(s.Logs, DailyLog{ID: i + 1, URL: "https://example.com/sign", Created: time.Unix(1700000000, 0).UTC()})
	}
	for i := 0; i < 2000; i++ {
		s.Hosts[fmt.Sprintf("ads%d.example.com", i)] = "0.0.0.0"
	}
	s.Playlists["1"] = json.RawMessage(`{"tracks":[{"id":1,"name":"song"}]}`)
	return s
}

func BenchmarkFileView(b *testing.B) {
	f := &File{state: benchmarkState()}
	b.ReportAllocs()
	b.ResetTimer()
	for i := 0; i < b.N; i++ {
		if err := f.View(func(s *State) error { _ = s.Config["weatherKey"]; return nil }); err != nil {
			b.Fatal(err)
		}
	}
}
