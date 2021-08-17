package com.dhb.gts.javacourse.week2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class HttpServer02 {

	public static void main(String[] args) throws IOException{
		final ServerSocket serverSocket = new ServerSocket(8802);
		while (true) {
			try {
				final Socket socket = serverSocket.accept();
				new Thread(() -> {
					service(socket);
				}).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void service(Socket socket) {
		try {
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
			System.out.println("HttpServer02 收到请求。。");
			TimeUnit.MILLISECONDS.sleep(1);
			printWriter.write("HTTP/1.1 200 OK");
			printWriter.println();
			printWriter.write("Content-Type: text/html; charset=utf-8");
			printWriter.println();
			String body = "hello,nio2 This is Server02";
			System.out.println("HttpServer02 回写数据："+body);
			printWriter.println("Content-length:"+body.getBytes().length);
			printWriter.println();
			printWriter.write(body);
			printWriter.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
