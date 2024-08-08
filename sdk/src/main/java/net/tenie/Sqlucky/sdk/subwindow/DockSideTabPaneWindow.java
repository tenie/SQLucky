package net.tenie.Sqlucky.sdk.subwindow;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

/**
 * 数据表单独窗口
 *
 * @author tenie
 */
public class DockSideTabPaneWindow {
    private Stage stage;

    public void showWindow(TabPane tabPane, Runnable rb) {

        VBox subvb = new VBox();

        subvb.getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        layout(subvb, "", rb);

    }


    // 组件布局
    public void layout(VBox tbox, String tableName, Runnable rb) {
        tbox.setPadding(new Insets(5));
        Stage stage = CreateWindow(tbox, tableName, rb);

        stage.show();
    }

    public Stage CreateWindow(VBox vb, String title, Runnable rb) {
        SqluckyStage sqluckyStatge = new SqluckyStage(vb);
        stage = sqluckyStatge.getStage();
        ComponentGetter.dockSideTabPaneWindow = stage;
        Scene scene = sqluckyStatge.getScene();

        vb.getStyleClass().add("connectionEditor");

        vb.setPrefWidth(1000);
        vb.setPrefHeight(600);
        AnchorPane bottomPane = new AnchorPane();
        bottomPane.setPadding(new Insets(10));

        vb.getChildren().add(bottomPane);
        KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
        KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
        scene.getAccelerators().put(escbtn, () -> {
            stage.close();
        });
        scene.getAccelerators().put(spacebtn, () -> {
            stage.close();
        });

        stage.setTitle(title);
        CommonUtils.loadCss(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        stage.setOnCloseRequest( event -> {
            rb.run();
            ComponentGetter.dockSideTabPaneWindow = null;
        });
        return stage;
    }

}
