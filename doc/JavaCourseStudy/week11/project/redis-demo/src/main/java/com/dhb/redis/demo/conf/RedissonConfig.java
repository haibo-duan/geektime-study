package com.dhb.redis.demo.conf;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RedissonConfig {

	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.password}")
	private String password;

	@Value("${spring.redis.port}")
	private int port;

	@Value("${spring.redis.timeout}")
	private int timeout;
	
	@Bean
	public RedissonClient getRedisson() {
		log.info("RedissonClient begin ...");
		Config config = new Config();
		config.useSingleServer()
				.setAddress("redis://"+host+":"+port);
		config.setCodec(new JsonJacksonCodec());
		return Redisson.create(config);
	}
	
}
