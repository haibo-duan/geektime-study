package com.dhb.activemq.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Queue;
import javax.jms.Topic;


@Component
@Slf4j
public class Producer {

	@Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;

	@Autowired
	private Queue queue;

	@Autowired
	private Topic topic;

	public void sendMsgToQueue(String msg) {
		log.info("发送消息内容 :" + msg);
		this.jmsMessagingTemplate.convertAndSend(this.queue, msg);
	}

	public void sendMsgToTopic(String msg) {
		log.info("发送消息内容 :" + msg);
		this.jmsMessagingTemplate.convertAndSend(this.topic, msg);
	}
}
