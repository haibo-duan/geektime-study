package com.dhb.bank.transfer.api;

import org.dromara.hmily.annotation.Hmily;

public interface Bank2Service {

	@Hmily
	Boolean transfer(int customerId,int amount);
}
