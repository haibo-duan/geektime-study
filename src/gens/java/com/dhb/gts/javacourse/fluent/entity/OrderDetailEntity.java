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
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * OrderDetailEntity: 数据映射实体定义
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
    table = "T_ORDER_DETAIL",
    schema = "gts"
)
public class OrderDetailEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 主键-自增列
   */
  @TableId("ID")
  private Integer id;

  /**
   * 创建时间
   */
  @TableField(
      value = "CREATE_TIME",
      insert = "now()"
  )
  private Date createTime;

  /**
   * 最后修改时间
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
   * 核算平均成本价格 单位 分 此处为int,比bigint节约4个字节
   */
  @TableField("AVERAGE_COST")
  private Integer averageCost;

  /**
   * 数量
   */
  @TableField("NUMBERS")
  private Integer numbers;

  /**
   * 订单汇总表ID
   */
  @TableField("ORDER_ID")
  private Integer orderId;

  /**
   * 产品单价 单位 分 此处为int,比bigint节约4个字节
   */
  @TableField("PRICE")
  private Integer price;

  /**
   * 产品编号
   */
  @TableField("PRODUCT_ID")
  private Integer productId;

  /**
   * 产品名称
   */
  @TableField("PRODUCT_NAME")
  private String productName;

  /**
   * 产品总金额 单位 分 此处为int,比bigint节约4个字节
   */
  @TableField("TOTAL")
  private Integer total;

  @Override
  public Serializable findPk() {
    return this.id;
  }

  @Override
  public final Class<? extends IEntity> entityClass() {
    return OrderDetailEntity.class;
  }

  @Override
  public final OrderDetailEntity changeTableBelongTo(TableSupplier supplier) {
    return super.changeTableBelongTo(supplier);
  }

  @Override
  public final OrderDetailEntity changeTableBelongTo(String table) {
    return super.changeTableBelongTo(table);
  }
}
