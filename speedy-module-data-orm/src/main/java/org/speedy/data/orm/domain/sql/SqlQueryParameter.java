package org.speedy.data.orm.domain.sql;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.Order;

import org.speedy.data.orm.annotation.MappingClass;
import org.speedy.data.orm.domain.base.QueryCondition;
import org.speedy.data.orm.domain.statement.SelectStatement;

import lombok.Getter;

/**
 * @Description SQL查询语句参数
 * @Author chenguangxue
 * @CreateDate 2018/06/10 22:28
 */
@Getter
public class SqlQueryParameter {

    /* 包含条件数据的对象 */
    private Object parameterObject;
    private ParameterType parameterType;
    private Class<?> parameterClass;

    /* 分页数据 */
    private PageInfo pageInfo;

    /* 排序数据 */
    private List<Order> orders;

    /* 是否显示不重复数据 */
    private boolean distinct;

    /* 查询类型 */
    private SelectStatement.SelectType selectType;

    // 私有化构造方法，必须使用构造器创建对象
    private SqlQueryParameter() {
        this.orders = new LinkedList<>();
        this.selectType = SelectStatement.SelectType.ALL;
        this.pageInfo = PageInfo.noPage();
        this.parameterType = ParameterType.EXAMPLE;
    }

    /* 提供一个用于执行count查询的对象 */
    public SqlQueryParameter count() {
        SqlQueryParameter countQueryParameter = new SqlQueryParameter();
        countQueryParameter.parameterObject = this.parameterObject;
        countQueryParameter.parameterType = this.parameterType;
        countQueryParameter.parameterClass = this.parameterClass;
        countQueryParameter.selectType = SelectStatement.SelectType.COUNT;
        return countQueryParameter;
    }

    /* 提供常见参数的静态方法1 */
    public static SqlQueryParameter ofExample(Object example) {
        return Builder.start().withExample(example).complete();
    }

    /* 提供常见参数的静态方法2 */
    public static SqlQueryParameter ofCondition(QueryCondition condition) {
        return Builder.start().withCondition(condition).complete();
    }

    /* 提供常见参数的静态方法3 */
    public static SqlQueryParameter ofMultiPrimary(Class<?> clazz, List<Serializable> primaries) {
        return Builder.start().withMultiPrimary(clazz, primaries).complete();
    }

    /* 提供常见参数的静态方法4 */
    public static SqlQueryParameter ofClass(Class<?> clazz) {
        return Builder.start().widthClass(clazz).complete();
    }

    public enum ParameterType {
        EXAMPLE, CONDITION, MULTI_PRIMARY, CLASS,
    }

    public static class Builder {
        // 构造器也不允许直接创建对象
        private Builder() {
        }

        private SqlQueryParameter sqlQueryParameter;

        public Builder withExample(Object example) {
            this.sqlQueryParameter.parameterObject = example;
            this.sqlQueryParameter.parameterType = ParameterType.EXAMPLE;
            this.sqlQueryParameter.parameterClass = example.getClass();
            return this;
        }

        public Builder widthClass(Class<?> clazz) {
            this.sqlQueryParameter.parameterClass = clazz;
            this.sqlQueryParameter.parameterType = ParameterType.CLASS;
            return this;
        }

        public Builder withMultiPrimary(Class<?> clazz, List<Serializable> primaryList) {
            this.sqlQueryParameter.parameterObject = primaryList;
            this.sqlQueryParameter.parameterType = ParameterType.MULTI_PRIMARY;
            this.sqlQueryParameter.parameterClass = clazz;
            return this;
        }

        public Builder withSelectType(SelectStatement.SelectType selectType) {
            this.sqlQueryParameter.selectType = selectType;
            return this;
        }

        public Builder withCondition(QueryCondition condition) {
            this.sqlQueryParameter.parameterObject = condition;
            this.sqlQueryParameter.parameterType = ParameterType.CONDITION;
            MappingClass annotation = condition.getClass().getAnnotation(MappingClass.class);
            this.sqlQueryParameter.parameterClass = annotation.clazz();
            return this;
        }

        public Builder initPageInfo(PageInfo pageInfo) {
            this.sqlQueryParameter.pageInfo = pageInfo;
            return this;
        }

        public Builder withOrders(Order... orders) {
            if (orders != null && orders.length > 0) {
                this.sqlQueryParameter.orders.addAll(Arrays.asList(orders));
            }
            return this;
        }

        public Builder needDistinct() {
            this.sqlQueryParameter.distinct = true;
            return this;
        }

        public SqlQueryParameter complete() {
            return this.sqlQueryParameter;
        }

        public static Builder start() {
            Builder builder = new Builder();
            builder.sqlQueryParameter = new SqlQueryParameter();
            return builder;
        }
    }
}
