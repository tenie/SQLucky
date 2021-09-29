package net.tenie.plugin.H2Connector.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.DbConnector;
import net.tenie.Sqlucky.sdk.db.ExportDDL;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DbSchemaPo;
import net.tenie.Sqlucky.sdk.po.TablePo;
import net.tenie.Sqlucky.sdk.utility.Dbinfo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;


/**
 * 
 * @author tenie
 *
 */
public class H2Connector extends DbConnector {
 
	
	public H2Connector(DBConnectorInfoPo connPo) {
		super(connPo);
		ExportSqlH2Imp ex = new ExportSqlH2Imp();
		getConnPo().setExportDDL( ex);
	} 
	 

	@Override
	public StringProperty DateToStringStringProperty(Date dv) {  
		String v = StrUtils.dateToStr(dv, ConfigVal.dateFormateL);
		StringProperty val = new SimpleStringProperty(v);
		
		return val;
	}

//	@Override
//	public void setExportDDL(ExportDDL exportDDL) {
//		 getConnPo().setExportDDL(exportDDL);		
//	}



	@Override
	public Map<String, DbSchemaPo> getSchemas() {
		var schemas = getConnPo().getSchemas();
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
				getHost(),
				getPort(),
				getUser(),
				getPassWord(),
				getDbVendor(),
				schema,
				getDbName()
				);
		var dbc = new H2Connector(val);
		
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
		String jdbcUrl = jdbcUrl = "jdbc:" + getDbVendor() + "://" + getHost() + ":" + getPort() + "/" + getDefaultSchema();
//		logger.info(jdbcUrl);

		return jdbcUrl;
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
