package org.speedy.data.orm.exception;

/**
 * @Description 数据访问模块异常抽象父类
 * @Author chenguangxue
 * @CreateDate 2018/06/10 22:10
 */
public class SpeedyDataAccessException extends RuntimeException {

    public SpeedyDataAccessException(String message) {
        super(message);
    }

    public SpeedyDataAccessException(Throwable e) {
        super(e);
    }

    public SpeedyDataAccessException(Throwable e, String message) {
        super(message, e);
    }
}
