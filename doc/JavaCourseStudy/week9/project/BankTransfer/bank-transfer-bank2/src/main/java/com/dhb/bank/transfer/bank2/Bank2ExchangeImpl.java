package com.dhb.bank.transfer.bank2;

import com.dhb.bank.transfer.api.Bank2Exchange;
import com.dhb.bank.transfer.api.BankExchangeService;
import com.dhb.bank.transfer.dto.ExchangeDTO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version = "1.0.0", tag = "red", weight = 100)
public class Bank2ExchangeImpl implements Bank2Exchange {


	@Autowired
	BankExchangeService bankExchangeService;
	
	@Override
	public void exchange(ExchangeDTO exchangeDTO) {
		bankExchangeService.receiveExchange(exchangeDTO);
	}
}
