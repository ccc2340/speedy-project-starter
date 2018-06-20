package org.speedy.common.exception;

/**
 * @Description
 * @Author chenguangxue
 * @CreateDate 2018/06/08 10:29
 */
public class SpeedyCommonException extends RuntimeException {

	public SpeedyCommonException() {
		super();
	}

	public SpeedyCommonException(String message) {
		super(message);
	}

	public SpeedyCommonException(Throwable e) {
		super(e);
	}

	public SpeedyCommonException(Throwable e, String message) {
		super(message, e);
	}
}
