package net.tenie.Sqlucky.sdk.component.sheet.bottom;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.CommonButtons;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.*;
import net.tenie.Sqlucky.sdk.excel.ExcelDataPo;
import net.tenie.Sqlucky.sdk.excel.ExcelUtil;
import net.tenie.Sqlucky.sdk.po.*;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.subwindow.DockSideTabPaneWindow;
import net.tenie.Sqlucky.sdk.subwindow.DockSideWindow;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.utility.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableView;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author tenie
 */
public class MyBottomSheetAction {
    private static final Logger logger = LogManager.getLogger(MyBottomSheetAction.class);
    /**
     * 表格数据导出到excel
     *
     * @param isSelect true 导出选中行的数据, false 全部导出
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
                logger.error(e1);
                MyAlert.errorAlert("Error");
            }

        });
    }

    // table view 数据转换为excel导出的数据结构
    public static ExcelDataPo tableValueToExcelDataPo(MyBottomSheet sheet, boolean isSelect) {
        SheetDataValue tableData = sheet.getTableData();
        String tabName = tableData.getTabName();
        ObservableList<SheetFieldPo> sheetFieldPoList = tableData.getColss();

        ObservableList<ResultSetRowPo> rows = getValsHelper(sheet, isSelect);

        ExcelDataPo po = new ExcelDataPo();

        // 表头字段
        List<String> fields = new ArrayList<>();
        for (var fpo : sheetFieldPoList) {
            fields.add(fpo.getColumnLabel().get());
        }
        // 数据
        List<List<String>> dataList = new ArrayList<>();
        for (var rowPo : rows) {
            List<String> rowlist = new ArrayList<>();
            ObservableList<ResultSetCellPo> cells = rowPo.getRowDatas();
            for (ResultSetCellPo cell : cells) {
                var cellVal = cell.getCellData().get();
                if ("<null>".equals(cellVal)) {
                    cellVal = null;
                }
                rowlist.add(cellVal);
            }
            dataList.add(rowlist);

        }

        po.setSheetName(tabName);
        po.setHeaderFields(fields);
        po.setDatas(dataList);

        return po;
    }

    public static void csvStrClipboard(MyBottomSheet sheet, boolean isSelected, boolean isFile) {
        File tmpFile = null;
        if (isFile) {
            tmpFile = CommonUtils.getFilePathHelper("csv");
        }
        final File ff = tmpFile;

        LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
            Thread t = new Thread(() -> {
                ObservableList<ResultSetRowPo> vals = MyBottomSheetAction.getValsHelper(sheet, isSelected);
                String sql = GenerateSQLString.csvStrHelper(vals);
                if (StrUtils.isNotNullOrEmpty(sql)) {
                    if (isFile) {
                        if (ff != null) {
                            try {
                                FileTools.save(ff, sql);
                            } catch (IOException e) {
                               logger.error(e);
                            }
                        }
                    } else {
                        CommonUtils.setClipboardVal(sql);
                    }
                }
            });
            t.start();
        });
    }

    public static void insertSqlClipboard(MyBottomSheet sheet, boolean isSelected, boolean isFile) {
        var tableData = sheet.getTableData();
        File tmpFile = null;
        if (isFile) {
            tmpFile = CommonUtils.getFilePathHelper("sql");
        }
        final File ff = tmpFile;
        LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
            Thread t = new Thread(() -> {
                String tableName = tableData.getTabName();
                final ObservableList<ResultSetRowPo> rowPoList = getValsHelper(sheet, isSelected);

                String sql = GenerateSQLString.insertSQLHelper(rowPoList, tableName);
                if (StrUtils.isNotNullOrEmpty(sql)) {
                    if (isFile) {
                        if (ff != null) {
                            try {
                                FileTools.save(ff, sql);
                            } catch (IOException e) {
                                logger.error(e);
                            }
                        }
                    } else {
                        CommonUtils.setClipboardVal(sql);
                    }

                }
            });
            t.start();
        });

    }

    public static ObservableList<ResultSetRowPo> getValsHelper(MyBottomSheet sheet, boolean isSelected) {
        ObservableList<ResultSetRowPo> vals;
        if (isSelected) {
            vals = sheet.getTableData().getTable().getSelectionModel().getSelectedItems();
        } else {
            vals = sheet.getTabData();
        }
        return vals;
    }


    // 获取tree 节点中的 table 的sql
    public static void findTable(SqluckyConnector sqluckyConnector, String tableName ) {
        if (sqluckyConnector == null) {
            return;
        }

        boolean connError = sqluckyConnector.checkSqluckyConnector();
        if(connError){
            return;
        }
        if(tableName.contains("`")){
            tableName = tableName.replaceAll("`", "");
        }

        String tbn = tableName;
        String key ;
        int idx = tbn.indexOf(".");
        if (idx > 0) {
            key = sqluckyConnector.getConnName() + "_" + tbn.substring(0, idx);
            // 去除schema , 得到表名
            tbn = tbn.substring(idx + 1);
        } else {
            key = sqluckyConnector.getConnName() + "_" + sqluckyConnector.getDefaultSchema();
        }

        // 从表格缓存中查找表
        List<TablePo> tableList = TreeObjCache.getTable(key.toUpperCase());

        TablePo tablePo = null;
        tbn = tbn.toUpperCase();
        for (TablePo po : tableList) {
            if (po.getTableName().toUpperCase().equals(tbn)) {
                tablePo = po;
                break;
            }
        }
        // 从试图缓存中查找
        if (tablePo == null) {
            tableList = TreeObjCache.getView(key.toUpperCase());
            for (TablePo po : tableList) {
                if (po.getTableName().toUpperCase().equals(tbn)) {
                    tablePo = po;
                    break;
                }
            }
        }
        // 找到了, 显示建表语句
        if (tablePo != null) {
            TreeObjAction.showTableSql(sqluckyConnector, tablePo);
        } else {
            // 没找到的情况
            int idx2 = tableName.indexOf(".");
            if (idx2 > 0) {
                String[] vales = tableName.split("\\.");
                TablePo po = queryDbTableDdl(sqluckyConnector, vales[0], vales[1]);
                if (po != null) {
                    TreeObjAction.showTableSql(sqluckyConnector, po);
                }


            }
        }

    }

    /**
     * 导出
     */
    public  static TablePo queryDbTableDdl(SqluckyConnector dbcp, String schema, String tableName){
        TablePo po = null;
        String tableDll = dbcp.getExportDDL().exportCreateTable(dbcp.getConn(), schema, tableName);
        if (StrUtils.isNotNullOrEmpty(tableDll)) {
            po = new TablePo();
            po.setTableName(tableName);
            po.setTableRemarks("");
            po.setTableSchema(schema);
            po.setTableType(CommonConst.TYPE_TABLE);
            po.setDdl(tableDll);
        } else {
            String viewDdl = dbcp.getExportDDL().exportCreateView(dbcp.getConn(), schema, tableName);
            if (StrUtils.isNotNullOrEmpty(viewDdl)) {
                po = new TablePo();
                po.setTableName(tableName);
                po.setTableRemarks("");
                po.setTableSchema(schema);
                po.setTableType(CommonConst.TYPE_TABLE);
                po.setDdl(viewDdl);
            }
        }
        return po;
    }

    /**
     * 将 dataTabPane, 独立显示
     */
    public static void dockSideTabPane() {
        // 隐藏 数据窗口, 禁用隐藏按钮
        JFXButton btn = CommonButtons.hideBottom;
        SdkComponent.hideShowBottomHelper(false);
        btn.setDisable(true);

        // 主界面 移除 tabPane, 把tabPane放入独立窗口
        TabPane tabPane =  ComponentGetter.dataTabPane;
        ComponentGetter.tabPanContainer.getChildren().remove(tabPane);

        // 移除不需要的按钮
        JFXButton sideRightBottomBtn = SheetDataValue.sideRightBottom;
        JFXButton hideBottom = SheetDataValue.hideBottom;
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        MyBottomSheet tmpSheet;
        if(tab instanceof  MyBottomSheet sheet ){
            // 移除按钮
            sheet.getBtnHbox().getChildren().remove(sideRightBottomBtn);
            sheet.getBtnHbox().getChildren().remove(hideBottom);
            sheet.getButtonAnchorPane().getChildren().remove(hideBottom);
            tmpSheet = sheet;
        } else {
            tmpSheet = null;
        }

        // 实例化独立窗口
        DockSideTabPaneWindow dsw = new DockSideTabPaneWindow();
        dsw.showWindow(tabPane, ()->{
            // 关闭独立窗口的时候, 将tabPane 放入主界面
            ComponentGetter.tabPanContainer.getChildren().add(tabPane);
            btn.setDisable(false);

            // 添加 sideRightBottom 到当前 MyBottomSheet
            if(tmpSheet != null){
                if( ! tmpSheet.getBtnHbox().getChildren().contains(sideRightBottomBtn)){
                    tmpSheet.getBtnHbox().getChildren().addFirst(sideRightBottomBtn);
                }
            }
            Platform.runLater(SdkComponent::hideBottom);

        });
    }


    /**
     * 将数据展示Pan 独立显示
     */
    public static void dockSide(MyBottomSheet sheet) {
//        var tab = sheet;
        var tableData = sheet.getTableData();

        String tableName = CommonUtils.tabText(sheet);

        FilteredTableView<ResultSetRowPo> table = tableData.getTable();
        table.getColumns().forEach(tabCol ->  tabCol.setContextMenu(null));

        DockSideWindow dsw = new DockSideWindow();
        VBox vb = (VBox) sheet.getContent();
        tableData.getLockBtn().setDisable(true);

        // 移除 隐藏按钮
        JFXButton hideBottom = SheetDataValue.hideBottom;
        sheet.getButtonAnchorPane().getChildren().remove(hideBottom);
        // 移除 side 按钮
        JFXButton sideBottom = SheetDataValue.sideRightBottom;
        sheet.getBtnHbox().getChildren().remove(sideBottom);

        sheet.setDockSide(true);
//        isDockSide = true;
        dsw.showWindow(sheet, vb, tableName);

        TabPane dataTab = ComponentGetter.dataTabPane;
        dataTab.getTabs().remove(sheet);
    }

    /**
     * 复制一行数据
     */
    public static void copyData(MyBottomSheet sheet) {
        var tableData = sheet.getTableData();
        // 获取当前的table view
        FilteredTableView<ResultSetRowPo> table = tableData.getTable();
        // 获取字段属性信息
        ObservableList<SheetFieldPo> fs = tableData.getColss();
        // 选中的行数据
        ObservableList<ResultSetRowPo> selectedRows = tableData.getTable().getSelectionModel().getSelectedItems();
        if (selectedRows == null || selectedRows.isEmpty()) {
            return;
        }
        try {
            // 遍历选中的行
            for (ResultSetRowPo rowPo : selectedRows) {
                // 一行数据, 提醒: 最后一列是行号
                var rs = rowPo.getResultSet();
                ResultSetRowPo appendRow = rs.manualAppendNewRow();
                ObservableList<ResultSetCellPo> cells = rowPo.getRowDatas();
                // copy 一行
                ObservableList<StringProperty> item = FXCollections.observableArrayList();
                for (int j = 0; j < cells.size(); j++) {
                    ResultSetCellPo cellPo = cells.get(j);

                    StringProperty newStrPro = new SimpleStringProperty(cellPo.getCellData().get());
                    appendRow.addCell(newStrPro, cellPo.getDbOriginalValue(), cellPo.getField());
                    int dataType = fs.get(j).getColumnType().get();
                    CommonUtils.newStringPropertyChangeListener(newStrPro, dataType);
                    item.add(newStrPro);
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
        List<ResultSetRowPo> selectRows = new ArrayList<>(vals);

        // 执行sql 后的信息 (主要是错误后显示到界面上)
        DbTableDatePo ddlDmlPo = DbTableDatePo.setExecuteInfoPo();
        Consumer<String> caller = x -> {
            boolean showDbExecInfo = false;
            try {
                for (ResultSetRowPo sps : selectRows) {
                    String msg = "";
                    // 如果不是后期手动添加的行, 就不需要执行数据库删除操作
                    Boolean isNewAdd = sps.getIsNewAdd();
                    if (!isNewAdd) {
                        showDbExecInfo = true;
                        msg = DeleteDao.execDelete(conn, tabName, sps);
                    }

                    var rs = sps.getResultSet();
                    rs.getDatas().remove(sps);
                    var fs = ddlDmlPo.getFields();
                    var row = ddlDmlPo.addRow();
                    ddlDmlPo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
                            fs.get(0));
                    ddlDmlPo.addData(row, CommonUtils.createReadOnlyStringProperty(msg), fs.get(1));
                    ddlDmlPo.addData(row, CommonUtils.createReadOnlyStringProperty("success"), fs.get(2));

                }

            } catch (Exception e1) {
                var fs = ddlDmlPo.getFields();
                var row = ddlDmlPo.addRow();
                ddlDmlPo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
                        fs.get(0));
                ddlDmlPo.addData(row, CommonUtils.createReadOnlyStringProperty(e1.getMessage()), fs.get(1));
                ddlDmlPo.addData(row, CommonUtils.createReadOnlyStringProperty("fail."), fs.get(2));
            } finally {
                if (showDbExecInfo) {
                    TableViewUtils.showInfo(ddlDmlPo);
                }
            }
        };
        if (!selectRows.isEmpty()) {
            MyAlert.myConfirmation("Sure to delete selected rows?", caller);
        }

    }


    // 添加一行数据
    public static void addData(MyBottomSheet sheet) {
        var tableData = sheet.getTableData();
        var tbv = sheet.getTableData().getTable();
        tbv.scrollTo(0);
        ResultSetPo resultSetPo = tableData.getDataRs();
        ResultSetRowPo rowPo = resultSetPo.manualAppendNewRow(0);

        ObservableList<SheetFieldPo> fs = resultSetPo.getFields();
        for (SheetFieldPo fieldPo : fs) {
            SimpleStringProperty sp = new SimpleStringProperty("");
            rowPo.addCell(sp, null, fieldPo);
        }
        Platform.runLater(() -> {
            ObservableList<ResultSetCellPo> vals = rowPo.getRowDatas();
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
        int pageStart = tableData.getPageStart();

        if(pageStart >= ConfigVal.MaxRows){
            pageStart -=  ConfigVal.MaxRows;
        }
        String sql = tableData.getSqlStr();
        Connection conn = tableData.getDbConnection().getConn();
        String connName = tableData.getConnName();
        if (conn != null) {
            if(sheet.isDockSide()) {
                SelectExecInfo execInfo;
                try {
                    execInfo = SelectDao.selectSql(sql,pageStart, ConfigVal.MaxRows, DBConns.get(connName), ParseSQL.SELECT);
                    ObservableList<ResultSetRowPo> allRawData = execInfo.getDataRs().getDatas();
                    tableData.getTable().setItems(allRawData);
                } catch (Exception e) {
                    logger.error(e);
                }
            }else {
                // 关闭当前tab
                var dataTab = ComponentGetter.dataTabPane;
                int selectIdx = dataTab.getSelectionModel().getSelectedIndex();
                SdkComponent.clearDataTable(selectIdx);

                ComponentGetter.appComponent.refreshDataTableViewByPage(connName, sql, selectIdx + "", isLock, pageStart);
            }
        }
    }


    // 刷新查询结果
    public static void nextData(MyBottomSheet sheet) {
        boolean isLock = sheet.getTableData().isLock();
        var tableData = sheet.getTableData();
        int pageStart = tableData.getPageStart();
        String sql = tableData.getSqlStr();
        Connection conn = tableData.getDbConnection().getConn();
        String connName = tableData.getConnName();
        if (conn != null) {
            if(sheet.isDockSide()) {
                SelectExecInfo execInfo;
                try {
                    execInfo = SelectDao.selectSql(sql, pageStart, ConfigVal.MaxRows, DBConns.get(connName) ,  ParseSQL.SELECT);
                    // 设置分页, 查询结果和limit相等就设置其实页
                    if(execInfo.getRowSize() ==  ConfigVal.MaxRows){
                        tableData.setPageStart(pageStart+ ConfigVal.MaxRows);
                    }

                    ObservableList<ResultSetRowPo> allRawData = execInfo.getDataRs().getDatas();
                    tableData.getTable().setItems(allRawData);
                } catch (Exception e) {
                    logger.error(e);
                }
            }else {
                // 关闭当前tab
                var dataTab = ComponentGetter.dataTabPane;
                int selectIdx = dataTab.getSelectionModel().getSelectedIndex();
                SdkComponent.clearDataTable(selectIdx);
                ComponentGetter.appComponent.refreshDataTableViewByPage(connName, sql, selectIdx + "", isLock,pageStart);

            }
        }
    }



    // 刷新查询结果
    public static void prePageData(MyBottomSheet sheet) {
        boolean isLock = sheet.getTableData().isLock();
        var tableData = sheet.getTableData();
        int pageStart = tableData.getPageStart();
        // pageStart
        if(pageStart>0 ){
            // 因为 第一次查询会字段加到下一页的值, 所以, 要减2次才能到当前页的下一页位置
            pageStart = pageStart- ConfigVal.MaxRows*2 ;
        }
        if(pageStart<0){// 减成负数, 回到第一页
            pageStart = 0;
        }
        String sql = tableData.getSqlStr();
        Connection conn = tableData.getDbConnection().getConn();
        String connName = tableData.getConnName();
        if (conn != null) {
            if(sheet.isDockSide()) {
                SelectExecInfo execInfo;
                try {
                    execInfo = SelectDao.selectSql(sql, pageStart, ConfigVal.MaxRows, DBConns.get(connName) ,  ParseSQL.SELECT);
                    // 设置分页, 查询结果和limit相等就设置其实页
                    if(execInfo.getRowSize() ==  ConfigVal.MaxRows){
                        tableData.setPageStart(pageStart+ ConfigVal.MaxRows);
                    }

                    ObservableList<ResultSetRowPo> allRawData = execInfo.getDataRs().getDatas();
                    tableData.getTable().setItems(allRawData);
                } catch (Exception e) {
                    logger.error(e);
                }
            }else {
                // 关闭当前tab
                var dataTab = ComponentGetter.dataTabPane;
                int selectIdx = dataTab.getSelectionModel().getSelectedIndex();
                SdkComponent.clearDataTable(selectIdx);
                ComponentGetter.appComponent.refreshDataTableViewByPage(connName, sql, selectIdx + "", isLock,pageStart);
            }
        }
    }

    // 保存按钮逻辑
    public static boolean dataSave(MyBottomSheet sheet) {
        var tableData = sheet.getTableData();
        String tabName = tableData.getTabName();
        Connection conn = tableData.getDbConnection().getConn();
        SqluckyConnector dpo = tableData.getDbConnection();
        boolean hasError = false;
        if (tabName != null && !tabName.isEmpty()) {
            // 待保存数据
            ObservableList<ResultSetRowPo> modifyData = tableData.getDataRs().getUpdateDatas();
            // 执行sql 后的信息 (主要是错误后显示到界面上)
            DbTableDatePo ddlDmlPo = DbTableDatePo.setExecuteInfoPo();
            boolean btnDisable = true;
            if (!modifyData.isEmpty()) {
                for (ResultSetRowPo val : modifyData) {
                    try {
                        String msg = UpdateDao.execUpdate(conn, tabName, val);

                        if (StrUtils.isNotNullOrEmpty(msg)) {
                            var fds = ddlDmlPo.getFields();
                            var row = ddlDmlPo.addRow();
                            ddlDmlPo.addData(row,
                                    CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
                                    fds.get(0));
                            ddlDmlPo.addData(row, CommonUtils.createReadOnlyStringProperty(msg), fds.get(1));
                            ddlDmlPo.addData(row, CommonUtils.createReadOnlyStringProperty("success"), fds.get(2));
                        }

                    } catch (Exception e1) {
                        logger.error(e1);
                        hasError = true;
                        btnDisable = false;
                        String msg = "failed : " + e1.getMessage();
                        msg += "\n" + dpo.translateErrMsg(msg);
                        var fds = ddlDmlPo.getFields();
                        var row = ddlDmlPo.addRow();
                        ddlDmlPo.addData(row,
                                CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())), fds.get(0));
                        ddlDmlPo.addData(row, CommonUtils.createReadOnlyStringProperty(msg), fds.get(1));
                        ddlDmlPo.addData(row, CommonUtils.createReadOnlyStringProperty("failed"), fds.get(2));
                    }
                }
                rmUpdateData(sheet);
            }

            // 插入操作
            ObservableList<ResultSetRowPo> dataList = tableData.getDataRs().getNewDatas();
            for (ResultSetRowPo os : dataList) {
                try {
                    ObservableList<ResultSetCellPo> cells = os.getRowDatas();
                    String msg = InsertDao.execInsert(conn, tabName, cells);
                    var fds = ddlDmlPo.getFields();
                    var row = ddlDmlPo.addRow();
                    ddlDmlPo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
                            fds.get(0));
                    ddlDmlPo.addData(row, new SimpleStringProperty(msg), fds.get(1));
                    ddlDmlPo.addData(row, new SimpleStringProperty("success"), fds.get(2));

                    // 对insert 的数据保存后 , 不能再修改
                    for (ResultSetCellPo cellPo : cells) {
                        StringProperty sp = cellPo.getCellData();
                        CommonUtils.prohibitChangeListener(sp, sp.get());
                    }

                } catch (Exception e1) {
                    hasError = true;
                    logger.error(e1);
                    btnDisable = false;
                    var fs = ddlDmlPo.getFields();
                    var row = ddlDmlPo.addRow();
                    ddlDmlPo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
                            fs.get(0));
                    ddlDmlPo.addData(row, new SimpleStringProperty(e1.getMessage()), fs.get(1));
                    ddlDmlPo.addData(row, new SimpleStringProperty("failed"), fs.get(2));
                }
            }
            // 删除缓存数据
            tableData.getDataRs().getNewDatas().clear();

            // 保存按钮禁用
            tableData.getSaveBtn().setDisable(btnDisable);
            if (hasError){
                TableViewUtils.showInfo(ddlDmlPo);
            }else {
                Platform.runLater(()-> MyAlert.notification("Save","Operation successful",MyAlert.NotificationType.show));
            }

        }
        return hasError;
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
            Thread t = new Thread(() -> {
                StringBuilder fieldsName = new StringBuilder();
                for (SheetFieldPo po : fs) {
                    String name = po.getColumnLabel().get();
                    fieldsName.append(name);
                    fieldsName.append(", \n");

                }
                if (StrUtils.isNotNullOrEmpty(fieldsName.toString())) {
                    String rsStr = fieldsName.toString().trim();
                    CommonUtils.setClipboardVal(fieldsName.substring(0, rsStr.length() - 1));
                }
            });
            t.start();

        });
    }


    // 导出表的字段列表, 不含分割字符
    public static void getTableFields(SheetDataValue tableData) {

        LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
            ObservableList<SheetFieldPo> fs = tableData.getColss();
            Thread t = new Thread(() -> {
                StringBuilder fieldsName = new StringBuilder();
                for (SheetFieldPo po : fs) {
                    String name = po.getColumnLabel().get();
                    fieldsName.append(name);
                    fieldsName.append("\n");

                }
                if (StrUtils.isNotNullOrEmpty(fieldsName.toString())) {
                    CommonUtils.setClipboardVal(fieldsName.toString());
                }
            });
            t.start();

        });
    }

    // 导出表的字段包含类型, 使用逗号分割
    public static void commaSplitTableFieldsIncludeType(SheetDataValue tableData) {

        LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
            ObservableList<SheetFieldPo> fs = tableData.getColss();
            Thread t = new Thread(() -> {
                StringBuilder fieldsName = new StringBuilder();
                for (SheetFieldPo po : fs) {
                    String name = po.getColumnLabel().get();
                    fieldsName.append(name);
                    fieldsName.append(", --");
                    fieldsName.append(po.getColumnTypeName().get());
                    fieldsName.append("\n");

                }
                if (StrUtils.isNotNullOrEmpty(fieldsName.toString())) {
                    CommonUtils.setClipboardVal(fieldsName.toString());
                }
            });
            t.start();
        });
    }

}
