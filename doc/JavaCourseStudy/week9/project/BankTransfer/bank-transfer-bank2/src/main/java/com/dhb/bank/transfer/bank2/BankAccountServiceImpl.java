package com.dhb.bank.transfer.bank2;

import com.dhb.bank.transfer.api.BankAccountService;
import com.dhb.bank.transfer.orm.dao.intf.BankAccountDao;
import com.dhb.bank.transfer.orm.dao.intf.CancelLogDao;
import com.dhb.bank.transfer.orm.dao.intf.ConfirmLogDao;
import com.dhb.bank.transfer.orm.dao.intf.TryLogDao;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.Hmily;
import org.dromara.hmily.core.concurrent.threadlocal.HmilyTransactionContextLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {


	@Autowired
	BankAccountDao bankAccountDao;

	@Autowired
	TryLogDao tryLogDao;

	@Autowired
	CancelLogDao cancelLogDao;

	@Autowired
	ConfirmLogDao confirmLogDao;
	
	
	@Override
	public void subtractAccountBalance(int customerId, int amount) {
		
	}

	@Override
	@Transactional
	@Hmily(confirmMethod = "commit",cancelMethod = "rollback")
	public void addAccountBalance(int customerId, int amount) {
		String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
		log.info("service begin try ... transID is {}",transId);
	}

	@Transactional
	public void confirmMethod(int customerId,int amount) {
		String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
		log.info("service begin confirm ... transID is {}",transId);
		boolean  existConfirm = confirmLogDao.isExist(transId);
		if(existConfirm) {
			log.info(" 已经执行过confirm... 无需再次confirm  transId is {}",transId );
		}
		//正式增加金额
		bankAccountDao.addAccountBalance(customerId,amount);
		//添加confirm日志
		confirmLogDao.addConfirm(transId);
	}


	@Transactional
	public  void cancelMethod(String accountNo, Double amount) {
		String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
		log.info("Service begin cancel...  "+transId );

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
