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
import net.tenie.Sqlucky.sdk.utility.Dbinfo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.lib.db.ExportDefaultImp;


/**
 * 
 * @author tenie
 *
 */
public class MyH2Connector extends DbConnector {
 
	
	public MyH2Connector(DBConnectorInfoPo connPo) {
		super(connPo);
		ExportDefaultImp ex = new ExportDefaultImp();
		getConnPo().setExportDDL( ex);
	} 
	 

	@Override
	public StringProperty DateToStringStringProperty(Date dv) {  
		String v = StrUtils.dateToStr(dv, ConfigVal.dateFormateL);
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
				getJdbcUrl()
				
				);
		var dbc = new MyH2Connector(val);
		
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
