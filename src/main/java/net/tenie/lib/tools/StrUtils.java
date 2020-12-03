package net.tenie.lib.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*   @author tenie */
public class StrUtils {
	public final static SimpleDateFormat sdf_s = new SimpleDateFormat("yyyy-MM-dd");
	public final static SimpleDateFormat sdf_l = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static String EMPTY_STRING = "";
	public final static String BLANK_SPRING_STRING = " ";
	public static final char CHAR_TILDE = '~';

	// 驼峰命名转下划线
	public static String CamelCaseUnderline(String str) {
		StringBuilder rs = new StringBuilder();

		rs.append(str.charAt(0));
		for (int i = 1; i < str.length(); i++) {
			char strChar = str.charAt(i);
			if (strChar >= 'A' && strChar <= 'Z') {
				rs.append("_");
				rs.append(strChar);
			} else {
				rs.append(strChar);
			}
		}
		return rs.toString();
	}
public static void main(String[] args) {
	String s = CamelCaseUnderline("balancePartAmount");
	System.out.println(s);
}
	// 下划线 轉 驼峰命名
	public static String underlineCaseCamel(String str) {
		StringBuilder rs = new StringBuilder();
		str = str.toLowerCase();
		boolean tf = false;
		for (int i = 0; i < str.length(); i++) {
			char strChar = str.charAt(i);
			if (strChar == '_') {
				tf = true;
			} else {
				if (tf) {
					if (strChar >= 'A' && strChar <= 'Z') {
						strChar += 26;
					} else if (strChar >= 'a' && strChar <= 'z') {
						strChar -= 32;
					}
				}
				rs.append(strChar);
				tf = false;
			}
		}
		return rs.toString();
	}

	// 去除字符串中的非数字部分
	public static String clearString(String str) {
		StringBuffer val = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c >= '0' && c <= '9') {
				val.append(c);
			}
		}
		return val.toString();
	}

	// 去除字符串中的非数字部分
	public static boolean isNumeric(String str) {
		return !str.isEmpty() && str.matches("-?\\d+(\\.\\d+)?");
	}

	// 去除字符串中的非数字部分, 包含.
	public static String clearIpStr(String str) {
		StringBuffer val = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if ((c >= '0' && c <= '9') || c == '.') {
				val.append(c);
			}
		}
		return val.toString();
	}

	public static UUID getRandomUUID() {
		return UUID.randomUUID();
	}

	public static boolean beginWith(String str, String tag) {
		boolean tf = false;
		if (str != null && tag != null && str.length() >= tag.length()) {
			if (str.subSequence(0, tag.length()).equals(tag)) {
				tf = true;
			}
		}
		return tf;
	}

	public static boolean beginWithNotSensitive(String str, String tag) {
		boolean tf = false;
		if (str != null && tag != null && str.length() > tag.length()) {
			if (str.subSequence(0, tag.length()).toString().toUpperCase().equals(tag.toUpperCase())) {
				tf = true;
			}
		}
		return tf;
	}

	// 去除多余空白字符， 转化为一个“ ”
	public static String clearBlank(String str) {
		String temp = str.replaceAll("\r", "");
		temp = temp.replaceAll("\n", " ");
		temp = temp.replaceAll("\t", " ");
		temp = temp.trim();
		String rs = "";
		if (temp.indexOf(' ') > 0) {
			String[] arr = temp.split(" ");
			for (int i = 0; i < arr.length; i++) {
				String val = arr[i].trim();
				if (val.length() > 0) {
					rs += val + " ";
				}
			}
		}

		return rs;
	}

	// 去除注释
	public static String trimComment(String sql, String symbol) {
		String str = sql.replaceAll(symbol, "\n" + symbol);
		if (str.contains("\r")) {
			str = str.replace("\r", "");
		}

		String[] sa = str.split("\n");
		String nstr = "";
		if (sa != null && sa.length > 1) {
			for (int i = 0; i < sa.length; i++) {
				String temp = sa[i];
				if (!beginWith(temp, symbol)) {
					nstr += temp + "\n";
				}
			}
		}
		if ("".equals(nstr)) {
			nstr = sql;
		}
		return nstr.trim();
	}

	/**
	 * check if null or empty string
	 */
	public static boolean isNullOrEmpty(Object obj) {
		return (null == obj || EMPTY_STRING.equals(obj));
	}

	public static boolean isNotNullOrEmpty(Object obj) {
		return (!isNullOrEmpty(obj));
	}

	/**
	 * 检查是否相等
	 */
	public static boolean isEquals(Object obj, String expectValue) {
		if (isNullOrEmpty(obj) || isNullOrEmpty(expectValue)) {
			return false;
		}
		return expectValue.equals(obj.toString());
	}

	/**
	 * 检查是否相等（忽略大小写）
	 */
	public static boolean isEqualsNoCasetive(Object obj, String expectValue) {
		if (isNullOrEmpty(obj) || isNullOrEmpty(expectValue)) {
			return false;
		}
		return expectValue.toUpperCase().equals(obj.toString().toUpperCase());
	}

	/**
	 * 判断是否是符合的正则表达示
	 * 
	 */
	public static boolean isMatcherPatten(String pattenStr, String extensions) {
		Pattern patten = Pattern.compile(pattenStr);
		Matcher matcher = patten.matcher(extensions);
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
	}

	public static String dateToStrL(Date d) {
		return sdf_l.format(d);
	}

	public static String dateToStr(Date d, String formate) {
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		return sdf.format(d);
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

	// 字符串转时间
	public static Date StrToDate_L(String str) {
		try {
			return new Date(sdf_l.parse(str).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	// 字符串转时间
	public static java.sql.Date StrToDate_S(String str) {

		try {
			return new java.sql.Date(sdf_s.parse(str).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

	}

	public static String StrPlitJoin(String str, String regex, String delimiter) {
		String rs = "";
		String[] temp = str.split(regex);
		if (temp != null && temp.length > 0) {
			int len = temp.length;
			for (int i = 0; i < len; i++) {
				String s = temp[i];
				if (s.length() > 0 && s.trim().length() > 0) {
					rs += s + delimiter;
				}
			}
		}
		if (rs.length() > 0)
			rs = rs.substring(0, rs.length() - 1);
		return rs;
	}
}
