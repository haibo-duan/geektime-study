package com.dhb.redis.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class RedisTemplateController {

	@Autowired
	private RedisTemplate redisTemplate;

	@RequestMapping("/testRedisTemplate")
	public String testRedisTemplate() {
		// C1.最简单demo
		log.info("jedis info: {}", redisTemplate.getRequiredConnectionFactory().getConnection().info());
		redisTemplate.boundValueOps("uptime").set(new Long(System.currentTimeMillis()).toString());
		log.info("uptime is: {}", redisTemplate.boundValueOps("uptime").get());

		BoundValueOperations teacherKey = redisTemplate.boundValueOps("teacher");
		teacherKey.set("Cuijing");
		log.info("teacher is: {}", teacherKey.get());
		return "OK";
	}
}
