package com.dhb.gts.javacourse.mapper;

import com.dhb.gts.javacourse.entity.OrderSummary;
import com.dhb.gts.javacourse.entity.OrderSummaryExample;
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

public interface OrderSummaryMapper {
    @SelectProvider(type=OrderSummarySqlProvider.class, method="countByExample")
    long countByExample(OrderSummaryExample example);

    @DeleteProvider(type=OrderSummarySqlProvider.class, method="deleteByExample")
    int deleteByExample(OrderSummaryExample example);

    @Insert({
        "insert into T_ORDER_SUMMARY (ORDER_ID, ORDER_NO, ",
        "CUSTOMER_ID, PAYMENT_METHOD, ",
        "ORDER_AMOUNT, PAYMENT_MONEY, ",
        "CONSIGNEE_NAME, CONSIGNEE_ADDRESS, ",
        "CONSIGNEE_PHONE, EXPRESS_COMP, ",
        "EXPRESS_NO, CREATE_TIME, ",
        "IS_VALIDATE, UPDATE_TIME)",
        "values (#{orderId,jdbcType=INTEGER}, #{orderNo,jdbcType=INTEGER}, ",
        "#{customerId,jdbcType=INTEGER}, #{paymentMethod,jdbcType=TINYINT}, ",
        "#{orderAmount,jdbcType=DECIMAL}, #{paymentMoney,jdbcType=DECIMAL}, ",
        "#{consigneeName,jdbcType=VARCHAR}, #{consigneeAddress,jdbcType=VARCHAR}, ",
        "#{consigneePhone,jdbcType=VARCHAR}, #{expressComp,jdbcType=VARCHAR}, ",
        "#{expressNo,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{isValidate,jdbcType=TINYINT}, #{updateTime,jdbcType=TIMESTAMP})"
    })
    int insert(OrderSummary record);

    @InsertProvider(type=OrderSummarySqlProvider.class, method="insertSelective")
    int insertSelective(OrderSummary record);

    @SelectProvider(type=OrderSummarySqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="ORDER_ID", property="orderId", jdbcType=JdbcType.INTEGER),
        @Result(column="ORDER_NO", property="orderNo", jdbcType=JdbcType.INTEGER),
        @Result(column="CUSTOMER_ID", property="customerId", jdbcType=JdbcType.INTEGER),
        @Result(column="PAYMENT_METHOD", property="paymentMethod", jdbcType=JdbcType.TINYINT),
        @Result(column="ORDER_AMOUNT", property="orderAmount", jdbcType=JdbcType.DECIMAL),
        @Result(column="PAYMENT_MONEY", property="paymentMoney", jdbcType=JdbcType.DECIMAL),
        @Result(column="CONSIGNEE_NAME", property="consigneeName", jdbcType=JdbcType.VARCHAR),
        @Result(column="CONSIGNEE_ADDRESS", property="consigneeAddress", jdbcType=JdbcType.VARCHAR),
        @Result(column="CONSIGNEE_PHONE", property="consigneePhone", jdbcType=JdbcType.VARCHAR),
        @Result(column="EXPRESS_COMP", property="expressComp", jdbcType=JdbcType.VARCHAR),
        @Result(column="EXPRESS_NO", property="expressNo", jdbcType=JdbcType.VARCHAR),
        @Result(column="CREATE_TIME", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="IS_VALIDATE", property="isValidate", jdbcType=JdbcType.TINYINT),
        @Result(column="UPDATE_TIME", property="updateTime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<OrderSummary> selectByExample(OrderSummaryExample example);

    @UpdateProvider(type=OrderSummarySqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") OrderSummary record, @Param("example") OrderSummaryExample example);

    @UpdateProvider(type=OrderSummarySqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") OrderSummary record, @Param("example") OrderSummaryExample example);
}