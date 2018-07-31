package org.speedy.file.domain;

import com.alibaba.fastjson.JSON;
import org.speedy.common.annotation.MappingClass;
import org.speedy.file.exception.SpeedyFileException;

/**
 * @Description 用于导入excel数据时的接口
 * @Author chenguangxue
 * @CreateDate 2018/7/4 14:56
 */
public interface ExcelImportObject {

    default Object toPersistentObject() {
        // 转换目标是由@MappingClass注解设置
        if (!this.getClass().isAnnotationPresent(MappingClass.class)) {
            String message = String.format("[%s] 未设置 @MappingClass，转换目标无法识别", this.getClass());
            throw new SpeedyFileException(message);
        }

        MappingClass annotation = this.getClass().getAnnotation(MappingClass.class);
        String jsonString = JSON.toJSONString(this);
        Object po = JSON.parseObject(jsonString).toJavaObject(annotation.clazz());

        extraDeal(po);
        return po;
    }

    /* 对转换对象进行额外处理的方法，默认情况下为空 */
    default void extraDeal(Object po) {
    }
}
