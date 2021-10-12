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
	


	@Override
	public void subtractAccountBalance(int customerId, int amount) {

	}

	@Override
	@HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
	public void addAccountBalance(int customerId, int amount) {
		if (bankAccountDao.addAccountBalance(customerId,1, amount)) {
			log.info("账户 {} 收款金额 {} 成功！！！", customerId, amount);
		} else {
			throw new HmilyRuntimeException("账户收款异常！");
		}
	}


	@Transactional(rollbackFor = Exception.class)
	public boolean confirm(int customerId, int amount) {
		log.info("============dubbo tcc 执行确认收款接口===============");
		return Boolean.TRUE;
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean cancel(int customerId, int amount) {
		log.info("============ dubbo tcc 收款取消付款接口===============");
		return Boolean.TRUE;
	}

}
