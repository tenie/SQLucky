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
import net.tenie.fx.dao.DeleteDao;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class CacheTableDate {
	private static Logger logger = LogManager.getLogger(CacheTableDate.class);
	private static Map<String, String> tabNames = new HashMap<>();
	private static Map<String, String> selectSql = new HashMap<>();
	private static Map<String, Tab> tabs = new HashMap<>();
	private static Map<String, String> connName = new HashMap<>();
	

	// table id + row num 组成key ,保存对于行的数据
	private static Map<String, ObservableList<StringProperty>> newLineDate = new HashMap<>();
	// table id + row num 组成key ,保存对于行的原始数据
	private static Map<String, ObservableList<StringProperty>> oldval = new HashMap<>();
	// 表字段的信息
	private static Map<String, ObservableList<SqlFieldPo>> tabCol = new HashMap<>();
	// 表格数据
	private static Map<String, ObservableList<ObservableList<StringProperty>>> tabData = new HashMap<>();

	// 待insert的 数据
	private static Map<String, ObservableList<StringProperty>> appendData = new HashMap<>();

	// 数据连接对象
	private static Map<String, Connection> dbconns = new HashMap<>();

	// 添加数据
	public static void appendDate(String tab, int row, ObservableList<StringProperty> vals) {
		appendData.put(tab + "_" + row, vals);

	}

	public static List<ObservableList<StringProperty>> getAppendData(String tag) {
		List<ObservableList<StringProperty>> rs = new ArrayList<>();
		for (String key : appendData.keySet()) {
			if (StrUtils.beginWith(key, tag)) {
				rs.add(appendData.get(key));
			}
		}
		return rs;
	}

	public static void rmAppendData(String tag) {
		removeHelper(appendData, tag);
	}

	public static void rmUpdateData(String tab) {
		removeHelper(newLineDate, tab);
		removeHelper(oldval, tab);
	}

	// 删除对应行号的 tale view中的数据
	public static void deleteTabDataRowNo(String tabid, String no) {
		ObservableList<ObservableList<StringProperty>> ol = tabData.get(tabid);
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

	// 清除不需要内存
	public static void clear(String tab) {
		Thread t = new Thread() {
			public void run() {
				logger.info("Thread-clear Cache Data");
				tabNames.remove(tab);
				removeHelper(newLineDate, tab);
				removeHelper(oldval, tab);
				tabCol.remove(tab);
				tabData.remove(tab);
				dbconns.remove(tab);
				selectSql.remove(tab);
				connName.remove(tab);
				removeHelper(appendData, tab);
				System.gc();
			}
		};
		t.start();

	}

	public static void saveDBConn(String tab, Connection connpo) {
		dbconns.put(tab, connpo);
	}

	public static Connection getDBConn(String tab) {
		return dbconns.get(tab);
	}

	private static void removeHelper(Map<String, ObservableList<StringProperty>> vals, String tab) {
		List<String> temp = new ArrayList<>();
		for (String k : vals.keySet()) {
			if (StrUtils.beginWith(k, tab + "_")) {
				temp.add(k);
			}
		}
		temp.forEach(v -> {
			vals.remove(v);
		});
	}

	public static void addCols(String tab, ObservableList<SqlFieldPo> cs) {
		tabCol.put(tab, cs);
	}

	public static ObservableList<SqlFieldPo> getCols(String tab) {
		return tabCol.get(tab);
	}

	public static void addData(String tab, ObservableList<ObservableList<StringProperty>> cs) {
		tabData.put(tab, cs);
	}

	public static ObservableList<ObservableList<StringProperty>> getData(String tab) {
		return tabData.get(tab);
	}

	public static void addData(String tab, int row, ObservableList<StringProperty> newDate,
			ObservableList<StringProperty> oldDate) {
		if (!exist(tab, row)) {
			oldval.put(tab + "_" + row, oldDate);
		}
		newLineDate.put(tab + "_" + row, newDate);

	}

	public static void addData(String tab, int row, ObservableList<StringProperty> newDate) {
		newLineDate.put(tab + "_" + row, newDate);
	}

	public static boolean exist(String tab, int row) {
		if (null != oldval.get(tab + "_" + row)) {
			return true;
		}

		return false;
	}

	private static Map<String, ObservableList<StringProperty>> getDataHelper(String tab,
			Map<String, ObservableList<StringProperty>> val) {
		Map<String, ObservableList<StringProperty>> nrs = new HashMap<>();
		Set<String> keys = val.keySet();
		for (String s : keys) {
			if (StrUtils.beginWith(s, tab + "_")) {
				nrs.put(s, val.get(s));
			}
		}
		return nrs;
	}

	public static Map<String, ObservableList<StringProperty>> getOriginalData(String tab) {
		return getDataHelper(tab, oldval);
	}

	public static Map<String, ObservableList<StringProperty>> getModifyData(String tab) {
		return getDataHelper(tab, newLineDate);
	}

	public static ObservableList<StringProperty> getold(String tab, String row) {
		return oldval.get(tab + "_" + row);
	}

	public static ObservableList<StringProperty> getold(String key) {
		return oldval.get(key);
	}

	// 保存tabname
	public static void saveTableName(String tab, String tableName) {
		tabNames.put(tab, tableName);
	}

	public static String getTableName(String tab) {
		return tabNames.get(tab);
	}

	// 保存sql
	public static void saveSelectSQl(String tab, String tableName) {
		selectSql.put(tab, tableName);

	}

	public static String getSelectSQl(String tab) {
		return selectSql.get(tab);
	}

	public static void saveTab(String id, Tab tb) {
		tabs.put(id, tb);
	}

	public static Tab getTab(String id) {
		return tabs.get(id);
	}
	
	// 保存 链接名字
	public static void saveConnName(String tab, String name) {
		connName.put(tab, name);

	}

	public static String getConnName(String tab) {
		return connName.get(tab);
	}
}
