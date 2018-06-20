package org.speedy.data.orm.domain.statement;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Table;

import org.speedy.common.util.ReflectUtils;
import org.speedy.data.orm.domain.sql.SqlCombo;

/**
 * @Description SQL语句
 * @Author chenguangxue
 * @CreateDate 2018/06/10 22:53
 */
public abstract class SqlStatement {

	// 操作目标
	String target;
	StringBuilder stringBuilder;

	// sql语句
	String sql;
	// 参数集合
	List<Object> args;

	public String getSql() {
		return sql;
	}

	public List<Object> getArgs() {
		return args;
	}

	// 条件字段名称及数据列表
	private List<String> whereFieldNameList;
	List<Object> whereFieldValueList;

	String whereFieldNameString;

	SqlStatement() {
		this.args = new LinkedList<>();
		this.stringBuilder = new StringBuilder();
		this.whereFieldNameList = new LinkedList<>();
		this.whereFieldValueList = new LinkedList<>();
	}

	void extractWhere(Object object) {
		Field[] fields = object.getClass().getDeclaredFields();

		for (Field field : fields) {
			Object fieldValue = ReflectUtils.methodGetFieldValue(object, field);
			if (fieldValue != null) {
				whereFieldNameList.add(field.getName() + " = " + getPlaceholder());
				whereFieldValueList.add(fieldValue);
			}
		}

		// 如果拼接的条件个数为0，说明未设置条件，则设置默认条件
		if (whereFieldNameList.isEmpty()) {
			whereFieldNameString = " 1=1 ";
			return;
		}

		whereFieldNameString = whereFieldNameList.stream().collect(Collectors.joining(" and "));
	}

	/* 设置数据库操作目标 */
	/* 1、首先查找@Table注解的name属性值 */
	void setDatabaseTarget(Object object) {
		Table annotation = object.getClass().getAnnotation(Table.class);
		this.target = annotation.name();
	}

	/* 获取字段的名称 */
	String getDatabaseFieldName(Field field) {
		return "`" + field.getName() + "`";
	}

	/* sql语句中的占位符 */
	String getPlaceholder() {
		return "?";
	}

	/* 完成sql语句及参数的构造 */
	abstract SqlStatement complete();

	/* 创建SqlCombo对象 */
	public final SqlCombo combo() {
		SqlStatement sqlStatement = this.complete();
		return SqlCombo.from(sqlStatement);
	}
}
