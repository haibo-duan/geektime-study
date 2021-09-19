package com.dhb.gts.javacourse.fluent.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
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
 * CustomerLoginEntity: 数据映射实体定义
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
    table = "T_CUSTOMER_LOGIN",
    schema = "gts"
)
public class CustomerLoginEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 用户ID,主键 自增列
   */
  @TableId(
      value = "USER_ID",
      auto = false
  )
  private Integer userId;

  /**
   * 用户创建时间
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
   * 用户登陆密码，MD5方式存储
   */
  @TableField("PASSWORD")
  private String password;

  /**
   * 用户状态：1有效 0 无效
   */
  @TableField("STATES")
  private Integer states;

  /**
   * 用户登陆名
   */
  @TableField("USER_NAME")
  private String userName;

  @Override
  public Serializable findPk() {
    return this.userId;
  }

  @Override
  public final Class<? extends IEntity> entityClass() {
    return CustomerLoginEntity.class;
  }

  @Override
  public final CustomerLoginEntity changeTableBelongTo(TableSupplier supplier) {
    return super.changeTableBelongTo(supplier);
  }

  @Override
  public final CustomerLoginEntity changeTableBelongTo(String table) {
    return super.changeTableBelongTo(table);
  }
}
