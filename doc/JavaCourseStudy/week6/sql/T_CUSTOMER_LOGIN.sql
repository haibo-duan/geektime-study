/*
Navicat MySQL Data Transfer

Source Server         : 192.168.162.49(gts)
Source Server Version : 50717
Source Host           : 192.168.162.49:3306
Source Database       : gts

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2021-09-09 18:11:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for T_CUSTOMER_LOGIN
-- ----------------------------
DROP TABLE IF EXISTS `T_CUSTOMER_LOGIN`;
CREATE TABLE `T_CUSTOMER_LOGIN` (
  `USER_ID` int(11) NOT NULL COMMENT '用户ID,主键 自增列',
  `USER_NAME` varchar(50) NOT NULL COMMENT '用户登陆名',
  `PASSWORD` char(32) NOT NULL COMMENT '用户登陆密码，MD5方式存储',
  `STATES` tinyint(4) NOT NULL DEFAULT '1' COMMENT '用户状态：1有效 0 无效',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户登陆状态表 与用户信息表拆分，考虑到用户状态会频繁的变更，冷热数据分离';
