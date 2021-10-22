package com.dhb.lettuce.demo.controller;

import com.dhb.lettuce.demo.orm.entity.BankAccountEntity;
import com.dhb.lettuce.demo.service.BankAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bankAccount")
@Slf4j
public class BankAccountController {
	
	@Autowired
	private BankAccountService bankAccountService;
	
	@GetMapping("query")
	@ResponseBody
	public BankAccountEntity queryBankAccount(int id){
		BankAccountEntity entity = bankAccountService.getBankAccount(id);
		log.info("query bankaccount is :{}",entity.toString());
		return entity;
	}

	@GetMapping("add")
	@ResponseBody
	public BankAccountEntity addBankAccount(int customerId,int account_type){
		BankAccountEntity entity = new BankAccountEntity();
		entity.setAccountType(account_type);
		entity.setBalance(0L);
		entity.setCustomerId(customerId);
		entity.setIsValidate(1);
		bankAccountService.addBankAccount(entity);
		log.info("add bankaccount is :{}",entity.toString());
		return entity;
	}

	@GetMapping("update")
	@ResponseBody
	public BankAccountEntity updateBankAccount(int id,int balance){
		BankAccountEntity entity = new BankAccountEntity();
		entity.setBalance((long)balance);
		entity.setId(id);
		bankAccountService.updateBankAccount(entity);
		log.info("add bankaccount is :{}",entity.toString());
		return entity;
	}
}
