package com.dhb.redis.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class RedissonController {

	@Autowired
	RedissonClient redissonClient;

	@RequestMapping("/testRedisson")
	public String testRedisson() {
		// C1.最简单demo
		RBucket<String> uptime = redissonClient.getBucket("uptime");
		uptime.set(new Long(System.currentTimeMillis()).toString());
		log.info("uptime is: {}", uptime.get());

		RBucket<String> teacher = redissonClient.getBucket("teacher");
		teacher.set("Cuijing");
		log.info("teacher is: {}", teacher.get());
		return "OK";
	}

}
