/*
Navicat MySQL Data Transfer

Source Server         : 192.168.162.49(gts)
Source Server Version : 50717
Source Host           : 192.168.162.49:3306
Source Database       : gts

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2021-09-09 18:11:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for T_PRODUCT_INFO
-- ----------------------------
DROP TABLE IF EXISTS `T_PRODUCT_INFO`;
CREATE TABLE `T_PRODUCT_INFO` (
  `PRODUCT_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `PRODUCT_CODE` char(16) NOT NULL COMMENT '商品编码',
  `PRODUCT_NAME` varchar(50) NOT NULL COMMENT '商品名称',
  `SUPPLIER_ID` int(11) NOT NULL COMMENT '供应商ID',
  `PRICE` decimal(8,2) NOT NULL COMMENT '商品单价',
  `ACERAGE_COST` int NOT NULL COMMENT '核算平均成本 单位 分',
  `STATUS` tinyint(4) NOT NULL DEFAULT '0' COMMENT '商品状态 0 不可售 1 可售',
  `PRODUCTION_DATE` datetime NOT NULL COMMENT '生产日期',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `IS_VALIDATE` tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据是否有效标识：1有效数据，2 无效数据',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`PRODUCT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='产品信息表';
