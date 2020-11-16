package net.tenie.fx.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.component.ModalDialog;
import net.tenie.fx.config.ConfigVal;
import net.tenie.lib.reflex.BuildObject;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class UpdateDao {

	public static String concatStr(ObservableList<StringProperty> vals, ObservableList<SqlFieldPo> fpos) {
		StringBuffer str = new StringBuffer(" ");// "where ";
		for (int i = 0; i < fpos.size(); i++) {
			String val = vals.get(i).get();
			String field = fpos.get(i).getColumnLabel().get();
			if ("<null>".equals(val)) {
				str.append(field + " = null ,");
			} else {
				str.append(field + " = ? ,");
			}
		}
		String rs = str.toString();
		if (rs.endsWith(",")) {
			rs = rs.substring(0, rs.length() - 1);
		}

		return rs;
	}

	public static String conditionStr(ObservableList<StringProperty> vals, ObservableList<SqlFieldPo> fpos) {
		StringBuffer str = new StringBuffer(" ");// "where ";
		for (int i = 0; i < fpos.size(); i++) {
			String val = vals.get(i).get();
			String field = fpos.get(i).getColumnLabel().get();
			if (StrUtils.isNullOrEmpty(val) || "<null>".equals(val)) {
				str.append(field + " is null and ");
			} else {
				str.append(field + " = ?  and ");
			}

		}
		String rs = str.toString();
		String t = " and ";
		if (rs.endsWith(t)) {
			rs = rs.substring(0, rs.length() - t.length());
		}

		return rs;
	}

	public static void checkSelect() {

	}

	public static String execUpdate(Connection conn, String tableName, ObservableList<StringProperty> newvals,
			ObservableList<StringProperty> oldvals, ObservableList<SqlFieldPo> fpos) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String msg = "";
		try {
			int oldvalsLen = fpos.size();
			int newvalsLen = fpos.size();
			String temp = concatStr(newvals, fpos);
			String condition = conditionStr(oldvals, fpos);

			// 校验 更新sql 会更1条以上, 如果查到一天以上给予提示确认!
			String select = "Select count(*) as val from " + tableName + " where " + condition;
			pstmt = conn.prepareStatement(select);
			int idx = 0;
			for (int i = 0; i < oldvalsLen; i++) {
				idx++;
				String val = oldvals.get(i).get();
				String type = fpos.get(i).getColumnClassName().get();
				if (StrUtils.isNullOrEmpty(val) || "<null>".equals(val)) {
					idx--;
					continue;
				} else if (type.equals("java.sql.Timestamp") || type.equals("java.sql.Time")
						|| type.equals("java.sql.Date")) {
					Date dv = StrUtils.StrToDate(val, ConfigVal.dateFormateL);
					Timestamp ts = new Timestamp(dv.getTime());
					pstmt.setTimestamp(idx, ts);
					System.out.println(idx + "  " + ts);
				} else {
					Object obj = BuildObject.buildObj(type, val);
					pstmt.setObject(idx, obj);
					System.out.println(idx + "  " + obj);
				}
			}
			boolean tf = true;
			System.out.println("sql = " + select);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				int val = rs.getInt(1);
				if (val > 1) {
					tf = ModalDialog.Confirmation(
							"Finded " + val + " line data , Are you sure continue Update " + val + " line data ?");
				} else if (val == 0) {
					tf = false; // 没有更新数据
				}
			}

			if (!tf) {
				msg = " Data Not finded, Skip Update.";
				return msg;
			}
			String sql = "update " + tableName + " set  " + temp + " where " + condition;
			pstmt = conn.prepareStatement(sql);

			// 赋值
			idx = 0;
			for (int i = 0; i < newvalsLen; i++) {
				idx++;
				String val = newvals.get(i).get();
				String type = fpos.get(i).getColumnClassName().get();
				if ("<null>".equals(val)) {
					idx--;
					continue;
				} else if (type.equals("java.sql.Timestamp") || type.equals("java.sql.Time")
						|| type.equals("java.sql.Date")) {
					Date dv = StrUtils.StrToDate(val, ConfigVal.dateFormateL);
					Timestamp ts = new Timestamp(dv.getTime());
					pstmt.setTimestamp(idx, ts);
				} else {
					Object obj = BuildObject.buildObj(type, val);
					pstmt.setObject(idx, obj);
				}
			}
			// where 部分
			for (int i = 0; i < oldvalsLen; i++) {
				idx++;
				String val = oldvals.get(i).get();
				String type = fpos.get(i).getColumnClassName().get();
				if ("<null>".equals(val)) {
					idx--;
					continue;
				} else if (type.equals("java.sql.Timestamp") || type.equals("java.sql.Time")
						|| type.equals("java.sql.Date")) {
					Date dv = StrUtils.StrToDate(val, ConfigVal.dateFormateL);
					Timestamp ts = new Timestamp(dv.getTime());
					pstmt.setTimestamp(idx, ts);
				} else {

					Object obj = BuildObject.buildObj(type, val);
					pstmt.setObject(idx, obj);
				}
			}

			// 更新
			int i = pstmt.executeUpdate();
			msg = "Ok, Update " + i;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (rs != null)
				rs.close();
		}
		return msg;
	}

}
