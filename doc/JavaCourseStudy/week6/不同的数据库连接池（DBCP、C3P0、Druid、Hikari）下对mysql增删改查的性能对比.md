
分别测试4中连接池（DBCP、C3P0、Druid、Hikari）的表现情况。

# 1.环境准备
## 1.1 连接池配置
### 1.1.1 DBCP
gradle导入包；
```
implementation 'org.apache.commons:commons-dbcp2:2.9.0'
```
application.yml配置：
```
# DBCP
spring.datasource.url: jdbc:mysql://192.168.162.49:3306/gts?useSSL=false&autoReconnect=true&characterEncoding=UTF-8
spring.datasource.username: gts
spring.datasource.password: mysql
spring.datasource.driver-class-name: com.mysql.cj.jdbc.Driver
spring.datasource.type: org.apache.commons.dbcp2.BasicDataSource
```
需要指定spring.datasource.type。

### 1.1.2 C3P0
gradle导入包:
```
implementation 'com.mchange:c3p0:0.9.5.5'
```
application.yml配置：
```
## C3P0
spring.datasource.jdbcUrl: jdbc:mysql://192.168.162.49:3306/gts?useSSL=false&autoReconnect=true&characterEncoding=UTF-8&serverTimezone=UTC
spring.datasource.user: gts
spring.datasource.password: mysql
spring.datasource.driverClass: com.mysql.cj.jdbc.Driver
spring.datasource.type: com.mchange.v2.c3p0.ComboPooledDataSource
```
需要指定spring.datasource.type。
C3P0不能直接复用springDataSource的默认配置，需要单独指定一个类：
```
package com.dhb.gts.javacourse.week6.mysqltest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class C3p0DatasourceConfig {

	@Bean(name = "dataSource")
	@Qualifier(value = "dataSource")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		       return DataSourceBuilder.create().type(com.mchange.v2.c3p0.ComboPooledDataSource.class).build();
	}
	
}
```
### 1.1.3 Druid
gradle导入包：
```
implementation 'com.alibaba:druid:1.2.6'
```
application.yml配置：
```
# 数据源配置 durid
spring.datasource.url: jdbc:mysql://192.168.162.49:3306/gts?useSSL=false&autoReconnect=true&characterEncoding=UTF-8
spring.datasource.username: gts
spring.datasource.password: mysql
spring.datasource.driver-class-name: com.mysql.cj.jdbc.Driver
spring.datasource.type: com.alibaba.druid.pool.DruidDataSource
```
需要指定spring.datasource.type。
### 1.1.4 Hikari
gradle导入包：
```
implementation 'com.zaxxer:HikariCP:4.0.3'
```
由于Hikari是springboot中 tomcat的默认连接池，因此无需指定type。
```
#数据源配置 默认Hikari
spring.datasource.url: jdbc:mysql://192.168.162.49:3306/gts?useSSL=false&autoReconnect=true&characterEncoding=UTF-8
spring.datasource.username: gts
spring.datasource.password: mysql
spring.datasource.driver-class-name: com.mysql.cj.jdbc.Driver
```
### 1.1.5 通用配置
```
spring.datasource.initialSize: 5
spring.datasource.minIdle: 5
spring.datasource.maxActive: 20
spring.datasource.maxWait: 60000
spring.datasource.timeBetweenEvictionRunsMillis: 60000
spring.datasource.validationQuery: SELECT 1
spring.datasource.testWhileIdle: true
spring.datasource.testOnBorrow: false
spring.datasource.testOnReturn: false
spring.datasource.poolPreparedStatements: true
spring.datasource.maxPoolPreparedStatementPerConnectionSize: 20
spring.datasource.filters: stat
```

## 1.2 测试的API
订单查询类:
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

## 1.3 测试工具
通过浏览器和ApacheBench.

# 2.批量写入测试
在数据库存量为100万条数据的基础上，分别写入10万条数据，每个批次为1000。情况如下：

## 2.1 C3P0
http://127.0.0.1:8084/randomInsertOrderTable?total=100000&batch=1000
```
2021-09-14 10:12:38.765  INFO 15256 --- [nio-8084-exec-2] c.d.g.j.w.m.RandomInsertOrderTable       : 总计插入批次共101次，其中，最大耗时:629ms 最小耗时：172ms 平均耗时:260.03960396039605ms
2021-09-14 10:12:38.765  INFO 15256 --- [nio-8084-exec-2] c.d.g.j.w.m.RandomInsertOrderTable       : 批量插入数据 totalSize [100000]... 共耗时[26300] ms
```
http://127.0.0.1:8084/randomInsertOrderTable?total=100000&batch=1000
```
2021-09-14 10:14:08.988  INFO 15256 --- [nio-8084-exec-5] c.d.g.j.w.m.RandomInsertOrderTable       : 总计插入批次共101次，其中，最大耗时:563ms 最小耗时：201ms 平均耗时:274.8217821782178ms
2021-09-14 10:14:08.988  INFO 15256 --- [nio-8084-exec-5] c.d.g.j.w.m.RandomInsertOrderTable       : 批量插入数据 totalSize [100000]... 共耗时[27784] ms
```
## 2.2 DBCP
DBCP:
http://127.0.0.1:8084/randomInsertOrderTable?total=100000&batch=1000
```
2021-09-13 20:40:05.859  INFO 18340 --- [nio-8084-exec-2] c.d.g.j.w.m.RandomInsertOrderTable       : 总计插入批次共101次，其中，最大耗时:364ms 最小耗时：164ms 平均耗时:246.54455445544554ms
2021-09-13 20:40:05.859  INFO 18340 --- [nio-8084-exec-2] c.d.g.j.w.m.RandomInsertOrderTable       : 批量插入数据 totalSize [100000]... 共耗时[24925] ms
```
第二次
```
2021-09-13 20:41:13.099  INFO 18340 --- [nio-8084-exec-5] c.d.g.j.w.m.RandomInsertOrderTable       : 总计插入批次共101次，其中，最大耗时:724ms 最小耗时：192ms 平均耗时:265.96039603960395ms
2021-09-13 20:41:13.099  INFO 18340 --- [nio-8084-exec-5] c.d.g.j.w.m.RandomInsertOrderTable       : 批量插入数据 totalSize [100000]... 共耗时[26891] ms
```

## 2.3 Druid
http://127.0.0.1:8084/randomInsertOrderTable?total=100000&batch=1000
```
2021-09-13 19:50:11.037  INFO 8308 --- [nio-8084-exec-1] c.d.g.j.w.m.RandomInsertOrderTable       : 总计插入批次共101次，其中，最大耗时:684ms 最小耗时：193ms 平均耗时:310.4257425742574ms
2021-09-13 19:50:11.037  INFO 8308 --- [nio-8084-exec-1] c.d.g.j.w.m.RandomInsertOrderTable       : 批量插入数据 totalSize [100000]... 共耗时[31366] ms
```
第二次测试：
```
2021-09-13 19:53:37.951  INFO 8308 --- [nio-8084-exec-6] c.d.g.j.w.m.RandomInsertOrderTable       : 总计插入批次共101次，其中，最大耗时:599ms 最小耗时：176ms 平均耗时:231.1980198019802ms
2021-09-13 19:53:37.951  INFO 8308 --- [nio-8084-exec-6] c.d.g.j.w.m.RandomInsertOrderTable       : 批量插入数据 totalSize [100000]... 共耗时[23367] ms
```

## 2.4 Hikari
http://127.0.0.1:8084/randomInsertOrderTable?total=100000&batch=1000
```
2021-09-13 20:58:46.881  INFO 19084 --- [nio-8084-exec-1] c.d.g.j.w.m.RandomInsertOrderTable       : 总计插入批次共101次，其中，最大耗时:565ms 最小耗时：195ms 平均耗时:270.34653465346537ms
2021-09-13 20:58:46.882  INFO 19084 --- [nio-8084-exec-1] c.d.g.j.w.m.RandomInsertOrderTable       : 批量插入数据 totalSize [100000]... 共耗时[27339] ms
```
http://127.0.0.1:8084/randomInsertOrderTable?total=100000&batch=1000
```
2021-09-13 21:00:35.252  INFO 19084 --- [nio-8084-exec-9] c.d.g.j.w.m.RandomInsertOrderTable       : 总计插入批次共101次，其中，最大耗时:681ms 最小耗时：187ms 平均耗时:263.23762376237624ms
2021-09-13 21:00:35.252  INFO 19084 --- [nio-8084-exec-9] c.d.g.j.w.m.RandomInsertOrderTable       : 批量插入数据 totalSize [100000]... 共耗时[26604] ms
```
## 2.5 汇总

| 测试次数 | C3P0  | DBCP  | Druid | Hikari |
|:--------|:------|:------|:------|:-------|
| 第一次   | 26.3s | 24.9s | 31.3s | 27.3s  |
| 第二次   | 27.7s | 26.8s | 23.3s | 26.6s  |

结论：不同的数据库连接池，在单线程批次insert的时候性能差异不大。

# 3 主键查询
## 3.1 DBCP
http://127.0.0.1:8084/queryByKey?key=100039
```
通过key查询，走索引耗时：4.932 ms
通过key查询，走索引耗时：3.223 ms
通过key查询，走索引耗时：4.227 ms
```
## 3.2 C3P0
http://127.0.0.1:8084/queryByKey?key=100045
```
通过key查询，走索引耗时：2.763 ms
通过key查询，走索引耗时：1.754 ms
通过key查询，走索引耗时：2.151 ms
```
## 3.3 Druid
http://127.0.0.1:8084/queryByKey?key=100036
```
通过key查询，走索引耗时：2.606 ms
通过key查询，走索引耗时：2.303 ms
通过key查询，走索引耗时：1.980 ms
```
## 3.4 Hikari
http://127.0.0.1:8084/queryByKey?key=100042
```
通过key查询，走索引耗时：3.658 ms
通过key查询，走索引耗时：3.548 ms
通过key查询，走索引耗时：2.974 ms
```
## 3.5  汇总

| 测试次数 | DBCP  | C3P0  | Druid | Hikari |
|:--------|:------|:------|:------|:-------|
| 第一次   | 4.9ms | 2.7ms | 2.6ms | 3.6ms  |
| 第二次   | 3.2ms | 1.7ms | 2.3ms | 3.1ms  |
| 第三次   | 4.2ms | 2.1ms | 2.1ms | 2.9ms  |

结论，4种连接池对单次查询的效率影响不大，都在毫秒级，差异主要是由于数据库数据量大小的差异，数据越多可能会导致查询耗时增加。

# 4.全表扫描查询
## 4.1 DBCP
http://127.0.0.1:8084/queryByOther?orderNo=100793
```
不通过key查询，全表扫描耗时：3.108 s
不通过key查询，全表扫描耗时：3.037 s
不通过key查询，全表扫描耗时：3.186 s
```

## 4.2 C3P0
http://127.0.0.1:8084/queryByOther?orderNo=100812
```
不通过key查询，全表扫描耗时：3.936 s
不通过key查询，全表扫描耗时：3.135 s
不通过key查询，全表扫描耗时：3.127 s
```
## 4.3 Druid
http://127.0.0.1:8084/queryByOther?orderNo=100790
```
不通过key查询，全表扫描耗时：2.181 s
不通过key查询，全表扫描耗时：2.633 s
不通过key查询，全表扫描耗时：2.193 s
```
## 4.4 Hikari
http://127.0.0.1:8084/queryByOther?orderNo=100797
```
不通过key查询，全表扫描耗时：3.376 s
不通过key查询，全表扫描耗时：3.453 s
不通过key查询，全表扫描耗时：3.063 s
```
## 4.5 汇总
| 测试次数 | DBCP  | C3P0  | Druid | Hikari |
|:--------|:------|:------|:------|:-------|
| 第一次   | 3.1s | 3.9s | 2.6ms | 3.3s  |
| 第二次   | 3.0s | 3.1s | 2.3ms | 3.4s  |
| 第三次   | 3.1s | 3.1s | 2.1ms | 3.0s  |

结论，如果不通过索引，全表扫描的话，在100万级数据量上进行全表扫描的时间将是通过索引时间的1000倍，这个时间达到了3秒左右。
需要注意的是，上述的测试每次都是测试的不同数据，以避免mysql数据库的缓存。

# 5 采用apachebench 进行负载测试
由于前面每个场景的测试过程中，通过主键的查询效率最高，另外由于mysql在第二次查询的时候，会对数据进行缓存，那么现在可以通过apachebench查询同一条数据，这条数据的性能在走缓存之后，
查询效率是最高的，通过这种方式来对4种连接池进行负载测试，测试结果的差异，就大致可以认为是4种连接池的差异了。
4种连接池的通用配置参数都相同。
## 5.1 DBCP
$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 5 -N 60
```
Starting at 2021/9/13 20:43:53
[Press C to stop the test]
116692  (RPS: 1838.5)
---------------Finished!----------------
Finished at 2021/9/13 20:44:56 (took 00:01:03.6372882)
Status 200:    116692

RPS: 1908.2 (requests/second)
Max: 45ms
Min: 0ms
Avg: 2ms

  50%   below 2ms
  60%   below 2ms
  70%   below 2ms
  80%   below 2ms
  90%   below 3ms
  95%   below 5ms
  98%   below 8ms
  99%   below 11ms
99.9%   below 22ms

```
$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 10 -N 60
```
Starting at 2021/9/13 20:45:35
[Press C to stop the test]
151850  (RPS: 2389.2)
---------------Finished!----------------
Finished at 2021/9/13 20:46:39 (took 00:01:03.7269118)
Status 200:    151850

RPS: 2482.5 (requests/second)
Max: 68ms
Min: 1ms
Avg: 3.4ms

  50%   below 3ms
  60%   below 3ms
  70%   below 3ms
  80%   below 4ms
  90%   below 6ms
  95%   below 8ms
  98%   below 13ms
  99%   below 17ms
99.9%   below 31ms
```
$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 20 -N 60
```
Starting at 2021/9/13 20:46:56
[Press C to stop the test]
153168  (RPS: 2403.7)
---------------Finished!----------------
Finished at 2021/9/13 20:48:00 (took 00:01:03.8479386)
Status 200:    153168

RPS: 2504.4 (requests/second)
Max: 124ms
Min: 1ms
Avg: 7.3ms

  50%   below 6ms
  60%   below 7ms
  70%   below 8ms
  80%   below 9ms
  90%   below 11ms
  95%   below 15ms
  98%   below 20ms
  99%   below 25ms
99.9%   below 55ms
```
$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 30 -N 60
```
Starting at 2021/9/13 20:55:02
[Press C to stop the test]
139871  (RPS: 2198.8)
---------------Finished!----------------
Finished at 2021/9/13 20:56:06 (took 00:01:03.7289551)
Status 200:    139871

RPS: 2288.4 (requests/second)
Max: 416ms
Min: 1ms
Avg: 12.3ms

  50%   below 10ms
  60%   below 11ms
  70%   below 13ms
  80%   below 16ms
  90%   below 21ms
  95%   below 26ms
  98%   below 34ms
  99%   below 41ms
99.9%   below 68ms
```
## 5.2 C3P0
$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 5 -N 60
```
Starting at 2021/9/14 10:16:28
[Press C to stop the test]
177537  (RPS: 2795.5)
---------------Finished!----------------
Finished at 2021/9/14 10:17:31 (took 00:01:03.6954500)
Status 200:    177537

RPS: 2901.6 (requests/second)
Max: 153ms
Min: 0ms
Avg: 1.2ms

  50%   below 1ms
  60%   below 1ms
  70%   below 1ms
  80%   below 1ms
  90%   below 2ms
  95%   below 2ms
  98%   below 4ms
  99%   below 7ms
99.9%   below 16ms
```
$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 10 -N 60
```
Starting at 2021/9/14 10:18:19
[Press C to stop the test]
219027  (RPS: 3436.9)
---------------Finished!----------------
Finished at 2021/9/14 10:19:22 (took 00:01:03.8997966)
Status 200:    219027

RPS: 3580.4 (requests/second)
Max: 186ms
Min: 0ms
Avg: 2.2ms

  50%   below 2ms
  60%   below 2ms
  70%   below 2ms
  80%   below 3ms
  90%   below 4ms
  95%   below 5ms
  98%   below 9ms
  99%   below 12ms
99.9%   below 35ms
```
$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 20 -N 60
```
Starting at 2021/9/14 10:37:44
[Press C to stop the test]
194916  (RPS: 3060.2)
---------------Finished!----------------
Finished at 2021/9/14 10:38:47 (took 00:01:03.7583865)
Status 200:    194916

RPS: 3192 (requests/second)
Max: 164ms
Min: 0ms
Avg: 5.5ms

  50%   below 3ms
  60%   below 4ms
  70%   below 5ms
  80%   below 6ms
  90%   below 9ms
  95%   below 18ms
  98%   below 35ms
  99%   below 45ms
99.9%   below 73ms
```

$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 30 -N 60
```
Starting at 2021/9/14 10:41:14
[Press C to stop the test]
177592  (RPS: 2794.4)
---------------Finished!----------------
Finished at 2021/9/14 10:42:18 (took 00:01:03.6924274)
Status 200:    177594

RPS: 2905.8 (requests/second)
Max: 195ms
Min: 0ms
Avg: 9.3ms

  50%   below 5ms
  60%   below 6ms
  70%   below 8ms
  80%   below 12ms
  90%   below 21ms
  95%   below 34ms
  98%   below 50ms
  99%   below 63ms
99.9%   below 114ms
```
## 5.3 Druid 
$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 5 -N 60
```
Starting at 2021/9/13 20:21:08
[Press C to stop the test]
175187  (RPS: 2755.2)
---------------Finished!----------------
Finished at 2021/9/13 20:22:12 (took 00:01:03.7623277)
Status 200:    175187

RPS: 2864.2 (requests/second)
Max: 38ms
Min: 0ms
Avg: 1.3ms

  50%   below 1ms
  60%   below 1ms
  70%   below 1ms
  80%   below 1ms
  90%   below 2ms
  95%   below 2ms
  98%   below 4ms
  99%   below 7ms
99.9%   below 14ms
```

sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 10 -N 60
```
Starting at 2021/9/13 20:02:50
[Press C to stop the test]
212730  (RPS: 3351)1)
---------------Finished!----------------
Finished at 2021/9/13 20:03:54 (took 00:01:03.5864881)
Status 200:    212730

RPS: 3481.4 (requests/second)
Max: 81ms
Min: 0ms
Avg: 2.2ms

  50%   below 2ms
  60%   below 2ms
  70%   below 2ms
  80%   below 3ms
  90%   below 4ms
  95%   below 5ms
  98%   below 9ms
  99%   below 13ms
99.9%   below 35ms
```
$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 20 -N 60
```
Starting at 2021/9/13 20:11:50
[Press C to stop the test]
203228  (RPS: 3200)1)
---------------Finished!----------------
Finished at 2021/9/13 20:12:54 (took 00:01:03.6655223)
Status 200:    203228

RPS: 3322.7 (requests/second)
Max: 157ms
Min: 0ms
Avg: 5.3ms

  50%   below 4ms
  60%   below 4ms
  70%   below 5ms
  80%   below 6ms
  90%   below 8ms
  95%   below 13ms
  98%   below 27ms
  99%   below 38ms
99.9%   below 70ms
```
$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 30 -N 60
```
Starting at 2021/9/13 20:16:23
[Press C to stop the test]
181003  (RPS: 2844.4)
---------------Finished!----------------
Finished at 2021/9/13 20:17:27 (took 00:01:03.6971351)
Status 200:    181003

RPS: 2964.6 (requests/second)
Max: 177ms
Min: 0ms
Avg: 9.1ms

  50%   below 6ms
  60%   below 7ms
  70%   below 8ms
  80%   below 10ms
  90%   below 16ms
  95%   below 29ms
  98%   below 49ms
  99%   below 61ms
99.9%   below 113ms
```
## 5.4 Hikari
$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 5 -N 60
```
Starting at 2021/9/13 21:06:49
[Press C to stop the test]
185323  (RPS: 2913.2)
---------------Finished!----------------
Finished at 2021/9/13 21:07:53 (took 00:01:03.7726191)
Status 200:    185323

RPS: 3030.2 (requests/second)
Max: 45ms
Min: 0ms
Avg: 1.2ms

  50%   below 1ms
  60%   below 1ms
  70%   below 1ms
  80%   below 1ms
  90%   below 2ms
  95%   below 2ms
  98%   below 4ms
  99%   below 6ms
99.9%   below 15ms
```
$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 10 -N 60
```
Starting at 2021/9/13 21:08:30
[Press C to stop the test]
225182  (RPS: 3543.7)
---------------Finished!----------------
Finished at 2021/9/13 21:09:34 (took 00:01:03.6733168)
Status 200:    225182

RPS: 3683.4 (requests/second)
Max: 81ms
Min: 0ms
Avg: 2.1ms

  50%   below 1ms
  60%   below 2ms
  70%   below 2ms
  80%   below 2ms
  90%   below 4ms
  95%   below 5ms
  98%   below 9ms
  99%   below 13ms
99.9%   below 26ms
```
$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 20 -N 60
```
Starting at 2021/9/13 21:10:13
[Press C to stop the test]
200447  (RPS: 3141.4)
---------------Finished!----------------
Finished at 2021/9/13 21:11:17 (took 00:01:03.9914240)
Status 200:    200447

RPS: 3276.1 (requests/second)
Max: 148ms
Min: 0ms
Avg: 5.3ms

  50%   below 4ms
  60%   below 4ms
  70%   below 5ms
  80%   below 6ms
  90%   below 9ms
  95%   below 14ms
  98%   below 32ms
  99%   below 39ms
99.9%   below 65ms
```
$ sb -u http://127.0.0.1:8084/queryByKey?key=100036 -c 30 -N 60
```
Starting at 2021/9/13 21:12:14
[Press C to stop the test]
189749  (RPS: 2983.5)
---------------Finished!----------------
Finished at 2021/9/13 21:13:17 (took 00:01:03.6197358)
189757  (RPS: 2983.6)                   Status 200:    189757

RPS: 3106.4 (requests/second)
Max: 202ms
Min: 0ms
Avg: 8.7ms

  50%   below 6ms
  60%   below 6ms
  70%   below 8ms
  80%   below 10ms
  90%   below 15ms
  95%   below 29ms
  98%   below 46ms
  99%   below 58ms
99.9%   below 108ms
```

## 5.5 汇总
通过apache bench,分别在5、10、20、30等并发的情况下进行测试：

| 测试次数 | DBCP       | C3P0       | Druid      | Hikari     |
|:--------|:-----------|:-----------|:-----------|:-----------|
| 5c      | 1908.2 R/S | 2901.6 R/S | 2865.2 R/S | 3030.2 R/S |
| 10c     | 2482.5 R/S | 3580.4 R/s | 3481.4 R/S | 3680.4 R/S |
| 20c     | 2504.4 R/S | 3192 R/S   | 3322.7 R/S | 3276.1 R/S |
| 30c     | 2288.4 R/S | 2905.8 R/S | 2964.6 R/S | 3106.4 R/S |

结论：
可以通过上述测试结果发现，Hikari > Druid > C3P0 > DBCP。
实际上Druid与C3P0差距不大。但是Druid具有良好的可监控性，比C3P0更值得推荐。DBCP是表现最差的。

