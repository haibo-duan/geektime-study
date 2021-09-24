如果有一个数据库gts中，存在一张订单表t_order_summary，这个表的数据量特别大。现在考虑对这张表进行水平拆分。具体的拆分方法有如下两种。
# 1.按order_id字段拆分表
可以将t_order_summary按order_id拆分到多个表如32个表，然后将32个表拆分到不同的数据库中。
如将t_order_summary拆分之后效果如下：
数据库gts01:
```
+--------------------+
| Tables_in_gts01    |
+--------------------+
| t_order_summary_1  |
| t_order_summary_10 |
| t_order_summary_11 |
| t_order_summary_12 |
| t_order_summary_13 |
| t_order_summary_14 |
| t_order_summary_15 |
| t_order_summary_16 |
| t_order_summary_2  |
| t_order_summary_3  |
| t_order_summary_4  |
| t_order_summary_5  |
| t_order_summary_6  |
| t_order_summary_7  |
| t_order_summary_8  |
| t_order_summary_9  |
+--------------------+
```
数据库gts02:
```
+--------------------+
| Tables_in_gts02    |
+--------------------+
| t_order_summary_17 |
| t_order_summary_18 |
| t_order_summary_19 |
| t_order_summary_20 |
| t_order_summary_21 |
| t_order_summary_22 |
| t_order_summary_23 |
| t_order_summary_24 |
| t_order_summary_25 |
| t_order_summary_26 |
| t_order_summary_27 |
| t_order_summary_28 |
| t_order_summary_29 |
| t_order_summary_30 |
| t_order_summary_31 |
| t_order_summary_32 |
+--------------------+
```
当然也可以根据实际情况拆分多个库和多张表，这种方式只与order_id有关。
配置如下：
```
#shardingSphereJDBC配置
spring.shardingsphere.datasource.names: gts01,gts02
spring.shardingsphere.datasource.common.type: com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.common.driver-class-name: com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.common.maxWait: 60000
spring.shardingsphere.datasource.common.initialSize: 5
spring.shardingsphere.datasource.common.minIdle: 5
spring.shardingsphere.datasource.common.maxActive: 20
spring.shardingsphere.datasource.gts01.jdbc-url: jdbc:mysql://192.168.161.114:3306/gts01?useSSL=false&autoReconnect=true&characterEncoding=UTF-8&serverTimezone=UTC
spring.shardingsphere.datasource.gts01.username: gts
spring.shardingsphere.datasource.gts01.password: mysql
spring.shardingsphere.datasource.gts02.jdbc-url: jdbc:mysql://192.168.161.114:3306/gts02?useSSL=false&autoReconnect=true&characterEncoding=UTF-8&serverTimezone=UTC
spring.shardingsphere.datasource.gts02.username: gts
spring.shardingsphere.datasource.gts02.password: mysql

#配置 t_order 表规则
spring.shardingsphere.rules.sharding.tables.t_order_summary.actual-data-nodes: gts01.t_order_summary_$->{1..16},gts02.t_order_summary_$->{17..32}

# 配置分表策略
spring.shardingsphere.rules.sharding.tables.t_order_summary.table-strategy.standard.sharding-column: order_id
spring.shardingsphere.rules.sharding.tables.t_order_summary.table-strategy.standard.sharding-algorithm-name: t-order-inline

# 分布式序列策略配置
spring.shardingsphere.rules.sharding.tables.t_order_summary.key-generate-strategy.column: order_id
spring.shardingsphere.rules.sharding.tables.t_order_summary.key-generate-strategy.key-generator-name: snowflake

E拆分算法
spring.shardingsphere.rules.sharding.sharding-algorithms.t-order-inline.type: inline
spring.shardingsphere.rules.sharding.sharding-algorithms.t-order-inline.props.algorithm-expression: t_order_summary_$->{order_id % 32 + 1}

#此处必须配置
spring.shardingsphere.rules.sharding.key-generators.snowflake.type: snowflake
spring.shardingsphere.rules.sharding.key-generators.snowflake.props.worker-id: 123


# 打开sql输出日志
spring.shardingsphere.props.sql-show: true
logging.level.org.springframework: debug
```

# 2.先按customer_id分库再按order_id分表
另外一种拆分方法，就是根据customer_id先拆分数据库，再根据order_id来分表。
这种方式要求每个数据库种的表结构都相同。上面的数据库如下：
gts01数据库：
```
+--------------------+
| Tables_in_gts01    |
+--------------------+
| t_order_summary_1  |
| t_order_summary_10 |
| t_order_summary_11 |
| t_order_summary_12 |
| t_order_summary_13 |
| t_order_summary_14 |
| t_order_summary_15 |
| t_order_summary_16 |
| t_order_summary_2  |
| t_order_summary_3  |
| t_order_summary_4  |
| t_order_summary_5  |
| t_order_summary_6  |
| t_order_summary_7  |
| t_order_summary_8  |
| t_order_summary_9  |
+--------------------+
```
gts02数据库：
```
+--------------------+
| Tables_in_gts02    |
+--------------------+
| t_order_summary_1  |
| t_order_summary_10 |
| t_order_summary_11 |
| t_order_summary_12 |
| t_order_summary_13 |
| t_order_summary_14 |
| t_order_summary_15 |
| t_order_summary_16 |
| t_order_summary_2  |
| t_order_summary_3  |
| t_order_summary_4  |
| t_order_summary_5  |
| t_order_summary_6  |
| t_order_summary_7  |
| t_order_summary_8  |
| t_order_summary_9  |
+--------------------+
```
这两个数据库的结构一致。
shardingSphere配置如下：
```

#shardingSphereJDBC配置
spring.shardingsphere.datasource.names: gts01,gts02
spring.shardingsphere.datasource.common.type: com.zaxxer.hikari.HikariDataSource
spring.shardingsphere.datasource.common.driver-class-name: com.mysql.cj.jdbc.Driver
spring.shardingsphere.datasource.common.maxWait: 60000
spring.shardingsphere.datasource.common.initialSize: 5
spring.shardingsphere.datasource.common.minIdle: 5
spring.shardingsphere.datasource.common.maxActive: 20
spring.shardingsphere.datasource.gts01.jdbc-url: jdbc:mysql://192.168.161.114:3306/gts01?useSSL=false&autoReconnect=true&characterEncoding=UTF-8&serverTimezone=UTC
spring.shardingsphere.datasource.gts01.username: gts
spring.shardingsphere.datasource.gts01.password: mysql
spring.shardingsphere.datasource.gts02.jdbc-url: jdbc:mysql://192.168.161.114:3306/gts02?useSSL=false&autoReconnect=true&characterEncoding=UTF-8&serverTimezone=UTC
spring.shardingsphere.datasource.gts02.username: gts
spring.shardingsphere.datasource.gts02.password: mysql

# 配置分库策略
spring.shardingsphere.rules.sharding.default-database-strategy.standard.sharding-column: customer_id
spring.shardingsphere.rules.sharding.default-database-strategy.standard.sharding-algorithm-name: database-inline
#配置 t_order 表规则
spring.shardingsphere.rules.sharding.tables.t_order_summary.actual-data-nodes: gts0$->{1..2}.t_order_summary_$->{1..16}

# 配置分表策略
spring.shardingsphere.rules.sharding.tables.t_order_summary.table-strategy.standard.sharding-column: order_id
spring.shardingsphere.rules.sharding.tables.t_order_summary.table-strategy.standard.sharding-algorithm-name: t-order-inline

# 分布式序列策略配置
spring.shardingsphere.rules.sharding.tables.t_order_summary.key-generate-strategy.column: order_id
spring.shardingsphere.rules.sharding.tables.t_order_summary.key-generate-strategy.key-generator-name: snowflake


spring.shardingsphere.rules.sharding.sharding-algorithms.database-inline.type: inline
spring.shardingsphere.rules.sharding.sharding-algorithms.database-inline.props.algorithm-expression: gts0$->{customer_id % 2 + 1}
spring.shardingsphere.rules.sharding.sharding-algorithms.t-order-inline.type: inline
spring.shardingsphere.rules.sharding.sharding-algorithms.t-order-inline.props.algorithm-expression: t_order_summary_$->{order_id % 16 + 1}


spring.shardingsphere.rules.sharding.key-generators.snowflake.type: snowflake
spring.shardingsphere.rules.sharding.key-generators.snowflake.props.worker-id: 123


# 打开sql输出日志
spring.shardingsphere.props.sql-show: true
#spring.shardingsphere.props.sql-simple: true

logging.level.org.springframework: debug
```
