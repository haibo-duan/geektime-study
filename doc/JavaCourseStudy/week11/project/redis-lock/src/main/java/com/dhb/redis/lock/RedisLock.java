package com.dhb.redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisLock {
	
	//锁的key
	private String lockKey = "redis_lock";
	//锁过期时间
	private long internalLockLeaseTime = 30000;
	//获取锁的超时时间
	private long timeout = 100000;
	
	SetParams params = SetParams.setParams().nx().px(internalLockLeaseTime);
	
	@Autowired
	JedisPool jedisPool;

	public boolean lock(String id) {
		Jedis jedis = jedisPool.getResource();
		Long start = System.currentTimeMillis();
		try {
			while (true){
				//通过set命令 如果set成功则说明获取到了锁
				String lock = jedis.set(lockKey,id,params);
				if("OK".equals(lock)){
					log.info("id {} 获取锁成功...",id);
					return true;
				}
				//否则循环等待
				long l = System.currentTimeMillis() - start;
				//判断 是否达到超时等待时间
				if(l >= timeout) {
					log.info("id {} 获取锁超时失败...",id);
					return false;
				}
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}finally {
			jedis.close();
		}
	}
	
	public boolean unlock(String id) {
		Jedis jedis = jedisPool.getResource();
		String script = " if redis.call('get',KEYS[1]) == ARGV[1] then"+
				"  return redis.call('del',KEYS[1])  else return 0 end ";
		try {
			Object result = jedis.eval(script, Collections.singletonList(lockKey),Collections.singletonList(id));
			if("1".equals(result.toString())) {
				log.info("id {} 释放锁成功...",id);
				return  true;
			}
			return false;
		}finally {
			jedis.close();
		}
	}
}
