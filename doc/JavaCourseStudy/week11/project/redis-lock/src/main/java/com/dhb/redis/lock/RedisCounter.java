package com.dhb.redis.lock;


import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Slf4j
public class RedisCounter {
	
	private JedisPool jedisPool;
	
	private static final String COUNTER_LOCK = "counter_lock";

	private static final long LOCK_TIMEOUT = 30000;

	private SnowFlake snowFlake = new SnowFlake(1, 1);


	public RedisCounter(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	

	public boolean increment(String key, int expire) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			result = jedis.incr(key);
			if (0 != expire) {
				jedis.expire(key, expire);
			}
			log.info("set {} = {}", key, result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
		return false;
	}

	public boolean increment(String key, int expire,int num) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			result = jedis.incrBy(key,num);
			if (0 != expire) {
				jedis.expire(key, expire);
			}
			log.info("set {} = {}", key, result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
		return false;
	}
	
	
	public boolean decrement(String key,int expire,int num) {
		boolean result = false;
		Jedis jedis = jedisPool.getResource();
		RedisLock lock = new RedisLock(jedisPool);
		String id = snowFlake.nextId() + "";
		try {
			lock.lock(id,LOCK_TIMEOUT);
			String size = jedis.get(key);
			int stock_size = Integer.parseInt(size);
			if(stock_size >= num){
				log.info("库存充足为 {}，现在开始减库存，扣除 {} ...",stock_size,num);
				jedis.decrBy(key,num);
				if (0 != expire) {
					jedis.expire(key, expire);
				}
				result = true;
			}else {
				log.info("库存不足，库存为 {}，需要扣除 {} ，扣除失败...",stock_size,num);
			}
		}finally {
			lock.unlock(id);
			jedis.close();
		}
		return result;
	}

	public boolean decrement(String key,int expire) {
		boolean result = false;
		Jedis jedis = jedisPool.getResource();
		RedisLock lock = new RedisLock(jedisPool);
		try {
			lock.lock(COUNTER_LOCK,LOCK_TIMEOUT);
			String size = jedis.get(key);
			int stock_size = Integer.parseInt(size);
			if(stock_size >= 1){
				log.info("库存充足为 {}，现在开始减库存 ...",stock_size);
				jedis.decr(key);
				if (0 != expire) {
					jedis.expire(key, expire);
				}
				result = true;
			}else {
				log.info("库存不足，库存为 {}，扣除失败...",stock_size);
			}
		}finally {
			lock.unlock(COUNTER_LOCK);
			jedis.close();
		}
		return result;
	}
}
