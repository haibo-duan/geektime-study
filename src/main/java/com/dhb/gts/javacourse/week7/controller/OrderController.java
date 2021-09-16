package com.dhb.gts.javacourse.week7.controller;

import com.alibaba.fastjson.JSON;
import com.dhb.gts.javacourse.fluent.entity.OrderSummaryEntity;
import com.dhb.gts.javacourse.week7.service.OrderService;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class OrderController {
	
	@Autowired
	OrderService orderService;

	@RequestMapping("/randomInsertOrderTable")
	public String randomInsertOrderTable(String total, String batch) {
		log.info("randomInsertOrderTable  insert records total is [" + total + "]");

		int totalNumbers = 0;
		int batchSize = 0;
		try {
			totalNumbers = Integer.parseInt(total);
			batchSize = Integer.parseInt(batch);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		orderService.randomBatchInsertOder(totalNumbers, batchSize);
		return "success";
	}

	@RequestMapping("/randomInsertOneOrder")
	public String randomInsertOneOrder() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		int orderNo = orderService.getMaxOderNo();
		orderService.insertOrder(orderNo+1);
		log.info("randomInsertOneOrder cost time :"+ stopwatch.stop());
		return "success";
	}

	@RequestMapping("/queryByKey")
	public String queryByKey(String key) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		Integer orde_id = Integer.parseInt(key);
		OrderSummaryEntity entity = orderService.queryOrderById(orde_id);
		stopwatch.stop();
		System.out.println("通过key查询，走索引耗时：" + stopwatch);
		return JSON.toJSONString(entity);
	}

	@RequestMapping("/queryByOther")
	public String queryByKey(String orderNo, String expressNo) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		List<OrderSummaryEntity> entitys =  orderService.queryOrderByKey(orderNo,expressNo);
		stopwatch.stop();
		System.out.println("不通过key查询，全表扫描耗时：" + stopwatch);
		return JSON.toJSONString(entitys);
	}
}
