package net.tenie.fx.config;

import java.util.ArrayList;
import java.util.List;

/*   @author tenie */
public class ConfigVal {
	// date formate
	public static String dateFormateL = "yyyy-MM-dd HH:mm:ss";
	public static String dateFormateS = "yyyy-MM-dd";

	public static String dburl = "";
	public static String ph = "";
	public static boolean del = false;
	// 数据table 最大显示页面个数
	public static final int maxDataTab = 10;
	// 代码显示个数计数
	public static int pageSize = -1;
	// sql 查询获取最大行
	public static int MaxRows = 100;

	// sql txt area tag
	public static String SQL_AREA_TAG = "code";
	// sql text save tag
	public static String SAVE_TAG = "code_save_";

	// data table tag
	public static volatile int tableIdx = 0;
	// new line date idx
	public static volatile int newLineIdx = Integer.MIN_VALUE;

	public static List<String> cssList = new ArrayList<>();
	static {

		cssList.add(ConfigVal.class.getResource("/css/sql-keywords.css").toExternalForm());
		cssList.add(ConfigVal.class.getResource("/css/treeView.css").toExternalForm());
		cssList.add(ConfigVal.class.getResource("/css/TableView.css").toExternalForm());
		cssList.add(ConfigVal.class.getResource("/css/tabPane.css").toExternalForm());
		cssList.add(ConfigVal.class.getResource("/css/common.css").toExternalForm());
	}

}
