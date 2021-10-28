package com.dhb.activemq.demo;

import org.apache.activemq.broker.BrokerService;

public class ActiveMQServer {

	public static void main(String[] args) throws Exception {
		// 创建 broker 服务器
		BrokerService brokerService = new BrokerService();
		// 设置使用 Jmx
		brokerService.setUseJmx(true);
		// 绑定服务地址
		brokerService.addConnector("tcp://localhost:61616");
		// 启动服务器
		brokerService.start();
	}
}
