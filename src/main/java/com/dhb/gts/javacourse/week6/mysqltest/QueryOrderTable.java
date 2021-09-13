package com.dhb.gts.javacourse.week6.mysqltest;

import com.alibaba.fastjson.JSON;
import com.dhb.gts.javacourse.fluent.dao.intf.OrderSummaryDao;
import com.dhb.gts.javacourse.fluent.entity.OrderSummaryEntity;
import com.dhb.gts.javacourse.fluent.helper.OrderSummaryMapping;
import com.dhb.gts.javacourse.fluent.mapper.OrderDetailMapper;
import com.dhb.gts.javacourse.fluent.mapper.OrderSummaryMapper;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class QueryOrderTable {
	
	@Autowired
	OrderSummaryDao orderSummaryDao;

	@Autowired
	OrderSummaryMapper orderSummaryMapper;

	@Autowired
	OrderDetailMapper orderDetailMapper;


	@RequestMapping("/queryByKey")
	public String queryByKey(String key) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		Integer orde_id = Integer.parseInt(key);
		OrderSummaryEntity entity = orderSummaryDao.selectById(orde_id);
		stopwatch.stop();
		System.out.println("通过key查询，走索引耗时：" + stopwatch);
		return JSON.toJSONString(entity);
	}

	@RequestMapping("/queryByOther")
	public String queryByKey(String orderNo, String expressNo) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		Map<String, Object> map = new HashMap<>();
		if (Strings.isNotEmpty(orderNo)) {
			Integer order_no = Integer.parseInt(orderNo);
			map.put(OrderSummaryMapping.orderNo.column, order_no);
		} else if (Strings.isNotEmpty(expressNo)) {
			map.put(OrderSummaryMapping.expressNo.column, expressNo);
		}
		List<OrderSummaryEntity> entitys = orderSummaryMapper.listByMap(map);
		stopwatch.stop();
		System.out.println("不通过key查询，全表扫描耗时：" + stopwatch);
		return JSON.toJSONString(entitys);
	}
}
