package org.speedy.common.constant;

/**
 * @Description 正则表达式集合
 * @Author chenguangxue
 * @CreateDate 2018/06/12 09:10
 */
public enum RegularExpression {

    /* 日期、时间相关 */
    DATE("[0-9]{4}-[0-9]{2}-[0-9]{2}"),

    // end
    ;

    /* 使用正则表达式进行校验 */
    public boolean isMatch(String content) {
        if (content == null) {
            return false;
        }
        return content.matches(this.expression);
    }

    private String expression;

    RegularExpression(String expression) {
        this.expression = expression;
    }
}
