package net.tenie.plugin.H2Connector.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.tenie.Sqlucky.sdk.db.DbConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
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
public class H2FileConnector extends DbConnector {
 
	
	public H2FileConnector(DBConnectorInfoPo connPo, SqluckyDbRegister dbReg) {
		super(connPo, dbReg);
		ExportSqlH2Imp ex = new ExportSqlH2Imp();
		getConnPo().setExportDDL( ex); 
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
		return "Schemas";
	}


	@Override
	public String translateErrMsg(String errString) {
		String  str = H2ErrorCode.translateErrMsg(errString);
		return str;
	}
	
	public  Map<String, DbSchemaPo> fetchSchemasInfo() {
		ResultSet rs = null;
		Map<String, DbSchemaPo> pos = new HashMap<String, DbSchemaPo>();
		Connection conn = getConn();
		try {
			DatabaseMetaData dmd = conn.getMetaData();
			rs = dmd.getSchemas(); // 默认 db2

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
		var dbc = new H2FileConnector(val, getDbRegister());
		
		return dbc;
	}


	@Override
	public String getRealDefaultSchema() {
		return getDefaultSchema();
	}


	@Override
	public String getJdbcUrl() {		
//		return connPo.getJdbcUrl();
//		if (getJdbcUrl() == null || getJdbcUrl().length() == 0) {
//			if (this.isH2()) {
//				jdbcUrl = "jdbc:h2:" + host;
//				defaultSchema = "PUBLIC";
//			}else if (this.isSqlite()) {
//				jdbcUrl = "jdbc:sqlite:" + host;
//				defaultSchema = SQLITE_DATABASE;
//			} else if (this.isPostgresql()) {
//				jdbcUrl = "jdbc:" + dbVendor + "://" + host + ":" + port + "/" + dbName;
//			} else {
//				jdbcUrl = "jdbc:" + dbVendor + "://" + host + ":" + port + "/" + defaultSchema;
//				if (otherParameter != null && otherParameter.length() > 0) {
//					jdbcUrl += "?" + getOtherParameter();
//				}
//			}
		 

//		}
//		jdbc:h2:tcp://localhost:9092/~/config/ssfblog_db
		String jdbcUrlstr = connPo.getJdbcUrl();
		if(StrUtils.isNotNullOrEmpty(jdbcUrlstr)) {
			return jdbcUrlstr;
		}else {
			
			String fp = connPo.getHostOrFile();
			if(fp.endsWith(".mv.db"))
				fp= fp.substring(0, fp.lastIndexOf(".mv.db"));
			if(fp.endsWith(".trace.db"))
				fp= fp.substring(0, fp.lastIndexOf(".trace.db"));
			if(fp.endsWith(".db"))
				fp= fp.substring(0, fp.lastIndexOf(".db")); 
			
			jdbcUrlstr = "jdbc:h2:" +fp;
			connPo.setJdbcUrl(jdbcUrlstr);
		}
		
		 
		return  jdbcUrlstr;
	}


	@Override
	public Connection getConn() {
		if (getConnPo().getConn() == null) {
				Dbinfo dbinfo = new Dbinfo( getJdbcUrl(), getUser(), getPassWord());
				var conn = dbinfo.getconn();
				getConnPo().setConn(conn);
		}

		return getConnPo().getConn();

	}


	 
}
