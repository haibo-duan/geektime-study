package com.dhb.gts.javacourse.mapper;

import com.dhb.gts.javacourse.entity.CustomerInfo;
import com.dhb.gts.javacourse.entity.CustomerInfoExample;
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

public interface CustomerInfoMapper {
    @SelectProvider(type=CustomerInfoSqlProvider.class, method="countByExample")
    long countByExample(CustomerInfoExample example);

    @DeleteProvider(type=CustomerInfoSqlProvider.class, method="deleteByExample")
    int deleteByExample(CustomerInfoExample example);

    @Insert({
        "insert into T_CUSTOMER_INFO (ID, USER_ID, ",
        "`NAME`, IDENTITY_CARD_TYPE, ",
        "IDENTITY_CARD_NO, MOBILE, ",
        "EMAIL, GENDER, CREATE_TIME, ",
        "BIRTHDAY, IS_VALIDATE, ",
        "UPDATE_TIME)",
        "values (#{id,jdbcType=INTEGER}, #{userId,jdbcType=INTEGER}, ",
        "#{name,jdbcType=VARCHAR}, #{identityCardType,jdbcType=tinyint}, ",
        "#{identityCardNo,jdbcType=VARCHAR}, #{mobile,jdbcType=VARCHAR}, ",
        "#{email,jdbcType=VARCHAR}, #{gender,jdbcType=CHAR}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{birthday,jdbcType=TIMESTAMP}, #{isValidate,jdbcType=tinyint}, ",
        "#{updateTime,jdbcType=TIMESTAMP})"
    })
    int insert(CustomerInfo record);

    @InsertProvider(type=CustomerInfoSqlProvider.class, method="insertSelective")
    int insertSelective(CustomerInfo record);

    @SelectProvider(type=CustomerInfoSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="ID", property="id", jdbcType=JdbcType.INTEGER),
        @Result(column="USER_ID", property="userId", jdbcType=JdbcType.INTEGER),
        @Result(column="NAME", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="IDENTITY_CARD_TYPE", property="identityCardType", jdbcType=JdbcType.tinyint),
        @Result(column="IDENTITY_CARD_NO", property="identityCardNo", jdbcType=JdbcType.VARCHAR),
        @Result(column="MOBILE", property="mobile", jdbcType=JdbcType.VARCHAR),
        @Result(column="EMAIL", property="email", jdbcType=JdbcType.VARCHAR),
        @Result(column="GENDER", property="gender", jdbcType=JdbcType.CHAR),
        @Result(column="CREATE_TIME", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="BIRTHDAY", property="birthday", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="IS_VALIDATE", property="isValidate", jdbcType=JdbcType.tinyint),
        @Result(column="UPDATE_TIME", property="updateTime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<CustomerInfo> selectByExample(CustomerInfoExample example);

    @UpdateProvider(type=CustomerInfoSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") CustomerInfo record, @Param("example") CustomerInfoExample example);

    @UpdateProvider(type=CustomerInfoSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") CustomerInfo record, @Param("example") CustomerInfoExample example);
}