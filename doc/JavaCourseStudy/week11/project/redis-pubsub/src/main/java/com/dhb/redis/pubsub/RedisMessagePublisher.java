package com.dhb.redis.pubsub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

public class RedisMessagePublisher implements MessagePublisher{

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private ChannelTopic topic;

	public RedisMessagePublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
		this.redisTemplate = redisTemplate;
		this.topic = topic;
	}

	@Override
	public void publish(String message) {
		redisTemplate.convertAndSend(topic.getTopic(),message);
	}
}
