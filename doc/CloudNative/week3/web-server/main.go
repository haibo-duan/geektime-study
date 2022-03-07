package main

import (
	"fmt"
	"net/http"
	"os"
	"strings"
)

func main() {
	http.HandleFunc("/hello", handler_hello)
	http.HandleFunc("/healthz", handler_healthz)
	http.ListenAndServe("0.0.0.0:8360", nil)
}

func handler_healthz(w http.ResponseWriter, r *http.Request) {
	w.WriteHeader(http.StatusOK)
	w.Write([]byte("OK"))
}

func handler_hello(w http.ResponseWriter, r *http.Request) {
	fmt.Println("method = ", r.Method)
	fmt.Println("URL = ", r.URL)
	fmt.Println("RemoteAddr = ", r.RemoteAddr)
	fmt.Println("IP = ", strings.Split(r.RemoteAddr, ":")[0])
	fmt.Println("header = ", r.Header)
	fmt.Println("body = ", r.Body)
	fmt.Println(r.RemoteAddr, "连接成功")

	for name, values := range r.Header {
		for _, value := range values {
			fmt.Println(name, value)
			_, exits := w.Header()[name]
			if exits {
				w.Header().Add(name, value)
			} else {
				w.Header().Set(name, value)
			}
		}
	}
	VERSION := os.Getenv("VERSION")
	fmt.Println("VERSION is ：", VERSION)
	w.Header().Set("VERSION", VERSION)
	w.WriteHeader(http.StatusOK)
	w.Write([]byte("hello http server"))
}
