package com.dhb.gts.javacourse.mapper;

import com.dhb.gts.javacourse.entity.OrderSummary;
import com.dhb.gts.javacourse.entity.OrderSummaryExample.Criteria;
import com.dhb.gts.javacourse.entity.OrderSummaryExample.Criterion;
import com.dhb.gts.javacourse.entity.OrderSummaryExample;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.jdbc.SQL;

public class OrderSummarySqlProvider {

    public String countByExample(OrderSummaryExample example) {
        SQL sql = new SQL();
        sql.SELECT("count(*)").FROM("T_ORDER_SUMMARY");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    public String deleteByExample(OrderSummaryExample example) {
        SQL sql = new SQL();
        sql.DELETE_FROM("T_ORDER_SUMMARY");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    public String insertSelective(OrderSummary record) {
        SQL sql = new SQL();
        sql.INSERT_INTO("T_ORDER_SUMMARY");
        
        if (record.getOrderId() != null) {
            sql.VALUES("ORDER_ID", "#{orderId,jdbcType=INTEGER}");
        }
        
        if (record.getOrderNo() != null) {
            sql.VALUES("ORDER_NO", "#{orderNo,jdbcType=INTEGER}");
        }
        
        if (record.getCustomerId() != null) {
            sql.VALUES("CUSTOMER_ID", "#{customerId,jdbcType=INTEGER}");
        }
        
        if (record.getPaymentMethod() != null) {
            sql.VALUES("PAYMENT_METHOD", "#{paymentMethod,jdbcType=TINYINT}");
        }
        
        if (record.getOrderAmount() != null) {
            sql.VALUES("ORDER_AMOUNT", "#{orderAmount,jdbcType=DECIMAL}");
        }
        
        if (record.getPaymentMoney() != null) {
            sql.VALUES("PAYMENT_MONEY", "#{paymentMoney,jdbcType=DECIMAL}");
        }
        
        if (record.getConsigneeName() != null) {
            sql.VALUES("CONSIGNEE_NAME", "#{consigneeName,jdbcType=VARCHAR}");
        }
        
        if (record.getConsigneeAddress() != null) {
            sql.VALUES("CONSIGNEE_ADDRESS", "#{consigneeAddress,jdbcType=VARCHAR}");
        }
        
        if (record.getConsigneePhone() != null) {
            sql.VALUES("CONSIGNEE_PHONE", "#{consigneePhone,jdbcType=VARCHAR}");
        }
        
        if (record.getExpressComp() != null) {
            sql.VALUES("EXPRESS_COMP", "#{expressComp,jdbcType=VARCHAR}");
        }
        
        if (record.getExpressNo() != null) {
            sql.VALUES("EXPRESS_NO", "#{expressNo,jdbcType=VARCHAR}");
        }
        
        if (record.getCreateTime() != null) {
            sql.VALUES("CREATE_TIME", "#{createTime,jdbcType=TIMESTAMP}");
        }
        
        if (record.getIsValidate() != null) {
            sql.VALUES("IS_VALIDATE", "#{isValidate,jdbcType=TINYINT}");
        }
        
        if (record.getUpdateTime() != null) {
            sql.VALUES("UPDATE_TIME", "#{updateTime,jdbcType=TIMESTAMP}");
        }
        
        return sql.toString();
    }

    public String selectByExample(OrderSummaryExample example) {
        SQL sql = new SQL();
        if (example != null && example.isDistinct()) {
            sql.SELECT_DISTINCT("ORDER_ID");
        } else {
            sql.SELECT("ORDER_ID");
        }
        sql.SELECT("ORDER_NO");
        sql.SELECT("CUSTOMER_ID");
        sql.SELECT("PAYMENT_METHOD");
        sql.SELECT("ORDER_AMOUNT");
        sql.SELECT("PAYMENT_MONEY");
        sql.SELECT("CONSIGNEE_NAME");
        sql.SELECT("CONSIGNEE_ADDRESS");
        sql.SELECT("CONSIGNEE_PHONE");
        sql.SELECT("EXPRESS_COMP");
        sql.SELECT("EXPRESS_NO");
        sql.SELECT("CREATE_TIME");
        sql.SELECT("IS_VALIDATE");
        sql.SELECT("UPDATE_TIME");
        sql.FROM("T_ORDER_SUMMARY");
        applyWhere(sql, example, false);
        
        if (example != null && example.getOrderByClause() != null) {
            sql.ORDER_BY(example.getOrderByClause());
        }
        
        return sql.toString();
    }

    public String updateByExampleSelective(Map<String, Object> parameter) {
        OrderSummary record = (OrderSummary) parameter.get("record");
        OrderSummaryExample example = (OrderSummaryExample) parameter.get("example");
        
        SQL sql = new SQL();
        sql.UPDATE("T_ORDER_SUMMARY");
        
        if (record.getOrderId() != null) {
            sql.SET("ORDER_ID = #{record.orderId,jdbcType=INTEGER}");
        }
        
        if (record.getOrderNo() != null) {
            sql.SET("ORDER_NO = #{record.orderNo,jdbcType=INTEGER}");
        }
        
        if (record.getCustomerId() != null) {
            sql.SET("CUSTOMER_ID = #{record.customerId,jdbcType=INTEGER}");
        }
        
        if (record.getPaymentMethod() != null) {
            sql.SET("PAYMENT_METHOD = #{record.paymentMethod,jdbcType=TINYINT}");
        }
        
        if (record.getOrderAmount() != null) {
            sql.SET("ORDER_AMOUNT = #{record.orderAmount,jdbcType=DECIMAL}");
        }
        
        if (record.getPaymentMoney() != null) {
            sql.SET("PAYMENT_MONEY = #{record.paymentMoney,jdbcType=DECIMAL}");
        }
        
        if (record.getConsigneeName() != null) {
            sql.SET("CONSIGNEE_NAME = #{record.consigneeName,jdbcType=VARCHAR}");
        }
        
        if (record.getConsigneeAddress() != null) {
            sql.SET("CONSIGNEE_ADDRESS = #{record.consigneeAddress,jdbcType=VARCHAR}");
        }
        
        if (record.getConsigneePhone() != null) {
            sql.SET("CONSIGNEE_PHONE = #{record.consigneePhone,jdbcType=VARCHAR}");
        }
        
        if (record.getExpressComp() != null) {
            sql.SET("EXPRESS_COMP = #{record.expressComp,jdbcType=VARCHAR}");
        }
        
        if (record.getExpressNo() != null) {
            sql.SET("EXPRESS_NO = #{record.expressNo,jdbcType=VARCHAR}");
        }
        
        if (record.getCreateTime() != null) {
            sql.SET("CREATE_TIME = #{record.createTime,jdbcType=TIMESTAMP}");
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
        sql.UPDATE("T_ORDER_SUMMARY");
        
        sql.SET("ORDER_ID = #{record.orderId,jdbcType=INTEGER}");
        sql.SET("ORDER_NO = #{record.orderNo,jdbcType=INTEGER}");
        sql.SET("CUSTOMER_ID = #{record.customerId,jdbcType=INTEGER}");
        sql.SET("PAYMENT_METHOD = #{record.paymentMethod,jdbcType=TINYINT}");
        sql.SET("ORDER_AMOUNT = #{record.orderAmount,jdbcType=DECIMAL}");
        sql.SET("PAYMENT_MONEY = #{record.paymentMoney,jdbcType=DECIMAL}");
        sql.SET("CONSIGNEE_NAME = #{record.consigneeName,jdbcType=VARCHAR}");
        sql.SET("CONSIGNEE_ADDRESS = #{record.consigneeAddress,jdbcType=VARCHAR}");
        sql.SET("CONSIGNEE_PHONE = #{record.consigneePhone,jdbcType=VARCHAR}");
        sql.SET("EXPRESS_COMP = #{record.expressComp,jdbcType=VARCHAR}");
        sql.SET("EXPRESS_NO = #{record.expressNo,jdbcType=VARCHAR}");
        sql.SET("CREATE_TIME = #{record.createTime,jdbcType=TIMESTAMP}");
        sql.SET("IS_VALIDATE = #{record.isValidate,jdbcType=TINYINT}");
        sql.SET("UPDATE_TIME = #{record.updateTime,jdbcType=TIMESTAMP}");
        
        OrderSummaryExample example = (OrderSummaryExample) parameter.get("example");
        applyWhere(sql, example, true);
        return sql.toString();
    }

    protected void applyWhere(SQL sql, OrderSummaryExample example, boolean includeExamplePhrase) {
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