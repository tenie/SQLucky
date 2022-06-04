package net.tenie.fx.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.po.SqlFieldPo;
import net.tenie.Sqlucky.sdk.subwindow.ModalDialog;
import net.tenie.lib.reflex.BuildObject;

/**
 * 
 * @author tenie
 *
 */
public class UpdateDao {
	private static Logger logger = LogManager.getLogger(UpdateDao.class);
	
	public static String execUpdate(Connection conn, String tableName, ObservableList<StringProperty> newvals,
			ObservableList<StringProperty> oldvals, ObservableList<SqlFieldPo> fpos) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String msg = "";
		try {
			int oldvalsLen = fpos.size();
			int newvalsLen = fpos.size();
			String temp = DaoTools.concatStrSetVal(newvals, fpos);
			String condition = DaoTools.conditionStr(oldvals, fpos);

			// 校验 更新sql 会更1条以上, 如果查到一天以上给予提示确认!
			String select = "Select count(*) as val from " + tableName + " where " + condition;
			pstmt = conn.prepareStatement(select);
			int idx = 0;
			String logmsg = "[ ";
			for (int i = 0; i < oldvalsLen; i++) {
				idx++;
				String val = oldvals.get(i).get();
				String type = fpos.get(i).getColumnClassName().get();
				if ("<null>".equals(val)) {
					idx--;
					continue;
				} else if (type.equals("java.sql.Timestamp") || type.equals("java.sql.Time")
						|| type.equals("java.sql.Date")) {
//					Date dv = StrUtils.StrToDate(val, ConfigVal.dateFormateL);
//					Timestamp ts = new Timestamp(dv.getTime());
//					pstmt.setTimestamp(idx, ts);
					
//					logger.info(idx );
					idx--;
					continue;
					
				} else {
					Object obj = BuildObject.buildObj(type, val);
					pstmt.setObject(idx, obj);
					logmsg += idx + " : " + obj +"\n";
				}
			}
			
			logger.info(logmsg +" ]");
			boolean tf = true;
			logger.info("sql = " + select);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				int val = rs.getInt(1);
				if (val > 1) {
					tf = ModalDialog.Confirmation("Finded " + val + " line data , Are you sure continue Update " + val + " line data ?");
				} else if (val == 0) {
					ModalDialog.Confirmation("Finded " + val + " line data");	
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
//					Date dv = StrUtils.StrToDate(val, ConfigVal.dateFormateL);
//					Timestamp ts = new Timestamp(dv.getTime());
//					pstmt.setTimestamp(idx, ts);
//					logger.info(idx );
					idx--;
					continue;
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
//					Date dv = StrUtils.StrToDate(val, ConfigVal.dateFormateL);
//					Timestamp ts = new Timestamp(dv.getTime());
//					pstmt.setTimestamp(idx, ts);
//					logger.info(idx );
					idx--;
					continue;
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

//	public static void main(String[] args) {
//		Date d1 = StrUtils.StrToDate("2021-01-07 11:47:17.0", ConfigVal.dateFormateL);
//		Date d2 = StrUtils.StrToDate("2021-01-07 11:47:17", ConfigVal.dateFormateL);
//		logger.info(d1);
//		logger.info(d2);
//		java.sql.Date sd = new java.sql.Date(d1.getTime());
//		logger.info(sd);
//		Timestamp ts = new Timestamp(d1.getTime());
//		logger.info(ts);
//		
//		Timestamp ts2 = new Timestamp(d2.getTime());
//		logger.info(ts2);
//		
//		java.sql.Time t = new java.sql.Time(d1.getTime());
//		logger.info(t);
//		
//		String s = StrUtils.dateToStr(d1, ConfigVal.dateFormateL);
//				logger.info(s);
//		
//	}
}
