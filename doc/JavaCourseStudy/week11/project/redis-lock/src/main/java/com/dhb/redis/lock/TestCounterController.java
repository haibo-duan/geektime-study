package com.dhb.redis.lock;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@Slf4j
public class TestCounterController {
	
	@Autowired
	JedisPool jedisPool;
	
	private static final  String COUNTER_KEY = "counter_key";
	
	private static final int EXPIRE = 500;

	int count = 0;
	
	private static final String STOCK_KEY = "stock_key";
	
	private static final String STOCK_INIT_SIZE = 1000+"";
	
	private Random random = new Random();



	@RequestMapping("/testCounter")
	@ResponseBody
	public String testCounter() throws InterruptedException {
		int clientcount = 10;
		CountDownLatch countDownLatch = new CountDownLatch(clientcount);
		ExecutorService executorService = Executors.newFixedThreadPool(clientcount);

		//初始化库存
		Jedis jedis = jedisPool.getResource();
		String lock = jedis.set(STOCK_KEY, STOCK_INIT_SIZE);
		jedis.close();
		
		long start = System.currentTimeMillis();
		RedisCounter counter = new RedisCounter(jedisPool);
		for(int i=0;i<clientcount;i++) {
			executorService.execute(() -> {
				int num = random.nextInt(100);
				boolean result =counter.decrement(STOCK_KEY,EXPIRE,num);
				if(!result) {
					log.info("库存扣除失败！");
				}
				count++;
				countDownLatch.countDown();
			});
		}
		countDownLatch.await();
		long end = System.currentTimeMillis();
		log.info("执行线程数:{},总耗时:{},count数为:{}",clientcount,end-start,count);
		return "OK";
	}
	
}
