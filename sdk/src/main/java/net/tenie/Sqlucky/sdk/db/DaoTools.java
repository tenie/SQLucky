package net.tenie.Sqlucky.sdk.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
/**
 * 
 * @author tenie
 *
 */
public class DaoTools {
		private static Logger logger = LogManager.getLogger(DaoTools.class);
	
		// where 后面的字段 处理, <null> 设置成is null, 时间类型 直接字符串拼接
		public static String conditionStr(ResultSetRowPo mval) {
			ObservableList<ResultSetCellPo> cellVals = mval.getRowDatas();
			StringBuffer str = new StringBuffer(" ");
			for (int i = 0; i < cellVals.size(); i++) {
				ResultSetCellPo cellpo = cellVals.get(i);
				String field = cellpo.getField().getColumnLabel().get();
				
				Object	valStr = cellpo.getDbOriginalValue();
				
				if( valStr != null) { 
					str.append(field + " = ?  and ");
				}else {
					str.append(field + " is null and ");
				}

			}
			String rs = str.toString();
			String t = " and ";
			if (rs.endsWith(t)) {
				rs = rs.substring(0, rs.length() - t.length());
			}

			return rs;
		}
		
		// where 后面的字段 处理, <null> 设置成is null, 时间类型 直接字符串拼接
		public static String conditionPrepareStatement(ResultSetRowPo mval, PreparedStatement pstmt) throws SQLException {
			int idx = 0;
			String logmsg = "[ ";
			ObservableList<ResultSetCellPo> cellVals = mval.getRowDatas();
			for (int i = 0; i < cellVals.size(); i++) {
				idx++;
				ResultSetCellPo cellpo = cellVals.get(i);
				Object val = cellpo.getDbOriginalValue();
				
				if(val != null) {
					pstmt.setObject(idx, val);
					logmsg += idx + " : " + val + "\n";
				}else {
					idx--;
					continue;
				}
				
			}
			logmsg += " ]";
			logger.info(logmsg);
			return logmsg;
		}
		
		// 更新操作的问号赋值
		public static String updatePrepareStatement(ResultSetRowPo mval, PreparedStatement pstmt) throws SQLException {
			int idx = 0;
			String logmsg = "[ ";
			// 更新部分
			StringBuffer str = new StringBuffer(" "); 
			ObservableList<ResultSetCellPo> cells = mval.getRowDatas();
			
			// 新值赋值操作
			for (int i = 0; i < cells.size(); i++) {
				ResultSetCellPo cellVal = cells.get(i); 
				// 通过getHasModify()判断, 给需要更新的字段 设置值
				if( cellVal != null && cellVal.getHasModify()) {
					idx++;
					String field = cellVal.getField().getColumnLabel().get();
					String strVal = cellVal.getCellData().get();
					
					if ("<null>".equals(strVal)) {
						idx--;
						continue;
					} else {
						String type = cellVal.getField().getColumnClassName().get();
						int fieldType =  cellVal.getField().getColumnType().get();
						// 时间的情况
						if( CommonUtility.isDateAndDateTime(fieldType)) {
							Date dv = StrUtils.StrToDate_L(strVal);
							pstmt.setObject(idx, dv);
							logmsg += idx + " : " + dv + "\n";
						}else {
							pstmt.setObject(idx, strVal);
							logmsg += idx + " : " + strVal + "\n";
						}
//						// 日期直接拼接字符串, 防止毫秒的情况, 多加了1秒后的比较 
//						if (type.equals("java.sql.Timestamp") || type.equals("java.sql.Time")
//								|| type.equals("java.sql.Date")) {
//							idx--;
//							continue;
//						}else {
//
//							Object obj = strVal;
//							pstmt.setObject(idx, obj);
//							logmsg += idx + " : " + obj + "\n";
//						}
					}
				}
				
			}
			
			// 条件部分, 会把界面上展示的字段都会作为条件
			for (int i = 0; i < cells.size(); i++) {
				idx++;
				ResultSetCellPo cellpo = cells.get(i);
				Object val = cellpo.getDbOriginalValue();
				if(val != null) { 
					pstmt.setObject(idx, val);
					logmsg += idx + " : " + val + "\n";
				}else {
					idx--;
					continue;
				}
//				if(cellpo.getHasModify() ) {
//					val = cellpo.getOldCellData().get();
//				}else {
//					val = cellpo.getCellData().get();
//				}
				
				
//				String type = cellpo.getField().getColumnClassName().get();
//				if ("<null>".equals(val)) {
//					idx--;
//					continue;
//				} else if (type.equals("java.sql.Timestamp") || type.equals("java.sql.Time")
//						|| type.equals("java.sql.Date")) {
//					idx--;
//					continue;
//	
//				} else {
//	//						Object obj = BuildObject.buildObj(type, val);
//	//						pstmt.setObject(idx, obj);
//	
//					Object obj = val;
//					pstmt.setObject(idx, obj);
//					logmsg += idx + " : " + obj + "\n";
//				}
			}
			logmsg += " ]";
			logger.info(logmsg);
			return logmsg;
		}

		

		// 更新值的 字符串拼接, <null>直接设置为 is null, 时间类型之间字符串拼接
		public static String concatStrSetVal(ResultSetRowPo mval) {
			StringBuffer str = new StringBuffer(" "); 
			ObservableList<ResultSetCellPo> cells = mval.getRowDatas();
			for (int i = 0; i < cells.size(); i++) {
				ResultSetCellPo cellVal = cells.get(i);  
				if( cellVal != null && cellVal.getHasModify()) {
					String field = cellVal.getField().getColumnLabel().get();
					String strVal = cellVal.getCellData().get();
					if ("<null>".equals(strVal)) {
						str.append(field + " = null ,");
					} else {
						str.append(field + " = ? ,");
					}
				}
				
			}
			String rs = str.toString().trim();
			if (rs.endsWith(",")) {
				rs = rs.substring(0, rs.length() - 1);
			}

			return rs;
		}
		
		
//		public static void deleteConditionSetVal2(PreparedStatement pstmt ,  ResultSetRowPo mval) throws Exception {
//			ObservableList<ResultSetCellPo> cells = mval.getRowDatas();
//			int idx = 0;
//			for (int i = 0; i < cells.size(); i++) {
//				ResultSetCellPo cellpo = cells.get(i);
//				idx++;
//				String val = cellpo.getCellData().get();
//				String type = cellpo.getField().getColumnClassName().get();
//				if ( "<null>".equals(val)) {
//					idx--;
//					continue;
//				}else if (type.equals("java.sql.Timestamp") || type.equals("java.sql.Time")
//						|| type.equals("java.sql.Date")) {
//					idx--;
//					continue;
//				} if (type.equals("java.lang.Object") ) {
//					pstmt.setObject(idx, val);
//					logger.info(idx + "  " + val);
//				 } else {
////					Object obj = BuildObject.buildObj(type, val);
//					Object obj = val;
//					pstmt.setObject(idx, obj);
//					logger.info(idx + "  " + obj);
//				}
//			}
//		}
		
}
