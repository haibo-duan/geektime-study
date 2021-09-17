在springboot项目中如果需要用到ThreadPoolExecutor线程池的话是非常方便的。比使用java并发包中的Executors都还方便很多。
实际上spring中的线程池ThreadpoolExecutor只是对java并发包中的线程池的封装。这样便于在spring环境中快速使用。通过几个注解即可，降低了对代码的侵入性。

# 1.ThreadPoolExecutor配置
在本文中，将使用spring提供的ThreadPoolTaskExecutor进行配置。
## 1.1 yml中的配置参数
配置参数如下：
```
#线程池配置
async.executor.thread.core_pool_size: 5
# 最大线程数
async.executor.thread.max_pool_size: 20
# 任务队列大小
async.executor.thread.queue_capacity: 100
# 线程池中线程的名称前缀
async.executor.thread.name.prefix: async-service-
# 缓冲队列中线程的空闲时间
async.executor.thread.keep_alive_seconds: 100
```

## 1.2 Executor的Bean
在Spring中通过@Configuration配置一个自定义的Bean，来启动Executor。
```
package com.dhb.gts.javacourse.week7;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class BeanConfig {

	@Value("${async.executor.thread.core_pool_size}")
	private int corePoolSize;
	@Value("${async.executor.thread.max_pool_size}")
	private int maxPoolSize;
	@Value("${async.executor.thread.queue_capacity}")
	private int queueCapacity;
	@Value("${async.executor.thread.name.prefix}")
	private String namePrefix;
	@Value("${async.executor.thread.keep_alive_seconds}")
	private int keepAliveSeconds;

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		// 设置核心线程数
		executor.setCorePoolSize(corePoolSize);
		// 设置最大线程数
		executor.setMaxPoolSize(maxPoolSize);
		// 设置队列容量
		executor.setQueueCapacity(queueCapacity);
		// 设置线程活跃时间（秒）
		executor.setKeepAliveSeconds(keepAliveSeconds);
		// 设置默认线程名称
		executor.setThreadNamePrefix(namePrefix);
		// 设置拒绝策略
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		// 等待所有任务结束后再关闭线程池
		executor.setWaitForTasksToCompleteOnShutdown(true);
		log.info("创建一个线程池 corePoolSize is [" + corePoolSize + "] maxPoolSize is [" + maxPoolSize + "] queueCapacity is [" + queueCapacity +
				"] keepAliveSeconds is [" + keepAliveSeconds + "] namePrefix is [" + namePrefix + "].");
		return executor;
	}
}
```
上述代码中用到的注解：
@Configuration：Spring 容器在启动时，会加载带有 @Configuration 注解的类，对其中带有 @Bean 注解的方法进行处理，实例化一个Bean。
@Bean：是一个方法级别上的注解，用以在代码中配置一个Bean,主要用在 @Configuration 注解的类里，也可以用在 @Component 注解的类里。添加的 bean 的 id 为方法名。
@Value：从yaml或者配置文件中去寻找指定标识的属性值。


# 2.线程的调用方式
使用线程持的时候，通过@Async进行配置，如果@Async不指定执行的线程池name,这个注解配置的方法将会自动提交到一个name为taskExecutor的线程池去执行。
如果taskExecutor不存在，则会去寻找ThreadPoolTaskExecutor类型的对象执行。
线程的调用方式有两种，分别是同步调用和异步调用。本文通过springboot的web项目中来演示。
## 2.1 异步调用
在OrderService中的方法为：
```
	@Async
	public void asyncInsertRandomOrder(){
		Stopwatch stopwatch = Stopwatch.createStarted();
		log.info("开始通过线程持进行异步插入！");
		int orderNo = getMaxOderNo();
		insertOrder(orderNo+1);
		log.info("通过线程池插入完成，共耗时:"+stopwatch.stop());
	}
```
异步调用的方法没有返回值。
现在在OrderController中使用：
```
	@RequestMapping("/asyncInsertRandomOrder")
	public String asyncInsertRandomOrder() {
		orderService.asyncInsertRandomOrder();
		log.info("调用异步方法插入一个订单！");
		return "success";
	}
```
只需要将OrderService注入到OrderController即可使用。

## 2.2 同步调用

在OrderService中的方法为：
```
	@Async
	public ListenableFuture<OrderSummaryEntity> asyncQueryOrderById(int order_id){
		OrderSummaryEntity entity = orderSummaryDao.selectById(order_id);
		return new AsyncResult<>(entity);
	}
```
需要注意的是，同步调用方法需要得到返回值，将通过ListenableFuture类返回。返回的结果是一个AsyncResult对象。
在OrderController中使用：
```
	@RequestMapping("/asyncQueryByKey")
	public String asyncQueryByKey(String key) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		Integer orde_id = Integer.parseInt(key);
		OrderSummaryEntity entity = null;
		try {
			entity = orderService.asyncQueryOrderById(orde_id).get();
		}catch (Exception e) {
			e.printStackTrace();
		}
		stopwatch.stop();
		log.info("通过key查询，走索引耗时：" + stopwatch);
		return JSON.toJSONString(entity);
	}
```
此处需要用get()方法，之后就会被阻塞，等待线程持中的方法执行完成。




