package com.dhb.gts.javacourse.week5.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
*@author 
*@description  创建一个数据库连接的单例
*@date  2021/9/2 18:59
*/
public class MysqlConnection {

	public static final String URL = "jdbc:mysql://192.168.162.49:3306/gts";

	public static final String USERNAME = "gts";
	
	public static final String PASSWD = "mysql";
	
	public  MysqlConnection() {
		try {
			connection = DriverManager.getConnection(URL,USERNAME,PASSWD);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	
	private Connection connection;
	
	public static MysqlConnection getInstance() {
		return Singleton.INSTANCE.getInstance();
	}
	
	public Connection getCOnnection() {
		return connection;
	}

	private enum Singleton {
		INSTANCE;
		
		private final MysqlConnection conn;

		Singleton() {
			this.conn = new MysqlConnection();
		}
		
		public MysqlConnection getInstance() {
			return conn;
		}
	}
	
}
