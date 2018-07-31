package org.speedy.data.orm.util;

import lombok.extern.slf4j.Slf4j;
import org.speedy.data.orm.convertor.DatabaseConverter;
import org.speedy.data.orm.domain.sql.NamedSqlCombo;
import org.speedy.data.orm.domain.sql.SqlCombo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @Description SQL命令执行器
 * @Author chenguangxue
 * @CreateDate 2018/06/11 22:02
 */
@Component
@Slf4j
public class SqlExecutor {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    /* 执行非查询语句 */
    public int executeNonQuery(SqlCombo sqlCombo) {
        return jdbcTemplate.update(sqlCombo.getSql(), sqlCombo.getArgs().toArray());
    }

    /* 执行添加语句，并且返回主键 */
    public int executeNonQueryAndReturnPrimary(NamedSqlCombo sqlCombo) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource(sqlCombo.getArgsMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int update = namedJdbcTemplate.update(sqlCombo.getSql(), parameterSource, keyHolder);
        if (update == 0) {
            return 0;
        }
        else {
            return keyHolder.getKey().intValue();
        }
    }

    /* 执行数值查询语句 */
    public Object executeValueQuery(SqlCombo sqlCombo) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlCombo.getSql(), sqlCombo.getArgs().toArray());
        if (rowSet.next()) {
            return rowSet.getObject(1);
        }
        else {
            return null;
        }
    }

    /* 执行对象查询语句 */
    public <T> List<T> executeObjectQuery(SqlCombo sqlCombo, Class<T> clazz) {
        List<T> result = new LinkedList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlCombo.getSql(), sqlCombo.getArgs().toArray());
        SqlRowSetMetaData metaData = rowSet.getMetaData();
        while (rowSet.next()) {
            T t = DatabaseConverter.convertObject(clazz, rowSet, metaData);
            result.add(t);
        }
        return result;
    }
}
