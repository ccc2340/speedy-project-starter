package org.speedy.data.orm.domain.sql;

import lombok.Getter;
import org.speedy.data.orm.domain.statement.SelectStatement;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description SQL查询语句参数
 * @Author chenguangxue
 * @CreateDate 2018/06/10 22:28
 */
@Getter
public class SqlQueryParameter {

    /* 操作的目标类型 */
    private Class<?> targetClass;

    /* sql条件对象 */
    private SqlCondition condition;

    /* 分页数据 */
    private PageInfo pageInfo;

    /* 排序数据 */
    private List<Sort.Order> orders;

    /* 是否显示不重复数据 */
    private boolean distinct;

    /* 查询类型 */
    private SelectStatement.SelectType selectType;

    // 私有化构造方法，必须使用构造器创建对象
    private SqlQueryParameter(SqlCondition sqlCondition) {
        this.orders = new LinkedList<>();
        this.selectType = SelectStatement.SelectType.ALL;
        this.pageInfo = PageInfo.noPage();
        this.distinct = false;

        this.targetClass = sqlCondition.getTargetClass();
        this.condition = sqlCondition;
    }

    /* 提供一个用于执行export查询的对象，条件不变，去掉分页数据 */
    public SqlQueryParameter export() {
        SqlQueryParameter exportParameter = new SqlQueryParameter(this.condition);
        exportParameter.selectType = SelectStatement.SelectType.ALL;
        exportParameter.pageInfo = PageInfo.noPage();
        exportParameter.distinct = this.distinct;
        return exportParameter;
    }

    /* 提供一个用于执行count查询的对象，条件不变 */
    public SqlQueryParameter count() {
        SqlQueryParameter countQueryParameter = new SqlQueryParameter(this.condition);
        countQueryParameter.selectType = SelectStatement.SelectType.COUNT;
        countQueryParameter.pageInfo = PageInfo.noPage();
        countQueryParameter.distinct = this.distinct;
        return countQueryParameter;
    }

    /* 提供常见参数的静态方法1 */
    public static SqlQueryParameter ofExample(Object example) {
        SqlCondition condition = SqlCondition.example(example);
        return new SqlQueryParameter(condition);
    }

    /* 提供常见参数的静态方法2 */
    public static SqlQueryParameter ofConditionObject(Object conditionObject) {
        SqlCondition condition = SqlCondition.condition(conditionObject);
        return new SqlQueryParameter(condition);
    }

    /* 提供常见参数的静态方法3 */
    public static SqlQueryParameter ofMultiPrimary(Class<?> clazz, List<Serializable> primaries) {
        SqlCondition condition = SqlCondition.multiPrimary(clazz, primaries);
        return new SqlQueryParameter(condition);
    }

    /* 提供常见参数的静态方法4 */
    public static SqlQueryParameter ofClass(Class<?> clazz) {
        SqlCondition condition = SqlCondition.empty(clazz);
        return new SqlQueryParameter(condition);
    }

    /* 提供常见参数的静态方法5 */
    public static SqlQueryParameter ofSqlCondition(SqlCondition condition) {
        return new SqlQueryParameter(condition);
    }

    public static class Builder {
        private SqlQueryParameter parameter;

        private Builder(SqlCondition condition) {
            this.parameter = new SqlQueryParameter(condition);
        }

        public static Builder condition(SqlCondition condition) {
            return new Builder(condition);
        }

        public Builder page(PageInfo pageInfo) {
            this.parameter.pageInfo = pageInfo;
            return this;
        }

        public Builder order(List<Sort.Order> orders) {
            this.parameter.orders.addAll(orders);
            return this;
        }

        public SqlQueryParameter complete() {
            return this.parameter;
        }
    }
}
