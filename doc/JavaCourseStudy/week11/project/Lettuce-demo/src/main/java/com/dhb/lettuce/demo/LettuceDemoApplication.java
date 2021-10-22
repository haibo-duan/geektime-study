package com.dhb.lettuce.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.dhb.lettuce.demo.orm.dao","com.dhb.lettuce.demo"})
@MapperScan(basePackages = {"com.dhb.lettuce.demo.orm.mapper"})
public class LettuceDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(LettuceDemoApplication.class, args);
	}

}
