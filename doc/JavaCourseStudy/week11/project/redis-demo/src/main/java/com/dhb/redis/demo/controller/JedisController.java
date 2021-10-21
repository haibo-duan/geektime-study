package com.dhb.redis.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
@Slf4j
public class JedisController {

	@Autowired
	JedisPool jedisPool;

	@RequestMapping("/testJedis")
	public String testJedis() {
		// C1.最简单demo
		Jedis jedis = jedisPool.getResource();
		log.info("jedis info: {}",jedis.info());
		jedis.set("uptime", new Long(System.currentTimeMillis()).toString());
		log.info("uptime is: {}",jedis.get("uptime"));
		jedis.set("teacher", "Cuijing");
		log.info("teacher is: {}",jedis.get("teacher"));
		return "OK";
	}
}
