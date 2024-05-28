package net.tenie.Sqlucky.sdk.ui;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

/**
 * 自定义 stage , 记载动画,可以在stackPane上显示
 */
public class SqluckyStage {
    private Stage stage;
    private Scene scene;
    private StackPane stackPane;

    public static void initStageCss(Stage stage) {
        var sc = stage.getScene();
        CommonUtils.loadCss(sc);
        // 图标
        stage.getIcons().add(ComponentGetter.LogoIcons);
    }

    public void init(Node pane) {
            stage = new Stage();

            stackPane = new StackPane(pane);
            scene = new Scene(stackPane);
            stage.setScene(scene);

            // css
            CommonUtils.loadCss(scene);
            // 图标
            stage.getIcons().add(ComponentGetter.LogoIcons);

            stage.focusedProperty().addListener(e -> {
                if (stage.isFocused()) {
                    ComponentGetter.currentStackPane = stackPane;
                }

            });

    }

    public SqluckyStage(Node pane) {
        init(pane);
    }

    public SqluckyStage(Node pane, Modality modality) {
        init(pane);
        stage.initModality(modality);
    }

    public SqluckyStage(Node pane, String title) {
        init(pane);
        stage.setTitle(title);
    }


    public void close(){
        Platform.runLater(()->{
            stackPane.getChildren().clear();
            stackPane = null;
            scene = null;
            stage.close();
            stage = null;
        });
    }


    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public void setStackPane(StackPane stackPane) {
        this.stackPane = stackPane;
    }

}
