package org.speedy.file.exception;

import org.speedy.common.exception.SpeedyCommonException;

/**
 * @Description 文件处理基础异常
 * @Author chenguangxue
 * @CreateDate 2018/7/3 23:15
 */
public class SpeedyFileException extends SpeedyCommonException {

    public SpeedyFileException(Throwable cause, String message) {
        super(cause, message);
    }

    public SpeedyFileException(String message) {
        super(message);
    }
}
