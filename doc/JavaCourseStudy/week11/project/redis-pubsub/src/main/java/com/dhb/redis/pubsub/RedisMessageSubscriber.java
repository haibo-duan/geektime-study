package com.dhb.redis.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RedisMessageSubscriber implements MessageListener {

	public static List<String> messageList = new ArrayList<>();
	
	@Override
	public void onMessage(Message message, byte[] pattern) {
		messageList.add(message.toString());
		log.info("订阅方接收到了消息==>{}", message.toString());
	}
}
