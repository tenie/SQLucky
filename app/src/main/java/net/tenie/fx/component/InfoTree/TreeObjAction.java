package net.tenie.fx.component.InfoTree;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.PropertyPo.DBOptionHelper;
import net.tenie.fx.component.dataView.MyTabData;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.TablePo;


public class TreeObjAction {
	public static void showTableSql(SqluckyConnector dpo ,TablePo table, String title) {
		String type = table.getTableType();
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
//		new DataViewTab().showDdlPanel(title, createTableSql);
		MyTabData mtd = MyTabData.ddlTab(title, createTableSql, false);
		mtd.show();
	}
}
