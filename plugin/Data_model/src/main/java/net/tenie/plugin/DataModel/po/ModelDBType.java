package net.tenie.plugin.DataModel.po;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**
 * 生成模型数据库的类型
 * @author tenie
 *
 */
public class ModelDBType {
	public static final String mySQL = "mySQL";
	
	
	static ObservableList<String> all = FXCollections.observableArrayList();
	public static ObservableList<String> allModeFileType() {
		if(all.size() == 0) {
			all.add(mySQL);
//			all.add(PDM);
//			all.add(CDM);
		}
		
		return all;
	}
	
}
