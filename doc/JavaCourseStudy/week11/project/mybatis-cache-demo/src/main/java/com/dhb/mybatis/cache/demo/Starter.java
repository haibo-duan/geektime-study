package com.dhb.mybatis.cache.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.dhb.mybatis.cache.orm.dao","com.dhb.mybatis.cache.demo"})
@MapperScan(basePackages = {"com.dhb.mybatis.cache.orm.mapper"})
public class Starter {

	public static void main(String[] args) {
		SpringApplication.run(Starter.class, args);
	}
}
