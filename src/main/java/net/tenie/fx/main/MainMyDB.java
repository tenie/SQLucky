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
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.SettingKeyCodeCombination;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.container.AppWindow;
import net.tenie.fx.config.ConfigVal;
import net.tenie.lib.db.h2.H2Db;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

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
	
	@SuppressWarnings("exports")
	public static final Region imgInfo = ImageViewGenerator.svgImageDefActive("info-circle", 14);  
	@SuppressWarnings("exports")
	public static final Region imgScript = ImageViewGenerator.svgImageDefActive("icomoon-files-empty", 14);
	@SuppressWarnings("exports")
	public static final Region imgRight = ImageViewGenerator.svgImageDefActive("chevron-circle-right", 14);
	@SuppressWarnings("exports")
	public static final Region imgLeft = ImageViewGenerator.svgImageDefActive("chevron-circle-down", 14);
	 
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
//		Thread.sleep(1000); 
		logger.info("完成初始化"); 
	}

	@Override
	public void start(Stage primaryStage) {
		try {
//			String cssStr = ConfigVal.class.getResource("/css/common.css").toExternalForm();
			// 图标
			primaryStage.getIcons().add(img);
			primaryStage.setTitle("SQLucky");
			primaryStage.setScene(scene); 


			primaryStage.setOnCloseRequest(CommonEventHandler.mainCloseEvent());
			ComponentGetter.primaryStage = primaryStage;
			CommonAction.setTheme(Theme);
			primaryStage.show();
			
			// 外形设置
			Platform.runLater(() -> { 
				primaryStage.setMaximized(true);
				primaryStage.setResizable(true);
				var dbTitledPane     = ComponentGetter.dbTitledPane  ;
				var scriptTitledPane = ComponentGetter.scriptTitledPane;
				
				final StackPane Node = (StackPane)dbTitledPane.lookup(".arrow-button");
				Node.getChildren().clear(); 
			
				Node.getChildren().add(imgInfo);
				
				var title = 	dbTitledPane.lookup(".title");
				title.setOnMouseEntered( e->{ 
					Node.getChildren().clear();
					if(dbTitledPane.isExpanded()) {
						Node.getChildren().add(imgLeft);
					}else {
						Node.getChildren().add(imgRight);
					}
					
				});
				
				title.setOnMouseExited( e->{ 
					Node.getChildren().clear();
					Node.getChildren().add(imgInfo);
				});
				
				
				
				final StackPane  Node2 = (StackPane)scriptTitledPane.lookup(".arrow-button");
				Node2.getChildren().clear();  
				Node2.getChildren().add(imgScript);
				
				
				var title2 = scriptTitledPane.lookup(".title");
				title2.setOnMouseEntered( e->{ 
					Node2.getChildren().clear();
					if(scriptTitledPane.isExpanded()) {
						Node2.getChildren().add(imgLeft);
					}else {
						Node2.getChildren().add(imgRight);
					}
				});
				
				title2.setOnMouseExited( e->{ 
					Node2.getChildren().clear();
					Node2.getChildren().add(imgScript);
				});
				
				
			});
			
			 
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}
	
	public static void printPath() {
		String  modulePath = System.getProperty("jdk.module.path");
		String[] ls  = modulePath.split(";");
		for(String path : ls) {
			logger.info(path);
		}
	}
	

	public static void main(String[] args) throws IOException {
//		System.out.println(System.getProperty("jdk.module.path"));
		printPath();
		LauncherImpl.launchApplication(MainMyDB.class, MyPreloader.class, args);
	}
}
