package net.tenie.plugin.mysqlConnector.impl;

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
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.Dbinfo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;


/**
 * 
 * @author tenie
 *
 */
public class MysqlConnector extends DbConnector {
 
	
	public MysqlConnector(DBConnectorInfoPo connPo) {
		super(connPo); 
		ExportSqlMySqlImp ex = new ExportSqlMySqlImp();
		getConnPo().setExportDDL( ex);
	} 
	 

	@Override
	public StringProperty DateToStringStringProperty(Object obj, int type ) {  
		Date dv = (Date) obj;
//		String v = StrUtils.dateToStr(dv, ConfigVal.dateFormateL);

		String v = CommonUtility.DateOrDateTimeToString(type, dv);
		StringProperty val = new SimpleStringProperty(v);
		
		return val;
	}

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
		String  str = MysqlErrorCode.translateErrMsg(errString);
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
		var dbc = new MysqlConnector(val);
		
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
			jdbcUrlstr  = "jdbc:mysql://" + getHostOrFile() + ":" + getPort() + "/" + getDefaultSchema();
			connPo.setJdbcUrl(jdbcUrlstr);
		}
		return  jdbcUrlstr;
	}


	@Override
	public Connection getConn() {
//		getConnPo().getConn()
		if (getConnPo().getConn() == null) {
//			logger.info(this.connPo.getDriver());
//			logger.info(getJdbcUrl());
//			logger.info(this.connPo.getUser());
//			logger.info(passWord);
//			if (DbVendor.sqlite.toUpperCase().equals(dbVendor.toUpperCase())) {
//				Dbinfo dbinfo = new Dbinfo(getJdbcUrl());
//				conn = dbinfo.getconn();
//			}else {
				Dbinfo dbinfo = new Dbinfo( getJdbcUrl(), getUser(), getPassWord());
				var conn = dbinfo.getconn();
				getConnPo().setConn(conn);
//			}			
		}

		return getConnPo().getConn();

	}
}
