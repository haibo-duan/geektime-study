package com.dhb.bank.transfer.bank2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.dhb.bank.transfer.orm.dao", "com.dhb.bank.transfer.bank2"})
@MapperScan(basePackages = {"com.dhb.bank.transfer.orm.mapper"})
public class BankTransferBank2Application {

	public static void main(String[] args) {
		SpringApplication.run(BankTransferBank2Application.class, args);
	}

}
