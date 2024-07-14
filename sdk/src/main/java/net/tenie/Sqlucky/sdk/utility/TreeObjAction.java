package net.tenie.Sqlucky.sdk.utility;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import net.tenie.Sqlucky.sdk.component.sheet.bottom.MyBottomSheetUtility;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.db.DBOptionHelper;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.db.TableForeignKeyPo;
import net.tenie.Sqlucky.sdk.po.db.TableIndexPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;

import java.util.List;

public class TreeObjAction {
    /**
     * 显示table ,view 的创建语句
     *
     * @param sqluckyConn
     * @param table
     */
    public static void showTableSql(SqluckyConnector sqluckyConn, TablePo table) {
        String tableName = table.getTableName();
        String type = table.getTableType();
        String createTableSql = table.getDdl();
        // 如果建表语句是空的, 那么导出建表语句
        if (type.equals(CommonConst.TYPE_TABLE)) {
            table.setSqluckyConnector(sqluckyConn);
            //
            createTableSql = DBOptionHelper.getCreateTableSQL(sqluckyConn, table.getTableSchema(),
                    table.getTableName());
            // 获取索引
            List<TableIndexPo> indexLs = DBOptionHelper.getTableIndex(sqluckyConn, table.getTableSchema(),
                    table.getTableName());
            table.setIndexs(indexLs);
            // TODO 获取外键
            List<TableForeignKeyPo> foreignKeyLs = DBOptionHelper.getTableForeignKey(sqluckyConn,
                    table.getTableSchema(), table.getTableName());
            table.setForeignKeys(foreignKeyLs);

            createTableSql = SqlFormatter.format(createTableSql);
            table.setDdl(createTableSql);

        } else if (type.equals(CommonConst.TYPE_VIEW)) {
            createTableSql = DBOptionHelper.getViewSQL(sqluckyConn, table.getTableSchema(), table.getTableName());
            createTableSql = SqlFormatter.format(createTableSql);
            table.setDdl(createTableSql);

        }

        if (type.equals(CommonConst.TYPE_TABLE)) {
            MyBottomSheetUtility.showTableInfoSheet(sqluckyConn, table);
        } else if (type.equals(CommonConst.TYPE_VIEW)) {
            MyBottomSheetUtility.showViewDDLSheet(sqluckyConn, table);

        }

    }

    public static String getTableSQL(SqluckyConnector sqluckyConn, String tableSchema, String tableName) {
        String createTableSql = DBOptionHelper.getCreateTableSQL(sqluckyConn, tableSchema, tableName);

        return createTableSql;
    }

}
