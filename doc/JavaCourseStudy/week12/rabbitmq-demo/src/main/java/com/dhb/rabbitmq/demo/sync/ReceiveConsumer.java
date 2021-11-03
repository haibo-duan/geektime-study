package com.dhb.rabbitmq.demo.sync;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Service
public class ReceiveConsumer {

	@Autowired
	Map<String, CountDownLatch> latchMap;

	@Autowired
	Map<String,String> resultMap;
	
	// 监听业务队列
	@RabbitListener(queues = "receiveQueue")
	public void businessQueue(String msg, Channel channel, Message message) throws IOException {
		log.info("receiveQueue consumer : msg is [{}].",msg);
		String uuid = msg.split("@")[0];
		resultMap.put(uuid,msg);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		CountDownLatch latch = latchMap.get(uuid);
		log.info("receiveQueue consumer countDownLatch call countdown and remove.");
		latch.countDown();
		latchMap.remove(uuid);
	}
}
