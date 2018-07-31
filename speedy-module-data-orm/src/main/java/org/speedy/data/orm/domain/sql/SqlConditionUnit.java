package org.speedy.data.orm.domain.sql;

import lombok.Getter;
import org.speedy.data.orm.constant.SqlConditionType;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description SQL条件单元(包含 ： 字段 、 类型 、 数值)
 * @Author chenguangxue
 * @CreateDate 2018/7/15 00:52
 */
@Getter
public class SqlConditionUnit {

    private final String field;
    private final SqlConditionType type;
    private final List<Serializable> value;

    private SqlConditionUnit(String field, SqlConditionType type) {
        this.field = field;
        this.type = type;
        this.value = new LinkedList<>();
    }

    // 条件值为单个的情况
    static SqlConditionUnit singleValue(String field, SqlConditionType type, Serializable value) {
        SqlConditionUnit unit = new SqlConditionUnit(field, type);

        if (value != null && (value.getClass() == boolean.class || value.getClass() == Boolean.class)) {
            Integer integer = Boolean.valueOf(value.toString()) ? Integer.valueOf(1) : Integer.valueOf(0);
            unit.value.add(integer);
        }
        else {
            unit.value.add(value);
        }

        return unit;
    }

    // 条件值为无的情况
    static SqlConditionUnit noneValue(String field, SqlConditionType type) {
        return new SqlConditionUnit(field, type);
    }

    // 条件值为多个的情况
    static SqlConditionUnit multiValue(String field, SqlConditionType type, List<Serializable> values) {
        SqlConditionUnit unit = new SqlConditionUnit(field, type);
        unit.value.addAll(values);
        return unit;
    }

    public Collection<Serializable> getValue() {
//        switch (type) {
//            case IN:
//            case NOT_IN:
//            case BETWEEN:
//            case NOT_BETWEEN:
//                return (List<?>) value;
//            case IS_NULL:
//            case IS_NOT_NULL:
//                return Collections.emptyList();
//            default:
//                return Collections.singleton(value);
//        }
        return this.value;
    }

    /* 特殊条件：1=1 */
    static SqlConditionUnit defaultUnit() {
        SqlConditionUnit unit = new SqlConditionUnit("1", SqlConditionType.EQUAL);
        unit.value.add(1);
        return unit;
    }

    @Override
    public String toString() {
        return type.build(field, value);
    }
}
