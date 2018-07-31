package org.speedy.data.orm.util;

import lombok.extern.slf4j.Slf4j;
import org.speedy.data.orm.exception.SpeedyDataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @Description 数据库操作工具类
 * @Author chenguangxue
 * @CreateDate 2018/7/13 23:03
 */
@Slf4j
public class DatabaseUtils {

    /* 以spring推荐的方式获取数据库连接对象，这样可以使用spring的声明式事务 */
    public static Connection getConnection(DataSource dataSource) {
        return DataSourceUtils.getConnection(dataSource);
    }

    /* 获取可用的预编译对象 */
    public static PreparedStatement getPreparedStatement(Connection connection, String sql) {
        checkPreparedStatementPremise(connection, sql);

        try {
            return connection.prepareStatement(sql);
        }
        catch (SQLException e) {
            throw new SpeedyDataAccessException(e, "获取预编译对象异常");
        }
    }

    private static void checkPreparedStatementPremise(Connection connection, String sql) {
        Assert.notNull(connection, "数据库连接对象为空");
        Assert.isTrue(!StringUtils.isEmpty(sql), "sql语句为空");
    }

    /* 获取可用的预编译对象，并且会返回主键 */
    public static PreparedStatement getPreparedStatementWithGenerateKey(Connection connection, String sql) {
        checkPreparedStatementPremise(connection, sql);
        try {
            return connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        }
        catch (SQLException e) {
            throw new SpeedyDataAccessException(e, "获取预编译对象异常");
        }
    }

    /* 为预编译对象设置参数 */
    public static void setPreparedStatementParameters(PreparedStatement pst, List<?> args) {
        Assert.notNull(pst, "预编译对象为空");
        Assert.notNull(args, "参数集合为null");

        try {
            for (int i = 0, size = args.size(); i < size; i++) {
                pst.setObject(i + 1, args.get(i));
            }
        }
        catch (SQLException e) {
            throw new SpeedyDataAccessException(e, "预编译对象设置参数异常");
        }
    }

    /* 释放数据库连接资源 */
    public static void releaseConnection(Connection connection, DataSource dataSource) {
        DataSourceUtils.releaseConnection(connection, dataSource);
    }
}
