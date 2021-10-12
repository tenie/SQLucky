package net.tenie.fx.config;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 *  数据库厂家 和对应的启动字符串
 * @author tenie
 *
 */
public class DbVendor {
	
//	final public static String db2 = "db2";
//	final public static String mysql = "mysql";
//	final public static String mariadb = "mariadb"; 
//	final public static String h2 = "h2";
//	final public static String sqlite = "sqlite";
//	final public static String postgresql  = "postgresql";
	
	
	private static LinkedHashSet<String> keys = new LinkedHashSet<String>();
	private static Map<String, SqluckyDbRegister> data = new HashMap<String, SqluckyDbRegister>();

	static {
//		add( db2, "com.ibm.db2.jcc.DB2Driver");
//		add( mysql, "com.mysql.jdbc.Driver");
//		add( mariadb, "");
//		add( h2, "org.h2.Driver");
//		add( postgresql, "");
//		// 新版的jdbc 不需要手动注册驱动了
//		add(sqlite, "");
	}

	public static void add(String name, SqluckyDbRegister val) {
		keys.add(name);
		data.put(name, val);
	}

	public static LinkedHashSet<String> getAll() {
		return keys;
	}

	public static String getDriver(String name) { 
		return data.get(name).getDriver();
	}
	
	public static SqluckyDbRegister register(String name) { 
		return data.get(name);
	}
	

	public static void clear() {
		keys.clear();
		data.clear();
	}
	/**
	 * 
	 * @param Name
	 * @param driver 可以为空
	 */
	public static void registerDbConnection( SqluckyDbRegister db) {
		if(db == null) return;
		String name = db.getName();
		add( name, db);
//		add(sqlite, "");
	}
}
