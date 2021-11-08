package com.dhb.kmq.v3.demo;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ConsumerDemo {
	
	public static final String CONSUMER_URL = "http://127.0.0.1:8080/consumerMsg";

	public static final String TOPIC = "kk.test";
	
	private static int offset = 0;
	
	public static String poll() {
		StringBuffer sb = new StringBuffer();
		sb.append("topic=");
		sb.append(TOPIC);
		sb.append("&");
		sb.append("offset=");
		sb.append(offset);
		return HttpClientUtils.httpPost(CONSUMER_URL,sb.toString());
	}
	
	
	public static void main(String[] args) throws Exception{

		new Thread(() -> {
			while (true) {
				String result = ConsumerDemo.poll();
				if (null != result) {
					System.out.println("consumer msg:" +result);
					offset+=1;
				}
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
		Thread.sleep(500);
		
	}
}
