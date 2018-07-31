package org.speedy.data.orm;

import org.speedy.data.orm.convertor.DatabaseConverter;
import org.speedy.data.orm.repository.RepositoryTemplate;
import org.speedy.data.orm.util.MysqlSqlBuilder;
import org.speedy.data.orm.util.SqlBuilder;
import org.speedy.data.orm.util.SqlExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/20 22:48
 */
@SpringBootApplication
public class SpeedyDataOrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpeedyDataOrmApplication.class, args);
    }

    @Bean
    public SqlBuilder sqlBuilder() {
        return new MysqlSqlBuilder();
    }

    @Bean
    public SqlExecutor sqlExecutor() {
        return new SqlExecutor();
    }

    @Bean
    public DatabaseConverter valueConverter() {
        return new DatabaseConverter();
    }

    @Bean
    public RepositoryTemplate repositoryTemplate() {
        return new RepositoryTemplate();
    }
}
