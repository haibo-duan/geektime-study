# 1.maven配置
project中需要import的代码:
```xml
 <dependencies>
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
            <artifactId>spring-boot-starter-activemq</artifactId>
        </dependency>

        <!-- activeMQ -->
        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-all</artifactId>
            <version>5.16.3</version>
        </dependency>

        <!-- 如果开启activeMQ的连接池，请引入pooled-jms，引入activemq-pool会报错。 -->
        <dependency>
            <groupId>org.messaginghub</groupId>
            <artifactId>pooled-jms</artifactId>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!-- fastjson -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.78</version>
        </dependency>
    </dependencies>
```

# 2.application.yml
```yaml
server:
  port: 8080
  
spring:
  activemq:
    broker-url: tcp://192.168.161.114:61616
    in-memory: false
    pool:
      enabled: true
      max-connections: 5
      idle-timeout: 30000
  jms:
    pub-sub-domain: false
```
# 3.java代码
ActiveMQConfig 类 定义了配置的bean。
```java
@Configuration
public class ActiveMQConfig {

	@Bean
	public Queue queue() {
		return new ActiveMQQueue("activemq-demo.queue");
	}

	@Bean
	public Topic topic() {
		return new ActiveMQTopic("activemq-demo.topic");
	}

	@Bean
	public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ConnectionFactory activeMQConnectionFactory) {
		DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
		bean.setPubSubDomain(true);
		bean.setConnectionFactory(activeMQConnectionFactory);
		return bean;
	}
}
```

Producer：
```java
@Component
@Slf4j
public class Producer {

	@Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;

	@Autowired
	private Queue queue;

	@Autowired
	private Topic topic;

	public void sendMsgToQueue(String msg) {
		log.info("发送消息内容 :" + msg);
		this.jmsMessagingTemplate.convertAndSend(this.queue, msg);
	}

	public void sendMsgToTopic(String msg) {
		log.info("发送消息内容 :" + msg);
		this.jmsMessagingTemplate.convertAndSend(this.topic, msg);
	}
}

```
Consumer:
```java
@Component
@Slf4j
public class Consumer {

	@JmsListener(destination = "activemq-demo.queue")
	public void receiveMsgFromQueue(String text) {
		log.info("queue 接收到消息 : "+text);
	}

	@JmsListener(destination = "activemq-demo.topic",containerFactory="jmsListenerContainerTopic")
	public void receiveMsgFromTopic(String text) {
		log.info("topic 接收到消息 : "+text);
	}
}
```

ActiveMQController:
```java
@RestController
@Slf4j
public class ActiveMQController {
	
	@Autowired
	Producer producer;

	@RequestMapping("/sendQueue")
	@ResponseBody
	public String sendQueue(String msg) {
		producer.sendMsgToQueue(msg);
		return "success";
	}

	@RequestMapping("/sendTopic")
	@ResponseBody
	public String sendTopic(String msg) {
		producer.sendMsgToTopic(msg);
		return "success";
	}
}
```
启动器ActivemqDemoApplication：
```java
@SpringBootApplication
public class ActivemqDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActivemqDemoApplication.class, args);
	}

}
```

# 4.测试
通过postman 请求如下地址即可：
```
http://127.0.0.1:8080/sendQueue?msg=testesfeeg

http://127.0.0.1:8080/sendTopic?msg=testesfeeg
```
日志：

```
2021-10-28 16:58:12.346  INFO 16416 --- [nio-8080-exec-3] com.dhb.activemq.demo.Producer           : 发送消息内容 :testesfeeg
2021-10-28 16:58:12.360  INFO 16416 --- [enerContainer-1] com.dhb.activemq.demo.Consumer           : queue 接收到消息 : testesfeeg
2021-10-28 16:58:15.105  INFO 16416 --- [nio-8080-exec-4] com.dhb.activemq.demo.Producer           : 发送消息内容 :testesfeeg
2021-10-28 16:58:15.116  INFO 16416 --- [enerContainer-1] com.dhb.activemq.demo.Consumer           : topic 接收到消息 : testesfeeg
```

# 5.所遇到的坑
## 5.1 queue和topic只能支持一种方式
在默认的代码中，springboot只能支持一种方式来与activeMQ进行交互，通过如下参数进行切换：
```yaml
spring:
  jms:
    pub-sub-domain: false
```
这个参数如果为false,则为默认值，默认支持Queue,如果为true则支持topic的方式。
如果要两种方式都支持，那么需要指定Consumer中的@JmsListener的containerFactory。
```
@JmsListener(destination = "activemq-demo.topic",containerFactory="jmsListenerContainerTopic")
	public void receiveMsgFromTopic(String text) {
		log.info("topic 接收到消息 : "+text);
	}
```
自定义一个containerFactory:
```
@Bean
	public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ConnectionFactory activeMQConnectionFactory) {
		DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
		bean.setPubSubDomain(true);
		bean.setConnectionFactory(activeMQConnectionFactory);
		return bean;
	}
```

## 5.2 开启连接池之后 出现：JmsMessagingTemplate' that could not be found
当配置开启activeMQ的连接池参数之后:
```yaml
spring:
  activemq:
    pool:
      enabled: true
```
pom中加入了连接池的配置：
```xml
       <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-pool</artifactId>
            <version>5.16.3</version>
        </dependency>
```
但是启动的时候出现如下异常：
```
Error starting ApplicationContext. To display the conditions report re-run your application with 'debug' enabled.
2021-10-28 17:05:06.184 ERROR 11068 --- [           main] o.s.b.d.LoggingFailureAnalysisReporter   : 

***************************
APPLICATION FAILED TO START
***************************

Description:

Field jmsMessagingTemplate in com.dhb.activemq.demo.Producer required a bean of type 'org.springframework.jms.core.JmsMessagingTemplate' that could not be found.

The injection point has the following annotations:
	- @org.springframework.beans.factory.annotation.Autowired(required=true)

The following candidates were found but could not be injected:
	- Bean method 'jmsMessagingTemplate' in 'JmsAutoConfiguration.MessagingTemplateConfiguration' not loaded because Ancestor org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration did not match


Action:

Consider revisiting the entries above or defining a bean of type 'org.springframework.jms.core.JmsMessagingTemplate' in your configuration.

2021-10-28 17:05:06.191  WARN 11068 --- [           main] o.s.boot.SpringApplication               : Unable to close ApplicationContext

org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'springApplicationAdminRegistrar' defined in class path resource [org/springframework/boot/autoconfigure/admin/SpringApplicationAdminJmxAutoConfiguration.class]: Unsatisfied dependency expressed through method 'springApplicationAdminRegistrar' parameter 1; nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'org.springframework.core.env.Environment' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:798) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:539) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1338) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1177) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:557) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:517) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:323) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:226) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:321) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:207) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.context.event.AbstractApplicationEventMulticaster.retrieveApplicationListeners(AbstractApplicationEventMulticaster.java:245) ~[spring-context-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.context.event.AbstractApplicationEventMulticaster.getApplicationListeners(AbstractApplicationEventMulticaster.java:197) ~[spring-context-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:134) ~[spring-context-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:403) ~[spring-context-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:360) ~[spring-context-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.boot.availability.AvailabilityChangeEvent.publish(AvailabilityChangeEvent.java:81) ~[spring-boot-2.3.0.RELEASE.jar:2.3.0.RELEASE]
	at org.springframework.boot.availability.AvailabilityChangeEvent.publish(AvailabilityChangeEvent.java:67) ~[spring-boot-2.3.0.RELEASE.jar:2.3.0.RELEASE]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.doClose(ServletWebServerApplicationContext.java:167) ~[spring-boot-2.3.0.RELEASE.jar:2.3.0.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.close(AbstractApplicationContext.java:978) ~[spring-context-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.boot.SpringApplication.handleRunFailure(SpringApplication.java:814) [spring-boot-2.3.0.RELEASE.jar:2.3.0.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:325) [spring-boot-2.3.0.RELEASE.jar:2.3.0.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1237) [spring-boot-2.3.0.RELEASE.jar:2.3.0.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1226) [spring-boot-2.3.0.RELEASE.jar:2.3.0.RELEASE]
	at com.dhb.activemq.demo.ActivemqDemoApplication.main(ActivemqDemoApplication.java:10) [classes/:na]
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'org.springframework.core.env.Environment' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.raiseNoMatchingBeanFound(DefaultListableBeanFactory.java:1716) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1272) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1226) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:885) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:789) ~[spring-beans-5.2.6.RELEASE.jar:5.2.6.RELEASE]
	... 23 common frames omitted
```

这个异常产生的原因是，activemq-pool中的线程池与springboot中的线程池冲突所致。
需要切换pom中的包为：
```xml
  <dependency>
            <groupId>org.messaginghub</groupId>
            <artifactId>pooled-jms</artifactId>
    </dependency>
```
这样重新启动，上述错误消失，启动成功。