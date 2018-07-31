package org.speedy.common.annotation;

import org.speedy.common.constant.FontColor;
import org.speedy.common.domain.Color;
import org.speedy.common.domain.ViewObjectFieldEnum;
import org.speedy.common.exception.SpeedyCommonException;
import org.speedy.common.util.ModelUtils;
import org.speedy.common.util.ReflectUtils;
import org.speedy.common.util.TimeUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * @Description 代表值对象的抽象类
 * @Author chenguangxue
 * @CreateDate 2018/06/15 18:48
 */
public abstract class ViewObject {

    public static final String PRIMARY_FIELD_TITLE = "编号";
    public static final String BUTTON_FIELD_TITLE = "操作";

    private Field[] fields;
    private Serializable primary;
    private int index;
    protected Object po;

    public ViewObject() {
        fields = ReflectUtils.getAllFields(this.getClass());
    }

    /* 提取出页面需要显示的数据列表 */
    public List<ViewObjectValueAggregate> generateWith(Object po,
                                                       Map<Class<?>, Map<?, ?>> joinObjects,
                                                       int index,
                                                       Object templete) {
        Field[] voFields = fields;
        List<ViewObjectValueAggregate> values = new ArrayList<>(voFields.length);
        this.primary = ModelUtils.getPrimaryValue(po);
        this.index = index;
        this.po = po;

        for (Field voField : voFields) {
            ViewField annotation = voField.getAnnotation(ViewField.class);
            switch (annotation.type()) {
                case BUTTON: {
                    values.add(buildButtons(voField, primary, index));
                    break;
                }
                case METHOD: {
                    values.add(buildVoFieldMethod(voField, po, templete));
                    break;
                }
                default: {
                    values.add(buildVoFieldValue(voField, annotation, joinObjects));
                    break;
                }
            }
        }
        return values;
    }

    private ViewObjectValueAggregate buildVoFieldMethod(Field voField, Object po, Object templete) {
        ViewField annotation = voField.getAnnotation(ViewField.class);
        List<Method> methods = ReflectUtils.findMethodByName(this.getClass(), voField.getName());
        Method method = methods.get(0);

        Object methodResult = ReflectUtils.invokeMethod(method, this, Arrays.asList(po, templete));
        FontColor color = annotation.color();
        String href = "";
        if (annotation.js().length() > 0) {
            href = String.format("%s('%s','%s')", annotation.js(), primary, index);
        }

        return ViewObjectValueAggregate.createToNonButton(color.getColorCode(), methodResult, href);
    }

    /* 构建vo中的字段值 */
    private ViewObjectValueAggregate buildVoFieldValue(Field voField, ViewField annotation,
                                                       Map<Class<?>, Map<?, ?>> joinObjects) {
        String fieldName = voField.getName();
        Field poField = ReflectUtils.findField(po.getClass(), fieldName);
        Object poFieldValue = ReflectUtils.directGetFieldValue(po, poField);

        Object content = poFieldValueToVoFieldValue(poFieldValue, annotation, joinObjects);

        String colorCode = annotation.color().getColorCode();
        // 颜色只取决于枚举字段
        if (poField.getType().isEnum()) {
            if (Color.class.isAssignableFrom(poField.getType())) {
                colorCode = ((Color) poFieldValue).getColorCode();
            }
        }

        String href = "";
        if (annotation.js().length() > 0) {
            href = String.format("%s('%s','%s')", annotation.js(), primary, index);
        }

        return ViewObjectValueAggregate.createToNonButton(colorCode, content, href);
    }

    /* 构建vo对象中的按钮 */
    private ViewObjectValueAggregate buildButtons(Field buttonField, Object primary, int index) {
        // 首先提取出所有针对按钮的条件注解
        Map<ButtonType, String> conditionMap = new HashMap<>();
        if (buttonField.isAnnotationPresent(ConditionMethods.class)) {
            ConditionMethods annotations = buttonField.getAnnotation(ConditionMethods.class);
            if (annotations != null && annotations.value().length > 0) {
                Arrays.asList(annotations.value()).forEach(annotation -> {
                    conditionMap.put(annotation.type(), annotation.method());
                });
            }
        }
        else if (buttonField.isAnnotationPresent(ConditionMethod.class)) {
            ConditionMethod annotation = buttonField.getAnnotation(ConditionMethod.class);
            conditionMap.put(annotation.type(), annotation.method());
        }

        // 提取出按钮字段的值
        ButtonType[] buttonValue = (ButtonType[]) ReflectUtils.directGetFieldValue(this, buttonField);
        List<ViewObjectButton> buttons = new ArrayList<>(buttonValue.length);
        for (ButtonType btnType : buttonValue) {
            // 构造按钮之前，先判断是否需要显示
            if (conditionMap.containsKey(btnType)) {
                String conditionMethod = conditionMap.get(btnType);
                if (!checkButtonCondition(conditionMethod)) {
                    continue;
                }
            }

            buttons.add(ViewObjectButton.create(btnType, primary, index));
        }

        return ViewObjectValueAggregate.createToButton(buttons);
    }

    /* 检查按钮是否需要显示 */
    private boolean checkButtonCondition(String conditionMethod) {
        Method method = ReflectUtils.findMethod(this.getClass(), conditionMethod, Object.class);
        if (method == null) {
            String message = String.format("在[%s]中未发现方法：[%s]", po.getClass(), conditionMethod);
            throw new SpeedyCommonException(message);
        }
        Object invokeResult = ReflectUtils.invokeMethod(method, this, Collections.singletonList(po));
        return (Boolean) invokeResult;
    }

    /* 从po和joinObjects中提取出页面显示时所需要的字段信息 */
    private Object poFieldValueToVoFieldValue(Object poFieldValue, ViewField annotation,
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