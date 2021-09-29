package com.dhb.hmilytcc.bank1.fluent.dao.intf;

import cn.org.atool.fluent.mybatis.base.IBaseDao;
import com.dhb.hmilytcc.bank1.fluent.entity.CancleLogEntity;


/**
 * CancleLogDao: 数据操作接口
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
public interface CancleLogDao extends IBaseDao<CancleLogEntity> {

	boolean isExist(String transId);

	void addCancle(String transId);
	
}
