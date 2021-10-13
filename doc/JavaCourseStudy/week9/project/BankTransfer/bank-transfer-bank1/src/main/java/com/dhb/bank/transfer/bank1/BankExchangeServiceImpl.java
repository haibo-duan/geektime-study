package com.dhb.bank.transfer.bank1;

import com.dhb.bank.transfer.api.Bank2Exchange;
import com.dhb.bank.transfer.api.BankExchangeService;
import com.dhb.bank.transfer.dto.ExchangeDTO;
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

@Component
@Slf4j
public class BankExchangeServiceImpl implements BankExchangeService {

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
	Bank2Exchange bank2Exchange;

	/**
	 * 兑换操作send方法，实现从bank1中的sourceid账号的sourcetype账户到 bank2中的targetid的账号的targettype账户 进行货币兑换
	 *  bank1中 sourceid的 sorcetype 类型账户 减少金额 减少的金额为 bank2中targetid的sorcetype类型账户增加的金额
	 *  bank1中 targetid的 targettype 类型账户 增加金额 增加的金额为bank2中targetid的targettype类型账户减少的金额
	 *
	 *  --bank1的 try阶段
	 *  sourceid 的sorcetype账户 扣除 sourceamount 进行冻结操作
	 *  sourceid 的sorcetype 的冻结金额增加 sourceamount 
	 *
	 *  --bank1的confirm阶段
	 *  sourceid 的targettype类型账户增加targetamount
	 *  sourceid 的sourcetype的冻结金额解除清零
	 *
	 *  --bank1的cancel阶段
	 *  如果confirm未执行
	 *      sourceid 的souretype的冻结金额解除清零
	 *      sourceid 的sourcetype账户加上 sourceamount
	 *  如果confirm已执行
	 *      sourceid 的targettype类型账户减去 targetamount
	 *      sourceid 的sourcetype账户加上 sourceamount
	 *
	 * @param dto
	 */
	@Override
	@HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
	public void sendExchange(ExchangeDTO dto) {
		log.info("Bank1 sendExchange try begin :" + dto.toString());
		//幂等判断 判断t_try_log表中是否有try日志记录，如果有则不再执行
		if (tryLogDao.isExist(dto.getTid())) {
			log.info("bank1 中，tid 为 {} 的try操作已执行。直接退出。。", dto.getTid());
			return;
		}
		//悬挂处理：如果cancel、confirm有一个已经执行了，try不再执行
		if (confirmLogDao.isExist(dto.getTid()) || cancelLogDao.isExist(dto.getTid())) {
			log.info("bank1 中，tid 为 {} 的cancel 或者 confirm 操作已执行。直接退出。。", dto.getTid());
			return;
		}
		//真正的try操作 扣减转出账户金额，执行冻结操作
		//账户金额是否足够，在方法subtractAccountBalance中判断。update语句中判断了是否有金额可供扣除。
		if (bankAccountDao.subtractAccountBalance(dto.getSourceId(),
				dto.getSourceAccountType(), dto.getSourceAmount())) {
			log.info("账户{} 类型{}  与账户 {} 类型{} 发起货币兑换，金额： {} ", dto.getSourceId(), dto.getSourceTypte(),
					dto.getTargetId(), dto.getTargetType(), dto.getSourceAmount());
			log.info("账户 {} 扣除兑换金额 {} 成功！！", dto.getTargetId(), dto.getSourceAmount());
			//扣减成功 调用bank2 执行冻结操作 增加冻结金额
			bankFreezeDao.addFreezeAmount(dto.getSourceId(), dto.getSourceAccountType(), dto.getSourceAmount());
			log.info("bank1 冻结操作成功，调用bank2的接口 bank2Exchange : {}", dto.toString());
			bank2Exchange.exchange(dto);
			log.info("bank1 调用bank2发起兑换成功，调用bank2的接口 bank2Exchange : {}", dto.toString());
			//记录try log
			tryLogDao.addTry(dto.getTid());
		} else {
			throw new HmilyRuntimeException("账户扣减异常！");
		}
		log.info("Bank1 sendExchange try end :" + dto.toString());
	}

	@Override
	public void receiveExchange(ExchangeDTO exchangeDTO) {
		log.info("Bank1 receiveExchange :" + exchangeDTO.toString());
	}


	/**
	 *  --bank1的confirm阶段
	 *  sourceid 的targettype类型账户增加targetamount
	 *  sourceid 的sourcetype的冻结金额解除清零
	 * @param dto
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean confirm(ExchangeDTO dto) {
		log.info("Bank1 confirm begin :{}", dto);
		//幂等判断 判断t_confirm_log表中是否有confirm日志记录，如果有则不再执行
		if (!confirmLogDao.isExist(dto.getTid())) {
			//只有try操作完成之后，且cancel操作未执行的情况下，才允许执行confirm
			if (tryLogDao.isExist(dto.getTid()) && !cancelLogDao.isExist(dto.getTid())) {
				//增加target的账户金额
				bankAccountDao.addAccountBalance(dto.getSourceId(), dto.getTargetAccountType(), dto.getTargetAmount());
				log.info("bank1 兑换成功，现在在bank1的target类型账户中增加所兑换到的金额。");
				//解除冻结金额
				bankFreezeDao.subtractFreezeAmount(dto.getSourceId(),dto.getSourceAccountType(),dto.getSourceAmount());
				log.info("bank1 exchange成功，解除账户冻结金额。。");
				//记录confirm日志
				confirmLogDao.addConfirm(dto.getTid());
			}else {
				log.info("bank1 中，tid 为 {} 的try操作未执行或者cancel操作已执行。直接退出。。", dto.getTid());	
			}
		}else {
			log.info("bank1 中，tid 为 {} 的confirm操作已执行。直接退出。。", dto.getTid());
		}
		log.info("Bank1 confirm end :{}", dto);
		return Boolean.TRUE;
	}


	/**
	 *  --bank1的cancel阶段
	 *  如果confirm未执行
	 *      sourceid 的souretype的冻结金额解除清零
	 *      sourceid 的sourcetype账户加上 sourceamount
	 *  如果confirm已执行
	 *      sourceid 的targettype类型账户减去 targetamount
	 *      sourceid 的sourcetype账户加上 sourceamount
	 * @param dto
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean cancel(ExchangeDTO dto) {
		log.info("Bank1 cancel begin :{}", dto);
		//幂等校验 只有当cancel操作未执行的情况下，才执行cancel，否则什么也不做。
		if (!cancelLogDao.isExist(dto.getTid())) {
			//空回滚操作，如果try操作未执行，那么cancel什么也不做，当且仅当try执行之后，才能执行cancel
			if (tryLogDao.isExist(dto.getTid())) {
				//cancel操作，需要判断confirm是否执行了
				//如果此时confirm还未执行，那么需要将冻结金额清除
				if(!confirmLogDao.isExist(dto.getTid())){
					bankFreezeDao.subtractFreezeAmount(dto.getSourceId(), dto.getSourceAccountType(), dto.getSourceAmount());
					log.info("bank1 cancel 操作中，解除冻结金额成功!");
				}else {
					//confirm已经执行的话，那么首先减去target 账户余额
					bankAccountDao.subtractAccountBalance(dto.getSourceId(), dto.getTargetAccountType(), dto.getTargetAmount());
					log.info("取消操作,扣减target账户余额!");
				}
				//增加soruce账户余额
				bankAccountDao.addAccountBalance(dto.getSourceId(),dto.getSourceAccountType(),dto.getSourceAmount());
				log.info("取消操作,增加source账户余额!");
				//记录cancel日志
				cancelLogDao.addCancel(dto.getTid());
			}else {
				log.info("bank1 中，tid 为 {} 的try操作未执行，空回滚。直接退出。。", dto.getTid());
			}
		}else {
			log.info("bank1 中，tid 为 {} 的cancel操作已执行。直接退出。。", dto.getTid());
		}
		return Boolean.TRUE;
	}
}
