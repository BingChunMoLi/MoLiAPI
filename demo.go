package main

import (
	"fmt"
	"net/http"
)

func main() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Println(w, "Welcome to moliapi!")
	})
	http.HandleFunc("/yiyan/random", yiYan)
	http.HandleFunc("/img/random", img)

	err := http.ListenAndServe(":80", nil)
	if err != nil {
		fmt.Println(err)
		return
	}

}
