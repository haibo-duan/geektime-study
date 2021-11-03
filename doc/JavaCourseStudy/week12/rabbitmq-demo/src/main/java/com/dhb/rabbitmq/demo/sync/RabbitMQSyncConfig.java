package com.dhb.rabbitmq.demo.sync;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Configuration
public class RabbitMQSyncConfig {

	// 声明业务Exchange
	@Bean
	public DirectExchange sendExchange(){
		return new DirectExchange("sendExchange");
	}

	// 声明业务队列A
	@Bean
	public Queue sendQueue(){
		Map<String, Object> args = new HashMap<>();
		//x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
		args.put("x-dead-letter-exchange", "deadSendExchange");
		//x-dead-letter-routing-key  这里声明当前队列的死信路由key
		args.put("x-dead-letter-routing-key", "dle.send");
		// 超时时间是5s
		args.put("x-message-ttl", 5000);  
		return new Queue("sendQueue",true,false,false,args);
	}

	// 声明业务队列A绑定关系
	@Bean
	public Binding sendBinding(Queue sendQueue, DirectExchange sendExchange){
		return BindingBuilder.bind(sendQueue).to(sendExchange).with("sync.send");
	}


	// 声明业务Exchange
	@Bean
	public DirectExchange receiveExchange(){
		return new DirectExchange("receiveExchange");
	}

	// 创建延时队列1
	@Bean
	public Queue receiveQueue(){
		Map<String, Object> args = new HashMap<>();
		return new Queue("receiveQueue",true,false,false,args);
	}
	
	@Bean
	public Binding receiveBinding(Queue receiveQueue, DirectExchange receiveExchange){
		return BindingBuilder.bind(receiveQueue).to(receiveExchange).with("sync.receive");
	}
	
	@Bean
	public Map<String, CountDownLatch> latchMap() {
		Map<String,CountDownLatch> map = new HashMap<>();
		return map;
	}

	@Bean
	public Map<String, String> resultMap() {
		Map<String,String> map = new HashMap<>();
		return map;
	}

}
