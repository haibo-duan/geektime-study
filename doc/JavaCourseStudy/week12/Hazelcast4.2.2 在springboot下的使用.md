Hazelcast是一款由Hazelcast开发的基于jvm环境的为各种应用提供分布式集群服务的分布式缓存解决方案。可以嵌入到java、c++、.net等开发的产品中使用。
其主要功能有：
- 提供了 Map、Queue、MultiMap、Set、List、Semaphore、Atomic 等接口的分布式实现；
- 提供了基于Topic 实现的消息队列或订阅\发布模式；
- 提供了分布式id生成器（IdGenerator）；
- 提供了分布式事件驱动（Distributed Events）；
- 提供了分布式计算（Distributed Computing）；
- 提供了分布式查询（Distributed Query）;

最简单的一个理解就是，在Hazelcast中创建了一个map之后，在节点A通过put方法添加数据，在节点B就能通过get方法获得该数据。是一个非常好用的分布式缓存。
本文介绍在springboot环境中，如何使用hazelcast。

# 1.pom配置
需要导入的包：
```xml
  <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- https://mvnrepository.com/artifact/com.hazelcast/hazelcast-all -->
        <dependency>
            <groupId>com.hazelcast</groupId>
            <artifactId>hazelcast-all</artifactId>
            <version>4.2.2</version>
        </dependency>
```
这里需要注意的是，在springboot的web环境中来使用的话，spring-boot-starter-cache是必须的。

# 2.yml配置
application.yml中需要指定一个端口：
```yaml
server:
  port: 8080
```
此外，需要创建一个hazelcast.yaml文件。这个文件非常重要，如果没有，那么hazelcast将无法使用。
```yaml
hazelcast:
  network:
    join:
      multicast:
        enabled: true
```
只需要加上上述配置，hazelcast就能使用了。

# 3.javad代码：
定义一个BookService：
```java
package com.dhb.hazelcast.demo.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BookService {

	@Cacheable("books")
	public String getBookNameByIsbn(String isbn) {
		return findBookInSlowSource(isbn);
	}

	private String findBookInSlowSource(String isbn) {
		// some long processing
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
		return "Sample Book Name";
	}
}

```
BookController：
```java
package com.dhb.hazelcast.demo.controller;

import com.dhb.hazelcast.demo.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
@Slf4j
public class BookController {

	private final BookService bookService;

	BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping("/{isbn}")
	public String getBookNameByIsbn(@PathVariable("isbn") String isbn) {
		StopWatch watch = new StopWatch();
		watch.start();
		String result = bookService.getBookNameByIsbn(isbn);
		watch.stop();
		log.info("getBookNameByIsbn cost {} ",watch.prettyPrint());
		return result;
	}
}

```
springboot启动器：
```java
package com.dhb.hazelcast.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class HazelcastDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(HazelcastDemoApplication.class, args);
	}

}

```
现在定义一个启动器，hazelcast就能很好的运行了。

# 4.运行
启动上述springboot的project：
```
2021-10-29 19:37:29.574  INFO 18304 --- [           main] c.d.h.demo.HazelcastDemoApplication      : Starting HazelcastDemoApplication on DESKTOP-HR38DGU with PID 18304 (D:\workspace-mashibing\geektime-study\doc\JavaCourseStudy\week12\hazelcast-demo\target\classes started by Administrator in D:\workspace-mashibing\geektime-study\doc\JavaCourseStudy\week12\hazelcast-demo)
2021-10-29 19:37:29.576  INFO 18304 --- [           main] c.d.h.demo.HazelcastDemoApplication      : No active profile set, falling back to default profiles: default
2021-10-29 19:37:30.570  INFO 18304 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2021-10-29 19:37:30.576  INFO 18304 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2021-10-29 19:37:30.576  INFO 18304 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.35]
2021-10-29 19:37:30.656  INFO 18304 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2021-10-29 19:37:30.656  INFO 18304 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 1053 ms
2021-10-29 19:37:30.776  INFO 18304 --- [           main] c.h.i.config.AbstractConfigLocator       : Loading 'hazelcast.yaml' from the classpath.
2021-10-29 19:37:31.032  INFO 18304 --- [           main] com.hazelcast.system                     : [192.168.87.1]:5701 [hazelcast-cluster] [4.2.2] Hazelcast 4.2.2 (20210811 - c38011e) starting at [192.168.87.1]:5701
2021-10-29 19:37:31.674  INFO 18304 --- [           main] com.hazelcast.instance.impl.Node         : [192.168.87.1]:5701 [hazelcast-cluster] [4.2.2] Using Multicast discovery
2021-10-29 19:37:31.676  WARN 18304 --- [           main] com.hazelcast.cp.CPSubsystem             : [192.168.87.1]:5701 [hazelcast-cluster] [4.2.2] CP Subsystem is not enabled. CP data structures will operate in UNSAFE mode! Please note that UNSAFE mode will not provide strong consistency guarantees.
2021-10-29 19:37:31.787  INFO 18304 --- [           main] c.h.internal.diagnostics.Diagnostics     : [192.168.87.1]:5701 [hazelcast-cluster] [4.2.2] Diagnostics disabled. To enable add -Dhazelcast.diagnostics.enabled=true to the JVM arguments.
2021-10-29 19:37:31.794  INFO 18304 --- [           main] com.hazelcast.core.LifecycleService      : [192.168.87.1]:5701 [hazelcast-cluster] [4.2.2] [192.168.87.1]:5701 is STARTING
2021-10-29 19:37:34.071  INFO 18304 --- [           main] c.h.internal.cluster.ClusterService      : [192.168.87.1]:5701 [hazelcast-cluster] [4.2.2] 

Members {size:1, ver:1} [
	Member [192.168.87.1]:5701 - f66204ea-ff6b-4ec9-be1d-c52be5ad2669 this
]

2021-10-29 19:37:34.097  INFO 18304 --- [           main] com.hazelcast.core.LifecycleService      : [192.168.87.1]:5701 [hazelcast-cluster] [4.2.2] [192.168.87.1]:5701 is STARTED
2021-10-29 19:37:34.212  INFO 18304 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2021-10-29 19:37:34.440  INFO 18304 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoint(s) beneath base path '/actuator'
2021-10-29 19:37:34.470  INFO 18304 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2021-10-29 19:37:34.482  INFO 18304 --- [           main] c.d.h.demo.HazelcastDemoApplication      : Started HazelcastDemoApplication in 5.146 seconds (JVM running for 5.657)
2021-10-29 19:37:35.097  INFO 18304 --- [(3)-10.38.1.240] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2021-10-29 19:37:35.097  INFO 18304 --- [(3)-10.38.1.240] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2021-10-29 19:37:35.101  INFO 18304 --- [(3)-10.38.1.240] o.s.web.servlet.DispatcherServlet        : Completed initialization in 4 ms
```
发送如下请求：
```
curl http://127.0.0.1:8080/books/12345
```
可以看到，第一次请求，需要等待一段时间，大概3秒，而第二次请求，很快就能收到结果。
这说明hazelcust对请求到结果进行了缓存。这样就实现了要给简单的hazel的使用demo。
日志如下：
```
2021-10-29 19:43:29.210  INFO 14032 --- [nio-8080-exec-2] c.h.i.p.impl.PartitionStateManager       : [192.168.87.1]:5701 [hazelcast-cluster] [4.2.2] Initializing cluster partition table arrangement...
2021-10-29 19:43:32.264  INFO 14032 --- [nio-8080-exec-2] c.d.h.demo.controller.BookController     : getBookNameByIsbn cost StopWatch '': running time = 3070475584 ns
---------------------------------------------
ns         %     Task name
---------------------------------------------
3070475584  100%  
 
2021-10-29 19:43:33.938  INFO 14032 --- [nio-8080-exec-4] c.d.h.demo.controller.BookController     : getBookNameByIsbn cost StopWatch '': running time = 2276241 ns
---------------------------------------------
ns         %     Task name
---------------------------------------------
002276241  100%  
 
```

