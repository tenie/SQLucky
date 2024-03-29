package net.tenie.Sqlucky.sdk.utility;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ObjFormater {
	private static Logger logger = LogManager.getLogger(CommonUtils.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdfshort = new SimpleDateFormat("yyyy-MM-dd");
	private static DecimalFormat def = new DecimalFormat("0.####");
	private static DecimalFormat def2d = new DecimalFormat("0.######");

	public static String dateFormat(Date dt) {
		return sdf.format(dt);
	}

	public static String decimalFormat(double val) {
		return def2d.format(val);
	}

	public static String decimalFormat(float val) {
		return def.format(val);
	}

	public static String decimalFormat(String val) {
		return def.format(val);
	}

	public static Date parseDate(String str) throws Exception {
		return str != null && str.length() != 0 ? sdf.parse(str) : null;
	}

	public static Date parseShortDate(String str) throws Exception {
		return str != null && str.length() != 0 ? sdfshort.parse(str) : null;
	}

}