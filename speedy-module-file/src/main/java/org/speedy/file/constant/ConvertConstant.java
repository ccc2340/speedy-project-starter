package org.speedy.file.constant;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 转换器的常量集合
 * @Author chenguangxue
 * @CreateDate 2018/7/4 17:38
 */
public class ConvertConstant {

    public static final String DEFAULT_SHEET_NAME = "导入数据";

    public static final String TITLE_KEY = "value";

    public static final String DATA_KEY = "datas";

    public static final int FIRST_ROW_NUM = 0;

    public static final int FIRST_CELL_NUM = 0;

    private static final int CACHE_MAX_SIZE = 16;

    private static final Map<Class<?>, List<Field>> CLASS_FIELDS_CACHE = new HashMap<>(CACHE_MAX_SIZE);

    public static List<Field> findFields(Class<?> clazz) {
        if (CLASS_FIELDS_CACHE.size() == CACHE_MAX_SIZE) {
            CLASS_FIELDS_CACHE.clear();
        }

        if (!CLASS_FIELDS_CACHE.containsKey(clazz)) {
            CLASS_FIELDS_CACHE.put(clazz, Arrays.asList(clazz.getDeclaredFields()));
        }

        return CLASS_FIELDS_CACHE.get(clazz);
    }
}
