
在学习过ShardingSphere-JDBC相关的操作之后，现在使用其对数据库进行水平拆分。
# 1.环境准备
## 1.版本信息
mysql的版本：
```
[root@m161p114 sql]$ mysql -uroot -pmysql;
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 224
Server version: 5.7.34-log Source distribution
```
ShardingSphere gradle中的引入：
```
 implementation 'org.apache.shardingsphere:shardingsphere-jdbc-core-spring-boot-starter:5.0.0-alpha'
```

## 2.表结构
需要拆分的表结构如下：
```
/*
* 逻辑表
 */
drop table if exists `t_order_summary`;
create table t_order_summary  (
                                    order_id         	bigint  comment '订单id'  not null,
                                    order_no         	int(11) comment '订单编号'  not null,
                                    customer_id      	int(11) comment '下单用户id'  not null,
                                    payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                    order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                    payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                    consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                    consignee_address	varchar(100) comment '收货人详细'  not null,
                                    consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                    express_comp     	varchar(30) comment '快递公司名称'  not null,
                                    express_no       	varchar(50) comment '快递单号'  not null,
                                    create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                    is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                    update_time      	timestamp comment '最新更新时间'  not null,
                                    primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-逻辑表' ;
```
这是之前定义的订单表，之前order_id是采用的mysql的自增列。这个表的数据量非常大，现在要进行拆分。

# 2.数据库的水平拆分
现在规划将该数据库进行水平的分库分表，拆分到两个库中，每个库16张表。
创建数据库：
```
create database gts01 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
grant all privileges on gts01.* to gts@’%’;
flush privileges;
create database gts02 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
grant all privileges on gts02.* to gts@’%’;
flush privileges;
```
数据库gts01创建表：
```
use gts01;

drop table if exists `t_order_summary_1`;
create table t_order_summary_1  (
  order_id         	bigint  comment '订单id'  not null,
  order_no         	int(11) comment '订单编号'  not null,
  customer_id      	int(11) comment '下单用户id'  not null,
  payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
  order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
  payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
  consignee_name   	varchar(50) comment '收货人姓名'  not null,
  consignee_address	varchar(100) comment '收货人详细'  not null,
  consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
  express_comp     	varchar(30) comment '快递公司名称'  not null,
  express_no       	varchar(50) comment '快递单号'  not null,
  create_time      	timestamp comment '创建时间'  not null default current_timestamp,
  is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
  update_time      	timestamp comment '最新更新时间'  not null,
  primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表02' ;

drop table if exists `t_order_summary_2`;
create table t_order_summary_2  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表02' ;

drop table if exists `t_order_summary_3`;
create table t_order_summary_3  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表03' ;

drop table if exists `t_order_summary_4`;
create table t_order_summary_4  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表04' ;

drop table if exists `t_order_summary_5`;
create table t_order_summary_5  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表05' ;

drop table if exists `t_order_summary_6`;
create table t_order_summary_6  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表06' ;

drop table if exists `t_order_summary_7`;
create table t_order_summary_7  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表07' ;

drop table if exists `t_order_summary_8`;
create table t_order_summary_8  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表08' ;

drop table if exists `t_order_summary_9`;
create table t_order_summary_9  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表09' ;

drop table if exists `t_order_summary_10`;
create table t_order_summary_10  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表10' ;

drop table if exists `t_order_summary_11`;
create table t_order_summary_11  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表11' ;

drop table if exists `t_order_summary_12`;
create table t_order_summary_12  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表12' ;

drop table if exists `t_order_summary_13`;
create table t_order_summary_13  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表13' ;

drop table if exists `t_order_summary_14`;
create table t_order_summary_14  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表14' ;

drop table if exists `t_order_summary_15`;
create table t_order_summary_15  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表15' ;

drop table if exists `t_order_summary_16`;
create table t_order_summary_16  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表16' ;
```
数据库gts02创建表：
```
use gts02;

drop table if exists `t_order_summary_17`;
create table t_order_summary_17  (
  order_id         	bigint  comment '订单id'  not null,
  order_no         	int(11) comment '订单编号'  not null,
  customer_id      	int(11) comment '下单用户id'  not null,
  payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
  order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
  payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
  consignee_name   	varchar(50) comment '收货人姓名'  not null,
  consignee_address	varchar(100) comment '收货人详细'  not null,
  consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
  express_comp     	varchar(30) comment '快递公司名称'  not null,
  express_no       	varchar(50) comment '快递单号'  not null,
  create_time      	timestamp comment '创建时间'  not null default current_timestamp,
  is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
  update_time      	timestamp comment '最新更新时间'  not null,
  primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表17' ;

drop table if exists `t_order_summary_18`;
create table t_order_summary_18  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表18' ;

drop table if exists `t_order_summary_19`;
create table t_order_summary_19  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表19' ;

drop table if exists `t_order_summary_20`;
create table t_order_summary_20  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表20' ;

drop table if exists `t_order_summary_21`;
create table t_order_summary_21  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表21' ;

drop table if exists `t_order_summary_22`;
create table t_order_summary_22  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表22' ;

drop table if exists `t_order_summary_23`;
create table t_order_summary_23  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表23' ;

drop table if exists `t_order_summary_24`;
create table t_order_summary_24  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表24' ;

drop table if exists `t_order_summary_25`;
create table t_order_summary_25  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表25' ;

drop table if exists `t_order_summary_26`;
create table t_order_summary_26  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表26' ;

drop table if exists `t_order_summary_27`;
create table t_order_summary_27  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表27' ;

drop table if exists `t_order_summary_28`;
create table t_order_summary_28  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表28' ;

drop table if exists `t_order_summary_29`;
create table t_order_summary_29  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表29' ;

drop table if exists `t_order_summary_30`;
create table t_order_summary_30  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表30' ;

drop table if exists `t_order_summary_31`;
create table t_order_summary_31  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表31' ;

drop table if exists `t_order_summary_32`;
create table t_order_summary_32  (
                                     order_id         	bigint  comment '订单id'  not null,
                                     order_no         	int(11) comment '订单编号'  not null,
                                     customer_id      	int(11) comment '下单用户id'  not null,
                                     payment_method   	tinyint(4) comment '支付方式：1现金，2余额，3网银，4支付宝，5微信'  not null,
                                     order_amount     	int(11) comment '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  not null,
                                     payment_money    	int(11) comment '支付金额  单位 分  此处为int,比bigint节约4个字节'  not null,
                                     consignee_name   	varchar(50) comment '收货人姓名'  not null,
                                     consignee_address	varchar(100) comment '收货人详细'  not null,
                                     consignee_phone  	varchar(30) comment '收货人联系电话'  not null,
                                     express_comp     	varchar(30) comment '快递公司名称'  not null,
                                     express_no       	varchar(50) comment '快递单号'  not null,
                                     create_time      	timestamp comment '创建时间'  not null default current_timestamp,
                                     is_validate      	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
                                     update_time      	timestamp comment '最新更新时间'  not null,
                                     primary key(order_id)
)
    engine = innodb
comment = '订单汇总信息表-分表32' ;
```
# 3.ShardingSphere配置
ShardingSphere-jdbc的配置文件如下，该配置文件全部写在application.yml中。
原来的一张表的数据，将拆分到两个数据库，32张表中。拆分的方式，先通过customer_id字段，按用户取模拆分到两个数据库中。
之后按照order_id字段与32取模，再将数据拆分到32张表中。
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
# 配置分库策略
spring.shardingsphere.rules.sharding.tables.t_order_summary.database-strategy.standard.sharding-column: customer_id
spring.shardingsphere.rules.sharding.tables.t_order_summary.database-strategy.standard.sharding-algorithm-name: database_inline

# 配置分表策略
spring.shardingsphere.rules.sharding.tables.t_order_summary.table-strategy.standard.sharding-column: order_id
spring.shardingsphere.rules.sharding.tables.t_order_summary.table-strategy.standard.sharding-algorithm-name: t-order-inline

# 分布式序列策略配置
spring.shardingsphere.rules.sharding.tables.t_order_summary.key-generate-strategy.column: order_id
spring.shardingsphere.rules.sharding.tables.t_order_summary.key-generate-strategy.key-generator-name: snowflake

E拆分算法
spring.shardingsphere.rules.sharding.sharding-algorithms.database-inline.type: inline
spring.shardingsphere.rules.sharding.sharding-algorithms.database-inline.props.algorithm-expression: gts0$->{customer_id % 2 + 1}
spring.shardingsphere.rules.sharding.sharding-algorithms.t-order-inline.type: inline
spring.shardingsphere.rules.sharding.sharding-algorithms.t-order-inline.props.algorithm-expression: t_order_summary_$->{order_id % 32 + 1}

#此处必须配置
spring.shardingsphere.rules.sharding.key-generators.snowflake.type: snowflake
spring.shardingsphere.rules.sharding.key-generators.snowflake.props.worker-id: 123


# 打开sql输出日志
spring.shardingsphere.props.sql.show: true
logging.level.org.springframework: debug
```

# 4. 数据测试
上述配置完成后，启动springboot项目，通过如下代码来测试:
## 4.1 insert
service层代码：
```
	public OrderSummaryEntity buildOrderSummary(int orderId) {
		Random random = new Random(System.currentTimeMillis());
		int expressNo = random.nextInt(10000000) + 10000000;
		OrderSummaryEntity orderSummaryEntity = new OrderSummaryEntity()
				.setOrderNo(orderId)
				.setConsigneeAddress("北京市朝阳区西坝河")
				.setConsigneeName("张三")
				.setConsigneePhone("13888888888")
				.setCustomerId(10001)
				.setExpressNo("1001" + expressNo)
				.setExpressComp("顺丰")
				.setPaymentMethod(1)
				.setPaymentMoney(5600)
				.setOrderAmount(5600)
				.setIsValidate(1);
		return orderSummaryEntity;
	}
	
		public void insertOrder(int orderId) {
		OrderSummaryEntity orderSummaryEntity = buildOrderSummary(orderId);
		orderSummaryDao.save(orderSummaryEntity);
	}

```
Controller层代码：
```
	@RequestMapping("/randomInsertOneOrder")
	public String randomInsertOneOrder() {
		Stopwatch stopwatch = Stopwatch.createStarted();
		Random random = new Random(10000);
		orderService.insertOrder(random.nextInt());
		log.info("randomInsertOneOrder cost time :"+ stopwatch.stop());
		return "success";
	}
	
```
上述代码执行后，数据完成插入数据库。
可以看到，对于order_id字段，我们没有设置任何值，ShardingSphere帮我们自动注入了雪花算法生成的值。
此外还通过随机批量插入了100000条数据。批量的数据也被注入了值。插入之后的数据库gts01情况如下：
```
mysql> SELECT table_name,table_rows FROM information_schema.tables where  TABLE_SCHEMA = 'gts01';
+--------------------+------------+
| table_name         | table_rows |
+--------------------+------------+
| t_order_summary_1  |       3128 |
| t_order_summary_10 |       2971 |
| t_order_summary_11 |       2612 |
| t_order_summary_12 |       3030 |
| t_order_summary_13 |       2606 |
| t_order_summary_14 |       3070 |
| t_order_summary_15 |       2648 |
| t_order_summary_16 |       2164 |
| t_order_summary_2  |       3370 |
| t_order_summary_3  |       2741 |
| t_order_summary_4  |       3130 |
| t_order_summary_5  |       2847 |
| t_order_summary_6  |       3154 |
| t_order_summary_7  |       2847 |
| t_order_summary_8  |       2622 |
| t_order_summary_9  |       2909 |
+--------------------+------------+
33 rows in set (0.00 sec)

```
gts02数据库的数据情况：
```

mysql> SELECT table_name,table_rows FROM information_schema.tables where  TABLE_SCHEMA = 'gts02';
+--------------------+------------+
| table_name         | table_rows |
+--------------------+------------+
| t_order_summary_17 |       2900 |
| t_order_summary_18 |       2710 |
| t_order_summary_19 |       2708 |
| t_order_summary_20 |       3145 |
| t_order_summary_21 |       2794 |
| t_order_summary_22 |       2646 |
| t_order_summary_23 |       2380 |
| t_order_summary_24 |       2864 |
| t_order_summary_25 |       2423 |
| t_order_summary_26 |       3311 |
| t_order_summary_27 |       2248 |
| t_order_summary_28 |       2688 |
| t_order_summary_29 |       2332 |
| t_order_summary_30 |       2470 |
| t_order_summary_31 |       2826 |
| t_order_summary_32 |       3015 |
+--------------------+------------+
32 rows in set (0.00 sec)
```
通过ShardingSphere完美的实现了数据库的插入。
## 4.2 select
Service层：
```
public OrderSummaryEntity queryOrderById(long order_id) {
		return orderSummaryDao.selectById(order_id);
	}

```
Controller层：
```
@RequestMapping("/queryByKey")
	public String queryByKey(String key) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		Long orde_id = Long.parseLong(key);
		OrderSummaryEntity entity = orderService.queryOrderById(orde_id);
		stopwatch.stop();
		log.info("通过key查询，走索引耗时：" + stopwatch);
		return JSON.toJSONString(entity);
	}
```
查询请求：
```
http://127.0.0.1:8084/queryByKey?key=647892524325122066
```
上述url能够查到所需的结果。

# 5.所踩到的坑
## 5.1 key-generators.snowflake.type 必须配置
```
spring.shardingsphere.rules.sharding.key-generators.snowflake.type: snowflake
spring.shardingsphere.rules.sharding.key-generators.snowflake.props.worker-id: 123
```
这个位置必须配置，虽然前面有如下配置：
```
spring.shardingsphere.rules.sharding.tables.t_order_summary.key-generate-strategy.column: order_id
spring.shardingsphere.rules.sharding.tables.t_order_summary.key-generate-strategy.key-generator-name: snowflake
```
但是个人理解，这个key-generate-strategy.key-generator-name应该是个name命名，实际上要通过key-generators.snowflake.type  生效。
否则将会出现如下错误：
```
2021-09-23 21:11:26.288 [geektime-study] [restartedMain] DEBUG [org.springframework.beans.factory.support.DefaultListableBeanFactory] -Creating shared instance of singleton bean 'propertySourcesPlaceholderConfigurer'
2021-09-23 21:11:26.351 [geektime-study] [restartedMain] ERROR [org.springframework.boot.SpringApplication] -Application run failed
java.lang.reflect.InvocationTargetException: null
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.shardingsphere.spring.boot.util.PropertyUtil.v2(PropertyUtil.java:111)
	at org.apache.shardingsphere.spring.boot.util.PropertyUtil.handle(PropertyUtil.java:75)
	at org.apache.shardingsphere.spring.boot.registry.AbstractAlgorithmProvidedBeanRegistry.registerBean(AbstractAlgorithmProvidedBeanRegistry.java:50)
	at org.apache.shardingsphere.sharding.spring.boot.algorithm.KeyGenerateAlgorithmProvidedBeanRegistry.postProcessBeanDefinitionRegistry(KeyGenerateAlgorithmProvidedBeanRegistry.java:38)
	at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanDefinitionRegistryPostProcessors(PostProcessorRegistrationDelegate.java:311)
	at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(PostProcessorRegistrationDelegate.java:142)
	at org.springframework.context.support.AbstractApplicationContext.invokeBeanFactoryPostProcessors(AbstractApplicationContext.java:746)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:564)
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:145)
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:754)
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:434)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:338)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1343)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1332)
	at com.dhb.gts.javacourse.week8.Starter.main(Starter.java:12)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.boot.devtools.restart.RestartLauncher.run(RestartLauncher.java:49)
Caused by: java.util.NoSuchElementException: No value bound
	at org.springframework.boot.context.properties.bind.BindResult.get(BindResult.java:55)
	... 24 common frames omitted
2021-09-23 21:11:26.353 [geektime-study] [restartedMain] DEBUG [org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext] -Closing org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext@52353625, started on Thu Sep 23 21:11:25 CST 2021
Exception in thread "restartedMain" java.lang.reflect.InvocationTargetException
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.springframework.boot.devtools.restart.RestartLauncher.run(RestartLauncher.java:49)
Caused by: java.lang.reflect.UndeclaredThrowableException
	at org.springframework.util.ReflectionUtils.rethrowRuntimeException(ReflectionUtils.java:147)
	at org.springframework.boot.SpringApplication.handleRunFailure(SpringApplication.java:817)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:348)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1343)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1332)
	at com.dhb.gts.javacourse.week8.Starter.main(Starter.java:12)
	... 5 more
Caused by: java.lang.reflect.InvocationTargetException
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.shardingsphere.spring.boot.util.PropertyUtil.v2(PropertyUtil.java:111)
	at org.apache.shardingsphere.spring.boot.util.PropertyUtil.handle(PropertyUtil.java:75)
	at org.apache.shardingsphere.spring.boot.registry.AbstractAlgorithmProvidedBeanRegistry.registerBean(AbstractAlgorithmProvidedBeanRegistry.java:50)
	at org.apache.shardingsphere.sharding.spring.boot.algorithm.KeyGenerateAlgorithmProvidedBeanRegistry.postProcessBeanDefinitionRegistry(KeyGenerateAlgorithmProvidedBeanRegistry.java:38)
	at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanDefinitionRegistryPostProcessors(PostProcessorRegistrationDelegate.java:311)
	at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(PostProcessorRegistrationDelegate.java:142)
	at org.springframework.context.support.AbstractApplicationContext.invokeBeanFactoryPostProcessors(AbstractApplicationContext.java:746)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:564)
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:145)
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:754)
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:434)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:338)
	... 8 more
Caused by: java.util.NoSuchElementException: No value bound
	at org.springframework.boot.context.properties.bind.BindResult.get(BindResult.java:55)
	... 24 more
```
# 5.2 数据库建表语句最好用小写
还犯了一个错误就是数据库的大小写问题，数据库一开始设置为了大小写敏感。用大写创建的数据库。
但是执行的时候一直出现如下错误：
```
Error updating database. Cause: java.lang.NullPointerException: Cannot invoke method mod() on null object
The error may involve org.dromara.hmily.demo.common.order.mapper.OrderMapper.save-Inline
The error occurred while setting parameters
SQL: insert into t_order (create_time,number,status,product_id,total_amount,count,user_id) values ( ?,?,?,?,?,?,?)
Cause: java.lang.NullPointerException: Cannot invoke method mod() on null object] with root cause
java.lang.NullPointerException: Cannot invoke method mod() on null object
at org.codehaus.groovy.runtime.NullObject.invokeMethod(NullObject.java:91) ~[groovy-2.4.19-indy.jar:2.4.19]
at org.codehaus.groovy.runtime.callsite.PogoMetaClassSite.call(PogoMetaClassSite.java:47) ~[groovy-2.4.19-indy.jar:2.4.19]
at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCall(CallSiteArray.java:47) ~[groovy-2.4.19-indy.jar:2.4.19]
at org.codehaus.groovy.runtime.callsite.NullCallSite.call(NullCallSite.java:34) ~[groovy-2.4.19-indy.jar:2.4.19]
at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCall(CallSiteArray.java:47) ~[groovy-2.4.19-indy.jar:2.4.19]
at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:116) ~[groovy-2.4.19-indy.jar:2.4.19]
at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:128) ~[groovy-2.4.19-indy.jar:2.4.19]
at Script6$_run_closure1.doCall(Script6.groovy:1) ~[na:na]
at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_201]
at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_201]
at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_201]
at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_201]
at org.codehaus.groovy.reflection.CachedMethod.invoke(CachedMethod.java:98) ~[groovy-2.4.19-indy.jar:2.4.19]
at groovy.lang.MetaMethod.doMethodInvoke(MetaMethod.java:325) ~[groovy-2.4.19-indy.jar:2.4.19]
at org.codehaus.groovy.runtime.metaclass.ClosureMetaClass.invokeMethod(ClosureMetaClass.java:264) ~[groovy-2.4.19-indy.jar:2.4.19]
at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:1034) ~[groovy-2.4.19-indy.jar:2.4.19]
at groovy.lang.Closure.call(Closure.java:420) ~[groovy-2.4.19-indy.jar:2.4.19]
at groovy.lang.Closure.call(Closure.java:414) ~[groovy-2.4.19-indy.jar:2.4.19]
```
这个错误非常隐蔽，最终参考了github https://github.com/apache/shardingsphere/issues/8571

最终的解决方案是数据库建表语句用小写，生成的代码，以及ShardingSphere相关的配置都统一采用小写，这个问题就解决了。

