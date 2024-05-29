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

	public void setWindow(Stage stage1, AnchorPane operateBtnPane) throws Exception {
		this.stage = stage1;

		// 添加关闭,最小化, 还原 按钮
		addTopButtonPane(stage, operateBtnPane);
		// 添加图标
		addTopImage(stage, operateBtnPane);

		this.scene = stage.getScene();
//      不带系统的边框
		stage.initStyle(StageStyle.TRANSPARENT);

		operateBtnPane.setOnMouseClicked(e->{
			if (e.getClickCount() == 2) {
				resize();
			}
		});

	}

	private void resize(){
			stage2 = new Stage();
			stage2.setOnCloseRequest(CommonEventHandler.mainCloseEvent());

			stage2.setScene(this.scene);
			stage2.show();
			stage.hide();
			Platform.runLater(()->{

				stage2.toFront();
				hidden.setVisible(false);
				windowResize.setVisible(false);
				close.setVisible(false);
			});


			stage2.maximizedProperty().addListener((a,b,c)->{
				System.out.println("stage2 = " + c);
				if(c){
					stage.setScene(this.scene);
					stage.show();
					stage.toFront();
					hidden.setVisible(true);
					windowResize.setVisible(true);
					close.setVisible(true);
					stage2.hide();

				}
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
