package net.tenie.Sqlucky.sdk.ui;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 加载中的动画
 */
public class LoadingAnimation {
    private static Label lb;
    private static Region Animation;
    private static ConcurrentHashMap<StackPane, RotateTransition> stackPaneMap = new ConcurrentHashMap<>();
    public static void ChangeLabelText(String val) {
        if (lb != null) {
            Platform.runLater(() -> {
                lb.setText(val);
            });
        }
    }

    public static void addLoading(StackPane root, String loadingString, int fontSize) {
        Platform.runLater(() -> {
            try {
                if (!root.getChildren().isEmpty()) {
                    root.getChildren().get(0).setDisable(true);
                }

                lb = new Label(loadingString);
                Animation = IconGenerator.loading(fontSize); //IconGenerator.svgImageUnactive("icomoon-spinner3", fontSize);
                RotateTransition rotateTransition = CommonUtils.rotateTransition(Animation);
                stackPaneMap.put(root, rotateTransition);
//                CommonUtils.rotateTransition(Animation);
                lb.setGraphic(Animation);
                lb.setFont(new Font(fontSize));
                StackPane.setAlignment(lb, Pos.CENTER);
                root.setCursor(Cursor.WAIT);
                root.getChildren().add(lb);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }


    // 添加loading... 动画
    public static void addLoading(StackPane root) {
        addLoading(root, "Loading.....", 30);
    }

    public static void addLoading(StackPane root, String loadingString) {
        addLoading(root, loadingString, 30);

    }


    //
    public static void addLoading(String loadingString) {
        addLoading(ComponentGetter.currentStackPane, loadingString, 30);
    }

    public static void rmLoading() {
        StackPane root = ComponentGetter.currentStackPane;
        rmLoading(root);
    }


    //	移除loading...
    public static void rmLoading(StackPane stackPane) {
        Platform.runLater(() -> {
            stackPane.getChildren().remove(lb);
            stackPane.setCursor(Cursor.DEFAULT);

            // 内容页面启用
            if(! stackPane.getChildren().isEmpty()){
                stackPane.getChildren().get(0).setDisable(false);
            }
            RotateTransition rotateTransition =  stackPaneMap.remove(stackPane);
            if(rotateTransition != null){
                rotateTransition.stop();
            }
        });

    }

    public static void loadingAnimation(StackPane root, String loadingString, Consumer<String> consumer) {
        addLoading(root, loadingString, 30);
        Thread th = new Thread(() -> {
            consumer.accept("");
            rmLoading(root);
        });
        th.start();

    }

    // 主界面上加载Loading 动画
    public static void primarySceneRootLoadingAnimation(String loadingString, Consumer<String> consumer) {
        StackPane root = ComponentGetter.primarySceneRoot;
        addLoading(root, loadingString, 30);
        // 后台执行
        CommonUtils.runThread(v -> {
            consumer.accept("");
            rmLoading(root);
        });

    }

    /**
     * 在当前激活的界面 StackPane 添加loading , 在子界面有效
     * 如果没有StackPane, 会创建一个临时的透明的StackPane
     * @param loadingString
     * @param consumer
     */
    public static void loadingAnimation(String loadingString, Consumer<String> consumer) {
        Platform.runLater(() -> {
            StackPane stackPane = ComponentGetter.currentStackPane;
            SqluckyStage sqlStage = null;
            if (stackPane == null) {
                sqlStage = createTmpLoadingStackPane();
                stackPane = sqlStage.getStackPane();
            }
            addLoading(stackPane, loadingString, 30);

            var tmpstackPane = stackPane;
            var tmpSqluckyStage = sqlStage;
            // 后台执行
            CommonUtils.runThread(v -> {
                try{
                    consumer.accept("");
                }finally {
                    Platform.runLater(()->{
                        rmLoading(tmpstackPane);
                        if (tmpSqluckyStage != null) {
                            tmpSqluckyStage.close();
                        }
                    });

                }
            });
        });
    }

    /**
     * 创建临时的面板(透明), 显示加载动画
     * @return
     */
    public static SqluckyStage createTmpLoadingStackPane(){

        Pane pane = new Pane();
        pane.setPrefWidth(300);
        pane.minHeight(300);
        pane.setPrefHeight(300);
//        pane.getStyleClass().add("styled-text-area");
        pane.setStyle("-fx-background:transparent;");

        SqluckyStage sqlStage = new SqluckyStage(pane);
        sqlStage.getStackPane().setStyle("-fx-background:transparent;");
        var stage = sqlStage.getStage();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setMaximized(false);
        stage.setResizable(false);
//        stage.initStyle(StageStyle.UNDECORATED);// 设定窗口无边框
        stage.initStyle(StageStyle.TRANSPARENT);
        sqlStage.getScene().setFill(null);
        stage.show();
        return sqlStage;
    }

}
