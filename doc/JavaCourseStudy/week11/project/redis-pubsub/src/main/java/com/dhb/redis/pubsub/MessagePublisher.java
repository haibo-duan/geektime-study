package com.dhb.redis.pubsub;

public interface MessagePublisher {
	void publish(String message);
}
