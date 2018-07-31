package org.speedy.data.orm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description 标记条件对象的注解
 * @Author chenguangxue
 * @CreateDate 2018/7/15 16:03
 */
@Documented
@Retention(RUNTIME)
@Target(value = {TYPE})
public @interface ConditionObject {

    Class<?> value();
}
