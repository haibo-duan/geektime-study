/*
Navicat MySQL Data Transfer

Source Server         : 192.168.162.49(gts)
Source Server Version : 50717
Source Host           : 192.168.162.49:3306
Source Database       : gts

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2021-09-09 18:11:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for T_ORDER_DETAIL
-- ----------------------------
DROP TABLE IF EXISTS `T_ORDER_DETAIL`;
CREATE TABLE `T_ORDER_DETAIL` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键-自增列',
  `ORDER_ID` int(11) NOT NULL COMMENT '订单汇总表ID',
  `PRODUCT_ID` int(11) NOT NULL COMMENT '产品编号',
  `PRODUCT_NAME` varchar(50) NOT NULL COMMENT '产品名称',
  `NUMBERS` int(11) NOT NULL COMMENT '数量',
  `PRICE` decimal(8,2) NOT NULL COMMENT '产品单价',
  `AVERAGE_COST` decimal(8,2) NOT NULL COMMENT '核算平均成本价格',
  `TOTAL` decimal(8,2) NOT NULL COMMENT '产品总金额',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `IS_VALIDATE` tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据是否有效标识：1有效数据，2 无效数据',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT='订单明细表';
