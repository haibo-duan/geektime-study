package com.dhb.bank.transfer.bank2;

import com.dhb.bank.transfer.api.BankAccountService;
import com.dhb.bank.transfer.orm.dao.intf.BankAccountDao;
import com.dhb.bank.transfer.orm.dao.intf.CancelLogDao;
import com.dhb.bank.transfer.orm.dao.intf.ConfirmLogDao;
import com.dhb.bank.transfer.orm.dao.intf.TryLogDao;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.HmilyTCC;
import org.dromara.hmily.common.exception.HmilyRuntimeException;
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
	ConfirmLogDao confirmLogDao;

	@Autowired
	CancelLogDao cancelLogDao;
	
	@Override
	public void subtractAccountBalance(String tid,int customerId, int amount) {

	}

	@Override
	@HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
	public void addAccountBalance(String tid,int customerId, int amount) {
		log.info("bank2 addAccountBalance try begin ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		//幂等校验 
		if(!tryLogDao.isExist(tid)) {
			tryLogDao.addTry(tid);
		}
		log.info("bank2 addAccountBalance try end ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);

	}


	@Transactional(rollbackFor = Exception.class)
	public boolean confirm(String tid,int customerId, int amount) {
		log.info("bank2 confirm begin ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		//幂等校验 且try执行完成
		if(!confirmLogDao.isExist(tid)){
			if(tryLogDao.isExist(tid)) {
				bankAccountDao.addAccountBalance(customerId, 1, amount);
				log.info("账户 {} 收款金额 {} 成功！！！", customerId, amount);
				confirmLogDao.addConfirm(tid);
			}
		}
		log.info("bank2 confirm end ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		return Boolean.TRUE;
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean cancel(String tid,int customerId, int amount) {
		log.info("bank2 cancel begin ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		//幂等校验 且try执行完毕
		if(!cancelLogDao.isExist(tid)){
			if(tryLogDao.isExist(tid)) {
				bankAccountDao.subtractAccountBalance(customerId, 1, amount);
				cancelLogDao.addCancel(tid);
			}
		}

		log.info("bank2 cancel end ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		return Boolean.TRUE;
	}

}
