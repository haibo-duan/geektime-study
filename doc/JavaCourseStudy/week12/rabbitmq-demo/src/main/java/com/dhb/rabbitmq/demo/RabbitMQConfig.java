package com.dhb.rabbitmq.demo;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
	
	@Autowired
	private MQProperties mqProperties;
	
	@Bean
	public Queue queue() {
		boolean durable = true;
		boolean exclusive = false;
		boolean autoDelete = false;
		return new Queue(mqProperties.getQueue(),durable,exclusive,autoDelete);
	}


	@Bean
	public DirectExchange defaultExchange() {
		boolean durable = true;
		boolean autoDelete = false;
		return new DirectExchange(mqProperties.getDefaultExchange(), durable, autoDelete);
	}

	@Bean
	public Binding binding() {
		return BindingBuilder.bind(queue())
				.to(defaultExchange())
				.with(mqProperties.getRouteKey());
	}
}
