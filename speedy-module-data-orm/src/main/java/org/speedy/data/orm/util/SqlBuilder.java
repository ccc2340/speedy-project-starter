package org.speedy.data.orm.util;

import org.speedy.data.orm.domain.sql.*;

/**
 * @Description SQL命令创建器
 * @Author chenguangxue
 * @CreateDate 2018/06/10 22:22
 */
public interface SqlBuilder {

    /* 创建插入语句 */
    NamedSqlCombo createInsertSql(Object object);

    /* 创建删除语句 */
    SqlCombo createDeleteSql(SqlCondition deleteCondition);

    /* 创建修改语句 */
    SqlCombo createUpdateSql(SqlUpdateParameter parameter);

    /* 创建查询语句 */
    SqlCombo createSelectSql(SqlQueryParameter parameter);
}
