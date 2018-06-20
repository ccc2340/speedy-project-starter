package org.speedy.data.orm.domain.statement;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.speedy.common.util.ReflectUtils;

/**
 * @Description 生成批量添加的sql命令
 * @Author chenguangxue
 * @CreateDate 2018/06/15 23:16
 */
public class BatchInsertStatement extends BatchStatement {

	public BatchInsertStatement(Object[] insertObjects) {
		super(insertObjects);
	}

	public BatchInsertStatement insert_into() {
		setDatabaseTarget(batchObjects[0]);
		return this;
	}

	// 这个是唯一的，因为所有添加对象应该都是相同类型
	private List<String> insertFieldNameList = new LinkedList<>();

	// 占位符个数与<insertFieldNameList>中的元素个数相同
	private List<String> placeholderList = new LinkedList<>();

	/* 执行插入时，需要忽略的字段名称 */
	private static List<String> IGNORE_FIELD = new ArrayList<>();

	static {
		IGNORE_FIELD.add("serialVersionUID");
	}

	public BatchInsertStatement values() {
		// 操作第一个对象
		Object insertObject = batchObjects[0];

		// 找出需要执行插入操作的字段列表
		List<Field> insertFieldList = Arrays.stream(targetFields).filter(f -> !IGNORE_FIELD.contains(f.getName()))
				.filter(f -> ReflectUtils.methodGetFieldValue(insertObject, f) != null).collect(Collectors.toList());

		// 从插入字段中提取出字段名以及占位符
		insertFieldList.forEach(f -> {
			insertFieldNameList.add(f.getName());
			placeholderList.add(getPlaceholder());
		});

		Arrays.asList(batchObjects).forEach(object -> {
			insertFieldList.forEach(f -> {
				args.add(ReflectUtils.directGetFieldValue(object, f));
			});
		});

		return this;
	}

	@Override
	SqlStatement complete() {
		String delimiter = ",";
		String prefix = "(";
		String suffix = ")";

		// 拼接插入的字段名称列表
		String insertFieldNameString = insertFieldNameList.stream()
				.collect(Collectors.joining(delimiter, prefix, suffix));

		// 拼接占位符
		String placeholderString = placeholderList.stream().collect(Collectors.joining(delimiter, prefix, suffix));

		// 如果是插入多个对象，则将占位符重复多次
		List<String> placeholderList = new ArrayList<>(batchObjects.length);
		Arrays.stream(batchObjects).forEach(e -> {
			placeholderList.add(placeholderString);
		});
		String placeholderListString = placeholderList.stream().collect(Collectors.joining(delimiter));

		// 拼接完整的sql语句
		this.sql = stringBuilder.append("insert into ").append(target).append(insertFieldNameString).append(" values ")
				.append(placeholderListString).toString();

		return this;
	}

}
