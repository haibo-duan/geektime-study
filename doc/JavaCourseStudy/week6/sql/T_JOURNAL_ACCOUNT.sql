/*
Navicat MySQL Data Transfer

Source Server         : 192.168.162.49(gts)
Source Server Version : 50717
Source Host           : 192.168.162.49:3306
Source Database       : gts

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2021-09-10 15:04:41
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for T_JOURNAL_ACCOUNT
-- ----------------------------
DROP TABLE IF EXISTS `T_JOURNAL_ACCOUNT`;
CREATE TABLE `T_JOURNAL_ACCOUNT` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键 自增列',
  `CUSTOMER_ID` int(11) NOT NULL COMMENT '用户ID',
  `TYPE` tinyint(4) NOT NULL COMMENT '记录类型 1 订单 2 退货 3 付款 4 退款',
  `SOURCE_NO` int(11) DEFAULT NULL COMMENT '关联单据编号',
  `BEFORE_BALANCE` decimal(8,2) NOT NULL COMMENT '操作前的账户余额',
  `CURRENT_BALANCE` decimal(8,2) NOT NULL COMMENT '当前账户余额',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `IS_VALIDATE` tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据是否有效标识：1有效数据，2 无效数据',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账户资金流水表 记录账户的每一笔变动详情';
