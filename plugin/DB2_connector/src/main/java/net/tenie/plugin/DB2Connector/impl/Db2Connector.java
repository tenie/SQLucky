package net.tenie.plugin.DB2Connector.impl;

import java.sql.Connection;
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
import net.tenie.Sqlucky.sdk.po.DbSchemaPo;
import net.tenie.Sqlucky.sdk.po.TablePo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;


/**
 * 
 * @author tenie
 *
 */
public class Db2Connector extends DbConnector {

	public Db2Connector(String connName, String driver, String host, String port, String user, String passWord,
			String dbVendor, String defaultSchema, String dbName) {
		super(connName, driver, host, port, user, passWord, dbVendor, defaultSchema, dbName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, DbSchemaPo> getSchemas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringProperty DateToStringStringProperty(Date dv) {  
		String v = StrUtils.dateToStr(dv, ConfigVal.dateFormateL);
		StringProperty val = new SimpleStringProperty(v);
		return val;
	}
	 
}
