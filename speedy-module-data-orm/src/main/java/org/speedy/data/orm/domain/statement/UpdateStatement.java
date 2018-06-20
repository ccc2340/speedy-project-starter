package org.speedy.data.orm.domain.statement;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.speedy.common.util.ModelUtils;
import org.speedy.common.util.ReflectUtils;

/**
 * @Description 修改语句模板
 * @Author chenguangxue
 * @CreateDate 2018/06/11 15:25
 */
public class UpdateStatement extends SqlStatement {

	public UpdateStatement() {
		setFieldNameList = new LinkedList<>();
		setFieldValueList = new LinkedList<>();
	}

	/* 设置操作目标 */
	public UpdateStatement update(Object object) {
		setDatabaseTarget(object);
		return this;
	}

	/* 需要修改的字段名称及数据列表 */
	private List<String> setFieldNameList;
	private List<Object> setFieldValueList;

	private String setFieldNameString;

	/* 设置变更数据:提取除主键之外的字段列表 */
	public UpdateStatement set(Object object) {
		Field[] fields = object.getClass().getDeclaredFields();
		Field primaryField = ModelUtils.getPrimaryField(object.getClass());
		for (Field field : fields) {
			if (field.equals(primaryField)) {
				continue;
			}
			Object fieldValue = ReflectUtils.methodGetFieldValue(object, field);
			setFieldNameList.add(getDatabaseFieldName(field) + " = " + getPlaceholder());
			setFieldValueList.add(fieldValue);
		}

		setFieldNameString = setFieldNameList.stream().collect(Collectors.joining(","));
		return this;
	}

	/* 设置条件数据:只能使用主键作为条件 */
	public UpdateStatement where(Object object) {
		Field primaryField = ModelUtils.getPrimaryField(object.getClass());
		this.whereFieldNameString = getDatabaseFieldName(primaryField) + "=" + getPlaceholder();

		this.whereFieldValueList.add(ReflectUtils.directGetFieldValue(object, primaryField));
		return this;
	}

	@Override
	SqlStatement complete() {
		this.sql = stringBuilder.append("update ").append(target).append(" set ").append(setFieldNameString)
				.append(" where ").append(whereFieldNameString).toString();

		this.args.addAll(setFieldValueList);
		this.args.addAll(whereFieldValueList);

		return this;
	}
}
