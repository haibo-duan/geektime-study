[toc]

# 1.背景知识

## 1.1 BASE 柔性事物
BASE是基本可用、柔性状态和最终一致性这三个要素的缩写。
- Basically Available (BA) 基本可用 : 保证分布式事务参与方不一定同时在线。
- Soft State (S) 柔性状态 : 则允许系统状态更新有一定的延时，这个延时对客户来说不一定能够察觉。
- Eventual consistency 最终一致性: 通常是通过消息传递的方式保证系统的最终一致性。

区别于传统的ACID事务,传统的ACID中对隔离性的要求很高，在事务执行过程中，必须将所有的资源锁定。柔性事务的理念
则是通过业务逻辑将互斥锁操作从资源层面上移至业务层面。通过放宽对强一致性要求，来换取系
统吞吐量的提升。

## 1.2 BASE柔性事物的常见模式
- TCC 

 通过手动补偿的方式来处理。
 TCC模式是经典的柔性事务解决方案，需要使用者提供 try, confirm, cancel 三个方法， 真正的情况下会执行 try, confirm, 异常情况下会执行try, cancel。 confirm 方法并不是 必须的，完全依赖于用户的try 方法如何去写。 confirm, cancel 2个方法也需要用户去保证幂等性, 这会附加一定的工作量，由于在try方法完成之后，数据已经提交了，因此它并不保证数据的隔离性。但是这样，它的 性能相对较高，一个好的系统设计，是非常适用适用TCC模式。

- AT

 通过自动补偿的方式处理。

## 1.3 hmily
Hmily是一款高性能，零侵入，金融级分布式事务解决方案，目前主要提供柔性事务的支持，包含 TCC, TAC(自动生成回滚SQL) 方案，未来还会支持 XA 等方案。
参考地址：
https://dromara.org/zh/projects/hmily/overview/

## 1.4 案例说明
通过一个转账的案例来体验hmily的使用过程。
有用户A，需要实现在两家银行bankA和bankB之间互相转账。

# 2.数据库设计
## 2.1 账户相关的表
分别设计了两张表：
T_BANK_ACCOUNT 账户表
T_BANK_FREEZE 冻结表

- T_BANK_ACCOUNT
```sql
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_bank_account
-- ----------------------------
DROP TABLE IF EXISTS `t_bank_account`;
CREATE TABLE `t_bank_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键 自增列',
  `customer_id` int(11) NOT NULL COMMENT '用户编号',
  `account_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '账户类型：1 人民币账户，2 美元账户',
  `balance` bigint(20) NOT NULL COMMENT '客户余额 单位 分',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户注册时间',
  `is_validate` tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据是否有效标识：1有效数据，2 无效数据',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
```
- T_BANK_FREEZE
```sql

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_bank_freeze
-- ----------------------------
DROP TABLE IF EXISTS `t_bank_freeze`;
CREATE TABLE `t_bank_freeze` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键 自增列',
  `customer_id` int(11) NOT NULL COMMENT '用户编号',
  `account_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '账户类型：1 人民币账户，2 美元账户',
  `amount` bigint(20) NOT NULL COMMENT '客户余额 单位 分',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_validate` tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据是否有效标识：1有效数据，2 无效数据',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

```
将上述两张表分别在bank1和bank2对应的数据库gts01 和gts02中创建。

## 2.2 TCC相关的表
需要在两个应用的数据库中都创建如下三张TCC相关的表。
```sql
DROP TABLE IF EXISTS `t_try_log`;
CREATE TABLE `t_try_log` (
  `tx_no` varchar(64) NOT NULL COMMENT '事务id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`tx_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `t_confirm_log`;
CREATE TABLE `t_confirm_log` (
                                 `tx_no` varchar(64) NOT NULL COMMENT '事务id',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`tx_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `t_cancel_log`;
CREATE TABLE `t_cancel_log` (
                                `tx_no` varchar(64) NOT NULL COMMENT '事务id',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`tx_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
上述表结构需要在两个应用的数据库gts01和gts02中分别创建。

# 3.Dubbo project配置
由于hmily的TCC过程需要在rpc框架或者spring cloud的模式下才能运行。本文中选择dubbo来实现。

## 3.1 project规划
dubbo项目采用分module的方式来进行。module划分如下：

| module | 描述 |
|:---|:---|
| bank-transfer-api|dubbo的API|
| bank-transfer-orm| 数据库的orm包，采用fluent自动生成|
|bank-transfer-bank1|bank1服务，发起转账的服务|
|bank-transfer-bank2|bank2服务，接受转账的服务|

## 3.2 pom文件
### 3.2.1 父项目的pom文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.3.0.RELEASE</version>
<!--		<relativePath/> &lt;!&ndash; lookup parent from repository &ndash;&gt;-->
	</parent>
	<groupId>com.dhb</groupId>
	<artifactId>bank-transfer</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>bank-transfer</name>
	<packaging>pom</packaging>
	<description>RPC demo project for Spring Boot</description>

	<modules>
		<module>bank-transfer-api</module>
		<module>bank-transfer-orm</module>
		<module>bank-transfer-bank1</module>
		<module>bank-transfer-bank2</module>
		<module>bank-transfer-client</module>
	</modules>

	<properties>
		<java.version>1.8</java.version>
		<spring-boot.version>2.3.0.RELEASE</spring-boot.version>
		<dubbo.version>2.7.7</dubbo.version>
	</properties>

	<dependencyManagement>
		<dependencies>
			<!-- Spring Boot -->
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-dependencies</artifactId>
				<version>${spring-boot.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>

			<!-- Apache Dubbo  -->
			<dependency>
				<groupId>org.apache.dubbo</groupId>
				<artifactId>dubbo-dependencies-bom</artifactId>
				<version>${dubbo.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>

		</dependencies>
	</dependencyManagement>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<!-- lombok -->
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
		</dependency>
		<!-- Dubbo Spring Boot Starter -->
		<dependency>
			<groupId>org.apache.dubbo</groupId>
			<artifactId>dubbo-spring-boot-starter</artifactId>
			<version>${dubbo.version}</version>
		</dependency>

		<!-- Zookeeper dependencies -->
		<dependency>
			<groupId>org.apache.dubbo</groupId>
			<artifactId>dubbo-dependencies-zookeeper</artifactId>
			<version>${dubbo.version}</version>
			<type>pom</type>
			<exclusions>
				<exclusion>
					<groupId>org.slf4j</groupId>
					<artifactId>slf4j-log4j12</artifactId>
				</exclusion>
			</exclusions>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
			<exclusions>
				<exclusion>
					<groupId>org.junit.vintage</groupId>
					<artifactId>junit-vintage-engine</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
	</dependencies>

</project>
```
### 3.2.2  api的pom文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.dhb</groupId>
        <artifactId>bank-transfer</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <!--		<relativePath/> &lt;!&ndash; lookup parent from repository &ndash;&gt;-->
    </parent>
    <groupId>com.dhb</groupId>
    <artifactId>bank-transfer-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>bank-transfer-api</name>
    <description>bank-transfer-api</description>
    <properties>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>hmily-annotation</artifactId>
            <version>2.1.1</version>
        </dependency>
    </dependencies>

</project>
```
### 3.2.3  orm的pom文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.dhb</groupId>
        <artifactId>bank-transfer</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <!--		<relativePath/> &lt;!&ndash; lookup parent from repository &ndash;&gt;-->
    </parent>
    <groupId>com.dhb</groupId>
    <artifactId>bank-transfer-orm</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>bank-transfer-orm</name>
    <description>bank-transfer-orm</description>
    <properties>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.2.0</version>
        </dependency>
        <dependency>
            <groupId>com.github.atool</groupId>
            <artifactId>fluent-mybatis</artifactId>
            <version>1.7.2</version>
        </dependency>
        <dependency>
            <groupId>com.github.atool</groupId>
            <artifactId>fluent-mybatis-processor</artifactId>
            <version>1.7.2</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.25</version>
        </dependency>
    </dependencies>
</project>
```
### 3.2.4  bank1的pom文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.dhb</groupId>
        <artifactId>bank-transfer</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <!--		<relativePath/> &lt;!&ndash; lookup parent from repository &ndash;&gt;-->
    </parent>
    <groupId>com.dhb</groupId>
    <artifactId>bank-transfer-bank1</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>bank-transfer-bank1</name>
    <description>bank-transfer-bank1</description>
    <properties>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>com.dhb</groupId>
            <artifactId>bank-transfer-api</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.dhb</groupId>
            <artifactId>bank-transfer-orm</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>hmily-spring-boot-starter-dubbo</artifactId>
            <version>2.1.1</version>
            <exclusions>
                <exclusion>
                    <groupId>org.dromara</groupId>
                    <artifactId>hmily-repository-mongodb</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-api</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>log4j</groupId>
                    <artifactId>log4j</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        
        <dependency>
            <groupId>javax.validation</groupId>
            <artifactId>validation-api</artifactId>
            <version>1.1.0.Final</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```
### 3.2.5  bank2的pom文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.dhb</groupId>
        <artifactId>bank-transfer</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <!--		<relativePath/> &lt;!&ndash; lookup parent from repository &ndash;&gt;-->
    </parent>
    <groupId>com.dhb</groupId>
    <artifactId>bank-transfer-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>bank-transfer-client</name>
    <description>bank-transfer-client</description>
    <properties>
        <java.version>1.8</java.version>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>
    <dependencies>
        <dependency>
            <groupId>com.dhb</groupId>
            <artifactId>bank-transfer-api</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>2.7.8</version>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```
### 3.2.6  client的pom文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.dhb</groupId>
        <artifactId>bank-transfer</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <!--		<relativePath/> &lt;!&ndash; lookup parent from repository &ndash;&gt;-->
    </parent>
    <groupId>com.dhb</groupId>
    <artifactId>bank-transfer-bank1</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>bank-transfer-bank1</name>
    <description>bank-transfer-bank1</description>
    <properties>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>com.dhb</groupId>
            <artifactId>bank-transfer-api</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.dhb</groupId>
            <artifactId>bank-transfer-orm</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.dromara</groupId>
            <artifactId>hmily-spring-boot-starter-dubbo</artifactId>
            <version>2.1.1</version>
            <exclusions>
                <exclusion>
                    <groupId>org.dromara</groupId>
                    <artifactId>hmily-repository-mongodb</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-api</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>log4j</groupId>
                    <artifactId>log4j</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        
        <dependency>
            <groupId>javax.validation</groupId>
            <artifactId>validation-api</artifactId>
            <version>1.1.0.Final</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```
## 3.3 yml配置文件
### 3.3.1 bank1的yml配置
```yaml
server:
  port: 8088

spring:
  application:
    name: bank-transfer-bank1

dubbo:
  scan:
    base-packages: com.dhb.bank.transfer.bank1
  protocol:
    name: dubbo
    port: 12345
  registry:
    address: zookeeper://localhost:2181
  metadata-report:
    address: zookeeper://localhost:2181
  application:
    qosEnable: true
    qosPort: 22222
    qosAcceptForeignIp: true
    qos-enable-compatible: true
    qos-host-compatible: localhost
    qos-port-compatible: 22222
    qos-accept-foreign-ip-compatible: true
    qos-host: localhost


#数据源配置 默认Hikari
spring.datasource.url: jdbc:mysql://192.168.161.114:3306/gts01?useSSL=false&autoReconnect=true&characterEncoding=UTF-8&serverTimezone=UTC
spring.datasource.username: gts
spring.datasource.password: ******
spring.datasource.driver-class-name: com.mysql.cj.jdbc.Driver

```
### 3.3.2  bank2的yml配置
```yaml
server:
  port: 8089

spring:
  application:
    name: bank-transfer-bank2

dubbo:
  scan:
    base-packages: com.dhb.bank.transfer.bank2
  protocol:
    name: dubbo
    port: 12346
  registry:
    address: zookeeper://localhost:2181
  metadata-report:
    address: zookeeper://localhost:2181
  application:
    qosEnable: true
    qosPort: 22222
    qosAcceptForeignIp: true
    qos-enable-compatible: true
    qos-host-compatible: localhost
    qos-port-compatible: 22222
    qos-accept-foreign-ip-compatible: true
    qos-host: localhost


#数据源配置 默认Hikari
spring.datasource.url: jdbc:mysql://192.168.161.114:3306/gts02?useSSL=false&autoReconnect=true&characterEncoding=UTF-8&serverTimezone=UTC
spring.datasource.username: gts
spring.datasource.password: ******
spring.datasource.driver-class-name: com.mysql.cj.jdbc.Driver
```
### 3.3.3  lient的yml配置
```yaml

spring:
  application:
    name: bank-transfer-client
  main:
    allow-bean-definition-overriding: true
    web-application-type: none
dubbo:
  scan:
    base-packages: com.dhb.bank.transfer.client
  registry:
    address: zookeeper://localhost:2181
  metadata-report:
    address: zookeeper://localhost:2181
```
# 4.hmily配置
hmily需要单独指定一个配置文件。
需要单独创建一个hmily的配置数据库，这个数据库只需要创建db就行，数据库表hmily会自动创建。
```sql
create database hmily DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
grant all privileges on gts.* to gts@’%.%.%.%’ identified by ‘******’;
flush privileges;
```
##4.1 bank1的hmily配置
```yaml

hmily:
  server:
    configMode: local
    appName: bank-transfer-bank1
  #  如果server.configMode eq local 的时候才会读取到这里的配置信息.
  config:
    appName: bank-transfer-bank1
    serializer: kryo
    contextTransmittalMode: threadLocal
    scheduledThreadMax: 16
    scheduledRecoveryDelay: 60
    scheduledCleanDelay: 60
    scheduledPhyDeletedDelay: 600
    scheduledInitDelay: 30
    recoverDelayTime: 60
    cleanDelayTime: 180
    limit: 200
    retryMax: 10
    bufferSize: 8192
    consumerThreads: 16
    asyncRepository: true
    autoSql: true
    phyDeleted: true
    storeDays: 3
    repository: mysql

repository:
  database:
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://192.168.161.114:3306/hmily?useSSL=false&autoReconnect=true&characterEncoding=UTF-8&serverTimezone=UTC
    username: gts
    password: mysql
    maxActive: 20
    minIdle: 10
    connectionTimeout: 30000
    idleTimeout: 600000
    maxLifetime: 1800000

metrics:
  metricsName: prometheus
  host:
  port: 9070
  async: true
  threadCount : 16
  jmxConfig:

```
##4.2 bank2的hmily配置
```yaml

hmily:
  server:
    configMode: local
    appName: bank-transfer-bank2
  #  如果server.configMode eq local 的时候才会读取到这里的配置信息.
  config:
    appName: bank-transfer-bank2
    serializer: kryo
    contextTransmittalMode: threadLocal
    scheduledThreadMax: 16
    scheduledRecoveryDelay: 60
    scheduledCleanDelay: 60
    scheduledPhyDeletedDelay: 600
    scheduledInitDelay: 30
    recoverDelayTime: 60
    cleanDelayTime: 180
    limit: 200
    retryMax: 10
    bufferSize: 8192
    consumerThreads: 16
    asyncRepository: true
    autoSql: true
    phyDeleted: true
    storeDays: 3
    repository: mysql

repository:
  database:
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://192.168.161.114:3306/hmily?useSSL=false&autoReconnect=true&characterEncoding=UTF-8&serverTimezone=UTC
    username: gts
    password: mysql
    maxActive: 20
    minIdle: 10
    connectionTimeout: 30000
    idleTimeout: 600000
    maxLifetime: 1800000

metrics:
  metricsName: prometheus
  host:
  port: 9071
  async: true
  threadCount : 16
  jmxConfig:
```
# 5.实现代码
## 5.1 API
```java
public interface Bank1Service {
	
	@Hmily
	Boolean transfer(String tid,int customerId,int amount);
}

public interface Bank2Service {

	@Hmily
	Boolean transfer(String tid,int customerId,int amount);
}
```

## 5.2 bank1实现
Bank1ServiceImpl
```java
@DubboService(version = "1.0.0", tag = "red", weight = 100)
public class Bank1ServiceImpl implements Bank1Service {
	
	@Autowired
	BankAccountService bankAccountService;

	@Override
	public Boolean transfer(String tid,int customerId, int amount) {
		this.bankAccountService.subtractAccountBalance(tid,customerId,amount);
		return true;
	}
}
```
Bank1AccountServiceImpl
```java
@Slf4j
@Component
public class BankAccountServiceImpl implements BankAccountService {

	private static AtomicInteger confrimCount = new AtomicInteger(0);

	@Autowired
	BankAccountDao bankAccountDao;

	@Autowired
	BankFreezeDao bankFreezeDao;

	@Autowired
	TryLogDao tryLogDao;

	@Autowired
	ConfirmLogDao confirmLogDao;

	@Autowired
	CancelLogDao cancelLogDao;

	@DubboReference(version = "1.0.0")
	Bank2Service bank2Service;

	@Override
	@HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
	public void subtractAccountBalance(String tid, int customerId, int amount) {
		log.info("bank1 subtractAccountBalance try begin ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		//幂等判断 判断t_try_log表中是否有try日志记录，如果有则不再执行
		if (tryLogDao.isExist(tid)) {
			log.info("bank1 中，tid 为 {} 的try操作已执行。直接退出。。", tid);
			return;
		}
		//悬挂处理：如果cancel、confirm有一个已经执行了，try不再执行
		if (confirmLogDao.isExist(tid) || cancelLogDao.isExist(tid)) {
			log.info("bank1 中，tid 为 {} 的cancel 或者 confirm 操作已执行。直接退出。。", tid);
			return;
		}
		//扣减金额 冻结操作
		if (bankAccountDao.subtractAccountBalance(customerId, 1, amount)) {
			log.info("bank1 中，账户 {} 扣减金额 {} 成功 tid is {} ！！！", customerId, amount, tid);
			//插入try操作日志
			tryLogDao.addTry(tid);
			log.info("bank1 中插入try log ...");
		} else {
			throw new HmilyRuntimeException("账户扣减异常！");
		}
		//调用bank2 发起转账
		try {
			bank2Service.transfer(tid,customerId, amount);
		} catch (Exception e) {
			throw new HmilyRuntimeException("远程调用异常！");
		}
		log.info("bank1 subtractAccountBalance try end ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
	}

	@Override
	public void addAccountBalance(String tid, int customerId, int amount) {
		return;
	}


	@Transactional(rollbackFor = Exception.class)
	public boolean confirm(String tid, int customerId, int amount) {
		log.info("bank1 confirm begin ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		//幂等校验 如果confirm操作未执行，才能去除冻结金额，否则什么也不做。。
		if (!confirmLogDao.isExist(tid)) {
			//只有try操作完成之后，且cancel操作未执行的情况下，才允许执行confirm
			if (tryLogDao.isExist(tid) && !cancelLogDao.isExist(tid)) {
				//解除冻结金额
				log.info("bank1 confirm 操作中，解除冻结金额成功!");
				bankFreezeDao.subtractFreezeAmount(customerId, 1, amount);
				//写入confirm日志
				log.info("bank1 confirm 操作中，增加confirm日志!");
				confirmLogDao.addConfirm(tid);
			}
		}
		log.info("bank1 confirm end ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		return Boolean.TRUE;
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean cancel(String tid, int customerId, int amount) {
		log.info("bank1 cancel begin ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		//幂等校验 只有当cancel操作未执行的情况下，才执行cancel，否则什么也不做。
		if (!cancelLogDao.isExist(tid)) {
			//空回滚操作，如果try操作未执行，那么cancel什么也不做，当且仅当try执行之后，才能执行cancel
			if (tryLogDao.isExist(tid)) {
				//cancel操作，需要判断confirm是否执行了
				//如果此时confirm还未执行，那么需要将冻结金额清除
				if (!confirmLogDao.isExist(tid)) {
					log.info("bank1 cancel 操作中，解除冻结金额成功!");
					bankFreezeDao.subtractFreezeAmount(customerId, 1, amount);
				}
				log.info("bank1  cancel 操作中，增加账户余额成功!");
				bankAccountDao.addAccountBalance(customerId, 1, amount);
				//增加cancel log
				cancelLogDao.addCancel(tid);
			}
		}
		log.info("bank1 cancel end ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		return Boolean.TRUE;
	}
}
```
Bank1的启动器
```java
@SpringBootApplication(scanBasePackages = {"com.dhb.bank.transfer.orm.dao", "com.dhb.bank.transfer.bank1"})
@MapperScan(basePackages = {"com.dhb.bank.transfer.orm.mapper"})
public class DubboServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DubboServerApplication.class, args);
	}

}
```

## 5.3 bank2实现
Bank2ServiceImpl
```java

@DubboService(version = "1.0.0", tag = "red", weight = 100)
public class Bank2ServiceImpl implements Bank2Service {

	@Autowired
	BankAccountService bankAccountService;

	@Override
	public Boolean transfer(String tid,int customerId, int amount) {
		this.bankAccountService.addAccountBalance(tid,customerId,amount);
		return true;
	}
}
```
BankAccountServiceImpl
```java
@Component
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {


	@Autowired
	BankAccountDao bankAccountDao;
	
	@Autowired
	TryLogDao tryLogDao;

	@Autowired
	ConfirmLogDao confirmLogDao;

	@Autowired
	CancelLogDao cancelLogDao;
	
	@Override
	public void subtractAccountBalance(String tid,int customerId, int amount) {

	}

	@Override
	@HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
	public void addAccountBalance(String tid,int customerId, int amount) {
		log.info("bank2 addAccountBalance try begin ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		//幂等校验 
		if(!tryLogDao.isExist(tid)) {
			tryLogDao.addTry(tid);
		}
		log.info("bank2 addAccountBalance try end ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);

	}


	@Transactional(rollbackFor = Exception.class)
	public boolean confirm(String tid,int customerId, int amount) {
		log.info("bank2 confirm begin ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		//幂等校验 且try执行完成
		if(!confirmLogDao.isExist(tid)){
			if(tryLogDao.isExist(tid)) {
				bankAccountDao.addAccountBalance(customerId, 1, amount);
				log.info("账户 {} 收款金额 {} 成功！！！", customerId, amount);
				confirmLogDao.addConfirm(tid);
			}
		}
		log.info("bank2 confirm end ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		return Boolean.TRUE;
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean cancel(String tid,int customerId, int amount) {
		log.info("bank2 cancel begin ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		//幂等校验 且try执行完毕
		if(!cancelLogDao.isExist(tid)){
			if(tryLogDao.isExist(tid)) {
				bankAccountDao.subtractAccountBalance(customerId, 1, amount);
				cancelLogDao.addCancel(tid);
			}
		}

		log.info("bank2 cancel end ... tid is {} customerId is {} amount is {} ！！！", tid, customerId, amount);
		return Boolean.TRUE;
	}

}

```
Bank2的启动器
```java
@SpringBootApplication(scanBasePackages = {"com.dhb.bank.transfer.orm.dao", "com.dhb.bank.transfer.bank2"})
@MapperScan(basePackages = {"com.dhb.bank.transfer.orm.mapper"})
public class DubboServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DubboServerApplication.class, args);
	}

}

```

# 6.client连接测试
## 6.1 client代码
```java

@SpringBootApplication
@Slf4j
public class BankTransferClientApplication {

	@DubboReference(version = "1.0.0") //, url = "dubbo://127.0.0.1:12345")
	private Bank1Service bank1Service;

	public static void main(String[] args) {
		SpringApplication.run(BankTransferClientApplication.class, args);
	}


	@Bean
	public ApplicationRunner runner() {
		return args -> {
			int customerid = 10000;
			int amount = 500;
			String tid = UUID.randomUUID().toString();
			bank1Service.transfer(tid,customerid,amount);
			log.info("customerid {} transfer amount {} ...",customerid,amount);
		};
	}
}
```
## 6.2 测试过程
首先需要在本地启动一个zookeeper，确保文中的配置能够连接。

启动 bank2、bank1 的dubbo Starter.

之后再启动client连接进行测试。

