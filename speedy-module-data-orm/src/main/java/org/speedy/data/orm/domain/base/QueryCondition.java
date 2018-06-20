package org.speedy.data.orm.domain.base;

/**
 * @Description 表示复杂查询条件的接口
 * @Author chenguangxue
 * @CreateDate 2018/06/10 22:20
 */
public interface QueryCondition {

    /* 条件类型 */
    enum Type {
        EQUAL("="),
        GREAT(">"),
        LESS("<"),
        GREAT_OR_EQUAL(">="),
        LESS_OR_EQUAL("<="),
        NOT_EQUAL("!="),
        LIKE("like"),
        NOT_LIKE("not like"),
        NULL("is null"),
        NOT_NULL("is not null"),

        // end
        ;
        private final String symbol;

        public String getSymbol() {
            return symbol;
        }

        Type(String symbol) {
            this.symbol = symbol;
        }
    }
}
