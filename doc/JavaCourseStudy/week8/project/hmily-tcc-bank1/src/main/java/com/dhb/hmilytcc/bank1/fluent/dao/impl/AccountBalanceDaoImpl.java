package com.dhb.hmilytcc.bank1.fluent.dao.impl;


import com.dhb.hmilytcc.bank1.fluent.dao.base.AccountBalanceBaseDao;
import com.dhb.hmilytcc.bank1.fluent.dao.intf.AccountBalanceDao;
import com.dhb.hmilytcc.bank1.fluent.wrapper.AccountBalanceUpdate;
import org.springframework.stereotype.Repository;

/**
 * AccountBalanceDaoImpl: 数据操作接口实现
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
@Repository
public class AccountBalanceDaoImpl extends AccountBalanceBaseDao implements AccountBalanceDao {

	@Override
	public boolean subtractAccountBalance(int customerId, int amount) {
		AccountBalanceUpdate update = new AccountBalanceUpdate()
				.set.balance().applyFunc("amount - ?",amount).end()
				.where.customerId().eq(customerId).end();
		return this.mapper.updateBy(update) > 0;
	}

	@Override
	public boolean addAccountBalance(int customerId, int amount) {
		AccountBalanceUpdate update = new AccountBalanceUpdate()
				.set.balance().applyFunc("amount + ?",amount).end()
				.where.customerId().eq(customerId).end();
		return this.mapper.updateBy(update) > 0;
	}
}
