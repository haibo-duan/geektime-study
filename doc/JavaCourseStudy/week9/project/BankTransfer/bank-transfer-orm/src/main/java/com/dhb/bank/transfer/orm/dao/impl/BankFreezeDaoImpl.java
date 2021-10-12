package com.dhb.bank.transfer.orm.dao.impl;

import com.dhb.bank.transfer.orm.dao.base.BankFreezeBaseDao;
import com.dhb.bank.transfer.orm.dao.intf.BankFreezeDao;
import com.dhb.bank.transfer.orm.wrapper.BankFreezeUpdate;
import org.springframework.stereotype.Repository;

/**
 * BankFreezeDaoImpl: 数据操作接口实现
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
@Repository
public class BankFreezeDaoImpl extends BankFreezeBaseDao implements BankFreezeDao {
	
	@Override
	public boolean subtractFreezeAmount(int customerId,int accountType, int amount) {
		BankFreezeUpdate update = new BankFreezeUpdate()
				.set.amount().applyFunc("amount - ?",amount).end()
				.where.customerId().eq(customerId).and.accountType().eq(accountType).and.amount().ge(amount).end();
		return this.mapper.updateBy(update) > 0;
	}

	@Override
	public boolean addFreezeAmount(int customerId, int accountType,int amount) {
		BankFreezeUpdate update = new BankFreezeUpdate()
				.set.amount().applyFunc("amount + ?",amount).end()
				.where.customerId().eq(customerId).and.accountType().eq(accountType).end();
		return this.mapper.updateBy(update) > 0;
	}
}
