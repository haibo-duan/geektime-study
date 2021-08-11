package com.dhb.gts.javacourse.week2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HttpServer03 {

	public static void main(String[] args) throws IOException{
		ExecutorService executorService  = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors()*4);
		final ServerSocket serverSocket = new ServerSocket(8803);
		while (true) {
			try {
				final Socket socket = serverSocket.accept();
				executorService.execute(() -> service(socket));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void service(Socket socket) {
		try {
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
			TimeUnit.MILLISECONDS.sleep(1);
			printWriter.write("Http/1.1 200 OK");
			printWriter.write("Content-Type:text/html;charset=utf-8");
			String body = "hello,nio1";
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
