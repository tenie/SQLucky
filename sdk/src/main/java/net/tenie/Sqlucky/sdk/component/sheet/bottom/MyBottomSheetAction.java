package net.tenie.Sqlucky.sdk.component.sheet.bottom;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.*;
import net.tenie.Sqlucky.sdk.excel.ExcelDataPo;
import net.tenie.Sqlucky.sdk.excel.ExcelUtil;
import net.tenie.Sqlucky.sdk.po.*;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.subwindow.DockSideWindow;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.utility.*;
import org.controlsfx.control.tableview2.FilteredTableView;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class MyBottomSheetAction {
    /**
     * 表格数据导出到excel
     *
     * @param isSelect true 导出选中行的数据, fasle 全部导出
     */
    public static void exportExcelAction(MyBottomSheet sheet, boolean isSelect) {
        File ff = CommonUtils.getFilePathHelper("xls");
        if (ff == null) {
            return;
        }
        if (ff.exists()) {
            MyAlert.errorAlert("File Name Exist. Need A New File Name, Please!");
            return;
        }
        String filePath = ff.getAbsolutePath();
        if (!filePath.endsWith(".xls") && !filePath.endsWith(".xlsx")) {
            filePath += ".xls";
        }

        File excleFile = new File(filePath);
        LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
            ExcelDataPo po = tableValueToExcelDataPo(sheet, isSelect);
            try {
                ExcelUtil.createExcel(po, excleFile);
            } catch (Exception e1) {
                e1.printStackTrace();
                MyAlert.errorAlert("Error");
            }

        });
    }

    // table view 数据转换为excel导出的数据结构
    public static ExcelDataPo tableValueToExcelDataPo(MyBottomSheet sheet, boolean isSelect) {
        SheetDataValue tableData = sheet.getTableData();
        String tabName = tableData.getTabName();
        ObservableList<SheetFieldPo> fpos = tableData.getColss();

        ObservableList<ResultSetRowPo> rows = getValsHelper(sheet, isSelect);

        ExcelDataPo po = new ExcelDataPo();

        // 表头字段
        List<String> fields = new ArrayList<>();
        for (var fpo : fpos) {
            fields.add(fpo.getColumnLabel().get());
        }
        // 数据
        List<List<String>> datas = new ArrayList<>();
        for (var rowpo : rows) {
            List<String> rowlist = new ArrayList<>();
            ObservableList<ResultSetCellPo> cells = rowpo.getRowDatas();
            for (ResultSetCellPo cell : cells) {
                var cellval = cell.getCellData().get();
                if (cellval != null && "<null>".equals(cellval)) {
                    cellval = null;
                }
                rowlist.add(cellval);
            }
            datas.add(rowlist);

        }

        po.setSheetName(tabName);
        po.setHeaderFields(fields);
        po.setDatas(datas);

        return po;
    }

    public static void csvStrClipboard(MyBottomSheet sheet, boolean isSelected, boolean isFile) {
        File tmpFile = null;
        if (isFile) {
            tmpFile = CommonUtils.getFilePathHelper("csv");
        }
        final File ff = tmpFile;

        LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
            Thread t = new Thread() {
                @Override
                public void run() {
                    ObservableList<ResultSetRowPo> vals = MyBottomSheetAction.getValsHelper(sheet, isSelected);
                    String sql = GenerateSQLString.csvStrHelper(vals);
                    if (StrUtils.isNotNullOrEmpty(sql)) {
                        if (isFile) {
                            if (ff != null) {
                                try {
                                    FileTools.save(ff, sql);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            CommonUtils.setClipboardVal(sql);
                        }
                    }
                }
            };
            t.start();
        });
    }

    public static void InsertSQLClipboard(MyBottomSheet sheet, boolean isSelected, boolean isFile) {
        var tableData = sheet.getTableData();
        File tmpFile = null;
        if (isFile) {
            tmpFile = CommonUtils.getFilePathHelper("sql");
        }
        final File ff = tmpFile;
        LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
            Thread t = new Thread() {
                @Override
                public void run() {
                    String tableName = tableData.getTabName();// SqluckyBottomSheetUtility.getTableName(tableData);
                    final ObservableList<ResultSetRowPo> fvals = getValsHelper(sheet, isSelected);

                    String sql = GenerateSQLString.insertSQLHelper(fvals, tableName);
                    if (StrUtils.isNotNullOrEmpty(sql)) {
                        if (isFile) {
                            if (ff != null) {
                                try {
                                    FileTools.save(ff, sql);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            CommonUtils.setClipboardVal(sql);
                        }

                    }
                }
            };
            t.start();
        });

    }

    public static ObservableList<ResultSetRowPo> getValsHelper(MyBottomSheet sheet, boolean isSelected) {
        ObservableList<ResultSetRowPo> vals = null;
        if (isSelected) {
            vals = sheet.getTableData().getTable().getSelectionModel().getSelectedItems();
        } else {
            vals = sheet.getTabData();
        }
        return vals;
    }


    // 获取tree 节点中的 table 的sql
    public static void findTable(MyBottomSheet sheet) {
        RsVal rv = sheet.tableInfo();
        SqluckyConnector dbcp = rv.dbconnPo;
        if (dbcp == null) {
            return;
        }
        String tbn = rv.tableName;
        String key = "";
        int idx = tbn.indexOf(".");
        if (idx > 0) {
            key = dbcp.getConnName() + "_" + tbn.substring(0, idx);
            tbn = tbn.substring(idx + 1); // 去除schema , 得到表名
        } else {
            key = dbcp.getConnName() + "_" + dbcp.getDefaultSchema();
        }

        // 从表格缓存中查找表
        List<TablePo> tbs = TreeObjCache.getTable(key.toUpperCase());

        TablePo tbrs = null;
        for (TablePo po : tbs) {
            if (po.getTableName().toUpperCase().equals(tbn)) {
                tbrs = po;
                break;
            }
        }
        // 从试图缓存中查找
        if (tbrs == null) {
            tbs = TreeObjCache.getView(key.toUpperCase());
            for (TablePo po : tbs) {
                if (po.getTableName().toUpperCase().equals(tbn)) {
                    tbrs = po;
                    break;
                }
            }
        }
        if (tbrs != null) {
            TreeObjAction.showTableSql(dbcp, tbrs);
        } else {

            int idx2 = rv.tableName.indexOf(".");
            if (idx2 > 0) {
                String[] vales = rv.tableName.split("\\.");
                TablePo po = null;
                String tableDll = dbcp.getExportDDL().exportCreateTable(dbcp.getConn(), vales[0], vales[1]);
                if (StrUtils.isNotNullOrEmpty(tableDll)) {
                    po = new TablePo();
                    po.setTableName(vales[1]);
                    po.setTableRemarks("");
                    po.setTableSchema(vales[0]);
                    po.setTableType(CommonConst.TYPE_TABLE);
                    po.setDdl(tableDll);
                } else {
                    String viewDDL = dbcp.getExportDDL().exportCreateView(dbcp.getConn(), vales[0], vales[1]);
                    if (StrUtils.isNotNullOrEmpty(viewDDL)) {
                        po = new TablePo();
                        po.setTableName(vales[1]);
                        po.setTableRemarks("");
                        po.setTableSchema(vales[0]);
                        po.setTableType(CommonConst.TYPE_TABLE);
                        po.setDdl(viewDDL);
                    }
                }
                if (po != null) {
                    TreeObjAction.showTableSql(dbcp, po);
                }


            }
        }

    }

    /**
     * 将数据表, 独立显示
     */
    public static void dockSide(MyBottomSheet sheet) {
//        var tab = sheet;
        var tableData = sheet.getTableData();

        String tableName = CommonUtils.tabText(sheet);

        FilteredTableView<ResultSetRowPo> table = tableData.getTable();
        table.getColumns().forEach(tabCol -> {
            tabCol.setContextMenu(null);
        });

        DockSideWindow dsw = new DockSideWindow();
        VBox vb = (VBox) sheet.getContent();
        tableData.getLockBtn().setDisable(true);

        // 移除 隐藏按钮
        JFXButton hideBottom = SheetDataValue.hideBottom;
        if(sheet.getButtonAnchorPane().getChildren().contains(hideBottom)){
            sheet.getButtonAnchorPane().getChildren().remove(hideBottom);
        }
        // 移除 side 按钮
        JFXButton sideBottom = SheetDataValue.sideRightBottom;
        if(sheet.getBtnHbox().getChildren().contains(sideBottom)){
            sheet.getBtnHbox().getChildren().remove(sideBottom);
        }

        sheet.setDockSide(true);
//        isDockSide = true;
        dsw.showWindow(sheet, vb, tableName);

        TabPane dataTab = ComponentGetter.dataTabPane;
        if (dataTab.getTabs().contains(sheet)) {
            dataTab.getTabs().remove(sheet);
        }
    }

    public static void copyData(MyBottomSheet sheet) {
        var tableData = sheet.getTableData();
        // 获取当前的table view
        FilteredTableView<ResultSetRowPo> table = tableData.getTable();
        // 获取字段属性信息
        ObservableList<SheetFieldPo> fs = tableData.getColss();
        // 选中的行数据
        ObservableList<ResultSetRowPo> selectedRows = tableData.getTable().getSelectionModel().getSelectedItems();
        if (selectedRows == null || selectedRows.size() == 0) {
            return;
        }
        try {
            // 遍历选中的行
            for (int i = 0; i < selectedRows.size(); i++) {
                // 一行数据, 提醒: 最后一列是行号
                ResultSetRowPo rowPo = selectedRows.get(i);
                var rs = rowPo.getResultSet();
                ResultSetRowPo appendRow = rs.manualAppendNewRow();
                ObservableList<ResultSetCellPo> cells = rowPo.getRowDatas();
                // copy 一行
                ObservableList<StringProperty> item = FXCollections.observableArrayList();
                for (int j = 0; j < cells.size(); j++) {
                    ResultSetCellPo cellPo = cells.get(j);

                    StringProperty newsp = new SimpleStringProperty(cellPo.getCellData().get());
                    appendRow.addCell(newsp, cellPo.getDbOriginalValue(), cellPo.getField());
                    int dataType = fs.get(j).getColumnType().get();
                    CommonUtils.newStringPropertyChangeListener(newsp, dataType);
                    item.add(newsp);
                }

            }
            table.scrollTo(table.getItems().size() - 1);

            // 保存按钮亮起
            tableData.getSaveBtn().setDisable(false);
        } catch (Exception e2) {
            MyAlert.errorAlert(e2.getMessage());
        }

    }

    public static void deleteData(MyBottomSheet sheet) {
        var tableData = sheet.getTableData();
        // 获取当前的table view
        FilteredTableView<ResultSetRowPo> table = sheet.getTableData().getTable();
        String tabName = tableData.getTabName();
        Connection conn = tableData.getDbConnection().getConn();

        ObservableList<ResultSetRowPo> vals = table.getSelectionModel().getSelectedItems();
        List<ResultSetRowPo> selectRows = new ArrayList<>();
        for (var vl : vals) {
            selectRows.add(vl);
        }

        // 执行sql 后的信息 (主要是错误后显示到界面上)
        DbTableDatePo ddlDmlpo = DbTableDatePo.setExecuteInfoPo();
        Consumer<String> caller = x -> {
            Boolean showDBExecInfo = false;
            try {
                for (int i = 0; i < selectRows.size(); i++) {
                    ResultSetRowPo sps = selectRows.get(i);
                    String msg = "";
                    // 如果不是后期手动添加的行, 就不需要执行数据库删除操作
                    Boolean isNewAdd = sps.getIsNewAdd();
                    if (isNewAdd == false) {
                        showDBExecInfo = true;
                        msg = DeleteDao.execDelete(conn, tabName, sps);
                    }

                    var rs = sps.getResultSet();
                    rs.getDatas().remove(sps);
                    var fs = ddlDmlpo.getFields();
                    var row = ddlDmlpo.addRow();
                    ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
                            fs.get(0));
                    ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(msg), fs.get(1));
                    ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty("success"), fs.get(2));

                }

            } catch (Exception e1) {
                var fs = ddlDmlpo.getFields();
                var row = ddlDmlpo.addRow();
                ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
                        fs.get(0));
                ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(e1.getMessage()), fs.get(1));
                ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty("fail."), fs.get(2));
            } finally {
                if (showDBExecInfo) {
                    TableViewUtils.showInfo(ddlDmlpo);
                }
            }
        };
        if (selectRows.size() > 0) {
            MyAlert.myConfirmation("Sure to delete selected rows?", caller);
        }

    }


    // 添加一行数据
    public static void addData(MyBottomSheet sheet) {
        var tableData = sheet.getTableData();
        var tbv = sheet.getTableData().getTable();
        tbv.scrollTo(0);
        ResultSetPo rspo = tableData.getDataRs();
        ResultSetRowPo rowpo = rspo.manualAppendNewRow(0);

        ObservableList<SheetFieldPo> fs = rspo.getFields();
        for (int i = 0; i < fs.size(); i++) {
            SheetFieldPo fieldpo = fs.get(i);
            SimpleStringProperty sp = new SimpleStringProperty("");
            rowpo.addCell(sp, null, fieldpo);
        }
        Platform.runLater(() -> {
            ObservableList<ResultSetCellPo> vals = rowpo.getRowDatas();
            for (ResultSetCellPo val : vals) {
                var cel = val.getCellData();
                cel.set("<null>");
            }
        });

        // 点亮保存按钮
        tableData.getSaveBtn().setDisable(false);
    }

    // 刷新查询结果
    public static void refreshData(MyBottomSheet sheet) {
        boolean isLock = sheet.getTableData().isLock();
        var tableData = sheet.getTableData();
        String sql = tableData.getSqlStr();
        Connection conn = tableData.getDbConnection().getConn();
        String connName = tableData.getConnName();
        if (conn != null) {
            if(sheet.isDockSide()) {
                SelectExecInfo execInfo;
                try {
                    execInfo = SelectDao.selectSql2(sql, ConfigVal.MaxRows, DBConns.get(connName));
                    ObservableList<ResultSetRowPo> allRawData = execInfo.getDataRs().getDatas();
                    tableData.getTable().setItems(allRawData);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }else {
                // TODO 关闭当前tab
                var dataTab = ComponentGetter.dataTabPane;
                int selidx = dataTab.getSelectionModel().getSelectedIndex();
                SdkComponent.clearDataTable(selidx);
                ComponentGetter.appComponent.refreshDataTableView(connName, sql, selidx + "", isLock);
            }
        }
    }

    // 保存按钮逻辑
    public static void dataSave(MyBottomSheet sheet) {
        var tableData = sheet.getTableData();
        String tabName = tableData.getTabName();
        Connection conn = tableData.getDbConnection().getConn();
        SqluckyConnector dpo = tableData.getDbConnection();
        if (tabName != null && tabName.length() > 0) {
            // 待保存数据
            ObservableList<ResultSetRowPo> modifyData = tableData.getDataRs().getUpdateDatas();
            // 执行sql 后的信息 (主要是错误后显示到界面上)
            DbTableDatePo ddlDmlpo = DbTableDatePo.setExecuteInfoPo();
            boolean btnDisable = true;
            if (!modifyData.isEmpty()) {
                for (ResultSetRowPo val : modifyData) {
                    try {
                        String msg = UpdateDao.execUpdate(conn, tabName, val);

                        if (StrUtils.isNotNullOrEmpty(msg)) {
                            var fds = ddlDmlpo.getFields();
                            var row = ddlDmlpo.addRow();
                            ddlDmlpo.addData(row,
                                    CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
                                    fds.get(0));
                            ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(msg), fds.get(1));
                            ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty("success"), fds.get(2));
                        }

                    } catch (Exception e1) {
                        e1.printStackTrace();
                        btnDisable = false;
                        String msg = "failed : " + e1.getMessage();
                        msg += "\n" + dpo.translateErrMsg(msg);
                        var fds = ddlDmlpo.getFields();
                        var row = ddlDmlpo.addRow();
                        ddlDmlpo.addData(row,
                                CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())), fds.get(0));
                        ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(msg), fds.get(1));
                        ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty("failed"), fds.get(2));
                    }
                }
                rmUpdateData(sheet);
            }

            // 插入操作
            ObservableList<ResultSetRowPo> dataList = tableData.getDataRs().getNewDatas();// SqluckyBottomSheetUtility.getAppendData(tableData);
            for (ResultSetRowPo os : dataList) {
                try {
                    ObservableList<ResultSetCellPo> cells = os.getRowDatas();
                    String msg = InsertDao.execInsert(conn, tabName, cells);
                    var fds = ddlDmlpo.getFields();
                    var row = ddlDmlpo.addRow();
                    ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
                            fds.get(0));
                    ddlDmlpo.addData(row, new SimpleStringProperty(msg), fds.get(1));
                    ddlDmlpo.addData(row, new SimpleStringProperty("success"), fds.get(2));

                    // 对insert 的数据保存后 , 不能再修改
//					ObservableList<ResultSetCellPo> cells = os.getRowDatas();
                    for (int i = 0; i < cells.size(); i++) {
                        var cellpo = cells.get(i);
                        StringProperty sp = cellpo.getCellData();
                        CommonUtils.prohibitChangeListener(sp, sp.get());
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                    btnDisable = false;
                    var fs = ddlDmlpo.getFields();
                    var row = ddlDmlpo.addRow();
                    ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
                            fs.get(0));
                    ddlDmlpo.addData(row, new SimpleStringProperty(e1.getMessage()), fs.get(1));
                    ddlDmlpo.addData(row, new SimpleStringProperty("failed"), fs.get(2));
                }
            }
            // 删除缓存数据
//			SqluckyBottomSheetUtility.rmAppendData(tableData);
            tableData.getDataRs().getNewDatas().clear();

            // 保存按钮禁用
            tableData.getSaveBtn().setDisable(btnDisable);
            TableViewUtils.showInfo(ddlDmlpo);

        }

    }
    // 清空更新过的数据缓存和新加的数据缓存
    public static void rmUpdateData(MyBottomSheet sheet) {
        var tableData = sheet.getTableData();
        if (tableData != null) {
            tableData.getDataRs().getNewDatas().clear();
            tableData.getDataRs().getUpdateDatas().clear();
        }
    }

    // 导出表的字段, 使用逗号分割
    public static void commaSplitTableFields(SheetDataValue tableData) {

        LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
            ObservableList<SheetFieldPo> fs = tableData.getColss();
            Thread t = new Thread() {
                @Override
                public void run() {
                    int size = fs.size();
                    StringBuilder fieldsName = new StringBuilder("");
                    for (int i = 0; i < size; i++) {
                        SheetFieldPo po = fs.get(i);
                        String name = po.getColumnName().get();
                        fieldsName.append(name);
                        fieldsName.append(", \n");

                    }
                    if (StrUtils.isNotNullOrEmpty(fieldsName.toString())) {
                        String rsStr = fieldsName.toString().trim();
                        CommonUtils.setClipboardVal(fieldsName.substring(0, rsStr.length() - 1));
                    }
                }
            };
            t.start();

        });
    }

    // 导出表的字段包含类型, 使用逗号分割
    public static void commaSplitTableFiledsIncludeType(SheetDataValue tableData) {

        LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
            ObservableList<SheetFieldPo> fs = tableData.getColss();
            Thread t = new Thread() {
                @Override
                public void run() {
                    int size = fs.size();
                    StringBuilder fieldsName = new StringBuilder("");
                    for (int i = 0; i < size; i++) {
                        SheetFieldPo po = fs.get(i);
                        String name = po.getColumnName().get();
                        fieldsName.append(name);
                        fieldsName.append(", --");
                        fieldsName.append(po.getColumnTypeName().get());
                        fieldsName.append("\n");

                    }
                    if (StrUtils.isNotNullOrEmpty(fieldsName.toString())) {
                        CommonUtils.setClipboardVal(fieldsName.toString());
                    }
                }
            };
            t.start();
        });
    }

}
