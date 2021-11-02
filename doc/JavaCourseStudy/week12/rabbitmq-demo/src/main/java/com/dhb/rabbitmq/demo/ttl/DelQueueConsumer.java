package com.dhb.rabbitmq.demo.ttl;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class DelQueueConsumer {

	// 监听业务队列
	@RabbitListener(queues = "businessQueue")
	public void businessQueue(String msg, Channel channel, Message message) throws IOException {
		if ("error".equals(msg)) {
			log.info("businessQueue 业务消费者出现问题:{}", msg);
			try {
				throw new RuntimeException();
			}catch (Exception e){
				// 无法消费消息，nack basicNack三个参数：
				//deliveryTag:该消息的index
				//multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
				//requeue：是否重新入队列   
				channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
			}
		} else {
			log.info("businessQueue 正常消费消息:{}",msg);
			// 正常消费了消息，手动ack basicAck两个参数：
			// deliveryTag:该消息的index
			// multiple：是否批量.true:将一次性ack所有小于deliveryTag的消息。
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		}
	}

	@RabbitListener(queues = "dle-queue")
	public void dleQueue(String msg, Channel channel, Message message) throws IOException {
		log.info("dle-queue: msg is [{}], date is[{}].",msg, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}
}
