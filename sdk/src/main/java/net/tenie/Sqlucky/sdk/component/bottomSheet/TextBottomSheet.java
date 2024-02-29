package net.tenie.Sqlucky.sdk.component.bottomSheet;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.component.editor.HighLightingEditorUtils;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.ui.CodeAreaHighLightingHelperForJava;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.FileTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TextBottomSheet {


    private static List<Node> switchNodeBtns(List<String > nodeNamelist ,Map<String, String> map, MyBottomSheet mtb){
        List<Node> btns = new ArrayList<>();
        for(String key : nodeNamelist){
            String val =map.get(key);
//            SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
            SqluckyEditor sqlArea =  HighLightingEditorUtils.javaEditor();
            StackPane sp = sqlArea.getCodeAreaPane(val, true);
            JFXButton tmpBtn = new JFXButton(key);
            tmpBtn.setGraphic(IconGenerator.svgImageDefActive("file-o"));
            tmpBtn.setOnAction(event -> {
                int cs =  mtb.getTabVBox().getChildren().size();
                if(cs > 1){
                    mtb.getTabVBox().getChildren().remove(1);
                }

                mtb.getTabVBox().getChildren().add(sp);
                VBox.setVgrow(sp, Priority.ALWAYS);

                for (var otherBtn : btns){
                    otherBtn.setDisable(false);
                }
                tmpBtn.setDisable(true);
            });

            btns.add(tmpBtn);
        }

        return btns;
    }
    public static MyBottomSheet multiplePaneSheet(String titleName, List<String > nodeNamelist , Map<String, String> map){
        var mtb = new MyBottomSheet(titleName);
//        mtb.setDDL(true);
//        SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
//        mtb.setSqlArea(sqlArea);


        // 表格上面的按钮, 暂时先不显示操作按钮
        mtb.getTableData().setLock(true);  // 在设置操作按钮前, 指定锁按钮的状态
//        mtb.operatePane(List.of(saveBtn(titleName, sqlArea))); // 没有操作按钮
        List<Node> btns = switchNodeBtns(nodeNamelist,map, mtb);

        mtb.operatePane(btns);
        Button firstBtn = (Button) btns.get(0);
        Platform.runLater(()->{
            firstBtn.fire();
        });
//        mtb.tabVBoxAddComponentView(sp);
//        VBox.setVgrow(sp, Priority.ALWAYS);
        mtb.show();
        return mtb;
    }

    /**
     * 展示文本页面
     * @return
     */
    public static MyBottomSheet showTextSheet(String titleName, String text, boolean allowEdit) {
        var mtb = new MyBottomSheet(titleName);
//        mtb.setDDL(true);
        SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
        StackPane sp = sqlArea.getCodeAreaPane(text, allowEdit);
        // 表格上面的按钮, 暂时先不显示操作按钮
        mtb.getTableData().setLock(true);  // 在设置操作按钮前, 指定锁按钮的状态
        mtb.operatePane(List.of(saveBtn(titleName, sqlArea))); // 没有操作按钮
        mtb.tabVBoxAddComponentView(sp);
        VBox.setVgrow(sp, Priority.ALWAYS);

        mtb.show();
        return mtb;
    }


    /**
     * 展示文本页面
     * @return
     */
    public static MyBottomSheet showJavaTextSheet(String titleName, String text, boolean allowEdit) {
        var mtb = new MyBottomSheet(titleName);
//        mtb.setDDL(true);
//        SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
        SqluckyEditor sqlArea =  HighLightingEditorUtils.javaEditor();
        StackPane sp = sqlArea.getCodeAreaPane(text, allowEdit);
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
