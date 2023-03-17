package net.tenie.fx.component.InfoTree;

import com.github.vertical_blank.sqlformatter.SqlFormatter;

import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.TablePo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Po.DBOptionHelper;


public class TreeObjAction {
	/**
	 * 显示table ,view 的创建语句
	 * @param sqluckyConn
	 * @param table
	 * @param tableName
	 */
	public static void showTableSql(SqluckyConnector sqluckyConn ,TablePo table, String tableName) {
		String type = table.getTableType();
		String createTableSql = table.getDdl();
		if (StrUtils.isNullOrEmpty(createTableSql)) {
			if(type.equals( CommonConst.TYPE_TABLE )) {
				createTableSql = DBOptionHelper.getCreateTableSQL(sqluckyConn, table.getTableSchema(), table.getTableName());	
			}else if(type.equals( CommonConst.TYPE_VIEW ) ) {
				createTableSql = DBOptionHelper.getViewSQL(sqluckyConn, table.getTableSchema(), table.getTableName());			
			}
			
			createTableSql = SqlFormatter.format(createTableSql);
			table.setDdl(createTableSql);
		}
		SqluckyBottomSheet mtd = ComponentGetter.appComponent.ddlSheet(sqluckyConn, tableName, createTableSql, false , true);
		mtd.show();
	}
	
	public static String getTableSQL(SqluckyConnector sqluckyConn ,String tableSchema, String tableName) {
		String createTableSql = DBOptionHelper.getCreateTableSQL(sqluckyConn, tableSchema, tableName);
		
		return createTableSql;
	}
	
}
