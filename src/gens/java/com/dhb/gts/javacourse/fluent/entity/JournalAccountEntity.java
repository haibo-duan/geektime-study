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
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * JournalAccountEntity: 数据映射实体定义
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
    table = "T_JOURNAL_ACCOUNT",
    schema = "gts"
)
public class JournalAccountEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 主键 自增列
   */
  @TableId("ID")
  private Integer id;

  /**
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
   * 操作前的账户余额 单位 分
   */
  @TableField("BEFORE_BALANCE")
  private Long beforeBalance;

  /**
   * 当前账户余额  单位 分
   */
  @TableField("CURRENT_BALANCE")
  private Long currentBalance;

  /**
   * 用户ID
   */
  @TableField("CUSTOMER_ID")
  private Integer customerId;

  /**
   * 关联单据编号
   */
  @TableField("SOURCE_NO")
  private Integer sourceNo;

  /**
   * 记录类型 1 订单 2 退货 3 付款 4 退款
   */
  @TableField("TYPE")
  private Integer type;

  @Override
  public Serializable findPk() {
    return this.id;
  }

  @Override
  public final Class<? extends IEntity> entityClass() {
    return JournalAccountEntity.class;
  }

  @Override
  public final JournalAccountEntity changeTableBelongTo(TableSupplier supplier) {
    return super.changeTableBelongTo(supplier);
  }

  @Override
  public final JournalAccountEntity changeTableBelongTo(String table) {
    return super.changeTableBelongTo(table);
  }
}
