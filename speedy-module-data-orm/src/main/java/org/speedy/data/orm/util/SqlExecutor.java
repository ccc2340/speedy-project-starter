package org.speedy.data.orm.util;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.speedy.common.util.ReflectUtils;
import org.speedy.data.orm.domain.sql.SqlCombo;
import org.speedy.data.orm.exception.SpeedyDataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description SQL命令执行器
 * @Author chenguangxue
 * @CreateDate 2018/06/11 22:02
 */
@Component
public class SqlExecutor {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DatabaseValueConverter valueConverter;

    /* 从连接池中获取可用的连接对象 */
    private Connection initConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        return connection;
    }

    /* 获取可用的预编译对象 */
    private PreparedStatement initPreparedStatement(Connection connection, SqlCombo sqlCombo) throws SQLException {
        PreparedStatement pst = connection.prepareStatement(sqlCombo.getSql());
        for (int i = 0, size = sqlCombo.getArgs().size(); i < size; i++) {
            pst.setObject(i + 1, sqlCombo.getArgs().get(i));
        }
        return pst;
    }

    /* 执行非查询语句 */
    public int executeNonQuery(SqlCombo sqlCombo) {
        try (Connection con = initConnection(); PreparedStatement pst = initPreparedStatement(con, sqlCombo);) {
            return pst.executeUpdate();
        } catch (SQLException e) {
            throw new SpeedyDataAccessException("fail to execute", e);
        }
    }

    /* 执行添加语句，并且返回主键 */
    public int executeNonQueryAndReturnPrimary(SqlCombo sqlCombo) {
        try (Connection con = initConnection();
             PreparedStatement pst = con.prepareStatement(sqlCombo.getSql(), Statement.RETURN_GENERATED_KEYS);) {
            for (int i = 0, size = sqlCombo.getArgs().size(); i < size; i++) {
                pst.setObject(i + 1, sqlCombo.getArgs().get(i));
            }
            pst.executeUpdate();
            ResultSet keys = pst.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new SpeedyDataAccessException("fail to execute", e);
        }
        return 0;
    }

    /* 执行数值查询语句 */
    public Object executeValueQuery(SqlCombo sqlCombo) {
        try (Connection con = initConnection();
             PreparedStatement pst = initPreparedStatement(con, sqlCombo);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getObject(1);
            }
            return null;
        } catch (SQLException e) {
            throw new SpeedyDataAccessException("fail to execute", e);
        }
    }

    /* 执行对象查询语句 */
    public <T> List<T> executeObjectQuery(SqlCombo sqlCombo, Class<T> clazz) {
        try (Connection con = initConnection();
             PreparedStatement pst = initPreparedStatement(con, sqlCombo);
             ResultSet rs = pst.executeQuery()) {
            ResultSetMetaData metaData = null;
            List<T> objectResult = new LinkedList<>();
            while (rs.next()) {
                if (metaData == null) {
                    metaData = rs.getMetaData();
                }
                T t = convertObject(rs, metaData, clazz);
                objectResult.add(t);
            }
            return objectResult;
        } catch (SQLException e) {
            throw new SpeedyDataAccessException("fail to execute", e);
        }
    }

    private <T> T convertObject(ResultSet rs, ResultSetMetaData metaData, Class<T> clazz) {
        T t = ReflectUtils.newInstance(clazz);

        try {
            int dbColumnCount = metaData.getColumnCount();
            for (int i = 0; i < dbColumnCount; i++) {
                String columnName = metaData.getColumnName(i + 1);
                Field field = ReflectUtils.findField(clazz, columnName, true);
                if (field == null) {
                    continue;
                }
                String dbValue = rs.getString(columnName);
                if (dbValue == null) {
                    continue;
                }
                Object javaValue = valueConverter.convert(dbValue, field);
                ReflectUtils.methodSetFieldValue(t, field, javaValue);
            }
        } catch (SQLException e) {
            throw new SpeedyDataAccessException("fail convert object", e);
        }

        return t;
    }
}
