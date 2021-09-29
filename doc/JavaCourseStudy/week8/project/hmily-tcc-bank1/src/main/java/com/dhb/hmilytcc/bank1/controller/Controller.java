package com.dhb.hmilytcc.bank1.controller;

import com.dhb.hmilytcc.bank1.service.AccountBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

public class Controller {
	
	@Autowired
	AccountBalanceService accountBalanceService;

	@RequestMapping("/transfer")
	public Boolean transfer(int customerId,int amount) {
		this.accountBalanceService.subtractAccountBalance(customerId,amount);
		return true;
	}
}
