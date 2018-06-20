package org.speedy.data.orm.domain.statement;

/**
 * @Description 查询语句模板
 * @Author chenguangxue
 * @CreateDate 2018/06/11 15:50
 */
public class SelectStatement extends SqlStatement {

    public SelectStatement from(Object object) {
        setDatabaseTarget(object);
        return this;
    }

    /* 查询字段类型：1、所有字段 */
    public enum SelectType {
        ALL("*"), COUNT("count(*)"),
        // end
        ;
        private String fields;

        SelectType(String fields) {
            this.fields = fields;
        }
    }

    /* 查询类型 */
    private String selectFields;

    public SelectStatement select(SelectType type) {
        this.selectFields = type.fields;
        return this;
    }

    /* 设置查询条件 */
    public SelectStatement where(Object object) {
        extractWhere(object);
        return this;
    }

    @Override
    SqlStatement complete() {
        this.sql = stringBuilder.append("select ").append(selectFields)
                .append(" from ").append(target)
                .append(" where ").append(whereFieldNameString).toString();

        this.args.addAll(whereFieldValueList);

        return this;
    }
}
