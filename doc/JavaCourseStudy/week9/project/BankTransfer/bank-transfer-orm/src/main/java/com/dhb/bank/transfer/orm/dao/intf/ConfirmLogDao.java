package com.dhb.bank.transfer.orm.dao.intf;

import cn.org.atool.fluent.mybatis.base.IBaseDao;
import com.dhb.bank.transfer.orm.entity.ConfirmLogEntity;

/**
 * ConfirmLogDao: 数据操作接口
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
public interface ConfirmLogDao extends IBaseDao<ConfirmLogEntity> {

	boolean isExist(String transId);

	void addConfirm(String transId);
}
