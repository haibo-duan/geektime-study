package com.dhb.gts.javacourse.week3;

import com.dhb.gts.javacourse.week3.inbound.HttpInboundServer;

import java.util.Arrays;

public class NettyServerApplication {

	public final static String GATEWAY_NAME = "NIOGateway";
	public final static String GATEWAY_VERSION = "3.0.0";

	public static void main(String[] args) {

		//代理服务端port
		String proxyPort = System.getProperty("proxyPort","8888");
		String proxyServers = System.getProperty("proxyServers","http://localhost:8801,http://localhost:8802");
		
		int port = Integer.parseInt(proxyPort);
		
		System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" starting...");
		HttpInboundServer server = new HttpInboundServer(port, Arrays.asList(proxyServers));
		System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" started at http://127.0.0.1:" + port + " for server:" + server.toString());

		try {
			server.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
