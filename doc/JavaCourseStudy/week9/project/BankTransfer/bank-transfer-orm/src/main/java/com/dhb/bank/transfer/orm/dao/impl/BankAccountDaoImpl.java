package com.dhb.bank.transfer.orm.dao.impl;

import com.dhb.bank.transfer.orm.dao.base.BankAccountBaseDao;
import com.dhb.bank.transfer.orm.dao.intf.BankAccountDao;
import com.dhb.bank.transfer.orm.wrapper.BankAccountUpdate;
import org.springframework.stereotype.Repository;

/**
 * BankAccountDaoImpl: 数据操作接口实现
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
@Repository
public class BankAccountDaoImpl extends BankAccountBaseDao implements BankAccountDao {

	@Override
	public boolean subtractAccountBalance(int customerId,int accountType, int amount) {
		BankAccountUpdate update = new BankAccountUpdate()
				.set.balance().applyFunc("balance - ?",amount).end()
				.where.customerId().eq(customerId).and.accountType().eq(accountType).and.balance().ge(amount).end();
		return this.mapper.updateBy(update) > 0;
	}

	@Override
	public boolean addAccountBalance(int customerId,int accountType, int amount) {
		BankAccountUpdate update = new BankAccountUpdate()
				.set.balance().applyFunc("balance + ?",amount).end()
				.where.customerId().eq(customerId).and.accountType().eq(accountType).end();
		return this.mapper.updateBy(update) > 0;
	}
}
