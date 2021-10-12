package com.dhb.bank.transfer.api;

import org.dromara.hmily.annotation.Hmily;

public interface BankAccountService {

	@Hmily
	void subtractAccountBalance(int customerId,int amount);
	@Hmily
	void addAccountBalance(int customerId,int amount);
}
