package net.tenie.plugin.PostgresqlConnector.impl;

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
public class PostgresqlConnector extends SqluckyConnector {
 
	
	public PostgresqlConnector(DBConnectorInfoPo connPo, SqluckyDbRegister dbReg) {
		super(connPo, dbReg); 
		ExportSqlPostgresqlImp ex = new ExportSqlPostgresqlImp();
		getConnPo().setExportDDL( ex);
	} 
	 

//	@Override
//	public StringProperty DateToStringStringProperty(Object obj) {  
//		Date dv = (Date) obj;
//		String v = StrUtils.dateToStr(dv, ConfigVal.dateFormateL);
//		StringProperty val = new SimpleStringProperty(v);
//		
//		return val;
//	}

	@Override
	public Map<String, DbSchemaPo> getSchemas() {
		var schemas = getConnPo().getSchemas();
		try {
			if (schemas == null || schemas.isEmpty()) { 
				schemas = fetchSchemasInfo();		
				getConnPo().setSchemas(schemas);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return schemas;
	}


	@Override
	public String dbRootNodeName() { 
		return "Databases";
	}


	@Override
	public String translateErrMsg(String errString) {
		String  str = PostgresqlErrorCode.translateErrMsg(errString);
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
			rs = dmd.getCatalogs();
//			rs = dmd.getSchemas(); // 默认 db2

			while (rs.next()) {
				DbSchemaPo po = new DbSchemaPo();
				String schema = rs.getString(1);
//				logger.info("fetchSchemasInfo(); schema=" + schema);
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
	public SqluckyConnector  instance(DBConnectorInfoPo val) {
		var dbc = new PostgresqlConnector(val, getDbRegister());
		return dbc;
	}


	@Override
	public String getRealDefaultSchema() {
		return getDefaultSchema();
	}


//	@Override
//	public String getJdbcUrl() {
//		String jdbcUrlstr = getDBConnectorInfoPo().getJdbcUrl();
//		if(StrUtils.isNotNullOrEmpty(jdbcUrlstr)) {
//			return jdbcUrlstr;
//		}else {
//			jdbcUrlstr  = "jdbc:postgresql://" + getHostOrFile() + ":" + getPort() + "/" + getDefaultSchema();
//			getDBConnectorInfoPo().setJdbcUrl(jdbcUrlstr);
//		}
//		return  jdbcUrlstr;
//	}


//	@Override
//	public Connection getConn() {
//		if (getConnPo().getConn() == null) {
//			Dbinfo dbinfo = new Dbinfo(getJdbcUrl(), getUser(), getPassWord());
//			var conn = dbinfo.getconn();
//			getConnPo().setConn(conn);
//		}
//
//		return getConnPo().getConn();
//	}


	@Override
	public String templateJdbcUrlString(String hostFile, String port, String schema) {
		String	jdbcUrlstr  = "jdbc:postgresql://" + hostFile + ":" + port + "/" + schema;
//		String jdbcUrlstr  = "jdbc:db2://" + getHostOrFile() + ":" + getPort() + "/" + getDefaultSchema();
		return jdbcUrlstr;
	}
}
