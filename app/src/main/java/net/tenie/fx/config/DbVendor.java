package net.tenie.fx.config;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;

/**
 *  数据库厂家 和对应的启动字符串
 * @author tenie
 *
 */
public class DbVendor {
	
	
	private static LinkedHashSet<String> keys = new LinkedHashSet<String>();
	private static Map<String, SqluckyDbRegister> data = new HashMap<String, SqluckyDbRegister>();

//	static {
////		add( db2, "com.ibm.db2.jcc.DB2Driver");
////		add( mysql, "com.mysql.jdbc.Driver");
////		add( mariadb, "");
////		add( h2, "org.h2.Driver");
////		add( postgresql, "");
////		// 新版的jdbc 不需要手动注册驱动了
////		add(sqlite, "");
//	}

	public static void add(String name, SqluckyDbRegister val) {
		var tmpName = name.toUpperCase();
		keys.add(tmpName);
		data.put(tmpName, val);
	}

	public static LinkedHashSet<String> getAll() {
		return keys;
	}

	public static String getDriver(String name) { 
		
		return data.get(name.toUpperCase()).getDriver();
	}
	
	// 获取注册的 db 对象
	public static SqluckyDbRegister register(String name) {
		for(var key :data.keySet()) {
			if(key.toUpperCase().equals(name.toUpperCase())) {
				return data.get(key);
			}
		}
		return null;
	}
	

	public static void clear() {
		keys.clear();
		data.clear();
	}

	public static void registerDbConnection( SqluckyDbRegister db) {
		if(db == null) {
            return;
        }
		String name = db.getName();
		add( name, db);
	}
}
