package net.tenie.Sqlucky.sdk;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.DocumentPo;

public interface AppComponent {
	
	void addTitledPane(TitledPane tp);
	void addIconBySvg(String name, String svg);
	SqluckyTab sqluckyTab();
//	SqluckyTab sqluckyTab(String TabName);
	SqluckyTab sqluckyTab(DocumentPo po);
	void tabPaneRemoveSqluckyTab(SqluckyTab stb);
	
	public Region getIconUnactive(String name);
	public Region getIconDefActive(String name);
	
	public void saveData(String name, String key, String value);
	public String fetchData(String name, String key);
	
	
	//DB DB2Connector
	public void registerDBConnector(SqluckyDbRegister ctr);
	
}
