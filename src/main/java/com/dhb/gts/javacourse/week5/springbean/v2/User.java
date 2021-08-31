package com.dhb.gts.javacourse.week5.springbean.v2;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class User {
	//编号
	private Integer id;
	//姓名
	private String name;
	//年龄
	private Integer age;

	@Autowired
	public User(@Value("1") int id, @Value("用户") String name,@Value("22")  int age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}
}
