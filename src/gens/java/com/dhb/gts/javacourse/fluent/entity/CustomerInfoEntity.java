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
 * CustomerInfoEntity: 数据映射实体定义
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
    table = "T_CUSTOMER_INFO",
    schema = "gts"
)
public class CustomerInfoEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 主键 自增列
   */
  @TableId("ID")
  private Integer id;

  /**
   * 用户注册时间
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
   * 用户生日
   */
  @TableField("BIRTHDAY")
  private Date birthday;

  /**
   * 用户邮箱
   */
  @TableField("EMAIL")
  private String email;

  /**
   * 用户性别
   */
  @TableField("GENDER")
  private String gender;

  /**
   * 身份证件号码
   */
  @TableField("IDENTITY_CARD_NO")
  private String identityCardNo;

  /**
   * 用户证件类型：1 身份证，2 军官证，3 护照
   */
  @TableField("IDENTITY_CARD_TYPE")
  private Integer identityCardType;

  /**
   * 用户手机号
   */
  @TableField("MOBILE")
  private String mobile;

  /**
   * 用户姓名
   */
  @TableField("NAME")
  private String name;

  /**
   * 用户ID 对外关联 T_CUSTOMER表
   */
  @TableField("USER_ID")
  private Integer userId;

  @Override
  public Serializable findPk() {
    return this.id;
  }

  @Override
  public final Class<? extends IEntity> entityClass() {
    return CustomerInfoEntity.class;
  }

  @Override
  public final CustomerInfoEntity changeTableBelongTo(TableSupplier supplier) {
    return super.changeTableBelongTo(supplier);
  }

  @Override
  public final CustomerInfoEntity changeTableBelongTo(String table) {
    return super.changeTableBelongTo(table);
  }
}
