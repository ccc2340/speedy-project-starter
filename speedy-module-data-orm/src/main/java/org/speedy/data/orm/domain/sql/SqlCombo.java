package org.speedy.data.orm.domain.sql;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.speedy.data.orm.domain.statement.SqlStatement;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/12 13:44
 */
@Data
public class SqlCombo {

    private static Logger logger = LoggerFactory.getLogger(SqlCombo.class);

    protected final String sql;
    private final List<Object> args;

    SqlCombo(String sql) {
        this.args = new LinkedList<>();
        this.sql = sql;
    }

    public SqlCombo(String sql, List<Object> args) {
        this(sql);
        this.args.addAll(args);
    }

    public static SqlCombo from(SqlStatement sqlStatement) {
        SqlCombo sqlCombo = new SqlCombo(sqlStatement.getSql());

        /* 将参数值中的enum值转换为String */
        for (Object e : sqlStatement.getArgs()) {
            if (e == null) {
                sqlCombo.args.add(null);
            }
            else if (e.getClass().isEnum()) {
                sqlCombo.args.add(e.toString());
            }
            else if (Collection.class.isAssignableFrom(e.getClass())) {
                Collection collection = (Collection) e;
                sqlCombo.args.addAll(collection);
            }
            else {
                sqlCombo.args.add(e);
            }
        }

        logger.info("sql @ " + sqlCombo.sql);
        logger.info("args @ " + sqlCombo.args);

        return sqlCombo;
    }
}
