package com.dhb.gts.javacourse.week5.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBTool {

	private DBTool() {
		this.start();
	}

	private short db_max_conn = 10;
	private String db_host = "192.168.162.49";
	private short db_port = 3306;
	private String db_name = "gts";
	private String db_username = "gts";
	private String db_password = "mysql";

	private Connection conn;
	
	private  HikariDataSource dataSource;

	public static DBTool getInstance() {
		return DBTool.Singleton.INSTANCE.getInstance();
	}

	public void start() {
		HikariConfig config = new HikariConfig();
		//classname
		config.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
		//url
		config.addDataSourceProperty("serverName",db_host);
		//port
		config.addDataSourceProperty("port",db_port);
		//databaseName
		config.addDataSourceProperty("databaseName",db_name);
		//username
		config.addDataSourceProperty("user",db_username);
		//password
		config.addDataSourceProperty("password",db_password);
		//自动commit
		config.setAutoCommit(true);
		//最大连接数
		config.setMaximumPoolSize(db_max_conn);
		//超时时间
		config.setConnectionTimeout(8*60*60);
		//数据库连接检测
		config.setConnectionTestQuery("SELECT 1");
		dataSource = new HikariDataSource(config);
		
	}
	
	public Connection getConn() throws SQLException {
		return dataSource.getConnection();
	}
	
	public boolean stop() throws SQLException{
		dataSource.close();
		return true;
	}
	
	private enum Singleton {
		INSTANCE;

		private final DBTool tool;

		Singleton() {
			this.tool = new DBTool();
		}

		public DBTool getInstance() {
			return tool;
		}
	}

}
