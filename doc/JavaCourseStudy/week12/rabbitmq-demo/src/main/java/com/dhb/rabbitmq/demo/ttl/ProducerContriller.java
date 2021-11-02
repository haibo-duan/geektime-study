package com.dhb.rabbitmq.demo.ttl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Slf4j
public class ProducerContriller {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@RequestMapping("/ttl")
	@ResponseBody
	public String test(String msg) {
		log.info("p:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		if ("5".equals(msg)) { // 添加到5s队列
			rabbitTemplate.convertAndSend("ttl-Exchange", "emp.add", msg);
		} else if ("20".equals(msg)) { // 添加到20s队列中
			rabbitTemplate.convertAndSend("ttl-Exchange", "user.add", msg);
		}
		return "success";
	}


	@RequestMapping("/send")
	@ResponseBody
	public String send(String msg){
		log.info("reeive msg is: [{}].",  msg);
		rabbitTemplate.convertAndSend("businessExchange","emp.add",msg);
		return "success";
	}


}
