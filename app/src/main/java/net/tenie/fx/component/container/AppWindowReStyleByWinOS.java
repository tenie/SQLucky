package net.tenie.fx.component.container;

import SQLucky.app;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.tenie.Sqlucky.sdk.component.CommonButtons;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

public class AppWindowReStyleByWinOS {
    private Scene scene;

    private Stage smallWindowStage;
    private Stage primaryWindowStage;

    private static Double smallWindowWidth = 1200.0;
    private static Double smallWindowHeight = 680.0;
    private Double primaryWindowWidth = 0.0;
    private Double primaryWindowHeight = 0.0;

    // 设置窗口的事件
    public void setupWindow(Stage primaryStage, HBox headHbox) throws Exception {
        this.primaryWindowStage = primaryStage;
        // 添加关闭,最小化, 还原 按钮的点击事件
        addTopButtonAction(primaryWindowStage);

        this.scene = primaryWindowStage.getScene();
        // 不带系统的边框
        primaryWindowStage.initStyle(StageStyle.TRANSPARENT);

        // 主窗口的顶部 双击切换到小窗口
        headHbox.setOnMouseClicked(e -> {
            // 第一次点击的时候, 记录一下主窗口的大小
            if (primaryWindowWidth < 1) {
                primaryWindowWidth = primaryWindowStage.getWidth();
                primaryWindowHeight = primaryWindowStage.getHeight();
            }
            // 主窗口, 顶部双击, 切换为小窗口
            if (e.getClickCount() == 2) {
                Platform.runLater(() -> {
                    if (!smallWindowStage.isShowing()) {
                        // 显示小窗
                        showSmallStage();
                        // 移除自定义的 关闭,最小化, 重置大小按钮
                        AppWindow.appHeadContainer.removeHiddenWindowResizeClose();
                    }
                });
            }
        });
        // 创建小窗
        Platform.runLater(this::createSmallWindow);
    }

    /**
     * 创建小窗口对象
     */
    private void createSmallWindow() {
        this.smallWindowStage = new Stage();
        this.smallWindowStage.setOnCloseRequest(e -> {
            // 主窗口关闭事件处理逻辑
            app.saveApplicationStatusInfo();
        });
        this.smallWindowStage.getIcons().add(ComponentGetter.LogoIcons);
        this.smallWindowStage.setTitle("SQLucky");
        // 设置最大化事件
        setupSmallMaximizedProperty();
        // 小窗 长度/宽度的监听
        setupSmallWidtHeightProperty();

    }

    // 设置最大化事件
    private void setupSmallMaximizedProperty() {
        // 小窗 监控到 最大化的时候, scene放入主窗口显示按钮也显示, 小窗隐藏
        smallWindowStage.maximizedProperty().addListener((a, b, isMax) -> {
            // 当最大化时
            if (isMax) {
                // linux 最大化的事件触发是getWidth的值还不是最大化的值
                if (CommonUtils.isLinuxOS()) {
                    smallWindowWidth = smallWindowStage.getWidth();
                    smallWindowHeight = smallWindowStage.getHeight();
                }
                // 创建一个新的最大化的窗口
                app.initStage(this.scene);
                // 小窗口隐藏 , 直接销毁
                Platform.runLater(() -> {
                    app.destroyStage(smallWindowStage);
                    smallWindowStage = null;
                    app.destroyStage(primaryWindowStage);
                    primaryWindowStage = null;
                });

            } else {
                AppHeadContainer.SQLuckyStage = smallWindowStage;
                AppWindow.appHeadContainer.removeHiddenWindowResizeClose();
            }
        });
    }

    // 小窗 长度/宽度的监听
    private void setupSmallWidtHeightProperty() {
        // windows 系统 监听窗口最大化
        if (CommonUtils.isWinOS()) {
            // 当小窗监控到最大化的时候, 保存之前的旧值, 在还原的时候使用
            smallWindowStage.widthProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() >= primaryWindowWidth) {
                    smallWindowWidth = oldVal.doubleValue();
                }
            });
            // 当小窗监控到最大化的时候, 保存之前的旧值, 在还原的时候使用
            smallWindowStage.heightProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() >= primaryWindowHeight) {
                    smallWindowHeight = oldVal.doubleValue();
                }
            });
        }
    }

    // 使用小窗口, 隐藏主窗口和按钮
    private void showSmallStage() {
        smallWindowStage.setMaximized(false);
        smallWindowStage.setScene(this.scene);

        if (smallWindowWidth > 1) {
            smallWindowStage.setWidth(smallWindowWidth);
            smallWindowStage.setHeight(smallWindowHeight);
        }
        smallWindowStage.toFront();
        smallWindowStage.show();
        AppHeadContainer.SQLuckyStage = smallWindowStage;
        AppWindow.appHeadContainer.removeHiddenWindowResizeClose();

        Platform.runLater(() -> {
            // 主窗口 直接销毁
            app.destroyStage(primaryWindowStage);
            primaryWindowStage = null;
        });
    }

    // 大窗口的顶部按钮面板, 添加 最小化, 重置大小, 关闭按钮的触发事件
    private void addTopButtonAction(Stage stage) {
        // 大窗口的最小化按钮
        CommonButtons.hidden.setOnAction(e -> {
            stage.setIconified(true);
        });
        // 大窗口的重置大小按钮, 就是触发显示小窗口
        CommonButtons.windowResize.setOnAction(e -> {
            showSmallStage();
        });
        // 大窗口的关闭 按钮;
        CommonButtons.close.setOnAction(e -> {
            Event.fireEvent(stage, new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
    }
}
