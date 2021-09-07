package com.dhb.gts.javacourse.week5.generator;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.dhb.gts.javacourse.week5.mapper,com.dhb.gts.javacourse.week5.entity"})
public class GeneratorStarter {

	public static void main(String[] args) {
		SpringApplication.run(GeneratorStarter.class, args);
	}

}

