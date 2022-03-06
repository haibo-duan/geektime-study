FROM golang:1.17 AS build

WORKDIR /web-server/

COPY . .
ENV CGO_ENABLED=0
ENV GO111MODULE=on
ENV GOPROXY=https://goproxy.cn,direct
RUN GOOS=linux go build -installsuffix cgo -o web-server server.go

FROM busybox
COPY --from=build /web-server/web-server /web-server/web-server
EXPOSE 8360
ENV ENV local
WORKDIR /web-server/
ENTRYPOINT ["./web-server"]

