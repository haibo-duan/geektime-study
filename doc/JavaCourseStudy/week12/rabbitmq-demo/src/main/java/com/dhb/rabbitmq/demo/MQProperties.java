package com.dhb.rabbitmq.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rabbitmq-demo")
@Data
public class MQProperties {

	private String defaultExchange;
	private String routeKey;
	private String queue;
}
