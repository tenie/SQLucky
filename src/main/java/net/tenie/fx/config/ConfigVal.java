package net.tenie.fx.config;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.SqlEditor;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.tools.StrUtils;

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
	public static List<String> cssList = new ArrayList<>();
	public static List<String> cssListLight = new ArrayList<>();
	public static List<String> cssListYellow = new ArrayList<>();
	public static String THEME = "";
	public static int FONT_SIZE = -1;
	 
	
	// 打开文件目录缓存
	public static String openfileDir = "";
	
	static {  

		    cssList.add(ConfigVal.class.getResource("/css/dark/common.css").toExternalForm());	
		    cssList.add(ConfigVal.class.getResource("/css/dark/sql-keywords.css").toExternalForm());
		    
			cssList.add(ConfigVal.class.getResource("/css/dark/treeView.css").toExternalForm());
			cssList.add(ConfigVal.class.getResource("/css/dark/TableView.css").toExternalForm());
			cssList.add(ConfigVal.class.getResource("/css/dark/tabPane.css").toExternalForm());
			 
			
			cssListLight.add(ConfigVal.class.getResource("/css/light/common-light.css").toExternalForm());
			cssListLight.add(ConfigVal.class.getResource("/css/light/sql-keywords-light.css").toExternalForm());
			cssListLight.add(ConfigVal.class.getResource("/css/light/tabPane-light.css").toExternalForm());
			
			cssListYellow.add(ConfigVal.class.getResource("/css/yellow/common-yellow.css").toExternalForm());
			cssListYellow.add(ConfigVal.class.getResource("/css/yellow/sql-keywords-yellow.css").toExternalForm());
			cssListYellow.add(ConfigVal.class.getResource("/css/yellow/treeView-yellow.css").toExternalForm());
			cssListYellow.add(ConfigVal.class.getResource("/css/yellow/TableView-yellow.css").toExternalForm());
			cssListYellow.add(ConfigVal.class.getResource("/css/yellow/tabPane-yellow.css").toExternalForm());
			 
	}
	
	

}
