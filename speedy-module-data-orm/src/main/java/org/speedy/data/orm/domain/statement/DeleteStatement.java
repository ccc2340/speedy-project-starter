package org.speedy.data.orm.domain.statement;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/11 15:03
 */
public class DeleteStatement extends SqlStatement {

    /* 设置数据库操作目标 */
    public DeleteStatement delete_from(Object object) {
        setDatabaseTarget(object);
        return this;
    }

    /* 设置删除条件 */
    public DeleteStatement where(Object object) {
        extractWhere(object);
        return this;
    }

    @Override
    SqlStatement complete() {
        // 组装sql语句
        this.sql = stringBuilder.append("delete from ").append(target).
                append(" where ").append(whereFieldNameString).toString();

        this.args.addAll(whereFieldValueList);

        return this;
    }
}
