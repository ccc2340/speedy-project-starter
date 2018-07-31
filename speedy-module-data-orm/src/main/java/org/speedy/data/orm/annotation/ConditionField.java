package org.speedy.data.orm.annotation;

import org.speedy.data.orm.constant.SqlConditionType;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description 条件字段，可重复
 * @Author chenguangxue
 * @CreateDate 2018/06/20 17:25
 */
@Documented
@Retention(RUNTIME)
@Target(value = {FIELD, ANNOTATION_TYPE})
@Repeatable(value = ConditionFields.class)
public @interface ConditionField {

    /* 条件类型 */
    SqlConditionType type();

    /* 条件字段对应的数据库字段名称，默认就是条件字段本身的名称 */
    String field() default "";
}
