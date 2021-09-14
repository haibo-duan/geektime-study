package com.dhb.gts.javacourse.week6.mysqltest;

import com.alibaba.fastjson.JSON;
import com.dhb.gts.javacourse.fluent.dao.intf.OrderSummaryDao;
import com.dhb.gts.javacourse.fluent.entity.OrderSummaryEntity;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@Slf4j
public class UpdateOrderTable {

	@Autowired
	OrderSummaryDao orderSummaryDao;

	@RequestMapping("/randomOneModifyByKey")
	public String randomModifyByKey() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		Random random = new Random(System.currentTimeMillis());
		int maxOrderId = orderSummaryDao.selectMaxOrderId();
		int orderId = maxOrderId;
		if(maxOrderId > 10000) {
			orderId = random.nextInt(maxOrderId-10000)+5000;
		}else {
			orderId = random.nextInt(maxOrderId);
		}
		OrderSummaryEntity entity = new OrderSummaryEntity()
				.setOrderId(orderId)
						.setConsigneeAddress("北京市丰台区方庄南路19号")
								.setPaymentMethod(2);
		orderSummaryDao.saveOrUpdate(entity);
		stopwatch.stop();
		System.out.println("通过key修改 key is["+entity.getOrderId()+"] cost is ：" + stopwatch);
		return "success";
	}

	@RequestMapping("/randomOneModifyByNo")
	public String randomOneModifyByNo() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		Random random = new Random(System.currentTimeMillis());
		int maxOrderNo = orderSummaryDao.selectMaxOrderNo();
		int orderNo = maxOrderNo;
		if(maxOrderNo > 10000) {
			orderNo = random.nextInt(maxOrderNo-10000)+5000;
		}else {
			orderNo = random.nextInt(maxOrderNo);
		}
		OrderSummaryEntity where = new OrderSummaryEntity()
				.setOrderNo(orderNo);
		OrderSummaryEntity 	update = new OrderSummaryEntity()
				.setConsigneeAddress("北京市丰台区方庄南路19号")
				.setPaymentMethod(2);
		orderSummaryDao.updateBy(update,where);
		stopwatch.stop();
		System.out.println("通过key修改 orderNo is["+where.getOrderNo()+"] cost is ：" + stopwatch);
		return "success";
	}
	
}
