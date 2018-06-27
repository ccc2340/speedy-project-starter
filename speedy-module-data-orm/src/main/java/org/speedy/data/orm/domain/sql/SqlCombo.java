package org.speedy.data.orm.domain.sql;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.speedy.data.orm.domain.statement.SqlStatement;

import lombok.Data;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/12 13:44
 */
@Data
public class SqlCombo {

    private static Logger logger = LoggerFactory.getLogger(SqlCombo.class);

    private String sql;
    private List<Object> args;

    public SqlCombo(String sql, List<Object> args) {
        this.sql = sql;
        this.args = args;

        logger.info("sql @ " + sql);
        logger.info("args @ " + args);
    }

    public static SqlCombo from(SqlStatement sqlStatement) {
        List<Object> argsList = new ArrayList<>();
        /* 将参数值中的enum值转换为String */
        for (Object e : sqlStatement.getArgs()) {
            if (e == null) {
                argsList.add(null);
            }
            else if (e.getClass().isEnum()) {
                argsList.add(e.toString());
            }
            else {
                argsList.add(e);
            }
        }
        return new SqlCombo(sqlStatement.getSql(), argsList);
    }
}
