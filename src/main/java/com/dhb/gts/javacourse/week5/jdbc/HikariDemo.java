package com.dhb.gts.javacourse.week5.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class HikariDemo {

	public static void main(String[] args) throws Exception{

		preparedStatementTransaction();
	}

	private static void preparedStatementTransaction() throws Exception{
		Connection connection = DBTool.getInstance().getConn();
		String sql1 = "insert into users(username,password) values(?,?);";
		PreparedStatement ps = null;
		try {
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(sql1);
			for(int i=0;i<10;i++) {
				ps.setString(1,"psbatch"+i);
				ps.setString(2,"password"+i);
				ps.addBatch();
				if(i == 5) {
					int a = 1/0;
				}
			}
			ps.executeBatch();
			connection.commit();
		} catch (Exception e) {
			System.out.println("执行过程出现异常！事务回滚！");
			connection.rollback();
			e.printStackTrace();
		}finally {
			if(null != ps){
				ps.close();
			}
		}
	}

	private static void statementTransaction() throws Exception{
		Connection connection = DBTool.getInstance().getConn();
		String sql1 = "update users set password = \"123456\" where id = \"13\";";
		String sql2 = "insert into users(username,password) values(\"stbatch1\",\"test\");";
		String sql3 = "update users set password = \"123456\" where username = \"stbatch1\";";
		String sql4 = "insert into users(username,password) values(\"stbatch2\",\"test\");";
		String sql5 = "delete from users where username = \"stbatch2\";";
		Statement st = null;
		try {
			connection.setAutoCommit(false);
			st = connection.createStatement();
			st.addBatch(sql1);
			st.addBatch(sql2);
			st.addBatch(sql3);
			int a = 1/0;
			st.addBatch(sql4);
			st.addBatch(sql5);
			st.executeBatch();
			connection.commit();
		} catch (Exception e) {
			System.out.println("出现异常，事务回滚！");
			connection.rollback();
			e.printStackTrace();
		}finally {
			if(null != st){
				st.close();
			}
		}
	}
	
}
