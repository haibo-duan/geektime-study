package com.dhb.redis.pubsub;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PubSubController {

	@Autowired
	private MessagePublisher publisher;


	@RequestMapping(value="/redisPubSubTest")
	@ResponseBody
	public String pubMsg(String cutomerId, String  msg) {
		log.info("发布消息：{}", msg);
		JSONObject json = new JSONObject();
		json.put("cutomerId", cutomerId);
		json.put("msg", msg);
		publisher.publish(json.toJSONString());
		return "成功";
	}
	
}
