package org.speedy.data.orm.domain.statement;

import org.speedy.data.orm.domain.sql.SqlUpdateParameter;
import org.springframework.util.Assert;

/**
 * @Description 修改语句模板
 * @Author chenguangxue
 * @CreateDate 2018/06/11 15:25
 */
public class UpdateStatement extends SqlStatement {

    private Class<?> updateClass;
    private SqlUpdateParameter updateParameter;
    private String set;
    private String where;

    public UpdateStatement(SqlUpdateParameter updateParameter) {
        Assert.notNull(updateParameter, "SqlUpdateParameter为null");

        this.updateClass = updateParameter.getTargetClass();
        this.updateParameter = updateParameter;
    }

    /* 设置操作目标 */
    public UpdateStatement update() {
        setDatabaseTarget(updateClass);
        return this;
    }

    /* 设置变更数据:提取除主键之外的字段列表 */
    public UpdateStatement set() {
        set = updateParameter.getUpdate().toString();
        return this;
    }

    /* 设置条件数据:只能使用主键作为条件 */
    public UpdateStatement where() {
        where = updateParameter.getCondition().toString();
        return this;
    }

    @Override
    SqlStatement complete() {
        this.update().set().where();

        this.sql = String.format("update %s set %s where %s", target, set, where);

        this.args.addAll(updateParameter.getUpdate().getArgs());
        this.args.addAll(updateParameter.getCondition().getArgs());

        return this;
    }
}
