package net.tenie.Sqlucky.sdk.component.sheet.bottom;

import com.jfoenix.controls.JFXButton;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.component.editor.HighLightingEditorUtils;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.FileTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyBottomSheetUtility {

    // 双击treeview 表格节点, 显示表信息
    public static MyBottomSheet showTableInfoSheet(SqluckyConnector sqluckyConn, TablePo table) {
        String name = table.getTableName();
        var mtb = new MyBottomSheet(name);
        mtb.setDDL(true);
        SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
        String ddl = table.getDdl();
        sqlArea.initCodeArea(ddl, false);

        // 表格上面的按钮
        List<Node> btnLs = mtb.tableDDLOptionBtns(sqluckyConn, sqlArea, table);
        // 锁定按钮
        mtb.getTableData().setLock(true);
        mtb.operatePane(btnLs);
        mtb.tabVBoxAddComponentView(sqlArea);
        VBox.setVgrow(sqlArea, Priority.ALWAYS);

        mtb.show();
        sqlArea.getCodeArea().showFindReplaceTextBox(false, "");
        return mtb;
    }

    // 视图dll
    public static MyBottomSheet showViewDDLSheet(SqluckyConnector sqluckyConn, TablePo table) {
        String name = table.getTableName();
        var mtb = new MyBottomSheet(name);
        mtb.setDDL(true);
        SqluckyEditor sqlArea = ComponentGetter.appComponent.createCodeArea();
        String ddl = table.getDdl();
        sqlArea.initCodeArea(ddl, false);
        // 表格上面的按钮
        List<Node> btnLs = new ArrayList<>();
        JFXButton sbtn = createSelectBtn(sqluckyConn, table.getTableSchema(), name);
        btnLs.add(sbtn);
        // 锁定按钮
        mtb.getTableData().setLock(true);
        mtb.operatePane(btnLs);
        mtb.tabVBoxAddComponentView(sqlArea);
        VBox.setVgrow(sqlArea, Priority.ALWAYS);

        mtb.show();
        sqlArea.getCodeArea().showFindReplaceTextBox(false, "");
        return mtb;
    }

    /**
     * 展示文本页面
     *
     * @return
     */
    public static MyBottomSheet showJavaTextSheet(String titleName, String text, boolean allowEdit) {
        MyBottomSheet mtb = new MyBottomSheet(titleName);
        SqluckyEditor sqlArea = HighLightingEditorUtils.javaEditor();
        sqlArea.initCodeArea(text, allowEdit);
        mtb.getTableData().setLock(true);  // 在设置操作按钮前, 指定锁按钮的状态
        mtb.operatePane(List.of(saveBtn(titleName, sqlArea), copyTextBtn(text))); // 没有操作按钮
        mtb.tabVBoxAddComponentView(sqlArea);
        VBox.setVgrow(sqlArea, Priority.ALWAYS);

        mtb.show();
        return mtb;
    }


    /**
     * 展示文本页面
     *
     * @return
     */
    public static MyBottomSheet showSqlSheet(String titleName, String text, boolean allowEdit) {
        MyBottomSheet mtb = new MyBottomSheet(titleName);
        SqluckyEditor sqlArea = HighLightingEditorUtils.sqlEditor();
        sqlArea.initCodeArea(text, allowEdit);
        mtb.getTableData().setLock(true);  // 在设置操作按钮前, 指定锁按钮的状态
        mtb.operatePane(List.of(saveBtn(titleName, sqlArea), copyTextBtn(text))); // 没有操作按钮
        mtb.tabVBoxAddComponentView(sqlArea);
        VBox.setVgrow(sqlArea, Priority.ALWAYS);

        mtb.show();
        return mtb;
    }


    // 创建查询按钮
    public static JFXButton createSelectBtn(SqluckyConnector sqluckyConn, String schemaName, String tableName) {
        // 查询按钮
        JFXButton selectBtn = new JFXButton();
        selectBtn.setGraphic(IconGenerator.svgImageDefActive("windows-magnify-browse"));
        String sqlstr = sqluckyConn.getExportDDL().select20(schemaName, tableName);
        selectBtn.setTooltip(MyTooltipTool.instance("Run SQL: " + sqlstr));
        selectBtn.setOnAction(e -> {
            ComponentGetter.appComponent.runSelectSqlLockTabPane(sqluckyConn, sqlstr, 20);
        });

        return selectBtn;
    }


    public static JFXButton saveBtn(String title, SqluckyEditor sqlArea) {
        JFXButton saveBtn = new JFXButton("保存到目录");
        saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
        saveBtn.setTooltip(MyTooltipTool.instance("保存到目录"));
        saveBtn.setOnAction(event -> {
            try {
                String text = sqlArea.getCodeArea().getText();
                File file = FileOrDirectoryChooser.showDirChooser("Save", ComponentGetter.primaryStage); // showSaveDefault
                if (file != null) {
                    File saveFile = new File(file.getAbsolutePath(), title);
                    FileTools.save(saveFile, text);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return saveBtn;
    }


    public static JFXButton copyTextBtn(String text) {
        JFXButton copyBtn = new JFXButton("拷贝");
        copyBtn.setGraphic(IconGenerator.svgImageDefActive("ant-copy"));
        copyBtn.setOnAction(event -> {
            try {
                CommonUtils.setClipboardVal(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return copyBtn;
    }
}
