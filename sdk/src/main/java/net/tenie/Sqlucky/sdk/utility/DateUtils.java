package net.tenie.Sqlucky.sdk.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import net.tenie.Sqlucky.sdk.config.ConfigVal;

/**
 * 时间转换
 * 
 * @author tenie
 *
 */
public class DateUtils {

	public static Date asDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date asDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate asLocalDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime asLocalDateTime(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static String localDateTimeToStr(LocalDateTime localDateTime) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//	          LocalDateTime localDateTime = LocalDateTime.now();
		String dateStr = localDateTime.format(fmt);
		System.out.println(dateStr);
		return dateStr;
	}

	public static LocalDateTime strToLocalDateTime(String dateStr) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//	         String dateStr = "2021-08-19 15:11:30";
		LocalDateTime date2 = LocalDateTime.parse(dateStr, fmt);
		System.out.println(date2);

		return date2;
	}

	public static String dateToStrL(Date d) {
		return StrUtils.sdf_DateTime.format(d);
	}

	public static String dateToStr(Date d, String formate) {
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		return sdf.format(d);
	}

	// 返回一个时间字符串, 可用于文件名的一部分
	public static String currentDateTimeToFileNameStr() {
		return dateToStr(new Date(), StrUtils.dateTimeForFileName);
	}

	public static Date StrToDate(String str, String formate) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(formate);
			return new Date(sdf.parse(str).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	// 日期类型或时间类型转换为字符串
	public static String DateOrDateTimeToString(int type, Date dv) {
		String val = "";
		if (CommonUtils.isDate(type)) {
			val = dateToStr(dv, ConfigVal.dateFormateS);
		} else if (CommonUtils.isDateTime(type)) {
			val = dateToStr(dv, ConfigVal.dateFormateL);
		}

		return val;
	}

	/*
	 * 数据库时间类型字段转换为字符串
	 */
	public static String DbDateTimeToString(Object obj, int sqlFiledtype) {

		String val = null;
		if (obj instanceof LocalDateTime ldt) {
			Date dv = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
			val = DateUtils.DateOrDateTimeToString(sqlFiledtype, dv);
		} else if (obj instanceof Date dv) {
			val = DateUtils.DateOrDateTimeToString(sqlFiledtype, dv);
		} else if (obj instanceof String stringVal) {
			val = stringVal;
		} else if (obj instanceof Long longVal) {
			Date date = new Date( longVal );
			val = DateUtils.dateToStr(date, ConfigVal.dateFormateL);
		}

		return val;
	}

}
