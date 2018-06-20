package org.speedy.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @Description 时间、日期处理工具类
 * @Author chenguangxue
 * @CreateDate 2018/06/12 09:07
 */
public class TimeUtils {

	// 默认日期时间格式
	private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss",
			Locale.CHINESE);

	// 默认日期格式
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINESE);

	/* 零点时刻 */
	public static final LocalTime ZERO_TIME = LocalTime.of(0, 0, 0);

	/* 最后时刻 */
	public static final LocalTime LAST_TIME = LocalTime.of(23, 59, 59);

	public static LocalDateTime parseDateTime(String content) {
		return LocalDateTime.parse(content, DATETIME_FORMATTER);
	}

	public static LocalDate parseDate(String content) {
		return LocalDate.parse(content, DATE_FORMATTER);
	}
}
