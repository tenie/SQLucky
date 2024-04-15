package net.tenie.lib.db.h2;

import java.sql.Connection;
import java.util.*;
import net.tenie.Sqlucky.sdk.db.DBTools;
import net.tenie.Sqlucky.sdk.db.PoDao;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.fx.factory.ServiceLoad;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateScript {

	private static Logger logger = LogManager.getLogger(ServiceLoad.class);
	private static Map<String, String> sqlMap = new HashMap<>();
	static {
		sqlMap.put("PLUGIN_INFO添加file_name", "ALTER TABLE PLUGIN_INFO ADD    FILE_NAME varchar(200)");
		sqlMap.put("PLUGIN_INFO添加file_name2", "ALTER TABLE PLUGIN_INFO ADD    FILE_NAME2 varchar(200)");
		sqlMap.put("APP_CONFIG 添加 PLUGIN_NAME", "ALTER TABLE APP_CONFIG ADD  PLUGIN_NAME VARCHAR(200)");
		sqlMap.put("add key press : Hide/Show Data View Panel ", "insert into KEYS_BINDING (ACTION_NAME, BINDING) values('Hide/Show Data View Panel', 'Ctrl + esc');");
		sqlMap.put("add key press : Hide/Show DB Info Panel ", "insert into KEYS_BINDING (ACTION_NAME, BINDING) values('Hide/Show DB Info Panel', 'Ctrl + F1');");


	}

	/**
	 * 添加脚本到数据库
	 */
	public static void insertNewSQL() {
		if (!sqlMap.isEmpty()) {
			var keys = sqlMap.keySet();

			Connection conn = SqluckyAppDB.getConn();
			try {
				for (String key : keys) {
					SqluckyAppendSqlPO po = new SqluckyAppendSqlPO();
					po.setRemark(key);

					List<SqluckyAppendSqlPO> list = PoDao.select(conn, po);
					if(list.isEmpty()){
						po.setSqlVal(sqlMap.get(key));
						PoDao.insert(conn, po);
					}

				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}finally {
				SqluckyAppDB.closeConn(conn);
			}
		}
	}

	/**
	 * 找到未执行的sql, 执行
	 */
	public static void executeAppendSql(){
		Connection conn = SqluckyAppDB.getConn();
		try {
				SqluckyAppendSqlPO po = new SqluckyAppendSqlPO();
				po.setIsExecute(0);


				List<SqluckyAppendSqlPO> list = PoDao.select(conn, po);
				logger.info("executeAppendSql() :: list.size() = ", list.size());

				if(list != null && ! list.isEmpty()){
					for(var sqlpo : list ){
						String sql = sqlpo.getSqlVal();
						DBTools.execDDLNoErr(conn, sql);
						po.setId(sqlpo.getId());

						SqluckyAppendSqlPO valpo = new SqluckyAppendSqlPO();
						valpo.setIsExecute(1);
						PoDao.update(conn, po, valpo);
					}
				}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
	}

}
