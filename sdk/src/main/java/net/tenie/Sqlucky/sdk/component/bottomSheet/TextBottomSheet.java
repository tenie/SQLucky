package net.tenie.Sqlucky.sdk.component.bottomSheet;

import com.jfoenix.controls.JFXButton;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.FileTools;

import java.io.File;
import java.util.List;

public class TextBottomSheet {

    /**
     * 展示文本页面
     * @return
     */
    public static MyBottomSheet showTextSheet(String titleName, String text, boolean allowEdit) {
        var mtb = new MyBottomSheet(titleName);
//        mtb.setDDL(true);
        SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
        mtb.setSqlArea(sqlArea);

        StackPane sp = mtb.getSqlArea().getCodeAreaPane(text, allowEdit);
        // 表格上面的按钮, 暂时先不显示操作按钮
        mtb.getTableData().setLock(true);  // 在设置操作按钮前, 指定锁按钮的状态
        mtb.operatePane(List.of(saveBtn(titleName, sqlArea))); // 没有操作按钮
        mtb.tabVBoxAddComponentView(sp);
        VBox.setVgrow(sp, Priority.ALWAYS);

        mtb.show();
        return mtb;
    }


    public static JFXButton  saveBtn(String title,  SqluckyEditor sqlArea) {
        JFXButton saveBtn = new JFXButton("Save");
        saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
        saveBtn.setTooltip(MyTooltipTool.instance("Save"));
        saveBtn.setOnAction(event -> {
            try {
                String text = sqlArea.getCodeArea().getText();
                File file = FileOrDirectoryChooser.showSaveText("Save", title, ComponentGetter.primaryStage); // showSaveDefault
                if (file != null) {
                    FileTools.save(file, text);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return saveBtn;
    }


}
