package net.tenie.fx.component.container;

import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.MasterDetailPane;
import javafx.geometry.Side;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.fx.component.AppWindowComponentGetter;

/*   @author tenie */
public class AppWindow {
	private VBox mainWindow;
	private MenuBarContainer mainMenuBar;
	private MasterDetailPane masterDetailPane;
	private OperateContainer operate;
	private DataViewContainer dataView;

	public AppWindow() {
		mainWindow = new VBox();
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
		mainWindow.getChildren().addAll(mainMenuBar.getMainMenuBar(), masterDetailPane);
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

}
