package com.dhb.gts.javacourse.mapper;

import com.dhb.gts.javacourse.entity.OrderDetail;
import com.dhb.gts.javacourse.entity.OrderDetailExample;
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

public interface OrderDetailMapper {
    @SelectProvider(type=OrderDetailSqlProvider.class, method="countByExample")
    long countByExample(OrderDetailExample example);

    @DeleteProvider(type=OrderDetailSqlProvider.class, method="deleteByExample")
    int deleteByExample(OrderDetailExample example);

    @Insert({
        "insert into T_ORDER_DETAIL (ID, ORDER_ID, ",
        "PRODUCT_ID, PRODUCT_NAME, ",
        "NUMBERS, PRICE, ",
        "AVERAGE_COST, TOTAL, ",
        "CREATE_TIME, IS_VALIDATE, ",
        "UPDATE_TIME)",
        "values (#{id,jdbcType=INTEGER}, #{orderId,jdbcType=INTEGER}, ",
        "#{productId,jdbcType=INTEGER}, #{productName,jdbcType=VARCHAR}, ",
        "#{numbers,jdbcType=INTEGER}, #{price,jdbcType=DECIMAL}, ",
        "#{averageCost,jdbcType=DECIMAL}, #{total,jdbcType=DECIMAL}, ",
        "#{createTime,jdbcType=TIMESTAMP}, #{isValidate,jdbcType=tinyint}, ",
        "#{updateTime,jdbcType=TIMESTAMP})"
    })
    int insert(OrderDetail record);

    @InsertProvider(type=OrderDetailSqlProvider.class, method="insertSelective")
    int insertSelective(OrderDetail record);

    @SelectProvider(type=OrderDetailSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="ID", property="id", jdbcType=JdbcType.INTEGER),
        @Result(column="ORDER_ID", property="orderId", jdbcType=JdbcType.INTEGER),
        @Result(column="PRODUCT_ID", property="productId", jdbcType=JdbcType.INTEGER),
        @Result(column="PRODUCT_NAME", property="productName", jdbcType=JdbcType.VARCHAR),
        @Result(column="NUMBERS", property="numbers", jdbcType=JdbcType.INTEGER),
        @Result(column="PRICE", property="price", jdbcType=JdbcType.DECIMAL),
        @Result(column="AVERAGE_COST", property="averageCost", jdbcType=JdbcType.DECIMAL),
        @Result(column="TOTAL", property="total", jdbcType=JdbcType.DECIMAL),
        @Result(column="CREATE_TIME", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="IS_VALIDATE", property="isValidate", jdbcType=JdbcType.tinyint),
        @Result(column="UPDATE_TIME", property="updateTime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<OrderDetail> selectByExample(OrderDetailExample example);

    @UpdateProvider(type=OrderDetailSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") OrderDetail record, @Param("example") OrderDetailExample example);

    @UpdateProvider(type=OrderDetailSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") OrderDetail record, @Param("example") OrderDetailExample example);
}