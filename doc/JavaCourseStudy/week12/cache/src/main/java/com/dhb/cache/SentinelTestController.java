package com.dhb.cache;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SentinelTestController {
	
	@Autowired
	RedisTemplate redisTemplate;

	@RequestMapping(value="/testSentinel")
	@ResponseBody
	public String testSentinel() {
		String info =  redisTemplate.getRequiredConnectionFactory().getConnection().info().toString();
		log.info("jedis info: {}",info);
		return info;
	}
	
}
