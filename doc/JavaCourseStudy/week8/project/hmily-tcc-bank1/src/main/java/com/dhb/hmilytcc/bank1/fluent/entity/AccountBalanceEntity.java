package com.dhb.hmilytcc.bank1.fluent.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.IEntity;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import cn.org.atool.fluent.mybatis.functions.TableSupplier;
import java.io.Serializable;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * AccountBalanceEntity: 数据映射实体定义
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
    table = "t_account_balance",
    schema = "gts01"
)
public class AccountBalanceEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 主键 自增列
   */
  @TableId("id")
  private Integer id;

  /**
   * 用户注册时间
   */
  @TableField("create_time")
  private Date createTime;

  /**
   * 最后修改时间
   */
  @TableField("update_time")
  private Date updateTime;

  /**
   * 数据是否有效标识：1有效数据，2 无效数据
   */
  @TableField("is_validate")
  private Integer isValidate;

  /**
   * 客户余额 单位 分
   */
  @TableField("balance")
  private Long balance;

  /**
   * 用户编号
   */
  @TableField("customer_id")
  private Integer customerId;

  @Override
  public Serializable findPk() {
    return this.id;
  }

  @Override
  public final Class<? extends IEntity> entityClass() {
    return AccountBalanceEntity.class;
  }

  @Override
  public final AccountBalanceEntity changeTableBelongTo(TableSupplier supplier) {
    return super.changeTableBelongTo(supplier);
  }

  @Override
  public final AccountBalanceEntity changeTableBelongTo(String table) {
    return super.changeTableBelongTo(table);
  }
}
