package com.dhb.bank.transfer.api;

import com.dhb.bank.transfer.dto.ExchangeDTO;

public interface BankExchangeService {
	
	void sendExchange(ExchangeDTO exchangeDTO);
	
	void receiveExchange(ExchangeDTO exchangeDTO);
	
}
