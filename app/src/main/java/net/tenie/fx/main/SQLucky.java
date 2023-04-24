package net.tenie.fx.main;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.net.ssl.SSLContext;

import com.sun.javafx.application.LauncherImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.LoadingAnimation;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.Log4jPrintStream;
import net.tenie.fx.Action.SettingKeyCodeCombination;
import net.tenie.fx.component.MyAreaTab;
import net.tenie.fx.component.UserAccount.UserAccountAction;
import net.tenie.fx.component.container.AppWindow;
import net.tenie.fx.factory.ServiceLoad;
import net.tenie.lib.db.h2.AppDao;
import net.tenie.lib.tools.UnlimitedCryptoPoliciesCheck;
import net.tenie.sdkImp.SqluckyAppComponent;

/**
 * 启动入口
 * @author tenie
 *
 */
public class SQLucky extends Application {
	public static String sysOpenFile = ""; 
	public static List<String> argsList = new ArrayList<>(); 
	public static String userDir = "";
	public static Stage pStage;
	private AppWindow app;
	private Scene scene;
	private Scene tmpscene;
	private Image img;
	private String Theme;
	private static Logger logger = LogManager.getLogger(SQLucky.class);
	
	private static boolean preloaderStatus = false;
	
	public static boolean isPreloaderStatus() {
		return preloaderStatus;
	}

	public static void setPreloaderStatus(boolean preloaderStatus) {
		SQLucky.preloaderStatus = preloaderStatus;
	}


	static {
		if( ! CommonUtility.isDev()) {
			Log4jPrintStream.redirectSystemOut();
		} 
//		Log4jPrintStream.redirectSystemOut();
	}
	@Override
	public void init() throws Exception {
		Connection conn = SqluckyAppDB.getConn();
		// 数据库迁移
		AppDao.testDbTableExists(conn);
		
		// 界面主题色， 没有设置过，默认黑色
	    Theme = AppDao.readConfig(conn , "THEME");  
	    if(StrUtils.isNullOrEmpty(Theme)) {
	    	AppDao.saveConfig(conn, "THEME", "DARK");
	    	Theme =  "DARK";
	    }
	    
	    ConfigVal.openfileDir = AppDao.readConfig(conn , "OPEN_FILE_DIR"); 
		SqluckyAppDB.closeConn(conn);
		ConfigVal.THEME = Theme;
		SqluckyAppComponent sqluckyComponent = new SqluckyAppComponent();
		ComponentGetter.appComponent = sqluckyComponent;
		// 注册
		ServiceLoad.callRegister();
		
		app = new AppWindow();
		SettingKeyCodeCombination.Setting();
		img = ComponentGetter.LogoIcons; //new Image(SQLucky.class.getResourceAsStream(ConfigVal.appIcon));
		
		
		scene = app.getAppScene();
		CommonAction.setTheme(Theme);
		// 加载插件
		ServiceLoad.callLoad();
		logger.info("完成初始化"); 

	}
	
 

	@Override
	public void start(Stage primaryStage) {
		ComponentGetter.SQLucky = this;
		try {
			pStage = primaryStage;
			
			// 图标 
			primaryStage.getIcons().add(img);
			primaryStage.setTitle("SQLucky"); 
//			primaryStage.centerOnScreen();
//			primaryStage.initStyle(StageStyle);			
//			primaryStage.setScene(tmpscene); 
			primaryStage.setScene(scene); 
//			var sceneRoot = scene.getRoot();
//			CommonUtility.fadeTransition(sceneRoot, 2000); 
//			app.fadeTransition();

//			 primaryStage.setIconified(true); 
			primaryStage.setIconified(true);
			// 确保全屏显示
			Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
			primaryStage.setX(primaryScreenBounds.getMinX());
			primaryStage.setY(primaryScreenBounds.getMinY());
			primaryStage.setWidth(primaryScreenBounds.getWidth());
			primaryStage.setHeight(primaryScreenBounds.getHeight());
			primaryStage.setMaximized(true); 
			primaryStage.setResizable(false);

			primaryStage.setOnCloseRequest(CommonEventHandler.mainCloseEvent());
			ComponentGetter.primaryStage = primaryStage; 
//			CommonAction.setTheme(Theme);
			 
//			 primaryStage.setX(500); 
//			 primaryStage.setY(100);
//			primaryStage.setWidth(100);
//			primaryStage.setHeight(0);
		    primaryStage.centerOnScreen();
//			primaryStage.initStyle(StageStyle.UNDECORATED);//设定窗口无边框
//		    primaryStage.setIconified(true); //最小化窗口，任务栏可见图标
			if(CommonUtility.isLinuxOS()) {
				MyPreloaderGif.hiden();
			}else {
				MyPreloaderMp4.hiden();
			}
			primaryStage.show(); 
			
			
			// 在stage show之后 需要初始化的内容, 如: 外观, 事件
			Platform.runLater(() -> {
				if(CommonUtility.isLinuxOS()) {
//					primaryStage.setAlwaysOnTop(true);
					primaryStage.toFront();
				}
				if(! primaryStage.isFocused()) {
					primaryStage.toFront();
				}
//				primaryStage.toFront();
				primaryStage.setMaximized(true);
				primaryStage.setResizable(true);
//				 primaryStage.setX(500); 
//				 primaryStage.setY(500);
				
				
				
				// 双击添加新codearea
				var mainTabPane = ComponentGetter.mainTabPane ;
				Node tabHeader = mainTabPane.lookup(".tab-header-area");
				tabHeader.setOnMouseClicked(mouseEvent->{
					if (mouseEvent.getClickCount() == 2) {
						MyAreaTab.addCodeEmptyTabMethod();
					}
				});
				 
				
			}); 
			
			ServiceLoad.callShowed(); 
			
			// 界面完成初始化后, 执行的回调函数
			Consumer< String > cr = v->{ 
				Platform.runLater(()->{
					// 移除loading...
					LoadingAnimation.rmLoading(ComponentGetter.primarySceneRoot);
					// 账号恢复
					UserAccountAction.appLanuchInitAccount();
				});
				
			}; 
			// 执行页面初始化好只会要执行的任务
			CommonUtility.executeInitTask(cr); 
			Long mm = Runtime.getRuntime().maxMemory()/1024;
			mm = mm / 1024;
			logger.info("Runtime.getRuntime().maxMemory = " + mm); 
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}

	}
	
	
	public static void main(String[] args) throws IOException { 
		logger.debug("main.args ==  "+ Arrays.toString(args)); 
		if(args!=null && args.length >0) {
			sysOpenFile = args[0];
		}
		
//		String dir = CommonUtility.sqluckyAppModsPath();
//		File file = new File(dir , "text");
//		System.out.println(file.getAbsolutePath());
//		file.createNewFile();
		
		
//		String  v = System.getProperty("jdk.module.path");
//		logger.debug("jdk.module.path = "+ v);
//		try {
//			String str = UnlimitedCryptoPoliciesCheck.getInfo();
//			logger.debug("UnlimitedCryptoPoliciesCheck ==  "+ str); 
//			System.setProperty("https.protocols", "TLSv1.2");
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
		
//		Properties  ps = System.getProperties();
//		ps.forEach((o1, o2)->{
//			System.out.println(o1 + " | "+ o2);
//			
//		});
		
		 
		
//		var val =System.getProperty("jdk.module.path");
//		var val =System.getProperty("jdk.module.upgrade.path");
//		var val =System.getProperty("jdk.module.main");
//		logger.debug("jdk.module.path ==  "+ Arrays.toString(val.split(":")));
		
		if(CommonUtility.isLinuxOS()) {
			LauncherImpl.launchApplication(SQLucky.class, MyPreloaderGif.class, args);
		}else {
			LauncherImpl.launchApplication(SQLucky.class, MyPreloaderMp4.class, args);
//			LauncherImpl.launchApplication(SQLucky.class, MyPreloaderGif.class, args);
//			LauncherImpl.launchApplication(SQLucky.class, MyPreloader.class, args);
			
		}
		
//		LauncherImpl.launchApplication(SQLucky.class, MyPreloaderGif.class, args);
	}
}
