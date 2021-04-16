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
			return dvt.getTabData();
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
	
	
//	private static Map<String, ObservableList<StringProperty>> getDataHelper(String id,
//			Map<String, ObservableList<StringProperty>> val) {
//		Map<String, ObservableList<StringProperty>> nrs = new HashMap<>();
//		Set<String> keys = val.keySet();
//		for (String s : keys) {
//			if (StrUtils.beginWith(s, tab + "_")) {
//				nrs.put(s, val.get(s));
//			}
//		}
//		return nrs;
//	}
	
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
	
	
	
	// 清除不需要内存
	public static void clear(String id) {
		
		DataViewTab dvt = getDataViewTab(id);
		if(dvt != null) {
			dvt.setAppendData(null);
			dvt.setColss(null);
			dvt.setConnName(null);
			dvt.setDataPane(null);
			dvt.setDbconns(null);
			dvt.setNewLineDate(null);
			dvt.setOldval(null);
			dvt.setRawData(null);
			dvt.setSqlStr(null);
			dvt.setTab(null);
			dvt.setTabCol(null);
			dvt.setTabData(null);
			dvt.setTabId(null);
			dvt.setTable(null);
			dvt.setTabName(null);
			dvt.setTdpo(null);
			var dpo = dvt.getDpo();
			dpo.clean();
			dvt.setDpo(null);
		}
		tabViews.remove(id);
//		Thread t = new Thread() {
//			public void run() {
//				logger.info("Thread-clear Cache Data");
//				tabNames.remove(tab);
//				removeHelper(newLineDate, tab);
//				removeHelper(oldval, tab);
//				tabCol.remove(tab);
//				tabData.remove(tab);
//				dbconns.remove(tab);
//				selectSql.remove(tab);
//				connName.remove(tab);
//				removeHelper(appendData, tab);
////				System.gc();
//			}
//		};
//		t.start();

	}
	
}
