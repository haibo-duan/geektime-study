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

	@Autowired
	TryLogDao tryLogDao;

	@Autowired
	ConfirmLogDao confirmLogDao;

	@Autowired
	CancelLogDao cancelLogDao;

	@DubboReference(version = "1.0.0")
	Bank2Service bank2Service;

	@Override
	@HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
	public void subtractAccountBalance(String tid, int customerId, int amount) {
		log.info("bank1 subtractAccountBalance try begin ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		//幂等判断 判断t_try_log表中是否有try日志记录，如果有则不再执行
		if (tryLogDao.isExist(tid)) {
			log.info("bank1 中，tid 为 {} 的try操作已执行。直接退出。。", tid);
			return;
		}
		//悬挂处理：如果cancel、confirm有一个已经执行了，try不再执行
		if (confirmLogDao.isExist(tid) || cancelLogDao.isExist(tid)) {
			log.info("bank1 中，tid 为 {} 的cancel 或者 confirm 操作已执行。直接退出。。", tid);
			return;
		}
		//扣减金额 冻结操作
		if (bankAccountDao.subtractAccountBalance(customerId, 1, amount)) {
			log.info("bank1 中，账户 {} 扣减金额 {} 成功 tid is {} ！！！", customerId, amount, tid);
			//插入try操作日志
			tryLogDao.addTry(tid);
			log.info("bank1 中插入try log ...");
		} else {
			throw new HmilyRuntimeException("账户扣减异常！");
		}
		//调用bank2 发起转账
		try {
			bank2Service.transfer(tid,customerId, amount);
		} catch (Exception e) {
			throw new HmilyRuntimeException("远程调用异常！");
		}
		log.info("bank1 subtractAccountBalance try end ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
	}

	@Override
	public void addAccountBalance(String tid, int customerId, int amount) {
		return;
	}


	@Transactional(rollbackFor = Exception.class)
	public boolean confirm(String tid, int customerId, int amount) {
		log.info("bank1 confirm begin ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		//幂等校验 如果confirm操作未执行，才能去除冻结金额，否则什么也不做。。
		if (!confirmLogDao.isExist(tid)) {
			//只有try操作完成之后，且cancel操作未执行的情况下，才允许执行confirm
			if (tryLogDao.isExist(tid) && !cancelLogDao.isExist(tid)) {
				//解除冻结金额
				log.info("bank1 confirm 操作中，解除冻结金额成功!");
				bankFreezeDao.subtractFreezeAmount(customerId, 1, amount);
				//写入confirm日志
				log.info("bank1 confirm 操作中，增加confirm日志!");
				confirmLogDao.addConfirm(tid);
			}
		}
		log.info("bank1 confirm end ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		return Boolean.TRUE;
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean cancel(String tid, int customerId, int amount) {
		log.info("bank1 cancel begin ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		//幂等校验 只有当cancel操作未执行的情况下，才执行cancel，否则什么也不做。
		if (!cancelLogDao.isExist(tid)) {
			//空回滚操作，如果try操作未执行，那么cancel什么也不做，当且仅当try执行之后，才能执行cancel
			if (tryLogDao.isExist(tid)) {
				//cancel操作，需要判断confirm是否执行了
				//如果此时confirm还未执行，那么需要将冻结金额清除
				if (!confirmLogDao.isExist(tid)) {
					log.info("bank1 cancel 操作中，解除冻结金额成功!");
					bankFreezeDao.subtractFreezeAmount(customerId, 1, amount);
				}
				log.info("bank1  cancel 操作中，增加账户余额成功!");
				bankAccountDao.addAccountBalance(customerId, 1, amount);
				//增加cancel log
				cancelLogDao.addCancel(tid);
			}
		}
		log.info("bank1 cancel end ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		return Boolean.TRUE;
	}
}
