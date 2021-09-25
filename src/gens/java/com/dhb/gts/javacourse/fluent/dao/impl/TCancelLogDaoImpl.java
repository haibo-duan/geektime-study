package com.dhb.gts.javacourse.fluent.dao.impl;

import com.dhb.gts.javacourse.fluent.dao.base.TCancelLogBaseDao;
import com.dhb.gts.javacourse.fluent.dao.intf.TCancelLogDao;
import org.springframework.stereotype.Repository;

/**
 * TCancelLogDaoImpl: 数据操作接口实现
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
@Repository
public class TCancelLogDaoImpl extends TCancelLogBaseDao implements TCancelLogDao {

	@Override
	public boolean isExist(String transId) {
		return this.mapper.existPk(transId);
	}
}
