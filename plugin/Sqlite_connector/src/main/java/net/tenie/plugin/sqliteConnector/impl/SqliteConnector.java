package net.tenie.plugin.sqliteConnector.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.DbConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DbSchemaPo;
import net.tenie.Sqlucky.sdk.utility.Dbinfo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;


/**
 * 
 * @author tenie
 *
 */
public class SqliteConnector extends DbConnector {
 
	
	public SqliteConnector(DBConnectorInfoPo connPo) {
		super(connPo);
		ExportSqlSqliteImp ex = new ExportSqlSqliteImp();
		getConnPo().setExportDDL( ex);
	} 
	 

	@Override
	public StringProperty DateToStringStringProperty(Object obj) {  
//		String v = StrUtils.dateToStr(dv, ConfigVal.dateFormateL);
//		StringProperty val = new SimpleStringProperty((String) dv);
		StringProperty val = null;
		if(obj instanceof String) {
			val = new SimpleStringProperty((String) obj);
		}else if( obj instanceof Long) {
			Date date = new Date((long) obj);
			String v = StrUtils.dateToStr(date, ConfigVal.dateFormateL);
			val = new SimpleStringProperty(v);
		}
		return val;
	}

	@Override
	public Map<String, DbSchemaPo> getSchemas() {
		var schemas = getConnPo().getSchemas();
		try {
			if (schemas == null || schemas.isEmpty()) { 
					Map<String, DbSchemaPo> sch = new HashMap<>();
					DbSchemaPo sp = new DbSchemaPo();
					sp.setSchemaName(SqliteRegister.instanceName);
					sch.put(SqliteRegister.instanceName, sp);
					schemas = sch;
					getConnPo().setSchemas(schemas);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return schemas;
	}


	@Override
	public String dbRootNodeName() { 
		return "Schemas";
	}


	@Override
	public String translateErrMsg(String errString) {
		String  str = SqliteErrorCode.translateErrMsg(errString);
		return str;
	}
	
//	public  Map<String, DbSchemaPo> fetchSchemasInfo() {
//		ResultSet rs = null;
//		Map<String, DbSchemaPo> pos = new HashMap<String, DbSchemaPo>();
//		Connection conn = getConn();
//		try {
//			DatabaseMetaData dmd = conn.getMetaData();
////			if (    DbVendor.mysql.toUpperCase().equals(dbVendor.toUpperCase())
////				||  DbVendor.mariadb.toUpperCase().equals(dbVendor.toUpperCase())
////					) {
////				rs = dmd.getCatalogs();
////			} else {
////				rs = dmd.getSchemas(); // 默认 db2
////			}
//			rs = dmd.getSchemas(); // 默认 db2
//
//			while (rs.next()) {
//				DbSchemaPo po = new DbSchemaPo();
//				String schema = rs.getString(1);
////				logger.info("fetchSchemasInfo(); schema=" + schema);
//				po.setSchemaName(schema);
//				pos.put(schema, po);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (rs != null)
//				try {
//					rs.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//		}
//
//		return pos;
//	}


	@Override
	public SqluckyConnector copyObj( String schema) {
		DBConnectorInfoPo val = new DBConnectorInfoPo(  
				getConnName()+"Copy",
				getDriver(),
				getHostOrFile(),
				getPort(),
				getUser(),
				getPassWord(),
				getDbVendor(),
				schema,
				getDbName(),
				getJdbcUrl(),
				getAutoConnect()
				);
		var dbc = new SqliteConnector(val);
		
		return dbc;
	}


	@Override
	public String getRealDefaultSchema() {
		return getDefaultSchema();
	}


	@Override
	public String getJdbcUrl() {
		String jdbcUrlstr = connPo.getJdbcUrl();
		if(StrUtils.isNotNullOrEmpty(jdbcUrlstr)) {
			return jdbcUrlstr;
		}else { 
			jdbcUrlstr  = "jdbc:sqlite:" + getHostOrFile(); 
			connPo.setJdbcUrl(jdbcUrlstr);
		}
		return  jdbcUrlstr;
	}

	/**
	 * if (getConnPo().getConn() == null) {
				Dbinfo dbinfo = new Dbinfo( getJdbcUrl(), getUser(), getPassWord());
				var conn = dbinfo.getconn();
				getConnPo().setConn(conn);
		}

		return getConnPo().getConn();
	 */

	@Override
	public Connection getConn() { 
		if (getConnPo().getConn() == null) {
			var hasUser = StrUtils.isNotNullOrEmpty(getUser());
			var hasPw = StrUtils.isNotNullOrEmpty(getPassWord());
			if(hasUser && hasPw) {
				Dbinfo dbinfo = new Dbinfo( getJdbcUrl(), getUser(), getPassWord());
				var conn = dbinfo.getconn();
				getConnPo().setConn(conn);
			}else {
				var conn = Dbinfo.getConnByJdbc( getJdbcUrl());
				getConnPo().setConn(conn);		 
			}
				
		}

		return getConnPo().getConn();

	}

}
