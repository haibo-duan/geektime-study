本文尝试在springboot下组建一个3节点的hazelcast集群。基础配置使用参考 [Hazelcast4.2.2 在springboot下的使用](./Hazelcast4.2.2%20在springboot下的使用.md)

# 1.pom配置
需要导入的依赖jar包：
```xml
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

        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

```
# 2.yml配置
hazelcast.yaml 这个配置文件非常重要，hazelcast启动的时候会load这个文件，如果这个文件不存在，或者没有hazelcast相关的配置文件和@Bean,那么将不能启动hazelcast。相关的配置不能放置在application.yml中。目前没有springboot的starter.
```yaml
hazelcast:
  cluster-name: hazelcast-cluster
  network:
    join:
      multicast:
        enabled: true
```
在这个配置文件中，需要指定要给cluster-name。

application.yml
在这个配置文件中需要配置启动tomcat的端口。
在下文中会分别定义三个节点的启动器。那么每次启动的时候都需要将这个文件中的端口修改为不同的端口。
```yaml
server:
  port: 8080
```

# 3.java代码

CommandController：
```java
package com.dhb.hazelcast.demo.controller;

import com.dhb.hazelcast.demo.bean.CommandResponse;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentMap;

@RestController
public class CommandController {

	@Autowired
	private HazelcastInstance hazelcastInstance;

	private ConcurrentMap<String,String> retrieveMap() {
		return hazelcastInstance.getMap("map");
	}

	@PostMapping("/put")
	public CommandResponse put(@RequestParam(value = "key") String key, @RequestParam(value = "value") String value) {
		retrieveMap().put(key, value);
		return new CommandResponse(value);
	}

	@GetMapping("/get")
	public CommandResponse get(@RequestParam(value = "key") String key) {
		String value = retrieveMap().get(key);
		return new CommandResponse(value);
	}

	@GetMapping("/getSize")
	public CommandResponse getSize() {
		int value = retrieveMap().size();
		return new CommandResponse(value + "");
	}
	
}
```
CommandResponse:
```java
package com.dhb.hazelcast.demo.bean;

public class CommandResponse {
	
	private String value;

	public CommandResponse(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

```

HazelcastNode1Starter:
```java
package com.dhb.hazelcast.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class HazelcastNode1Starter {

	public static void main(String[] args) {
		SpringApplication.run(HazelcastNode1Starter.class, args);
	}

}

```
HazelcastNode2Starter:
```java
package com.dhb.hazelcast.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class HazelcastNode2Starter {

	public static void main(String[] args) {
		SpringApplication.run(HazelcastNode2Starter.class, args);
	}

}

```
HazelcastNode3Starter:
```java
package com.dhb.hazelcast.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class HazelcastNode3Starter {

	public static void main(String[] args) {
		SpringApplication.run(HazelcastNode3Starter.class, args);
	}

}

```

# 4.启动测试
现在启动三个Node节点，需要注意的是，每个节点的port都需要配置为不同。
现在将port配置为8081，然后启动HazelcastNode1Starter:
```
2021-10-29 20:08:47.423  INFO 8664 --- [           main] c.h.internal.cluster.ClusterService      : [192.168.87.1]:5701 [hazelcast-cluster] [4.2.2] 

Members {size:1, ver:1} [
	Member [192.168.87.1]:5701 - 92936f5f-6e5d-486a-82a6-7db9d304cf76 this
]

```
可以看到，member中出现了一个成员。默认端口为5701。
然后修改port为8082，启动HazelcastNode2Starter：
```
2021-10-29 20:11:29.087  INFO 15176 --- [ration.thread-1] c.h.internal.cluster.ClusterService      : [192.168.87.1]:5702 [hazelcast-cluster] [4.2.2] 

Members {size:2, ver:4} [
	Member [192.168.87.1]:5701 - 92936f5f-6e5d-486a-82a6-7db9d304cf76
	Member [192.168.87.1]:5702 - 944879dd-1242-4ce5-96c7-efd080372836 this
]
```
可以看到，新加入了一个成员，端口为5702。
现在修改prot为8083，启动HazelcastNode3Starter：
```
2021-10-29 20:12:28.366  INFO 18400 --- [ration.thread-0] c.h.internal.cluster.ClusterService      : [192.168.87.1]:5703 [hazelcast-cluster] [4.2.2] 

Members {size:3, ver:5} [
	Member [192.168.87.1]:5701 - 92936f5f-6e5d-486a-82a6-7db9d304cf76
	Member [192.168.87.1]:5702 - 944879dd-1242-4ce5-96c7-efd080372836
	Member [192.168.87.1]:5703 - 9b75a1ce-c9a1-4ad5-87c9-19c17b3f0ca9 this
]

```
这样一个3节点的集群就启动了。

# 5.访问测试
通过调用post调用如下地址：
```
http://127.0.0.1:8081/put
```
post携带不同key和value数据，就能写入到对应的缓存中。

通过getSize方法可以查看map中的数据：
```
http://127.0.0.1:8081/getSize

{
  "value": "4"
}
```
通过get方法可以拿到之前set进去的key。
```
http://127.0.0.1:8081/get?key=dff

{
  "value": "44534"
}
```

# 6.客户端请求：
定义如下java客户端代码进行测试：
```java
package com.dhb.hazelcast.demo.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class HazelcastGetStartClient {

	public static void main(String[] args) {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.setClusterName("hazelcast-cluster");
		HazelcastInstance instance = HazelcastClient.newHazelcastClient(clientConfig);
		Map<Integer, String> clusterMap = instance.getMap("map");
//		Queue<String> clusterQueue = instance.getQueue("MyQueue");

		log.info(instance.getConfig().toString());
		log.info("Map Value:" + clusterMap.toString());
		log.info("Map Size :" + clusterMap.size());
		instance.shutdown();
	}
}

```
这个代码中最关键的参数是需要设置之前定义的cluster-name "hazelcast-cluster"。
这样就实现了对hazelcast集群中map的调用。

上述过程中，如果关闭任意一个hazelcast节点，上述缓存中的数据都可用。很好的实现了分布式。
在后续文章中，我们将测试，写入100万条数据到hazelcast中，然后关闭某个节点，观察failover的过程。