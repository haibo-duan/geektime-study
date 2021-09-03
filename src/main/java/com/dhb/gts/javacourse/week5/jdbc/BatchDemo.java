package com.dhb.gts.javacourse.week5.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.IntStream;

public class BatchDemo {

	public static void main(String[] args) throws Exception{
//		statementBatch();
		preparedStatementBatch();
	}

	private static void preparedStatementBatch() throws Exception{
		Connection connection = MysqlConnection.getInstance().getCOnnection();
		String sql1 = "insert into users(username,password) values(?,?);";
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(sql1);
			for(int i=0;i<10;i++) {
				ps.setString(1,"psbatch"+i);
				ps.setString(2,"password"+i);
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}finally {
			if(null != ps){
				ps.close();
			}
		}
	}
	
	private static void statementBatch() throws Exception{
		Connection connection = MysqlConnection.getInstance().getCOnnection();
		String sql1 = "update users set password = \"123456\" where id = \"13\";";
		String sql2 = "insert into users(username,password) values(\"stbatch1\",\"test\");";
		String sql3 = "update users set password = \"123456\" where username = \"stbatch1\";";
		String sql4 = "insert into users(username,password) values(\"stbatch2\",\"test\");";
		String sql5 = "delete from users where username = \"stbatch2\";";
		Statement st = null;
		try {
			st = connection.createStatement();
			st.addBatch(sql1);
			st.addBatch(sql2);
			st.addBatch(sql3);
			st.addBatch(sql4);
			st.addBatch(sql5);
			st.executeBatch();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}finally {
			if(null != st){
				st.close();
			}
		}
	}
}
