package com.dhb.hmilytcc.bank1;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.dhb.hmilytcc.bank1.fluent.mapper"})
public class HmilyTccBank1Application {

	public static void main(String[] args) {
		SpringApplication.run(HmilyTccBank1Application.class, args);
	}

}
