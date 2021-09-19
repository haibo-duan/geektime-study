package com.dhb.gts.javacourse.fluent.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.LogicDelete;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.IEntity;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import cn.org.atool.fluent.mybatis.functions.TableSupplier;
import java.io.Serializable;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * ProductInfoEntity: 数据映射实体定义
 *
 * @author Powered By Fluent Mybatis
 */
@SuppressWarnings({"unchecked"})
@Data
@Accessors(
    chain = true
)
@EqualsAndHashCode(
    callSuper = false
)
@FluentMybatis(
    table = "T_PRODUCT_INFO",
    schema = "gts"
)
public class ProductInfoEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 商品ID
   */
  @TableId("PRODUCT_ID")
  private Integer productId;

  /**
   * 创建时间
   */
  @TableField(
      value = "CREATE_TIME",
      insert = "now()"
  )
  private Date createTime;

  /**
   * 修改时间
   */
  @TableField(
      value = "UPDATE_TIME",
      insert = "now()",
      update = "now()"
  )
  private Date updateTime;

  /**
   * 数据是否有效标识：1有效数据，2 无效数据
   */
  @TableField(
      value = "IS_VALIDATE",
      insert = "0"
  )
  @LogicDelete
  private Integer isValidate;

  /**
   * 核算平均成本 单位 分
   */
  @TableField("ACERAGE_COST")
  private Integer acerageCost;

  /**
   * 商品单价
   */
  @TableField("PRICE")
  private BigDecimal price;

  /**
   * 商品编码
   */
  @TableField("PRODUCT_CODE")
  private String productCode;

  /**
   * 商品名称
   */
  @TableField("PRODUCT_NAME")
  private String productName;

  /**
   * 生产日期
   */
  @TableField("PRODUCTION_DATE")
  private Date productionDate;

  /**
   * 商品状态 0 不可售 1 可售
   */
  @TableField("STATUS")
  private Integer status;

  /**
   * 供应商ID
   */
  @TableField("SUPPLIER_ID")
  private Integer supplierId;

  @Override
  public Serializable findPk() {
    return this.productId;
  }

  @Override
  public final Class<? extends IEntity> entityClass() {
    return ProductInfoEntity.class;
  }

  @Override
  public final ProductInfoEntity changeTableBelongTo(TableSupplier supplier) {
    return super.changeTableBelongTo(supplier);
  }

  @Override
  public final ProductInfoEntity changeTableBelongTo(String table) {
    return super.changeTableBelongTo(table);
  }
}
