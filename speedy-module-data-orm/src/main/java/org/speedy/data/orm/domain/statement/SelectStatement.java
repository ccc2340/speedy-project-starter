package org.speedy.data.orm.domain.statement;

import org.speedy.common.util.ModelUtils;
import org.speedy.common.util.ReflectUtils;
import org.speedy.data.orm.annotation.ConditionField;
import org.speedy.data.orm.domain.base.QueryCondition;
import org.speedy.data.orm.domain.sql.SqlQueryParameter;
import org.speedy.data.orm.exception.SpeedyDataAccessException;
import org.springframework.util.StringUtils;

import javax.persistence.Table;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 查询语句模板
 * @Author chenguangxue
 * @CreateDate 2018/06/11 15:50
 */
public class SelectStatement extends SqlStatement {

    private SqlQueryParameter queryParameter;

    public SelectStatement(SqlQueryParameter queryParameter) {
        this.queryParameter = queryParameter;
    }

    public SelectStatement from() {
        this.target = queryParameter.getParameterClass().getAnnotation(Table.class).name();
        return this;
    }

    /* 查询字段类型：1、所有字段 */
    public enum SelectType {
        ALL("*"), COUNT("count(*)"),
        // end
        ;
        private String fields;

        SelectType(String fields) {
            this.fields = fields;
        }
    }

    /* 查询类型 */
    private String selectFields;

    public SelectStatement select() {
        this.selectFields = queryParameter.getSelectType().fields;
        return this;
    }

    /* 设置查询条件 */
    public SelectStatement where() {
        switch (queryParameter.getParameterType()) {
            case EXAMPLE:
                extractWhere(queryParameter.getParameterObject());
                return this;
            case CONDITION:
                extractWhereFromCondition(queryParameter.getParameterObject());
                return this;
            case MULTI_PRIMARY:
                extractWhereFromMultiPrimary(queryParameter.getParameterObject());
                return this;
            case CLASS:
                extractWhereFromClass();
                return this;
            default:
                throw new SpeedyDataAccessException(queryParameter.getParameterType().name());
        }
    }

    private void extractWhereFromClass() {
    }

    private List<String> wherePlaceholderList = new ArrayList<>();

    private void extractWhereFromMultiPrimary(Object parameterObject) {
        Field primaryField = ModelUtils.getPrimaryField(queryParameter.getParameterClass());
        this.whereFieldNameList.add(getDatabaseFieldName(primaryField));

        Set<Serializable> primarySet = new HashSet<>((Collection) parameterObject);
        for (Serializable s : primarySet) {
            whereFieldValueList.add(s);
            wherePlaceholderList.add(getPlaceholder());
        }
    }

    private void extractWhereFromCondition(Object conditionObject) {
        Field[] fields = conditionObject.getClass().getDeclaredFields();
        for (Field f : fields) {
            Object fieldValue = ReflectUtils.directGetFieldValue(conditionObject, f);

            // 如果值为null或者为空白字符串，则跳过，不需要设置这个条件
            if (StringUtils.isEmpty(fieldValue)) {
                continue;
            }

            ConditionField annotation = f.getAnnotation(ConditionField.class);
            String fieldName = StringUtils.isEmpty(annotation.field()) ? f.getName() : annotation.field();
            jointConditionField(fieldName, annotation.type(), fieldValue);
        }
    }

    // 拼接条件字段
    private void jointConditionField(String field, QueryCondition.Type type, Object value) {
        switch (type) {
            // 对于这2种情况，不需要拼接数值
            case NULL:
            case NOT_NULL:
                whereFieldNameList.add(String.format("%s %s", field, type.getSymbol()));
                break;
            // 对于like的情况，需要拼接占位符
            case LIKE:
                value = getLikePlaceholder() + value + getLikePlaceholder();
            default:
                whereFieldNameList.add(String.format("%s %s %s", field, type.getSymbol(), getPlaceholder()));
                whereFieldValueList.add(value);
        }
    }

    private String getLikePlaceholder() {
        return "%";
    }

    @Override
    SqlStatement complete() {
        switch (queryParameter.getParameterType()) {
            case MULTI_PRIMARY: {
                String placeholderString = wherePlaceholderList.stream().collect(Collectors.joining(",", "(", ")")).toString();
                whereFieldNameString = whereFieldNameList.get(0) + " in " + placeholderString;
                break;
            }
            case CLASS: {
                whereFieldNameString = " 1=1 ";
                break;
            }
            default: {
                // 如果拼接的条件个数为0，说明未设置条件，则设置默认条件
                if (whereFieldNameList.isEmpty()) {
                    whereFieldNameString = " 1=1 ";
                }
                else {
                    whereFieldNameString = whereFieldNameList.stream().collect(Collectors.joining(" and "));
                }
            }
        }

        stringBuilder.append("select ").append(selectFields)
                .append(" from ").append(target)
                .append(" where ").append(whereFieldNameString);

        /* 如果查询参数中需要分页，则加上分页的数据 */
        if (queryParameter.getPageInfo().isPage()) {
            int offset = queryParameter.getPageInfo().getOffset();
            int cpp = queryParameter.getPageInfo().getCpp();
            String pageSql = String.format(" limit %d, %d", offset, cpp);
            stringBuilder.append(pageSql);
        }

        this.sql = stringBuilder.toString();
        this.args.addAll(whereFieldValueList);

        return this;
    }
}
