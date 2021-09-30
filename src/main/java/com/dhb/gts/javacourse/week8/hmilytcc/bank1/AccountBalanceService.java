package com.dhb.gts.javacourse.week8.hmilytcc.bank1;

import com.dhb.gts.javacourse.fluent.dao.intf.AccountBalanceDao;
import com.dhb.gts.javacourse.fluent.dao.intf.CancleLogDao;
import com.dhb.gts.javacourse.fluent.dao.intf.ConfirmLogDao;
import com.dhb.gts.javacourse.fluent.dao.intf.TryLogDao;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.Hmily;
import org.dromara.hmily.common.bean.context.HmilyTransactionContext;
import org.dromara.hmily.common.exception.HmilyRuntimeException;
import org.dromara.hmily.core.concurrent.threadlocal.HmilyTransactionContextLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class AccountBalanceService {
	
	@Autowired
	private AccountBalanceDao accountBalanceDao;
	@Autowired
	private TryLogDao tryLogDao;
	@Autowired
	private CancleLogDao cancleLogDao;
	@Autowired
	private ConfirmLogDao confirmLogDao;
	
	@Autowired
	private Bank2Client bank2Client;
	
	@Transactional
	@Hmily(confirmMethod = "commit",cancelMethod = "rollback")
	public void subtractAccountBalance(int customerId,int amount) {
		String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
		log.info("service begin try ... transID is {}",transId);
		
		boolean existTry = tryLogDao.isExist(transId);
		if(existTry) {
			log.info("service 已经执行了try,无需重复执行，事务id:{} ",transId);
			return;
		}
		//悬挂处理
		boolean  existCancle = cancleLogDao.isExist(transId);
		boolean  existConfirm = confirmLogDao.isExist(transId);
		if(existCancle||existConfirm ) {
			log.info("service 已经执行 confirm 或者 cancle，悬挂处理，事务id:{}",transId);
			return;
		}
		if(!accountBalanceDao.subtractAccountBalance(customerId,amount)) {
			//扣减失败
			throw new HmilyRuntimeException("subtractAccountBalance exception! 扣减失败，事务id:{"+transId+"}");
		}
		
		//增加本地try日志
		tryLogDao.addTry(transId);

		//远程调用bank2 转账处理
		if(!bank2Client.transfer(amount)){
			throw new RuntimeException("bank1 远程调用李四微服务失败,xid:{}"+transId);
		}
		
		
		if(amount == 1000) {
			throw new RuntimeException(" when amount is 1000,make a exception!");
		}
		log.info("service subtract end try ... transID is {}",transId);
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
		boolean  existCancle = cancleLogDao.isExist(transId);
		if(existCancle){//cancle阶段如果已经执行过了 什么也不做
			log.info("cancle 阶段已经执行过rollback操作。transId is {}",transId);
			return;
		}
		//回滚操作，将金额添加回账户余额
		accountBalanceDao.addAccountBalance(customerId,amount);
		//添加cancle 日志
		cancleLogDao.addCancle(transId);
		log.info("service end rollback ... transId is {}",transId);
	}
	
}
