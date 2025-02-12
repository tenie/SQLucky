package net.tenie.fx.component.container;

import SQLucky.app;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.tenie.Sqlucky.sdk.component.CommonButtons;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

/**
 * 使用自定义的壳(最大化, 最小化, 关闭), 完全不用系统的
 */
public class AppCustomizeShellFullForWinLinux {
    // 窗体拉伸属性
    // 是否处于右边界调整窗口状态
    private static boolean isRight;
    // 是否处于右下角调整窗口状态
    private static boolean isBottomRight;
    private static boolean isBottomLeft;
    // 是否处于下边界调整窗口状态
    private static boolean isBottom;
    private static boolean isTop;
    private static boolean isLeft;
    private static boolean isTopLeft;
    private static boolean isTopRight;
    // 窗口头部
    private static boolean isWindowHead;
    private final static int windowheadHeight = 30;
    // 判定是否为调整窗口状态的范围与边界距离
    private final static int RESIZE_WIDTH = 3;
    // 窗口最小宽度
    private final static double MIN_WIDTH = 500;
    // 窗口最小高度
    private final static double MIN_HEIGHT = 300;
    private double xOffset = 0;
    private double yOffset = 0;

    // 窗口默认收缩尺寸(按钮触发)
    javafx.geometry.Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    private Double stageX;
    private Double stageY;
    private Double stageWidth;
    private Double stageHeight;
//    // 窗口是否最大化, (拖拽窗口有用, 最大化时不能拖拽)
//    private Boolean isMaxWindow = true;

    // 大窗口的顶部按钮面板, 添加 最小化, 重置大小, 关闭按钮的触发事件
    private void addTopButtonAction(Stage stage) {
        // 大窗口的最小化按钮
        CommonButtons.hidden.setOnAction(e -> {
            stage.setIconified(true);
        });
        // 大窗口的重置大小按钮, 就是触发显示小窗口
        CommonButtons.windowResize.setOnAction(e -> {
            optionWindowResize(stage);
        });
        // 大窗口的关闭 按钮;
        CommonButtons.close.setOnAction(e -> {
            Event.fireEvent(stage, new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
    }

    public void setupWindow(Stage stage) throws Exception {
        stageX = primaryScreenBounds.getMinX() + 180;
        stageY = primaryScreenBounds.getMinY() + 100;
        stageWidth = primaryScreenBounds.getWidth() - 360;
        stageHeight = primaryScreenBounds.getHeight() - 200;

        addTopButtonAction(stage);
        Scene scene = stage.getScene();
        Parent root = scene.getRoot();
        // 不带系统的边框
        stage.initStyle(StageStyle.TRANSPARENT);

        // 鼠标移动事件, 记录移动的位置
        scene.setOnMouseMoved(event -> {
            mouseMoved(event, stage, root);
        });
        // 鼠标拖拽, 根据拖拽移动窗口大小 或者 当在窗口顶部和非最大化时移动窗口位置
        scene.setOnMouseDragged(event -> {
            mouseDragged(event, stage);
        });

        // 鼠标按下, 如果是在窗口顶部和窗口非最大化的时候, 记录鼠标按下时的鼠标坐标, 用于拖拽事件中移动位置
        scene.setOnMousePressed(event -> {
            if (isWindowHead && !app.isMaxWindow) {
                var primaryScreenBoundsWidth = primaryScreenBounds.getWidth();
                var primaryScreenBoundsHeight = primaryScreenBounds.getHeight();
                double width = stage.getWidth();
                double height = stage.getHeight();
                // 如果已经最大化, 跳过
                if (width >= primaryScreenBoundsWidth && height >= primaryScreenBoundsHeight) {
                    return;
                }
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }

        });

        // 双标双击, 如果在窗口顶部双击, 对窗口最大化或非最大化
        root.setOnMouseClicked(e -> {
            if (isWindowHead) {
                if (e.getClickCount() == 2) {
                    optionWindowResize(stage);
                }
            }

        });

    }

    // 多屏情况下, 获取当前的屏幕
    public static Screen getScreenForStage(Stage stage) {
        Screen targetScreen = Screen.getPrimary(); // Default to primary screen
        double maxOverlapArea = 0;

        Rectangle2D stageBounds = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());

        for (Screen screen : Screen.getScreens()) {
            Rectangle2D screenBounds = screen.getBounds();

            // Calculate the intersection (overlap) area
            double overlapX = Math.max(0, Math.min(stageBounds.getMaxX(), screenBounds.getMaxX()) - Math.max(stageBounds.getMinX(), screenBounds.getMinX()));
            double overlapY = Math.max(0, Math.min(stageBounds.getMaxY(), screenBounds.getMaxY()) - Math.max(stageBounds.getMinY(), screenBounds.getMinY()));
            double overlapArea = overlapX * overlapY;

            if (overlapArea > maxOverlapArea) {
                maxOverlapArea = overlapArea;
                targetScreen = screen;
            }
        }

        return targetScreen;
    }

    // 最大化和还原的切换
    private void optionWindowResize(Stage stage) {
        if (!app.isMaxWindow) {
            // 缓存一下没有最大化前的窗口位置
            stageWidth = stage.getWidth();
            stageHeight = stage.getHeight();
            stageX = stage.getX();
            stageY = stage.getY();
            // 最大化窗口
            stage.setMaximized(true);
            Platform.runLater(() -> {
                // 窗口位置调整
                Screen currentScreen = getScreenForStage(stage);
                Rectangle2D primaryScreenBoundsTmp = currentScreen.getVisualBounds();
                stage.setX(primaryScreenBoundsTmp.getMinX());
                stage.setY(primaryScreenBoundsTmp.getMinY());
                stage.setWidth(primaryScreenBoundsTmp.getWidth());
                stage.setHeight(primaryScreenBoundsTmp.getHeight());
            });
            app.isMaxWindow = true;
        } else {
            stage.setMaximized(false);
            stage.setX(stageX);
            stage.setY(stageY);
            stage.setWidth(stageWidth);
            stage.setHeight(stageHeight);

            app.isMaxWindow = false;
        }
    }

    // 设置 x y w h
    private void setXYWH(Stage stage, Double X, Double Y, Double W, Double H) {
        if (X != null) {
            stage.setX(X);
            stageX = X;
        }

        if (Y != null) {
            stage.setY(Y);
            stageY = Y;
        }

        if (W != null) {
            stage.setWidth(W);
            stageWidth = W;
        }
        if (H != null) {
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
        if (width >= primaryScreenBoundsWidth && height >= primaryScreenBoundsHeight) {
            if (y <= windowheadHeight && y >= 0) {
                isWindowHead = true;
            }
            return;
        }
        // 鼠标光标初始为默认类型，若未进入调整窗口状态，保持默认类型
        Cursor cursorType = Cursor.DEFAULT;

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
        else if (RESIZE_WIDTH >= x && RESIZE_WIDTH >= y) {
            isTopLeft = true;
            cursorType = Cursor.SE_RESIZE;
        }
        // 右上 isTopRight
        else if (RESIZE_WIDTH >= y && x >= width - RESIZE_WIDTH) {
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
        } else if (RESIZE_WIDTH >= y) {
            // 上面
            cursorType = Cursor.S_RESIZE;
            isTop = true;
        } else if (RESIZE_WIDTH >= x) {
            // 左面
            System.out.println(x);
            cursorType = Cursor.E_RESIZE;
            isLeft = true;
        } else if (y <= windowheadHeight) {
            isWindowHead = true;
        }

        // 最后改变鼠标光标
        root.setCursor(cursorType);

    }

    private void mouseDragged(MouseEvent event, Stage stage) {

        // 鼠标x y 坐标
        double x = event.getSceneX();
        double y = event.getSceneY();

        // 窗口的x, y ; 保存窗口改变后的x、y坐标和宽度、高度，用于预判是否会小于最小宽度、最小高度
        double nextX = stage.getX();
        double nextY = stage.getY();

        double nextWidth = stage.getWidth();
        double nextHeight = stage.getHeight();

        if (isRight || isBottomRight || isBottom) {
            // 所有右边调整窗口状态
            if (isRight || isBottomRight) {
                nextWidth = x;
            }
            // 所有下边调整窗口状态
            if (isBottomRight || isBottom) {
                nextHeight = y;
            }
            // 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
            if (nextWidth <= MIN_WIDTH) {
                nextWidth = MIN_WIDTH;
            }
            // 如果窗口改变后的高度小于最小高度，则高度调整到最小高度
            if (nextHeight <= MIN_HEIGHT) {
                nextHeight = MIN_HEIGHT;
            }
            var nextWidthTmp = nextWidth;
            var nextHeightTmp = nextHeight;

            CommonUtils.delayRunThread(s -> {
                // 最后统一改变窗口的x、y坐标和宽度、高度，可以防止刷新频繁出现的屏闪情况
                setXYWH(stage, nextX, nextY, nextWidthTmp, nextHeightTmp);
            }, 150);

        }

        if (isBottomLeft) {
            nextHeight = y;
            nextWidth = nextWidth - x;
            // 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
            if (nextWidth <= MIN_WIDTH) {
                return;
            }
            // 如果窗口改变后的高度小于最小高度，则高度调整到最小高度
            if (nextHeight <= MIN_HEIGHT) {
                return;
            }
            var nextWidthTmp = nextWidth;
            var nextHeightTmp = nextHeight;
            CommonUtils.delayRunThread(s -> {
                setXYWH(stage, nextX + x, nextY, nextWidthTmp, nextHeightTmp);
            }, 150);

        }

        if (isTopRight) {

            nextWidth = x;
            nextHeight = nextHeight - y;
            // 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
            if (nextWidth <= MIN_WIDTH || nextHeight <= MIN_HEIGHT) {
                return;
            }

            var nextWidthTmp = nextWidth;
            var nextHeightTmp = nextHeight;

            CommonUtils.delayRunThread(s -> {
                setXYWH(stage, nextX, nextY + y, nextWidthTmp, nextHeightTmp);
            }, 150);

        }
        if (isTopLeft) {
            nextWidth = nextWidth - x;
            nextHeight = nextHeight - y;
            // 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
            if (nextWidth <= MIN_WIDTH || nextHeight <= MIN_HEIGHT) {
                return;
            }

            var nextWidthTmp = nextWidth;
            var nextHeightTmp = nextHeight;
            CommonUtils.delayRunThread(s -> {
                setXYWH(stage, nextX + x, nextY + y, nextWidthTmp, nextHeightTmp);
            }, 150);
        }
        if (isLeft) {
            nextWidth = nextWidth - x;

            if (nextWidth <= MIN_WIDTH) {
                return;
            }
            var nextWidthTmp = nextWidth;
            CommonUtils.delayRunThread(s -> {
                setXYWH(stage, nextX + x, null, nextWidthTmp, null);
            }, 150);

        }

        if (isTop) {
            nextHeight = nextHeight - y;
            // 如果窗口改变后的高度小于最小高度，则高度调整到最小高度
            if (nextHeight <= MIN_HEIGHT) {
                nextHeight = MIN_HEIGHT;
                return;
            }
            var nextHeightTmp = nextHeight;
            CommonUtils.delayRunThread(s -> {
                setXYWH(stage, null, nextY + y, null, nextHeightTmp);
            }, 150);
        }

        if (isWindowHead && !app.isMaxWindow) {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        }

    }
}
