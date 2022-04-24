package main

import (
	"github.com/prometheus/common/log"
	"math/rand"
	"net/http"
	"os"
	"strings"
	"time"
	"web-server/metrics"

	"github.com/prometheus/client_golang/prometheus/promhttp"
)

func main() {
	log.Info("Starting http server...")
	metrics.Register()
	http.HandleFunc("/hello", handler_hello)
	http.HandleFunc("/healthz", handler_healthz)
	http.Handle("/metrics", promhttp.Handler())
	http.ListenAndServe("0.0.0.0:8360", nil)
}

func handler_healthz(w http.ResponseWriter, r *http.Request) {
	w.WriteHeader(http.StatusOK)
	w.Write([]byte("OK"))
}

func randInt(min, max int) int {
	rand.Seed(time.Now().Unix())
	return rand.Intn(max-min) + min
}

func handler_hello(w http.ResponseWriter, r *http.Request) {
	log.Info("entering hello handler ... ")
	timer := metrics.NewTimer()
	defer timer.ObserveTotal()

	log.Info("method = ", r.Method)
	log.Info("URL = ", r.URL)
	log.Info("RemoteAddr = ", r.RemoteAddr)
	log.Info("IP = ", strings.Split(r.RemoteAddr, ":")[0])
	log.Info("header = ", r.Header)
	log.Info("body = ", r.Body)
	log.Info(r.RemoteAddr, "连接成功")

	//增加随机延时 0 - 2 秒
	delay := randInt(0, 2000)
	time.Sleep(time.Millisecond * time.Duration(delay))
	log.Infof("Respond in %d ms", delay)
	for name, values := range r.Header {
		for _, value := range values {
			log.Info(name, value)
			_, exits := w.Header()[name]
			if exits {
				w.Header().Add(name, value)
			} else {
				w.Header().Set(name, value)
			}
		}
	}
	VERSION := os.Getenv("VERSION")
	log.Info("VERSION is ：", VERSION)
	w.Header().Set("VERSION", VERSION)
	w.WriteHeader(http.StatusOK)
	w.Write([]byte("hello http server"))
}
