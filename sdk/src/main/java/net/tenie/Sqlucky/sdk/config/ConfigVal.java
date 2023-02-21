package net.tenie.Sqlucky.sdk.config;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
 

/*   @author tenie */
public class ConfigVal {
	// 模块路径
	public static File MODULE_PATH ;
	
	// 账号信息
	public static String SQLUCKY_USERNAME = "";
	public static String SQLUCKY_EMAIL = "" ;
	public static String SQLUCKY_PASSWORD = "";
	public static boolean SQLUCKY_REMEMBER = false;
	
	
	
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
	
	// h2数据库位置
	public static String H2_DIRVER = "org.h2.Driver";
	public static String H2_DB_FILE_NAME = "";
	public static String H2_DB_FULL_FILE_NAME = "";
	
	public static String H2_DB_NAME = "h2db";
	public static int H2_DB_VERSION = 4;
	  
	public static String USER = "sa";
	public static String PASSWD = "xyz123qweasd";
	 
	
	// 打开文件目录缓存
	public static String openfileDir = "";
	public static List<String> cssList = new ArrayList<>();
	public static List<String> cssListLight = new ArrayList<>();
	public static List<String> cssListYellow = new ArrayList<>();
	
	
	// 获取skd 的路径
	static {
		URL url = ConfigVal.class.getClassLoader().getResource("");
		String pathVal = url.getPath();
		MODULE_PATH = new File(pathVal);  
	}
		
}
