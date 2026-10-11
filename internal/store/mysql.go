package store

import (
	"context"
	"database/sql"
	"encoding/json"
	"time"

	_ "github.com/go-sql-driver/mysql"
)

type MySQL struct{ db *sql.DB }

func OpenMySQL(dsn string) (*MySQL, error) {
	db, err := sql.Open("mysql", dsn)
	if err != nil {
		return nil, err
	}
	db.SetConnMaxLifetime(3 * time.Minute)
	db.SetMaxOpenConns(10)
	db.SetMaxIdleConns(5)
	if err = initializeMySQL(db); err != nil {
		db.Close()
		return nil, err
	}
	return &MySQL{db: db}, nil
}

func initializeMySQL(db *sql.DB) error {
	ctx, cancel := context.WithTimeout(context.Background(), 15*time.Second)
	defer cancel()
	if _, err := db.ExecContext(ctx, `CREATE TABLE IF NOT EXISTS moli_go_state (
        id TINYINT NOT NULL PRIMARY KEY, payload LONGBLOB NOT NULL) ENGINE=InnoDB`); err != nil {
		return err
	}
	initial, _ := json.Marshal(NewState())
	if _, err := db.ExecContext(ctx, "INSERT IGNORE INTO moli_go_state (id, payload) VALUES (1, ?)", initial); err != nil {
		return err
	}
	return nil
}

func (m *MySQL) View(fn func(*State) error) error {
	ctx, cancel := context.WithTimeout(context.Background(), 15*time.Second)
	defer cancel()
	var data []byte
	if err := m.db.QueryRowContext(ctx, "SELECT payload FROM moli_go_state WHERE id = 1").Scan(&data); err != nil {
		return err
	}
	s, err := Decode(data)
	if err != nil {
		return err
	}
	return fn(s)
}

func (m *MySQL) Update(fn func(*State) error) error {
	ctx, cancel := context.WithTimeout(context.Background(), 15*time.Second)
	defer cancel()
	tx, err := m.db.BeginTx(ctx, nil)
	if err != nil {
		return err
	}
	defer tx.Rollback()
	var data []byte
	if err = tx.QueryRowContext(ctx, "SELECT payload FROM moli_go_state WHERE id = 1 FOR UPDATE").Scan(&data); err != nil {
		return err
	}
	s, err := Decode(data)
	if err != nil {
		return err
	}
	if err = fn(s); err != nil {
		return err
	}
	data, err = json.Marshal(s)
	if err != nil {
		return err
	}
	if _, err = tx.ExecContext(ctx, "UPDATE moli_go_state SET payload = ? WHERE id = 1", data); err != nil {
		return err
	}
	return tx.Commit()
}

func (m *MySQL) Close() error { return m.db.Close() }
