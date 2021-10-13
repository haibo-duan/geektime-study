package com.dhb.bank.transfer.api;

public interface BankAccountService {


	void subtractAccountBalance(String tid,int customerId, int amount);

	void addAccountBalance(String tid,int customerId, int amount);
}
