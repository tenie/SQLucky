package net.tenie.fx.component.container;

import javafx.scene.layout.*;
import org.controlsfx.control.MasterDetailPane;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.DataViewContainer;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.po.component.TreeNodePo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.fx.component.InfoTree.DBinfoTree;

/*   @author tenie */
public class AppWindow extends VBox{
	private MenuBarContainer mainMenuBar;
	private MasterDetailPane masterDetailPane;
	private OperateContainer operate;
	public static DataViewContainer dataView;
	private Scene appScene;
	private StackPane root;
	// 窗口的顶部(主菜单的位置)
	private AnchorPane headAnchorPane;

	// 全局组件
//	public static DataViewContainer dataView;
	public static TreeView<TreeNodePo> treeView;
	public static DBinfoTree dbInfoTree;
	public static HBox dbInfoTreeFilter;
	public static volatile AppWindow app;

	public AppWindow() {
		super();
		CommonUtils.addCssClass(this, "main-background");
		ComponentGetter.mainWindow = this;

		mainMenuBar = new MenuBarContainer();
		masterDetailPane = new MasterDetailPane(Side.BOTTOM);
		masterDetailPane = new MasterDetailPane(Side.BOTTOM);
		operate = new OperateContainer();
		dataView = new DataViewContainer();

		masterDetailPane.getStyleClass().add(0, "masterDetailPane");
		masterDetailPane.setShowDetailNode(false);
		masterDetailPane.setMasterNode(operate);
		masterDetailPane.setDetailNode(dataView.getContainer());
		masterDetailPane.setDividerPosition(0.6);
		VBox.setVgrow(masterDetailPane, Priority.ALWAYS);

		ComponentGetter.masterDetailPane = masterDetailPane;
		// 设置tree 面板的显示比例
		masterDetailPane.widthProperty().addListener((ob, ov, nv) -> {
			if (nv.doubleValue() > 1) {
				double wi = ComponentGetter.masterDetailPane.getWidth();
				double tbp = 300.0;
				double val = tbp / wi;
				ComponentGetter.treeAreaDetailPane.setDividerPosition(val);
			}
		});
		root = new StackPane(this);
		LoadingAnimation.addLoading(root);

		appScene = new Scene(root);
		ComponentGetter.primaryscene = appScene;
		ComponentGetter.primarySceneRoot = root;

		// 主菜单加入到顶部pane中
		headAnchorPane = new AnchorPane();
		headAnchorPane.getChildren().add(mainMenuBar);
		AnchorPane.setLeftAnchor(mainMenuBar, 3.0);
		AnchorPane.setTopAnchor(mainMenuBar, 3.0);

		headAnchorPane.getStyleClass().add("window-head-pane");

		Platform.runLater(() -> {
			this.getChildren().addAll(headAnchorPane, masterDetailPane);
			VBox.setMargin(masterDetailPane, new Insets(3, 3, 3, 3));
			CommonUtils.fadeTransition(operate, 2000);
			CommonUtils.fadeTransition(dataView.getContainer(), 2000);
			CommonUtils.fadeTransition(mainMenuBar, 2000);
			CommonUtils.fadeTransition(masterDetailPane, 2000);
		});

		CommonUtils.fadeTransition(this, 1000);
		ComponentGetter.treeView = treeView;
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
		ConfigVal.cssListYellow
				.add(AppWindow.class.getResource("/css/yellow/sql-keywords-yellow.css").toExternalForm());
		ConfigVal.cssListYellow.add(AppWindow.class.getResource("/css/yellow/treeView-yellow.css").toExternalForm());
		ConfigVal.cssListYellow.add(AppWindow.class.getResource("/css/yellow/TableView-yellow.css").toExternalForm());
		ConfigVal.cssListYellow.add(AppWindow.class.getResource("/css/yellow/tabPane-yellow.css").toExternalForm());

		ComponentGetter.INFO = new Label("");  //Info
		ComponentGetter.INFO.setGraphic(IconGenerator.svgImage("info-circle", "#7CFC00"));

		ComponentGetter.ABOUT = new Label(""); // About
		ComponentGetter.ABOUT.setGraphic(IconGenerator.svgImage("info-circle", "#7CFC00"));

		ComponentGetter.WARN = new Label(""); // Warn
		ComponentGetter.WARN.setGraphic(IconGenerator.svgImage("info-circle", "#FFD700"));
		ComponentGetter.ERROR = new Label(""); // Error
		ComponentGetter.ERROR.setGraphic(IconGenerator.svgImage("info-circle", "red"));
		ComponentGetter.EMPTY = new Label("");

		ComponentGetter.iconInfo = IconGenerator.svgImageDefActive("info-circle");
		ComponentGetter.uaIconInfo = IconGenerator.svgImageUnactive("info-circle");
		ComponentGetter.iconScript = IconGenerator.svgImageDefActive("icomoon-files-empty");
		ComponentGetter.uaIconScript = IconGenerator.svgImageUnactive("icomoon-files-empty");

		ComponentGetter.iconRight = IconGenerator.svgImageDefActive("chevron-circle-right", 14);
		ComponentGetter.iconLeft = IconGenerator.svgImageDefActive("chevron-circle-down", 14);

		ComponentGetter.LogoIcons = new Image(AppWindow.class.getResourceAsStream(ConfigVal.appIcon));

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

	public AnchorPane getHeadAnchorPane() {
		return headAnchorPane;
	}

	public void setHeadAnchorPane(AnchorPane headAnchorPane) {
		this.headAnchorPane = headAnchorPane;
	}

}
