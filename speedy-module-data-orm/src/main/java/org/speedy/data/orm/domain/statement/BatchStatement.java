package org.speedy.data.orm.domain.statement;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.speedy.common.util.ModelUtils;
import org.speedy.data.orm.exception.SpeedyDataAccessException;

/**
 * @Description 批量操作的sql命令
 * @Author chenguangxue
 * @CreateDate 2018/06/17 21:26
 */
public abstract class BatchStatement extends SqlStatement {

	protected Object[] batchObjects;

	/* 检查需要执行批量操作的对象是否合法 */
	/* 1、不能为空 */
	/* 2、数组中的对象数据类型必须相同 */
	private void checkBatchObjects() {
		if (batchObjects == null || batchObjects.length == 0) {
			throw new SpeedyDataAccessException("批量添加操作的目标对象集合为空");
		}

		long nullCount = Arrays.stream(batchObjects).filter(Objects::isNull).count();
		if (nullCount > 0) {
			throw new SpeedyDataAccessException("批量添加操作的目标对象集合中包含null");
		}

		this.targetClass = batchObjects[0].getClass();
		for (int i = 1; i < batchObjects.length; i++) {
			if (batchObjects[i].getClass() != targetClass) {
				throw new SpeedyDataAccessException("批量添加操作的目标对象类型不一致");
			}
		}
	}

	/* 获取操作对象的类型 */
	protected Class<?> targetClass;

	/* 获取操作对象的字段列表 */
	protected Field[] targetFields;

	protected List<Object> batchWhereFieldValues;
	protected List<String> batchPlaceholderList;
	protected String batchWhereString;

	/* 提取条件，在批量操作的情况下，条件就是主键列表 */
	protected void extractBatchWhere() {
		Arrays.stream(batchObjects).forEach(o -> {
			batchWhereFieldValues.add(ModelUtils.getPrimaryValue(o));
			batchPlaceholderList.add(getPlaceholder());
		});

		batchWhereString = batchPlaceholderList.stream().collect(Collectors.joining(",", "(", ")")).toString();
	}

	protected String primaryFieldName;

	/* 批量操作的对象需要通过构造方法进行传递 */
	protected BatchStatement(Object[] batchObjects) {
		this.batchObjects = batchObjects;
		checkBatchObjects();
		this.targetFields = targetClass.getDeclaredFields();
		this.primaryFieldName = ModelUtils.getPrimaryField(targetClass).getName();
		this.batchWhereFieldValues = new ArrayList<>(batchObjects.length);
		this.batchPlaceholderList = new ArrayList<>(batchObjects.length);
	}
}
