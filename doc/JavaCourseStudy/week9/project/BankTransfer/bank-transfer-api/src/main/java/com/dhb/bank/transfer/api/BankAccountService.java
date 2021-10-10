package com.dhb.bank.transfer.api;

public interface BankAccountService {

	void subtractAccountBalance(int customerId,int amount);

	void addAccountBalance(int customerId,int amount);
}
