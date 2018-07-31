package org.speedy.data.orm.constant;

/**
 * @Description sql相关常量集合
 * @Author chenguangxue
 * @CreateDate 2018/7/15 22:06
 */
public class SqlConstant {

    public static final String PLACE_HOLDER = "?";

    public static String insertPlaceholder(String fieldName) {
        return ":" + fieldName;
    }
}
