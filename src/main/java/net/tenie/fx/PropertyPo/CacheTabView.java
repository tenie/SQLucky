package net.tenie.fx.PropertyPo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import net.tenie.fx.component.container.DataViewTab;
import net.tenie.lib.tools.StrUtils;


/*   @author tenie */
public class CacheTabView {
	private static Logger logger = LogManager.getLogger(CacheTabView.class);
	private static Map<String, DataViewTab> tabViews = new HashMap<>();
	
	public static void  addDataViewTab(DataViewTab tb ,String id) {
		tabViews.put(id, tb);
	}
	
	public static Map<String, DataViewTab> getTabViews() {
		return tabViews;
	}
	public static void setTabViews(Map<String, DataViewTab> tabViews) {
		CacheTabView.tabViews = tabViews;
	}
	
	public static DataViewTab   getDataViewTab(String id) {
		return tabViews.get(id); 
	}
	
	// 获取缓存key
	public static Set<String> getKey(){
		return tabViews.keySet();
	}
	
	
	// 获取字段
	public static ObservableList<SqlFieldPo>  getFields(String id) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) {
			return dvt.getColss();
		}
		return null;
	}
	
	// 获取tableName
	public static String  getTableName(String id) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) {
			return dvt.getTabName();
		}
		return "";
	}
	
	//	String connName
	public static String  getConnName(String id) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) {
			return dvt.getConnName();
		}
		return "";
	}
//	Connection  dbconns 
	public static Connection  getDbConn(String id) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) {
			return dvt.getDbconns();
		}
		return null;
	}
//	 DbConnectionPo dpo
	public static DbConnectionPo  getDbConnection(String id) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) {
			return dvt.getDbConnection();
		}
		return null;
	}
	
	
//	getTab
	public static Tab  getTab(String id) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) {
			return dvt.getTab();
		}
		return null;
	}
	//getSelectSQl
	public static String  getSelectSQl(String id) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) {
			return dvt.getSqlStr();
		}
		return "";
	}
	
	
	
	// 获取某一行数据
	public static ObservableList<StringProperty>  getRowValues(String id, int idx) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) { 
			return dvt.getRawData().get(idx);
		}
		return null;
	}
	  
	// 获取所有数据
	public static ObservableList<ObservableList<StringProperty>>  getTabData(String id) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) { 
			return dvt.getRawData();
		}
		return null;
	}
	
	// 添加一行新数据
	public static void addDataNewLine(String id, int rowNo, ObservableList<StringProperty> vals) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) { 
			Map<String, ObservableList<StringProperty>> map = dvt.getNewLineDate();
			map.put(rowNo+"", vals);
		}
	}
	
	public static void addDataOldVal(String id, int rowNo, ObservableList<StringProperty> vals) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) { 
			Map<String, ObservableList<StringProperty>> map = dvt.getOldval();
			map.put(rowNo+"", vals);
		}
	}
	
	public static void addData(String id, int rowNo, ObservableList<StringProperty> newDate, ObservableList<StringProperty> oldDate) {
		if (!exist(id, rowNo)) { 
			addDataOldVal(id, rowNo, oldDate);
		} 
		addDataNewLine(id, rowNo, newDate);

	}
	public static void addData(String id, int rowNo, ObservableList<StringProperty> newDate) {
		addDataNewLine(id, rowNo, newDate);
	}
	
	public static Map<String, ObservableList<StringProperty>> getModifyData(String id) {
		return   getNewLineDate(id) ;
	}
	
	public static  ObservableList<StringProperty> getold(String id, String row) {
		var ov = getOldval(id);
		return ov.get(row); 
	}
	
	public static void rmUpdateData(String id) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) {
			dvt.getNewLineDate().clear();
			dvt.getOldval().clear();
		} 
	}
	public static void rmAppendData(String id) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) {
			dvt.getAppendData().clear();
			
		} 
	}

	
	public static List<ObservableList<StringProperty>> getAppendData(String id) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) {
			List<ObservableList<StringProperty>> dataList = new ArrayList<>();
			
			var map = dvt.getAppendData();
			for(String key: map.keySet()) {
				dataList.add(map.get(key));
			}
			return dataList;
		} 
		return null;
		 
	}

	public static void deleteTabDataRowNo(String tabid, String no) {
		 
			ObservableList<ObservableList<StringProperty>> ol = getTabData(tabid);;
			if (ol == null)
				return;
			for (int i = 0; i < ol.size(); i++) {
				ObservableList<StringProperty> sps = ol.get(i);
				int len = sps.size();
				String dro = sps.get(len - 1).get();
				if (dro.equals(no)) {
					ol.remove(i);
					break;
				}
			}
		 
		
	}
		
	public static Map<String, ObservableList<StringProperty>>  getOldval(String id){
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) { 
			var v = dvt.getOldval();
			return v;
		}
		return null;
	}
	
	public static Map<String, ObservableList<StringProperty>>  getNewLineDate(String id){
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) { 
			var v = dvt.getNewLineDate();
			return v;
		}
		return null;
	}
	
	
	public static boolean exist(String id, int row) {
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) { 
			Map<String, ObservableList<StringProperty>> oldval = dvt.getNewLineDate();
			if (null != oldval.get(row+"")) {
				return true;
			} 
		}
		return false;
	}
	
	public static void appendDate(String id, int rowNo, ObservableList<StringProperty> newDate) {
//		addDataNewLine(id, rowNo, newDate);
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) { 
			Map<String, ObservableList<StringProperty>> map = dvt.getAppendData();
			map.put(rowNo+"", newDate);
		}
	}
	
	
	public static List<ButtonBase> optionBtns(String id){
		DataViewTab dvt = getDataViewTab(id);
		List<ButtonBase> val =  new ArrayList<>();
		if(dvt != null) { 
			  val = dvt.getBtns();
		}
		return val;
	}
	
	public static List<MenuItem> MenuItems(String id){
		DataViewTab dvt = getDataViewTab(id);
		List<MenuItem> val =  new ArrayList<>();
		if(dvt != null) { 
			val =  dvt.getMenuItems();
		}
		return val;
	}
	
	// 清除不需要内存
	public static void clear(String id) {
		
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) {
			dvt.clean();
		}
		tabViews.remove(id);

	}
	
}
