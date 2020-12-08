package net.tenie.fx.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.tenie.fx.PropertyPo.DbTableDatePo;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.lib.tools.StrUtils;

public class TransferTabeDataDao {
	public static DbTableDatePo selectSql(Connection conn, String tableName, int limit,
			FilteredTableView<ObservableList<StringProperty>> table) throws SQLException {
		String sql = "select   *   from   "+tableName+"    where   1=1  ";
		DbTableDatePo dpo = new DbTableDatePo();
		// DB对象
		PreparedStatement pstate = null;
		ResultSet rs = null;
		try {
			pstate = conn.prepareStatement(sql);
			// 处理结果集
			rs = pstate.executeQuery();
			// 获取元数据
			ResultSetMetaData mdata = rs.getMetaData();
			// 获取元数据列数
			Integer columnnums = Integer.valueOf(mdata.getColumnCount());
			// 迭代元数据
			for (int i = 1; i <= columnnums; i++) {
				SqlFieldPo po = new SqlFieldPo();
				po.setScale(mdata.getScale(i));
				po.setColumnName(mdata.getColumnName(i));
				po.setColumnClassName(mdata.getColumnClassName(i));
				po.setColumnDisplaySize(mdata.getColumnDisplaySize(i));
				po.setColumnLabel(mdata.getColumnLabel(i));
				po.setColumnType(mdata.getColumnType(i));
				po.setColumnTypeName(mdata.getColumnTypeName(i));
				dpo.addField(po);
			}


		execRs(rs, dpo, table);
			
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				rs.close();
		}
		return dpo;
	}
//	private static String bulidUpdateSql(String tableName, ObservableList<SqlFieldPo> fpos) { }
	
	private static void execRs( ResultSet rs, DbTableDatePo dpo,
			FilteredTableView<ObservableList<StringProperty>> table) throws SQLException {
		 
		int rowNo = 0;
		ObservableList<SqlFieldPo> fpo = dpo.getFields();
		int columnnums = fpo.size();
		while (rs.next()) {
			ObservableList<StringProperty> vals = FXCollections.observableArrayList();
			int rn = rowNo++;
			for (int i = 0; i < columnnums; i++) {
//				String field = fpo.get(i).getColumnLabel().get();
				int dbtype = fpo.get(i).getColumnType().get();
				StringProperty val;
				
				Object obj = rs.getObject(i + 1);
				if(obj == null) {
					val = new SimpleStringProperty("<null>");
				}else {
					if (CommonUtility.isDateTime(dbtype)) {
						java.sql.Timestamp ts = rs.getTimestamp(i + 1);
						Date d = new Date(ts.getTime());
						String v = StrUtils.dateToStr(d, ConfigVal.dateFormateL);
						val = new SimpleStringProperty(v);
					} else {
						String temp = rs.getString(i+1);
						val = new SimpleStringProperty(temp); 
					}
				}
				 vals.add(val);
			}
//Connection conn, String tableName, ObservableList<StringProperty> data, ObservableList<SqlFieldPo> fpos
			execInsert
			dpo.addData(vals);

			 
		}
	}

}
