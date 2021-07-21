package net.tenie.fx.config;

import java.util.HashMap;
import java.util.Map;

public class CommonConst {
	public final static String TYPE_TABLE = "TABLE";
	public final static String TYPE_VIEW  = "VIEW";
	
	public final static String THEME_DARK    = "DARK";
	public final static String THEME_LIGHT   = "LIGHT";
	public final static String THEME_YELLOW  = "YELLOW";
	
	
	public static Map<String, Integer> PROCEDURE_TYPE = new HashMap<>();
	
	static {
		PROCEDURE_TYPE.put("String", java.sql.Types.VARCHAR);
		PROCEDURE_TYPE.put("Integer", java.sql.Types.BIGINT);
		PROCEDURE_TYPE.put("Decimal", java.sql.Types.DOUBLE);
		PROCEDURE_TYPE.put("Time",  java.sql.Types.TIMESTAMP);
	}
	
}
