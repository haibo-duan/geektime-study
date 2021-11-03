package com.dhb.rabbitmq.demo.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@RestController
@Slf4j
public class SyncController {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	Map<String,CountDownLatch> latchMap;
	
	@Autowired
	Map<String,String> resultMap;
	
	@RequestMapping("/testSync")
	@ResponseBody
	public String testSync(String msg) {

		CountDownLatch latch = new CountDownLatch(1);

		String uuid = UUID.randomUUID().toString().replaceAll("-","");
		String result = "";
		try {
			log.info("发送消息到队列 sendQueue: uuid is [{}], msg is [{}].",uuid,msg);
			rabbitTemplate.convertAndSend("sendExchange", "sync.send", uuid+"@"+msg);
			latchMap.put(uuid,latch);
			latch.await();
			result = resultMap.get(uuid);
			resultMap.remove(uuid);
			log.info("接收到消息:{}",result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
