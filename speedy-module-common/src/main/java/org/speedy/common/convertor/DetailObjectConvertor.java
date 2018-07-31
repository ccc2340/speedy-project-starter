package org.speedy.common.convertor;

import org.speedy.common.annotation.DetailObject;
import org.speedy.common.annotation.DetailObjectField;
import org.speedy.common.annotation.ViewField;
import org.speedy.common.domain.ViewObjectFieldEnum;
import org.speedy.common.domain.DetailObjectEntry;
import org.speedy.common.domain.DetailObjectTable;
import org.speedy.common.util.ReflectUtils;
import org.speedy.common.util.TimeUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 详情对象转换器
 * @Author chenguangxue
 * @CreateDate 2018/7/12 18:02
 */
public class DetailObjectConvertor {

    /* 检查一个指定类型是否为@DetailObject，并且检查其中的所有字段是否已经添加了@DetailObjectField注解 */
    private static void check(Class<?> clazz) {
        Assert.notNull(clazz, "转换详情对象时目标类型为空");
        Assert.isTrue(clazz.isAnnotationPresent(DetailObject.class),
                "转换详情对象时目标类型不被支持 @ " + clazz);

        Field[] allFields = ReflectUtils.getAllFields(clazz);
        Arrays.asList(allFields).forEach(field -> {
            Assert.isTrue(field.isAnnotationPresent(DetailObjectField.class),
                    "字段未设置DetailObjectField注解 @ " + field);
        });
    }

    /* 将po对象按照指定的详情类型转换为详情entity对象 */
    public static DetailObjectEntry entity(Object po, Class<?> detailClass) {
        check(detailClass);

        Field[] doFields = ReflectUtils.getAllFields(detailClass);

        DetailObjectEntry entry = new DetailObjectEntry();
        Arrays.asList(doFields).forEach(field -> {
            Object poFieldValue = getPoFieldValue(po, field);
            entry.addData(poFieldValue, field.getAnnotation(DetailObjectField.class));
        });

        return entry;
    }

    /* 将po对象按照指定的详情类型转换为详情table对象 */
    public static DetailObjectTable table(List<?> pos, Class<?> detailClass) {
        check(detailClass);

        // 提取标题
        Field[] doFields = ReflectUtils.getAllFields(detailClass);
        List<String> title = Arrays.stream(doFields).
                map(field -> field.getAnnotation(DetailObjectField.class).value()).
                collect(Collectors.toList());

        // 提取数据
        List<List<Object>> datas = new LinkedList<>();
        if (pos != null && !pos.isEmpty()) {
            pos.forEach(po -> {
                datas.add(Arrays.stream(doFields).map(doField -> getPoFieldValue(po, doField)).
                        collect(Collectors.toList()));
            });
        }

        return DetailObjectTable.build(title, datas);
    }

    /* 通过do对象的字段，查找po对象中对应字段的值 */
    private static Object getPoFieldValue(Object po, Field doField) {
        if (po == null) {
            return "";
        }

        Class<?> poClass = po.getClass();
        String fieldName = doField.getName();
        Field poField = ReflectUtils.findField(poClass, fieldName);
        Object poFieldValue = poField == null ? null : ReflectUtils.directGetFieldValue(po, poField);

        return convertPoFieldValue(poFieldValue, poField);
    }

    /* 将po字段值按照do字段的参数进行转换 */
    public static Object convertPoFieldValue(Object poValue, Field poField) {
        if (poField == null || poValue == null) {
            return null;
        }
        Class<?> poFieldType = poField.getType();

        if (poFieldType == LocalDateTime.class) {
            return TimeUtils.formatString((LocalDateTime) poValue);
        }

        if (!poField.isAnnotationPresent(ViewField.class)) {
            return poValue;
        }

        ViewField annotation = poField.getAnnotation(ViewField.class);
        if (poValue.getClass().isEnum()) {
            if (ViewObjectFieldEnum.class.isAssignableFrom(poValue.getClass())) {
                return ((ViewObjectFieldEnum) poValue).getDescription();
            }
            else {
                return poValue.toString();
            }
        }
        else if (poFieldType == Boolean.class || poFieldType == boolean.class) {
            boolean boolValue = (boolean) poValue;
            return boolValue ? annotation.trueValue() : annotation.falseValue();
        }
        return poValue;
    }
}
