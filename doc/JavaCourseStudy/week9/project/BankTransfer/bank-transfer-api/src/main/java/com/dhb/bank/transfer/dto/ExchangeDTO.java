package com.dhb.bank.transfer.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExchangeDTO implements Serializable {

	private String tid;

	private int sourceId;

	private AccountTypeEnum sourceTypte;

	private int targetId;
	
	private AccountTypeEnum targetType;

	private int sourceAmount;

	private int targetAmount;

	public ExchangeDTO(String tid, int sourceId, AccountTypeEnum sourceTypte, int targetId, AccountTypeEnum targetType, int sourceAmount) {
		this.tid = tid;
		this.sourceId = sourceId;
		this.sourceTypte = sourceTypte;
		this.targetId = targetId;
		this.targetType = targetType;
		this.sourceAmount = sourceAmount;
		this.targetAmount = getExchangeAmount();
	}

	private int getExchangeAmount() {
		int amount;
		if (sourceTypte == AccountTypeEnum.RMB && targetType == AccountTypeEnum.USD) {
			amount = sourceAmount / 7;
		} else if (sourceTypte == AccountTypeEnum.USD && targetType == AccountTypeEnum.RMB) {
			amount = sourceAmount * 7;
		} else {
			amount = sourceAmount;
		}
		return amount;
	}
	
	public int getSourceAccountType() {
		return getSourceTypte().getAccountType();
	}
	
	public int getTargetAccountType() {
		return getTargetType().getAccountType();
	}
}
