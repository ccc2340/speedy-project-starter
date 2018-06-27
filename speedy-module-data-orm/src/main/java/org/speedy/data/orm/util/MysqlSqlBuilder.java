package org.speedy.data.orm.util;

import org.speedy.data.orm.domain.sql.SqlCombo;
import org.speedy.data.orm.domain.sql.SqlQueryParameter;
import org.speedy.data.orm.domain.statement.BatchInsertStatement;
import org.speedy.data.orm.domain.statement.DeleteStatement;
import org.speedy.data.orm.domain.statement.InsertStatement;
import org.speedy.data.orm.domain.statement.SelectStatement;
import org.speedy.data.orm.domain.statement.UpdateStatement;
import org.springframework.stereotype.Component;

/**
 * @Description 适用于mysql数据库的命令创建器
 * @Author chenguangxue
 * @CreateDate 2018/06/10 22:41
 */
@Component
public class MysqlSqlBuilder implements SqlBuilder {

    @Override
    public SqlCombo createInsertSql(Object object) {
        InsertStatement statement = new InsertStatement();
        return statement.insert_into(object).values(object).combo();
    }

    @Override
    public SqlCombo createBatchInsertSql(Object[] objects) {
        BatchInsertStatement statement = new BatchInsertStatement(objects);
        return statement.insert_into().values().combo();
    }

    @Override
    public SqlCombo createDeleteSql(Object object) {
        DeleteStatement statement = new DeleteStatement();
        return statement.delete_from(object).where(object).combo();
    }

    @Override
    public SqlCombo createUpdateSql(Object object) {
        UpdateStatement statement = new UpdateStatement();
        return statement.update(object).set(object).where(object).combo();
    }

    @Override
    public SqlCombo createSelectSql(SqlQueryParameter parameter) {
        SelectStatement statement = new SelectStatement(parameter);
        return statement.select().from().where().combo();
    }
}
