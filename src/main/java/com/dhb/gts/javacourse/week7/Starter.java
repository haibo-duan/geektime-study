package com.dhb.gts.javacourse.week7;

import com.dhb.gts.javacourse.week7.dynamic.DynamicDataSourceRegister;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages  = {"com.dhb.gts.javacourse.fluent.dao","com.dhb.gts.javacourse.week7"} )
@MapperScan(basePackages = {"com.dhb.gts.javacourse.fluent.mapper"})
@Import({DynamicDataSourceRegister.class})
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class Starter {

	public static void main(String[] args) {
		SpringApplication.run(Starter.class, args);
	}
}
