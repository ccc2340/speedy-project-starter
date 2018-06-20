package org.speedy.data.orm.domain.statement;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.speedy.common.util.ReflectUtils;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/11 14:59
 */
public class InsertStatement extends SqlStatement {

	public InsertStatement() {
		this.insertFieldValueList = new LinkedList<>();
	}

	// 设置插入的目标
	public InsertStatement insert_into(Object object) {
		setDatabaseTarget(object);
		return this;
	}

	/* 执行插入时，需要忽略的字段名称 */
	private static List<String> IGNORE_FIELD = new ArrayList<>();

	static {
		IGNORE_FIELD.add("serialVersionUID");
	}

	// 设置需要的插入的字段以及数据
	public InsertStatement values(Object object) {
		Field[] fields = object.getClass().getDeclaredFields();
		List<String> insertFieldNameList = new ArrayList<>(fields.length);
		for (Field f : fields) {
			String fieldName = f.getName();
			if (IGNORE_FIELD.contains(fieldName)) {
				continue;
			}
			Object fieldValue = ReflectUtils.methodGetFieldValue(object, f);
			if (fieldValue == null) {
				continue;
			}
			insertFieldNameList.add(fieldName);
			insertFieldValueList.add(fieldValue);
		}

		final String delimiter = ",";
		final String prefix = "(";
		final String suffix = ")";

		// 将所需插入的字段列表进行拼接组合：(`name`,`sex`,`age`)
		insertFieldNameString = insertFieldNameList.stream().collect(Collectors.joining(delimiter, prefix, suffix));

		// 按照插入字段的个数，拼接出占位符列表：(?,?,?)
		List<String> placeholderList = new ArrayList<>(insertFieldNameList.size());
		insertFieldNameList.forEach(e -> placeholderList.add(getPlaceholder()));
		placeholderString = placeholderList.stream().collect(Collectors.joining(delimiter, prefix, suffix));

		return this;
	}

	private String insertFieldNameString;
	private String placeholderString;
	private List<Object> insertFieldValueList;

	@Override
	SqlStatement complete() {
		// 组装出sql语句
		this.sql = stringBuilder.append("insert into ").append(target).append(insertFieldNameString).append(" values ")
				.append(placeholderString).toString();

		// 设置占位符所对应的参数值
		this.args.addAll(insertFieldValueList);

		return this;
	}
}
