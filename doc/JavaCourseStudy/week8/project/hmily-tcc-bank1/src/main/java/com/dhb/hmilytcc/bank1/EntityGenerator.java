package com.dhb.hmilytcc.bank1;


import cn.org.atool.generator.FileGenerator;
import cn.org.atool.generator.annotation.Column;
import cn.org.atool.generator.annotation.Table;
import cn.org.atool.generator.annotation.Tables;


public class EntityGenerator {

	// 数据源 url
	static final String url = "jdbc:mysql://192.168.161.114:3306/gts01?useSSL=false&autoReconnect=true&characterEncoding=UTF-8&serverTimezone=UTC";
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
			srcDir = "src/main/java",
			// 设置entity类的package值
			basePack = "com.dhb.hmilytcc.bank1.fluent",
			// 设置dao接口和实现的src目录, 相对于 user.dir
			daoDir = "src/main/java",
			// 设置哪些表要生成Entity文件
			tables = {
					@Table(value = {"t_try_log:TryLog"},
							gmtCreated = "CREATE_TIME"),
					@Table(value = {"t_confirm_log:ConfirmLog"},
							gmtCreated = "CREATE_TIME"),
					@Table(value = {"t_cancel_log:CancleLog"},
							gmtCreated = "CREATE_TIME"),
					@Table(value = {"t_account_balance:AccountBalance"},
							columns = {
									@Column(value = "IS_VALIDATE", javaType = Integer.class)
							}, logicDeleted = "IS_VALIDATE", gmtCreated = "CREATE_TIME", gmtModified = "UPDATE_TIME")
			}
	)
	static class Empty {
	}


}
