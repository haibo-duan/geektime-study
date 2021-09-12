package com.dhb.gts.javacourse.week6.mysqltest;

import com.dhb.gts.javacourse.fluent.dao.intf.OrderDetailDao;
import com.dhb.gts.javacourse.fluent.dao.intf.OrderSummaryDao;
import com.dhb.gts.javacourse.fluent.entity.OrderDetailEntity;
import com.dhb.gts.javacourse.fluent.entity.OrderSummaryEntity;
import com.dhb.gts.javacourse.fluent.mapper.OrderDetailMapper;
import com.dhb.gts.javacourse.fluent.mapper.OrderSummaryMapper;
import com.dhb.gts.javacourse.fluent.wrapper.OrderSummaryQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@Slf4j
public class RandomInsertOrderTable {

	@Autowired
	OrderSummaryDao orderSummaryDao;

	@Autowired
	OrderDetailDao orderDetailDao;

	@Autowired
	OrderSummaryMapper orderSummaryMapper;

	@Autowired
	OrderDetailMapper orderDetailMapper;


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

	private void randomBatchInsertOder(int totalSize, int batchSize) {
		int maxOrderNo = getMaxOderNo();
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
			List<OrderSummaryEntity> summarys = buildBatchOrderSummary(orderId, batchSize);
			List<OrderDetailEntity> detials = buildBatcOrderDetail(orderId, batchSize);
			batchInsert(summarys, detials);
			orderId += batchSize;
			log.info("批次[" + i + "]处理完毕...");
		}
		log.info("批量插入数据 totalSize [" + totalSize + "]... 共耗时[" + (System.currentTimeMillis() - start) + "]");
	}

	private void batchInsert(List<OrderSummaryEntity> summarys, List<OrderDetailEntity> detials) {
		orderSummaryMapper.insertBatch(summarys);
		orderDetailMapper.insertBatch(detials);
	}

	private int getMaxOderNo() {
		OrderSummaryQuery query = new OrderSummaryQuery()
				.select.max.orderNo("maxOrderNo")
				.end();
		List<Map<String, Object>> result = orderSummaryMapper.listMaps(query);
		if (result.isEmpty() || null == result.get(0) || null == result.get(0).get("maxOrderNo")) {
			return 0;
		}
		return (Integer) result.get(0).get("maxOrderNo");
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
		orderDetailMapper.insert(orderDetailEntity1);
		OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity()
				.setOrderId(orderId)
				.setProductId(10003)
				.setProductName("手机壳")
				.setPrice(new BigDecimal(7))
				.setNumbers(4)
				.setTotal(new BigDecimal(28))
				.setAverageCost(new BigDecimal(6))
				.setIsValidate(1);
		orderDetailMapper.insert(orderDetailEntity2);
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
