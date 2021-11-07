package com.dhb.kmq.v3.core;

import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Kmq {
	public Kmq(String topic, int capacity) {
		this.topic = topic;
		this.capacity = capacity;
		this.queue = new KmqMessage[capacity];
	}

	private String topic;

	private int capacity;

	private KmqMessage[] queue;
	
	private Map<String,AtomicInteger> clientIndex = new HashMap<>();
	
	private AtomicInteger size = new AtomicInteger(0);
	
	public boolean send(KmqMessage message) {
		int index = size.getAndIncrement();
		if(index < capacity){
			queue[index] = message;
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 考虑到v1中的blockingQueue获取不到内容会一直阻塞，此处用sleep循环暂时替代
	 * @param index
	 * @return
	 */
	@SneakyThrows
	public KmqMessage poll(int index) {
		while (true){
			if(size.get() > index) {
				return queue[index];
			}else {
				TimeUnit.MILLISECONDS.sleep(500);
			}
		}
	}

	@SneakyThrows
	public KmqMessage poll(int index, long timeout) {
		long start = System.currentTimeMillis();
		while (true) {
			if (size.get() > index) {
				return queue[index];
			} else {
				TimeUnit.MILLISECONDS.sleep(500);
				if(System.currentTimeMillis() - start >= timeout) {
					return null;
				}
			}
		}
	}

}
