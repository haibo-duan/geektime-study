package com.dhb.gts.javacourse.mapper;

import com.dhb.gts.javacourse.entity.CustomerLogin;
import com.dhb.gts.javacourse.entity.CustomerLoginExample;
import java.util.List;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface CustomerLoginMapper {
    @SelectProvider(type=CustomerLoginSqlProvider.class, method="countByExample")
    long countByExample(CustomerLoginExample example);

    @DeleteProvider(type=CustomerLoginSqlProvider.class, method="deleteByExample")
    int deleteByExample(CustomerLoginExample example);

    @Insert({
        "insert into T_CUSTOMER_LOGIN (USER_ID, USER_NAME, ",
        "`PASSWORD`, STATES, CREATE_TIME, ",
        "UPDATE_TIME)",
        "values (#{userId,jdbcType=INTEGER}, #{userName,jdbcType=VARCHAR}, ",
        "#{password,jdbcType=CHAR}, #{states,jdbcType=tinyint}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{updateTime,jdbcType=TIMESTAMP})"
    })
    int insert(CustomerLogin record);

    @InsertProvider(type=CustomerLoginSqlProvider.class, method="insertSelective")
    int insertSelective(CustomerLogin record);

    @SelectProvider(type=CustomerLoginSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="USER_ID", property="userId", jdbcType=JdbcType.INTEGER),
        @Result(column="USER_NAME", property="userName", jdbcType=JdbcType.VARCHAR),
        @Result(column="PASSWORD", property="password", jdbcType=JdbcType.CHAR),
        @Result(column="STATES", property="states", jdbcType=JdbcType.tinyint),
        @Result(column="CREATE_TIME", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="UPDATE_TIME", property="updateTime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<CustomerLogin> selectByExample(CustomerLoginExample example);

    @UpdateProvider(type=CustomerLoginSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") CustomerLogin record, @Param("example") CustomerLoginExample example);

    @UpdateProvider(type=CustomerLoginSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") CustomerLogin record, @Param("example") CustomerLoginExample example);
}