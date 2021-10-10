drop table if exists `t_bank_account`;
create table t_bank_account  (
         id         	int(11) auto_increment comment '主键 自增列'  not null,
         customer_id	int(11) comment '用户编号'  not null,
         account_type tinyint(4) comment '账户类型：1 人民币账户，2 美元账户'  not null default '1',
         balance    	bigint(20) comment '客户余额 单位 分'  not null,
         create_time	timestamp comment '用户注册时间'  not null,
         is_validate	tinyint(4) comment '数据是否有效标识：1有效数据，2 无效数据'  not null default '1',
         update_time	timestamp comment '最后修改时间'  not null,
         primary key(id)
) engine=innodb default charset=utf8  auto_increment=1;