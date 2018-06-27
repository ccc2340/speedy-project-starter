package org.speedy.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Locale;

/**
 * @Description 时间、日期处理工具类
 * @Author chenguangxue
 * @CreateDate 2018/06/12 09:07
 */
public class TimeUtils {

    /* 这3个是默认的日期、时间格式 */
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String DATETIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;

    /* 默认的时区 */
    private static final Locale DEFAULT_LOCALE = Locale.CHINESE;

    /* 默认的转换器 */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT, DEFAULT_LOCALE);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT, DEFAULT_LOCALE);

    /* 2个特殊的时刻 */
    public static final LocalTime ZERO_TIME = LocalTime.of(0, 0, 0);
    public static final LocalTime LAST_TIME = LocalTime.of(23, 59, 59);

    /* 将字符串转换为日期 */
    public static LocalDate parseDate(String content) {
        return LocalDate.parse(content, DATE_FORMATTER);
    }

    /* 将字符串转换为日期 */
    public static LocalDate parseDate(String content, String format) {
        if (DATE_FORMAT.equals(format)) {
            return parseDate(content);
        } else {
            return LocalDate.parse(content, buildFormatter(format));
        }
    }

    private static DateTimeFormatter buildFormatter(String format) {
        return DateTimeFormatter.ofPattern(format, DEFAULT_LOCALE);
    }

    /* 将字符串转换为日期时间 */
    public static LocalDateTime parseDateTime(String content) {
        return LocalDateTime.parse(content, DATETIME_FORMATTER);
    }

    /* 将字符串转换为日期时间 */
    public static LocalDateTime parseDateTime(String content, String format) {
        if (DATETIME_FORMAT.equals(format)) {
            return parseDateTime(content);
        } else {
            return LocalDateTime.parse(content, buildFormatter(format));
        }
    }

    public static String formatString(LocalDate date, String format) {
        if (DATE_FORMAT.equals(format)) {
            return DATE_FORMATTER.format(date);
        } else {
            return buildFormatter(format).format(date);
        }
    }

    public static String formatString(LocalDateTime datetime, String format) {
        if (DATETIME_FORMAT.equals(format)) {
            return DATETIME_FORMATTER.format(datetime);
        } else {
            return buildFormatter(format).format(datetime);
        }
    }
}
