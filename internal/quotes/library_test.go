package quotes

import (
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
	"reflect"
	"sync"
	"testing"
)

func TestLibraryConcurrentLoadAndSharedJSON(t *testing.T) {
	root := t.TempDir()
	for kind := byte('a'); kind <= 'l'; kind++ {
		data := fmt.Sprintf(`[{"id":%d,"type":%q,"from_who":null,"extra":{"tags":["test"]}}]`, kind, string(kind))
		if err := os.WriteFile(filepath.Join(root, string(kind)+".json"), []byte(data), 0600); err != nil {
			t.Fatal(err)
		}
	}
	library := New(root)
	var workers sync.WaitGroup
	for i := 0; i < 20; i++ {
		workers.Add(1)
		go func() {
			defer workers.Done()
			entries, err := library.All()
			if err != nil || len(entries) != 12 {
				t.Errorf("load: %d entries, %v", len(entries), err)
			}
		}()
	}
	workers.Wait()
	all, _ := library.All()
	category, err := library.Category('a')
	if err != nil {
		t.Fatal(err)
	}
	if &all[0].JSON[0] != &category[0].JSON[0] {
		t.Fatal("all/category duplicated JSON")
	}
	var value map[string]any
	if err := json.Unmarshal(all[0].JSON, &value); err != nil {
		t.Fatal(err)
	}
	if _, exists := value["from_who"]; !exists || value["from_who"] != nil || value["extra"] == nil {
		t.Fatal("lost null or extra fields", value)
	}
}

func TestParsePreservesRecordJSON(t *testing.T) {
	data := []byte(`[{"id":1,"type":"a","from_who":null,"extra":[1,{"x":true}]}]`)
	entries, err := Parse(data)
	if err != nil || len(entries) != 1 || entries[0].ID != 1 || entries[0].Kind != "a" {
		t.Fatalf("parse: %v, %v", entries, err)
	}
	var want []map[string]any
	var got map[string]any
	json.Unmarshal(data, &want)
	json.Unmarshal(entries[0].JSON, &got)
	if !reflect.DeepEqual(want[0], got) {
		t.Fatal("record changed")
	}
	for _, invalid := range []string{`{`, `[{}] trailing`, `[{"id":"wrong"}]`} {
		if _, err := Parse([]byte(invalid)); err == nil {
			t.Fatalf("accepted %q", invalid)
		}
	}
}

func TestLibraryErrors(t *testing.T) {
	root := t.TempDir()
	library := New(root)
	if _, err := library.Category('z'); err == nil {
		t.Fatal("accepted invalid category")
	}
	if entries, err := library.All(); err == nil || entries != nil {
		t.Fatal("missing file did not fail atomically")
	}
	for kind := byte('a'); kind <= 'l'; kind++ {
		if err := os.WriteFile(filepath.Join(root, string(kind)+".json"), []byte(`[]`), 0600); err != nil {
			t.Fatal(err)
		}
	}
	if _, err := New(root).All(); err == nil {
		t.Fatal("accepted empty library")
	}
}

func TestCategoryRetriesFailedLoadAndCachesSuccess(t *testing.T) {
	root := t.TempDir()
	library := New(root)
	if _, err := library.Category('a'); err == nil {
		t.Fatal("expected missing file")
	}
	path := filepath.Join(root, "a.json")
	if err := os.WriteFile(path, []byte(`[{"id":1,"type":"a"}]`), 0600); err != nil {
		t.Fatal(err)
	}
	if _, err := library.Category('a'); err != nil {
		t.Fatal("failed load was cached", err)
	}
	if err := os.Remove(path); err != nil {
		t.Fatal(err)
	}
	entries, err := library.Category('a')
	if err != nil || len(entries) != 1 || entries[0].ID != 1 {
		t.Fatalf("successful load was not cached: %v, %v", entries, err)
	}
}
