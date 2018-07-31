package org.speedy.data.orm.domain.sql;

import lombok.Getter;
import org.speedy.common.util.ModelUtils;
import org.speedy.common.util.ReflectUtils;
import org.speedy.data.orm.annotation.ConditionField;
import org.speedy.data.orm.annotation.ConditionObject;
import org.speedy.data.orm.constant.SqlConditionType;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description sql语句查询条件模型
 * @Author chenguangxue
 * @CreateDate 2018/7/15 14:48
 */
@Getter
public class SqlCondition {

    private final Class<?> targetClass;
    private List<SqlConditionUnit> units;

    private SqlCondition(Class<?> targetClass) {
        this.targetClass = targetClass;
        this.units = new LinkedList<>();
    }

    // 重写方法：生成sql所需的where语句
    @Override
    public String toString() {
        if (units.size() == 1) {
            return units.get(0).toString();
        }
        else {
            return units.stream().map(SqlConditionUnit::toString).
                    collect(Collectors.joining(" and "));

        }
    }

    public List<Object> getArgs() {
        List<Object> args = new LinkedList<>();
        units.forEach(sqlConditionUnit -> args.addAll(sqlConditionUnit.getValue()));
        return args;
    }

    // 针对单个主键的快捷方法
    public static <T extends Serializable> SqlCondition primary(Class<?> clazz, T primary) {
        // 找出主键的名称
        Field primaryField = ModelUtils.getPrimaryField(clazz);
        Assert.notNull(primaryField, clazz + "没有设置主键");

        return Builder.target(clazz).equal(primaryField.getName(), primary).complete();
    }

    public static <T extends Serializable> SqlCondition primary(Object object) {
        Serializable primaryValue = ModelUtils.getPrimaryValue(object);
        return primary(object.getClass(), primaryValue);
    }

    // 针对多个主键的快捷方法
    public static <T extends Serializable> SqlCondition multiPrimary(Class<?> clazz, List<T> primaries) {
        // 找出主键的名称
        Field primaryField = ModelUtils.getPrimaryField(clazz);
        Assert.notNull(primaryField, clazz + "没有设置主键");

        return Builder.target(clazz).in(primaryField.getName(), primaries).complete();
    }

    // 针对没有条件的方法
    public static <T extends Serializable> SqlCondition empty(Class<?> clazz) {
        return Builder.target(clazz).complete();
    }

    // 针对一个对象的快捷方法
    public static <T extends Serializable> SqlCondition example(Object example) {
        Assert.notNull(example, "获取条件模型的对象为null");
        // 找出所有不为null的字段
        Field[] allFields = ReflectUtils.getAllFields(example.getClass());
        Map<String, Object> fieldMap = new HashMap<>(allFields.length * 2);
        Arrays.stream(allFields).forEach(field -> {
            Object fieldValue = ReflectUtils.directGetFieldValue(example, field);
            fieldMap.put(field.getName(), fieldValue);
        });

        Builder builder = Builder.target(example.getClass());
        fieldMap.forEach((name, value) -> {
            if (value != null) {
                builder.equal(name, value);
            }
        });

        return builder.complete();
    }

    // 针对@ConditionObject修饰的对象
    public static <T extends Serializable> SqlCondition condition(Object object) {
        Assert.notNull(object, "获取条件模型的对象为null");
        Assert.isTrue(object.getClass().isAnnotationPresent(ConditionObject.class),
                "类型不支持ConditionObject注解：" + object.getClass());

        ConditionObject annotation = object.getClass().getAnnotation(ConditionObject.class);
        Builder builder = Builder.target(annotation.value());

        Field[] allFields = ReflectUtils.getAllFields(object.getClass());
        Arrays.asList(allFields).forEach(field -> {
            Object fieldValue = ReflectUtils.directGetFieldValue(object, field);
            if (!StringUtils.isEmpty(fieldValue)) {
                Assert.isTrue(field.isAnnotationPresent(ConditionField.class),
                        object.getClass() + " 内的 " + field.getName() + " 不支持ConditionField注解");
                ConditionField fieldAnnotation = field.getAnnotation(ConditionField.class);

                SqlConditionType type = fieldAnnotation.type();
                String fieldName = StringUtils.isEmpty(fieldAnnotation.field()) ? field.getName() : fieldAnnotation.field();

                builder.custom(type, fieldName, fieldValue);
            }
        });

        return builder.complete();
    }

    // 用于生成sql条件的构造器
    public static class Builder {
        private SqlCondition sqlCondition;

        private Builder(Class<?> clazz) {
            this.sqlCondition = new SqlCondition(clazz);
        }

        public static Builder target(Class<?> targetClass) {
            return new Builder(targetClass);
        }

        public SqlCondition complete() {
            // 在最终生成结果之前，如果units中没有任何对象，则添加默认unit
            if (this.sqlCondition.units.isEmpty()) {
                this.sqlCondition.units.add(SqlConditionUnit.defaultUnit());
            }

            return this.sqlCondition;
        }

        private static SqlConditionUnit buildUnit(String field, SqlConditionType type, Object value) {
            switch (type) {
                case IN:
                case NOT_IN:
                case BETWEEN:
                case NOT_BETWEEN:
                    return SqlConditionUnit.multiValue(field, type, (List<Serializable>) value);
                case IS_NULL:
                case IS_NOT_NULL:
                    return SqlConditionUnit.noneValue(field, type);
                default:
                    return SqlConditionUnit.singleValue(field, type, (Serializable) value);
            }
        }

        // 指定type
        public Builder custom(SqlConditionType type, String field, Object value) {
            this.sqlCondition.units.add(buildUnit(field, type, value));
            return this;
        }

        // 相等
        public Builder equal(String field, Object value) {
            this.sqlCondition.units.add(buildUnit(field, SqlConditionType.EQUAL, value));
            return this;
        }

        // 不相等
        public Builder notEqual(String field, Object value) {
            this.sqlCondition.units.add(buildUnit(field, SqlConditionType.NOT_EQUAL, value));
            return this;
        }

        // 大于
        public Builder greater(String field, Object value) {
            this.sqlCondition.units.add(buildUnit(field, SqlConditionType.GREATER, value));
            return this;
        }

        // 小于
        public Builder less(String field, Object value) {
            this.sqlCondition.units.add(buildUnit(field, SqlConditionType.LESS, value));
            return this;
        }

        // 大于等于
        public Builder greaterOrEqual(String field, Object value) {
            this.sqlCondition.units.add(buildUnit(field, SqlConditionType.GREATER_EQUAL, value));
            return this;
        }

        // 小于等于
        public Builder lessOrEqual(String field, Object value) {
            this.sqlCondition.units.add(buildUnit(field, SqlConditionType.LESS_EQUAL, value));
            return this;
        }

        // like
        public Builder like(String field, Object value) {
            this.sqlCondition.units.add(buildUnit(field, SqlConditionType.LIKE, value));
            return this;
        }

        // not like
        public Builder notLike(String field, Object value) {
            this.sqlCondition.units.add(buildUnit(field, SqlConditionType.NOT_LIKE, value));
            return this;
        }

        // in
        public Builder in(String field, List<?> values) {
            this.sqlCondition.units.add(buildUnit(field, SqlConditionType.IN, values));
            return this;
        }

        // not in
        public Builder notIn(String field, List<?> values) {
            this.sqlCondition.units.add(buildUnit(field, SqlConditionType.NOT_IN, values));
            return this;
        }

        // between and
        public Builder between(String field, List<?> values) {
            this.sqlCondition.units.add(buildUnit(field, SqlConditionType.BETWEEN, values));
            return this;
        }

        // not between and
        public Builder notBetween(String field, List<?> values) {
            this.sqlCondition.units.add(buildUnit(field, SqlConditionType.NOT_BETWEEN, values));
            return this;
        }

        // is null
        public Builder isNull(String field) {
            this.sqlCondition.units.add(buildUnit(field, SqlConditionType.IS_NULL, null));
            return this;
        }

        // is not null
        public Builder isNotNull(String field) {
            this.sqlCondition.units.add(buildUnit(field, SqlConditionType.IS_NOT_NULL, null));
            return this;
        }
    }
}
