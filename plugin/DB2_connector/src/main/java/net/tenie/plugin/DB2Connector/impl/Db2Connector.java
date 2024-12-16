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
import net.tenie.Sqlucky.sdk.utility.StrUtils;


/**
 * 
 * @author tenie
 *
 */
public class Db2Connector extends SqluckyConnector {
 
	
	public Db2Connector(DBConnectorInfoPo connPo, SqluckyDbRegister dbRegister) {
		super(connPo, dbRegister);
		ExportSqlDB2Imp ex = new ExportSqlDB2Imp();
		getDbConnectorInfoPo().setExportDDL( ex);
	} 
	 
 

	@Override
	public Map<String, DbSchemaPo> getSchemas() {
		Map<String, DbSchemaPo> schemas = getDbConnectorInfoPo().getSchemas();
		try {
			if (schemas == null || schemas.isEmpty()) {
				schemas = fetchSchemasInfo();		
				getDbConnectorInfoPo().setSchemas(schemas);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getDbConnectorInfoPo().getSchemas();
	}


	@Override
	public String dbRootNodeName() { 
		return "Schemas";
	}


	@Override
	public String translateErrMsg(String errString) {
        return Db2ErrorCode.translateErrMsg(errString);
	}
	
	public  Map<String, DbSchemaPo> fetchSchemasInfo() {
		ResultSet rs = null;
		Map<String, DbSchemaPo> pos = new HashMap<>();
		Connection conn = getConn();
		try {
			DatabaseMetaData dmd = conn.getMetaData();
			rs = dmd.getSchemas();

			while (rs.next()) {
				DbSchemaPo po = new DbSchemaPo();
				String schema = rs.getString(1);
				po.setSchemaName(schema);
				pos.put(schema, po);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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

        return new Db2Connector(val , getDbRegister());
	}


	@Override
	public String getRealDefaultSchema() {
		return getDefaultSchema();
	}


	@Override
	public String getJdbcUrl() {
		String jdbcUrlstr = dbConnectorInfoPo.getJdbcUrl();
		if(StrUtils.isNotNullOrEmpty(jdbcUrlstr)) {
			return jdbcUrlstr;
		}else {
			jdbcUrlstr  = "jdbc:db2://" + getHostOrFile() + ":" + getPort() + "/" + getDefaultSchema();
			dbConnectorInfoPo.setJdbcUrl(jdbcUrlstr);
		}
		return  jdbcUrlstr;
	}


}
