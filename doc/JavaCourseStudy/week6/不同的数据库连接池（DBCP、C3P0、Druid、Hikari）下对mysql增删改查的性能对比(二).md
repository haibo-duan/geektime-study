第二部分，对insert和update进行测试：

# 1 环境准备
分别准备了Insert和Update两种场景，其中，对于Update场景，还准备了通过索引更新和不走索引更新两种情况。
## 1.1 Update
代码如下：
```
package com.dhb.gts.javacourse.week6.mysqltest;

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
//		int maxOrderId = orderSummaryDao.selectMaxOrderId();
//		int orderId = maxOrderId;
//		if(maxOrderId > 50000) {
//			orderId = random.nextInt(maxOrderId-50000)+50000;
//		}else {
//			orderId = random.nextInt(maxOrderId);
//		}
		int orderId = random.nextInt(2100000-50000)+50000;
		OrderSummaryEntity where = new OrderSummaryEntity()
				.setOrderId(orderId);
		OrderSummaryEntity 	update = new OrderSummaryEntity()
				.setConsigneeAddress("北京市丰台区方庄南路19号")
				.setPaymentMethod(2);
		orderSummaryDao.updateBy(update,where);
		stopwatch.stop();
		System.out.println("通过key修改 key is["+where.getOrderId()+"] cost is ：" + stopwatch);
		return "success";
	}

	@RequestMapping("/randomOneModifyByNo")
	public String randomOneModifyByNo() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		Random random = new Random(System.currentTimeMillis());
//		int maxOrderNo = orderSummaryDao.selectMaxOrderNo();
//		int orderNo = maxOrderNo;
//		if(maxOrderNo > 10000) {
//			orderNo = random.nextInt(maxOrderNo-10000)+5000;
//		}else {
//			orderNo = random.nextInt(maxOrderNo);
//		}
		int orderNo = random.nextInt(1998425-50000)+50000;
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
```
虽然提供了两个方法，一个走索引，一个走全表扫描。但是经过验证，全表扫描耗时特别长，大概5秒左右。因此通过全表扫描的方法来进行负载测试的话已经没有意义。

## 1.2 Insert
insert方法：
```
	@RequestMapping("/randomInsertOneOrder")
	public String randomInsertOneOrder() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		int orderNo = getMaxOderNo();
		insertOrder(orderNo+1);
		log.info("randomInsertOneOrder cost time :"+ stopwatch.stop());
		return "success";
	}
```
利用之前提供的批次插入类中的方法，随机插入一行数据。

# 2.DBCP
## 2.1 update
$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 2 -N 60
```
Starting at 2021/9/14 16:34:26
[Press C to stop the test]
11965   (RPS: 188.1)
---------------Finished!----------------
Finished at 2021/9/14 16:35:30 (took 00:01:03.8028754)
Status 200:    11965

RPS: 195.6 (requests/second)
Max: 270ms
Min: 3ms
Avg: 9.4ms

  50%   below 7ms
  60%   below 8ms
  70%   below 9ms
  80%   below 10ms
  90%   below 14ms
  95%   below 18ms
  98%   below 29ms
  99%   below 60ms
99.9%   below 117ms
```
$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 5 -N 60
```
Starting at 2021/9/14 16:35:46
[Press C to stop the test]
19225   (RPS: 300.8)
---------------Finished!----------------
Finished at 2021/9/14 16:36:50 (took 00:01:04.1057964)
Status 200:    19225

RPS: 314.3 (requests/second)
Max: 285ms
Min: 4ms
Avg: 15ms

  50%   below 11ms
  60%   below 13ms
  70%   below 15ms
  80%   below 18ms
  90%   below 25ms
  95%   below 34ms
  98%   below 62ms
  99%   below 95ms
99.9%   below 153ms

```
$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 10 -N 60
```
Starting at 2021/9/14 16:37:41
[Press C to stop the test]
25655   (RPS: 402.9)
---------------Finished!----------------
Finished at 2021/9/14 16:38:45 (took 00:01:03.7073633)
25656   (RPS: 402.9)                    Status 200:    25656

RPS: 419.5 (requests/second)
Max: 284ms
Min: 4ms
Avg: 22.8ms

  50%   below 17ms
  60%   below 20ms
  70%   below 23ms
  80%   below 28ms
  90%   below 38ms
  95%   below 53ms
  98%   below 94ms
  99%   below 122ms
99.9%   below 213ms
```
$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 20 -N 60
```
Starting at 2021/9/14 16:39:16
[Press C to stop the test]
24877   (RPS: 390.7)
---------------Finished!----------------
Finished at 2021/9/14 16:40:20 (took 00:01:03.7967098)
Status 200:    24877

RPS: 406.7 (requests/second)
Max: 396ms
Min: 4ms
Avg: 47.7ms

  50%   below 38ms
  60%   below 43ms
  70%   below 49ms
  80%   below 58ms
  90%   below 79ms
  95%   below 111ms
  98%   below 152ms
  99%   below 195ms
99.9%   below 312ms

```
## 2.2 Insert

$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 2 -N 60
```
Starting at 2021/9/14 16:40:56
[Press C to stop the test]
6111    (RPS: 96.1)
---------------Finished!----------------
Finished at 2021/9/14 16:42:00 (took 00:01:03.7607069)
Status 200:    6111

RPS: 99.9 (requests/second)
Max: 3529ms
Min: 7ms
Avg: 19ms

  50%   below 16ms
  60%   below 17ms
  70%   below 19ms
  80%   below 21ms
  90%   below 26ms
  95%   below 31ms
  98%   below 37ms
  99%   below 43ms
99.9%   below 80ms
```
$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 5 -N 60
```
Starting at 2021/9/14 16:42:36
[Press C to stop the test]
11722   (RPS: 184.2)
---------------Finished!----------------
Finished at 2021/9/14 16:43:40 (took 00:01:03.8341984)
Status 200:    11722

RPS: 191.6 (requests/second)
Max: 140ms
Min: 8ms
Avg: 25ms

  50%   below 22ms
  60%   below 24ms
  70%   below 28ms
  80%   below 32ms
  90%   below 39ms
  95%   below 47ms
  98%   below 56ms
  99%   below 65ms
99.9%   below 99ms
```
$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 10 -N 60
```
Starting at 2021/9/14 16:44:11
[Press C to stop the test]
15552   (RPS: 243.9)
---------------Finished!----------------
Finished at 2021/9/14 16:45:14 (took 00:01:03.8915896)
Status 200:    15552

RPS: 254.3 (requests/second)
Max: 150ms
Min: 11ms
Avg: 38ms

  50%   below 34ms
  60%   below 38ms
  70%   below 43ms
  80%   below 50ms
  90%   below 60ms
  95%   below 71ms
  98%   below 84ms
  99%   below 95ms
99.9%   below 123ms
```
$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 20 -N 60
```
Starting at 2021/9/14 16:45:45
[Press C to stop the test]
16114   (RPS: 251.9)
---------------Finished!----------------
Finished at 2021/9/14 16:46:49 (took 00:01:04.0614060)
Status 200:    16114

RPS: 263.5 (requests/second)
Max: 216ms
Min: 19ms
Avg: 74ms

  50%   below 69ms
  60%   below 75ms
  70%   below 83ms
  80%   below 93ms
  90%   below 109ms
  95%   below 121ms
  98%   below 139ms
  99%   below 151ms
99.9%   below 180ms
```

# 3 C3P0
## 3.1 update
$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 2 -N 60
```
Starting at 2021/9/14 16:53:46
[Press C to stop the test]
13215   (RPS: 206.5)
---------------Finished!----------------
Finished at 2021/9/14 16:54:50 (took 00:01:04.0950495)
Status 200:    13215

RPS: 216.1 (requests/second)
Max: 230ms
Min: 3ms
Avg: 8.5ms

  50%   below 6ms
  60%   below 7ms
  70%   below 8ms
  80%   below 9ms
  90%   below 12ms
  95%   below 17ms
  98%   below 28ms
  99%   below 50ms
99.9%   below 140ms
```
$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 5 -N 60
```
Starting at 2021/9/14 16:54:57
[Press C to stop the test]
20706   (RPS: 325.5)
---------------Finished!----------------
Finished at 2021/9/14 16:56:00 (took 00:01:03.7819929)
Status 200:    20706

RPS: 338.5 (requests/second)
Max: 211ms
Min: 3ms
Avg: 13.9ms

  50%   below 10ms
  60%   below 11ms
  70%   below 13ms
  80%   below 17ms
  90%   below 23ms
  95%   below 32ms
  98%   below 65ms
  99%   below 95ms
99.9%   below 164ms
```
$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 10 -N 60
```
Starting at 2021/9/14 16:57:40
[Press C to stop the test]
28039   (RPS: 439.3)
---------------Finished!----------------
Finished at 2021/9/14 16:58:44 (took 00:01:03.9884322)
Status 200:    28039

RPS: 458.4 (requests/second)
Max: 383ms
Min: 3ms
Avg: 20.8ms

  50%   below 15ms
  60%   below 18ms
  70%   below 22ms
  80%   below 27ms
  90%   below 38ms
  95%   below 53ms
  98%   below 90ms
  99%   below 121ms
99.9%   below 204ms
```
$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 20 -N 60
```
Starting at 2021/9/14 16:58:52
[Press C to stop the test]
34082   (RPS: 535.1)
---------------Finished!----------------
Finished at 2021/9/14 16:59:56 (took 00:01:03.8173913)
Status 200:    34082

RPS: 557.2 (requests/second)
Max: 2495ms
Min: 4ms
Avg: 34.7ms

  50%   below 24ms
  60%   below 29ms
  70%   below 35ms
  80%   below 44ms
  90%   below 63ms
  95%   below 86ms
  98%   below 134ms
  99%   below 175ms
99.9%   below 367ms
```

## 3.2 Insert
```
$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 2 -N 60
Starting at 2021/9/14 17:01:47
[Press C to stop the test]
7300    (RPS: 114.6)
---------------Finished!----------------
Finished at 2021/9/14 17:02:50 (took 00:01:03.8533378)
Status 200:    7300

RPS: 119.4 (requests/second)
Max: 2204ms
Min: 6ms
Avg: 15.8ms

  50%   below 14ms
  60%   below 15ms
  70%   below 16ms
  80%   below 18ms
  90%   below 22ms
  95%   below 27ms
  98%   below 34ms
  99%   below 40ms
99.9%   below 66ms
```
$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 5 -N 60
```
Starting at 2021/9/14 17:03:56
[Press C to stop the test]
14092   (RPS: 221.5)
---------------Finished!----------------
Finished at 2021/9/14 17:04:59 (took 00:01:03.7887214)
Status 200:    14092

RPS: 230.3 (requests/second)
Max: 107ms
Min: 8ms
Avg: 20.7ms

  50%   below 18ms
  60%   below 20ms
  70%   below 23ms
  80%   below 26ms
  90%   below 31ms
  95%   below 37ms
  98%   below 45ms
  99%   below 52ms
99.9%   below 75ms

``````
$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 10 -N 60
```
Starting at 2021/9/14 17:05:37
[Press C to stop the test]
21272   (RPS: 333.9)
---------------Finished!----------------
Finished at 2021/9/14 17:06:40 (took 00:01:03.8715456)
Status 200:    21272

RPS: 347.8 (requests/second)
Max: 146ms
Min: 7ms
Avg: 27.6ms

  50%   below 24ms
  60%   below 27ms
  70%   below 30ms
  80%   below 36ms
  90%   below 43ms
  95%   below 53ms
  98%   below 63ms
  99%   below 72ms
99.9%   below 114ms
```
$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 20 -N 60
```
Starting at 2021/9/14 17:06:47
[Press C to stop the test]
28066   (RPS: 440.7)
---------------Finished!----------------
Finished at 2021/9/14 17:07:50 (took 00:01:03.8444867)
Status 200:    28066

RPS: 458.8 (requests/second)
Max: 200ms
Min: 9ms
Avg: 42.2ms

  50%   below 37ms
  60%   below 42ms
  70%   below 47ms
  80%   below 54ms
  90%   below 67ms
  95%   below 79ms
  98%   below 97ms
  99%   below 110ms
99.9%   below 158ms

```

# 4.Druid
## 4.1 update
$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 2 -N 60
```
Starting at 2021/9/14 16:10:41
[Press C to stop the test]
13300   (RPS: 209.2)
---------------Finished!----------------
Finished at 2021/9/14 16:11:44 (took 00:01:03.7482328)
Status 200:    13300

RPS: 217.5 (requests/second)
Max: 206ms
Min: 3ms
Avg: 8.4ms

  50%   below 6ms
  60%   below 7ms
  70%   below 8ms
  80%   below 9ms
  90%   below 12ms
  95%   below 17ms
  98%   below 28ms
  99%   below 55ms
99.9%   below 136ms

```
$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 5 -N 60
```
Starting at 2021/9/14 16:12:17
[Press C to stop the test]
21599   (RPS: 338.8)
---------------Finished!----------------
Finished at 2021/9/14 16:13:21 (took 00:01:03.9229244)
Status 200:    21599

RPS: 353.2 (requests/second)
Max: 250ms
Min: 3ms
Avg: 13.3ms

  50%   below 9ms
  60%   below 11ms
  70%   below 13ms
  80%   below 16ms
  90%   below 22ms
  95%   below 30ms
  98%   below 56ms
  99%   below 85ms
99.9%   below 174ms

```
$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 10 -N 60
```
Starting at 2021/9/14 16:14:07
[Press C to stop the test]
25938   (RPS: 406.4)
---------------Finished!----------------
Finished at 2021/9/14 16:15:11 (took 00:01:03.9032480)
Status 200:    25938

RPS: 424.1 (requests/second)
Max: 358ms
Min: 3ms
Avg: 22.6ms

  50%   below 17ms
  60%   below 19ms
  70%   below 23ms
  80%   below 28ms
  90%   below 39ms
  95%   below 56ms
  98%   below 96ms
  99%   below 124ms
99.9%   below 214ms
```

$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 20 -N 60
```
Starting at 2021/9/14 16:16:45
[Press C to stop the test]
27083   (RPS: 425.8)
---------------Finished!----------------
Finished at 2021/9/14 16:17:49 (took 00:01:03.7526193)
Status 200:    27083

RPS: 442.9 (requests/second)
Max: 531ms
Min: 4ms
Avg: 43.8ms

  50%   below 34ms
  60%   below 38ms
  70%   below 44ms
  80%   below 53ms
  90%   below 73ms
  95%   below 106ms
  98%   below 153ms
  99%   below 186ms
99.9%   below 406ms
```
## 4.2 Insert
 sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 2 -N 60
 ```
Starting at 2021/9/14 16:20:58
[Press C to stop the test]
7224    (RPS: 113.7)
---------------Finished!----------------
Finished at 2021/9/14 16:22:02 (took 00:01:03.7367591)
Status 200:    7224

RPS: 118.1 (requests/second)
Max: 3212ms
Min: 6ms
Avg: 16ms

  50%   below 13ms
  60%   below 14ms
  70%   below 16ms
  80%   below 18ms
  90%   below 22ms
  95%   below 27ms
  98%   below 35ms
  99%   below 41ms
99.9%   below 70ms
```
$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 5 -N 60
```
Starting at 2021/9/14 16:22:13
[Press C to stop the test]
14139   (RPS: 222.4)
---------------Finished!----------------
Finished at 2021/9/14 16:23:17 (took 00:01:03.7148356)
Status 200:    14139

RPS: 231.3 (requests/second)
Max: 102ms
Min: 7ms
Avg: 20.6ms

  50%   below 18ms
  60%   below 20ms
  70%   below 22ms
  80%   below 26ms
  90%   below 32ms
  95%   below 39ms
  98%   below 48ms
  99%   below 56ms
99.9%   below 79ms
```
$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 10 -N 60
```
Starting at 2021/9/14 16:23:31
[Press C to stop the test]
18935   (RPS: 296.8)
---------------Finished!----------------
Finished at 2021/9/14 16:24:35 (took 00:01:03.9473026)
Status 200:    18935

RPS: 309.6 (requests/second)
Max: 159ms
Min: 8ms
Avg: 31.1ms

  50%   below 27ms
  60%   below 30ms
  70%   below 35ms
  80%   below 41ms
  90%   below 51ms
  95%   below 60ms
  98%   below 72ms
  99%   below 80ms
99.9%   below 107ms
```
$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 20 -N 60
```
Starting at 2021/9/14 16:25:41
[Press C to stop the test]
20411   (RPS: 320.6)
---------------Finished!----------------
Finished at 2021/9/14 16:26:44 (took 00:01:03.7828596)
Status 200:    20411

RPS: 333.7 (requests/second)
Max: 209ms
Min: 15ms
Avg: 58.3ms

  50%   below 53ms
  60%   below 58ms
  70%   below 66ms
  80%   below 75ms
  90%   below 88ms
  95%   below 101ms
  98%   below 119ms
  99%   below 134ms
99.9%   below 183ms
```

# 5.Hikari
## 5.1 update
$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 2 -N 60
```
Starting at 2021/9/14 15:50:10
[Press C to stop the test]
13091   (RPS: 205.8)
---------------Finished!----------------
Finished at 2021/9/14 15:51:14 (took 00:01:03.7557339)
Status 200:    13091

RPS: 214.1 (requests/second)
Max: 207ms
Min: 3ms
Avg: 8.6ms

  50%   below 6ms
  60%   below 7ms
  70%   below 8ms
  80%   below 10ms
  90%   below 13ms
  95%   below 18ms
  98%   below 30ms
  99%   below 58ms
99.9%   below 106ms
```
$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 5 -N 60
```
Starting at 2021/9/14 15:52:06
[Press C to stop the test]
21052   (RPS: 331)9)
---------------Finished!----------------
Finished at 2021/9/14 15:53:10 (took 00:01:03.7767948)
Status 200:    21052

RPS: 344.2 (requests/second)
Max: 269ms
Min: 3ms
Avg: 13.7ms

  50%   below 10ms
  60%   below 11ms
  70%   below 13ms
  80%   below 16ms
  90%   below 23ms
  95%   below 32ms
  98%   below 61ms
  99%   below 97ms
99.9%   below 152ms
```

$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 10 -N 60
```
Starting at 2021/9/14 15:53:52
[Press C to stop the test]
30200   (RPS: 475)9)
---------------Finished!----------------
Finished at 2021/9/14 15:54:56 (took 00:01:03.7583124)
Status 200:    30200

RPS: 493.7 (requests/second)
Max: 353ms
Min: 3ms
Avg: 19.3ms

  50%   below 14ms
  60%   below 16ms
  70%   below 20ms
  80%   below 25ms
  90%   below 34ms
  95%   below 48ms
  98%   below 84ms
  99%   below 110ms
99.9%   below 226ms
```

$ sb -u http://127.0.0.1:8084/randomOneModifyByKey -c 20 -N 60
```
Starting at 2021/9/14 15:55:43
[Press C to stop the test]
29998   (RPS: 469.5)
---------------Finished!----------------
Finished at 2021/9/14 15:56:47 (took 00:01:03.9178287)
30005   (RPS: 469.6)                    Status 200:    30005

RPS: 490.6 (requests/second)
Max: 359ms
Min: 4ms
Avg: 39.5ms

  50%   below 31ms
  60%   below 35ms
  70%   below 41ms
  80%   below 49ms
  90%   below 66ms
  95%   below 97ms
  98%   below 143ms
  99%   below 171ms
99.9%   below 275ms
```
## 5.2  Insert 
$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 2 -N 60
```
Starting at 2021/9/14 16:00:08
[Press C to stop the test]
7907    (RPS: 124.5)
---------------Finished!----------------
Finished at 2021/9/14 16:01:12 (took 00:01:03.6645126)
Status 200:    7907

RPS: 129.3 (requests/second)
Max: 83ms
Min: 6ms
Avg: 14.6ms

  50%   below 13ms
  60%   below 14ms
  70%   below 15ms
  80%   below 17ms
  90%   below 22ms
  95%   below 26ms
  98%   below 32ms
  99%   below 37ms
99.9%   below 58ms
```
$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 5 -N 60
```
Starting at 2021/9/14 16:02:01
[Press C to stop the test]
13950   (RPS: 219)1)
---------------Finished!----------------
Finished at 2021/9/14 16:03:05 (took 00:01:03.8706084)
Status 200:    13950

RPS: 228.1 (requests/second)
Max: 96ms
Min: 8ms
Avg: 20.9ms

  50%   below 19ms
  60%   below 21ms
  70%   below 23ms
  80%   below 26ms
  90%   below 32ms
  95%   below 38ms
  98%   below 45ms
  99%   below 52ms
99.9%   below 81ms
```
$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 10 -N 60
```
Starting at 2021/9/14 16:03:44
[Press C to stop the test]
20816   (RPS: 327.4)
---------------Finished!----------------
Finished at 2021/9/14 16:04:48 (took 00:01:03.7252700)
Status 200:    20816

RPS: 340.4 (requests/second)
Max: 114ms
Min: 9ms
Avg: 28.3ms

  50%   below 25ms
  60%   below 28ms
  70%   below 31ms
  80%   below 36ms
  90%   below 45ms
  95%   below 53ms
  98%   below 64ms
  99%   below 73ms
99.9%   below 94ms
```
$ sb -u http://127.0.0.1:8084/randomInsertOneOrder -c 20 -N 60
```
Starting at 2021/9/14 16:05:30
[Press C to stop the test]
21108   (RPS: 331.6)
---------------Finished!----------------
Finished at 2021/9/14 16:06:34 (took 00:01:03.8185660)
Status 200:    21108

RPS: 345.1 (requests/second)
Max: 183ms
Min: 17ms
Avg: 56.3ms

  50%   below 52ms
  60%   below 57ms
  70%   below 64ms
  80%   below 73ms
  90%   below 86ms
  95%   below 98ms
  98%   below 112ms
  99%   below 122ms
99.9%   below 144ms
```

# 6.总结
将上述测试结果汇总
## 6.1 update

| 并发数 | DBCP     | C3P0     | Druid    | Hikari   |
|:------|:---------|:---------|:---------|:---------|
| 2C    | 195.6R/S | 216.1R/S | 217.5R/S | 214.1R/S |
| 5C    | 314.3R/S | 325.5R/S | 353.2R/S | 344.2R/S |
| 10C   | 419.5R/S | 458.4R/S | 424.1R/S | 493.7R/S |
| 20C   | 406.7R/S | 557.2R/S | 442.9R/S | 490.6R/S |

可以发现，C3P0的随机update性能是最好的，DBCP的随机UPDATE性能最差。而Hikari会优于Druid。
C3P0 > Hikari >= Druid > DBCP。
## 6.2 insert
| 并发数 | DBCP     | C3P0     | Druid    | Hikari   |
|:------|:---------|:---------|:---------|:---------|
| 2C    | 99.9R/S  | 119.4R/S | 118.1R/S | 129.3R/S |
| 5C    | 184.2R/S | 230.3R/S | 231.3R/S | 228.1R/S |
| 10C   | 254.3R/S | 347.8R/S | 309.6R/S | 340.4R/S |
| 20C   | 263.5R/S | 458.8R/S | 333.7R/S | 345.1R/S |

可以发现，C3P0的随机insert与Hikari大致相同，DBCP的Insert性能最差。Druid优于DBCP。
C3P0 >= Hikari > Druid > DBCP。

# 6.3 select 
回顾之前的select部分的测试结果。

| 测试次数 | DBCP       | C3P0       | Druid      | Hikari     |
|:--------|:-----------|:-----------|:-----------|:-----------|
| 5c      | 1908.2 R/S | 2901.6 R/S | 2865.2 R/S | 3030.2 R/S |
| 10c     | 2482.5 R/S | 3580.4 R/s | 3481.4 R/S | 3680.4 R/S |
| 20c     | 2504.4 R/S | 3192 R/S   | 3322.7 R/S | 3276.1 R/S |
| 30c     | 2288.4 R/S | 2905.8 R/S | 2964.6 R/S | 3106.4 R/S |

Hikari > Druid >= C3P0 > DBCP。
# 6.4 总结
将上述三个场景的测试结果汇总。可以发现：
Hikari的select性能是最好的，再随机update方面弱于C3P0,insert方面与C3P0几乎相同。因此也可以理解为什么springboot中缺省的连接池是Hikari。
Druid 表现比较均衡，居于中上水平，在select、insert、update三方面都属于中上等，考虑到Druid还有丰富的监控功能，因此也是值得在生产环境使用的。
C3P0 虽然其查询select性能与Druid持平，但是在update随机修改场景具有最好的性能。insert场景也不弱。因此在合适的场景也是值得使用的。
DBCP 目前看来这个连接池在三个维度上对比都居于劣势，不知道还有没有其他优势场景没有测到。



