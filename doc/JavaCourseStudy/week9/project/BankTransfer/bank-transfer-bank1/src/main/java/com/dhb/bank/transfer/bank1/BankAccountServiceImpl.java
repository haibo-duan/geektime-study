package com.dhb.bank.transfer.bank1;

import com.dhb.bank.transfer.api.Bank2Service;
import com.dhb.bank.transfer.api.BankAccountService;
import com.dhb.bank.transfer.orm.dao.intf.BankAccountDao;
import com.dhb.bank.transfer.orm.dao.intf.CancelLogDao;
import com.dhb.bank.transfer.orm.dao.intf.ConfirmLogDao;
import com.dhb.bank.transfer.orm.dao.intf.TryLogDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class BankAccountServiceImpl implements BankAccountService {
	
	@Autowired
	BankAccountDao bankAccountDao;
	
	@Autowired
	TryLogDao tryLogDao;
	
	@Autowired
	CancelLogDao cancelLogDao;
	
	@Autowired
	ConfirmLogDao confirmLogDao;

	@DubboReference(version = "1.0.0")
	Bank2Service bank2Service;

	@Override
	@Transactional
	@Hmily(confirmMethod = "commit",cancelMethod = "rollback")
	public void subtractAccountBalance(int customerId, int amount) {
		HmilyTransactionContextLocal hmily = HmilyTransactionContextLocal.getInstance();
		HmilyTransactionContext context = hmily.get();
		String transId  = context.getTransId();
//		String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
		log.info("service begin try ... transID is {}",transId);
		boolean existTry = tryLogDao.isExist(transId);
		if(existTry) {
			log.info("service 已经执行了try,无需重复执行，事务id:{} ",transId);
			return;
		}
		//悬挂处理
		boolean  existCancle = cancelLogDao.isExist(transId);
		boolean  existConfirm = confirmLogDao.isExist(transId);
		if(existCancle||existConfirm ) {
			log.info("service 已经执行 confirm 或者 cancle，悬挂处理，事务id:{}",transId);
			return;
		}
		if(!bankAccountDao.subtractAccountBalance(customerId,amount)) {
			//扣减失败
			throw new HmilyRuntimeException("subtractAccountBalance exception! 扣减失败，事务id:{"+transId+"}");
		}
		//增加本地try日志
		tryLogDao.addTry(transId);

		//远程调用bank2 转账处理
		if(!bank2Service.transfer(customerId,amount)){
			throw new RuntimeException("bank1 远程调用李四微服务失败,xid:{}"+transId);
		}

		if(amount == 1000) {
			throw new RuntimeException(" when amount is 1000,make a exception!");
		}
		log.info("service subtract end try ... transID is {}",transId);
	}

	@Override
	public void addAccountBalance(int customerId, int amount) {
		return;
	}

	@Transactional
	public void commit(int customerId,int amount) {
		String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
		log.info("service begin commit ! transId is {}",transId);
	}

	@Transactional
	public void rollback(int customerId,int amount) {
		String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
		log.info("servie begin rollback... transId is {}",transId);
		boolean existTry = tryLogDao.isExist(transId);
		if(!existTry){//try阶段如果没有执行，什么也不做
			log.info("try 阶段失败，无需rollback。 transId is {}",transId);
			return;
		}
		boolean  existCancle = cancelLogDao.isExist(transId);
		if(existCancle){//cancle阶段如果已经执行过了 什么也不做
			log.info("cancle 阶段已经执行过rollback操作。transId is {}",transId);
			return;
		}
		//回滚操作，将金额添加回账户余额
		bankAccountDao.addAccountBalance(customerId,amount);
		//添加cancle 日志
		cancelLogDao.addCancel(transId);
		log.info("service end rollback ... transId is {}",transId);
	}
}
