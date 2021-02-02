package net.tenie.fx.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.component.ModalDialog;
import net.tenie.fx.config.ConfigVal;
import net.tenie.lib.reflex.BuildObject;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class DeleteDao {
	private static Logger logger = LogManager.getLogger(DeleteDao.class);


	public static String execDelete(Connection conn, String tableName, ObservableList<StringProperty> vals,
			ObservableList<SqlFieldPo> fpos) throws Exception {
		String msg = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
//			int valsLen = fpos.size();
			String condition = DaoTools.conditionStr(vals, fpos);

			// 校验 更新sql 会更1条以上, 如果查到一天以上给予提示确认!
			String select = "Select count(*) as val from " + tableName + " where " + condition;
			pstmt = conn.prepareStatement(select);
			DaoTools.conditionSetVal(pstmt, vals, fpos);
//			int idx = 0;
//			for (int i = 0; i < valsLen; i++) {
//				idx++;
//				String val = vals.get(i).get();
//				String type = fpos.get(i).getColumnClassName().get();
//				if (StrUtils.isNullOrEmpty(val) || "<null>".equals(val)) {
//					idx--;
//					continue;
//				} else if (type.equals("java.sql.Timestamp") || type.equals("java.sql.Time")
//						|| type.equals("java.sql.Date")) {
//					Date dv = StrUtils.StrToDate(val, ConfigVal.dateFormateL);
//					Timestamp ts = new Timestamp(dv.getTime());
//					pstmt.setTimestamp(idx, ts);
//					logger.info(idx + "  " + ts);
//				} else {
//					Object obj = BuildObject.buildObj(type, val);
//					pstmt.setObject(idx, obj);
//					logger.info(idx + "  " + obj);
//				}
//			}
			
			boolean tf = true;
			logger.info("sql = " + select);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				int val = rs.getInt(1);
				logger.info("select count = " + val);
				if (val > 1) {
					tf = ModalDialog.Confirmation("Finded " + val + " line data , Are you sure continue delete  ?");
				} else if (val == 0) {
					tf = false; // 没有更新数据
				}
			}

			if (!tf) {
				msg = " Data Not finded, Skip Delete. ";
				return msg;
			}
			String sql = "DELETE  FROM  " + tableName + " where " + condition;

			logger.info("sql = " + sql);
			pstmt = conn.prepareStatement(sql);
			DaoTools.conditionSetVal(pstmt, vals, fpos);
//			// 赋值
//			idx = 0;
//			// where 部分
//			for (int i = 0; i < valsLen; i++) {
//				idx++;
//				String val = vals.get(i).get();
//				String type = fpos.get(i).getColumnClassName().get();
//				if ("<null>".equals(val)) {
//					idx--;
//					continue;
//				} else if (type.equals("java.sql.Timestamp") || type.equals("java.sql.Time")
//						|| type.equals("java.sql.Date")) {
//					Date dv = StrUtils.StrToDate(val, ConfigVal.dateFormateL);
//					Timestamp ts = new Timestamp(dv.getTime());
//					pstmt.setTimestamp(idx, ts);
//				} else {
//
//					Object obj = BuildObject.buildObj(type, val);
//					pstmt.setObject(idx, obj);
//				}
//			}

			// 更新
			int i = pstmt.executeUpdate();
			logger.info("executeDelete = " + i);
			msg = "Ok, Delete " + i;
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

	public static void execUpdate2(Connection conn) throws Exception {
		PreparedStatement pstmt = null;
		try {

			String sql = "update tm_employee  set  WORKING_HOURS = ?  where EMPLOYEE_ID = 3 and  WORKING_HOURS = ?  ";
			pstmt = conn.prepareStatement(sql);

			pstmt.setObject(1, null);
			pstmt.setObject(2, null);
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
	}

}
