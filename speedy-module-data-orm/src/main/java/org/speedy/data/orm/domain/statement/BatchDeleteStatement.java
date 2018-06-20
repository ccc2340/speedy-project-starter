package org.speedy.data.orm.domain.statement;

/**
 * @Description 批量删除的sql命令
 * @Author chenguangxue
 * @CreateDate 2018/06/17 01:19
 */
public class BatchDeleteStatement extends BatchStatement {

    public BatchDeleteStatement(Object[] deleteObjects) {
        super(deleteObjects);
    }

    public BatchDeleteStatement delete_from() {
        setDatabaseTarget(batchObjects[0]);
        return this;
    }

    public BatchDeleteStatement where() {
        extractBatchWhere();
        return this;
    }

    @Override
    SqlStatement complete() {
        this.sql = stringBuilder.append("delete from ").append(target)
                .append(" where ").append(primaryFieldName)
                .append(" in ").append(batchWhereString).toString();

        this.args.addAll(batchWhereFieldValues);

        return this;
    }
}
