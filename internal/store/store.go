package store

import (
	"encoding/json"
	"errors"
	"fmt"
	"maps"
	"os"
	"path/filepath"
	"slices"
	"sync"
	"time"
)

type Tag struct {
	ID       int    `json:"id"`
	Name     string `json:"tagName"`
	Open     bool   `json:"isOpen"`
	Private  bool   `json:"isPrivate"`
	Password string `json:"pwd"`
}

type Navigation struct {
	ID          int    `json:"id"`
	Title       string `json:"title"`
	Description string `json:"des"`
	URL         string `json:"url"`
	Icon        string `json:"icon"`
	Tenant      string `json:"tenant"`
	Tags        []Tag  `json:"tagList"`
}

type DailyLog struct {
	ID      int       `json:"id"`
	URL     string    `json:"url"`
	Type    int       `json:"type"`
	Tenant  int       `json:"tenant"`
	Created time.Time `json:"createTime"`
}

type User struct {
	Name     string `json:"name"`
	Password string `json:"password"`
}

type Device struct {
	ID        int    `json:"id"`
	Token     string `json:"token"`
	HMSToken  string `json:"hmsToken"`
	PushType  string `json:"pushType"`
	Name      string `json:"name"`
	Model     string `json:"model"`
	AndroidID string `json:"androidId"`
}

type Subscription struct {
	Email    string `json:"email"`
	Location string `json:"location"`
}

type Media struct {
	ID              int64  `json:"id"`
	BVID            string `json:"bvid"`
	Title           string `json:"title"`
	Downloaded      bool   `json:"downloaded"`
	Uploaded        bool   `json:"uploaded"`
	InvalidNotified bool   `json:"invalidNotified"`
}

type PushLog struct {
	Created time.Time `json:"createTime"`
	Type    string    `json:"type"`
	Target  string    `json:"target"`
	Title   string    `json:"title"`
	Success bool      `json:"success"`
}

type State struct {
	Version       int                        `json:"version"`
	NextID        int                        `json:"nextId"`
	User          *User                      `json:"user,omitempty"`
	Config        map[string]string          `json:"config"`
	Navigations   []Navigation               `json:"navigations"`
	Daily         map[string][]string        `json:"daily"`
	Logs          []DailyLog                 `json:"logs"`
	Devices       []Device                   `json:"devices"`
	Subscriptions []Subscription             `json:"subscriptions"`
	Bing          map[string]map[string]any  `json:"bing"`
	Hosts         map[string]string          `json:"hosts"`
	Playlists     map[string]json.RawMessage `json:"playlists"`
	Media         map[string]Media           `json:"media"`
	PushLogs      []PushLog                  `json:"pushLogs"`
}

func NewState() *State {
	return &State{Version: 1, NextID: 1, Config: map[string]string{},
		Navigations: []Navigation{}, Daily: map[string][]string{"moli": {
			"https://keylol.com/t735968-1-1", "https://www.52pojie.cn/home.php?mod=task&do=apply&id=2&referer=%2F",
			"https://hifiti.com/"}}, Logs: []DailyLog{}, Devices: []Device{}, Subscriptions: []Subscription{},
		Bing: map[string]map[string]any{}, Hosts: map[string]string{},
		Playlists: map[string]json.RawMessage{}, Media: map[string]Media{}, PushLogs: []PushLog{}}
}

func (s *State) ID() int { id := s.NextID; s.NextID++; return id }

func Decode(data []byte) (*State, error) {
	s := NewState()
	if err := json.Unmarshal(data, s); err != nil {
		return nil, fmt.Errorf("decode state: %w", err)
	}
	if err := s.validate(); err != nil {
		return nil, err
	}
	return s, nil
}

func (s *State) validate() error {
	if s.Version != 1 {
		return fmt.Errorf("unsupported state version %d", s.Version)
	}
	if s.NextID < 1 || s.Config == nil || s.Daily == nil || s.Bing == nil || s.Hosts == nil ||
		s.Playlists == nil || s.Media == nil {
		return errors.New("invalid state")
	}
	return nil
}

type Store interface {
	View(func(*State) error) error
	Update(func(*State) error) error
	Close() error
}

type File struct {
	mu    sync.RWMutex
	path  string
	state *State
}

func Open(path string) (*File, error) {
	f := &File{path: path, state: NewState()}
	if path == "" {
		return f, nil
	}
	data, err := os.ReadFile(path)
	if errors.Is(err, os.ErrNotExist) {
		return f, nil
	}
	if err != nil {
		return nil, err
	}
	f.state, err = Decode(data)
	return f, err
}

func clone(s *State) (*State, error) {
	if err := s.validate(); err != nil {
		return nil, err
	}
	// Strings are immutable and can be shared. Copy every mutable container so
	// snapshots and failed updates cannot change the committed state.
	next := *s
	if s.User != nil {
		user := *s.User
		next.User = &user
	}
	next.Config = maps.Clone(s.Config)
	next.Navigations = slices.Clone(s.Navigations)
	for i := range next.Navigations {
		next.Navigations[i].Tags = slices.Clone(s.Navigations[i].Tags)
	}
	next.Daily = maps.Clone(s.Daily)
	for key, values := range s.Daily {
		next.Daily[key] = slices.Clone(values)
	}
	next.Logs = slices.Clone(s.Logs)
	next.Devices = slices.Clone(s.Devices)
	next.Subscriptions = slices.Clone(s.Subscriptions)
	next.Hosts = maps.Clone(s.Hosts)
	next.Playlists = maps.Clone(s.Playlists)
	for key, data := range s.Playlists {
		if data != nil && !json.Valid(data) {
			return nil, fmt.Errorf("invalid playlist JSON %q", key)
		}
		next.Playlists[key] = slices.Clone(data)
	}
	next.Media = maps.Clone(s.Media)
	next.PushLogs = slices.Clone(s.PushLogs)
	// Bing contains arbitrary JSON values. Keep JSON's normalization and deep
	// copy semantics for this field without encoding the rest of the state.
	next.Bing = map[string]map[string]any{}
	if len(s.Bing) > 0 {
		data, err := json.Marshal(s.Bing)
		if err != nil {
			return nil, err
		}
		if err := json.Unmarshal(data, &next.Bing); err != nil {
			return nil, err
		}
	}
	return &next, nil
}

func (f *File) View(fn func(*State) error) error {
	f.mu.RLock()
	s, err := clone(f.state)
	f.mu.RUnlock()
	if err != nil {
		return err
	}
	return fn(s)
}

func (f *File) Update(fn func(*State) error) error {
	f.mu.Lock()
	defer f.mu.Unlock()
	next, err := clone(f.state)
	if err != nil {
		return err
	}
	if err = fn(next); err != nil {
		return err
	}
	if f.path != "" {
		if err = os.MkdirAll(filepath.Dir(f.path), 0700); err != nil {
			return err
		}
		data, marshalErr := json.Marshal(next)
		if marshalErr != nil {
			return marshalErr
		}
		tmp, createErr := os.CreateTemp(filepath.Dir(f.path), ".state-*")
		if createErr != nil {
			return createErr
		}
		name := tmp.Name()
		defer os.Remove(name)
		if err = tmp.Chmod(0600); err == nil {
			_, err = tmp.Write(data)
		}
		if err == nil {
			err = tmp.Sync()
		}
		closeErr := tmp.Close()
		if err != nil {
			return err
		}
		if closeErr != nil {
			return closeErr
		}
		if err = os.Rename(name, f.path); err != nil {
			return err
		}
	}
	f.state = next
	return nil
}

func (f *File) Close() error { return nil }
