package net.tenie.lib.db.h2;

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
import net.tenie.lib.db.ExportDefaultImp;


/**
 * 
 * @author tenie
 *
 */
public class SqluckySqliteConnector extends DbConnector {

	public SqluckySqliteConnector(DBConnectorInfoPo connPo) {
		super(connPo);
		ExportDefaultImp ex = new ExportDefaultImp();
		getConnPo().setExportDDL( ex);
	} 
	 
	public static SqluckySqliteConnector  createTmpConnector(String user, String password, String jdbcUrl) {
		DBConnectorInfoPo connPo = new DBConnectorInfoPo(
				"CONN_NAME",  
				"",
				"", 
				"", 
				user, 
				password, 
				"VENDOR",  
				"SCHEMA",  
				"DB_NAME",  
				jdbcUrl,
				false
		);
		SqluckySqliteConnector val = new SqluckySqliteConnector(connPo);
		return val;
	} 


	@Override
	public StringProperty DateToStringStringProperty(Object obj, int type) {  
//		Date dv = (Date) obj;
//		String v = StrUtils.dateToStr(dv, ConfigVal.dateFormateL);
//		StringProperty val = new SimpleStringProperty(v);
		StringProperty val = null;
		if(obj instanceof String) {
			val = new SimpleStringProperty((String) obj);
		}else if( obj instanceof Long) {
			Date date = new Date((long) obj);
//			String v = StrUtils.dateToStr(date, ConfigVal.dateFormateL);

			String v = CommonUtility.DateOrDateTimeToString(type, date);
			val = new SimpleStringProperty(v);
		}
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
		return "Schemas";
	}


	@Override
	public String translateErrMsg(String errString) { 
		return errString;
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
		var dbc = new SqluckySqliteConnector(val);
		
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
