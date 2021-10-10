package com.dhb.bank.transfer.orm.dao.impl;

import com.dhb.bank.transfer.orm.dao.base.ConfirmLogBaseDao;
import com.dhb.bank.transfer.orm.dao.intf.ConfirmLogDao;
import com.dhb.bank.transfer.orm.entity.ConfirmLogEntity;
import org.springframework.stereotype.Repository;

/**
 * ConfirmLogDaoImpl: 数据操作接口实现
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
@Repository
public class ConfirmLogDaoImpl extends ConfirmLogBaseDao implements ConfirmLogDao {

	@Override
	public boolean isExist(String transId) {
		return this.mapper.existPk(transId);
	}

	@Override
	public void addConfirm(String transId) {
		ConfirmLogEntity entity = new ConfirmLogEntity()
				.setTxNo(transId);
		this.mapper.save(entity);
	}
}
