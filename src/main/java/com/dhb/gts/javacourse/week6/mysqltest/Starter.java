package com.dhb.gts.javacourse.week6.mysqltest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages  = {"com.dhb.gts.javacourse.fluent.dao","com.dhb.gts.javacourse.week6.mysqltest"} )
@MapperScan(basePackages = {"com.dhb.gts.javacourse.fluent.mapper"})
public class Starter {

	public static void main(String[] args) {
		SpringApplication.run(Starter.class, args);
	}
}
