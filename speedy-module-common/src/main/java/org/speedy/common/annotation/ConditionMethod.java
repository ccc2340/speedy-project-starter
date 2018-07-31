package org.speedy.common.annotation;

import java.lang.annotation.*;

/**
 * @Description 用于判断vo按钮是否显示的注解
 * @Author chenguangxue
 * @CreateDate 2018/7/8 00:14
 */
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = ConditionMethods.class)
public @interface ConditionMethod {

    String method();

    ButtonType type();
}
