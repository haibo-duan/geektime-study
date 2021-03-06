package com.dhb.hmilytcc.bank1.fluent.entity;

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
 * CancleLogEntity: 数据映射实体定义
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
    table = "t_cancel_log",
    schema = "gts01"
)
public class CancleLogEntity extends RichEntity {
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
    return CancleLogEntity.class;
  }

  @Override
  public final CancleLogEntity changeTableBelongTo(TableSupplier supplier) {
    return super.changeTableBelongTo(supplier);
  }

  @Override
  public final CancleLogEntity changeTableBelongTo(String table) {
    return super.changeTableBelongTo(table);
  }
}
