package com.dhb.gts.javacourse.fluent.dao.impl;

import com.dhb.gts.javacourse.fluent.dao.base.CancleLogBaseDao;
import com.dhb.gts.javacourse.fluent.dao.intf.CancleLogDao;
import com.dhb.gts.javacourse.fluent.entity.CancleLogEntity;
import org.springframework.stereotype.Repository;

/**
 * CancleLogDaoImpl: 数据操作接口实现
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
@Repository
public class CancleLogDaoImpl extends CancleLogBaseDao implements CancleLogDao {

	@Override
	public boolean isExist(String transId) {
		return this.mapper.existPk(transId);
	}

	@Override
	public void addCancle(String transId) {
		CancleLogEntity entity = new CancleLogEntity()
				.setTxNo(transId);
		this.mapper.save(entity);
	}
}