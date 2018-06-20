package org.speedy.data.orm.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;

import org.speedy.common.constant.RegularExpression;
import org.speedy.common.exception.SpeedyCommonException;
import org.springframework.stereotype.Component;

/**
 * @Description 数据转换器:将数据库值转换为Java值
 * @Author chenguangxue
 * @CreateDate 2018/06/12 09:52
 */
@Component
public class DatabaseValueConverter {

	public Object convert(String dbValue, Field field) {
		if (dbValue == null) {
			return null;
		}

		Class<?> convertType = field.getType();

		if (convertType.isEnum()) {
			return dealEnum(convertType, dbValue);
		} else if (Number.class.isAssignableFrom(convertType)) {
			return dealNumber(convertType, dbValue);
		} else if (convertType == Boolean.class || convertType == boolean.class) {
			return dealBoolean(dbValue);
		} else if (java.util.Date.class.isAssignableFrom(convertType) || Temporal.class.isAssignableFrom(convertType)) {
			return dealDateTime(convertType, dbValue);
		}

		// 如果是没有特殊处理的类型，则返回数据库实际值
		return dbValue;
	}

	// 处理enum类型
	private Object dealEnum(Class<?> enumClass, String dbValue) {
		return Enum.valueOf((Class<Enum>) enumClass, dbValue);
	}

	// 处理number类型
	private Object dealNumber(Class<?> numberClass, String dbValue) {
		if (numberClass == Integer.class) {
			return Double.valueOf(dbValue).intValue();
		}
		if (numberClass == Double.class) {
			return Double.valueOf(dbValue);
		}
		if (numberClass == Float.class) {
			return Float.valueOf(dbValue);
		}
		if (numberClass == Long.class) {
			return Double.valueOf(dbValue).longValue();
		}
		if (numberClass == BigDecimal.class) {
			return BigDecimal.valueOf(Double.valueOf(dbValue));
		}

		throw new SpeedyCommonException("do not support number type " + numberClass);
	}

	// 处理boolean类型
	private boolean dealBoolean(String dbValue) {
		return "true".equalsIgnoreCase(dbValue) || "1".equals(dbValue);
	}

	// 处理日期、时间类型
	private Object dealDateTime(Class<?> datetimeType, String dbValue) {
		if (dbValue.startsWith("0000-00-00 00:00:00")) {
			return null;
		}

		if (RegularExpression.DATE.isMatch(dbValue)) {
			dbValue = dbValue + " " + "00:00:00";
		}

		Timestamp timestamp = Timestamp.valueOf(dbValue);
		Instant instant = Instant.ofEpochMilli(timestamp.getTime());
		LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		if (datetimeType == LocalDateTime.class) {
			return dateTime;
		} else if (datetimeType == LocalDate.class) {
			return dateTime.toLocalDate();
		} else if (datetimeType == LocalTime.class) {
			return dateTime.toLocalTime();
		} else if (datetimeType == Timestamp.class) {
			return timestamp;
		}

		return null;
	}

}
