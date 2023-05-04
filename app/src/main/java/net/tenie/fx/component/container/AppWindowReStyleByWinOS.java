package net.tenie.fx.component.container;

import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;

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
	private final static int windowheadHeight = 20;

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
	public void start(Stage stage, AnchorPane operateBtnPane) throws Exception {
		stageX = primaryScreenBounds.getMinX() + 20;
		stageY = primaryScreenBounds.getMinY() + 20;
		stageWidth = primaryScreenBounds.getWidth() - 40;
		stageHeight = primaryScreenBounds.getHeight() - 40;

//		VBox root = new VBox(8);
		addTopButtonPane(stage, operateBtnPane);
		var scene = stage.getScene();
		var root = scene.getRoot();
//      不带系统的边框
		stage.initStyle(StageStyle.TRANSPARENT);

		// 鼠标移动事件, 记录移动的位置
		root.setOnMouseMoved(event -> {
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
				if (y <= windowheadHeight && y >= RESIZE_WIDTH) {
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
			} else if (y <= windowheadHeight && y >= RESIZE_WIDTH) {
//				cursorType = Cursor.HAND;
				cursorType = Cursor.DEFAULT;
				isWindowHead = true;
			} else {
				cursorType = Cursor.DEFAULT;
			}

			// 最后改变鼠标光标
			root.setCursor(cursorType);
		});
//		Rectangle2D screenRectangle = Screen.getPrimary().getBounds();

		root.setOnMouseDragged(event -> {
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
//					stage.setX(nextX);
//					stage.setY(nextY);
//					stage.setWidth(nextWidthTmp);
//					stage.setHeight(nextHeightTmp);
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
//					stage.setX(nextX + x);
//					stage.setY(nextY);
//
//					stage.setHeight(nextHeightTmp);
//					stage.setWidth(nextWidthTmp);
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
//					stage.setX(nextX);
//					stage.setY(nextY + y);
//					stage.setWidth(nextWidthTmp);
//					stage.setHeight(nextHeightTmp);
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
//					stage.setX(nextX + x);
//					stage.setWidth(nextWidthTmp);
//
//					stage.setY(nextY + y);
//					stage.setHeight(nextHeightTmp);
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
//					stage.setX(nextX + x);
//					stage.setWidth(nextWidthTmp);
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
//					stage.setY(nextY + y);
//					stage.setHeight(nextHeightTmp);

					setXYWH(stage,  null, nextY + y, null, nextHeightTmp);

				}, 150);
			}

			if (isWindowHead) {
				stage.setX(event.getScreenX() - xOffset);
				stage.setY(event.getScreenY() - yOffset);
			}
		});
		
		// 顶部被拖拽后
		root.setOnMousePressed(event -> {
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
		});
		
		// 顶部被双击后, 
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
	private Region hidden ;
	private Region windowResize;
	private Region close;
	private SVGPath svgRestore;
	private SVGPath svgMax;
	
	// 顶部按钮面板, 添加 最小化, 重置大小, 关闭按钮
		private void addTopButtonPane(Stage stage , AnchorPane operateBtnPane ) {

			// 最小化
		    hidden = IconGenerator.svgImageDefActive("minus-square");// IconGenerator.svgImage("window-minimize" ,
			hidden.setOnMouseClicked(e -> {
				stage.setIconified(true);
			});

			// 最大化, 非最大化
		    windowResize = IconGenerator.svgImageDefActive("window-restore");
		    
			svgRestore = new SVGPath();
			svgRestore.setContent(IconGenerator.getSvgStr("window-restore"));

			svgMax = new SVGPath();
			svgMax.setContent(IconGenerator.getSvgStr("window-maximize"));

			windowResize.setOnMouseClicked(e -> {
				optionWindowResize(stage);

			});
			// 关闭
			close = IconGenerator.svgImageDefActive("window-close");
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
		
		
		private void optionWindowResize(Stage stage) {
			if (stage.getWidth() < primaryScreenBounds.getWidth()
					&& stage.getHeight() < primaryScreenBounds.getHeight()) {
				stage.setX(primaryScreenBounds.getMinX());
				stage.setY(primaryScreenBounds.getMinY());
				stage.setWidth(primaryScreenBounds.getWidth());
				stage.setHeight(primaryScreenBounds.getHeight());

				windowResize.setShape(svgRestore);
			} else {
				stage.setX(stageX);
				stage.setY(stageY);
				stage.setWidth(stageWidth);
				stage.setHeight(stageHeight);

				windowResize.setShape(svgMax);

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
}

