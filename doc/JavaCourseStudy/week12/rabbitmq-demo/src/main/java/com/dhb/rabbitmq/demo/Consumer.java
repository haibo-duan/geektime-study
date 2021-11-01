package com.dhb.rabbitmq.demo;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Consumer {
	
	@RabbitListener(queues = "${rabbitmq-demo.queue}")
	public void  receive(String payload, Channel channel,
	                     @Header(AmqpHeaders.DELIVERY_TAG) long tag){
		log.info("消费者获取消息内容：{}",payload);
		RabbitMQUtils.askMessage(channel, tag);
	}
}
