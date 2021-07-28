package net.tenie.fx.main;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import net.tenie.fx.component.SqlCodeAreaHighLightingHelper;
import net.tenie.fx.component.container.AppWindow;
import net.tenie.fx.config.ConfigVal;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.tools.StrUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;

/*   @author tenie */
public class MainMyDB extends Application {
	public static List<String> argsList = new ArrayList<>();
	public static String userDir = "";
	private AppWindow app;
	private Scene scene;
	private Image img;
	private String Theme;
	private static Logger logger = LogManager.getLogger(MainMyDB.class);
	 
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

			primaryStage.setMaximized(true);
			primaryStage.setResizable(true);
			primaryStage.setOnCloseRequest(CommonEventHandler.mainCloseEvent());
			ComponentGetter.primaryStage = primaryStage;
			CommonAction.setTheme(Theme);
			primaryStage.show();
			 
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	public static void main(String[] args) throws IOException { 
		if(args != null && args.length > 0) {
			argsList = Arrays.asList(args);
		}
		logger.info("args = " + Arrays.toString(args));
//		File f = new File("");
//		String cf = null;
//		try {
//		     cf = f.getCanonicalPath();
//		} catch (IOException e) {
//		     e.printStackTrace();
//		}
//		userDir = System.getProperty("user.dir");
//	    System.out.println();//user.dir指定了当前的路径 
//		System.out.println(cf);
		userDir = System.getProperty("user.dir");
		LauncherImpl.launchApplication(MainMyDB.class, MyPreloader.class, args);
	}
}
