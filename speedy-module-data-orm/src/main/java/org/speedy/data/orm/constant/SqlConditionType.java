package org.speedy.data.orm.constant;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description SQL条件中使用的判断类型
 * @Author chenguangxue
 * @CreateDate 2018/7/15 00:40
 */
@Getter
public enum SqlConditionType {

    GREATER(">"),
    EQUAL("="),
    LESS("<"),
    GREATER_EQUAL(">="),
    LESS_EQUAL("<="),
    NOT_EQUAL("!="),
    LIKE("like"),
    NOT_LIKE("not like"),

    IN("in"),
    NOT_IN("not in"),
    BETWEEN("between"),
    NOT_BETWEEN("not between"),

    IS_NULL("is null"),
    IS_NOT_NULL("is not null"),

    //
    ;

    public String build(String field, Object value) {
        switch (this) {
            case IS_NULL:
            case IS_NOT_NULL:
                return String.format("%s %s", field, this.symbol);
            case BETWEEN:
            case NOT_BETWEEN:
                return String.format("%s %s %s and %s", field, this.symbol,
                        SqlConstant.PLACE_HOLDER, SqlConstant.PLACE_HOLDER);
            case IN:
            case NOT_IN:
                List<?> listN = (List<?>) value;
                String values = listN.stream().map(o -> SqlConstant.PLACE_HOLDER).
                        collect(Collectors.joining(",", "(", ")"));
                return String.format("%s %s %s", field, this.symbol, values);
            case EQUAL:
            case GREATER:
            case GREATER_EQUAL:
            case LESS:
            case LESS_EQUAL:
            case LIKE:
            case NOT_EQUAL:
            case NOT_LIKE:
                return String.format("%s %s %s", field, this.symbol, SqlConstant.PLACE_HOLDER);
        }
        return null;
    }

    private final String symbol;

    SqlConditionType(String symbol) {
        this.symbol = symbol;
    }
}
