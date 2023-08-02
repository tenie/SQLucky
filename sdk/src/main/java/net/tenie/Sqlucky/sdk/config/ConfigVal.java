package net.tenie.Sqlucky.sdk.config;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

/*   @author tenie */
public class ConfigVal {
	public static String textLogo = "\n\n" + "███████╗ ██████╗ ██╗     ██╗   ██╗ ██████╗██╗  ██╗██╗   ██╗\n"
			+ "██╔════╝██╔═══██╗██║     ██║   ██║██╔════╝██║ ██╔╝╚██╗ ██╔╝\n"
			+ "███████╗██║   ██║██║     ██║   ██║██║     █████╔╝  ╚████╔╝ \n"
			+ "╚════██║██║▄▄ ██║██║     ██║   ██║██║     ██╔═██╗   ╚██╔╝  \n"
			+ "███████║╚██████╔╝███████╗╚██████╔╝╚██████╗██║  ██╗   ██║   \n"
			+ "╚══════╝ ╚══▀▀═╝ ╚══════╝ ╚═════╝  ╚═════╝╚═╝  ╚═╝   ╚═╝   \n"
			+ "                                                           \n" + "\n";

	public static final String version = "3.1.0-Beta";
	// 模块路径
	public static File MODULE_PATH;

	// 账号信息
	public static SimpleBooleanProperty SQLUCKY_LOGIN_STATUS = new SimpleBooleanProperty(false);
	public static SimpleStringProperty SQLUCKY_USERNAME = new SimpleStringProperty("");
	public static SimpleStringProperty SQLUCKY_EMAIL = new SimpleStringProperty("");
	public static SimpleStringProperty SQLUCKY_PASSWORD = new SimpleStringProperty("");
	public static SimpleBooleanProperty SQLUCKY_REMEMBER = new SimpleBooleanProperty(false);
	public static SimpleBooleanProperty SQLUCKY_REMEMBER_SETTINGS = new SimpleBooleanProperty(false);
	public static SimpleBooleanProperty SQLUCKY_VIP = new SimpleBooleanProperty(false);

	public static String SQLUCKY_URL_DEV = "http://127.0.0.1:8088";
	public static String SQLUCKY_URL = "https://www.tenie.net";
	public static String SQLUCKY_URL_CUSTOM = "";

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
	public static final String EXEC_INFO_TITLE = "Execute Info";

	// 图标
//	public static String appIcon = "/image/SQL6.png";
//	public static String appIcon = "/image/logo32.png";

	public static String appIcon = "/icon/32.png";
	public static String THEME = CommonConst.THEME_DARK;
	public static int FONT_SIZE = -1;

	// h2数据库位置
	public static boolean IS_NEW_DB_VERSION = false;
	public static String H2_DIRVER = "org.h2.Driver";
	public static String H2_DB_FILE_NAME = "";
	public static String H2_DB_FULL_FILE_NAME = "";

	public static String H2_DB_NAME = "h2db";
	public static int H2_DB_VERSION = 5;

	public static String USER = "sa";
	public static String PASSWD = "xyz123qweasd";

	// 打开文件目录缓存
	public static String openfileDir = "";
	public static List<String> cssList = new ArrayList<>();
	public static List<String> cssListLight = new ArrayList<>();
	public static List<String> cssListYellow = new ArrayList<>();

	// 获取skd 的路径
	static {
		URL url = ConfigVal.class.getResource("");
		String pathVal = url.getPath();
		MODULE_PATH = new File(pathVal);
	}

	public static String getSqluckyServer() {

		String tmp = "";
		if (SQLUCKY_URL_CUSTOM != null && !"".equals(SQLUCKY_URL_CUSTOM)) {
			return SQLUCKY_URL_CUSTOM;
		}
		if (CommonUtils.isDev()) {
			tmp = SQLUCKY_URL_DEV;
		} else {
			tmp = SQLUCKY_URL;
		}
		return tmp;
	}

}
