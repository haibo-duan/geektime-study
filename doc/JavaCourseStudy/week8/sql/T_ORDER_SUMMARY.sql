/*
* 逻辑表
 */
drop table if exists `T_ORDER_SUMMARY`;
CREATE TABLE T_ORDER_SUMMARY  (
                                    ORDER_ID         	bigint  COMMENT '订单ID'  NOT NULL,
                                    ORDER_NO         	int(11) COMMENT '订单编号'  NOT NULL,
                                    CUSTOMER_ID      	int(11) COMMENT '下单用户ID'  NOT NULL,
                                    PAYMENT_METHOD   	tinyint(4) COMMENT '支付方式：1现金，2余额，3网银，4支付宝，5微信'  NOT NULL,
                                    ORDER_AMOUNT     	int(11) COMMENT '订单汇总金额 单位 分  此处为int,比bigint节约4个字节'  NOT NULL,
                                    PAYMENT_MONEY    	int(11) COMMENT '支付金额  单位 分  此处为int,比bigint节约4个字节'  NOT NULL,
                                    CONSIGNEE_NAME   	varchar(50) COMMENT '收货人姓名'  NOT NULL,
                                    CONSIGNEE_ADDRESS	varchar(100) COMMENT '收货人详细'  NOT NULL,
                                    CONSIGNEE_PHONE  	varchar(30) COMMENT '收货人联系电话'  NOT NULL,
                                    EXPRESS_COMP     	varchar(30) COMMENT '快递公司名称'  NOT NULL,
                                    EXPRESS_NO       	varchar(50) COMMENT '快递单号'  NOT NULL,
                                    CREATE_TIME      	timestamp COMMENT '创建时间'  NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    IS_VALIDATE      	tinyint(4) COMMENT '数据是否有效标识：1有效数据，2 无效数据'  NOT NULL DEFAULT '1',
                                    UPDATE_TIME      	timestamp COMMENT '最新更新时间'  NOT NULL,
                                    PRIMARY KEY(ORDER_ID)
)
    ENGINE = InnoDB
COMMENT = '订单汇总信息表-逻辑表' ;