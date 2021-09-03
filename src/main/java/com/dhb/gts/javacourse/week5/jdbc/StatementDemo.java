package com.dhb.gts.javacourse.week5.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementDemo {

	public static void main(String[] args) throws Exception {
//		insertRecordToUsersTable();
//		selectRecordsFromDbUsersTable();
//		updateRecordsToUsersTable();
		deleteRecordFromUsersTable();
	}

	public static void deleteRecordFromUsersTable() throws Exception{
		Connection conn = MysqlConnection.getInstance().getCOnnection();
		Statement st = null;
		String username = "aas";
		String sql = "delete from users where username = \""+username+"\" ;";
		try {
			st = conn.createStatement();
			st.execute(sql);
			
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			if(null != st) {
				st.close();
			}
		}
	}
	
	/**
	 * update方法
	 * @throws Exception
	 */
	public static void updateRecordsToUsersTable() throws Exception{
		Connection conn = MysqlConnection.getInstance().getCOnnection();
		Statement st = null;
		String passwd = "dfrgeg";
		String id = "13";
		String sql = "update users set password = \""+passwd+"\" where id=\""+id+"\";";
		try {
			st = conn.createStatement();
			st.execute(sql);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(null != st) {
				st.close();
			}
		}
	}
	
	/**
	 * 插入数据
	 * @throws Exception
	 */
	public static void insertRecordToUsersTable() throws Exception {
		Connection conn = MysqlConnection.getInstance().getCOnnection();
		Statement statement = null;
		String sql = "insert into users(id,username,password) values(12,\"dffd\",\"sddfef\")";
		try {
			statement = conn.createStatement();
			boolean result = statement.execute(sql);
			System.out.println(result);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			if (null != statement) {
				statement.close();
			}
			if (null != conn) {
				conn.close();
			}
		}
	}

	/**
	 * 查询数据
	 * @throws Exception
	 */
	public static void selectRecordsFromDbUsersTable() throws Exception {
		Connection conn = MysqlConnection.getInstance().getCOnnection();
		Statement statement = null;
		String sql = "select id,username,password from users;";
		try {
			statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			System.out.println(rs.getFetchSize());
			while (rs.next()) {
				int id = rs.getInt("id");
				String username = rs.getString("username");
				System.out.println("查询到数据： id [" + id + "] username [" + username + "]");
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			if (null != statement) {
				statement.close();
			}
			if (null != conn) {
				conn.close();
			}
		}
	}
}
