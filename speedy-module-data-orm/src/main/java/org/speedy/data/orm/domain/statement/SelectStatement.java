package org.speedy.data.orm.domain.statement;

import org.speedy.data.orm.domain.sql.SqlQueryParameter;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 查询语句模板
 * @Author chenguangxue
 * @CreateDate 2018/06/11 15:50
 */
public class SelectStatement extends SqlStatement {

    private SqlQueryParameter queryParameter;
    private Class<?> targetClass;
    private String selectFields;
    private String where;
    private String order;
    private String page;

    public SelectStatement(SqlQueryParameter queryParameter) {
        Assert.notNull(queryParameter, "SqlQueryParameter is null");

        this.queryParameter = queryParameter;
        this.targetClass = queryParameter.getTargetClass();
    }

    private SelectStatement from() {
        setDatabaseTarget(targetClass);
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

    private SelectStatement select() {
        this.selectFields = queryParameter.getSelectType().fields;
        return this;
    }

    /* 设置查询条件 */
    private SelectStatement where() {
        where = queryParameter.getCondition().toString();
        return this;
    }

    /* 设置排序 */
    private SelectStatement orderBy() {
        List<Sort.Order> orders = queryParameter.getOrders();
        if (orders == null || orders.isEmpty()) {
            this.order = "";
        }
        else {
            List<String> orderStrings = new LinkedList<>();
            orders.forEach(order -> {
                orderStrings.add(order.getProperty() + " " + order.getDirection().name());
            });
            String orderString = orderStrings.stream().collect(Collectors.joining(","));
            this.order = "order by " + orderString;
        }
        return this;
    }

    private SelectStatement page() {
        /* 如果查询参数中需要分页，则加上分页的数据 */
        if (queryParameter.getPageInfo().isPage()) {
            int offset = queryParameter.getPageInfo().getOffset();
            int cpp = queryParameter.getPageInfo().getCpp();
            this.page = String.format("limit %d, %d", offset, cpp);
        }
        else {
            this.page = "";
        }

        return this;
    }

    @Override
    SqlStatement complete() {
        this.select().from().where().orderBy().page();

        this.sql = String.format("select %s from %s where %s %s %s", selectFields, target, where, order, page);

        this.args.addAll(queryParameter.getCondition().getArgs());

        return this;
    }
}
