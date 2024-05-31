package net.tenie.fx.component.container;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.fx.Action.CommonEventHandler;

public class AppWindowReStyleByWinOS {
	// 关闭, 最小化, 还原的图标
	private JFXButton windowResize;
	private	Scene scene ;
	private	Stage	stage2;
	private	Stage	stage;
	private Double stage2Width = 0.0;
	private Double stage2Height = 0.0;
	private Double stageWidth = 0.0;
	private Double stageHeight = 0.0;


	// 窗口默认收缩尺寸(按钮触发)
	javafx.geometry.Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
	public void setWindow(Stage stage1, AnchorPane operateBtnPane) throws Exception {
		this.stage = stage1;
		this.stage2 = new Stage();
		this.stage2.setOnCloseRequest(CommonEventHandler.mainCloseEvent());
		this.stage2.getIcons().add(ComponentGetter.LogoIcons);
		this.stage2.setTitle("SQLucky");
		// 添加关闭,最小化, 还原 按钮
		addTopButtonPane(stage, operateBtnPane);
		// 添加图标
		addTopImage(stage, operateBtnPane);

		this.scene = stage.getScene();
//      不带系统的边框
		stage.initStyle(StageStyle.TRANSPARENT);


		// 双击切换窗口大小
		operateBtnPane.setOnMouseClicked(e->{
			// 第一次点击的时候, 记录一下最大化窗口的尺寸,
			if(stageWidth < 1 ){
				stageWidth = stage.getWidth();
				stageHeight = stage.getHeight();
			}
			if (e.getClickCount() == 2) {
				Platform.runLater(()->{
					if( !stage2.isShowing()){
						resize();
					}

				});

			}
		});
		// 小窗 监控到 最大化的时候, scene放入主窗口显示按钮也显示, 小窗隐藏
		stage2.maximizedProperty().addListener((a,b,c)->{
			System.out.println("stage2 = " + c);
			if(c){
				stage.setScene(this.scene);
				stage.show();
				stage.toFront();

				// 按钮隐藏
				hidden.setVisible(true);
				windowResize.setVisible(true);
				close.setVisible(true);

				stage2.hide();

			}
		});
		// 当小窗监控到最大化的时候, 保存之前的旧值, 在还原的时候使用
		stage2.widthProperty().addListener((obs, oldVal, newVal) -> {
			if(newVal.doubleValue() >= stageWidth){
				stage2Width = oldVal.doubleValue();
			}
		});
		// 当小窗监控到最大化的时候, 保存之前的旧值, 在还原的时候使用
		stage2.heightProperty().addListener((obs, oldVal, newVal) -> {
			if(newVal.doubleValue() >= stageHeight){
				stage2Height = oldVal.doubleValue();
			}
		});
	}
	// 使用小窗口, 隐藏主窗口和按钮
	private void resize(){
//			stage2.centerOnScreen();
			stage2.setMaximized(false);
			stage2.setScene(this.scene);

			if(stage2Width > 1){
				stage2.setWidth(stage2Width);
				stage2.setHeight(stage2Height);
			}
			stage2.show();

			// 主窗口隐藏
			stage.hide();
			hidden.setVisible(false);
			windowResize.setVisible(false);
			close.setVisible(false);

			// 强制显示前端
			Platform.runLater(()->{
				stage2.toFront();
			});

	}

	// 顶部左上角 图标
	private void addTopImage(Stage stage, AnchorPane operateBtnPane) {
		// 先将原来的按钮bar 移动一下
		var children1 = operateBtnPane.getChildren().get(0);
		AnchorPane.setLeftAnchor(children1, 38.0);
		// 添加图标
		Image i = ComponentGetter.LogoIcons;
		ImageView mediaView = new ImageView(i);
		mediaView.setFitWidth(22.0);
		mediaView.setFitHeight(22.0);
		operateBtnPane.getChildren().add(0, mediaView);
		AnchorPane.setTopAnchor(mediaView, 10.0);
		AnchorPane.setLeftAnchor(mediaView, 10.0);
	}
	JFXButton hidden = new JFXButton();
	JFXButton close = new JFXButton();
	// 顶部按钮面板, 添加 最小化, 重置大小, 关闭按钮
	private void addTopButtonPane(Stage stage, AnchorPane operateBtnPane) {
		hidden.setGraphic(IconGenerator.svgImageCss("my-minus-square", 12.0, 1.0, "top-btn-Icon-color"));
		hidden.getStyleClass().add("window-other-btn");
		hidden.setOnAction(e -> {
			stage.setIconified(true);
		});

		// 最大化, 非最大化(还原)
		Region windowResizeSvg = IconGenerator.svgImageCss("my-window-restore", 12, 12, "top-btn-Icon-color");
		windowResize = new JFXButton();
		windowResize.setGraphic(windowResizeSvg);
		windowResize.getStyleClass().add("window-other-btn");

		// 还原
		SVGPath svgRestore = new SVGPath();
		svgRestore = (SVGPath) windowResizeSvg.getShape();
		// 最大化
		SVGPath	svgMax = new SVGPath();
		var tmpMax = IconGenerator.svgImageCss("my-window-maximize", 12, 12, "top-btn-Icon-color");
		svgMax = (SVGPath) tmpMax.getShape();

		windowResize.setOnMouseClicked(e -> {
			resize();
		});
		// 关闭
		var closeSvg = IconGenerator.svgImageCss("my-window-close", 12, 12, "top-btn-Icon-color");

		close.getStyleClass().add("window-close-btn");
		close.setGraphic(closeSvg);
		close.setOnMouseClicked(e -> {
			Event.fireEvent(stage, new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
		});

		operateBtnPane.getChildren().add(hidden);

		operateBtnPane.getChildren().add(windowResize);
		operateBtnPane.getChildren().add(close);

		AnchorPane.setTopAnchor(hidden, 5.0);
		AnchorPane.setTopAnchor(windowResize, 5.0);
		AnchorPane.setTopAnchor(close, 5.0);

		AnchorPane.setRightAnchor(hidden, 75.0);
		AnchorPane.setRightAnchor(windowResize, 40.0);
		AnchorPane.setRightAnchor(close, 5.0);
	}



}
