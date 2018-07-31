package org.speedy.data.orm.util;

import org.speedy.data.orm.domain.sql.*;
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
    public NamedSqlCombo createInsertSql(Object object) {
        InsertStatement statement = new InsertStatement(object);
        return statement.combo();
    }

    @Override
    public SqlCombo createDeleteSql(SqlCondition deleteCondition) {
        DeleteStatement statement = new DeleteStatement(deleteCondition);
        return statement.combo();
    }

    @Override
    public SqlCombo createUpdateSql(SqlUpdateParameter updateParameter) {
        UpdateStatement statement = new UpdateStatement(updateParameter);
        return statement.combo();
    }

    @Override
    public SqlCombo createSelectSql(SqlQueryParameter queryParameter) {
        SelectStatement statement = new SelectStatement(queryParameter);
        return statement.combo();
    }
}
