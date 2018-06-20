package org.speedy.data.orm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description 条件字段的重复对象
 * @Author chenguangxue
 * @CreateDate 2018/06/20 17:26
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface ConditionFields {

    ConditionField[] value();
}
