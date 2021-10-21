package com.dhb.redis.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LettuceController {
	@Autowired
	@Qualifier("lettuceTemplate")
	private RedisTemplate redisTemplate;

	@RequestMapping("/testLettuce")
	public String testLettuce() {
		// C1.最简单demo
		log.info("lettuce info: {}", redisTemplate.getRequiredConnectionFactory().getConnection().info());
		redisTemplate.boundValueOps("uptime").set(new Long(System.currentTimeMillis()).toString());
		log.info("uptime is: {}", redisTemplate.boundValueOps("uptime").get());

		BoundValueOperations teacherKey = redisTemplate.boundValueOps("teacher");
		teacherKey.set("Cuijing");
		log.info("teacher is: {}", teacherKey.get());
		return "OK";
	}
}
