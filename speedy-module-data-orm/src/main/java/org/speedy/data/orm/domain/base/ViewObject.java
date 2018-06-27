package org.speedy.data.orm.domain.base;

import lombok.Getter;
import org.speedy.common.util.ReflectUtils;
import org.speedy.common.util.TimeUtils;
import org.speedy.data.orm.annotation.ViewObjectField;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 代表值对象的抽象类
 * @Author chenguangxue
 * @CreateDate 2018/06/15 18:48
 */
public abstract class ViewObject {

    public static final String PRIMARY_FIELD_TITLE = "编号";

    @Getter
    public static class Value {
        private String content;
        private String color;
        private String js;
    }

    private Field[] fields;

    public ViewObject() {
        fields = this.getClass().getDeclaredFields();
    }

    /* 提取出页面需要显示的数据列表 */
    public List<Value> generateWith(Object po, Map<Class<?>, Map<?, ?>> joinObjects) {
        Field[] voFields = fields;
        List<Value> values = new ArrayList<>(voFields.length);
        for (Field voField : voFields) {
            Value voValue = new Value();
            ViewObjectField annotation = voField.getAnnotation(ViewObjectField.class);
            String fieldName = voField.getName();
            Field poField = ReflectUtils.findField(po.getClass(), fieldName);
            Object poFieldValue = ReflectUtils.directGetFieldValue(po, poField);
            voValue.content = poFieldValueToVoFieldValue(poFieldValue, annotation, joinObjects);
            voValue.color = annotation.color().getColorCode();
            values.add(voValue);
        }
        return values;
    }

    /* 从po和joinObjects中提取出页面显示时所需要的字段信息 */
    private String poFieldValueToVoFieldValue(Object poFieldValue, ViewObjectField annotation,
                                              Map<Class<?>, Map<?, ?>> joinObjects) {
        if (poFieldValue == null) {
            return null;
        }

        Class<?> poFieldType = poFieldValue.getClass();
        // 需要转换为其他类的数据
        if (annotation.joinClass() != Object.class) {
            Class<?> joinClass = annotation.joinClass();
            String joinFieldName = annotation.joinField();
            Serializable joinObjectPrimary = (Serializable) poFieldValue;
            Object joinObject = joinObjects.get(joinClass).get(joinObjectPrimary);
            Field joinField = ReflectUtils.findField(joinClass, joinFieldName);
            return ReflectUtils.directGetFieldValue(joinObject, joinField).toString();
        }
        else if (poFieldType == Boolean.class || poFieldType == boolean.class) {
            boolean boolValue = (boolean) poFieldValue;
            return boolValue ? annotation.trueValue() : annotation.falseValue();
        }
        else if (Number.class.isAssignableFrom(poFieldType)) {
            return poFieldValue.toString();
        }
        else if (Temporal.class.isAssignableFrom(poFieldType)) {
            if (poFieldType == LocalDate.class) {
                return TimeUtils.formatString((LocalDate) poFieldValue, annotation.dateFormat());
            }
            else if (poFieldType == LocalDateTime.class) {
                return TimeUtils.formatString((LocalDateTime) poFieldValue, annotation.datetimeFormat());
            }
        }
        else if (poFieldValue.getClass().isEnum()) {
            if (ViewObjectFieldEnum.class.isAssignableFrom(poFieldValue.getClass())) {
                return ((ViewObjectFieldEnum) poFieldValue).getDescription();
            }
            else {
                return poFieldValue.toString();
            }
        }
        else {
            return poFieldValue.toString();
        }
        return null;
    }
}