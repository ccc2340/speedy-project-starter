package org.speedy.file.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description 用于设置excel导入导出的字段注解
 * @Author chenguangxue
 * @CreateDate 2018/7/4 09:39
 */
@Documented
@Retention(RUNTIME)
@Target(value = {FIELD})
public @interface ExcelObjectField {

    String value();
}
