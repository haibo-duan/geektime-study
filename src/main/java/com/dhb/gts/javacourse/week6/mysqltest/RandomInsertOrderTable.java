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
import java.util.List;
import java.util.Map;

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
	public String randomInsertOrderTable(String number) {
		log.info("randomInsertOrderTable  insert records number is [" + number + "]");

		int batchNumber = 0;
		try {
			batchNumber = Integer.parseInt(number);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		randomBatchInsertOder(batchNumber);
		return "success";
	}

	private void randomBatchInsertOder(int number) {
		int maxOrderNo = getMaxOderNo();
		log.info("maxOrderNo is [" + maxOrderNo + "]!");
		int orderId = maxOrderNo +1;
		insertOrder(orderId);
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
		OrderSummaryEntity orderSummaryEntity = new OrderSummaryEntity()
				.setOrderNo(orderId)
				.setConsigneeAddress("北京市朝阳区西坝河")
				.setConsigneeName("张三")
				.setConsigneePhone("13888888888")
				.setCustomerId(10001)
				.setExpressNo("345454622324")
				.setExpressComp("顺丰")
				.setPaymentMethod(1)
				.setPaymentMoney(new BigDecimal(56.00))
				.setOrderAmount(new BigDecimal(56.00))
				.setIsValidate(1);
		orderSummaryMapper.insert(orderSummaryEntity);

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
}
