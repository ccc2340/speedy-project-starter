package org.speedy.data.orm.domain.statement;

import lombok.Getter;
import org.speedy.common.util.ReflectUtils;
import org.speedy.data.orm.constant.SqlConstant;
import org.speedy.data.orm.domain.sql.NamedSqlCombo;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 插入的sql命令
 * @Author chenguangxue
 * @CreateDate 2018/06/11 14:59
 */
@Getter
public class InsertStatement extends SqlStatement {

    private Object insertObject;
    protected Class<?> insertClass;
    protected String name;
    protected String placeholder;
    private Map<String, Object> argsMap;

    public InsertStatement() {
        this.argsMap = new HashMap<>();
    }

    public InsertStatement(Object insertObject) {
        this();

        Assert.notNull(insertObject, "插入的对象为null");

        this.insertObject = insertObject;
        this.insertClass = insertObject.getClass();
    }

    // 设置插入的目标
    protected InsertStatement insert_into() {
        setDatabaseTarget(insertClass);
        return this;
    }

    /* 执行插入时，需要忽略的字段名称 */
    static final List<String> IGNORE_FIELD = new LinkedList<>();

    static {
        IGNORE_FIELD.add("serialVersionUID");
    }

    // 设置需要的插入的字段以及数据
    protected InsertStatement values() {
        List<Field> insertFields = findInsertFields();

        List<String> names = new ArrayList<>(insertFields.size());
        List<String> placeholders = new ArrayList<>(insertFields.size());

        // 提取名称、设置占位符、设置占位符数据
        insertFields.forEach(field -> {
            String fieldName = field.getName();
            names.add(fieldName);

            String placeholder = SqlConstant.insertPlaceholder(fieldName);
            placeholders.add(placeholder);

            Object fieldValue = ReflectUtils.directGetFieldValue(insertObject, field);
            if (fieldValue.getClass().isEnum()) {
                fieldValue = fieldValue.toString();
            }
            this.argsMap.put(fieldName, fieldValue);
        });

        this.name = joinString(names);
        this.placeholder = joinString(placeholders);

        return this;
    }

    // 从insertClass中找出需要插入的字段列表
    protected List<Field> findInsertFields() {
        Field[] allFields = ReflectUtils.getAllFields(insertClass);
        return Arrays.stream(allFields).
                filter(field -> ReflectUtils.directGetFieldValue(insertObject, field) != null).
                collect(Collectors.toList());
    }

    String joinString(List<String> list) {
        return joinString(list, DELIMITER, PREFIX, SUFFIX);
    }

    String joinString(List<String> list, String delimiter, String prefix, String suffix) {
        return list.stream().collect(Collectors.joining(delimiter, prefix, suffix));
    }

    static final String DELIMITER = ",";
    static final String PREFIX = "(";
    static final String SUFFIX = ")";

    @Override
    SqlStatement complete() {
        this.insert_into().values();

        this.sql = String.format("insert into %s %s values %s", target, name, placeholder);

        return this;
    }

    @Override
    public NamedSqlCombo combo() {
        SqlStatement sqlStatement = this.complete();
        return NamedSqlCombo.fromInsert((InsertStatement) sqlStatement);
    }
}
