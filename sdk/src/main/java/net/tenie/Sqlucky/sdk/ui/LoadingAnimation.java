package net.tenie.Sqlucky.sdk.ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

import java.util.function.Consumer;

public class LoadingAnimation {
    private static Label lb;
    private static Region Animation;

    public static void ChangeLabelText(String val) {
        if (lb != null) {
            Platform.runLater(() -> {
                lb.setText(val);
            });
        }
    }

    public static void addLoading(StackPane root, String loadingString, int fontSize) {
        Platform.runLater(() -> {
            root.getChildren().get(0).setDisable(true);
            lb = new Label(loadingString);
            Animation = IconGenerator.svgImageUnactive("icomoon-spinner3", fontSize);
            CommonUtils.rotateTransition(Animation);
            lb.setGraphic(Animation);
            lb.setFont(new Font(fontSize));
            StackPane.setAlignment(lb, Pos.CENTER);
            root.setCursor(Cursor.WAIT);
            root.getChildren().add(lb);
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
    public static void rmLoading(StackPane root) {
        Platform.runLater(() -> {
            root.getChildren().remove(lb);
            root.setCursor(Cursor.DEFAULT);
            root.getChildren().get(0).setDisable(false);
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

    // 当前激活的界面StackPane 添加loading , 在子界面有效
    public static void loadingAnimation(String loadingString, Consumer<String> consumer) {
        StackPane root = ComponentGetter.currentStackPane;
        addLoading(root, loadingString, 30);
        // 后台执行
        CommonUtils.runThread(v -> {
            consumer.accept("");
            rmLoading(root);
        });
    }

}
