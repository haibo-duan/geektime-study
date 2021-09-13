mybatis generator生成的mapper中，只有一些简陋的基本操作代码。如果要对一张表进行比较复杂的sql操作，或者使用到聚合函数的时候。
在之前的mybatis框架中就只能人工通过硬编码的方式来实现。定义xml或者通过注解来完成。实现这种扩展的sql增强的框架也有很多。
常见的如 mybatis plus,MyBatis Dynamic SQL和Fluent mybatis。目前Fluent Mybatis在一众mybatis 增强框架中最优，现在尝试对fluent mybatis进行使用。

# 1.gradle配置
fluent mybatis需要引入的包有：fluent-mybatis、fluent-mybatis-processor。此外还严重依赖lombok。
```
plugins {
    id 'org.springframework.boot' version '2.5.3'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'java'
    id 'org.graalvm.buildtools.native' version '0.9.1'
    id 'net.ltgt.apt' version '0.21'
}


apply plugin: 'net.ltgt.apt-idea'

group = 'com.dhb'
version = '0.0.1-SNAPSHOT'

sourceCompatibility = 1.8
targetCompatibility = 1.8

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    maven { url 'https://repo.spring.io/snapshot' }
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.0'
    implementation 'io.netty:netty-all:4.1.56.Final'
    implementation 'com.google.guava:guava:23.0'
    implementation 'org.apache.httpcomponents:httpclient:4.5.13'
    implementation 'org.apache.httpcomponents:httpcore:4.4.14'
    implementation 'org.apache.httpcomponents:httpcore-nio:4.4.14'
    implementation 'org.apache.httpcomponents:httpasyncclient:4.1.4'
    implementation 'com.squareup.okhttp3:okhttp:4.9.1'
    implementation 'mysql:mysql-connector-java:6.0.6'
    implementation 'com.zaxxer:HikariCP:4.0.3'
    implementation 'com.alibaba:fastjson:1.2.78'
    # fluent mybatis
    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'com.github.atool:fluent-mybatis:1.7.2'
    runtimeOnly 'com.github.atool:fluent-mybatis-processor:1.7.2'
    annotationProcessor'com.github.atool:fluent-mybatis-processor:1.7.2'
}


test {
    useJUnitPlatform()
}

bootBuildImage {
    builder = 'paketobuildpacks/builder:tiny'
    environment = ['BP_NATIVE_IMAGE': 'true']
}

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
配置见上述内容。

# 2.生成代码
fluent mybatis也需要反向生成代码，只不过这个配置在一个java的class中：
在本文实例中，定义了一个EntityGenerator类，放置在src/main/java 中。代码如下：
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
					@Table(value = {"users"},
							columns = {
									@Column(value = "createDate", javaType = Date.class, typeHandler = MyDateTypeHandler.class)
							}),
					@Table(value = {"T_ACCOUNT_BALANCE:AccountBalance"},
							columns = {
									@Column(value = "IS_VALIDATE", javaType = Integer.class)
							}, logicDeleted = "IS_VALIDATE", gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME"),
					@Table(value = {"T_CUSTOMER_INFO:CustomerInfo"},
							columns = {
									@Column(value = "IDENTITY_CARD_TYPE", javaType = Integer.class),
									@Column(value = "IS_VALIDATE", javaType = Integer.class)
							}, logicDeleted = "IS_VALIDATE", gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME"),

					@Table(value = {"T_CUSTOMER_LOGIN:CustomerLogin"},
							columns = {
									@Column(value = "STATES", javaType = Integer.class)
							}, gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME"),

					@Table(value = {"T_JOURNAL_ACCOUNT:JournalAccount"},
							columns = {
									@Column(value = "TYPE", javaType = Integer.class),
									@Column(value = "IS_VALIDATE", javaType = Integer.class)
							}, logicDeleted = "IS_VALIDATE", gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME"),

					@Table(value = {"T_ORDER_DETAIL:OrderDetail"},
							columns = {
									@Column(value = "IS_VALIDATE", javaType = Integer.class)
							}, logicDeleted = "IS_VALIDATE", gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME"),

					@Table(value = {"T_ORDER_SUMMARY:OrderSummary"},
							columns = {
									@Column(value = "PAYMENT_METHOD", javaType = Integer.class),
									@Column(value = "IS_VALIDATE", javaType = Integer.class)
							}, logicDeleted = "IS_VALIDATE", gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME"),

					@Table(value = {"T_PRODUCT_INFO:ProductInfo"},
							columns = {
									@Column(value = "STATUS", javaType = Integer.class),
									@Column(value = "IS_VALIDATE", javaType = Integer.class)
							}, logicDeleted = "IS_VALIDATE", gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME")
			}
	)
	static class Empty {
	}
```
需要定义一个空的类Emoty,然后通过@Tables进行配置。这个类的名称可以根据喜好自定义。在Table标签配置系统中的实体表，然后将实体表和对象通过冒号分隔，
如本文中@Table(value = {"T_PRODUCT_INFO:ProductInfo"}
在配置好后执行该main方法，就会将定义的Mapper及相关代码生成到指定的目录。
上述配置生成的代码如下图：
![fluent mybatis 生成的代码](../../images/fluent%20mybatis%20生成的代码.png)
需要注意的是，Dao及其实现类，实际上是空的，需要自行定义。

生成完这些代码之后，还需要做的一件事情就是在Build菜单下面执行 Rebuild project。这将会自动生成Mapper的实体类。
因为fluent借助于lombok。

# 3.增删改查测试
定义了一个spring boot的启动器。之后测试对标CustomerInfo标进行增删改查:
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

Contraller如下：
```
package com.dhb.gts.javacourse.week6.mysqltest;


import com.alibaba.fastjson.JSON;
import com.dhb.gts.javacourse.fluent.dao.intf.CustomerInfoDao;
import com.dhb.gts.javacourse.fluent.entity.CustomerInfoEntity;
import com.dhb.gts.javacourse.fluent.mapper.CustomerInfoMapper;
import com.dhb.gts.javacourse.fluent.wrapper.CustomerInfoQuery;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class CustomerInfoContraller {

	@Autowired
	CustomerInfoDao customerInfoDao;

	@Autowired
	CustomerInfoMapper customerInfoMapper;

	@RequestMapping("/customerInfo/add")
	public String AddCustomer(String userid, String name, String mobile, String email, String identityNo, String gender) {
		if (!Strings.isNullOrEmpty(userid)) {
			CustomerInfoEntity customerInfo = new CustomerInfoEntity()
					.setUserId(Integer.parseInt(userid))
					.setName(name)
					.setMobile(mobile)
					.setEmail(email)
					.setIdentityCardType(1)
					.setIdentityCardNo(identityNo)
					.setGender(gender)
					.setIsValidate(1);
			customerInfoMapper.insert(customerInfo);
		}

		return "success";
	}

	@RequestMapping("/customerInfo/delete")
	public String deleteCustomer(String id) {
		if (!Strings.isNullOrEmpty(id)) {
			try {
				Integer key = new Integer(Integer.parseInt(id));
				customerInfoMapper.deleteById(key);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return "id 不能转换为int";
			}
		} else {
			return "id 为空！";
		}
		return "success";
	}

	@RequestMapping("/customerInfo/modify")
	public String modifyCustomer(String id, String name, String mobile, String email, String identityNo, String gender) {
		if (!Strings.isNullOrEmpty(id)) {
			int key = Integer.parseInt(id);
			CustomerInfoEntity customerInfo = new CustomerInfoEntity()
					.setId(key);
			if (!Strings.isNullOrEmpty(name)) {
				customerInfo.setName(name);
			}
			if (!Strings.isNullOrEmpty(mobile)) {
				customerInfo.setMobile(mobile);
			}
			if (!Strings.isNullOrEmpty(email)) {
				customerInfo.setEmail(email);
			}
			if (!Strings.isNullOrEmpty(identityNo)) {
				customerInfo.setIdentityCardNo(identityNo);
			}
			if (!Strings.isNullOrEmpty(gender)) {
				customerInfo.setGender(gender);
			}
			customerInfoMapper.updateById(customerInfo);
			return "success";
		} else {
			return "id不能为空！";
		}
	}

	@RequestMapping("/customerInfo/query")
	public String modifyCustomer(String id, String userid) {
		if (!Strings.isNullOrEmpty(id)) {
			Integer key = Integer.parseInt(id);
			CustomerInfoEntity customerInfo = customerInfoMapper.findById(key);
			return JSON.toJSONString(customerInfo);
		} else if (!Strings.isNullOrEmpty(userid)) {
			Integer userId = Integer.parseInt(userid);
			CustomerInfoQuery query = new CustomerInfoQuery().selectAll()
					.where.userId().eq(userId).end();
			List<CustomerInfoEntity> list = customerInfoMapper.listEntity(query);
			return JSON.toJSONString(list);
		} else {
			return "id不能为空！";
		}
	}
	
}	
```
启动springboot,即可实现对标CustomerInfo的测试。
当然，在生产系统生成的dao中，定义这些数据库操作的具体方法。