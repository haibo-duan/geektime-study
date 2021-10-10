package com.dhb.bank.transfer.orm.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.IEntity;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import cn.org.atool.fluent.mybatis.functions.TableSupplier;
import java.io.Serializable;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * TryLogEntity: 数据映射实体定义
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
    table = "t_try_log",
    schema = "gts01"
)
public class TryLogEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 事务id
   */
  @TableId(
      value = "tx_no",
      auto = false
  )
  private String txNo;

  /**
   */
  @TableField("create_time")
  private Date createTime;

  @Override
  public Serializable findPk() {
    return this.txNo;
  }

  @Override
  public final Class<? extends IEntity> entityClass() {
    return TryLogEntity.class;
  }

  @Override
  public final TryLogEntity changeTableBelongTo(TableSupplier supplier) {
    return super.changeTableBelongTo(supplier);
  }

  @Override
  public final TryLogEntity changeTableBelongTo(String table) {
    return super.changeTableBelongTo(table);
  }
}
