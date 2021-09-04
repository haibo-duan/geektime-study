package com.dhb.gts.javacourse.week5.springbean.v5;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class BeanCreate {

	@Bean("student1")
	@Order(1)
	public static Student getStudent1() {
		return Student.create();
	}

	@Bean("student2")
	@Order(2)
	public static Student getStudent2() {
		return Student.create();
	}

}
