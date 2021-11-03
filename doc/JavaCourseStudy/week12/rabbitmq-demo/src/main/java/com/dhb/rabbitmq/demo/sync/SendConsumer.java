package com.dhb.rabbitmq.demo.sync;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class SendConsumer {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	// 监听业务队列
	@RabbitListener(queues = "sendQueue")
	public void businessQueue(String msg, Channel channel, Message message) throws IOException {
		log.info("receiveQueue consumer : msg is [{}].",msg);
		String[] strs = msg.split("@");
		String uuid = strs[0];
		String sourceMsg = strs[1];
		String result = uuid + "@" +"done-success-"+sourceMsg;
		rabbitTemplate.convertAndSend("receiveExchange", "sync.receive", result);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}
	
}
