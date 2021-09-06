package com.dhb.gts.javacourse.week5.springbean.v5;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.dhb.gts.javacourse.week5.mapper,com.dhb.gts.javacourse.week5.entity"})
public class Starter {


	public static void main(String[] args) {
		SpringApplication.run(Starter.class, args);
	}
}
