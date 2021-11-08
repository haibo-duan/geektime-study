package com.dhb.kmq.v3.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class KmqConfig {

	@Autowired
	KmqBroker broker;
	
	@Bean
	public Kmq testTopic() {
		return broker.createTopic("kk.test");
	}
	
	@Bean
	public List<Thread> threads(){
		return new ArrayList<>();
	} 

}
