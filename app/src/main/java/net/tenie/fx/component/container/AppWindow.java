package net.tenie.fx.component.container;

import com.jfoenix.controls.JFXButton;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import net.tenie.fx.factory.ButtonFactory;
import org.controlsfx.control.MasterDetailPane;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
	public static  Stage SQLuckyApp ;
	public static  AppWindow SQLuckyAppWindow ;
	private MenuBarContainer mainMenuBar;
	private MasterDetailPane masterDetailPane;
	// 操作区域, 数据库链接, sql编辑
	private OperateContainer operateContainer;

	// 数据展示区域
	public static DataViewContainer dataViewContainer;
	private Scene appScene;
	private StackPane root;
	// 窗口的顶部(主菜单的位置)
	private AnchorPane headAnchorPane = new AnchorPane();;
	// 放菜单
	HBox menuHBox = new HBox();
	//控制菜单显示按钮
	JFXButton showMenuBarBtn = new JFXButton();
	// sql btn
	public static HBox buttonBox;

	// 全局组件
//	public static DataViewContainer dataView;
	public static TreeView<TreeNodePo> treeView;
	public static DBinfoTree dbInfoTree;
	public static HBox dbInfoTreeFilter;
	public static volatile AppWindow app;

	public AppWindow() {
		super();
		AppWindow.SQLuckyAppWindow =this;
		CommonUtils.addCssClass(this, "main-background");
		ComponentGetter.mainWindow = this;

		mainMenuBar = new MenuBarContainer();
		masterDetailPane = new MasterDetailPane(Side.BOTTOM);
		//  操作容器, 左侧窗口, 代码编辑窗口
		operateContainer = new OperateContainer();
		// 下面窗口
		dataViewContainer = new DataViewContainer();

		masterDetailPane.getStyleClass().add(0, "masterDetailPane");
		masterDetailPane.setShowDetailNode(false);
		masterDetailPane.setMasterNode(operateContainer);
		masterDetailPane.setDetailNode(dataViewContainer);
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
		addTopImage(headAnchorPane);

		menuHBox.setPadding(new Insets(6, 0,0,0));

		showMenuBarBtn.setGraphic(IconGenerator.menuBarIcon());
		showMenuBarBtn.setOnAction(event -> {
			menuHBox.getChildren().remove(showMenuBarBtn);
			menuHBox.getChildren().add(mainMenuBar);
			menuHBox.setPadding(new Insets(0));
			Platform.runLater(()->{
				 addMouseEventFilter(SQLuckyApp);
			});

		});
		menuHBox.getChildren().add(showMenuBarBtn);

//		AnchorPane.setLeftAnchor(mainMenuBar, 3.0);
//		AnchorPane.setTopAnchor(mainMenuBar, 3.0);
//		AnchorPane.setLeftAnchor(showMenuBarBtn, 30.0);
//		AnchorPane.setTopAnchor(showMenuBarBtn, 6.0);

		headAnchorPane.getStyleClass().add("window-head-pane");
        // 按钮面板
		buttonBox = ButtonFactory.codeAreabtnInit();

		Platform.runLater(() -> {
			this.getChildren().addAll(headAnchorPane, masterDetailPane);
			VBox.setMargin(masterDetailPane, new Insets(3, 3, 3, 3));
			CommonUtils.fadeTransition(operateContainer, 2000);
			CommonUtils.fadeTransition(dataViewContainer, 2000);
			CommonUtils.fadeTransition(mainMenuBar, 2000);
			CommonUtils.fadeTransition(masterDetailPane, 2000);
		});

		CommonUtils.fadeTransition(this, 1000);
		ComponentGetter.treeView = treeView;


	}
	// 控制菜单显示
	public void ctrlMenuBarShow(){
		if( menuHBox.getChildren().contains(mainMenuBar) && !subMenuIsHover()){
			 hideMenuBar();
			 removeMouseEventFilter(SQLuckyApp);


		}
	}
	// 隐藏MenuBar
	public void hideMenuBar(){
		menuHBox.getChildren().remove(mainMenuBar);
		if(!menuHBox.getChildren().contains(showMenuBarBtn)){
			menuHBox.getChildren().add(showMenuBarBtn);
			menuHBox.setPadding(new Insets(6, 0,0,0));
		}
	}
	private  EventHandler<MouseEvent> appMouseEvent =new EventHandler<MouseEvent>() {
		//重写EventHandler接口实现方法
		@Override
		public void handle(MouseEvent event) {
			 ctrlMenuBarShow();
		}
	};
	public void addMouseEventFilter(Stage pStage){
		pStage.addEventFilter(MouseEvent.MOUSE_CLICKED, appMouseEvent);
	}
	public void removeMouseEventFilter(Stage pStage){
		pStage.removeEventFilter(MouseEvent.MOUSE_CLICKED, appMouseEvent);
	}

	private boolean subMenuIsHover(){
		var menusList = mainMenuBar.getMenus();
		boolean tf = false;
		for(var menu : menusList){
			tf = menu.isShowing();
			if(tf){
				return tf;
			}
		}

		return tf;
	}


	// 顶部左上角 图标
	private void addTopImage(AnchorPane operateBtnPane) {
		// 添加图标
		Image i = ComponentGetter.LogoIcons;
		ImageView mediaView = new ImageView(i);
		mediaView.setFitWidth(22.0);
		mediaView.setFitHeight(22.0);
		operateBtnPane.getChildren().addFirst(mediaView);
		AnchorPane.setTopAnchor(mediaView, 7.0);
		AnchorPane.setLeftAnchor(mediaView, 10.0);

         //    按钮bar 移动一下
		operateBtnPane.getChildren().add(menuHBox);
		AnchorPane.setLeftAnchor(menuHBox, 38.0);
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
		return operateContainer;
	}

	public void setOperate(OperateContainer operate) {
		this.operateContainer = operate;
	}

	public DataViewContainer getDataView() {
		return dataViewContainer;
	}

	public void setDataView(DataViewContainer dataView) {
		this.dataViewContainer = dataView;
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
