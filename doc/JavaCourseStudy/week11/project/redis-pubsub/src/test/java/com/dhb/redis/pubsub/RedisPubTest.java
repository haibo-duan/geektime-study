package com.dhb.redis.pubsub;

import org.junit.Test;
import redis.clients.jedis.Jedis;

public class RedisPubTest {

	@Test
	public void pubjava() {
		System.out.println("发布者 ");
		Jedis jr = null;
		try {
			jr = new Jedis("192.168.161.114", 6379, 0);// redis服务地址和端口号
			// jr客户端配置监听两个channel
			jr.publish( "news.share", "新闻分享");
			jr.publish( "news.blog", "新闻博客");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jr != null) {
				jr.disconnect();
			}
		}
	}
}
