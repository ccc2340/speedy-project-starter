package org.speedy.common.exception;

import java.lang.reflect.Field;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/09 21:22
 */
public class NoFieldAccessMethodException extends RuntimeException {

	private Field field;
	private Class<?> clazz;
	private String type;

	public static NoFieldAccessMethodException getter(Class<?> clazz, Field field) {
		return new NoFieldAccessMethodException(clazz, field, "getter");
	}

	public static NoFieldAccessMethodException setter(Class<?> clazz, Field field) {
		return new NoFieldAccessMethodException(clazz, field, "setter");
	}

	private NoFieldAccessMethodException(Class<?> clazz, Field field, String type) {
		super(String.format("field [%s] in class [%s] no [%s] action ", field.getName(), clazz.getName(), type));
	}
}
