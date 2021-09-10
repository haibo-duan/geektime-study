package com.dhb.gts.javacourse.mapper;

import com.dhb.gts.javacourse.entity.AccountBalance;
import com.dhb.gts.javacourse.entity.AccountBalanceExample;
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

public interface AccountBalanceMapper {
    @SelectProvider(type=AccountBalanceSqlProvider.class, method="countByExample")
    long countByExample(AccountBalanceExample example);

    @DeleteProvider(type=AccountBalanceSqlProvider.class, method="deleteByExample")
    int deleteByExample(AccountBalanceExample example);

    @Insert({
        "insert into T_ACCOUNT_BALANCE (ID, CUSTOMER_ID, ",
        "BALANCE, CREATE_TIME, ",
        "IS_VALIDATE, UPDATE_TIME)",
        "values (#{id,jdbcType=INTEGER}, #{customerId,jdbcType=INTEGER}, ",
        "#{balance,jdbcType=DECIMAL}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{isValidate,jdbcType=tinyint}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    int insert(AccountBalance record);

    @InsertProvider(type=AccountBalanceSqlProvider.class, method="insertSelective")
    int insertSelective(AccountBalance record);

    @SelectProvider(type=AccountBalanceSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="ID", property="id", jdbcType=JdbcType.INTEGER),
        @Result(column="CUSTOMER_ID", property="customerId", jdbcType=JdbcType.INTEGER),
        @Result(column="BALANCE", property="balance", jdbcType=JdbcType.DECIMAL),
        @Result(column="CREATE_TIME", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="IS_VALIDATE", property="isValidate", jdbcType=JdbcType.tinyint),
        @Result(column="UPDATE_TIME", property="updateTime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<AccountBalance> selectByExample(AccountBalanceExample example);

    @UpdateProvider(type=AccountBalanceSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") AccountBalance record, @Param("example") AccountBalanceExample example);

    @UpdateProvider(type=AccountBalanceSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") AccountBalance record, @Param("example") AccountBalanceExample example);
}