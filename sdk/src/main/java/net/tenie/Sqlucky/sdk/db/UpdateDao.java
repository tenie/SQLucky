package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.subwindow.MyAlert;

/**
 * 
 * @author tenie
 *
 */
public class UpdateDao {
	private static Logger logger = LogManager.getLogger(UpdateDao.class);
	
	public static String execUpdate(Connection conn,
			String tableName,
			ResultSetRowPo mval) throws Exception {
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

			// 更新
			int i = pstmt.executeUpdate();
			msg = "Ok, Update " + i + " ;\n" + sql +" ;\n"+ valStr;
			logger.info( msg);
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
