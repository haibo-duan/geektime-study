package com.dhb.bank.transfer.client;

import com.dhb.bank.transfer.api.Bank1Exchange;
import com.dhb.bank.transfer.api.Bank1Service;
import com.dhb.bank.transfer.dto.AccountTypeEnum;
import com.dhb.bank.transfer.dto.ExchangeDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
@Slf4j
public class BankExchangeClientApplication {
	
	@DubboReference(version = "1.0.0") //, url = "dubbo://127.0.0.1:12345")
	private Bank1Exchange bank1Exchange;

	public static void main(String[] args) {
		SpringApplication.run(BankExchangeClientApplication.class, args);
	}


	@Bean
	public ApplicationRunner runner() {
		return args -> {
			String tid = UUID.randomUUID().toString();
			int sourceId = 10000;
			int sourceAmount = 500;
			int targetId = 10001;
			ExchangeDTO dto = new ExchangeDTO(tid, sourceId,AccountTypeEnum.USD,targetId,AccountTypeEnum.RMB,sourceAmount);
			bank1Exchange.exchange(dto);
			log.info("exchange dto is : {} ...",dto);
		};
	}
}
