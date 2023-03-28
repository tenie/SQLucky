package net.tenie.fx.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.db.DaoTools;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.subwindow.ModalDialog;

/**
 * 
 * @author tenie
 *
 */
public class DeleteDao {
	private static Logger logger = LogManager.getLogger(DeleteDao.class);

	public static String execDelete(Connection conn, String tableName, ResultSetRowPo mval) throws Exception {
		String msg = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try { 
			String condition = DaoTools.conditionStr(mval);

			// 校验 更新sql 会更1条以上, 如果查到一天以上给予提示确认!
			String select = "Select count(*) as val from " + tableName + " where " + condition;
			pstmt = conn.prepareStatement(select);
//			DaoTools.deleteConditionSetVal(pstmt, mval);
			DaoTools.conditionPrepareStatement(mval, pstmt);
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
//			DaoTools.deleteConditionSetVal(pstmt, vals, fpos); 
			String valStr = DaoTools.conditionPrepareStatement(mval, pstmt);
			// 更新
			int i = pstmt.executeUpdate();
			logger.info("executeDelete = " + i);
			msg = "Ok, Delete " + i + " ;\n" +sql + " ;\n"+valStr;
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
	public static void dropColumn(Connection conn , String table, String col) throws Exception {
		PreparedStatement pstmt = null;
		try {

			String sql = "ALTER TABLE "+table+" DROP COLUMN   " + col;
			pstmt = conn.prepareStatement(sql);
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
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
