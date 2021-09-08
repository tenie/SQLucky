package net.tenie.fx.Action;

import com.github.vertical_blank.sqlformatter.SqlFormatter;

import net.tenie.fx.PropertyPo.DBOptionHelper;
import net.tenie.fx.PropertyPo.DbConnectionPo;
import net.tenie.fx.PropertyPo.TablePo;
import net.tenie.fx.component.container.DataViewTab;
import net.tenie.fx.config.CommonConst;
import net.tenie.lib.tools.StrUtils;

public class TreeObjAction {
	public static void showTableSql(DbConnectionPo dpo ,TablePo table, String title) {
//		DbConnectionPo dpo = item.getValue().getConnpo();
//		TablePo table = item.getValue().getTable();
		String type = table.getTableType();
				//CommonConst.TYPE_TABLE
		String createTableSql = table.getDdl();
		if (StrUtils.isNullOrEmpty(createTableSql)) {
			if(type.equals( CommonConst.TYPE_TABLE )) {
				createTableSql = DBOptionHelper.getCreateTableSQL(dpo, table.getTableSchema(), table.getTableName());	
			}else if(type.equals( CommonConst.TYPE_VIEW ) ) {
				createTableSql = DBOptionHelper.getViewSQL(dpo, table.getTableSchema(), table.getTableName());			
			}
			
			createTableSql = SqlFormatter.format(createTableSql);
			table.setDdl(createTableSql);
		}
		new DataViewTab().showDdlPanel(title, createTableSql);
	}
}
