package main

import (
	"bingchunmoli.com/moliapi/api"
	"fmt"
	"net/http"
)

func main() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Println("Welcome to moliapi!")
	})
	http.HandleFunc("/yiyan/random", api.YiYan)
	http.HandleFunc("/img/random", api.Img)

	err := http.ListenAndServe(":80", nil)
	if err != nil {
		fmt.Println(err)
		return
	}
	print("启动成功")

}
