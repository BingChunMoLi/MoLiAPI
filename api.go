package main

import (
	"encoding/json"
	"log"
	"math/rand"
	"net/http"
	"os"
	"path/filepath"
)

type YiYan struct {
	Id         int16  `json:"id,omitempty"`
	Uuid       string `json:"uuid,omitempty"`
	Hitokoto   string `json:"hitokoto,omitempty"`
	Type       string `json:"type,omitempty"`
	From       string `json:"from,omitempty"`
	FromWho    string `json:"from_who,omitempty"`
	Creator    string `json:"creator,omitempty"`
	CreatorUid int16  `json:"creator_uid,omitempty"`
	Reviewer   int16  `json:"reviewer,omitempty"`
	CommitFrom string `json:"commit_from,omitempty"`
	CreatedAt  string `json:"created_at,omitempty"`
	Length     int16  `json:"length,omitempty"`
}

func yiYan(w http.ResponseWriter, r *http.Request) {
	randString := string(rand.Intn(12) + 97)
	file, err := os.ReadFile(filepath.Join("yiyan", randString+".json"))
	if err != nil {
		print(err)
		panic(err)
		return
	}
	var yiYans []YiYan
	err = json.Unmarshal(file, &yiYans)
	if err != nil {
		print(err)
		panic(err)
		return
	}
	randYiYanIndex := rand.Intn(len(yiYans))
	res, err := json.Marshal(yiYans[randYiYanIndex])
	if err != nil {
		print(err)
		panic(err)
		return
	}
	//w.Header().Set("Content-Type", "application/json")
	write, err := w.Write(res)
	if err != nil {
		print(write)
		print(err)
		http.Error(w, "无法编码 JSON 数据", http.StatusInternalServerError)
		return
	}
}

func img(w http.ResponseWriter, r *http.Request) {
	userHomeDir, err := os.UserHomeDir()
	if err != nil {
		print(err)
		return
	}
	prefixPath := userHomeDir + "/.api/img/pc/"
	dir, err := os.ReadDir(prefixPath)
	if err != nil {
		print(err)
		return
	}
	if len(dir) == 0 {
		log.Print("没有图片")
		return
	}
	random := rand.Intn(len(dir))
	http.ServeFile(w, r, prefixPath+dir[random].Name())
}
