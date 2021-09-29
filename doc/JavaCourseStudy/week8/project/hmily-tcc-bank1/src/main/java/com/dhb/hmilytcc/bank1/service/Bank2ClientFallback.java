package com.dhb.hmilytcc.bank1.service;

public class Bank2ClientFallback implements Bank2Client {

	@Override
	public Boolean transfer(int amount) {
		return false;
	}
}
