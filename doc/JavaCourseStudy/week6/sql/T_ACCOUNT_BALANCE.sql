/*
Navicat MySQL Data Transfer

Source Server         : 192.168.162.49(gts)
Source Server Version : 50717
Source Host           : 192.168.162.49:3306
Source Database       : gts

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2021-09-10 15:04:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for T_ACCOUNT_BALANCE
-- ----------------------------
DROP TABLE IF EXISTS `T_ACCOUNT_BALANCE`;
CREATE TABLE `T_ACCOUNT_BALANCE` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键 自增列',
  `CUSTOMER_ID` int(11) NOT NULL COMMENT '用户编号',
  `BALANCE` decimal(8,2) NOT NULL COMMENT '庄户余额',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '用户注册时间',
  `IS_VALIDATE` tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据是否有效标识：1有效数据，2 无效数据',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
