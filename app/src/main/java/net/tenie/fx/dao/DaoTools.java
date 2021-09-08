package net.tenie.fx.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.config.ConfigVal;
import net.tenie.lib.reflex.BuildObject;
import net.tenie.lib.tools.StrUtils;

public class DaoTools {
		private static Logger logger = LogManager.getLogger(DaoTools.class);
	
		// where 后面的字段 处理, <null> 设置成is null, 时间类型 直接字符串拼接
		public static String conditionStr(ObservableList<StringProperty> vals, ObservableList<SqlFieldPo> fpos) {
			StringBuffer str = new StringBuffer(" ");// "where ";
			for (int i = 0; i < fpos.size(); i++) {
				String val = vals.get(i).get();
				String field = fpos.get(i).getColumnLabel().get();
				if( val != null) {
					if( "<null>".equals(val) ) {
						str.append(field + " is null and ");
					}else {
						String type = fpos.get(i).getColumnClassName().get();
						// 日期直接拼接字符串, 防止毫秒的情况, 多加了1秒后的比较 
						if (type.equals("java.sql.Timestamp") || type.equals("java.sql.Time")
								|| type.equals("java.sql.Date")) {
							str.append(field + " >= '"+val+"'  and ");
							Date v =  StrUtils.datePlus1Second(val);
							String p1s = StrUtils.dateToStr(v, ConfigVal.dateFormateL);
							str.append(field + " <'"+p1s+"'  and ");
						 
						}else {
							str.append(field + " = ?  and ");
						}
						
					}
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

		// 更新值的 字符串拼接, <null>直接设置为 is null, 时间类型之间字符串拼接
		public static String concatStrSetVal(ObservableList<StringProperty> vals, ObservableList<SqlFieldPo> fpos) {
			StringBuffer str = new StringBuffer(" ");// "where ";
			for (int i = 0; i < fpos.size(); i++) {
				String val = vals.get(i).get();
				String field = fpos.get(i).getColumnLabel().get();
				if ("<null>".equals(val)) {
					str.append(field + " = null ,");
				} else {
//							str.append(field + " = ? ,");
					String type = fpos.get(i).getColumnClassName().get();
					// 日期直接拼接字符串, 防止毫秒的情况, 多加了1秒后的比较 
					if (type.equals("java.sql.Timestamp") || type.equals("java.sql.Time")
							|| type.equals("java.sql.Date")) {
						str.append(field + " = '"+val+"' , ");  
					}else {
						str.append(field + " = ? ,");
					}
				}
			}
			String rs = str.toString();
			if (rs.endsWith(",")) {
				rs = rs.substring(0, rs.length() - 1);
			}

			return rs;
		}
		
		
		public static void deleteConditionSetVal(PreparedStatement pstmt , ObservableList<StringProperty> vals,ObservableList<SqlFieldPo> fpos) throws Exception {
			int valsLen = fpos.size();
			int idx = 0;
			for (int i = 0; i < valsLen; i++) {
				idx++;
				String val = vals.get(i).get();
				String type = fpos.get(i).getColumnClassName().get();
				if ( "<null>".equals(val)) {
					idx--;
					continue;
				}else if (type.equals("java.sql.Timestamp") || type.equals("java.sql.Time")
						|| type.equals("java.sql.Date")) {
//					Date dv = StrUtils.StrToDate(val, ConfigVal.dateFormateL);
//					Timestamp ts = new Timestamp(dv.getTime());
//					pstmt.setTimestamp(idx, ts);
//					logger.info(idx );
					idx--;
					continue;
				} if (type.equals("java.lang.Object") ) {
					pstmt.setObject(idx, val);
					logger.info(idx + "  " + val);
				 } else {
					Object obj = BuildObject.buildObj(type, val);
					pstmt.setObject(idx, obj);
					logger.info(idx + "  " + obj);
				}
			}
		}
		
}
