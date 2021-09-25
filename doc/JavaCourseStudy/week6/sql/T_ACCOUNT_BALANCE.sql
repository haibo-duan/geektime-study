/*
navicat mysql data transfer

source server         : 192.168.162.49(gts)
source server version : 50717
source host           : 192.168.162.49:3306
source database       : gts

target server type    : mysql
target server version : 50717
file encoding         : 65001

date: 2021-09-10 15:04:52
*/

set foreign_key_checks=0;

-- ----------------------------
-- table structure for t_account_balance
-- ----------------------------
drop table if exists `t_account_balance`;
create table `t_account_balance` (
  `id` int(11) not null auto_increment comment '主键 自增列',
  `customer_id` int(11) not null comment '用户编号',
  `balance`  bigint not null comment '客户余额 单位 分',
  `create_time` timestamp not null default current_timestamp on update current_timestamp comment '用户注册时间',
  `is_validate` tinyint(4) not null default '1' comment '数据是否有效标识：1有效数据，2 无效数据',
  `update_time` timestamp not null default current_timestamp on update current_timestamp comment '最后修改时间',
  primary key (`id`)
) engine=innodb default charset=utf8;
