package com.dhb.redis.demo.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@Slf4j
public class RedisConfig {

	@Value("${spring.redis.host}")
	private String host;
	
	@Value("${spring.redis.port}")
	private int port;

	@Value("${spring.redis.timeout}")
	private int timeout;

	@Value("${spring.redis.jedis.pool.max-idle}")
	private int maxIdle;

	@Value("${spring.redis.jedis.pool.min-idle}")
	private int minIdle;
	
	@Value("${spring.redis.jedis.pool.max-wait}")
	private long maxWaitMillis;

	@Value("${spring.redis.jedis.pool.max-active}")
	private int maxActive;

	@Value("${spring.redis.password}")
	private String password;
	
	@Bean
	public JedisPool jedisPoolFactory() throws Exception {
		log.info("jedis pool begin ...");
		log.info("host is {}, port is {} timeout is {}.",host,port,timeout);
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMinIdle(minIdle);
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		jedisPoolConfig.setJmxEnabled(true);
		JedisPool pool = new JedisPool(jedisPoolConfig,host,port,timeout);
		return pool;
	}
}
