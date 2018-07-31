package org.speedy.data.orm.repository;

import org.speedy.common.util.ModelUtils;
import org.speedy.data.orm.domain.sql.*;
import org.speedy.data.orm.util.SqlBuilder;
import org.speedy.data.orm.util.SqlExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 数据库持久化模板
 * @Author chenguangxue
 * @CreateDate 2018/06/12 10:53
 */
@Component
public class RepositoryTemplate {

    @Autowired
    private SqlBuilder builder;
    @Autowired
    private SqlExecutor executor;

    public int insert(Object insertObject) {
        NamedSqlCombo sqlCombo = builder.createInsertSql(insertObject);
        return executor.executeNonQueryAndReturnPrimary(sqlCombo);
    }

    public int delete(SqlCondition condition) {
        SqlCombo sqlCombo = builder.createDeleteSql(condition);
        return executor.executeNonQuery(sqlCombo);
    }

    public int delete(Class<?> clazz, Serializable primary) {
        SqlCondition condition = SqlCondition.primary(clazz, primary);
        return delete(condition);
    }

    public int delete(Object object) {
        Serializable primaryValue = ModelUtils.getPrimaryValue(object);
        return delete(object.getClass(), primaryValue);
    }

    public int update(Object newObject) {
        Serializable primaryValue = ModelUtils.getPrimaryValue(newObject);
        Object oldObject = selectByPrimary(newObject.getClass(), primaryValue);
        SqlUpdateParameter parameter = SqlUpdateParameter.primary(newObject, oldObject);
        return update(parameter);
    }

    public int update(SqlUpdateParameter parameter) {
        SqlCombo sqlCombo = builder.createUpdateSql(parameter);
        return executor.executeNonQuery(sqlCombo);
    }

    public <T> T selectByPrimary(Class<T> clazz, Serializable primary) {
        SqlCondition condition = SqlCondition.primary(clazz, primary);
        List<T> list = select(condition, clazz);
        if (list.isEmpty()) {
            return null;
        }
        else {
            return list.get(0);
        }
    }

    public <T> List<T> selectByPrimaries(Class<T> clazz, List<Serializable> primaries) {
        SqlCondition condition = SqlCondition.multiPrimary(clazz, primaries);
        return select(condition, clazz);
    }

    public <T> T selectByExample(Object example) {
        List<Object> list = selectListByExample(example);
        return list.isEmpty() ? null : (T) list.get(0);
    }

    public <T> List<T> selectListByExample(T example) {
        SqlCondition condition = SqlCondition.example(example);
        return (List<T>) select(condition, example.getClass());
    }

    public <T> List<T> selectAll(Class<T> clazz) {
        SqlCondition condition = SqlCondition.empty(clazz);
        return select(condition, clazz);
    }

    public <T> T selectBySqlCondition(SqlCondition condition, Class<T> clazz) {
        List<T> list = selectListBySqlCondition(condition, clazz);
        if (list.isEmpty()) {
            return null;
        }
        else {
            return list.get(0);
        }
    }

    public <T> List<T> selectListBySqlCondition(SqlCondition condition, Class<T> clazz) {
        return select(condition, clazz);
    }

    public PageResult selectPage(SqlQueryParameter queryParameter) {
        // 查询数据
        SqlCombo dataSqlCombo = builder.createSelectSql(queryParameter);
        List<?> data = executor.executeObjectQuery(dataSqlCombo, queryParameter.getTargetClass());

        // 查询总数
        SqlQueryParameter countQueryParameter = queryParameter.count();
        SqlCombo countSqlCombo = builder.createSelectSql(countQueryParameter);
        Long count = (Long) executor.executeValueQuery(countSqlCombo);

        // 封装分页结果
        return PageResult.Builder.start()
                .fillPageInfo(queryParameter.getPageInfo())
                .fillCount(count)
                .fillData(data).complete();
    }

    private <T> List<T> select(SqlCondition condition, Class<T> clazz) {
        SqlQueryParameter parameter = SqlQueryParameter.ofSqlCondition(condition);
        SqlCombo sqlCombo = builder.createSelectSql(parameter);
        return executor.executeObjectQuery(sqlCombo, clazz);
    }
}
