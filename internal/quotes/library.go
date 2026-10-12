// Package quotes keeps immutable quote JSON and only the fields needed to select it.
package quotes

import (
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
	"sync"
)

type Entry struct {
	ID   int64
	Kind string
	JSON json.RawMessage
}

type category struct {
	mu      sync.Mutex
	loaded  bool
	entries []Entry
}

type Library struct {
	path       string
	categories [12]category
	allOnce    sync.Once
	all        []Entry
	allErr     error
}

// Default is shared by the legacy and application routes.
var Default = New("yiyan")

func New(path string) *Library { return &Library{path: path} }

// Category loads a file once. Callers must not modify the returned entries or JSON.
func (l *Library) Category(kind byte) ([]Entry, error) {
	if kind < 'a' || kind > 'l' {
		return nil, fmt.Errorf("unsupported quote category %q", kind)
	}
	c := &l.categories[kind-'a']
	c.mu.Lock()
	defer c.mu.Unlock()
	if c.loaded {
		return c.entries, nil
	}
	data, err := os.ReadFile(filepath.Join(l.path, string(kind)+".json"))
	if err != nil {
		return nil, err
	}
	entries, err := Parse(data)
	if err != nil {
		return nil, err
	}
	c.entries, c.loaded = entries, true
	return c.entries, nil
}

// All loads every category, preserving the order of files and entries.
// Its index shares JSON with Category instead of decoding another copy.
func (l *Library) All() ([]Entry, error) {
	l.allOnce.Do(func() {
		var lists [12][]Entry
		count := 0
		for kind := byte('a'); kind <= 'l'; kind++ {
			entries, err := l.Category(kind)
			if err != nil {
				l.allErr = err
				return
			}
			lists[kind-'a'] = entries
			count += len(entries)
		}
		if count == 0 {
			l.allErr = fmt.Errorf("empty quote library")
			return
		}
		l.all = make([]Entry, 0, count)
		for _, entries := range lists {
			l.all = append(l.all, entries...)
		}
	})
	return l.all, l.allErr
}

// Parse retains JSON so optional fields, nulls and extra metadata survive responses.
func Parse(data []byte) ([]Entry, error) {
	var records []json.RawMessage
	if err := json.Unmarshal(data, &records); err != nil {
		return nil, err
	}
	entries := make([]Entry, len(records))
	for i, record := range records {
		var key struct {
			ID   int64  `json:"id"`
			Kind string `json:"type"`
		}
		if err := json.Unmarshal(record, &key); err != nil {
			return nil, err
		}
		entries[i] = Entry{ID: key.ID, Kind: key.Kind, JSON: record}
	}
	return entries, nil
}
