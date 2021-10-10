package com.dhb.bank.transfer.orm.dao.impl;

import com.dhb.bank.transfer.orm.dao.base.TryLogBaseDao;
import com.dhb.bank.transfer.orm.dao.intf.TryLogDao;
import com.dhb.bank.transfer.orm.entity.TryLogEntity;
import org.springframework.stereotype.Repository;

/**
 * TryLogDaoImpl: 数据操作接口实现
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
@Repository
public class TryLogDaoImpl extends TryLogBaseDao implements TryLogDao {

	@Override
	public boolean isExist(String transId) {
		return this.mapper.existPk(transId);
	}

	@Override
	public void addTry(String transId) {
		TryLogEntity entity = new TryLogEntity()
				.setTxNo(transId);
		this.mapper.save(entity);
	}
}
