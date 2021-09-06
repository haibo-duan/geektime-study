最近将springboot相关的项目都迁移到了gradle,通过gradle进行编译，这个实现过程比较曲折。由于gradle的后向兼容一直很差，
这导致如果是最新版本的gradle7.1.1版本，很可能之前的build.gradle中的配置无法使用。
这里需要说明一下就是，gradle环境下，相关的插件都是采用ant的方式来使用。

# 1. build.gradle配置
完整配置如下：
```
plugins {
    id 'org.springframework.boot' version '2.5.3'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'java'
    id 'org.springframework.experimental.aot' version '0.10.2-SNAPSHOT'
    id 'org.graalvm.buildtools.native' version '0.9.1'
    # 增加gradle Mybatisgenerator插件
    id 'com.arenagod.gradle.MybatisGenerator' version '1.4'
}

group = 'com.dhb'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

# 导入MybatisGenerator插件
apply plugin: "com.arenagod.gradle.MybatisGenerator"

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
    # 增加自定义task
    mybatisGenerator
}

repositories {
    maven { url 'https://repo.spring.io/snapshot' }
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    compileOnly 'org.projectlombok:lombok'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    # 自定义mybatisGenerator task需要的包
    mybatisGenerator 'org.mybatis.generator:mybatis-generator-core:1.3.5'
    mybatisGenerator 'tk.mybatis:mapper:3.3.9'
    mybatisGenerator 'mysql:mysql-connector-java:6.0.6'
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
    # generator-core包
    compileOnly 'org.mybatis.generator:mybatis-generator-core:1.3.5'
}

#自定义的task mybatisGenerate
task mybatisGenerate {
    ant.taskdef(
            name: 'mbgenerator',
            classname: 'org.mybatis.generator.ant.GeneratorAntTask',
            classpath: configurations.mybatisGenerator.asPath
    )
    ant.mbgenerator(overwrite: true,
            configfile: 'src/main/resources/generator/generatorConfig.xml', verbose: true)
}


test {
    useJUnitPlatform()
}

bootBuildImage {
    builder = 'paketobuildpacks/builder:tiny'
    environment = ['BP_NATIVE_IMAGE': 'true']
}

```

上述配置中，通过自定义的task mybatisGenerate来执行ant。
关键的配置src/main/resources/generator/generatorConfig.xml。

# 2. generatorConfig.xml配置
在generatorConfig.xml中，对需要generator生成的表进行配置，完整配置如下：
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
		PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
		"http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">


<generatorConfiguration>
	<!-- 设置mysql驱动路径 -->

	<context id="ParaGenconfig" targetRuntime="MyBatis3"
		defaultModelType="hierarchical">
		<property name="autoDelimitKeywords" value="true" />
		<property name="beginningDelimiter" value="`" />
		<property name="endingDelimiter" value="`" />
		<plugin type="org.mybatis.generator.plugins.EqualsHashCodePlugin" />
		<plugin type="org.mybatis.generator.plugins.ToStringPlugin" />
		<plugin type="org.mybatis.generator.plugins.CaseInsensitiveLikePlugin" />
		<commentGenerator>
			<property name="suppressAllComments" value="true" />
			<property name="suppressDate" value="true" />
		</commentGenerator>
		<jdbcConnection driverClass="com.mysql.jdbc.Driver"
			connectionURL="jdbc:mysql://192.168.162.49:3306/gts?useSSL=false"
			userId="gts" password="mysql">
		</jdbcConnection>
		<javaTypeResolver>
			<property name="forceBigDecimals" value="true" />
		</javaTypeResolver>
		<javaModelGenerator targetPackage="com.dhb.gts.javacourse.week5.entity" targetProject="src/main/java" />
		<sqlMapGenerator targetPackage="com.dhb.gts.javacourse.week5.mybatis" targetProject="src/main/java">
			<property name="enableSubPackages" value="true" />
		</sqlMapGenerator>
		<javaClientGenerator type="ANNOTATEDMAPPER"
			targetPackage="com.dhb.gts.javacourse.week5.mapper" targetProject="src/main/java"
			implementationPackage="com.dhb.gts.javacourse.week5.mapper.impl">
			<property name="enableSubPackages" value="true" />
		</javaClientGenerator>
		<table tableName="users" domainObjectName="Users"></table>
	</context>
</generatorConfiguration>

```
上述为generatorConfig.xml的完整配置。

# 3.执行generator
在右侧的Gradle -> other -> mybatisGeneratic 点击运行即可执行。
![mybatis generator 执行](../../images/mybatis%20generator%20执行.png)

这样就生成了mybatis的相关代码。

在整个过程中，需要注意的就是gradle的版本问题。可以修改项目根目录下gradle
的wrapper中的gradle-wrapper.properties
。将distributionUrl中的版本改为指定的版本，本文中版本为 6.9.1 。

