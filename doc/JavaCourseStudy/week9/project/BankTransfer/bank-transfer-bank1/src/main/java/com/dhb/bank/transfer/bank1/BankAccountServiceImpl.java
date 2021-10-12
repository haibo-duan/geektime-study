package com.dhb.bank.transfer.bank1;

import com.dhb.bank.transfer.api.Bank2Service;
import com.dhb.bank.transfer.api.BankAccountService;
import com.dhb.bank.transfer.orm.dao.intf.BankAccountDao;
import com.dhb.bank.transfer.orm.dao.intf.BankFreezeDao;
import com.dhb.bank.transfer.orm.dao.intf.CancelLogDao;
import com.dhb.bank.transfer.orm.dao.intf.ConfirmLogDao;
import com.dhb.bank.transfer.orm.dao.intf.TryLogDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;

import org.dromara.hmily.annotation.HmilyTCC;
import org.dromara.hmily.common.exception.HmilyRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class BankAccountServiceImpl implements BankAccountService {

	private static AtomicInteger confrimCount = new AtomicInteger(0);
	
	@Autowired
	BankAccountDao bankAccountDao;
	
	@Autowired
	BankFreezeDao bankFreezeDao;
	

	@DubboReference(version = "1.0.0")
	Bank2Service bank2Service;

	@Override
	@HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
	public void subtractAccountBalance(int customerId, int amount) {
		if(bankAccountDao.subtractAccountBalance(customerId,1,amount)){
			log.info("账户 {} 扣减金额 {} 成功！！！",customerId,amount);
		}else {
			throw new HmilyRuntimeException("账户扣减异常！");
		}
	}

	@Override
	public void addAccountBalance(int customerId, int amount) {
		return;
	}
	

	@Transactional(rollbackFor = Exception.class)
	public boolean confirm(int customerId, int amount) {
		log.info("============dubbo tcc 执行确认付款接口===============");
		if(bankFreezeDao.addFreezeAmount(customerId,1,amount)){
			final int i = confrimCount.incrementAndGet();
			log.info("bank1 冻结操作成功，调用bank2的接口。");
			bank2Service.transfer(customerId,amount);
		}else {
			log.info("调用操作失败！");
			throw new HmilyRuntimeException("调用操作失败！");
		}
		return Boolean.TRUE;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public boolean cancel(int customerId, int amount) {
		log.info("============ dubbo tcc 执行取消付款接口===============");
		if(bankFreezeDao.subtractFreezeAmount(customerId,1,amount)){
			log.info("取消操作，解除冻结金额成功!");
			bankAccountDao.addAccountBalance(customerId,1,amount);
			log.info("增加账户余额!");
		}else {
			log.info("cancel操作执行失败！！！");
		}
		return Boolean.TRUE;
	}
}
