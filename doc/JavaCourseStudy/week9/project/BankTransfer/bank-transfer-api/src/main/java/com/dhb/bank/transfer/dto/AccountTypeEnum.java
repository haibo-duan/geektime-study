package com.dhb.bank.transfer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountTypeEnum {
	RMB("人民币",1),USD("美元",2);
	
	private String name;
	private int accountType;
	
}
