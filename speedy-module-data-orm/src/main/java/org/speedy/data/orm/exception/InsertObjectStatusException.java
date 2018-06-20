package org.speedy.data.orm.exception;

/**
 * @Description 插入对象状态错误
 * @Author chenguangxue
 * @CreateDate 2018/06/10 23:20
 */
public class InsertObjectStatusException extends SpeedyDataAccessException {

	public InsertObjectStatusException(String message) {
		super(message);
	}

	/* 插入对象中包含主键 */
	public static InsertObjectStatusException hasPrimary(Object object) {
		return new InsertObjectStatusException("already has primary value");
	}
}
