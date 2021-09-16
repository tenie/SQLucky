package net.tenie.Sqlucky.sdk.config;

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
	public static final int maxDataTab = 1;
	// 代码显示个数计数
	public static int pageSize = -1;
	// sql 查询获取最大行
	public static int MaxRows = 100;

	// sql txt area tag
	public static String SQL_AREA_TAG = "code";
	// sql text save tag
//	public static String SAVE_TAG = "code_save_";

	// data table tag
	public static volatile int tableIdx = 0;
	// new line date idx
	public static volatile int newLineIdx = Integer.MIN_VALUE;
	
	// 数据展示窗口, 执行日志信息Tab的title名称
	public static final String  EXEC_INFO_TITLE = "Execute Info";

	// 图标
	public static String appIcon = "/image/SQL6.png";

	public static String THEME = "";
	public static int FONT_SIZE = -1;
	 
	
	// 打开文件目录缓存
	public static String openfileDir = "";
	public static List<String> cssList = new ArrayList<>();
	public static List<String> cssListLight = new ArrayList<>();
	public static List<String> cssListYellow = new ArrayList<>();
	
	
	

}
