package com.dhb.kmq.v3.demo;

import com.alibaba.fastjson.JSONObject;
import com.dhb.kmq.v2.core.KmqMessage;
import com.dhb.kmq.v2.demo.Order;

public class ProducerDemo {

	public static final String PRODUCER_URL = "http://127.0.0.1:8080/produceMsg";
	
	public static final String TOPIC = "kk.test";
	
	public static void send(String topic,KmqMessage message) {
		StringBuffer sb = new StringBuffer();
		sb.append("topic='");
		sb.append(topic);
		sb.append("'&");
		sb.append("msg='");
		sb.append(JSONObject.toJSONString(message));
		sb.append("'");
		HttpClientUtils.httpPost(PRODUCER_URL,sb.toString());
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println("点击任何键，发送一条消息；点击q或e，退出程序。");
		while (true) {
			char c = (char) System.in.read();
			if (c > 20) {
				System.out.println(c);
				ProducerDemo.send(TOPIC, new KmqMessage(null, new Order(100000L + c, System.currentTimeMillis(), "USD2CNY", 6.52d)));
			}
			if (c == 'q' || c == 'e') {
				break;
			}
		}
	}
}
