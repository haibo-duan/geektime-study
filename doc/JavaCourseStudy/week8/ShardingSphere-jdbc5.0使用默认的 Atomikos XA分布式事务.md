使用ShardingSphere-jdbc默认的AtomikosXA事务非常简单。配置过程如下：

# 1.需要导入的依赖包
采用springboot 2.5.3版本，采用gradle进行配置，需要导入的包有：
```
implementation 'org.apache.shardingsphere:shardingsphere-jdbc-core-spring-boot-starter:5.0.0-alpha'
//shardingsphere 分布式事务的包
implementation 'org.apache.shardingsphere:shardingsphere-transaction-xa-core:5.0.0-alpha'
```
这样就能将ShardingSphere的相关包导入。

# 2.数据库表结构
t_order_summary 逻辑表，拆分到两个数据 
这样就能将ShardingSphere的相关包导入。

# 2.数据库表结构
t_order_summary 逻辑表，拆分到两个数据库gts01和gts02,库gts01和gts02,然后存在32张表里面：
gts01数据库：
```
+--------------------+
| Tables_in_gts01    |
+--------------------+
| t_order_summary_1  |
| t_order_summary_10 |
| t_order_summary_11 |
| t_order_summary_12 |
| t_order_summary_13 |
| t_order_summary_14 |
| t_order_summary_15 |
| t_order_summary_16 |
| t_order_summary_2  |
| t_order_summary_3  |
| t_order_summary_4  |
| t_order_summary_5  |
| t_order_summary_6  |
| t_order_summary_7  |
| t_order_summary_8  |
| t_order_summary_9  |
+--------------------+
```
gts02数据库：
```
+--------------------+
| Tables_in_gts02    |
+--------------------+
| t_order_summary_1  |
| t_order_summary_10 |
| t_order_summary_11 |
| t_order_summary_12 |
| t_order_summary_13 |
| t_order_summary_14 |
| t_order_summary_15 |
| t_order_summary_16 |
| t_order_summary_2  |
| t_order_summary_3  |
| t_order_summary_4  |
| t_order_summary_5  |
| t_order_summary_6  |
| t_order_summary_7  |
| t_order_summary_8  |
| t_order_summary_9  |
+--------------------+
```
这两个数据库的结构一致。

# 3.shardingSphere配置
分库分表的配置如下：
```
# 配置分库策略
spring.shardingsphere.rules.sharding.default-database-strategy.standard.sharding-column: customer_id
spring.shardingsphere.rules.sharding.default-database-strategy.standard.sharding-algorithm-name: database-inline
#配置 t_order 表规则
spring.shardingsphere.rules.sharding.tables.t_order_summary.actual-data-nodes: gts0$->{1..2}.t_order_summary_$->{1..16}

# 配置分表策略
spring.shardingsphere.rules.sharding.tables.t_order_summary.table-strategy.standard.sharding-column: order_id
spring.shardingsphere.rules.sharding.tables.t_order_summary.table-strategy.standard.sharding-algorithm-name: t-order-inline

# 分布式序列策略配置
spring.shardingsphere.rules.sharding.tables.t_order_summary.key-generate-strategy.column: order_id
spring.shardingsphere.rules.sharding.tables.t_order_summary.key-generate-strategy.key-generator-name: snowflake


spring.shardingsphere.rules.sharding.sharding-algorithms.database-inline.type: inline
spring.shardingsphere.rules.sharding.sharding-algorithms.database-inline.props.algorithm-expression: gts0$->{customer_id % 2 + 1}
spring.shardingsphere.rules.sharding.sharding-algorithms.t-order-inline.type: inline
spring.shardingsphere.rules.sharding.sharding-algorithms.t-order-inline.props.algorithm-expression: t_order_summary_$->{order_id % 16 + 1}


spring.shardingsphere.rules.sharding.key-generators.snowflake.type: snowflake
spring.shardingsphere.rules.sharding.key-generators.snowflake.props.worker-id: 123


# 打开sql输出日志
spring.shardingsphere.props.sql-show: true
#spring.shardingsphere.props.sql-simple: true

logging.level.org.springframework: debug
```
# 4.开启事务
在ShardingSphere种使用分布式的XA事务非常简单，只需要一个注解：
```
@ShardingTransactionType(TransactionType.XA)
@Transactional
```
通过这个注解ShardingSphere就能帮我们实现分布式事务。

# 5.测试代码
现在有如下测试代码：
```
@ShardingTransactionType(TransactionType.XA)
	@Transactional
	public void xaTransactionTest() {
		int max = this.getMaxOderNo();
		OrderSummaryEntity entity1 = new OrderSummaryEntity()
				.setOrderNo(200000001)
				.setConsigneeAddress("北京市朝阳区西坝河")
				.setConsigneeName("张三")
				.setConsigneePhone("13888888888")
				.setCustomerId(11000001)
				.setExpressNo("3400000001")
				.setExpressComp("顺丰")
				.setPaymentMethod(1)
				.setPaymentMoney(5600)
				.setOrderAmount(5600)
				.setIsValidate(1);
		orderSummaryDao.save(entity1);
		OrderSummaryEntity entity2 = new OrderSummaryEntity()
				.setOrderNo(200000002)
				.setConsigneeAddress("北京市朝阳区西坝河")
				.setConsigneeName("张三")
				.setConsigneePhone("13888888888")
				.setCustomerId(11000002)
				.setExpressNo("3400000001")
				.setExpressComp("顺丰")
				.setPaymentMethod(1)
				.setPaymentMoney(5600)
				.setOrderAmount(5600)
				.setIsValidate(1);
		orderSummaryDao.save(entity2);
		OrderSummaryEntity entity3 = new OrderSummaryEntity()
				.setOrderNo(200000003)
				.setConsigneeAddress("北京市朝阳区西坝河")
				.setConsigneeName("张三")
				.setConsigneePhone("13888888888")
				.setCustomerId(11000003)
				.setExpressNo("3400000001")
				.setExpressComp("顺丰")
				.setPaymentMethod(1)
				.setPaymentMoney(5600)
				.setOrderAmount(5600)
				.setIsValidate(1);
		orderSummaryDao.save(entity3);
		int i = 1/0;
	}
	
	
	@RequestMapping("/xaTransactionTest")
	public String xaTransactionTest() {
		orderService.xaTransactionTest();
		log.info("调用异步方法插入一个订单！");
		return "success";
	}
```
插入了3条customer_id都不相同的数据，然后在最后一条数据插入之后，制造一个异常，待这个方法执行之后，我们来查看数据库中上述3条插入操作插入的数据是否都存在，如果不存在，说明数据库已经回滚，分布式事务执行成功。
该方法执行日志如下：
启动时加载了Atomikos日志：
```
2021-09-24 17:53:14.124 [geektime-study] [restartedMain] INFO  [org.apache.shardingsphere.infra.context.schema.SchemaContextsBuilder] -Load meta data for schema logic_db finished, cost 143 milliseconds.
2021-09-24 17:53:14.147 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -Loaded jar:file:/D:/java/gradle-6.9.1/caches/modules-2/files-2.1/com.atomikos/transactions/4.0.6/1ae19829fd88f5467f45dd1bd9713473f008ff53/transactions-4.0.6.jar!/transactions-defaults.properties
2021-09-24 17:53:14.147 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -Loaded jar:file:/D:/java/gradle-6.9.1/caches/modules-2/files-2.1/org.apache.shardingsphere/shardingsphere-transaction-xa-atomikos/5.0.0-alpha/46f2cbba6711f50585bbedff4024b36f2b59d0cd/shardingsphere-transaction-xa-atomikos-5.0.0-alpha.jar!/transactions.properties
2021-09-24 17:53:14.149 [geektime-study] [restartedMain] WARN  [com.atomikos.icatch.provider.imp.AssemblerImp] -Thanks for using Atomikos! Evaluate http://www.atomikos.com/Main/ExtremeTransactions for advanced features and professional support
or register at http://www.atomikos.com/Main/RegisterYourDownload to disable this message and receive FREE tips & advice
Thanks for using Atomikos! Evaluate http://www.atomikos.com/Main/ExtremeTransactions for advanced features and professional support
or register at http://www.atomikos.com/Main/RegisterYourDownload to disable this message and receive FREE tips & advice
2021-09-24 17:53:14.153 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.default_max_wait_time_on_shutdown = 9223372036854775807
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.allow_subtransactions = true
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.recovery_delay = 300000
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.automatic_resource_registration = false
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.oltp_max_retries = 5
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.client_demarcation = false
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.threaded_2pc = false
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.serial_jta_transactions = false
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.log_base_dir = ./logs
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.rmi_export_class = none
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.max_actives = 10000
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.checkpoint_interval = 50000
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.enable_logging = true
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.log_base_name = xa_tx
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.max_timeout = 300000
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.trust_client_tm = false
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: java.naming.factory.initial = com.sun.jndi.rmi.registry.RegistryContextFactory
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.tm_unique_name = 10.38.2.77.tm
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.forget_orphaned_log_entries_delay = 86400000
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.oltp_retry_interval = 10000
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: java.naming.provider.url = rmi://localhost:1099
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.force_shutdown_on_vm_exit = false
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -USING: com.atomikos.icatch.default_jta_timeout = 300000
2021-09-24 17:53:14.154 [geektime-study] [restartedMain] INFO  [com.atomikos.icatch.provider.imp.AssemblerImp] -Using default (local) logging and recovery...
2021-09-24 17:53:14.284 [geektime-study] [restartedMain] INFO  [com.atomikos.datasource.xa.XATransactionalResource] -resource-1-gts01: refreshed XAResource
2021-09-24 17:53:14.373 [geektime-study] [restartedMain] INFO  [com.atomikos.datasource.xa.XATransactionalResource] -resource-2-gts02: refreshed XAResource
```
请求时的出错日志：
```
[ShardingSphere-SQL] -Logic SQL: INSERT INTO `t_order_summary`(`create_time`, `update_time`, `is_validate`, `consignee_address`, `consignee_name`, `consignee_phone`, `customer_id`, `express_comp`, `express_no`, `order_amount`, `order_no`, `payment_method`, `payment_money`) VALUES (now(), now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
2021-09-24 17:53:28.794 [geektime-study] [http-nio-8084-exec-1] INFO  [ShardingSphere-SQL] -SQLStatement: MySQLInsertStatement(setAssignment=Optional.empty, onDuplicateKeyColumns=Optional.empty)
2021-09-24 17:53:28.794 [geektime-study] [http-nio-8084-exec-1] INFO  [ShardingSphere-SQL] -Actual SQL: gts02 ::: INSERT INTO `t_order_summary_1`(`create_time`, `update_time`, `is_validate`, `consignee_address`, `consignee_name`, `consignee_phone`, `customer_id`, `express_comp`, `express_no`, `order_amount`, `order_no`, `payment_method`, `payment_money`, order_id) VALUES (now(), now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ::: [1, 北京市朝阳区西坝河, 张三, 13888888888, 11000001, 顺丰, 3400000001, 5600, 200000001, 1, 5600, 648219653298434048]
2021-09-24 17:53:28.807 [geektime-study] [http-nio-8084-exec-1] INFO  [ShardingSphere-SQL] -Logic SQL: INSERT INTO `t_order_summary`(`create_time`, `update_time`, `is_validate`, `consignee_address`, `consignee_name`, `consignee_phone`, `customer_id`, `express_comp`, `express_no`, `order_amount`, `order_no`, `payment_method`, `payment_money`) VALUES (now(), now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
2021-09-24 17:53:28.807 [geektime-study] [http-nio-8084-exec-1] INFO  [ShardingSphere-SQL] -SQLStatement: MySQLInsertStatement(setAssignment=Optional.empty, onDuplicateKeyColumns=Optional.empty)
2021-09-24 17:53:28.807 [geektime-study] [http-nio-8084-exec-1] INFO  [ShardingSphere-SQL] -Actual SQL: gts01 ::: INSERT INTO `t_order_summary_2`(`create_time`, `update_time`, `is_validate`, `consignee_address`, `consignee_name`, `consignee_phone`, `customer_id`, `express_comp`, `express_no`, `order_amount`, `order_no`, `payment_method`, `payment_money`, order_id) VALUES (now(), now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ::: [1, 北京市朝阳区西坝河, 张三, 13888888888, 11000002, 顺丰, 3400000001, 5600, 200000002, 1, 5600, 648219653810139137]
2021-09-24 17:53:28.816 [geektime-study] [http-nio-8084-exec-1] INFO  [ShardingSphere-SQL] -Logic SQL: INSERT INTO `t_order_summary`(`create_time`, `update_time`, `is_validate`, `consignee_address`, `consignee_name`, `consignee_phone`, `customer_id`, `express_comp`, `express_no`, `order_amount`, `order_no`, `payment_method`, `payment_money`) VALUES (now(), now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
2021-09-24 17:53:28.816 [geektime-study] [http-nio-8084-exec-1] INFO  [ShardingSphere-SQL] -SQLStatement: MySQLInsertStatement(setAssignment=Optional.empty, onDuplicateKeyColumns=Optional.empty)
2021-09-24 17:53:28.816 [geektime-study] [http-nio-8084-exec-1] INFO  [ShardingSphere-SQL] -Actual SQL: gts02 ::: INSERT INTO `t_order_summary_1`(`create_time`, `update_time`, `is_validate`, `consignee_address`, `consignee_name`, `consignee_phone`, `customer_id`, `express_comp`, `express_no`, `order_amount`, `order_no`, `payment_method`, `payment_money`, order_id) VALUES (now(), now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ::: [1, 北京市朝阳区西坝河, 张三, 13888888888, 11000003, 顺丰, 3400000001, 5600, 200000003, 1, 5600, 648219653843693568]
2021-09-24 17:53:28.825 [geektime-study] [http-nio-8084-exec-1] DEBUG [org.springframework.transaction.jta.JtaTransactionManager] -Initiating transaction rollback
2021-09-24 17:53:28.874 [geektime-study] [http-nio-8084-exec-1] DEBUG [org.springframework.web.servlet.DispatcherServlet] -Failed to complete request: java.lang.ArithmeticException: / by zero
2021-09-24 17:53:28.878 [geektime-study] [http-nio-8084-exec-1] ERROR [org.apache.catalina.core.ContainerBase.[Tomcat].[localhost].[/].[dispatcherServlet]] -Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is java.lang.ArithmeticException: / by zero] with root cause
java.lang.ArithmeticException: / by zero
	at com.dhb.gts.javacourse.week8.service.OrderService.xaTransactionTest(OrderService.java:111)
	at com.dhb.gts.javacourse.week8.service.OrderService$$FastClassBySpringCGLIB$$ed0c74f6.invoke(<generated>)
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:779)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.transaction.interceptor.TransactionInterceptor$1.proceedWithInvocation(TransactionInterceptor.java:123)
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:388)
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:119)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.apache.shardingsphere.spring.transaction.ShardingTransactionTypeInterceptor.invoke(ShardingTransactionTypeInterceptor.java:44)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:750)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:692)
	at com.dhb.gts.javacourse.week8.service.OrderService$$EnhancerBySpringCGLIB$$879554d3.xaTransactionTest(<generated>)
	at com.dhb.gts.javacourse.week8.controller.OrderController.xaTransactionTest(OrderController.java:92)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:197)
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:141)
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:106)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:895)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:808)
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1064)
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:963)
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006)
	at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:898)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:655)
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:764)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:228)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:163)
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:190)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:163)
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:190)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:163)
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:190)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:163)
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:190)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:163)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:202)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:97)
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:542)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:143)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:78)
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:357)
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:382)
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65)
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:893)
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1723)
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:748)
2021-09-24 17:53:28.881 [geektime-study] [http-nio-8084-exec-1] DEBUG [org.springframework.web.servlet.DispatcherServlet] -"ERROR" dispatch for GET "/error", parameters={}
2021-09-24 17:53:28.884 [geektime-study] [http-nio-8084-exec-1] DEBUG [org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping] -Mapped to org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController#error(HttpServletRequest)
2021-09-24 17:53:28.904 [geektime-study] [http-nio-8084-exec-1] DEBUG [org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor] -Using 'application/json', given [*/*] and supported [application/json, application/*+json, application/json, application/*+json]
2021-09-24 17:53:28.904 [geektime-study] [http-nio-8084-exec-1] DEBUG [org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor] -Writing [{timestamp=Fri Sep 24 17:53:28 CST 2021, status=500, error=Internal Server Error, trace=java.lang.Ar (truncated)...]
2021-09-24 17:53:29.080 [geektime-study] [http-nio-8084-exec-1] DEBUG [org.springframework.web.servlet.DispatcherServlet] -Exiting from "ERROR" dispatch, status 500
```
可以看到上述三条数据被拆分后分别插入到了两个库
```
gts02 ::: INSERT INTO `t_order_summary_1`
gts01 ::: INSERT INTO `t_order_summary_2`
gts02 ::: INSERT INTO `t_order_summary_1`
```
现在出现了异常，那么事务就会回滚，现在通过查询接口查询上述三条数据的order_id：
```
648219653298434048 
648219653810139137 
648219653843693568
```
通过查询接口：
http://127.0.0.1:8084/queryByKey?key=648219653843693568
http://127.0.0.1:8084/queryByKey?key=648219653810139137
http://127.0.0.1:8084/queryByKey?key=648219653298434048
返回的结果均是空。
在数据库中执行sql语句，也没有检索到这些数据。
