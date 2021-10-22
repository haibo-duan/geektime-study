package com.dhb.redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@Slf4j
public class TestLockController {

	@Autowired
	RedisLock redisLock;

	private SnowFlake snowFlake = new SnowFlake(1, 1);
	
	int count = 0;

	@RequestMapping("/testLock")
	@ResponseBody
	public String testLock() throws InterruptedException {
		int clientcount = 30;
		CountDownLatch countDownLatch = new CountDownLatch(clientcount);
		ExecutorService executorService = Executors.newFixedThreadPool(clientcount);
		long start = System.currentTimeMillis();
		for(int i=0;i<clientcount;i++) {
			executorService.execute(() -> {
				//通过Snowflake算法获取唯一的ID字符串
				String id = "" + snowFlake.nextId();
				log.info("id {} 开始获取redis的锁...",id);
				try {
					redisLock.lock(id);
					count++;
				} finally {
					redisLock.unlock(id);
					log.info("id {} 释放redis的锁...",id);
				}
				countDownLatch.countDown();
			});
		}
		countDownLatch.await();
		long end = System.currentTimeMillis();
		log.info("执行线程数:{},总耗时:{},count数为:{}",clientcount,end-start,count);
		return "";
	}
}
