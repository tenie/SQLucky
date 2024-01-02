package net.tenie.plugin.DB2Connector.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DbSchemaPo;


/**
 * 
 * @author tenie
 *
 */
public class Db2Connector extends SqluckyConnector {
 
	
	public Db2Connector(DBConnectorInfoPo connPo, SqluckyDbRegister dbRegister) {
		super(connPo, dbRegister);
		ExportSqlDB2Imp ex = new ExportSqlDB2Imp();
		getConnPo().setExportDDL( ex);
	} 
	 
 

	@Override
	public Map<String, DbSchemaPo> getSchemas() {
		Map<String, DbSchemaPo> schemas = getConnPo().getSchemas();
		try {
			if (schemas == null || schemas.isEmpty()) { 
//				if (DbVendor.sqlite.toUpperCase().equals(dbVendor.toUpperCase())) {
//					Map<String, DbSchemaPo> sch = new HashMap<>();
//					DbSchemaPo sp = new DbSchemaPo();
//					sp.setSchemaName(SQLITE_DATABASE);
//					sch.put(SQLITE_DATABASE, sp);
//					schemas = sch;
//				} else {
//					schemas = Dbinfo.fetchSchemasInfo(this);					
//				}
				schemas = fetchSchemasInfo();		
				getConnPo().setSchemas(schemas);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getConnPo().getSchemas();
	}


	@Override
	public String dbRootNodeName() { 
		return "Schemas";
	}


	@Override
	public String translateErrMsg(String errString) {
		String  str = Db2ErrorCode.translateErrMsg(errString);
		return str;
	}
	
	public  Map<String, DbSchemaPo> fetchSchemasInfo() {
		ResultSet rs = null;
		Map<String, DbSchemaPo> pos = new HashMap<String, DbSchemaPo>();
		Connection conn = getConn();
		try {
			DatabaseMetaData dmd = conn.getMetaData();
//			if (    DbVendor.mysql.toUpperCase().equals(dbVendor.toUpperCase())
//				||  DbVendor.mariadb.toUpperCase().equals(dbVendor.toUpperCase())
//					) {
//				rs = dmd.getCatalogs();
//			} else {
//				rs = dmd.getSchemas(); // 默认 db2
//			}
			rs = dmd.getSchemas(); // 默认 db2

			while (rs.next()) {
				DbSchemaPo po = new DbSchemaPo();
				String schema = rs.getString(1);
				po.setSchemaName(schema);
				pos.put(schema, po);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return pos;
	}


	@Override
	public SqluckyConnector instance( DBConnectorInfoPo val ) {
		var dbc = new Db2Connector(val , getDbRegister());
		return dbc;
	}


	@Override
	public String getRealDefaultSchema() {
		return getDefaultSchema();
	}


	
	@Override
	public  String templateJdbcUrlString(String hostFile, String port, String schema) {
		String jdbcUrlstr  = "jdbc:db2://" + hostFile + ":" + port + "/" + schema;
		return jdbcUrlstr;
	}



	
}
