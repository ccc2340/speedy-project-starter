package org.speedy.data.orm.domain.statement;

import org.speedy.data.orm.domain.sql.SqlCondition;
import org.springframework.util.Assert;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/11 15:03
 */
public class DeleteStatement extends SqlStatement {

    private SqlCondition deleteCondition;
    private Class<?> deleteClass;
    private String where;

    public DeleteStatement(SqlCondition condition) {
        Assert.notNull(condition, "删除条件为null");

        this.deleteCondition = condition;
        this.deleteClass = condition.getTargetClass();
    }

    /* 设置数据库操作目标 */
    public DeleteStatement delete_from() {
        setDatabaseTarget(deleteClass);
        return this;
    }

    /* 设置删除条件 */
    public DeleteStatement where() {
        where = deleteCondition.toString();
        return this;
    }

    @Override
    SqlStatement complete() {
        this.delete_from().where();

        this.sql = String.format("delete from %s where %s", target, where);
        this.args.addAll(deleteCondition.getArgs());

        return this;
    }
}
