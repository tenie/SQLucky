package net.tenie.fx.main;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
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
import net.tenie.fx.factory.ServiceLoad;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.tools.IconGenerator;
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
public class MainMyDB extends Application {
	public static List<String> argsList = new ArrayList<>(); 
	public static String userDir = "";
	private AppWindow app;
	private Scene scene;
	private Image img;
	private String Theme;
	private static Logger logger = LogManager.getLogger(MainMyDB.class);
	

	
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
	    H2Db.updateAppSql(conn);
	    ConfigVal.openfileDir = H2Db.getConfigVal(conn , "OPEN_FILE_DIR"); 
		H2Db.closeConn();
		ConfigVal.THEME = Theme;
		app = new AppWindow();
		scene = new Scene(app.getMainWindow());
//		scene.getStylesheets().addAll(ConfigVal.cssList);
		ComponentGetter.primaryscene = scene;
		SettingKeyCodeCombination.Setting();
		img = new Image(MainMyDB.class.getResourceAsStream(ConfigVal.appIcon));

		SqluckyComponent sqluckyComponent = new SqluckyComponent();
		ComponentGetter.appComponent = sqluckyComponent;
		// 加载插件
		ServiceLoad.myLoader();
		logger.info("完成初始化"); 
	}

	@Override
	public void start(Stage primaryStage) {
		try {
//			String cssStr = ConfigVal.class.getResource("/css/common.css").toExternalForm();
			// 图标
			primaryStage.getIcons().add(img);
			primaryStage.setTitle("SQLucky"); 
//			primaryStage.initStyle(StageStyle);
//			primaryStage.setScene(scene); 

//			primaryStage.setMaximized(true);
//			primaryStage.setResizable(true);

			primaryStage.setOnCloseRequest(CommonEventHandler.mainCloseEvent());
			ComponentGetter.primaryStage = primaryStage;
			CommonAction.setTheme(Theme);
			primaryStage.show();
			
			
			// 在stage show之后 需要初始化的内容, 如: 外观, 事件
			Platform.runLater(() -> { 
				primaryStage.setScene(scene); 
				primaryStage.setMaximized(true);
				primaryStage.setResizable(true);
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
			 
			ServiceLoad.myShowed(); 
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}
	
	
	public static void main(String[] args) throws IOException { 
		LauncherImpl.launchApplication(MainMyDB.class, MyPreloader.class, args);
	}
}
