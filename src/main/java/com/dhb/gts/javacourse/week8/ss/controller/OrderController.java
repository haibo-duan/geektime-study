package com.dhb.gts.javacourse.week8.ss.controller;

import com.alibaba.fastjson.JSON;
import com.dhb.gts.javacourse.fluent.entity.OrderSummaryEntity;
import com.dhb.gts.javacourse.week8.ss.service.OrderService;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

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

	@RequestMapping("/asyncRandomInsertOrderTable")
	public String asyncRandomInsertOrderTable(String total, String batch) {
		log.info("randomInsertOrderTable  insert records total is [" + total + "] .........");
		int totalNumbers = 0;
		int batchSize = 0;
		try {
			totalNumbers = Integer.parseInt(total);
			batchSize = Integer.parseInt(batch);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		concurrentRandomBatchInsertOrder(totalNumbers, batchSize);
		return "success";
	}
	

	@RequestMapping("/randomInsertOneOrder")
	public String randomInsertOneOrder() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		orderService.insertOrder(orderService.getMaxOderNo() + 1);
		log.info("randomInsertOneOrder cost time :"+ stopwatch.stop());
		return "success";
	}

	@RequestMapping("/queryByKey")
	public String queryByKey(String key) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		Long orde_id = Long.parseLong(key);
		OrderSummaryEntity entity = orderService.queryOrderById(orde_id);
		stopwatch.stop();
		log.info("??????key???????????????????????????" + stopwatch);
		return JSON.toJSONString(entity);
	}

	@RequestMapping("/queryByOther")
	public String queryByKey(String orderNo, String expressNo) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		List<OrderSummaryEntity> entitys =  orderService.queryOrderByKey(orderNo,expressNo);
		stopwatch.stop();
		log.info("?????????key??????????????????????????????" + stopwatch);
		return JSON.toJSONString(entitys);
	}
	
	@RequestMapping("/asyncInsertRandomOrder")
	public String asyncInsertRandomOrder() {
		orderService.asyncInsertRandomOrder();
		log.info("???????????????????????????????????????");
		return "success";
	}

	@RequestMapping("/xaTransactionTest")
	public String xaTransactionTest() {
		orderService.xaTransactionTest();
		log.info("???????????????????????????????????????");
		return "success";
	}
	
	@RequestMapping("/asyncQueryByKey")
	public String asyncQueryByKey(String key) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		Integer orde_id = Integer.parseInt(key);
		OrderSummaryEntity entity = null;
		try {
			entity = orderService.asyncQueryOrderById(orde_id).get();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		stopwatch.stop();
		log.info("??????key???????????????????????????" + stopwatch);
		return JSON.toJSONString(entity);
	}

	private void concurrentRandomBatchInsertOrder(int totalSize, int batchSize) {
		int maxOrderNo = orderService.getMaxOderNo();
		List<Integer> costs = Lists.newArrayList();
		log.info("maxOrderNo is [" + maxOrderNo + "]!");
		log.info("??????????????????????????????...");
		long start = System.currentTimeMillis();
		int count = 0;
		if (batchSize != 0 && totalSize != 0) {
			count = totalSize / batchSize;
		}
		List<Future<Integer>> results = new ArrayList<>();
		for (int i = 0; i <= count; i++) {
			log.info("??????[" + i + "]????????????...");
			results.add(orderService.ansyncInsertOrder(batchSize,maxOrderNo));
			maxOrderNo += batchSize;
		}

		for(int i=0;i<results.size();i++){
			try {
				int cost = results.get(i).get();
				costs.add(cost);
				log.info("??????[" + i + "]????????????,?????????" + cost + " ms");
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		long max = costs.stream().mapToLong(Integer::intValue).max().getAsLong();
		long min = costs.stream().mapToLong(Integer::intValue).min().getAsLong();
		double average = costs.stream().mapToLong(Integer::intValue).average().getAsDouble();
		log.info("?????????????????????" + costs.size() + "???????????????????????????:" + max + "ms ???????????????" + min + "ms ????????????:" + average + "ms");
		log.info("?????????????????? totalSize [" + totalSize + "]... ?????????[" + (System.currentTimeMillis() - start) + "] ms");
	}
}
