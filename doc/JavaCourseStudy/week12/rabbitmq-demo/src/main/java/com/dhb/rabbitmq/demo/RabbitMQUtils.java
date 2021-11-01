package com.dhb.rabbitmq.demo;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class RabbitMQUtils {

	public static void askMessage(Channel channel, long tag) {
		askMessage(channel, tag, false);
	}

	public static void askMessage(Channel channel, long tag, boolean multiple) {
		try {
			channel.basicAck(tag, multiple);
		} catch (IOException e) {
			log.error("RabbitMQ，IO异常，异常原因为：{}", e.getMessage());
		}
	}

	public static void rejectMessage(Channel channel, long tag) {
		rejectMessage(channel, tag, false, false);
	}

	public static void rejectAndBackMQ(Channel channel, long tag) {
		rejectMessage(channel, tag, false, true);
	}

	public static void rejectMessage(Channel channel, long tag, boolean multiple, boolean request) {
		try {
			channel.basicNack(tag, multiple, request);
		} catch (IOException e) {
			log.error("RabbitMQ，IO异常，异常原因为：{}", e.getMessage());
		}
	}
}
