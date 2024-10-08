package net.tenie.fx.component.container;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.tenie.Sqlucky.sdk.component.CommonButtons;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.AppCommonAction;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.fx.Action.CommonEventHandler;

public class AppWindowReStyleByWinOS {
	// 关闭, 最小化, 还原的图标
//	private JFXButton windowResize = new JFXButton();
//	private	JFXButton hidden = new JFXButton();
//	private JFXButton close = new JFXButton();
//
//	private JFXButton hideLeft = new JFXButton();
//	private JFXButton hideBottom = new JFXButton();
//	private JFXButton hideRight = new JFXButton();

	private	Scene scene ;

	private	Stage smallWindowStage;
	private	Stage primaryWindowStage;

	private Double smallWindowWidth = 1200.0;
	private Double smallWindowHeight = 680.0;
	private Double primaryWindowWidth = 0.0;
	private Double primaryWindowHeight = 0.0;


	// 窗口默认收缩尺寸(按钮触发)
	javafx.geometry.Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
	public void setWindow(Stage primaryStage,  HBox headHbox ) throws Exception {
		this.primaryWindowStage = primaryStage;
		// 添加关闭,最小化, 还原 按钮
		addTopButtonPane(primaryWindowStage);
		//界面上的子窗口的隐藏和显示按钮
//		addSubWindowCtrlBtn(operateBtnPane);
		// 添加图标
//		addTopImage(primaryWindowStage, operateBtnPane);
		this.scene = primaryWindowStage.getScene();
		// 不带系统的边框
		primaryWindowStage.initStyle(StageStyle.TRANSPARENT);

		// 主窗口的顶部 双击切换到小窗口
		headHbox.setOnMouseClicked(e->{
			// 第一次点击的时候, 记录一下主窗口的大小
			if(primaryWindowWidth < 1 ){
				primaryWindowWidth = primaryWindowStage.getWidth();
				primaryWindowHeight = primaryWindowStage.getHeight();
			}
			if (e.getClickCount() == 2) {
				Platform.runLater(()->{
					if( !smallWindowStage.isShowing()){

//						AppWindow.SQLuckyApp = smallWindowStage;
						AppHeadContainer.SQLuckyStage = smallWindowStage;
//						AppWindow.SQLuckyAppWindow.hideMenuBar();
						resize();
//						smallWindowMoveAnchorPaneChildrens();
						AppWindow.appHeadContainer.removeHiddenWindowResizeClose();
					}
				});

			}
		});

		Platform.runLater(this::createSmallWindow);
	}

	/**
	 * 创建小窗口对象
	 */
	private void createSmallWindow(){
			this.smallWindowStage = new Stage();
			this.smallWindowStage.setOnCloseRequest(CommonEventHandler.mainCloseEvent());
			this.smallWindowStage.getIcons().add(ComponentGetter.LogoIcons);
			this.smallWindowStage.setTitle("SQLucky");
			// 小窗 监控到 最大化的时候, scene放入主窗口显示按钮也显示, 小窗隐藏
			smallWindowStage.maximizedProperty().addListener((a, b, c)->{
				// 当最大化时
				if(c){
					// linux 最大化的事件触发是getWidth的值还不是最大化的值
					if (CommonUtils.isLinuxOS()) {
						smallWindowWidth = smallWindowStage.getWidth();
						smallWindowHeight = smallWindowStage.getHeight();
						System.out.println(smallWindowWidth);
						System.out.println(smallWindowHeight);
					}

					// 主窗口显示
					primaryWindowStage.setScene(this.scene);
					primaryWindowStage.show();
					primaryWindowStage.toFront();

					// 按钮显示

//					CommonButtons.hidden.setVisible(true);
//					CommonButtons.windowResize.setVisible(true);
//					CommonButtons.close.setVisible(true);

					// 小窗口隐藏
					smallWindowStage.hide();
//					AnchorPane.setRightAnchor(hideLeft, 180.0);
//					AnchorPane.setRightAnchor(hideBottom, 145.0);
//					AnchorPane.setRightAnchor(hideRight, 110.0);
//					AnchorPane.setRightAnchor(AppWindow.buttonBox, 220.0);
					AppWindow.appHeadContainer.addHiddenWindowResizeClose();
					AppHeadContainer.SQLuckyStage = primaryWindowStage;
//					AppWindow.SQLuckyAppWindow.hideMenuBar();
				}else {
//					AppWindow.SQLuckyAppWindow.hideMenuBar();
					AppHeadContainer.SQLuckyStage = smallWindowStage;
					AppWindow.appHeadContainer.removeHiddenWindowResizeClose();
				}
			});
			// windows 系统 监听窗口最大化
			if (CommonUtils.isWinOS()) {
				// 当小窗监控到最大化的时候, 保存之前的旧值, 在还原的时候使用
				smallWindowStage.widthProperty().addListener((obs, oldVal, newVal) -> {
					if(newVal.doubleValue() >= primaryWindowWidth){
						smallWindowWidth = oldVal.doubleValue();
					}
				});
				// 当小窗监控到最大化的时候, 保存之前的旧值, 在还原的时候使用
				smallWindowStage.heightProperty().addListener((obs, oldVal, newVal) -> {
					if(newVal.doubleValue() >= primaryWindowHeight){
						smallWindowHeight = oldVal.doubleValue();
					}
				});
			}
	}
	// 使用小窗口, 隐藏主窗口和按钮
	private void resize(){
			smallWindowStage.setMaximized(false);
			smallWindowStage.setScene(this.scene);

			if(smallWindowWidth > 1){
				smallWindowStage.setWidth(smallWindowWidth);
				smallWindowStage.setHeight(smallWindowHeight);

			}
		    smallWindowStage.toFront();
			smallWindowStage.show();

			// 主窗口隐藏
			primaryWindowStage.hide();
			AppWindow.appHeadContainer.removeHiddenWindowResizeClose();
//		CommonButtons.hidden.setVisible(false);
//		CommonButtons.windowResize.setVisible(false);
//		CommonButtons.close.setVisible(false);

			// 强制显示前端
//			Platform.runLater(()->{
//				smallWindowStage.toFront();
//				if(smallWindowStage.getWidth() >= primaryWindowWidth){
//					smallWindowStage.setWidth(primaryWindowWidth - 100);
//				}
//				if(smallWindowStage.getHeight() >= primaryWindowHeight){
//					smallWindowStage.setHeight(primaryWindowHeight - 100);
//				}
//			});
		// 强制显示前端
//		Platform.runLater(()->{
////			if (CommonUtils.isWinOS()) {
//				boolean tf = false;
//				double tmpW = 0.0;
//				double tmpH = 0.0;
//				if(smallWindowStage.getWidth() >= primaryWindowWidth){
//					tmpW = 1100.0;
//					tf = true;
//				}
//				if(smallWindowStage.getHeight() >= primaryWindowHeight){
//					tmpH = 900.0;
//					tf = true;
//				}
//				if(tf){
//					smallWindowStage.hide();
//					if(tmpW > 1) {
//						smallWindowStage.setWidth(tmpW);
//					}
//					if(tmpH > 1) {
//						smallWindowStage.setHeight(tmpH);
//					}
//					Platform.runLater(()->{
//						smallWindowStage.show();
//						smallWindowStage.toFront();
//					});
//				}
////			}
////			else {
////				smallWindowStage.toFront();
////			}
//
//
//		});

	}

	// 顶部左上角 图标
	private void addTopImage(Stage stage, AnchorPane operateBtnPane) {
		// 先将原来的按钮bar 移动一下
		var children1 = operateBtnPane.getChildren().getFirst();
		AnchorPane.setLeftAnchor(children1, 38.0);
		// 添加图标
		Image i = ComponentGetter.LogoIcons;
		ImageView mediaView = new ImageView(i);
		mediaView.setFitWidth(22.0);
		mediaView.setFitHeight(22.0);
		operateBtnPane.getChildren().addFirst(mediaView);
		AnchorPane.setTopAnchor(mediaView, 7.0);
		AnchorPane.setLeftAnchor(mediaView, 10.0);
	}

	// 顶部按钮面板, 添加 最小化, 重置大小, 关闭按钮
	private void addTopButtonPane(Stage stage) {
//		hidden.setGraphic(IconGenerator.svgImageCss("my-minus-square", 12.0, 1.0, "top-btn-Icon-color"));
//		hidden.getStyleClass().add("window-other-btn");
		CommonButtons.hidden.setOnAction(e -> {
			stage.setIconified(true);
		});

		// 最大化, 非最大化(还原)
//		Region windowResizeSvg = IconGenerator.svgImageCss("my-window-restore", 12, 12, "top-btn-Icon-color");
//
//		windowResize.setGraphic(windowResizeSvg);
//		CommonButtons.windowResize.getStyleClass().add("window-other-btn");

		// 还原
//		SVGPath svgRestore = new SVGPath();
//		svgRestore = (SVGPath) windowResizeSvg.getShape();
//		// 最大化
//		SVGPath	svgMax = new SVGPath();
//		var tmpMax = IconGenerator.svgImageCss("my-window-maximize", 12, 12, "top-btn-Icon-color");
//		svgMax = (SVGPath) tmpMax.getShape();

		CommonButtons.windowResize.setOnMouseClicked(e -> {
			resize();
		});
		// 关闭
//		var closeSvg = IconGenerator.svgImageCss("my-window-close", 12, 12, "top-btn-Icon-color");

//		close.getStyleClass().add("window-close-btn");
//		close.setGraphic(closeSvg);
		CommonButtons.close.setOnMouseClicked(e -> {
			Event.fireEvent(stage, new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
		});

//		operateBtnPane.getChildren().add(hidden);
//		operateBtnPane.getChildren().add(windowResize);
//		operateBtnPane.getChildren().add(close);
//
//		AnchorPane.setTopAnchor(hidden, 5.0);
//		AnchorPane.setTopAnchor(windowResize, 5.0);
//		AnchorPane.setTopAnchor(close, 5.0);
//
//		AnchorPane.setRightAnchor(hidden, 75.0);
//		AnchorPane.setRightAnchor(windowResize, 40.0);
//		AnchorPane.setRightAnchor(close, 5.0);

	}

	/**
	 * 界面上的子窗口的隐藏和显示按钮
	 * @param operateBtnPane
	 */
//	private void addSubWindowCtrlBtn(AnchorPane operateBtnPane){
//		hideLeft.setGraphic(IconGenerator.mainTabPaneClose());
//		hideLeft.setOnMouseClicked(event ->  { AppCommonAction.hideLeft();});
//		hideLeft.setTooltip(MyTooltipTool.instance("hide or show connection panel "));
//
//		// TODO hideBottom
//		hideBottom.setGraphic(IconGenerator.bottomTabPaneOpen());
//		hideBottom.setOnMouseClicked(event -> { SdkComponent.hideBottom();});
//		hideBottom.setTooltip(MyTooltipTool.instance("hide or show data panel "));
//
//		hideRight.setOnAction(event -> {
//			SdkComponent.showOrhideRight();
//		});
//		hideRight.setTooltip(MyTooltipTool.instance("Hide or Show Right Panel "));
//		operateBtnPane.getChildren().add(hideLeft);
//		operateBtnPane.getChildren().add(hideBottom);
//		operateBtnPane.getChildren().add(hideRight);
//		CommonButtons.hideLeft = hideLeft;
//		CommonButtons.hideBottom = hideBottom;
//		CommonButtons.hideRight = hideRight;
//		AnchorPane.setTopAnchor(hideLeft, 5.0);
//		AnchorPane.setTopAnchor(hideBottom, 5.0);
//		AnchorPane.setTopAnchor(hideRight, 5.0);
//
//
//		AnchorPane.setRightAnchor(hideLeft, 180.0);
//		AnchorPane.setRightAnchor(hideBottom, 145.0);
//		AnchorPane.setRightAnchor(hideRight, 110.0);
////		AnchorPane.setRightAnchor(AppWindow.buttonBox, 220.0);
//	}



}
