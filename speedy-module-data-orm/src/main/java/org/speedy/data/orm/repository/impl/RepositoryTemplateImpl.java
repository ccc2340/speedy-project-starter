package org.speedy.data.orm.repository.impl;

import org.speedy.common.util.ModelUtils;
import org.speedy.data.orm.domain.sql.PageResult;
import org.speedy.data.orm.domain.sql.SqlCombo;
import org.speedy.data.orm.domain.sql.SqlQueryParameter;
import org.speedy.data.orm.repository.RepositoryTemplate;
import org.speedy.data.orm.util.SqlBuilder;
import org.speedy.data.orm.util.SqlExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/12 11:08
 */
public class RepositoryTemplateImpl implements RepositoryTemplate {

    @Autowired
    private SqlBuilder builder;
    @Autowired
    private SqlExecutor executor;

    @Override
    public <T> int insert(T t) {
        SqlCombo sqlCombo = builder.createInsertSql(t);
        return executor.executeNonQueryAndReturnPrimary(sqlCombo);
    }

    @Override
    public <T> int batchInsert(T[] ts) {
        SqlCombo sqlCombo = builder.createBatchInsertSql(ts);
        return executor.executeNonQuery(sqlCombo);
    }

    @Override
    public <T> int deleteByPrimary(Class<T> clazz, Serializable primary) {
        T object = ModelUtils.createObjectWithPrimary(clazz, primary);
        SqlCombo sqlCombo = builder.createDeleteSql(object);
        return executor.executeNonQuery(sqlCombo);
    }

    @Override
    public <T> int updateByPrimary(T t) {
        SqlCombo sqlCombo = builder.createUpdateSql(t);
        return executor.executeNonQuery(sqlCombo);
    }

    @Override
    public <T> Optional<T> selectByPrimary(Class<T> clazz, Serializable primary) {
        T object = ModelUtils.createObjectWithPrimary(clazz, primary);
        SqlQueryParameter queryParameter = SqlQueryParameter.ofExample(object);

        SqlCombo sqlCombo = builder.createSelectSql(queryParameter);
        Class<T> parameterClass = (Class<T>) queryParameter.getParameterClass();

        List<T> list = executor.executeObjectQuery(sqlCombo, parameterClass);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        else {
            return Optional.of(list.get(0));
        }
    }

    @Override
    public <T> List<T> selectByPrimaries(Class<T> clazz, List<Serializable> primaries) {
        SqlQueryParameter queryParameter = SqlQueryParameter.ofMultiPrimary(clazz, primaries);
        SqlCombo sqlCombo = builder.createSelectSql(queryParameter);
        return executor.executeObjectQuery(sqlCombo, clazz);
    }

    @Override
    public <T> List<T> selectForList(T object) {
        SqlQueryParameter queryParameter = SqlQueryParameter.ofExample(object);
        Class<T> parameterClass = (Class<T>) queryParameter.getParameterClass();
        SqlCombo sqlCombo = builder.createSelectSql(queryParameter);
        return executor.executeObjectQuery(sqlCombo, parameterClass);
    }

    @Override
    public <T> List<T> selectForClass(Class<T> clazz) {
        SqlQueryParameter queryParameter = SqlQueryParameter.ofClass(clazz);
        SqlCombo sqlCombo = builder.createSelectSql(queryParameter);
        return executor.executeObjectQuery(sqlCombo, clazz);
    }

    @Override
    public PageResult selectForPage(SqlQueryParameter queryParameter) {
        // 查询数据
        SqlCombo dataSqlCombo = builder.createSelectSql(queryParameter);
        List<?> data = executor.executeObjectQuery(dataSqlCombo, queryParameter.getParameterClass());

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
}
