package net.tenie.fx.main;

import java.io.IOException;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.container.AppWindow;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.utility.EventAndListener.CommonEventHandler;
import net.tenie.fx.utility.EventAndListener.SettingKeyCodeCombination;
import net.tenie.lib.db.h2.H2Db;
import javafx.scene.Scene;
import javafx.scene.image.Image;

/*   @author tenie */
public class MainMyDB extends Application {
	private AppWindow app;
	private Scene scene;
	private Image img;

	@Override
	public void init() throws Exception {
		app = new AppWindow();
		scene = new Scene(app.getMainWindow());
		scene.getStylesheets().addAll(ConfigVal.cssList);
		ComponentGetter.primaryscene = scene;
		SettingKeyCodeCombination.Setting();
		img = new Image(MainMyDB.class.getResourceAsStream("/image/SQL6.png"));
		Thread.sleep(1000);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			// 图标
			primaryStage.getIcons().add(img);
			primaryStage.setTitle("SQLucky");
			primaryStage.setScene(scene);

			primaryStage.setMaximized(true);
			primaryStage.setResizable(true);
			primaryStage.setOnCloseRequest(CommonEventHandler.mainCloseEvent());
			ComponentGetter.primaryStage = primaryStage;
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	public static void main(String[] args) throws IOException {

		H2Db.getConn();
		H2Db.closeConn();
		LauncherImpl.launchApplication(MainMyDB.class, MyPreloader.class, args);

	}
}
