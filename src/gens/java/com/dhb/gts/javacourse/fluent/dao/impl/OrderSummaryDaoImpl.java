package com.dhb.gts.javacourse.fluent.dao.impl;

import com.dhb.gts.javacourse.fluent.dao.base.OrderSummaryBaseDao;
import com.dhb.gts.javacourse.fluent.dao.intf.OrderSummaryDao;
import com.dhb.gts.javacourse.fluent.wrapper.OrderSummaryQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * OrderSummaryDaoImpl: 数据操作接口实现
 *
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
@Repository
public class OrderSummaryDaoImpl extends OrderSummaryBaseDao implements OrderSummaryDao {

	@Override
	public int selectMaxOrderNo() {
		OrderSummaryQuery query = new OrderSummaryQuery()
				.select.max.orderNo("maxOrderNo")
				.end();
		List<Map<String, Object>> result = mapper.listMaps(query);
		if (result.isEmpty() || null == result.get(0) || null == result.get(0).get("maxOrderNo")) {
			return 0;
		}
		return (Integer) result.get(0).get("maxOrderNo");
	}

	@Override
	public int selectMaxOrderId() {
		OrderSummaryQuery query = new OrderSummaryQuery()
				.select.max.orderId("maxOrderId")
				.end();
		List<Map<String, Object>> result = mapper.listMaps(query);
		if (result.isEmpty() || null == result.get(0) || null == result.get(0).get("maxOrderId")) {
			return 0;
		}
		return (Integer) result.get(0).get("maxOrderId");
	}
}
