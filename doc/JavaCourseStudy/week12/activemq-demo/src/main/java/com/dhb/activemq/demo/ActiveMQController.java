package com.dhb.activemq.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ActiveMQController {
	
	@Autowired
	Producer producer;

	@RequestMapping("/sendQueue")
	@ResponseBody
	public String sendQueue(String msg) {
		producer.sendMsgToQueue(msg);
		return "success";
	}

	@RequestMapping("/sendTopic")
	@ResponseBody
	public String sendTopic(String msg) {
		producer.sendMsgToTopic(msg);
		return "success";
	}
}
