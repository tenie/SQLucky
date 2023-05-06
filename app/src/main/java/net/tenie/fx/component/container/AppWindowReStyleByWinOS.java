package net.tenie.fx.component.container;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.fx.main.MyPreloader;

public class AppWindowReStyleByWinOS {

	// 窗体拉伸属性
	private static boolean isRight;// 是否处于右边界调整窗口状态
	private static boolean isBottomRight;// 是否处于右下角调整窗口状态
	private static boolean isBottomLeft;

	private static boolean isBottom;// 是否处于下边界调整窗口状态
	private static boolean isTop;
	private static boolean isLeft;
	private static boolean isTopLeft;
	private static boolean isTopRight;
	private static boolean isWindowHead; // 窗口头部
	private final static int windowheadHeight = 30;

	private final static int RESIZE_WIDTH = 3;// 判定是否为调整窗口状态的范围与边界距离
	private final static double MIN_WIDTH = 500;// 窗口最小宽度
	private final static double MIN_HEIGHT = 300;// 窗口最小高度
	private double xOffset = 0;
	private double yOffset = 0;

	// 窗口默认收缩尺寸(按钮触发)
	javafx.geometry.Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
	private Double stageX;
	private Double stageY;
	private Double stageWidth;
	private Double stageHeight;
	
	private Boolean isMaxWindow = true;  // 窗口是否最大化, (拖拽窗口有用, 最大化时不能拖拽)
	// 关闭, 最小化, 还原的图标
	private JFXButton hidden ;
	private JFXButton windowResize;
	private JFXButton close;
	private SVGPath svgRestore;
	private SVGPath svgMax;
	
	public void setWindow(Stage stage, AnchorPane operateBtnPane) throws Exception {
		stageX = primaryScreenBounds.getMinX() + 20;
		stageY = primaryScreenBounds.getMinY() + 20;
		stageWidth = primaryScreenBounds.getWidth() - 40;
		stageHeight = primaryScreenBounds.getHeight() - 40;
		
		// 添加关闭,最小化, 还原 按钮
		addTopButtonPane(stage, operateBtnPane);
	    // 添加图标
		addTopImage(stage, operateBtnPane);
		
		Scene scene = stage.getScene();
		Parent root = scene.getRoot();
//      不带系统的边框
		stage.initStyle(StageStyle.TRANSPARENT);

		// 鼠标移动事件, 记录移动的位置
		root.setOnMouseMoved(event -> {
			mouseMoved(event, stage, root);
		});
		// 鼠标拖拽, 根据拖拽移动窗口大小 或者 当在窗口顶部和非最大化时移动窗口位置
		root.setOnMouseDragged(event -> {
			mouseDragged(event, stage);
		});
		
		// 鼠标按下, 如果是在窗口顶部和窗口非最大化的时候,  记录鼠标按下时的鼠标坐标, 用于拖拽事件中移动位置
		root.setOnMousePressed(event -> {
			if(isWindowHead && isMaxWindow == false) {
				var primaryScreenBoundsWidth = primaryScreenBounds.getWidth();
				var primaryScreenBoundsHeight = primaryScreenBounds.getHeight();
				double width = stage.getWidth();
				double height = stage.getHeight();
				// 如果已经最大化, 跳过
				if(width >= primaryScreenBoundsWidth  && height >= primaryScreenBoundsHeight) {
					return;
				}
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
			
		});
		
		// 双标双击, 如果在窗口顶部双击, 对窗口最大化或非最大化
		root.setOnMouseClicked(e->{
			if(isWindowHead) {
				if(e.getClickCount() == 2) {
					if(windowResize != null) {
						optionWindowResize(stage);
					}
				}
			}
			
		});
 
	}
	
	// 顶部左上角 图标
	private void addTopImage(Stage stage , AnchorPane operateBtnPane ) {
				// 先将原来的按钮bar 移动一下
				var children1 = operateBtnPane.getChildren().get(0);
				AnchorPane.setLeftAnchor(children1, 38.0);
				// 添加图标
				Image i = ComponentGetter.LogoIcons;
		        ImageView  mediaView =  new ImageView(i);
		        mediaView.setFitWidth(22.0);
		        mediaView.setFitHeight(22.0);
		        operateBtnPane.getChildren().add(0, mediaView);
		        AnchorPane.setTopAnchor(mediaView, 5.0);
		        AnchorPane.setLeftAnchor(mediaView, 8.0);
	}
	
		// 顶部按钮面板, 添加 最小化, 重置大小, 关闭按钮
		private void addTopButtonPane(Stage stage , AnchorPane operateBtnPane ) {
			// 最小化my-minus-square
//		    hidden = IconGenerator.svgImageCss("my-minus-square",12.0, 1.0,"top-btn-Icon-color");//IconGenerator.svgImageDefActive("my-minus-square");
//			hidden.setOnMouseClicked(e -> {
//				stage.setIconified(true);
//			});

			JFXButton hidden = new JFXButton();
			hidden.setGraphic(IconGenerator.svgImageCss("my-minus-square",12.0, 1.0,"top-btn-Icon-color"));
			hidden.setOnAction(e -> {
				stage.setIconified(true);
			}); 
			
			
			// 最大化, 非最大化(还原)
			Region windowResizeSvg = IconGenerator.svgImageCss("my-window-restore",12, 12, "top-btn-Icon-color");
		    windowResize = new JFXButton();
		    windowResize.setGraphic(windowResizeSvg);
		    
		    // 还原
			svgRestore = new SVGPath();
			svgRestore = (SVGPath) windowResizeSvg.getShape();
			// 最大化
			svgMax = new SVGPath();
			var tmpMax = IconGenerator.svgImageCss("my-window-maximize",12, 12, "top-btn-Icon-color");
			svgMax = (SVGPath) tmpMax.getShape();

			windowResize.setOnMouseClicked(e -> {
				optionWindowResize(stage);

			});
			// 关闭
			var closeSvg =  IconGenerator.svgImageCss("my-window-close",12, 12, "top-btn-Icon-color");
			JFXButton close = new JFXButton();
			close.setGraphic(closeSvg);
			close.setOnMouseClicked(e -> {
				Event.fireEvent(stage, new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
			});

			operateBtnPane.getChildren().add(hidden);

			operateBtnPane.getChildren().add(windowResize);
			operateBtnPane.getChildren().add(close);

			AnchorPane.setTopAnchor(hidden, 6.0);
			AnchorPane.setTopAnchor(windowResize, 6.0);
			AnchorPane.setTopAnchor(close, 6.0);

			AnchorPane.setRightAnchor(hidden, 60.0);
			AnchorPane.setRightAnchor(windowResize, 30.0);
			AnchorPane.setRightAnchor(close, 5.0);
		}
		
		// 最大化和还原的切换
		private void optionWindowResize(Stage stage) {
			if (stage.getWidth() < primaryScreenBounds.getWidth()
					&& stage.getHeight() < primaryScreenBounds.getHeight()) {
				stage.setX(primaryScreenBounds.getMinX());
				stage.setY(primaryScreenBounds.getMinY());
				stage.setWidth(primaryScreenBounds.getWidth());
				stage.setHeight(primaryScreenBounds.getHeight());
				Region rg = (Region) windowResize.getGraphic();
				rg.setShape(svgRestore);
				isMaxWindow = true;
			} else {
				stage.setX(stageX);
				stage.setY(stageY);
				stage.setWidth(stageWidth);
				stage.setHeight(stageHeight);

				Region rg = (Region) windowResize.getGraphic();
				rg.setShape(svgMax);
				isMaxWindow = false;
			}
		}
		
		
		// 设置 x y  w h
		private void setXYWH(Stage stage, Double X , Double Y , Double W, Double H ) {
			if(X != null ) {
				stage.setX(X);
				stageX = X;
			}
				
			if(Y != null ) {
				stage.setY(Y);
				stageY = Y;
			}

			if(W != null ) {
				stage.setWidth(W);
				stageWidth = W;
			}
			if(H != null ) {
				stage.setHeight(H);
				stageHeight = H;
			}
		}
		
		// 鼠标移动记录鼠标状态
		private void mouseMoved(MouseEvent event, Stage stage, Parent root) {

			event.consume();
			// 先将所有调整窗口状态重置
			isWindowHead = isTopRight = isTopLeft = isBottomLeft = isBottomRight = isLeft = isTop = isRight = isBottom = false;
						
			double x = event.getSceneX();
			double y = event.getSceneY();
			double width = stage.getWidth();
			double height = stage.getHeight();
			
			var primaryScreenBoundsWidth = primaryScreenBounds.getWidth();
			var primaryScreenBoundsHeight = primaryScreenBounds.getHeight();
			
			// 如果已经最大化, 就不在变化尺寸的逻辑, 但是判断鼠标是不是在窗口顶部还是要的, 后面双击需要
			if(width >= primaryScreenBoundsWidth  && height >= primaryScreenBoundsHeight) {
				if (y <= windowheadHeight && y >= 0) {
					isWindowHead = true;
				}
				return;
			}

			Cursor cursorType = Cursor.DEFAULT;// 鼠标光标初始为默认类型，若未进入调整窗口状态，保持默认类型

			// 左下角调整窗口状态 isBottomLeft
			if (x <= RESIZE_WIDTH && y >= height - RESIZE_WIDTH) {
				isBottomLeft = true;
				cursorType = Cursor.NE_RESIZE;
			}
			// 右下角调整窗口状态
			else if (y >= height - RESIZE_WIDTH && x >= width - RESIZE_WIDTH) {
				isBottomRight = true;
				cursorType = Cursor.SE_RESIZE;
			} // 左上 isTopLeft
			else if (RESIZE_WIDTH >= x && RESIZE_WIDTH >= y) { // x <= RESIZE_WIDTH
				isTopLeft = true;
				cursorType = Cursor.SE_RESIZE;
			}
			// 右上 isTopRight
			else if (RESIZE_WIDTH >= y && x >= width - RESIZE_WIDTH) { // x <= RESIZE_WIDTH
				isTopRight = true;
				cursorType = Cursor.NE_RESIZE;
			}
			// 下边界调整窗口状态 isBottom
			else if (y >= height - RESIZE_WIDTH) {
				isBottom = true;
				cursorType = Cursor.S_RESIZE;
			}
			// 右边界调整窗口状态 isRight
			else if (x >= width - RESIZE_WIDTH) {
				isRight = true;
				cursorType = Cursor.E_RESIZE;
			} else if (RESIZE_WIDTH >= y) { // 上面
				cursorType = Cursor.S_RESIZE;
				isTop = true;
			} else if (RESIZE_WIDTH >= x) { // 左面
				System.out.println(x);
				cursorType = Cursor.E_RESIZE;
				isLeft = true;
			} else if (y <= windowheadHeight && y >= 0) {
//				cursorType = Cursor.HAND;
				cursorType = Cursor.DEFAULT;
				isWindowHead = true;
			} else {
				cursorType = Cursor.DEFAULT;
			}

			// 最后改变鼠标光标
			root.setCursor(cursorType);
		
		}
		
		
		private void mouseDragged(MouseEvent event, Stage stage ) {

			// 鼠标x y 坐标
			double x = event.getSceneX();
			double y = event.getSceneY();

			// 窗口的x, y ; 保存窗口改变后的x、y坐标和宽度、高度，用于预判是否会小于最小宽度、最小高度
			double nextX = stage.getX();
			double nextY = stage.getY();

			double nextWidth = stage.getWidth();
			double nextHeight = stage.getHeight();

			if (isRight || isBottomRight || isBottom) {
				if (isRight || isBottomRight) {// 所有右边调整窗口状态
					nextWidth = x;
				}
				if (isBottomRight || isBottom) {// 所有下边调整窗口状态
					nextHeight = y;
				}
				if (nextWidth <= MIN_WIDTH) {// 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
					nextWidth = MIN_WIDTH;
				}
				if (nextHeight <= MIN_HEIGHT) {// 如果窗口改变后的高度小于最小高度，则高度调整到最小高度
					nextHeight = MIN_HEIGHT;
				}
				var nextWidthTmp = nextWidth;
				var nextHeightTmp = nextHeight;

				CommonUtility.delayRunThread(s -> {
					// 最后统一改变窗口的x、y坐标和宽度、高度，可以防止刷新频繁出现的屏闪情况
					setXYWH(stage, nextX, nextY, nextWidthTmp, nextHeightTmp);
				}, 150);

			}

			if (isBottomLeft) {
				nextHeight = y;
				nextWidth = nextWidth - x;

				if (nextWidth <= MIN_WIDTH) {// 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
					return;
				}
				if (nextHeight <= MIN_HEIGHT) {// 如果窗口改变后的高度小于最小高度，则高度调整到最小高度
					return;
				}
				var nextWidthTmp = nextWidth;
				var nextHeightTmp = nextHeight;
				CommonUtility.delayRunThread(s -> {
					setXYWH(stage, nextX + x, nextY, nextWidthTmp, nextHeightTmp);
				}, 150);

			}

			if (isTopRight) {

				nextWidth = x;
				nextHeight = nextHeight - y;

				if (nextWidth <= MIN_WIDTH || nextHeight <= MIN_HEIGHT) {// 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
					return;
				}

				var nextWidthTmp = nextWidth;
				var nextHeightTmp = nextHeight;

				CommonUtility.delayRunThread(s -> {
					setXYWH(stage,nextX , nextY + y, nextWidthTmp, nextHeightTmp);
				}, 150);

			}
			if (isTopLeft) {
				nextWidth = nextWidth - x;
				nextHeight = nextHeight - y;
				if (nextWidth <= MIN_WIDTH || nextHeight <= MIN_HEIGHT) {// 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
					return;
				}

				var nextWidthTmp = nextWidth;
				var nextHeightTmp = nextHeight;
				CommonUtility.delayRunThread(s -> {
					setXYWH(stage, nextX + x , nextY + y, nextWidthTmp, nextHeightTmp);
				}, 150);
			}
			if (isLeft) {
				nextWidth = nextWidth - x;

				if (nextWidth <= MIN_WIDTH) {
					return;
				}
				var nextWidthTmp = nextWidth;
				CommonUtility.delayRunThread(s -> {
					setXYWH(stage, nextX + x , null, nextWidthTmp, null);
				}, 150);

			}

			if (isTop) {
				nextHeight = nextHeight - y;
				if (nextHeight <= MIN_HEIGHT) {// 如果窗口改变后的高度小于最小高度，则高度调整到最小高度
					nextHeight = MIN_HEIGHT;
					return;
				}
				var nextHeightTmp = nextHeight;
				CommonUtility.delayRunThread(s -> {
					setXYWH(stage,  null, nextY + y, null, nextHeightTmp);
				}, 150);
			}

			if (isWindowHead && isMaxWindow == false) {
				stage.setX(event.getScreenX() - xOffset);
				stage.setY(event.getScreenY() - yOffset);
			}
		
		}
}

