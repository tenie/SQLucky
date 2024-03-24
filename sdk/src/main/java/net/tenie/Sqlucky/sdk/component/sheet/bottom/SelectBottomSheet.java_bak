package net.tenie.Sqlucky.sdk.component.sheet.bottom;

import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.component.DataViewContainer;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SelectExecInfo;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableView;

public class SelectBottomSheet {

    private static Logger logger = LogManager.getLogger(SelectBottomSheet.class);
    public static 	MyBottomSheet createMyBottomSheet(SqluckyConnector sqluckyConn, String sql, String tableName, boolean isLock, SelectExecInfo execInfo ){
        MyBottomSheet myBottomSheet  =  new MyBottomSheet(tableName);

        SheetDataValue sheetDaV = myBottomSheet.getTableData();
        sheetDaV.setDbConnection(sqluckyConn);
        sheetDaV.setSqlStr(sql);
        sheetDaV.setTabName(tableName);
        sheetDaV.setLock(isLock);
        sheetDaV.setSelectExecInfo(execInfo);

        FilteredTableView<ResultSetRowPo> table = sheetDaV.getTable();
        // 设置行号显示宽度
        DataViewContainer.setTabRowWith(table, sheetDaV.getDataRs().getDatas().size());
        ObservableList<ResultSetRowPo> allRawData = sheetDaV.getDataRs().getDatas();
        ObservableList<SheetFieldPo> colss = sheetDaV.getColss();

        return myBottomSheet;
    }
}
