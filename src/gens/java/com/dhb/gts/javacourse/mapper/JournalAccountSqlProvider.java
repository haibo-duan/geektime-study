package com.dhb.gts.javacourse.mapper;

import com.dhb.gts.javacourse.entity.JournalAccount;
import com.dhb.gts.javacourse.entity.JournalAccountExample.Criteria;
import com.dhb.gts.javacourse.entity.JournalAccountExample.Criterion;
import com.dhb.gts.javacourse.entity.JournalAccountExample;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.jdbc.SQL;

public class JournalAccountSqlProvider {

    public String countByExample(JournalAccountExample example) {
        SQL sql = new SQL();
        sql.SELECT("count(*)").FROM("T_JOURNAL_ACCOUNT");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    public String deleteByExample(JournalAccountExample example) {
        SQL sql = new SQL();
        sql.DELETE_FROM("T_JOURNAL_ACCOUNT");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    public String insertSelective(JournalAccount record) {
        SQL sql = new SQL();
        sql.INSERT_INTO("T_JOURNAL_ACCOUNT");
        
        if (record.getId() != null) {
            sql.VALUES("ID", "#{id,jdbcType=INTEGER}");
        }
        
        if (record.getCustomerId() != null) {
            sql.VALUES("CUSTOMER_ID", "#{customerId,jdbcType=INTEGER}");
        }
        
        if (record.getType() != null) {
            sql.VALUES("`TYPE`", "#{type,jdbcType=tinyint}");
        }
        
        if (record.getSourceNo() != null) {
            sql.VALUES("SOURCE_NO", "#{sourceNo,jdbcType=INTEGER}");
        }
        
        if (record.getBeforeBalance() != null) {
            sql.VALUES("BEFORE_BALANCE", "#{beforeBalance,jdbcType=DECIMAL}");
        }
        
        if (record.getCurrentBalance() != null) {
            sql.VALUES("CURRENT_BALANCE", "#{currentBalance,jdbcType=DECIMAL}");
        }
        
        if (record.getCreateTime() != null) {
            sql.VALUES("CREATE_TIME", "#{createTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getIsValidate() != null) {
            sql.VALUES("IS_VALIDATE", "#{isValidate,jdbcType=tinyint}");
        }
        
        if (record.getUpdateTime() != null) {
            sql.VALUES("UPDATE_TIME", "#{updateTime,jdbcType=TIMESTAMP}");
        }
        
        return sql.toString();
    }

    public String selectByExample(JournalAccountExample example) {
        SQL sql = new SQL();
        if (example != null && example.isDistinct()) {
            sql.SELECT_DISTINCT("ID");
        } else {
            sql.SELECT("ID");
        }
        sql.SELECT("CUSTOMER_ID");
        sql.SELECT("`TYPE`");
        sql.SELECT("SOURCE_NO");
        sql.SELECT("BEFORE_BALANCE");
        sql.SELECT("CURRENT_BALANCE");
        sql.SELECT("CREATE_TIME");
        sql.SELECT("IS_VALIDATE");
        sql.SELECT("UPDATE_TIME");
        sql.FROM("T_JOURNAL_ACCOUNT");
        applyWhere(sql, example, false);
        
        if (example != null && example.getOrderByClause() != null) {
            sql.ORDER_BY(example.getOrderByClause());
        }
        
        return sql.toString();
    }

    public String updateByExampleSelective(Map<String, Object> parameter) {
        JournalAccount record = (JournalAccount) parameter.get("record");
        JournalAccountExample example = (JournalAccountExample) parameter.get("example");
        
        SQL sql = new SQL();
        sql.UPDATE("T_JOURNAL_ACCOUNT");
        
        if (record.getId() != null) {
            sql.SET("ID = #{record.id,jdbcType=INTEGER}");
        }
        
        if (record.getCustomerId() != null) {
            sql.SET("CUSTOMER_ID = #{record.customerId,jdbcType=INTEGER}");
        }
        
        if (record.getType() != null) {
            sql.SET("`TYPE` = #{record.type,jdbcType=tinyint}");
        }
        
        if (record.getSourceNo() != null) {
            sql.SET("SOURCE_NO = #{record.sourceNo,jdbcType=INTEGER}");
        }
        
        if (record.getBeforeBalance() != null) {
            sql.SET("BEFORE_BALANCE = #{record.beforeBalance,jdbcType=DECIMAL}");
        }
        
        if (record.getCurrentBalance() != null) {
            sql.SET("CURRENT_BALANCE = #{record.currentBalance,jdbcType=DECIMAL}");
        }
        
        if (record.getCreateTime() != null) {
            sql.SET("CREATE_TIME = #{record.createTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getIsValidate() != null) {
            sql.SET("IS_VALIDATE = #{record.isValidate,jdbcType=tinyint}");
        }
        
        if (record.getUpdateTime() != null) {
            sql.SET("UPDATE_TIME = #{record.updateTime,jdbcType=TIMESTAMP}");
        }
        
        applyWhere(sql, example, true);
        return sql.toString();
    }

    public String updateByExample(Map<String, Object> parameter) {
        SQL sql = new SQL();
        sql.UPDATE("T_JOURNAL_ACCOUNT");
        
        sql.SET("ID = #{record.id,jdbcType=INTEGER}");
        sql.SET("CUSTOMER_ID = #{record.customerId,jdbcType=INTEGER}");
        sql.SET("`TYPE` = #{record.type,jdbcType=tinyint}");
        sql.SET("SOURCE_NO = #{record.sourceNo,jdbcType=INTEGER}");
        sql.SET("BEFORE_BALANCE = #{record.beforeBalance,jdbcType=DECIMAL}");
        sql.SET("CURRENT_BALANCE = #{record.currentBalance,jdbcType=DECIMAL}");
        sql.SET("CREATE_TIME = #{record.createTime,jdbcType=TIMESTAMP}");
        sql.SET("IS_VALIDATE = #{record.isValidate,jdbcType=tinyint}");
        sql.SET("UPDATE_TIME = #{record.updateTime,jdbcType=TIMESTAMP}");
        
        JournalAccountExample example = (JournalAccountExample) parameter.get("example");
        applyWhere(sql, example, true);
        return sql.toString();
    }

    protected void applyWhere(SQL sql, JournalAccountExample example, boolean includeExamplePhrase) {
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