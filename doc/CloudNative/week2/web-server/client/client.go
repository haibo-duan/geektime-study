package main

import (
	"fmt"
	"io/ioutil"
	"net/http"
)

func main() {
	resp, err := http.Get("http://127.0.0.1:8080/hello")
	if err != nil {
		fmt.Println("Get error : ", err)
		return
	}

	defer resp.Body.Close()

	fmt.Println("Status = ", resp.Status)
	fmt.Println("StatusCode = ", resp.StatusCode)
	fmt.Println("Header = ", resp.Header)
	fmt.Println("Body = ", resp.Body)

	content, err := ioutil.ReadAll(resp.Body)
	fmt.Println("result = ", string(content))
}
