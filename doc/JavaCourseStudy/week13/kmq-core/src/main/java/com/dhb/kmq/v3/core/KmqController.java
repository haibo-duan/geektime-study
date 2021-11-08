package com.dhb.kmq.v3.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.locks.LockSupport;

@RestController
@Slf4j
public class KmqController {

	
	@Autowired
	Kmq testTopic;
	
	@Autowired
	KmqBroker broker;
	
	@Autowired
	List<Thread> threads;
	
	@RequestMapping("/produceMsg")
	@ResponseBody
	public String produceMsg(String topic,String msg) {
		log.info("produceMsg : topic is {}, msg is {}.",topic,msg);
		if(null == testTopic) {
			return "fail";
		}
		KmqMessage message = new KmqMessage(null,msg);
		testTopic.send(message);
		for(Thread t:threads) {
			LockSupport.unpark(t);
		}
		return "success";
	}
	
	@RequestMapping("/consumerMsg")
	@ResponseBody
	public String consumerMsg(String topic,int offset) {
		log.info("consumerMsg : topic is {}, offset is {}.",topic,offset);
		String msg = "";
		Kmq kmq = broker.findKmq(topic);
		if(null == testTopic) {
			log.info("####  topic is not exists!");
			return "fail";
		}
		threads.add(Thread.currentThread());
		KmqMessage message = testTopic.poll(offset);
		log.info("consumer msg is {}.",message);
		threads.remove(Thread.currentThread());
		return message.toString();
	}
}
