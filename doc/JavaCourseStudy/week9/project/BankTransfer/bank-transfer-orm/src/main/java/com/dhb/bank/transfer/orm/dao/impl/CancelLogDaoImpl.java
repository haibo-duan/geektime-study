package com.dhb.bank.transfer.orm.dao.impl;

import com.dhb.bank.transfer.orm.dao.base.CancelLogBaseDao;
import com.dhb.bank.transfer.orm.dao.intf.CancelLogDao;
import com.dhb.bank.transfer.orm.entity.CancelLogEntity;
import org.springframework.stereotype.Repository;

/**
 * CancelLogDaoImpl: 数据操作接口实现
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
@Repository
public class CancelLogDaoImpl extends CancelLogBaseDao implements CancelLogDao {

	@Override
	public boolean isExist(String transId) {
		return this.mapper.existPk(transId);
	}

	@Override
	public void addCancel(String transId) {
		CancelLogEntity entity = new CancelLogEntity()
				.setTxNo(transId);
		this.mapper.save(entity);
	}
}
