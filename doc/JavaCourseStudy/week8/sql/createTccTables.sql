drop table if exists `t_try_log`;
CREATE TABLE `t_try_log` (
                                 `tx_no` varchar(64) NOT NULL COMMENT '事务id',
                                 `create_time` datetime DEFAULT NULL,
                                 PRIMARY KEY (`tx_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
drop table if exists `t_confirm_log`;
CREATE TABLE `t_confirm_log` (
                                     `tx_no` varchar(64) NOT NULL COMMENT '事务id',
                                     `create_time` datetime DEFAULT NULL,
                                     PRIMARY KEY (`tx_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
drop table if exists `t_cancel_log`;
CREATE TABLE `t_cancel_log` (
                                    `tx_no` varchar(64) NOT NULL COMMENT '事务id',
                                    `create_time` datetime DEFAULT NULL,
                                    PRIMARY KEY (`tx_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;