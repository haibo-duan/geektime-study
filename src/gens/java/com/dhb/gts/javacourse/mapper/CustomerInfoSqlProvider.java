package com.dhb.gts.javacourse.mapper;

import com.dhb.gts.javacourse.entity.CustomerInfo;
import com.dhb.gts.javacourse.entity.CustomerInfoExample.Criteria;
import com.dhb.gts.javacourse.entity.CustomerInfoExample.Criterion;
import com.dhb.gts.javacourse.entity.CustomerInfoExample;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.jdbc.SQL;

public class CustomerInfoSqlProvider {

    public String countByExample(CustomerInfoExample example) {
        SQL sql = new SQL();
        sql.SELECT("count(*)").FROM("T_CUSTOMER_INFO");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    public String deleteByExample(CustomerInfoExample example) {
        SQL sql = new SQL();
        sql.DELETE_FROM("T_CUSTOMER_INFO");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    public String insertSelective(CustomerInfo record) {
        SQL sql = new SQL();
        sql.INSERT_INTO("T_CUSTOMER_INFO");
        
        if (record.getId() != null) {
            sql.VALUES("ID", "#{id,jdbcType=INTEGER}");
        }
        
        if (record.getUserId() != null) {
            sql.VALUES("USER_ID", "#{userId,jdbcType=INTEGER}");
        }
        
        if (record.getName() != null) {
            sql.VALUES("`NAME`", "#{name,jdbcType=VARCHAR}");
        }
        
        if (record.getIdentityCardType() != null) {
            sql.VALUES("IDENTITY_CARD_TYPE", "#{identityCardType,jdbcType=TINYINT}");
        }
        
        if (record.getIdentityCardNo() != null) {
            sql.VALUES("IDENTITY_CARD_NO", "#{identityCardNo,jdbcType=VARCHAR}");
        }
        
        if (record.getMobile() != null) {
            sql.VALUES("MOBILE", "#{mobile,jdbcType=VARCHAR}");
        }
        
        if (record.getEmail() != null) {
            sql.VALUES("EMAIL", "#{email,jdbcType=VARCHAR}");
        }
        
        if (record.getGender() != null) {
            sql.VALUES("GENDER", "#{gender,jdbcType=CHAR}");
        }
        
        if (record.getCreateTime() != null) {
            sql.VALUES("CREATE_TIME", "#{createTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getBirthday() != null) {
            sql.VALUES("BIRTHDAY", "#{birthday,jdbcType=TIMESTAMP}");
        }
        
        if (record.getIsValidate() != null) {
            sql.VALUES("IS_VALIDATE", "#{isValidate,jdbcType=TINYINT}");
        }
        
        if (record.getUpdateTime() != null) {
            sql.VALUES("UPDATE_TIME", "#{updateTime,jdbcType=TIMESTAMP}");
        }
        
        return sql.toString();
    }

    public String selectByExample(CustomerInfoExample example) {
        SQL sql = new SQL();
        if (example != null && example.isDistinct()) {
            sql.SELECT_DISTINCT("ID");
        } else {
            sql.SELECT("ID");
        }
        sql.SELECT("USER_ID");
        sql.SELECT("`NAME`");
        sql.SELECT("IDENTITY_CARD_TYPE");
        sql.SELECT("IDENTITY_CARD_NO");
        sql.SELECT("MOBILE");
        sql.SELECT("EMAIL");
        sql.SELECT("GENDER");
        sql.SELECT("CREATE_TIME");
        sql.SELECT("BIRTHDAY");
        sql.SELECT("IS_VALIDATE");
        sql.SELECT("UPDATE_TIME");
        sql.FROM("T_CUSTOMER_INFO");
        applyWhere(sql, example, false);
        
        if (example != null && example.getOrderByClause() != null) {
            sql.ORDER_BY(example.getOrderByClause());
        }
        
        return sql.toString();
    }

    public String updateByExampleSelective(Map<String, Object> parameter) {
        CustomerInfo record = (CustomerInfo) parameter.get("record");
        CustomerInfoExample example = (CustomerInfoExample) parameter.get("example");
        
        SQL sql = new SQL();
        sql.UPDATE("T_CUSTOMER_INFO");
        
        if (record.getId() != null) {
            sql.SET("ID = #{record.id,jdbcType=INTEGER}");
        }
        
        if (record.getUserId() != null) {
            sql.SET("USER_ID = #{record.userId,jdbcType=INTEGER}");
        }
        
        if (record.getName() != null) {
            sql.SET("`NAME` = #{record.name,jdbcType=VARCHAR}");
        }
        
        if (record.getIdentityCardType() != null) {
            sql.SET("IDENTITY_CARD_TYPE = #{record.identityCardType,jdbcType=TINYINT}");
        }
        
        if (record.getIdentityCardNo() != null) {
            sql.SET("IDENTITY_CARD_NO = #{record.identityCardNo,jdbcType=VARCHAR}");
        }
        
        if (record.getMobile() != null) {
            sql.SET("MOBILE = #{record.mobile,jdbcType=VARCHAR}");
        }
        
        if (record.getEmail() != null) {
            sql.SET("EMAIL = #{record.email,jdbcType=VARCHAR}");
        }
        
        if (record.getGender() != null) {
            sql.SET("GENDER = #{record.gender,jdbcType=CHAR}");
        }
        
        if (record.getCreateTime() != null) {
            sql.SET("CREATE_TIME = #{record.createTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getBirthday() != null) {
            sql.SET("BIRTHDAY = #{record.birthday,jdbcType=TIMESTAMP}");
        }
        
        if (record.getIsValidate() != null) {
            sql.SET("IS_VALIDATE = #{record.isValidate,jdbcType=TINYINT}");
        }
        
        if (record.getUpdateTime() != null) {
            sql.SET("UPDATE_TIME = #{record.updateTime,jdbcType=TIMESTAMP}");
        }
        
        applyWhere(sql, example, true);
        return sql.toString();
    }

    public String updateByExample(Map<String, Object> parameter) {
        SQL sql = new SQL();
        sql.UPDATE("T_CUSTOMER_INFO");
        
        sql.SET("ID = #{record.id,jdbcType=INTEGER}");
        sql.SET("USER_ID = #{record.userId,jdbcType=INTEGER}");
        sql.SET("`NAME` = #{record.name,jdbcType=VARCHAR}");
        sql.SET("IDENTITY_CARD_TYPE = #{record.identityCardType,jdbcType=TINYINT}");
        sql.SET("IDENTITY_CARD_NO = #{record.identityCardNo,jdbcType=VARCHAR}");
        sql.SET("MOBILE = #{record.mobile,jdbcType=VARCHAR}");
        sql.SET("EMAIL = #{record.email,jdbcType=VARCHAR}");
        sql.SET("GENDER = #{record.gender,jdbcType=CHAR}");
        sql.SET("CREATE_TIME = #{record.createTime,jdbcType=TIMESTAMP}");
        sql.SET("BIRTHDAY = #{record.birthday,jdbcType=TIMESTAMP}");
        sql.SET("IS_VALIDATE = #{record.isValidate,jdbcType=TINYINT}");
        sql.SET("UPDATE_TIME = #{record.updateTime,jdbcType=TIMESTAMP}");
        
        CustomerInfoExample example = (CustomerInfoExample) parameter.get("example");
        applyWhere(sql, example, true);
        return sql.toString();
    }

    protected void applyWhere(SQL sql, CustomerInfoExample example, boolean includeExamplePhrase) {
        if (example == null) {
            return;
        }
        
        String parmPhrase1;
        String parmPhrase1_th;
        String parmPhrase2;
        String parmPhrase2_th;
        String parmPhrase3;
        String parmPhrase3_th;
        if (includeExamplePhrase) {
            parmPhrase1 = "%s #{example.oredCriteria[%d].allCriteria[%d].value}";
            parmPhrase1_th = "%s #{example.oredCriteria[%d].allCriteria[%d].value,typeHandler=%s}";
            parmPhrase2 = "%s #{example.oredCriteria[%d].allCriteria[%d].value} and #{example.oredCriteria[%d].criteria[%d].secondValue}";
            parmPhrase2_th = "%s #{example.oredCriteria[%d].allCriteria[%d].value,typeHandler=%s} and #{example.oredCriteria[%d].criteria[%d].secondValue,typeHandler=%s}";
            parmPhrase3 = "#{example.oredCriteria[%d].allCriteria[%d].value[%d]}";
            parmPhrase3_th = "#{example.oredCriteria[%d].allCriteria[%d].value[%d],typeHandler=%s}";
        } else {
            parmPhrase1 = "%s #{oredCriteria[%d].allCriteria[%d].value}";
            parmPhrase1_th = "%s #{oredCriteria[%d].allCriteria[%d].value,typeHandler=%s}";
            parmPhrase2 = "%s #{oredCriteria[%d].allCriteria[%d].value} and #{oredCriteria[%d].criteria[%d].secondValue}";
            parmPhrase2_th = "%s #{oredCriteria[%d].allCriteria[%d].value,typeHandler=%s} and #{oredCriteria[%d].criteria[%d].secondValue,typeHandler=%s}";
            parmPhrase3 = "#{oredCriteria[%d].allCriteria[%d].value[%d]}";
            parmPhrase3_th = "#{oredCriteria[%d].allCriteria[%d].value[%d],typeHandler=%s}";
        }
        
        StringBuilder sb = new StringBuilder();
        List<Criteria> oredCriteria = example.getOredCriteria();
        boolean firstCriteria = true;
        for (int i = 0; i < oredCriteria.size(); i++) {
            Criteria criteria = oredCriteria.get(i);
            if (criteria.isValid()) {
                if (firstCriteria) {
                    firstCriteria = false;
                } else {
                    sb.append(" or ");
                }
                
                sb.append('(');
                List<Criterion> criterions = criteria.getAllCriteria();
                boolean firstCriterion = true;
                for (int j = 0; j < criterions.size(); j++) {
                    Criterion criterion = criterions.get(j);
                    if (firstCriterion) {
                        firstCriterion = false;
                    } else {
                        sb.append(" and ");
                    }
                    
                    if (criterion.isNoValue()) {
                        sb.append(criterion.getCondition());
                    } else if (criterion.isSingleValue()) {
                        if (criterion.getTypeHandler() == null) {
                            sb.append(String.format(parmPhrase1, criterion.getCondition(), i, j));
                        } else {
                            sb.append(String.format(parmPhrase1_th, criterion.getCondition(), i, j,criterion.getTypeHandler()));
                        }
                    } else if (criterion.isBetweenValue()) {
                        if (criterion.getTypeHandler() == null) {
                            sb.append(String.format(parmPhrase2, criterion.getCondition(), i, j, i, j));
                        } else {
                            sb.append(String.format(parmPhrase2_th, criterion.getCondition(), i, j, criterion.getTypeHandler(), i, j, criterion.getTypeHandler()));
                        }
                    } else if (criterion.isListValue()) {
                        sb.append(criterion.getCondition());
                        sb.append(" (");
                        List<?> listItems = (List<?>) criterion.getValue();
                        boolean comma = false;
                        for (int k = 0; k < listItems.size(); k++) {
                            if (comma) {
                                sb.append(", ");
                            } else {
                                comma = true;
                            }
                            if (criterion.getTypeHandler() == null) {
                                sb.append(String.format(parmPhrase3, i, j, k));
                            } else {
                                sb.append(String.format(parmPhrase3_th, i, j, k, criterion.getTypeHandler()));
                            }
                        }
                        sb.append(')');
                    }
                }
                sb.append(')');
            }
        }
        
        if (sb.length() > 0) {
            sql.WHERE(sb.toString());
        }
    }
}