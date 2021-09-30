package com.dhb.gts.javacourse.week8.hmilytcc.bank1;

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
