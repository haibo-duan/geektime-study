package com.dhb.gts.javacourse.mapper;

import com.dhb.gts.javacourse.entity.JournalAccount;
import com.dhb.gts.javacourse.entity.JournalAccountExample;
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

public interface JournalAccountMapper {
    @SelectProvider(type=JournalAccountSqlProvider.class, method="countByExample")
    long countByExample(JournalAccountExample example);

    @DeleteProvider(type=JournalAccountSqlProvider.class, method="deleteByExample")
    int deleteByExample(JournalAccountExample example);

    @Insert({
        "insert into T_JOURNAL_ACCOUNT (ID, CUSTOMER_ID, ",
        "`TYPE`, SOURCE_NO, ",
        "BEFORE_BALANCE, CURRENT_BALANCE, ",
        "CREATE_TIME, IS_VALIDATE, ",
        "UPDATE_TIME)",
        "values (#{id,jdbcType=INTEGER}, #{customerId,jdbcType=INTEGER}, ",
        "#{type,jdbcType=TINYINT}, #{sourceNo,jdbcType=INTEGER}, ",
        "#{beforeBalance,jdbcType=DECIMAL}, #{currentBalance,jdbcType=DECIMAL}, ",
        "#{createTime,jdbcType=TIMESTAMP}, #{isValidate,jdbcType=TINYINT}, ",
        "#{updateTime,jdbcType=TIMESTAMP})"
    })
    int insert(JournalAccount record);

    @InsertProvider(type=JournalAccountSqlProvider.class, method="insertSelective")
    int insertSelective(JournalAccount record);

    @SelectProvider(type=JournalAccountSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="ID", property="id", jdbcType=JdbcType.INTEGER),
        @Result(column="CUSTOMER_ID", property="customerId", jdbcType=JdbcType.INTEGER),
        @Result(column="TYPE", property="type", jdbcType=JdbcType.TINYINT),
        @Result(column="SOURCE_NO", property="sourceNo", jdbcType=JdbcType.INTEGER),
        @Result(column="BEFORE_BALANCE", property="beforeBalance", jdbcType=JdbcType.DECIMAL),
        @Result(column="CURRENT_BALANCE", property="currentBalance", jdbcType=JdbcType.DECIMAL),
        @Result(column="CREATE_TIME", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="IS_VALIDATE", property="isValidate", jdbcType=JdbcType.TINYINT),
        @Result(column="UPDATE_TIME", property="updateTime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<JournalAccount> selectByExample(JournalAccountExample example);

    @UpdateProvider(type=JournalAccountSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") JournalAccount record, @Param("example") JournalAccountExample example);

    @UpdateProvider(type=JournalAccountSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") JournalAccount record, @Param("example") JournalAccountExample example);
}