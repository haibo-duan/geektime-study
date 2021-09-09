/*
Navicat MySQL Data Transfer

Source Server         : 192.168.162.49(gts)
Source Server Version : 50717
Source Host           : 192.168.162.49:3306
Source Database       : gts

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2021-09-09 18:10:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for T_CUSTOMER_INFO
-- ----------------------------
DROP TABLE IF EXISTS `T_CUSTOMER_INFO`;
CREATE TABLE `T_CUSTOMER_INFO` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键 自增列',
  `USER_ID` int(255) NOT NULL COMMENT '用户ID 对外关联 T_CUSTOMER表',
  `NAME` varchar(25) NOT NULL COMMENT '用户姓名',
  `IDENTITY_CARD_TYPE` tinyint(4) NOT NULL DEFAULT '1' COMMENT '用户证件类型：1 身份证，2 军官证，3 护照',
  `IDENTITY_CARD_NO` varchar(25) NOT NULL COMMENT '身份证件号码',
  `MOBILE` varchar(50) NOT NULL COMMENT '用户手机号',
  `EMAIL` varchar(50) DEFAULT NULL COMMENT '用户邮箱',
  `GENDER` char(1) DEFAULT NULL COMMENT '用户性别',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户注册时间',
  `BIRTHDAY` datetime DEFAULT NULL COMMENT '用户生日',
  `IS_VALIDATE` tinyint(4) NOT NULL DEFAULT '1' COMMENT '数据是否有效标识：1有效数据，2 无效数据',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT='用户信息表，存放用户的详细信息，PK为ID';
