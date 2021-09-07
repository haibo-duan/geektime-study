package com.dhb.gts.javacourse.week5.generator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dhb.gts.javacourse.week5.entity.Users;
import com.dhb.gts.javacourse.week5.entity.UsersExample;
import com.dhb.gts.javacourse.week5.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
public class GeneratorController {

	@Autowired
	private UsersMapper usersMapper;
	
	private Random random = new Random();

	@RequestMapping("/generatorquery")
	@ResponseBody
	public String generatorQuery(HttpServletRequest request, HttpServletResponse response) {
		UsersExample example = new UsersExample();
		example.createCriteria();

		List<Users> list = usersMapper.selectByExample(example);
		return JSON.toJSONString(list, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteDateUseDateFormat);
	}

	@RequestMapping("/generatorinsert")
	@ResponseBody
	public String generatorInsert(HttpServletRequest request, HttpServletResponse response) {
		Users users = new Users();
		users.setPassword(String.valueOf(System.currentTimeMillis()));
		users.setUsername("generatorinsert"+random.nextInt(100));
		users.setCreatedate(new Date());
		usersMapper.insert(users);
		return "success";
	}
}
