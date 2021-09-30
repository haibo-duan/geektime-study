package com.dhb.gts.javacourse.week8.hmilytcc.bank1;

public class Bank2ClientFallback implements Bank2Client{

	@Override
	public Boolean transfer(int amount) {
		return false;
	}
}
