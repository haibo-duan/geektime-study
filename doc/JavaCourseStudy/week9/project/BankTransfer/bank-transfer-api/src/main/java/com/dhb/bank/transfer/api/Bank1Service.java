package com.dhb.bank.transfer.api;

import org.dromara.hmily.annotation.Hmily;

public interface Bank1Service {
	
	@Hmily
	Boolean transfer(String tid,int customerId,int amount);
}
