package com.dhb.rabbitmq.demo.ttl;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfigTTL {

	// 声明业务Exchange
	@Bean
	public TopicExchange businessExchange(){
		return new TopicExchange("businessExchange");
	}

	// 声明业务队列A
	@Bean
	public Queue businessQueue(){
		Map<String, Object> args = new HashMap<>();
//       x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
		args.put("x-dead-letter-exchange", "deadLetterExchange");
//       x-dead-letter-routing-key  这里声明当前队列的死信路由key
		args.put("x-dead-letter-routing-key", "dle.err");

		return new Queue("businessQueue",true,false,false,args);
	}

	// 声明业务队列A绑定关系
	@Bean
	public Binding businessBinding(Queue businessQueue, TopicExchange businessExchange){
		return BindingBuilder.bind(businessQueue).to(businessExchange).with("emp.*");
	}
	

	// 声明业务Exchange
	@Bean
	public TopicExchange ttlExchange(){
		return new TopicExchange("ttl-Exchange");
	}

	// 创建延时队列1
	@Bean
	public Queue businessQueue1(){
		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", "deadLetterExchange");
		args.put("x-dead-letter-routing-key", "dle.err");
		args.put("x-message-ttl", 5000);   // 超时时间是5s
		return new Queue("5-queue",true,false,false,args);
	}

	// 创建延时队列2
	@Bean
	public Queue businessQueue2(){
		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", "deadLetterExchange");
		args.put("x-dead-letter-routing-key", "dle.err");
		args.put("x-message-ttl", 20000); //  // 超时时间是20s
		return new Queue("20-queue",true,false,false,args);
	}

	// 延时队列绑定关系
	@Bean
	public Binding businessBinding1(Queue businessQueue1, TopicExchange ttlExchange){
		return BindingBuilder.bind(businessQueue1).to(ttlExchange).with("emp.*");
	}

	// 延时队列绑定
	@Bean
	public Binding businessBinding2(Queue businessQueue2, TopicExchange ttlExchange){
		return BindingBuilder.bind(businessQueue2).to(ttlExchange).with("user.*");
	}


	//声明死信Exchange
	@Bean
	public TopicExchange deadLetterExchange(){
		return new TopicExchange("deadLetterExchange");
	}

	// 声明死信队列
	@Bean
	public Queue deadLetterQueue(){
		return new Queue("dle-queue",true,false,false,null);
	}

	// 死信队列绑定交换机
	@Bean
	public Binding deadLetterQueueBinding(Queue deadLetterQueue, TopicExchange deadLetterExchange){
		return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("dle.*");
	}
}
