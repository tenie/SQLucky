package net.tenie.fx.component.container;

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.MasterDetailPane;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.fx.component.AppWindowComponentGetter;
import net.tenie.lib.tools.IconGenerator;

/*   @author tenie */
public class AppWindow {
	private VBox mainWindow;
	private MenuBarContainer mainMenuBar;
	private MasterDetailPane masterDetailPane;
	private OperateContainer operate;
	private DataViewContainer dataView;
	private Scene appScene;
	

	public AppWindow() {
		mainWindow = new VBox();
//		mainWindow.getStyleClass().add("main-background");
		CommonUtility.addCssClass(mainWindow, "main-background");
		ComponentGetter.mainWindow = mainWindow;

		mainMenuBar = new MenuBarContainer();
		masterDetailPane = new MasterDetailPane(Side.BOTTOM);
		masterDetailPane = new MasterDetailPane(Side.BOTTOM);
		operate = new OperateContainer();
		dataView = new DataViewContainer();

		masterDetailPane.getStyleClass().add(0, "masterDetailPane");
		masterDetailPane.setShowDetailNode(false);
		masterDetailPane.setMasterNode(operate.getContainer());
		masterDetailPane.setDetailNode(dataView.getContainer());
		masterDetailPane.setDividerPosition(0.6);
		VBox.setVgrow(masterDetailPane, Priority.ALWAYS);

		ComponentGetter.masterDetailPane = masterDetailPane;
		AppWindowComponentGetter.dataView = dataView;
//		mainWindow.getChildren().addAll(mainMenuBar.getMainMenuBar(), masterDetailPane);
		// 设置tree 面板的显示比例
		masterDetailPane.widthProperty().addListener((ob, ov ,nv)->{
				if (nv.doubleValue() > 1) {
					double wi = ComponentGetter.masterDetailPane.getWidth();
					double tbp = 275.0;
					double val =  tbp / wi;  
					System.out.println("设置窗口比例 :" + val);
					ComponentGetter.treeAreaDetailPane.setDividerPosition(val);
				}
		});  
//		BorderPane root = new BorderPane();
//		root.setCenter(mainWindow);
		Label lb =new Label("Loading.....");
		StackPane root = new StackPane( mainWindow, lb);
		root.getChildren().remove(lb);

		appScene = new Scene(root); 
//		appScene = new Scene(mainWindow); 
		ComponentGetter.primaryscene = appScene;
		Platform.runLater(()->{
//			CommonUtility.platformAwait();
			mainWindow.getChildren().addAll(mainMenuBar.getMainMenuBar(), masterDetailPane);
			CommonUtility.fadeTransition(operate.getContainer(), 3500); 
			CommonUtility.fadeTransition(dataView.getContainer(), 3500); 
			CommonUtility.fadeTransition(mainMenuBar.getMainMenuBar(), 3500); 
			CommonUtility.fadeTransition(masterDetailPane, 3500); 
		});  
		
		CommonUtility.fadeTransition(mainWindow, 2500); 
	}

 
	static {
		ConfigVal.cssList.add(AppWindow.class.getResource("/css/application.css").toExternalForm());	
		ConfigVal.cssList.add(AppWindow.class.getResource("/css/dark/common.css").toExternalForm());	
		ConfigVal.cssList.add(AppWindow.class.getResource("/css/dark/sql-keywords.css").toExternalForm());	    
		ConfigVal.cssList.add(AppWindow.class.getResource("/css/dark/treeView.css").toExternalForm());
		ConfigVal.cssList.add(AppWindow.class.getResource("/css/dark/TableView.css").toExternalForm());
		ConfigVal.cssList.add(AppWindow.class.getResource("/css/dark/tabPane.css").toExternalForm());
		 
		ConfigVal.cssListLight.add(AppWindow.class.getResource("/css/application.css").toExternalForm());	
		ConfigVal.cssListLight.add(AppWindow.class.getResource("/css/light/common-light.css").toExternalForm());
		ConfigVal.cssListLight.add(AppWindow.class.getResource("/css/light/sql-keywords-light.css").toExternalForm());
		ConfigVal.cssListLight.add(AppWindow.class.getResource("/css/light/tabPane-light.css").toExternalForm());
		
		ConfigVal.cssListYellow.add(AppWindow.class.getResource("/css/application.css").toExternalForm());	
		ConfigVal.cssListYellow.add(AppWindow.class.getResource("/css/yellow/common-yellow.css").toExternalForm());
		ConfigVal.cssListYellow.add(AppWindow.class.getResource("/css/yellow/sql-keywords-yellow.css").toExternalForm());
		ConfigVal.cssListYellow.add(AppWindow.class.getResource("/css/yellow/treeView-yellow.css").toExternalForm());
		ConfigVal.cssListYellow.add(AppWindow.class.getResource("/css/yellow/TableView-yellow.css").toExternalForm());
		ConfigVal.cssListYellow.add(AppWindow.class.getResource("/css/yellow/tabPane-yellow.css").toExternalForm());	 

		 
		ComponentGetter.INFO = new Label("Info");
		ComponentGetter.INFO.setGraphic( IconGenerator.svgImage("info-circle", "#7CFC00", false));
		
		ComponentGetter.ABOUT = new Label("About");
		ComponentGetter.ABOUT.setGraphic( IconGenerator.svgImage("info-circle", "#7CFC00", false));
		
		ComponentGetter.WARN = new Label("Warn");
		ComponentGetter.WARN.setGraphic( IconGenerator.svgImage("info-circle", "#FFD700", false));
		ComponentGetter.ERROR = new Label("Error");
		ComponentGetter.ERROR.setGraphic( IconGenerator.svgImage("info-circle", "red", false));
		ComponentGetter.EMPTY = new Label("");
		
		 
		ComponentGetter.iconInfo   = IconGenerator.svgImageDefActive("info-circle", 14);  
		ComponentGetter.iconScript = IconGenerator.svgImageDefActive("icomoon-files-empty", 14);
		ComponentGetter.iconRight  = IconGenerator.svgImageDefActive("chevron-circle-right", 14);
		ComponentGetter.iconLeft   = IconGenerator.svgImageDefActive("chevron-circle-down", 14);
		 
		
		
		 
	}
	
	public VBox getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(VBox mainWindow) {
		this.mainWindow = mainWindow;
	}

	public MenuBarContainer getMainMenuBar() {
		return mainMenuBar;
	}

	public void setMainMenuBar(MenuBarContainer mainMenuBar) {
		this.mainMenuBar = mainMenuBar;
	}

	public MasterDetailPane getMasterDetailPane() {
		return masterDetailPane;
	}

	public void setMasterDetailPane(MasterDetailPane masterDetailPane) {
		this.masterDetailPane = masterDetailPane;
	}

	public OperateContainer getOperate() {
		return operate;
	}

	public void setOperate(OperateContainer operate) {
		this.operate = operate;
	}

	public DataViewContainer getDataView() {
		return dataView;
	}

	public void setDataView(DataViewContainer dataView) {
		this.dataView = dataView;
	}

	public Scene getAppScene() {
		return appScene;
	}

	public void setAppScene(Scene appScene) {
		this.appScene = appScene;
	}
	
	
	public Scene getTmpScene() {
		VBox tmpbox = new VBox(); 
		BorderPane root = new BorderPane(tmpbox);
		root.setCenter(tmpbox);
		tmpbox.getStyleClass().add("main-background");
		Scene tmpscene = new Scene( root);
		return tmpscene;
	}


}
