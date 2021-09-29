package com.dhb.hmilytcc.bank1.service;

import org.dromara.hmily.annotation.Hmily;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "bank2",fallback = Bank2ClientFallback.class)
public interface Bank2Client {

	//远程调用bank2的微服务
	@GetMapping("/bank2/transfer")
	@Hmily
	public  Boolean transfer(@RequestParam("amount") int amount);
	
}
