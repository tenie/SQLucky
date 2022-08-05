package net.tenie.Sqlucky.sdk.utility;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;

/*   @author tenie */
public class GenerateSQLString {
	/**
	 * 给一行数据到处insert 语句
	 * @param tableName
	 * @param data
	 * @param fpos
	 * @return
	 */
	public static String insertSQL(String tableName, ResultSetRowPo data  ) {

		StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
		StringBuilder values = new StringBuilder("");
		var fpos = data.getFields();
		int size = fpos.size();
		for (int i = 0; i < size; i++) {
			SheetFieldPo po = fpos.get(i);
			int type = po.getColumnType().get();
			var cells = data.getRowDatas();
			String temp =  cells.get(i).getCellData().get();
//			String temp = data.get(i).get();
			sql.append(po.getColumnLabel().get());
			if (StrUtils.isNullOrEmpty(temp) || "<null>".equals(temp)) {
				values.append("null");
			} else if (CommonUtility.isString(type) || CommonUtility.isDateTime(type)) {
				values.append("'" + temp + "'");
			} else {
				values.append(temp);
			}
			sql.append(" ,");
			values.append(" ,");
		}
		String insert = sql.toString();
		String valstr = values.toString();
		if (insert.endsWith(",")) {
			insert = insert.substring(0, insert.length() - 1);
			valstr = valstr.substring(0, values.length() - 1);
		}

		insert += " ) VALUES (" + valstr + ") ";

		return insert;
	}
	
	public static String insertSQLExcludeNull(String tableName, ObservableList<StringProperty> data, ObservableList<SheetFieldPo> fpos) {

		StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
		StringBuilder values = new StringBuilder("");
		int size = fpos.size();
		for (int i = 0; i < size; i++) {
			SheetFieldPo po = fpos.get(i);
			int type = po.getColumnType().get();
			String temp = data.get(i).get(); 
			if ( "<null>".equals(temp)) {
				continue;
			} else if (CommonUtility.isString(type) || CommonUtility.isDateTime(type)) {
				values.append("'" + temp + "'");
			} else {
				values.append(temp);
			}
			sql.append(po.getColumnLabel().get());
			sql.append(" ,");
			values.append(" ,");
		}
		String insert = sql.toString();
		String valstr = values.toString();
		if (insert.endsWith(",")) {
			insert = insert.substring(0, insert.length() - 1);
			valstr = valstr.substring(0, values.length() - 1);
		}

		insert += " ) VALUES (" + valstr + ") ";

		return insert;
	}
	

	public static String insertSQLHelper(ObservableList<ResultSetRowPo> vals, String tableName ) {

		if (vals != null && vals.size() > 0) {
			StringBuilder strb = new StringBuilder();
			for (int i = 0; i < vals.size(); i++) {
//				ObservableList<StringProperty> vl = vals.get(i);
				ResultSetRowPo row = vals.get(i);
				String rs = GenerateSQLString.insertSQL(tableName, row) + ";\n";
				strb.append(rs);
			}
			String str = strb.toString();
			return str;
		}
		return "";
	}

	public static String csvStr(ObservableList<ResultSetCellPo> cells) {
		StringBuilder values = new StringBuilder("");
		int size = cells.size();
		for (int i = 0; i < size; i++) {
			ResultSetCellPo cellpo = cells.get(i);
			SheetFieldPo po = cellpo.getField();
			int type = po.getColumnType().get();
			String temp = cellpo.getCellData().get(); // data.get(i).get();
			if (StrUtils.isNullOrEmpty(temp) || "<null>".equals(temp)) {
				values.append("null");
			} else if (CommonUtility.isString(type) || CommonUtility.isDateTime(type)) {
				values.append("'" + temp + "'");
			} else {
				values.append(temp);
			}
			values.append(" ,");
		}
		String valstr = values.toString();
		if (valstr.endsWith(",")) {
			valstr = valstr.substring(0, values.length() - 1);
		}

		valstr += "\n";

		return valstr;
	}

	public static String csvStrHelper(ObservableList<ResultSetRowPo> vals ) {

		if (vals != null && vals.size() > 0) {
			StringBuilder strb = new StringBuilder();
			for (int i = 0; i < vals.size(); i++) {
				ResultSetRowPo row = vals.get(i);
				ObservableList<ResultSetCellPo> cells = row.getRowDatas();
//				ObservableList<StringProperty> vl = vals.get(i);
				String rs = GenerateSQLString.csvStr( cells);
				strb.append(rs);
			}
			String str = strb.toString();
			return str;
		}
		return "";
	}

	public static String txtStr(ResultSetRowPo data) {
		var cells = data.getRowDatas();
		StringBuilder values = new StringBuilder("");
		int size = cells.size();
		for (int i = 0; i < size; i++) {
			var cellpo = cells.get(i);
			SheetFieldPo po = cellpo.getField();
			int type = po.getColumnType().get();
			String temp = cellpo.getCellData().get();
			if (StrUtils.isNullOrEmpty(temp) || "<null>".equals(temp)) {
				values.append("null");
			} else if (CommonUtility.isString(type) || CommonUtility.isDateTime(type)) {
				values.append("'" );
				values.append(temp);
				values.append("'");
			} else {
				values.append(temp);
			}
			values.append("  ");
		}
		String valstr = values.toString();
		if (valstr.endsWith(",")) {
			valstr = valstr.substring(0, values.length() - 1);
		}

		valstr += "\n";

		return valstr;
	}

	public static String txtStrHelper(ObservableList<ResultSetRowPo> vals ) {

		if (vals != null && vals.size() > 0) {
			StringBuilder strb = new StringBuilder();
			for (int i = 0; i < vals.size(); i++) {
//				ObservableList<StringProperty> vl = vals.get(i);
				ResultSetRowPo rowpo = vals.get(i);
				String rs = GenerateSQLString.txtStr(rowpo );
				strb.append(rs);
			}
			String str = strb.toString();
			return str;
		}
		return "";
	}
	
	
	public static String columnStrHelper(ObservableList<ResultSetRowPo> vals,  String colName) {

		if (vals != null && vals.size() > 0) {
			StringBuilder strb = new StringBuilder();
			for (int i = 0; i < vals.size(); i++) {
				ResultSetRowPo vl = vals.get(i);
				String rs = GenerateSQLString.columnStr(vl, colName);
				strb.append(rs);
			}
			String str = strb.toString();
			return str;
		}
		return "";
	}
	
	public static String columnStr(ResultSetRowPo data, String colName) {
		StringBuilder values = new StringBuilder("");
		 ObservableList<ResultSetCellPo> cells = data.getRowDatas();
		int size = cells.size();
		int idx = -1;
		for (int i = 0; i < size; i++) {
			ResultSetCellPo cellpo = cells.get(i);
			SheetFieldPo po = cellpo.getField();
			String  name = po.getColumnLabel().get();
			if( name.equals(colName)) {
				idx = i;
				break;
			}
		}
		if( idx > -1) { 
				String temp = cells.get(idx).getCellData().get(); 
				values.append(temp);
				values.append("\n"); 
		}
		 
		return values.toString();
	}
	

}
