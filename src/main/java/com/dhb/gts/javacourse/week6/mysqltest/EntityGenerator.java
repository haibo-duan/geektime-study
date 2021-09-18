package com.dhb.gts.javacourse.week6.mysqltest;


import cn.org.atool.generator.FileGenerator;
import cn.org.atool.generator.annotation.Column;
import cn.org.atool.generator.annotation.Table;
import cn.org.atool.generator.annotation.Tables;
import com.dhb.gts.javacourse.week5.typehandler.MyDateTypeHandler;

import java.util.Date;

public class EntityGenerator {

	// 数据源 url
	static final String url = "jdbc:mysql://192.168.161.114:3306/gts?useSSL=false&autoReconnect=true&characterEncoding=UTF-8&serverTimezone=UTC";
	// 数据库用户名
	static final String username = "gts";
	// 数据库密码
	static final String password = "mysql";

	public static void main(String[] args) {
		// 引用配置类，build方法允许有多个配置类
		FileGenerator.build(Empty.class);
	}


	@Tables(
			// 设置数据库连接信息
			url = url, username = username, password = password,driver = "com.mysql.cj.jdbc.Driver",
			// 设置entity类生成src目录, 相对于 user.dir
			srcDir = "src/gens/java",
			// 设置entity类的package值
			basePack = "com.dhb.gts.javacourse.fluent",
			// 设置dao接口和实现的src目录, 相对于 user.dir
			daoDir = "src/gens/java",
			// 设置哪些表要生成Entity文件
			tables = {
					@Table(value = {"users"},
							columns = {
									@Column(value = "createDate", javaType = Date.class, typeHandler = MyDateTypeHandler.class)
							}),
					@Table(value = {"T_ACCOUNT_BALANCE:AccountBalance"},
							columns = {
									@Column(value = "IS_VALIDATE", javaType = Integer.class)
							}, logicDeleted = "IS_VALIDATE", gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME"),
					@Table(value = {"T_CUSTOMER_INFO:CustomerInfo"},
							columns = {
									@Column(value = "IDENTITY_CARD_TYPE", javaType = Integer.class),
									@Column(value = "IS_VALIDATE", javaType = Integer.class)
							}, logicDeleted = "IS_VALIDATE", gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME"),

					@Table(value = {"T_CUSTOMER_LOGIN:CustomerLogin"},
							columns = {
									@Column(value = "STATES", javaType = Integer.class)
							}, gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME"),

					@Table(value = {"T_JOURNAL_ACCOUNT:JournalAccount"},
							columns = {
									@Column(value = "TYPE", javaType = Integer.class),
									@Column(value = "IS_VALIDATE", javaType = Integer.class)
							}, logicDeleted = "IS_VALIDATE", gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME"),

					@Table(value = {"T_ORDER_DETAIL:OrderDetail"},
							columns = {
									@Column(value = "IS_VALIDATE", javaType = Integer.class)
							}, logicDeleted = "IS_VALIDATE", gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME"),

					@Table(value = {"T_ORDER_SUMMARY:OrderSummary"},
							columns = {
									@Column(value = "PAYMENT_METHOD", javaType = Integer.class),
									@Column(value = "IS_VALIDATE", javaType = Integer.class)
							}, logicDeleted = "IS_VALIDATE", gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME"),

					@Table(value = {"T_PRODUCT_INFO:ProductInfo"},
							columns = {
									@Column(value = "STATUS", javaType = Integer.class),
									@Column(value = "IS_VALIDATE", javaType = Integer.class)
							}, logicDeleted = "IS_VALIDATE", gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME")
			}
	)
	static class Empty {
	}


}
