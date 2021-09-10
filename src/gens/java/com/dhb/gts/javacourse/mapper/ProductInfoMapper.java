package com.dhb.gts.javacourse.mapper;

import com.dhb.gts.javacourse.entity.ProductInfo;
import com.dhb.gts.javacourse.entity.ProductInfoExample;
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

public interface ProductInfoMapper {
    @SelectProvider(type=ProductInfoSqlProvider.class, method="countByExample")
    long countByExample(ProductInfoExample example);

    @DeleteProvider(type=ProductInfoSqlProvider.class, method="deleteByExample")
    int deleteByExample(ProductInfoExample example);

    @Insert({
        "insert into T_PRODUCT_INFO (PRODUCT_ID, PRODUCT_CODE, ",
        "PRODUCT_NAME, SUPPLIER_ID, ",
        "PRICE, ACERAGE_COST, ",
        "`STATUS`, PRODUCTION_DATE, ",
        "CREATE_TIME, IS_VALIDATE, ",
        "UPDATE_TIME)",
        "values (#{productId,jdbcType=INTEGER}, #{productCode,jdbcType=CHAR}, ",
        "#{productName,jdbcType=VARCHAR}, #{supplierId,jdbcType=INTEGER}, ",
        "#{price,jdbcType=DECIMAL}, #{acerageCost,jdbcType=DECIMAL}, ",
        "#{status,jdbcType=TINYINT}, #{productionDate,jdbcType=TIMESTAMP}, ",
        "#{createTime,jdbcType=TIMESTAMP}, #{isValidate,jdbcType=tinyint}, ",
        "#{updateTime,jdbcType=TIMESTAMP})"
    })
    int insert(ProductInfo record);

    @InsertProvider(type=ProductInfoSqlProvider.class, method="insertSelective")
    int insertSelective(ProductInfo record);

    @SelectProvider(type=ProductInfoSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="PRODUCT_ID", property="productId", jdbcType=JdbcType.INTEGER),
        @Result(column="PRODUCT_CODE", property="productCode", jdbcType=JdbcType.CHAR),
        @Result(column="PRODUCT_NAME", property="productName", jdbcType=JdbcType.VARCHAR),
        @Result(column="SUPPLIER_ID", property="supplierId", jdbcType=JdbcType.INTEGER),
        @Result(column="PRICE", property="price", jdbcType=JdbcType.DECIMAL),
        @Result(column="ACERAGE_COST", property="acerageCost", jdbcType=JdbcType.DECIMAL),
        @Result(column="STATUS", property="status", jdbcType=JdbcType.TINYINT),
        @Result(column="PRODUCTION_DATE", property="productionDate", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="CREATE_TIME", property="createTime", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="IS_VALIDATE", property="isValidate", jdbcType=JdbcType.tinyint),
        @Result(column="UPDATE_TIME", property="updateTime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<ProductInfo> selectByExample(ProductInfoExample example);

    @UpdateProvider(type=ProductInfoSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") ProductInfo record, @Param("example") ProductInfoExample example);

    @UpdateProvider(type=ProductInfoSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") ProductInfo record, @Param("example") ProductInfoExample example);
}