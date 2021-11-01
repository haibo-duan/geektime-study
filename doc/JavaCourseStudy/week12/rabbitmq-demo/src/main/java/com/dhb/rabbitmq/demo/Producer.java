package com.dhb.rabbitmq.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Producer {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private MQProperties mqProperties;

	public void sendMessage(String msg) {
		rabbitTemplate.convertAndSend(mqProperties.getDefaultExchange(),
				mqProperties.getRouteKey(), msg);
	}
}
