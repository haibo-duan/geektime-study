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
 * OrderSummaryEntity: 数据映射实体定义
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
    table = "T_ORDER_SUMMARY",
    schema = "gts"
)
public class OrderSummaryEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 订单ID
   */
  @TableId("ORDER_ID")
  private Long orderId;

  /**
   * 创建时间
   */
  @TableField(
      value = "CREATE_TIME",
      insert = "now()"
  )
  private Date createTime;

  /**
   * 最新更新时间
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
   * 收货人详细
   */
  @TableField("CONSIGNEE_ADDRESS")
  private String consigneeAddress;

  /**
   * 收货人姓名
   */
  @TableField("CONSIGNEE_NAME")
  private String consigneeName;

  /**
   * 收货人联系电话
   */
  @TableField("CONSIGNEE_PHONE")
  private String consigneePhone;

  /**
   * 下单用户ID
   */
  @TableField("CUSTOMER_ID")
  private Integer customerId;

  /**
   * 快递公司名称
   */
  @TableField("EXPRESS_COMP")
  private String expressComp;

  /**
   * 快递单号
   */
  @TableField("EXPRESS_NO")
  private String expressNo;

  /**
   * 订单汇总金额 单位 分  此处为int,比bigint节约4个字节
   */
  @TableField("ORDER_AMOUNT")
  private Integer orderAmount;

  /**
   * 订单编号
   */
  @TableField("ORDER_NO")
  private Integer orderNo;

  /**
   * 支付方式：1现金，2余额，3网银，4支付宝，5微信
   */
  @TableField("PAYMENT_METHOD")
  private Integer paymentMethod;

  /**
   * 支付金额  单位 分  此处为int,比bigint节约4个字节
   */
  @TableField("PAYMENT_MONEY")
  private Integer paymentMoney;

  @Override
  public Serializable findPk() {
    return this.orderId;
  }

  @Override
  public final Class<? extends IEntity> entityClass() {
    return OrderSummaryEntity.class;
  }

  @Override
  public final OrderSummaryEntity changeTableBelongTo(TableSupplier supplier) {
    return super.changeTableBelongTo(supplier);
  }

  @Override
  public final OrderSummaryEntity changeTableBelongTo(String table) {
    return super.changeTableBelongTo(table);
  }
}
