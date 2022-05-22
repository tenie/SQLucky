package net.tenie.sdkImp;

import java.sql.SQLException;

import javafx.scene.control.Accordion;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.fx.component.MyTab;
import net.tenie.fx.config.DbVendor;
import net.tenie.fx.dao.DmlDdlDao;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.tools.IconGenerator; 

public class SqluckyComponent implements AppComponent { 

	@Override
	public void addTitledPane(TitledPane tp) {
		Accordion ad = ComponentGetter.infoAccordion;
		if(ad != null) {
			ad.getPanes().add(tp);
		} 
	}

	@Override
	public void addIconBySvg(String name, String svg) {
		IconGenerator.addSvgStr(name, svg);		
	}

	@Override
	public SqluckyTab sqluckyTab() { 
		return new MyTab(false);
	}
//
//	@Override
//	public SqluckyTab sqluckyTab(String TabName) { 
//		return new MyTab(TabName);
//	}

	@Override
	public SqluckyTab sqluckyTab(DocumentPo po) { 
		return new MyTab(po, false);
	}
	/**
	 * 获取图标
	 */
	@Override
	public Region getIconUnactive(String name) {
		return IconGenerator.svgImageUnactive( name); 
	}

	@Override
	public Region getIconDefActive(String name) {
		return IconGenerator.svgImageDefActive( name); 
	}

	@Override
	public void saveData(String name, String key, String value) {
		try {
			StringBuilder strb = new StringBuilder();
			strb.append(name);
			strb.append("-");
			strb.append(key); 
			H2Db.setConfigVal(H2Db.getConn(), strb.toString(), value);
		} finally {
			H2Db.closeConn();
		} 
	}
	public String fetchData(String name, String key) {
		String val = "";
		try {
			StringBuilder strb = new StringBuilder();
			strb.append(name);
			strb.append("-");
			strb.append(key); 
			val = H2Db.getConfigVal(H2Db.getConn(), strb.toString());
		} finally {
			H2Db.closeConn();
		} 
		
		return val;
	}

	@Override
	public void tabPaneRemoveSqluckyTab(SqluckyTab stb) {
		var myTabPane = ComponentGetter.mainTabPane; 
		Tab tb = (Tab) stb;
		if (myTabPane.getTabs().contains(tb)) {
			myTabPane.getTabs().remove(tb);
		}
		
	}

	@Override
	public void registerDBConnector(SqluckyDbRegister ctr) {
		DbVendor.registerDbConnection(ctr);
		
	}

	@Override
	public boolean execDML(String sql) {
		boolean succeed = false;
		var conn = H2Db.getConn();
		try {
			DmlDdlDao.execDML(conn, sql);
			succeed = true;
		} catch (SQLException e) { 
			e.printStackTrace();
		}
		
		return succeed; 
	}

}
