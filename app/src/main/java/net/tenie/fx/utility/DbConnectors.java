package net.tenie.fx.utility;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*   @author tenie */
/**
 * 全局保存数据库连接
 *
 */
public class DbConnectors {
	private static Map<String, Connection> connMap;

	public static void addConn(String name, Connection conn) {
		if (connMap == null) {
			connMap = new HashMap<String, Connection>();
		}
		connMap.put(name, conn);

	}

	/**
	 * 
	 * @param name
	 * @return maybe return null
	 */
	public static Connection getConn(String name) {
		if (connMap != null) {
			return connMap.get(name);
		}
		return null;
	}

	/**
	 * 
	 * @return all connectors name set
	 */
	public static Set<String> getAllName() {
		if (connMap != null) {
			return connMap.keySet();
		}
		return null;
	}
}
