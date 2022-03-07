需要使用docker将golang的httpserver容器化。在这个过程中遇到了一个弱智问题，特此记录。
# 1.背景
## 1.1 问题描述
问题描述：
docker镜像启动成果之后，通过curl不能访问：
```
[root@hecs-205828 ~]# curl -XGET http://127.0.0.1:8360/hello
curl: (56) Recv failure: Connection reset by peer
```

## 1.2 webserver代码
go文件：main.go
```
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
	http.ListenAndServe("127.0.0.1:8360", nil)
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
```

## 3.Dockerfile文件
Dockerfile文件：
```
FROM golang:1.17 AS build

WORKDIR /web-server/

COPY . .
ENV CGO_ENABLED=0
ENV GO111MODULE=on
ENV GOPROXY=https://goproxy.cn,direct
RUN GOOS=linux go build -installsuffix cgo -o web-server main.go

FROM busybox
COPY --from=build /web-server/web-server /web-server/web-server
EXPOSE 8360
ENV ENV local
WORKDIR /web-server/
ENTRYPOINT ["/web-server/web-server"]
```

# 2.问题分析
发现curl无法访问docker容器中的服务，telnet结果如下：
```
[root@hecs-205828 ~]# telnet 127.0.0.1 8360
Trying 127.0.0.1...
Connected to 127.0.0.1.
Escape character is '^]'.
Connection closed by foreign host.
```

于是，打算采用命令进入docker容器内部查看：

```
[root@hecs-205828 ~]# docker ps -a
CONTAINER ID   IMAGE              COMMAND                  CREATED          STATUS          PORTS                                       NAMES
dfb2b46abd34   httpserver:0.0.1   "/web-server/web-ser…"   27 hours ago     Up 2 hours      0.0.0.0:8360->8360/tcp, :::8360->8360/tcp   relaxed_mccarthy
```
通过docer ps -a 得到容器id为dfb2b46abd34。进入容器：
```
[root@hecs-205828 ~]# docker exec -it dfb2b46abd34 sh
/web-server # ps 
PID   USER     TIME  COMMAND
    1 root      0:00 /web-server/web-server
   38 root      0:00 sh
   94 root      0:00 sh
  101 root      0:00 ps
/web-server # netstat -an |grep 8360
tcp        0      0 127.0.0.1:8360          0.0.0.0:*               LISTEN      
/web-server #
```
可以看到，在容器内部实际上8360端口已经被监听。容器访问应该不存在问题。
容器内部支持wget：
```
/web-server # wget -q -O -  http://127.0.0.1:8360/hello
hello http server
/web-server # 
```
可以看到在docker服务内部运行是正常的。

查看其网络端口衍射：
```
[root@hecs-205828 ~]# docker port  dfb2b46abd34 
8360/tcp -> 0.0.0.0:8360
8360/tcp -> :::8360
```
端口衍射也不存在问题。那么问题究竟出在什么地方呢？
忽然想到，容器内部的nestat监听端口是127.0.0.1，于是瞬间明白了。
在容器内部的监听端口为127.0.0.1的话，那么只能接受容器内部来自127.0.0.1的本地回环访问。来自容器外外部的访问请求将被拒绝。
因此，这个问题的修复原因实际上很简单，只需要将main.go的中监听ip改为0.0.0.0即可。

# 3.解决方案
果断将监听ip改为0.0.0.0：
```
func main() {
	http.HandleFunc("/hello", handler_hello)
	http.HandleFunc("/healthz", handler_healthz)
	http.ListenAndServe("0.0.0.0:8360", nil)
}
```
之后重新制作镜像：
```
sudo docker build . -t httpserver:0.0.2
```
然后启动本地镜像：
```
 sudo docker run -d -p 8260:8230 httpserver:0.0.2
```
启动之后：
```
[root@hecs-205828 ~]# docker ps -a
CONTAINER ID   IMAGE              COMMAND                  CREATED          STATUS          PORTS                                       NAMES
ae5e2bf431c7   httpserver:0.0.2   "/web-server/web-ser…"   50 minutes ago   Up 50 minutes   0.0.0.0:8260->8360/tcp, :::8260->8360/tcp   affectionate_nash
dfb2b46abd34   httpserver:0.0.1   "/web-server/web-ser…"   27 hours ago     Up 2 hours      0.0.0.0:8360->8360/tcp, :::8360->8360/tcp   relaxed_mccarthy
[root@hecs-205828 ~]# 
```
之后再访问新增的容器，结果正常：
```
[root@hecs-205828 ~]# curl -XGET http://127.0.0.1:8260/hello
hello http server
[root@hecs-205828 ~]# 
```
问题解决。

