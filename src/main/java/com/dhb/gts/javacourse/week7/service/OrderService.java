package com.dhb.gts.javacourse.week7.service;

import com.dhb.gts.javacourse.fluent.dao.intf.OrderDetailDao;
import com.dhb.gts.javacourse.fluent.dao.intf.OrderSummaryDao;
import com.dhb.gts.javacourse.fluent.entity.OrderDetailEntity;
import com.dhb.gts.javacourse.fluent.entity.OrderSummaryEntity;
import com.dhb.gts.javacourse.fluent.helper.OrderSummaryMapping;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class OrderService {
	
	@Autowired
	OrderSummaryDao orderSummaryDao;

	@Autowired
	OrderDetailDao orderDetailDao;
	
	@Async
	public void asyncInsertRandomOrder(){
		Stopwatch stopwatch = Stopwatch.createStarted();
		log.info("开始通过线程持进行异步插入！");
		int orderNo = getMaxOderNo();
		insertOrder(orderNo+1);
		log.info("通过线程池插入完成，共耗时:"+stopwatch.stop());
	}
	
	@Async
	
	public ListenableFuture<OrderSummaryEntity> asyncQueryOrderById(int order_id){
		OrderSummaryEntity entity = orderSummaryDao.selectById(order_id);
		return new AsyncResult<>(entity);
	}
	
	
	public OrderSummaryEntity queryOrderById(int order_id){
		return orderSummaryDao.selectById(order_id);
	}


	public List<OrderSummaryEntity> queryOrderByKey(String orderNo, String expressNo){
		Map<String, Object> map = new HashMap<>();
		if (Strings.isNotEmpty(orderNo)) {
			Integer order_no = Integer.parseInt(orderNo);
			map.put(OrderSummaryMapping.orderNo.column, order_no);
		} else if (Strings.isNotEmpty(expressNo)) {
			map.put(OrderSummaryMapping.expressNo.column, expressNo);
		}
		return orderSummaryDao.selectByMap(map);
	}

	public void randomBatchInsertOder(int totalSize, int batchSize) {
		int maxOrderNo = getMaxOderNo();
		List<Long> costs = Lists.newArrayList();
		log.info("maxOrderNo is [" + maxOrderNo + "]!");
		log.info("数据批流插入开始...");
		long start = System.currentTimeMillis();
		int orderId = maxOrderNo + 1;
		int count = 0;
		if (batchSize != 0 && totalSize != 0) {
			count = totalSize / batchSize;
		}
		for (int i = 0; i <= count; i++) {
			log.info("批次[" + i + "]处理开始...");
			long begin = System.currentTimeMillis();
			List<OrderSummaryEntity> summarys = buildBatchOrderSummary(orderId, batchSize);
			List<OrderDetailEntity> detials = buildBatcOrderDetail(orderId, batchSize);
			batchInsert(summarys, detials);
			orderId += batchSize;
			long cost = System.currentTimeMillis() - begin;
			costs.add(cost);
			log.info("批次[" + i + "]处理完毕,耗时：" + cost + " ms");
		}
		long max = costs.stream().mapToLong(Long::longValue).max().getAsLong();
		long min = costs.stream().mapToLong(Long::longValue).min().getAsLong();
		double average = costs.stream().mapToLong(Long::longValue).average().getAsDouble();
		log.info("总计插入批次共" + costs.size() + "次，其中，最大耗时:" + max + "ms 最小耗时：" + min + "ms 平均耗时:" + average + "ms");
		log.info("批量插入数据 totalSize [" + totalSize + "]... 共耗时[" + (System.currentTimeMillis() - start) + "] ms");
	}
	
	public void batchInsert(List<OrderSummaryEntity> summarys, List<OrderDetailEntity> detials) {
		orderSummaryDao.save(summarys);
		orderDetailDao.save(detials);
	}

	public int getMaxOderNo() {
		return orderSummaryDao.selectMaxOrderNo();
	}

	public void insertOrder(int orderId) {
		OrderDetailEntity orderDetailEntity1 = new OrderDetailEntity()
				.setOrderId(orderId)
				.setProductId(10002)
				.setProductName("手机钢化膜")
				.setPrice(1400)
				.setNumbers(2)
				.setTotal(2800)
				.setAverageCost(1200)
				.setIsValidate(1);
		orderDetailDao.save(orderDetailEntity1);
		OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity()
				.setOrderId(orderId)
				.setProductId(10003)
				.setProductName("手机壳")
				.setPrice(700)
				.setNumbers(4)
				.setTotal(2800)
				.setAverageCost(600)
				.setIsValidate(1);
		orderDetailDao.save(orderDetailEntity2);
	}

	public List<OrderSummaryEntity> buildBatchOrderSummary(int orderId, int batchNumber) {
		List<OrderSummaryEntity> orderSummarys = new ArrayList<>();
		for (int i = 0; i < batchNumber; i++) {
			orderId += 1;
			orderSummarys.add(buildOrderSummary(orderId));
		}
		return orderSummarys;
	}

	public List<OrderDetailEntity> buildBatcOrderDetail(int orderId, int batchNumber) {
		List<OrderDetailEntity> orderSummarys = new ArrayList<>();
		for (int i = 0; i < batchNumber; i++) {
			orderId += 1;
			orderSummarys.add(buildOrderDetail1(orderId));
			orderSummarys.add(buildOrderDetail2(orderId));
		}
		return orderSummarys;
	}

	public OrderSummaryEntity buildOrderSummary(int orderId) {
		Random random = new Random(System.currentTimeMillis());
		int expressNo = random.nextInt(10000000) + 10000000;
		OrderSummaryEntity orderSummaryEntity = new OrderSummaryEntity()
				.setOrderNo(orderId)
				.setConsigneeAddress("北京市朝阳区西坝河")
				.setConsigneeName("张三")
				.setConsigneePhone("13888888888")
				.setCustomerId(10001)
				.setExpressNo("1001" + expressNo)
				.setExpressComp("顺丰")
				.setPaymentMethod(1)
				.setPaymentMoney(5600)
				.setOrderAmount(5600)
				.setIsValidate(1);
		return orderSummaryEntity;
	}

	private OrderDetailEntity buildOrderDetail1(int orderId) {
		OrderDetailEntity orderDetailEntity1 = new OrderDetailEntity()
				.setOrderId(orderId)
				.setProductId(10002)
				.setProductName("手机钢化膜")
				.setPrice(1400)
				.setNumbers(2)
				.setTotal(2800)
				.setAverageCost(1200)
				.setIsValidate(1);
		return orderDetailEntity1;
	}

	private OrderDetailEntity buildOrderDetail2(int orderId) {
		OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity()
				.setOrderId(orderId)
				.setProductId(10003)
				.setProductName("手机壳")
				.setPrice(700)
				.setNumbers(4)
				.setTotal(2800)
				.setAverageCost(600)
				.setIsValidate(1);
		return orderDetailEntity2;
	}
	
}
