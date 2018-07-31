package org.speedy.common.annotation;

import java.lang.annotation.*;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/7/8 21:07
 */
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionMethods {

    ConditionMethod[] value();
}
