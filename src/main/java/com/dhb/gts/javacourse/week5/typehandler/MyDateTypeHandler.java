package com.dhb.gts.javacourse.week5.typehandler;

import com.google.common.base.Strings;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(Date.class)
public class MyDateTypeHandler extends BaseTypeHandler<Date> {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

	private Date convert(String source) {
		Date result = new Date(0);
		try {
			if (!Strings.isNullOrEmpty(source)) {
				result = dateFormat.parse(source);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
	

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
		String result = dateFormat.format(parameter);
		ps.setString(i, result);
	}

	@Override
	public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String source = rs.getString(columnName);
		return convert(source);
	}

	@Override
	public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String source = rs.getString(columnIndex);
		return convert(source);
	}

	@Override
	public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String source = cs.getString(columnIndex);
		return convert(source);
	}
}
