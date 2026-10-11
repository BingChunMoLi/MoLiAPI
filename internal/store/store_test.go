package store

import (
	"errors"
	"os"
	"path/filepath"
	"sync"
	"testing"
)

func TestFilePersistsAndRollsBack(t *testing.T) {
	path := filepath.Join(t.TempDir(), "state.json")
	database, err := Open(path)
	if err != nil {
		t.Fatal(err)
	}
	if err = database.Update(func(st *State) error { st.Config["weatherKey"] = "first"; return nil }); err != nil {
		t.Fatal(err)
	}
	if err = database.Update(func(st *State) error { st.Config["weatherKey"] = "second"; return nil }); err != nil {
		t.Fatal(err)
	}
	failure := errors.New("rollback")
	err = database.Update(func(st *State) error { st.Config["weatherKey"] = "wrong"; return failure })
	if !errors.Is(err, failure) {
		t.Fatal(err)
	}
	restored, err := Open(path)
	if err != nil {
		t.Fatal(err)
	}
	if err = restored.View(func(st *State) error {
		if st.Config["weatherKey"] != "second" {
			t.Fatal("configuration did not persist")
		}
		st.Config["weatherKey"] = "mutated snapshot"
		return nil
	}); err != nil {
		t.Fatal(err)
	}
	restored.View(func(st *State) error {
		if st.Config["weatherKey"] != "second" {
			t.Fatal("view changed stored state")
		}
		return nil
	})
}

func TestFileWriteFailureDoesNotChangeMemory(t *testing.T) {
	root := t.TempDir()
	database, err := Open(filepath.Join(root, "blocked", "state.json"))
	if err != nil {
		t.Fatal(err)
	}
	if err = os.WriteFile(filepath.Join(root, "blocked"), []byte("file"), 0600); err != nil {
		t.Fatal(err)
	}
	if err = database.Update(func(st *State) error { st.Config["key"] = "value"; return nil }); err == nil {
		t.Fatal("expected write failure")
	}
	database.View(func(st *State) error {
		if st.Config["key"] != "" {
			t.Fatal("failed write changed memory")
		}
		return nil
	})
}

func TestFileConcurrentUpdates(t *testing.T) {
	database, _ := Open("")
	var workers sync.WaitGroup
	for i := 0; i < 100; i++ {
		workers.Add(1)
		go func() {
			defer workers.Done()
			if err := database.Update(func(st *State) error { st.ID(); return nil }); err != nil {
				t.Error(err)
			}
		}()
	}
	workers.Wait()
	database.View(func(st *State) error {
		if st.NextID != 101 {
			t.Fatalf("lost updates: %d", st.NextID)
		}
		return nil
	})
}

func TestCorruptStateIsRejected(t *testing.T) {
	for _, data := range []string{`{`, `{"version":2}`, `{"version":1,"config":null}`} {
		path := filepath.Join(t.TempDir(), "state.json")
		if err := os.WriteFile(path, []byte(data), 0600); err != nil {
			t.Fatal(err)
		}
		if _, err := Open(path); err == nil {
			t.Fatalf("accepted corrupt state %s", data)
		}
	}
}

func TestMySQLIntegration(t *testing.T) {
	dsn := os.Getenv("MYSQL_TEST_DSN")
	if dsn == "" {
		t.Skip("MYSQL_TEST_DSN must point to an empty disposable database")
	}
	database, err := OpenMySQL(dsn)
	if err != nil {
		t.Fatal(err)
	}
	defer database.Close()
	if err = database.Update(func(st *State) error { st.Config["integration"] = "value"; return nil }); err != nil {
		t.Fatal(err)
	}
	sentinel := errors.New("rollback")
	err = database.Update(func(st *State) error { st.Config["integration"] = "wrong"; return sentinel })
	if !errors.Is(err, sentinel) {
		t.Fatal(err)
	}
	if err = database.View(func(st *State) error {
		if st.Config["integration"] != "value" {
			t.Fatal("transaction rollback failed")
		}
		return nil
	}); err != nil {
		t.Fatal(err)
	}
	if err = database.Update(func(st *State) error { delete(st.Config, "integration"); return nil }); err != nil {
		t.Fatal(err)
	}
}
