package net.tenie.Sqlucky.sdk.component.bottomSheet;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.component.editor.HighLightingEditorUtils;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.ui.CodeAreaHighLightingHelperForJava;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TextBottomSheet {

    /**
     * bottomSheet 内嵌一个TabPane, 来显示多个codeArea
     * @param titleName
     * @param nodeNamelist
     * @param map
     * @return
     */
    public static MyBottomSheet tabPaneSheet(String titleName, List<String > nodeNamelist , Map<String, String> map){
        var mtb = new MyBottomSheet(titleName);
        mtb.getTableData().setLock(true);  // 在设置操作按钮前, 指定锁按钮的状态
        TabPane tabPane = new TabPane();
        JFXButton saveBtn = saveBtnByTabPane(titleName, tabPane);

        mtb.operatePane(List.of(saveBtn));
        // 添加 TabPane 到bottomSheet
        mtb.getTabVBox().getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        tabPaneAddPage(nodeNamelist, map, tabPane);
        mtb.show();
        return mtb;
    }

    // 生成 Tab
    private static void tabPaneAddPage(List<String > nodeNamelist ,Map<String, String> map, TabPane tabPane){
        for(String key : nodeNamelist){
            String val = map.get(key);
            SqluckyEditor sqlArea ;
            if(key.endsWith(".java")){
                 sqlArea =  HighLightingEditorUtils.javaEditor();
            }else {
                 sqlArea =  HighLightingEditorUtils.javaEditor();
            }

            sqlArea.getCodeArea().setTitleName(key);
            StackPane sp = sqlArea.getCodeAreaPane(val, true);

            Tab  tab = new Tab(key);
            VBox tabVBox = new VBox();
            tabVBox.getChildren().add(sp);
            VBox.setVgrow(sp, Priority.ALWAYS);
            tab.setContent(tabVBox);
            tab.setUserData(sqlArea);
            tabPane.getTabs().add(tab);

        }

    }


    // 生成多个按钮, 来切换 多个 codeArea
    private static List<Node> switchNodeBtns(List<String > nodeNamelist ,Map<String, String> map, MyBottomSheet mtb){
        List<Node> btns = new ArrayList<>();
        for(String key : nodeNamelist){
            String val = map.get(key);
            SqluckyEditor sqlArea =  HighLightingEditorUtils.javaEditor();
            sqlArea.getCodeArea().setTitleName(key);
            StackPane sp = sqlArea.getCodeAreaPane(val, true);
            JFXButton tmpBtn = new JFXButton(key);

            tmpBtn.setGraphic(IconGenerator.svgImageDefActive("file-o"));
            tmpBtn.setOnAction(event -> {
                int cs =  mtb.getTabVBox().getChildren().size();
                if(cs > 1){
                    // 如果当前children 和tabVbox中取出的是同一个, 就不用切换
                    var tmpSp =  mtb.getTabVBox().getChildren().get(1);
                    if(tmpSp.equals(sp)){
                        return;
                    }else {
                        mtb.getTabVBox().getChildren().remove(1);
                    }

                }

                mtb.getTabVBox().getChildren().add(sp);
                VBox.setVgrow(sp, Priority.ALWAYS);

                for (var otherBtn : btns){
                    // 添加选择下划线
                    otherBtn.getStyleClass().remove("activated-btn");
                }
                tmpBtn.getStyleClass().add("activated-btn");
            });

            btns.add(tmpBtn);
        }

        return btns;
    }

    /**
     * 多个按钮组成的 bottomSheet, 用来切换多个codeAre, 没有tabPaneSheet好用, 弃用中
     * @param titleName
     * @param nodeNamelist
     * @param map
     * @return
     */
    @Deprecated
    public static MyBottomSheet multiplePaneSheet(String titleName, List<String > nodeNamelist , Map<String, String> map){
        var mtb = new MyBottomSheet(titleName);
        mtb.getTableData().setLock(true);  // 在设置操作按钮前, 指定锁按钮的状态
        List<Node> btns = switchNodeBtns(nodeNamelist,map, mtb);

        Button firstBtn = (Button) btns.get(0);
        Platform.runLater(()->{
            firstBtn.fire();
        });
        JFXButton saveBtn = saveBtn(titleName, mtb.getTabVBox());
        btns.add(0, saveBtn);
        mtb.operatePane(btns);
        mtb.show();
        return mtb;
    }

    /**
     * 展示文本页面
     * @return
     */
    public static MyBottomSheet showTextSheet(String titleName, String text, boolean allowEdit) {
        var mtb = new MyBottomSheet(titleName);
        SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
        StackPane sp = sqlArea.getCodeAreaPane(text, allowEdit);
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
        SqluckyEditor sqlArea =  HighLightingEditorUtils.javaEditor();
        StackPane sp = sqlArea.getCodeAreaPane(text, allowEdit);
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
                File file = FileOrDirectoryChooser.showDirChooser("Save", ComponentGetter.primaryStage); // showSaveDefault
                if (file != null) {
                    File saveFile = new File(file.getAbsolutePath(),title );
                    FileTools.save(saveFile, text);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return saveBtn;
    }


    public static JFXButton  saveBtn(String title,VBox bottomVBox) {
        JFXButton saveBtn = new JFXButton("Save");
        saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
        saveBtn.setTooltip(MyTooltipTool.instance("Save"));
        saveBtn.setOnAction(event -> {
            try {
                String text = "";
                String titleName = "";
                if( bottomVBox.getChildren().size() > 1){
                    StackPane codeAreaPane  = (StackPane) bottomVBox.getChildren().get(1);
                    VirtualizedScrollPane vsp = (VirtualizedScrollPane) codeAreaPane.getChildren().get(0);
                    MyCodeArea ca = (MyCodeArea) vsp.getContent();
                    titleName = ca.getTitleName();
                    text =  ca.getText();
                }
                if(StrUtils.isNotNullOrEmpty(text)){
                    File file = FileOrDirectoryChooser.showDirChooser("Save", ComponentGetter.primaryStage); // showSaveDefault
                    if (file != null) {
                        File saveFile = new File(file.getAbsolutePath(), titleName);
                        FileTools.save(saveFile, text);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return saveBtn;
    }

    public static JFXButton  saveBtnByTabPane(String title,TabPane tabPane) {
        JFXButton saveBtn = new JFXButton("Save");
        saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
        saveBtn.setTooltip(MyTooltipTool.instance("Save"));
        saveBtn.setOnAction(event -> {
            try {
                String text = "";
                String titleName = "";

                for(Tab tab: tabPane.getTabs()){
                    titleName = tab.getText();
                    SqluckyEditor sqlArea = (SqluckyEditor) tab.getUserData();
                    text =  sqlArea.getCodeArea().getText();
                }

                if(StrUtils.isNotNullOrEmpty(text)){
                    File file = FileOrDirectoryChooser.showDirChooser("Save", ComponentGetter.primaryStage); // showSaveDefault
                    if (file != null) {
                        File saveFile = new File(file.getAbsolutePath(), titleName);
                        FileTools.save(saveFile, text);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return saveBtn;
    }

}
