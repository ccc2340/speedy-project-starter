package org.speedy.data.orm.annotation;

import org.speedy.data.orm.constant.FontColor;
import org.speedy.data.orm.constant.FieldType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description 用于设置vo字段属性的注解
 * @Author chenguangxue
 * @CreateDate 2018/06/21 15:34
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface ViewObjectField {

    /* 字段名称，默认值为字段本身的名称 */
    String title();

    /* 页面显示颜色，默认为黑色，如果需要设置 */
    FontColor color() default FontColor.BLACK;

    /* 外键关联的类型 */
    Class<?> joinClass() default Object.class;

    /* 外键关联对象需要用于使用的字段名称 */
    String joinField() default "";

    /* 页面的js方法 */
    String js() default "";

    /* 特殊的数据类型：日期、时间、货币等 */
    FieldType type() default FieldType.TEXT;

    /* 字符的最大长度，如果超出这个长度则会截取内容显示 */
    int maxLength() default 0;

    /* 字符超出长度时，连接符的值 */
    String overflow() default "....";

    String trueValue() default "是";

    String falseValue() default "否";

    String dateFormat() default "yyyy-MM-dd";

    String timeFormat() default "HH:mm:ss";

    String datetimeFormat() default "yyyy-MM-dd HH:mm:ss";
}