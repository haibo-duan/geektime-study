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