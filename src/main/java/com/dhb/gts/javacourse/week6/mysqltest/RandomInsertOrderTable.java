package com.dhb.gts.javacourse.week6.mysqltest;

import com.dhb.gts.javacourse.fluent.dao.intf.OrderDetailDao;
import com.dhb.gts.javacourse.fluent.dao.intf.OrderSummaryDao;
import com.dhb.gts.javacourse.fluent.entity.OrderDetailEntity;
import com.dhb.gts.javacourse.fluent.entity.OrderSummaryEntity;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@Slf4j
public class RandomInsertOrderTable {

	@Autowired
	OrderSummaryDao orderSummaryDao;

	@Autowired
	OrderDetailDao orderDetailDao;


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
		randomBatchInsertOder(totalNumbers, batchSize);
		return "success";
	}

	@RequestMapping("/randomInsertOneOrder")
	public String randomInsertOneOrder() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		int orderNo = getMaxOderNo();
		insertOrder(orderNo+1);
		log.info("randomInsertOneOrder cost time :"+ stopwatch.stop());
		return "success";
	}

	private void randomBatchInsertOder(int totalSize, int batchSize) {
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

	private void batchInsert(List<OrderSummaryEntity> summarys, List<OrderDetailEntity> detials) {
		orderSummaryDao.save(summarys);
		orderDetailDao.save(detials);
	}

	private int getMaxOderNo() {
		return orderSummaryDao.selectMaxOrderNo();
	}

	private void insertOrder(int orderId) {
		OrderDetailEntity orderDetailEntity1 = new OrderDetailEntity()
				.setOrderId(orderId)
				.setProductId(10002)
				.setProductName("手机钢化膜")
				.setPrice(new BigDecimal(14))
				.setNumbers(2)
				.setTotal(new BigDecimal(28))
				.setAverageCost(new BigDecimal(12))
				.setIsValidate(1);
		orderDetailDao.save(orderDetailEntity1);
		OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity()
				.setOrderId(orderId)
				.setProductId(10003)
				.setProductName("手机壳")
				.setPrice(new BigDecimal(7))
				.setNumbers(4)
				.setTotal(new BigDecimal(28))
				.setAverageCost(new BigDecimal(6))
				.setIsValidate(1);
		orderDetailDao.save(orderDetailEntity2);
	}

	private List<OrderSummaryEntity> buildBatchOrderSummary(int orderId, int batchNumber) {
		List<OrderSummaryEntity> orderSummarys = new ArrayList<>();
		for (int i = 0; i < batchNumber; i++) {
			orderId += 1;
			orderSummarys.add(buildOrderSummary(orderId));
		}
		return orderSummarys;
	}

	private List<OrderDetailEntity> buildBatcOrderDetail(int orderId, int batchNumber) {
		List<OrderDetailEntity> orderSummarys = new ArrayList<>();
		for (int i = 0; i < batchNumber; i++) {
			orderId += 1;
			orderSummarys.add(buildOrderDetail1(orderId));
			orderSummarys.add(buildOrderDetail2(orderId));
		}
		return orderSummarys;
	}

	private OrderSummaryEntity buildOrderSummary(int orderId) {
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
				.setPaymentMoney(new BigDecimal(56.00))
				.setOrderAmount(new BigDecimal(56.00))
				.setIsValidate(1);
		return orderSummaryEntity;
	}

	private OrderDetailEntity buildOrderDetail1(int orderId) {
		OrderDetailEntity orderDetailEntity1 = new OrderDetailEntity()
				.setOrderId(orderId)
				.setProductId(10002)
				.setProductName("手机钢化膜")
				.setPrice(new BigDecimal(14))
				.setNumbers(2)
				.setTotal(new BigDecimal(28))
				.setAverageCost(new BigDecimal(12))
				.setIsValidate(1);
		return orderDetailEntity1;
	}

	private OrderDetailEntity buildOrderDetail2(int orderId) {
		OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity()
				.setOrderId(orderId)
				.setProductId(10003)
				.setProductName("手机壳")
				.setPrice(new BigDecimal(7))
				.setNumbers(4)
				.setTotal(new BigDecimal(28))
				.setAverageCost(new BigDecimal(6))
				.setIsValidate(1);
		return orderDetailEntity2;
	}
}
