package org.speedy.common.annotation;

import java.lang.annotation.*;

/**
 * @Description 标记详情对象的注解
 * @Author chenguangxue
 * @CreateDate 2018/7/12 22:47
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DetailObject {

    enum Type {
        TABLE, ENTITY
    }
}
