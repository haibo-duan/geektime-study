package com.dhb.kafka.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class KafkaConsumer {

	// 消费监听
	@KafkaListener(topics = {"topic01"})
	public void onMessage1(List<ConsumerRecord<?, ?>> records){
		// 消费的哪个topic、partition的消息,打印出消息内容
		for(ConsumerRecord record : records) {
			log.info("简单消费：" + record.topic() + "-" + record.partition() + "-" + record.value());
		}
	}
}
