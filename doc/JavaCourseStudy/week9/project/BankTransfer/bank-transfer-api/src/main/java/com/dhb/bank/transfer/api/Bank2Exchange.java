package com.dhb.bank.transfer.api;

import com.dhb.bank.transfer.dto.ExchangeDTO;
import org.dromara.hmily.annotation.Hmily;

public interface Bank2Exchange {
	
	@Hmily
	void exchange(ExchangeDTO exchangeDTO);
	
}
