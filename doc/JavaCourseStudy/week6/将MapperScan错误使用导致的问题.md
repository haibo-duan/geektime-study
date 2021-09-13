在前面的代码中，使用fluent mybatis的mapper对表进行增删改查都没有问题。
但是fluent mybatis官方也说了，自动会生成dao层代码，将dao及其实现类都生成好了。因此也想尝试下生成的代码使用的效果。
此外，由于不想建多个project来进行测试，因此对于不同的测试，都在src/main/java下面弄各种不同的包来进行区分。本次测试的package:com.dhb.gts.javacourse.week6.mysqltest目录。

# 1.环境说明
Starter类代码:
```
package com.dhb.gts.javacourse.week6.mysqltest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.dhb.gts.javacourse.fluent"})
public class Starter {

	public static void main(String[] args) {
		SpringApplication.run(Starter.class, args);
	}
}

```
Controller层代码：
```
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
		List<OrderSummaryEntity> entitys = orderSummaryDao.selectByMap(map);
		stopwatch.stop();
		System.out.println("不通过key查询，全表扫描耗时：" + stopwatch);
		return JSON.toJSONString(entitys);
	}
}
```
mapper及dao层代码，通过fluent mybatis生成。
```
package com.dhb.gts.javacourse.week6.mysqltest;


import cn.org.atool.generator.FileGenerator;
import cn.org.atool.generator.annotation.Column;
import cn.org.atool.generator.annotation.Table;
import cn.org.atool.generator.annotation.Tables;
import com.dhb.gts.javacourse.week5.typehandler.MyDateTypeHandler;

import java.util.Date;

public class EntityGenerator {

	// 数据源 url
	static final String url = "jdbc:mysql://192.168.162.49:3306/gts?useUnicode=true&characterEncoding=utf8";
	// 数据库用户名
	static final String username = "gts";
	// 数据库密码
	static final String password = "mysql";

	public static void main(String[] args) {
		// 引用配置类，build方法允许有多个配置类
		FileGenerator.build(Empty.class);
	}


	@Tables(
			// 设置数据库连接信息
			url = url, username = username, password = password,
			// 设置entity类生成src目录, 相对于 user.dir
			srcDir = "src/gens/java",
			// 设置entity类的package值
			basePack = "com.dhb.gts.javacourse.fluent",
			// 设置dao接口和实现的src目录, 相对于 user.dir
			daoDir = "src/gens/java",
			// 设置哪些表要生成Entity文件
			tables = {
					@Table(value = {"T_ORDER_SUMMARY:OrderSummary"},
							columns = {
									@Column(value = "PAYMENT_METHOD", javaType = Integer.class),
									@Column(value = "IS_VALIDATE", javaType = Integer.class)
							}, logicDeleted = "IS_VALIDATE", gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME"),

			}
	)
	static class Empty {
	}
}

```
表结构如下：
```
/*
Navicat MySQL Data Transfer

Source Server         : 192.168.162.49(gts)
Source Server Version : 50717
Source Host           : 192.168.162.49:3306
Source Database       : gts

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2021-09-09 18:11:29
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for T_ORDER_SUMMARY
-- ----------------------------
DROP TABLE IF EXISTS `T_ORDER_SUMMARY`;
CREATE TABLE `T_ORDER_SUMMARY` (
  `ORDER_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `ORDER_NO` int(11) NOT NULL COMMENT '订单编号',
  `CUSTOMER_ID` int(11) NOT NULL COMMENT '下单用户ID',
  `PAYMENT_METHOD` tinyint(4) NOT NULL COMMENT '支付方式：1现金，2余额，3网银，4支付宝，5微信',
  `ORDER_AMOUNT` decimal(8,2) NOT NULL COMMENT '订单汇总金额',
  `PAYMENT_MONEY` decimal(8,2) NOT NULL COMMENT '支付金额',
  `CONSIGNEE_NAME` varchar(50) NOT NULL COMMENT '收货人姓名',
  `CONSIGNEE_ADDRESS` varchar(100) NOT NULL COMMENT '收货人详细',
  `CONSIGNEE_PHONE` varchar(30) NOT NULL COMMENT '收货人联系电话',
  `EXPRESS_COMP` varchar(30) NOT NULL COMMENT '快递公司名称',
  `EXPRESS_NO` varchar(50) NOT NULL COMMENT '快递单号',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `IS_VALIDATE` tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据是否有效标识：1有效数据，2 无效数据',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间',
  PRIMARY KEY (`ORDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单汇总信息表 ';

```

生成的代码都在src/gens/java目录，在build.gradle中配置了这个额外的resource目录。
```
sourceSets {
    main {
        java {
            srcDirs = ['src/main/java', 'src/gens/java']
        }
        resources {
            srcDirs = ['src/main/resources']
        }
    }
    test {
        java {
            srcDirs = ['src/test/java']
        }
        resources {
            srcDirs = ['src/test/resources']
        }
    }
}

```
此外，在application.yml中指定了数据库信息：
```
# 数据源配置
spring.datasource.url: jdbc:mysql://192.168.162.49:3306/gts?useSSL=false&autoReconnect=true&characterEncoding=UTF-8
spring.datasource.username: gts
spring.datasource.password: mysql
spring.datasource.driver-class-name: com.mysql.cj.jdbc.Driver
```

# 2.问题说明
现在启动：
```

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.5.3)

2021-09-13 18:01:57.542  INFO 16620 --- [  restartedMain] c.d.g.j.week6.mysqltest.Starter          : Starting Starter using Java 1.8.0_231 on DESKTOP-HR38DGU with PID 16620 (D:\workspace-mashibing\geektime-study\build\classes\java\main started by Administrator in D:\workspace-mashibing\geektime-study)
2021-09-13 18:01:57.544  INFO 16620 --- [  restartedMain] c.d.g.j.week6.mysqltest.Starter          : No active profile set, falling back to default profiles: default
2021-09-13 18:01:57.585  INFO 16620 --- [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable
2021-09-13 18:01:57.585  INFO 16620 --- [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : For additional web related logging consider setting the 'logging.level.web' property to 'DEBUG'
2021-09-13 18:01:58.530  INFO 16620 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8084 (http)
2021-09-13 18:01:58.537  INFO 16620 --- [  restartedMain] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2021-09-13 18:01:58.537  INFO 16620 --- [  restartedMain] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.50]
2021-09-13 18:01:58.604  INFO 16620 --- [  restartedMain] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2021-09-13 18:01:58.604  INFO 16620 --- [  restartedMain] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1019 ms
2021-09-13 18:01:59.087  INFO 16620 --- [  restartedMain] o.s.b.d.a.OptionalLiveReloadServer       : LiveReload server is running on port 35729
2021-09-13 18:01:59.230  INFO 16620 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8084 (http) with context path ''
2021-09-13 18:01:59.238  INFO 16620 --- [  restartedMain] c.d.g.j.week6.mysqltest.Starter          : Started Starter in 2.001 seconds (JVM running for 2.683)

```
可以看到能成功启动。但是我们再访问一下controller提供的api:
http://127.0.0.1:8084/queryByKey?key=100033

出现如下错误:
```
2021-09-13 18:02:57.507 ERROR 16620 --- [nio-8084-exec-1] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): com.dhb.gts.javacourse.fluent.dao.intf.OrderSummaryDao.mapper] with root cause

org.apache.ibatis.binding.BindingException: Invalid bound statement (not found): com.dhb.gts.javacourse.fluent.dao.intf.OrderSummaryDao.mapper
	at org.apache.ibatis.binding.MapperMethod$SqlCommand.<init>(MapperMethod.java:235) ~[mybatis-3.5.7.jar:3.5.7]
	at org.apache.ibatis.binding.MapperMethod.<init>(MapperMethod.java:53) ~[mybatis-3.5.7.jar:3.5.7]
	at org.apache.ibatis.binding.MapperProxy.lambda$cachedInvoker$0(MapperProxy.java:108) ~[mybatis-3.5.7.jar:3.5.7]
	at java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1688) ~[na:1.8.0_231]
	at org.apache.ibatis.util.MapUtil.computeIfAbsent(MapUtil.java:36) ~[mybatis-3.5.7.jar:3.5.7]
	at org.apache.ibatis.binding.MapperProxy.cachedInvoker(MapperProxy.java:95) ~[mybatis-3.5.7.jar:3.5.7]
	at org.apache.ibatis.binding.MapperProxy.invoke(MapperProxy.java:86) ~[mybatis-3.5.7.jar:3.5.7]
	at com.sun.proxy.$Proxy75.mapper(Unknown Source) ~[na:na]
	at cn.org.atool.fluent.mybatis.base.IBaseDao.selectById(IBaseDao.java:62) ~[fluent-mybatis-1.7.2.jar:na]
	at java.lang.invoke.MethodHandle.invokeWithArguments(MethodHandle.java:627) ~[na:1.8.0_231]
	at org.apache.ibatis.binding.MapperProxy$DefaultMethodInvoker.invoke(MapperProxy.java:159) ~[mybatis-3.5.7.jar:3.5.7]
	at org.apache.ibatis.binding.MapperProxy.invoke(MapperProxy.java:86) ~[mybatis-3.5.7.jar:3.5.7]
	at com.sun.proxy.$Proxy75.selectById(Unknown Source) ~[na:na]
	at com.dhb.gts.javacourse.week6.mysqltest.QueryOrderTable.queryByKey(QueryOrderTable.java:32) ~[main/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_231]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_231]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_231]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_231]
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:197) ~[spring-web-5.3.9.jar:5.3.9]
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:141) ~[spring-web-5.3.9.jar:5.3.9]
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:106) ~[spring-webmvc-5.3.9.jar:5.3.9]
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:895) ~[spring-webmvc-5.3.9.jar:5.3.9]
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:808) ~[spring-webmvc-5.3.9.jar:5.3.9]
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87) ~[spring-webmvc-5.3.9.jar:5.3.9]
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1064) ~[spring-webmvc-5.3.9.jar:5.3.9]
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:963) ~[spring-webmvc-5.3.9.jar:5.3.9]
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006) ~[spring-webmvc-5.3.9.jar:5.3.9]
	at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:898) ~[spring-webmvc-5.3.9.jar:5.3.9]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:655) ~[tomcat-embed-core-9.0.50.jar:4.0.FR]
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883) ~[spring-webmvc-5.3.9.jar:5.3.9]
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:764) ~[tomcat-embed-core-9.0.50.jar:4.0.FR]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:228) ~[tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:163) ~[tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53) ~[tomcat-embed-websocket-9.0.50.jar:9.0.50]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:190) ~[tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:163) ~[tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-5.3.9.jar:5.3.9]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.3.9.jar:5.3.9]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:190) ~[tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:163) ~[tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-5.3.9.jar:5.3.9]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.3.9.jar:5.3.9]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:190) ~[tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:163) ~[tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-5.3.9.jar:5.3.9]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119) ~[spring-web-5.3.9.jar:5.3.9]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:190) ~[tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:163) ~[tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:202) ~[tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:97) [tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:542) [tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:143) [tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92) [tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:78) [tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:357) [tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:382) [tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65) [tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:893) [tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1723) [tomcat-embed-core-9.0.50.jar:9.0.50]
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49) [tomcat-embed-core-9.0.50.jar:9.0.50]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) [na:1.8.0_231]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) [na:1.8.0_231]
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) [tomcat-embed-core-9.0.50.jar:9.0.50]
	at java.lang.Thread.run(Thread.java:748) [na:1.8.0_231]
```

上述代码如果修改为Mapper，而不是使用dao,则能正常访问：
```
	@Autowired
	OrderSummaryMapper orderSummaryMapper;

	@RequestMapping("/queryByKey")
	public String queryByKey(String key) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		Integer orde_id = Integer.parseInt(key);
		OrderSummaryEntity entity = orderSummaryMapper.findById(orde_id);
		stopwatch.stop();
		System.out.println("通过key查询，走索引耗时：" + stopwatch);
		return JSON.toJSONString(entity);
	}
```
上述代码能够正常执行：
http://127.0.0.1:8084/queryByKey?key=100033 返回结果：
```
{"consigneeAddress":"北京市朝阳区西坝河","consigneeName":"张三","consigneePhone":"13888888888","createTime":1631481629000,"customerId":10001,"expressComp":"顺丰","expressNo":"100110978472","isValidate":1,"orderAmount":56.00,"orderId":100033,"orderNo":100034,"paymentMethod":1,"paymentMoney":56.00,"updateTime":1631481629000}
```

# 3.问题分析
仔细检查了代码，发现问题可能存在于Startler的注解。由于生成的dao层代码为于com.dhb.gts.javacourse.fluent.dap目录中。于Starter所在的
com.dhb.gts.javacourse.week6.mysqltest不在同一个目录，因此无法通过扫描下层目录的方式扫描到对应的类，因此只能通过手动配置scan。
那么此时，代码中偷懒了，误认为MapperScan也能扫描dao代码。因此将MapperScan的目录指向了com.dhb.gts.javacourse.fluent。
但是实际上这是一个错误的做法，MapperScan只能用来配置Mapper,而如果要指定Startler之后扫描的目录，则需要在@SpringBootApplication中指定：
代码修改如下：
```
package com.dhb.gts.javacourse.week6.mysqltest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages  = {"com.dhb.gts.javacourse.fluent.dao","com.dhb.gts.javacourse.week6.mysqltest"} )
@MapperScan(basePackages = {"com.dhb.gts.javacourse.fluent.mapper"})
public class Starter {

	public static void main(String[] args) {
		SpringApplication.run(Starter.class, args);
	}
}

```
这样解决了该问题。做了此种修改之后，使用dao层就不会出现问题了。