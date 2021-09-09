/*
Navicat MySQL Data Transfer

Source Server         : 192.168.162.49(gts)
Source Server Version : 50717
Source Host           : 192.168.162.49:3306
Source Database       : gts

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2021-09-09 18:11:29
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for T_ORDER_SUMMARY
-- ----------------------------
DROP TABLE IF EXISTS `T_ORDER_SUMMARY`;
CREATE TABLE `T_ORDER_SUMMARY` (
  `ORDER_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `ORDER_NO` int(11) NOT NULL COMMENT '订单编号',
  `CUSTOMER_ID` int(11) NOT NULL COMMENT '下单用户ID',
  `PAYMENT_METHOD` tinyint(4) NOT NULL COMMENT '支付方式：1现金，2余额，3网银，4支付宝，5微信',
  `ORDER_AMOUNT` decimal(8,2) NOT NULL COMMENT '订单汇总金额',
  `PAYMENT_MONEY` decimal(8,2) NOT NULL COMMENT '支付金额',
  `CONSIGNEE_NAME` varchar(50) NOT NULL COMMENT '收货人姓名',
  `CONSIGNEE_ADDRESS` varchar(100) NOT NULL COMMENT '收货人详细',
  `CONSIGNEE_PHONE` varchar(30) NOT NULL COMMENT '收货人联系电话',
  `EXPRESS_COMP` varchar(30) NOT NULL COMMENT '快递公司名称',
  `EXPRESS_NO` varchar(50) NOT NULL COMMENT '快递单号',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `IS_VALIDATE` tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据是否有效标识：1有效数据，2 无效数据',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最新更新时间',
  PRIMARY KEY (`ORDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单汇总信息表 ';
