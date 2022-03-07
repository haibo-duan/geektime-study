
```
[www@hecs-205828 web-server]$ cd /home/www/web-server
[www@hecs-205828 web-server]$ pwd
/home/www/web-server
[www@hecs-205828 web-server]$ sudo docker build . -t httpserver:0.0.1
[sudo] password for www:
Sending build context to Docker daemon  6.131MB
Step 1/13 : FROM golang:1.17 AS build
---> 0659a535a734
Step 2/13 : WORKDIR /web-server/
---> Using cache
---> 0492ee3c2bb0
Step 3/13 : COPY . .
---> Using cache
---> 335ab3a46654
Step 4/13 : ENV CGO_ENABLED=0
---> Using cache
---> aab0479d8c15
Step 5/13 : ENV GO111MODULE=on
---> Using cache
---> b5f583e281e1
Step 6/13 : ENV GOPROXY=https://goproxy.cn,direct
---> Using cache
---> 16ed28706810
Step 7/13 : RUN GOOS=linux go build -installsuffix cgo -o web-server server.go
---> Using cache
---> b75a8562a8bf
Step 8/13 : FROM busybox
---> 829374d342ae
Step 9/13 : COPY --from=build /web-server/web-server /web-server/web-server
---> dfb06b31677e
Step 10/13 : EXPOSE 8360
---> Running in 80f41c8a08f8
Removing intermediate container 80f41c8a08f8
---> 22bd086b2b66
Step 11/13 : ENV ENV local
---> Running in 16a23368dc84
Removing intermediate container 16a23368dc84
---> 1b77cb41e48c
Step 12/13 : WORKDIR /web-server/
---> Running in 0f41cfabc58f
Removing intermediate container 0f41cfabc58f
---> 9b68866a1ed4
Step 13/13 : ENTRYPOINT ["./web-server"]
---> Running in e4776edb0921
Removing intermediate container e4776edb0921
---> a7978d3fda20
Successfully built a7978d3fda20
Successfully tagged httpserver:0.0.1
```

运行docker
```
[www@hecs-205828 web-server]$ sudo docker run -d httpserver:0.0.1
5effa32fc2e03c0f15232365a7f0b1871e7d1ad57ee558d4174032d174380af4
[www@hecs-205828 web-server]$ sudo docker ps -a
CONTAINER ID   IMAGE              COMMAND          CREATED          STATUS          PORTS      NAMES
5effa32fc2e0   httpserver:0.0.1   "./web-server"   15 seconds ago   Up 14 seconds   8360/tcp   epic_wright
[www@hecs-205828 web-server]$ 
```
