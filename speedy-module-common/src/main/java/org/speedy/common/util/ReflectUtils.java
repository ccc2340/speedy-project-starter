package org.speedy.common.util;

import lombok.extern.slf4j.Slf4j;
import org.speedy.common.exception.NoFieldAccessMethodException;
import org.speedy.common.exception.SpeedyCommonException;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description 反射工具包
 * @Author chenguangxue
 * @CreateDate 2018/06/08 10:05
 */
@Slf4j
public class ReflectUtils {

    private static final Class<?>[] EMPTY_PARAMETER_TYPES = new Class<?>[0];

    /* 反射工具的缓存集合 */
    private static final int MAX_CACHE_SIZE = 64;
    private static final Map<Class<?>, Field[]> CLASS_FIELD_CACHE = new HashMap<>(MAX_CACHE_SIZE, 1f);
    private static final Map<Class<?>, Method[]> CLASS_METHOD_CACHE = new HashMap<>(MAX_CACHE_SIZE, 1f);

    /* 获取字段集合的方法，优先从本地缓存中获取 */
    private static Field[] findFields(Class<?> clazz) {
        if (CLASS_FIELD_CACHE.size() == MAX_CACHE_SIZE) {
            CLASS_FIELD_CACHE.clear();
        }

        if (!CLASS_FIELD_CACHE.containsKey(clazz)) {
            CLASS_FIELD_CACHE.put(clazz, clazz.getDeclaredFields());
        }
        return CLASS_FIELD_CACHE.get(clazz);
    }

    /* 获取方法集合的方法，优先从缓存中获取 */
    private static Method[] findMethods(Class<?> clazz) {
        if (CLASS_METHOD_CACHE.size() == MAX_CACHE_SIZE) {
            CLASS_METHOD_CACHE.clear();
        }

        if (!CLASS_METHOD_CACHE.containsKey(clazz)) {
            CLASS_METHOD_CACHE.put(clazz, clazz.getDeclaredMethods());
        }
        return CLASS_METHOD_CACHE.get(clazz);
    }

    public static Field[] getAllFields(Class<?> clazz) {
        return findFields(clazz);
    }

    public static Method[] getAllMethods(Class<?> clazz) {
        return findMethods(clazz);
    }

    /* 通过反射，直接获取属性值 */
    public static Object directGetFieldValue(Object object, Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            return field.get(object);
        }
        catch (IllegalAccessException e) {
            log.error("反射直接获取属性值失败，对象为：{}", object);
            log.error("反射直接获取属性值失败，字段为：{}", field);
            throw new SpeedyCommonException(e);
        }
    }

    /* 通过getter方法，获取属性值 */
    public static Object methodGetFieldValue(Object object, Field field) {
        Method getterMethod = findGetterMethod(object.getClass(), field);
        if (getterMethod == null) {
            throw NoFieldAccessMethodException.getter(object.getClass(), field);
        }
        try {
            return getterMethod.invoke(object);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new SpeedyCommonException(e);
        }
    }

    /* 通过反射，直接设置属性值 */
    public static void directSetFieldValue(Object object, Field field, Object fieldValue) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            field.set(object, fieldValue);
        }
        catch (IllegalAccessException e) {
            throw new SpeedyCommonException(e);
        }
    }

    /* 通过setter方法，设置属性值 */
    public static void methodSetFieldValue(Object object, Field field, Object fieldValue) {
        Method setterMethod = findSetterMethod(object.getClass(), field);
        if (setterMethod == null) {
            throw NoFieldAccessMethodException.setter(object.getClass(), field);
        }
        try {
            setterMethod.invoke(object, fieldValue);
        }
        catch (Exception e) {
            throw new SpeedyCommonException(e);
        }
    }

    /* 获取属性的getter方法 */
    public static Method findGetterMethod(Class<?> clazz, Field field) {
        String fieldName = field.getName();
        // 选择getter方法前缀，如果是boolean类型的字段，则以is开头
        String prefix = "get";
        if (field.getType() == boolean.class) {
            prefix = "is";
        }
        String getterMethodName = String.format("%s%s%s", prefix, fieldName.toUpperCase().charAt(0),
                fieldName.substring(1));
        return findMethod(clazz, getterMethodName, EMPTY_PARAMETER_TYPES);
    }

    /* 获取属性的setter方法 */
    public static Method findSetterMethod(Class<?> clazz, Field field) {
        String fieldName = field.getName();
        String setterMethodName = String.format("%s%s%s", "set", fieldName.toUpperCase().charAt(0),
                fieldName.substring(1));
        return findMethod(clazz, setterMethodName, field.getType());
    }

    /* 通过反射，查找指定名称的方法 */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        }
        catch (NoSuchMethodException e) {
            throw new SpeedyCommonException(e);
        }
    }

    public static List<Method> findMethodByName(Class<?> target, String name) {
        Method[] allMethods = getAllMethods(target);
        return Arrays.stream(allMethods).filter(method -> method.getName().equals(name)).
                collect(Collectors.toList());
    }

    /* 通过反射，查找指定名称的字段，默认会区分大小写 */
    public static Field findField(Class<?> clazz, String targetFieldName) {
        return findField(clazz, targetFieldName, false);
    }

    /* 通过反射，查找指定名称的字段 */
    public static Field findField(Class<?> clazz, String targetFieldName, boolean ignoreCase) {
        Field[] fields = findFields(clazz);
        for (Field f : fields) {
            String fieldName = f.getName();
            if (ignoreCase) {
                if (fieldName.equalsIgnoreCase(targetFieldName)) {
                    return f;
                }
            }
            else {
                if (fieldName.equals(targetFieldName)) {
                    return f;
                }
            }
        }
        return null;
    }

    /* 通过反射创建对象 */
    public static <T> T newInstance(Class<T> clazz, Object... args) {
        // 整理参数的类型列表
        Class<?>[] argsTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argsTypes[i] = args[i].getClass();
        }

        // 根据参数，查找对应的构造方法
        Constructor constructor = null;
        try {
            constructor = clazz.getConstructor(argsTypes);
        }
        catch (NoSuchMethodException e) {
            throw new SpeedyCommonException(e);
        }

        // 使用构造方法创建对象
        try {
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return (T) constructor.newInstance(args);
        }
        catch (Exception e) {
            throw new SpeedyCommonException(e);
        }
    }

    /* 通过反射，执行指定方法，获得结果 */
    public static Object invokeMethod(Method method, Object targetObject, List<Object> args) {
        Assert.notNull(method, "需要获取结果的方法为空");
        try {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method.invoke(targetObject, args.toArray());
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new SpeedyCommonException(e, "反射执行方法失败");
        }
    }
}
