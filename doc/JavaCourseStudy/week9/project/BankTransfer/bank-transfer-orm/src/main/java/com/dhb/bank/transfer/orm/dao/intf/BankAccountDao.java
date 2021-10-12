package com.dhb.bank.transfer.orm.dao.intf;

import cn.org.atool.fluent.mybatis.base.IBaseDao;
import com.dhb.bank.transfer.orm.entity.BankAccountEntity;

/**
 * BankAccountDao: 数据操作接口
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
public interface BankAccountDao extends IBaseDao<BankAccountEntity> {

	boolean subtractAccountBalance(int customerId,int accountType, int amount);

	boolean addAccountBalance(int customerId,int accountType,int amount);
}
