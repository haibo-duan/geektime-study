package com.dhb.lettuce.demo.conf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Configuration
@Slf4j
public class RedisConfig {

	@Value("${spring.redis.database}")
	private int database;

	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.password}")
	private String password;

	@Value("${spring.redis.port}")
	private int port;

	@Value("${spring.redis.timeout}")
	private long timeout;

	@Value("${spring.redis.lettuce.shutdown-timeout}")
	private long shutDownTimeout;

	@Value("${spring.redis.lettuce.pool.max-idle}")
	private int maxIdle;

	@Value("${spring.redis.lettuce.pool.min-idle}")
	private int minIdle;

	@Value("${spring.redis.lettuce.pool.max-active}")
	private int maxActive;

	@Value("${spring.redis.lettuce.pool.max-wait}")
	private long maxWait;

	Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

	@Bean
	public LettuceConnectionFactory lettuceConnectionFactory() {
		log.info("lettuceConnectionFactory begin .... ");
		GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
		genericObjectPoolConfig.setMaxIdle(maxIdle);
		genericObjectPoolConfig.setMinIdle(minIdle);
		genericObjectPoolConfig.setMaxTotal(maxActive);
		genericObjectPoolConfig.setMaxWaitMillis(maxWait);
		genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(100);
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setDatabase(database);
		redisStandaloneConfiguration.setHostName(host);
		redisStandaloneConfiguration.setPort(port);
		redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
		LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
				.commandTimeout(Duration.ofMillis(timeout))
				.shutdownTimeout(Duration.ofMillis(shutDownTimeout))
				.poolConfig(genericObjectPoolConfig)
				.build();

		LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
//        factory.setShareNativeConnection(true);
//        factory.setValidateConnection(false);
		log.info("lettuceConnectionFactory created .... ");
		return factory;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(lettuceConnectionFactory);
		//??????Jackson2JsonRedisSerializer???????????????JdkSerializationRedisSerializer???????????????????????????redis???value???
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
				ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
		jackson2JsonRedisSerializer.setObjectMapper(mapper);
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		//key??????String??????????????????
		template.setKeySerializer(stringRedisSerializer);
		// hash???key?????????String??????????????????
		template.setHashKeySerializer(stringRedisSerializer);
		// value?????????????????????jackson
		template.setValueSerializer(jackson2JsonRedisSerializer);
		// hash???value?????????????????????jackson
		template.setHashValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}

	@Bean("redisCacheManager")
	@Primary
	public CacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
		RedisSerializer<String> redisSerializer = new StringRedisSerializer();

		log.info("cacheManager begin .... ");
		//???????????????????????????????????????
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
				ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
		jackson2JsonRedisSerializer.setObjectMapper(mapper);

		// ??????1 ,
		RedisCacheConfiguration config1 = RedisCacheConfiguration.defaultCacheConfig()
				//??????????????????
				.entryTtl(Duration.ofSeconds(30))
				//key???????????????
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
				//value???????????????
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
				//???????????????null???
				.disableCachingNullValues();
		//??????2 ,
		RedisCacheConfiguration config2 = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(1000))
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
				.disableCachingNullValues();


		//????????????????????????????????????set??????
		Set<String> cacheNames = new HashSet<>();
		cacheNames.add("my-redis-cache1");
		cacheNames.add("my-redis-cache2");


		//??????????????????????????????????????????
		Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>(3);
		configurationMap.put("my-redis-cache1", config1);
		configurationMap.put("my-redis-cache2", config2);

		return RedisCacheManager.builder(lettuceConnectionFactory)
				//??????????????????
				.cacheDefaults(config1)
				//?????????????????????
				.initialCacheNames(cacheNames)
				//?????????????????????
				.withInitialCacheConfigurations(configurationMap).build();


	}
}
