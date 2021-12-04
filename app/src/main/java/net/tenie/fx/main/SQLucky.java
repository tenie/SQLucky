package net.tenie.fx.main;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.Log4jPrintStream;
import net.tenie.fx.Action.SettingKeyCodeCombination;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.fx.component.MyTab;
import net.tenie.fx.component.container.AppWindow;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.factory.ServiceLoad;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.db.h2.SqlTextDao;
import net.tenie.sdkImp.SqluckyComponent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * 启动入口
 * @author tenie
 *
 */
public class SQLucky extends Application {
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
		if( ! H2Db.isDev()) {
			Log4jPrintStream.redirectSystemOut();
		} 
//		Log4jPrintStream.redirectSystemOut();
	}
	@Override
	public void init() throws Exception {
		
		Connection conn = H2Db.getConn();
	    Theme = H2Db.getConfigVal(conn , "THEME");  
	    if(StrUtils.isNullOrEmpty(Theme)) {
	    	SqlTextDao.saveConfig(conn, "THEME", "DARK");
	    	Theme =  "DARK";
	    }
//	    H2Db.updateAppSql(conn);
	    
	    ConfigVal.openfileDir = H2Db.getConfigVal(conn , "OPEN_FILE_DIR"); 
		H2Db.closeConn();
		ConfigVal.THEME = Theme;
		SqluckyComponent sqluckyComponent = new SqluckyComponent();
		ComponentGetter.appComponent = sqluckyComponent;
		// 注册
		ServiceLoad.callRegister();
		
		app = new AppWindow();
//		scene = new Scene(app.getMainWindow());
//		scene.getStylesheets().addAll(ConfigVal.cssList);
//		ComponentGetter.primaryscene = scene;
		SettingKeyCodeCombination.Setting();
		img = new Image(SQLucky.class.getResourceAsStream(ConfigVal.appIcon));
		
		
		tmpscene = app.getTmpScene();
		CommonUtility.loadCss(tmpscene); 
		scene = app.getAppScene();
		CommonAction.setTheme(Theme);
		// 加载插件
		ServiceLoad.callLoad();
		logger.info("完成初始化"); 
		
		MyPreloaderMp4.hiden();
		MyPreloaderGif.hiden();
	}
	
	public void testFinish() {
//		while(true) {
//			var tf = SQLucky.isPreloaderStatus();
//			if(tf) { 
//				break;
//			}
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
	}

	@Override
	public void start(Stage primaryStage) {
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
//			  primaryStage.initStyle(StageStyle.UNDECORATED);//设定窗口无边框

			
			primaryStage.show(); 
			
			
			// 在stage show之后 需要初始化的内容, 如: 外观, 事件
			Platform.runLater(() -> {
				if(CommonUtility.isLinuxOS()) {
//					primaryStage.setAlwaysOnTop(true);
					primaryStage.toFront();
				}
//				primaryStage.toFront();
				primaryStage.setMaximized(true);
				primaryStage.setResizable(true);
//				 primaryStage.setX(500); 
//				 primaryStage.setY(500);
				var dbTitledPane     = ComponentGetter.dbTitledPane  ;
				var scriptTitledPane = ComponentGetter.scriptTitledPane;
				
				final StackPane Node = (StackPane)dbTitledPane.lookup(".arrow-button");
				Node.getChildren().clear(); 
			
				Node.getChildren().add(ComponentGetter.iconInfo);
				
				var title = 	dbTitledPane.lookup(".title");
				title.setOnMouseEntered( e->{ 
					Node.getChildren().clear();
					if(dbTitledPane.isExpanded()) {
						Node.getChildren().add(ComponentGetter.iconLeft);
					}else {
						Node.getChildren().add(ComponentGetter.iconRight);
					}
					
				});
				
				title.setOnMouseExited( e->{ 
					Node.getChildren().clear();
					Node.getChildren().add(ComponentGetter.iconInfo);
				});
				
				
				
				final StackPane  Node2 = (StackPane)scriptTitledPane.lookup(".arrow-button");
				Node2.getChildren().clear();  
				Node2.getChildren().add(ComponentGetter.iconScript);
				
				
				var title2 = scriptTitledPane.lookup(".title");
				title2.setOnMouseEntered( e->{ 
					Node2.getChildren().clear();
					if(scriptTitledPane.isExpanded()) {
						Node2.getChildren().add(ComponentGetter.iconLeft);
					}else {
						Node2.getChildren().add(ComponentGetter.iconRight);
					}
				});
				
				title2.setOnMouseExited( e->{ 
					Node2.getChildren().clear();
					Node2.getChildren().add(ComponentGetter.iconScript);
				});
				// 双击添加新codearea
				var mainTabPane = ComponentGetter.mainTabPane ;
				Node tabHeader = mainTabPane.lookup(".tab-header-area");
				tabHeader.setOnMouseClicked(mouseEvent->{
					if (mouseEvent.getClickCount() == 2) {
						MyTab.addCodeEmptyTabMethod();
					}
				});
				 
				
			}); 
			
			ServiceLoad.callShowed(); 
			
			// 移除loading...
			Consumer< String > cr = v->{ 
				Platform.runLater(()->{
					app.rmlb();
				});
				
			}; 
			CommonUtility.executeInitTask(cr);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}
	
	
	public static void main(String[] args) throws IOException { 
		if(CommonUtility.isLinuxOS()) {
			LauncherImpl.launchApplication(SQLucky.class, MyPreloaderGif.class, args);
		}else {
			LauncherImpl.launchApplication(SQLucky.class, MyPreloaderMp4.class, args);
		}
//		LauncherImpl.launchApplication(SQLucky.class, MyPreloaderGif.class, args);
	}
}
