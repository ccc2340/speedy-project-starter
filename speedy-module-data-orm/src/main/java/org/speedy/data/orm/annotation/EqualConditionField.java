package org.speedy.data.orm.annotation;

import org.speedy.data.orm.domain.base.QueryCondition;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/26 18:25
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
@ConditionField(type = QueryCondition.Type.EQUAL)
public @interface EqualConditionField {

    /* 条件字段对应的数据库字段名称，默认就是条件字段本身的名称 */
    @AliasFor(annotation = ConditionField.class)
    String field() default "";
}
