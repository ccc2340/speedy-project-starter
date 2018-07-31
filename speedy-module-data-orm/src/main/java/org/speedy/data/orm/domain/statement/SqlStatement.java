package org.speedy.data.orm.domain.statement;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.speedy.data.orm.domain.sql.SqlCombo;

import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description SQL语句
 * @Author chenguangxue
 * @CreateDate 2018/06/10 22:53
 */
@Slf4j
@Getter
public abstract class SqlStatement {

    // 操作目标
    protected String target;

    // sql语句
    protected String sql;

    // 参数集合
    protected List<Object> args;

    SqlStatement() {
        this.args = new LinkedList<>();
    }

    /* 设置数据库操作目标 */
    /* 1、首先查找@Table注解的name属性值 */
    protected void setDatabaseTarget(Class<?> clazz) {
        Table annotation = clazz.getAnnotation(Table.class);
        this.target = annotation.name();
    }

    /* 获取字段的名称 */
    protected String getDatabaseFieldName(Field field) {
        return "`" + field.getName() + "`";
    }

    /* 完成sql语句及参数的构造，由子类自行完成，生成sql语句以及参数集合 */
    abstract SqlStatement complete();

    /* 创建SqlCombo对象 */
    public SqlCombo combo() {
        SqlStatement sqlStatement = this.complete();
        return SqlCombo.from(sqlStatement);
    }
}
