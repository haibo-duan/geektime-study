package com.dhb.gts.javacourse.week5.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PreparedStatementDemo {

	public static void main(String[] args) {
		updateRecordsToDbUsersTable();
	}
	

	private static void updateRecordsToDbUsersTable() {
		Connection conn = MysqlConnection.getInstance().getCOnnection();
		PreparedStatement ps = null;
		String id = "12";
		try {
			String sql = "update users set password = ? where id=? ;";
			ps = conn.prepareStatement(sql);
			ps.setString(1,"12345678");
			ps.setString(2,id);
			int result = ps.executeUpdate();
			System.out.println("update 语句执行影响行数："+result);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void selectRecordsFromDbUsersTable() {
		Connection conn = MysqlConnection.getInstance().getCOnnection();
		PreparedStatement ps = null;
		String uname = "james";
		try {
			String sql = "select id,username,password from users where username=? ;";
			ps = conn.prepareStatement(sql);
			ps.setString(1,uname);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				System.out.println("查询到数据：..");
				int id = rs.getInt("id");
				String username = rs.getString("username");
				String password = rs.getString("password");
				System.out.println("数据： id [" + id + "] username [" + username + "] password ["+password+"]");
			}else {
				System.out.println("未查询到数据！");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
