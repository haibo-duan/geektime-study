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