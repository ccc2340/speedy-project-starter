package org.speedy.common.annotation;

import org.speedy.common.constant.FieldType;

import java.lang.annotation.*;

/**
 * @Description 标记详情字段的注解
 * @Author chenguangxue
 * @CreateDate 2018/7/11 17:55
 */
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DetailObjectField {

    String value();

    FieldType type() default FieldType.TEXT;

    int width() default 1;

    int height() default 1;
}
