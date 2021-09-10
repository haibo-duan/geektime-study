package com.dhb.gts.javacourse.mapper;

import com.dhb.gts.javacourse.entity.ProductInfo;
import com.dhb.gts.javacourse.entity.ProductInfoExample.Criteria;
import com.dhb.gts.javacourse.entity.ProductInfoExample.Criterion;
import com.dhb.gts.javacourse.entity.ProductInfoExample;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.jdbc.SQL;

public class ProductInfoSqlProvider {

    public String countByExample(ProductInfoExample example) {
        SQL sql = new SQL();
        sql.SELECT("count(*)").FROM("T_PRODUCT_INFO");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    public String deleteByExample(ProductInfoExample example) {
        SQL sql = new SQL();
        sql.DELETE_FROM("T_PRODUCT_INFO");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    public String insertSelective(ProductInfo record) {
        SQL sql = new SQL();
        sql.INSERT_INTO("T_PRODUCT_INFO");
        
        if (record.getProductId() != null) {
            sql.VALUES("PRODUCT_ID", "#{productId,jdbcType=INTEGER}");
        }
        
        if (record.getProductCode() != null) {
            sql.VALUES("PRODUCT_CODE", "#{productCode,jdbcType=CHAR}");
        }
        
        if (record.getProductName() != null) {
            sql.VALUES("PRODUCT_NAME", "#{productName,jdbcType=VARCHAR}");
        }
        
        if (record.getSupplierId() != null) {
            sql.VALUES("SUPPLIER_ID", "#{supplierId,jdbcType=INTEGER}");
        }
        
        if (record.getPrice() != null) {
            sql.VALUES("PRICE", "#{price,jdbcType=DECIMAL}");
        }
        
        if (record.getAcerageCost() != null) {
            sql.VALUES("ACERAGE_COST", "#{acerageCost,jdbcType=DECIMAL}");
        }
        
        if (record.getStatus() != null) {
            sql.VALUES("`STATUS`", "#{status,jdbcType=TINYINT}");
        }
        
        if (record.getProductionDate() != null) {
            sql.VALUES("PRODUCTION_DATE", "#{productionDate,jdbcType=TIMESTAMP}");
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

    public String selectByExample(ProductInfoExample example) {
        SQL sql = new SQL();
        if (example != null && example.isDistinct()) {
            sql.SELECT_DISTINCT("PRODUCT_ID");
        } else {
            sql.SELECT("PRODUCT_ID");
        }
        sql.SELECT("PRODUCT_CODE");
        sql.SELECT("PRODUCT_NAME");
        sql.SELECT("SUPPLIER_ID");
        sql.SELECT("PRICE");
        sql.SELECT("ACERAGE_COST");
        sql.SELECT("`STATUS`");
        sql.SELECT("PRODUCTION_DATE");
        sql.SELECT("CREATE_TIME");
        sql.SELECT("IS_VALIDATE");
        sql.SELECT("UPDATE_TIME");
        sql.FROM("T_PRODUCT_INFO");
        applyWhere(sql, example, false);
        
        if (example != null && example.getOrderByClause() != null) {
            sql.ORDER_BY(example.getOrderByClause());
        }
        
        return sql.toString();
    }

    public String updateByExampleSelective(Map<String, Object> parameter) {
        ProductInfo record = (ProductInfo) parameter.get("record");
        ProductInfoExample example = (ProductInfoExample) parameter.get("example");
        
        SQL sql = new SQL();
        sql.UPDATE("T_PRODUCT_INFO");
        
        if (record.getProductId() != null) {
            sql.SET("PRODUCT_ID = #{record.productId,jdbcType=INTEGER}");
        }
        
        if (record.getProductCode() != null) {
            sql.SET("PRODUCT_CODE = #{record.productCode,jdbcType=CHAR}");
        }
        
        if (record.getProductName() != null) {
            sql.SET("PRODUCT_NAME = #{record.productName,jdbcType=VARCHAR}");
        }
        
        if (record.getSupplierId() != null) {
            sql.SET("SUPPLIER_ID = #{record.supplierId,jdbcType=INTEGER}");
        }
        
        if (record.getPrice() != null) {
            sql.SET("PRICE = #{record.price,jdbcType=DECIMAL}");
        }
        
        if (record.getAcerageCost() != null) {
            sql.SET("ACERAGE_COST = #{record.acerageCost,jdbcType=DECIMAL}");
        }
        
        if (record.getStatus() != null) {
            sql.SET("`STATUS` = #{record.status,jdbcType=TINYINT}");
        }
        
        if (record.getProductionDate() != null) {
            sql.SET("PRODUCTION_DATE = #{record.productionDate,jdbcType=TIMESTAMP}");
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
        sql.UPDATE("T_PRODUCT_INFO");
        
        sql.SET("PRODUCT_ID = #{record.productId,jdbcType=INTEGER}");
        sql.SET("PRODUCT_CODE = #{record.productCode,jdbcType=CHAR}");
        sql.SET("PRODUCT_NAME = #{record.productName,jdbcType=VARCHAR}");
        sql.SET("SUPPLIER_ID = #{record.supplierId,jdbcType=INTEGER}");
        sql.SET("PRICE = #{record.price,jdbcType=DECIMAL}");
        sql.SET("ACERAGE_COST = #{record.acerageCost,jdbcType=DECIMAL}");
        sql.SET("`STATUS` = #{record.status,jdbcType=TINYINT}");
        sql.SET("PRODUCTION_DATE = #{record.productionDate,jdbcType=TIMESTAMP}");
        sql.SET("CREATE_TIME = #{record.createTime,jdbcType=TIMESTAMP}");
        sql.SET("IS_VALIDATE = #{record.isValidate,jdbcType=TINYINT}");
        sql.SET("UPDATE_TIME = #{record.updateTime,jdbcType=TIMESTAMP}");
        
        ProductInfoExample example = (ProductInfoExample) parameter.get("example");
        applyWhere(sql, example, true);
        return sql.toString();
    }

    protected void applyWhere(SQL sql, ProductInfoExample example, boolean includeExamplePhrase) {
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