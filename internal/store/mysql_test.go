package store

import (
	"database/sql/driver"
	"encoding/json"
	"errors"
	"testing"

	"github.com/DATA-DOG/go-sqlmock"
)

type stateArgument struct{ key, value string }

func (a stateArgument) Match(value driver.Value) bool {
	data, valid := value.([]byte)
	if !valid {
		return false
	}
	st, err := Decode(data)
	return err == nil && st.Config[a.key] == a.value
}

func TestMySQLInitializesDedicatedTable(t *testing.T) {
	db, mock, err := sqlmock.New(sqlmock.QueryMatcherOption(sqlmock.QueryMatcherEqual))
	if err != nil {
		t.Fatal(err)
	}
	defer db.Close()
	mock.ExpectExec(`CREATE TABLE IF NOT EXISTS moli_go_state (
        id TINYINT NOT NULL PRIMARY KEY, payload LONGBLOB NOT NULL) ENGINE=InnoDB`).
		WillReturnResult(sqlmock.NewResult(0, 0))
	mock.ExpectExec("INSERT IGNORE INTO moli_go_state (id, payload) VALUES (1, ?)").
		WithArgs(stateArgument{}).WillReturnResult(sqlmock.NewResult(1, 1))
	if err = initializeMySQL(db); err != nil {
		t.Fatal(err)
	}
	if err = mock.ExpectationsWereMet(); err != nil {
		t.Fatal(err)
	}
}

func TestMySQLTransactionCommitAndRollback(t *testing.T) {
	sentinel := errors.New("expected failure")
	for _, kind := range []string{"success", "business-failure", "write-failure", "corrupt-state"} {
		t.Run(kind, func(t *testing.T) {
			db, mock, err := sqlmock.New(sqlmock.QueryMatcherOption(sqlmock.QueryMatcherEqual))
			if err != nil {
				t.Fatal(err)
			}
			defer db.Close()
			database := &MySQL{db: db}
			initial, _ := json.Marshal(NewState())
			if kind == "corrupt-state" {
				initial = []byte("{")
			}
			mock.ExpectBegin()
			mock.ExpectQuery("SELECT payload FROM moli_go_state WHERE id = 1 FOR UPDATE").
				WillReturnRows(sqlmock.NewRows([]string{"payload"}).AddRow(initial))
			switch kind {
			case "success":
				mock.ExpectExec("UPDATE moli_go_state SET payload = ? WHERE id = 1").
					WithArgs(stateArgument{key: "weatherKey", value: "updated"}).
					WillReturnResult(sqlmock.NewResult(0, 1))
				mock.ExpectCommit()
			case "write-failure":
				mock.ExpectExec("UPDATE moli_go_state SET payload = ? WHERE id = 1").
					WithArgs(stateArgument{key: "weatherKey", value: "updated"}).WillReturnError(sentinel)
				mock.ExpectRollback()
			default:
				mock.ExpectRollback()
			}
			err = database.Update(func(st *State) error {
				st.Config["weatherKey"] = "updated"
				if kind == "business-failure" {
					return sentinel
				}
				return nil
			})
			if kind == "success" && err != nil {
				t.Fatal(err)
			}
			if kind != "success" && err == nil {
				t.Fatal("expected rollback")
			}
			if err = mock.ExpectationsWereMet(); err != nil {
				t.Fatal(err)
			}
		})
	}
}
