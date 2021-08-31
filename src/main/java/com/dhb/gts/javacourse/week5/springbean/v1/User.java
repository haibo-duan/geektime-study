package com.dhb.gts.javacourse.week5.springbean.v1;

import lombok.Data;

@Data
public class User {
	//编号
	private int id;
	//姓名
	private String name;
	//年龄
	private int age;

	public User(int id, String name, int age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}
}
