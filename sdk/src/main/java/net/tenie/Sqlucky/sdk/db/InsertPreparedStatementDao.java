package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.po.ExcelFieldPo;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;

/**
 * 生成insert的PreparedStatement
 * 
 * @author tenie
 *
 */
public class InsertPreparedStatementDao {
	private static Logger logger = LogManager.getLogger(InsertPreparedStatementDao.class);

	public static String createPreparedStatementSql(String tableName, List<SheetFieldPo> fpos) {

		StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
		StringBuilder values = new StringBuilder("");
		int size = fpos.size();
		for (int i = 0; i < size; i++) {
			SheetFieldPo po = fpos.get(i);
			sql.append(po.getColumnLabel().get() + " ,");
			values.append("?,");
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

	public static String createPreparedStatementSqlForExcel(String tableName, List<ExcelFieldPo> fpos) {

		StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
		StringBuilder values = new StringBuilder("");
		int size = fpos.size();
		for (int i = 0; i < size; i++) {
			SheetFieldPo po = fpos.get(i);
			sql.append(po.getColumnLabel().get() + " ,");
			values.append("?,");
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

	public static String execInsert(Connection conn, String tableName, ResultSetRowPo mval) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String msg = "";
		try {
			String condition = DaoTools.conditionStr(mval);

			// 校验 更新sql 会更1条以上, 如果查到一天以上给予提示确认!
			String select = "Select count(*) as val from " + tableName + " where " + condition;
			pstmt = conn.prepareStatement(select);
			DaoTools.conditionPrepareStatement(mval, pstmt);

			boolean tf = true;
			String showMsg = "";
			logger.info("sql = " + select);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				int val = rs.getInt(1);
				if (val > 1) {
					showMsg = "Finded " + val + " line data , Are you sure continue Update " + val + " line data ?";
//					tf = ModalDialog.Confirmation("Finded " + val + " line data , Are you sure continue Update " + val + " line data ?");
					tf = MyAlert.myConfirmationShowAndWait(showMsg);
				} else if (val == 0) {
					showMsg = "Finded " + val + " line data, Skip Update.";
					MyAlert.myConfirmationShowAndWait(showMsg);
//					ModalDialog.Confirmation("Finded " + val + " line data");	
					tf = false; // 没有更新数据
				}
			}

			if (!tf) {
				msg = "";
				return msg;
			}
//			MyAlert.myConfirmation(showMsg, );

			String temp = DaoTools.concatStrSetVal(mval);
			String sql = "update " + tableName + " set  " + temp + " where " + condition;
			pstmt = conn.prepareStatement(sql);

			String valStr = DaoTools.updatePrepareStatement(mval, pstmt);
			pstmt.addBatch();
//			pstmt.executeBatch()
			// 更新
			int i = pstmt.executeUpdate();
			msg = "Ok, Update " + i + " ;\n" + sql + " ;\n" + valStr;
			logger.info(msg);
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
