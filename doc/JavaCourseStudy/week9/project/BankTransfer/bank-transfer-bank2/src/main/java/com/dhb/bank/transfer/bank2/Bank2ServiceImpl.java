package com.dhb.bank.transfer.bank2;

import com.dhb.bank.transfer.api.Bank2Service;
import com.dhb.bank.transfer.api.BankAccountService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version = "1.0.0", tag = "red", weight = 100)
public class Bank2ServiceImpl implements Bank2Service {

	@Autowired
	BankAccountService bankAccountService;

	@Override
	public Boolean transfer(int customerId, int amount) {
		this.bankAccountService.addAccountBalance(customerId,amount);
		return true;
	}
}
