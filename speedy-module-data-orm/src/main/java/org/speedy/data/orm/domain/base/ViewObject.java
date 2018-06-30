package org.speedy.data.orm.domain.base;

import org.speedy.common.util.ModelUtils;
import org.speedy.common.util.ReflectUtils;
import org.speedy.common.util.TimeUtils;
import org.speedy.data.orm.annotation.ViewObjectField;
import org.speedy.data.orm.constant.FontColor;

import java.awt.*;
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
    public static final String BUTTON_FIELD_TITLE = "操作";

    private Field[] fields;

    public ViewObject() {
        fields = this.getClass().getDeclaredFields();
    }

    /* 提取出页面需要显示的数据列表 */
    public List<ViewObjectValueAggregate> generateWith(Object po, Map<Class<?>, Map<?, ?>> joinObjects, int index) {
        Field[] voFields = fields;
        List<ViewObjectValueAggregate> values = new ArrayList<>(voFields.length);
        Serializable primaryValue = ModelUtils.getPrimaryValue(po);
        for (Field voField : voFields) {
            ViewObjectField annotation = voField.getAnnotation(ViewObjectField.class);
            switch (annotation.type()) {
                case BUTTON: {
                    values.add(buildButtons(voField, primaryValue, index));
                    break;
                }
                default: {
                    values.add(buildVoFieldValue(voField, annotation, po, joinObjects));
                    break;
                }
            }
        }
        return values;
    }

    /* 构建vo中的字段值 */
    private ViewObjectValueAggregate buildVoFieldValue(Field voField, ViewObjectField annotation, Object po,
                                                       Map<Class<?>, Map<?, ?>> joinObjects) {
        String fieldName = voField.getName();
        Field poField = ReflectUtils.findField(po.getClass(), fieldName);
        Object poFieldValue = ReflectUtils.directGetFieldValue(po, poField);

        Object content = poFieldValueToVoFieldValue(poFieldValue, annotation, joinObjects);
        FontColor color = annotation.color();

        return ViewObjectValueAggregate.createToNonButton(color, content);
    }

    /* 构建vo对象中的按钮 */
    private ViewObjectValueAggregate buildButtons(Field buttonField, Object primary, int index) {
        // 提取出按钮字段的值
        ButtonType[] buttonValue = (ButtonType[]) ReflectUtils.directGetFieldValue(this, buttonField);
        List<ViewObjectButton> buttons = new ArrayList<>(buttonValue.length);
        for (ButtonType btnType : buttonValue) {
            buttons.add(ViewObjectButton.create(btnType, primary, index));
        }

        return ViewObjectValueAggregate.createToButton(buttons);
    }

    /* 从po和joinObjects中提取出页面显示时所需要的字段信息 */
    private Object poFieldValueToVoFieldValue(Object poFieldValue, ViewObjectField annotation,
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