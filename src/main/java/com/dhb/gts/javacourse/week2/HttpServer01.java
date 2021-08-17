package com.dhb.gts.javacourse.week2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

//单线程的socket程序
public class HttpServer01 {

	public static void main(String[] args) throws IOException {
		final ServerSocket serverSocket = new ServerSocket(8801);
		while (true) {
			try {
				final Socket socket = serverSocket.accept();
				service(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void service(Socket socket) {
		try {
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
			System.out.println("HttpServer01 收到请求。。");
			
			TimeUnit.MILLISECONDS.sleep(1);
			printWriter.write("HTTP/1.1 200 OK");
			printWriter.println();
			printWriter.write("Content-Type: text/html; charset=utf-8");
			printWriter.println();
			String body = "hello,nio1 this is Server01";
			System.out.println("HttpServer01 回写数据："+body);
			printWriter.println("Content-length:" + body.getBytes().length);
			printWriter.println();
			printWriter.write(body);
			printWriter.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
