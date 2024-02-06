package net.tenie.Sqlucky.sdk.component;

import com.jfoenix.controls.JFXButton;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.subwindow.ImportCsvWindow;
import net.tenie.Sqlucky.sdk.subwindow.ImportExcelWindow;
import net.tenie.Sqlucky.sdk.subwindow.ImportSQLWindow;
import net.tenie.Sqlucky.sdk.subwindow.TableDataDetail;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.UiTools;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TableViewUtils;

import java.util.ArrayList;
import java.util.List;

public class MyBottomSheetButton {
    public List<Node> sqlDataOptionBtns(MyBottomSheet sheet, boolean disable, boolean isCreate) {
        List<Node> ls = new ArrayList<>();
        // 锁
        JFXButton lockbtn = sheet.getTableData().getLockBtn();
//		ls.add(lockbtn);
        if (isCreate) {

            JFXButton saveBtn = sheet.getTableData().getSaveBtn();
            saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
            saveBtn.setOnMouseClicked(e -> sheet.dataSave());
            saveBtn.setTooltip(MyTooltipTool.instance("Save data"));
            saveBtn.setDisable(true);
            // 保存按钮监听 : 保存亮起, 锁住
            saveBtn.disableProperty().addListener(e -> {
                if (!saveBtn.disableProperty().getValue()) {
                    if (sheet.getTableData().isLock()) {
                        lockbtn.setGraphic(IconGenerator.svgImageDefActive("lock"));
                    } else {
                        lockbtn.setGraphic(IconGenerator.svgImageDefActive("unlock"));

                    }
                }
            });

            JFXButton detailBtn = new JFXButton();
            detailBtn.setGraphic(IconGenerator.svgImageDefActive("search-plus"));
            detailBtn.setOnMouseClicked(e -> {
                TableDataDetail.show(sheet);
            });
            detailBtn.setTooltip(MyTooltipTool.instance("current line detail "));
            detailBtn.setDisable(disable);

            JFXButton tableSQLBtn = new JFXButton();
            tableSQLBtn.setGraphic(IconGenerator.svgImageDefActive("table"));
            tableSQLBtn.setOnMouseClicked(e -> {
                sheet.findTable();
            });
            tableSQLBtn.setTooltip(MyTooltipTool.instance("Table SQL"));
            tableSQLBtn.setDisable(disable);

            // refresh
            JFXButton refreshBtn = new JFXButton();
            refreshBtn.setGraphic(IconGenerator.svgImageDefActive("refresh"));
            refreshBtn.setOnMouseClicked(e -> {
                sheet.refreshData(sheet.getTableData().isLock());
            });
            refreshBtn.setTooltip(MyTooltipTool.instance("refresh table "));
            refreshBtn.setDisable(disable);

            // 添加一行数据
            JFXButton addBtn = new JFXButton();
            addBtn.setGraphic(IconGenerator.svgImageDefActive("plus-square"));

            addBtn.setOnMouseClicked(e -> {
                sheet.addData();
            });
            addBtn.setTooltip(MyTooltipTool.instance("add new data "));
            addBtn.setDisable(disable);

            JFXButton minusBtn = new JFXButton();
            minusBtn.setGraphic(IconGenerator.svgImage("minus-square", "#EC7774"));

            minusBtn.setOnMouseClicked(e -> {
                sheet.deleteData();
            });
            minusBtn.setTooltip(MyTooltipTool.instance("delete data "));
            minusBtn.setDisable(disable);

            // 复制一行数据
            JFXButton copyBtn = new JFXButton();
            copyBtn.setGraphic(IconGenerator.svgImageDefActive("files-o"));
            copyBtn.setOnMouseClicked(e -> {
                sheet.copyData();
            });
            copyBtn.setTooltip(MyTooltipTool.instance("copy selected row data "));
            copyBtn.setDisable(disable);

            // 独立窗口
            JFXButton dockSideBtn = new JFXButton();
            dockSideBtn.setGraphic(IconGenerator.svgImageDefActive("material-filter-none"));
            dockSideBtn.setOnMouseClicked(e -> {
                sheet.dockSide();
            });
            dockSideBtn.setTooltip(MyTooltipTool.instance("Dock side"));
            dockSideBtn.setDisable(disable);

            // excel 导入
            MenuButton importFileBtn = new MenuButton();
            importFileBtn.setGraphic(IconGenerator.svgImageDefActive("bootstrap-save-file"));
            importFileBtn.setTooltip(MyTooltipTool.instance("Import data"));
            importFileBtn.setDisable(disable);

            MenuItem excelImportBtn = new MenuItem("Import Excel");
            excelImportBtn.setGraphic(IconGenerator.svgImageDefActive("EXCEL"));
            excelImportBtn.setDisable(disable);
            excelImportBtn.setOnAction(e -> {
                ImportExcelWindow.showWindow(sheet.getTableData().getTabName(), sheet.getTableData().getConnName());

            });

            MenuItem csvImportBtn = new MenuItem("Import CSV");
            csvImportBtn.setGraphic(IconGenerator.svgImageDefActive("CSV"));
            csvImportBtn.setDisable(disable);
            csvImportBtn.setOnAction(e -> {
                ImportCsvWindow.showWindow(sheet.getTableData().getTabName(), sheet.getTableData().getConnName());

            });

            MenuItem sqlImportBtn = new MenuItem("Import Sql File");
            sqlImportBtn.setGraphic(IconGenerator.svgImageDefActive("SQL"));
            sqlImportBtn.setDisable(disable);
            sqlImportBtn.setOnAction(e -> {
                ImportSQLWindow.showWindow(sheet.getTableData().getTabName(), sheet.getTableData().getConnName());

            });

            importFileBtn.getItems().addAll(excelImportBtn, csvImportBtn, sqlImportBtn);

            // 导出
            MenuButton exportBtn = new MenuButton();
            exportBtn.setGraphic(IconGenerator.svgImageDefActive("share-square-o"));
            exportBtn.setTooltip(MyTooltipTool.instance("Export data"));
            exportBtn.setDisable(disable);

            // 导出sql
            Menu insertSQL = new Menu("Export Insert SQL Format ");
            MenuItem selected = new MenuItem("Selected Data to Clipboard ");
            selected.setOnAction(e -> sheet.InsertSQLClipboard(true, false, sheet));
            MenuItem selectedfile = new MenuItem("Selected Data to file");
            selectedfile.setOnAction(e -> sheet.InsertSQLClipboard(true, true, sheet));

            MenuItem all = new MenuItem("All Data to Clipboard ");
            all.setOnAction(e -> sheet.InsertSQLClipboard(false, false, sheet));
            MenuItem allfile = new MenuItem("All Data to file");
            allfile.setOnAction(e -> sheet.InsertSQLClipboard(false, true, sheet));

            insertSQL.getItems().addAll(selected, selectedfile, all, allfile);

            // 导出csv
            Menu csv = new Menu("Export CSV Format ");
            MenuItem csvselected = new MenuItem("Selected Data to Clipboard ");
            csvselected.setOnAction(e -> sheet.csvStrClipboard(true, false));
            MenuItem csvselectedfile = new MenuItem("Selected Data to file");
            csvselectedfile.setOnAction(e -> sheet.csvStrClipboard(true, true));

            MenuItem csvall = new MenuItem("All Data to Clipboard ");
            csvall.setOnAction(e -> sheet.csvStrClipboard(false, false));
            MenuItem csvallfile = new MenuItem("All Data to file");
            csvallfile.setOnAction(e -> sheet.csvStrClipboard(false, true));

            csv.getItems().addAll(csvselected, csvselectedfile, csvall, csvallfile);

            // 导出 excel
            Menu excel = new Menu("Export Excel ");

            // 导出选中的数据
            MenuItem excelSelected = new MenuItem("Export Selected Data ");
            excelSelected.setOnAction(e -> {
                sheet.exportExcelAction(true);
            });

            // 导出所有数据
            MenuItem excelAll = new MenuItem("Export All Data  ");
            excelAll.setOnAction(e -> {
                sheet.exportExcelAction(false);
            });

            excel.getItems().addAll(excelSelected, excelAll);

            // 导出 txt
//		Menu txt = new Menu("Export TXT Format ");
//		MenuItem txtselected = new MenuItem("Selected Data to Clipboard ");
//		txtselected.setOnAction(CommonEventHandler.txtStrClipboard(true, false));
//		MenuItem txtselectedfile = new MenuItem("Selected Data to file");
//		txtselectedfile.setOnAction(CommonEventHandler.txtStrClipboard(true, true));
//
//		MenuItem txtall = new MenuItem("All Data to Clipboard ");
//		txtall.setOnAction(CommonEventHandler.txtStrClipboard(false, false));
//		MenuItem txtallfile = new MenuItem("All Data to file");
//		txtallfile.setOnAction(CommonEventHandler.txtStrClipboard(false, true));
//
//		txt.getItems().addAll(txtselected, txtselectedfile, txtall, txtallfile);

            // 导出字段
            Menu fieldNames = new Menu("Export Table Field Name ");
            MenuItem CommaSplit = new MenuItem("Comma splitting");
            CommaSplit.setOnAction(sheet.commaSplitTableFields(sheet.getTableData()));

            MenuItem CommaSplitIncludeType = new MenuItem("Comma splitting Include Field Type");
            CommaSplitIncludeType.setOnAction(sheet.commaSplitTableFiledsIncludeType(sheet.getTableData()));

            fieldNames.getItems().addAll(CommaSplit, CommaSplitIncludeType);


            // java 代码导出

            MenuItem javaCode = new MenuItem("Export Java Bean Field");
            javaCode.setOnAction(event -> exportJavaCode(sheet));
            exportBtn.getItems().addAll(insertSQL, csv, excel, fieldNames, javaCode);





            exportBtn.setOnShowing(e -> {
                var selectedItems = sheet.getTableData().getTable().getSelectionModel().getSelectedItems(); // SqluckyBottomSheetUtility.dataTableViewSelectedItems(this);
                if (selectedItems != null && !selectedItems.isEmpty()) {
                    selected.setDisable(false);
                    selectedfile.setDisable(false);

                    csvselected.setDisable(false);
                    csvselectedfile.setDisable(false);
                    excelSelected.setDisable(false);
                } else {
                    selected.setDisable(true);
                    selectedfile.setDisable(true);

                    csvselected.setDisable(true);
                    csvselectedfile.setDisable(true);
                    excelSelected.setDisable(true);
                }
            });
            ls.add(saveBtn);
            ls.add(detailBtn);
            ls.add(tableSQLBtn);
            ls.add(refreshBtn);
            ls.add(addBtn);
            ls.add(minusBtn);
            ls.add(copyBtn);
            ls.add(dockSideBtn);
            ls.add(importFileBtn);

            ls.add(exportBtn);
        }

        // 搜索
        // 查询框
        TextField searchField = new TextField();
        searchField.getStyleClass().add("myTextField");

        AnchorPane txtAP = UiTools.textFieldAddCleanBtn(searchField);
        txtAP.setVisible(false);

        JFXButton searchBtn = new JFXButton();
        searchBtn.setGraphic(ComponentGetter.getIconDefActive("search"));
        searchBtn.setTooltip(MyTooltipTool.instance("Search "));
        searchBtn.setOnAction(e -> {
            txtAP.setVisible(!txtAP.isVisible());
        });
        TableView<ResultSetRowPo> tableView = sheet.getTableData().getDbValTable();
        ObservableList<ResultSetRowPo> items = tableView.getItems();

        // 添加过滤功能
        searchField.textProperty().addListener((o, oldVal, newVal) -> {
            if (StrUtils.isNotNullOrEmpty(newVal)) {
                TableViewUtils.tableViewAllDataFilter(tableView, items, newVal);
            } else {
                tableView.setItems(items);
            }

        });

        ls.add(searchBtn);
        ls.add(txtAP);

        return ls;
    }

    /**
     数据库字段导出为java bean字段
     */
    public void exportJavaCode( MyBottomSheet sheet){
        SheetDataValue data = sheet.getTableData();
        ObservableList<SheetFieldPo> SheetFieldPoList =  data.getColss();
        StringBuilder codeStr = new StringBuilder();
        for(var field : SheetFieldPoList){
            String className = CommonUtils.dbTypeToJavaType(field);
            String fieldName = field.getColumnLabel().get();
            codeStr.append("private ");
            codeStr.append(className.substring(className.lastIndexOf(".") + 1));
            codeStr.append(" ");
            fieldName = StrUtils.underlineCaseCamel(fieldName);
            codeStr.append(fieldName);
            codeStr.append(";\n");
        }
        CommonUtils.setClipboardVal(codeStr.toString() );
    }
}
