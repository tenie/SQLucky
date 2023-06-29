package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/*
 * 
 */
public class InsertDao {

	private static Logger logger = LogManager.getLogger(InsertDao.class);

	/**
	 * 提供一行表格数据, 做插入保存
	 * 
	 * @param conn      数据库连接
	 * @param tableName 表
	 * @param data      列的数据
	 * @param fpos      列的数据字段类型
	 * @return
	 * @throws Exception
	 */
	public static String execInsert(Connection conn, String tableName, List<ResultSetCellPo> cells) throws Exception {
		String msg = "";
		StringBuilder sql = new StringBuilder("insert into " + tableName + " (");
		StringBuilder values = new StringBuilder("");
		int size = cells.size();
		for (int i = 0; i < size; i++) {
			ResultSetCellPo cellPo = cells.get(i);
			SheetFieldPo po = cellPo.getField();
			String temp = cellPo.getCellData().get();
			if (!"<null>".equals(temp)) {
				sql.append(po.getColumnLabel().get());
				values.append(" ? ");
				sql.append(" ,");
				values.append(" ,");
			}

		}
		String insert = sql.toString();
		String valstr = values.toString();
		if (insert.endsWith(",")) {
			insert = insert.substring(0, insert.length() - 1);
			valstr = valstr.substring(0, values.length() - 1);
		}

		insert += " ) VALUES (" + valstr + ")";

		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(insert);
		String insertLog = insert;

		int idx = 0;
		for (int i = 0; i < size; i++) {
			ResultSetCellPo cellPo = cells.get(i);
			String val = cellPo.getCellData().get();
			if (!"<null>".equals(val)) {
				idx++;
				String type = cellPo.getField().getColumnClassName().get();
				int javatype = cellPo.getField().getColumnType().get();
				String columnTypeName = cellPo.getField().getColumnTypeName().get();
				logger.info("javatype = " + javatype + " | " + columnTypeName);
				if (CommonUtility.isDateAndDateTime(javatype)) {
					if (StrUtils.isNullOrEmpty(val.trim())) {
						pstmt.setObject(idx, null);
						insertLog += " | null";
					} else {
						Date dv = StrUtils.StrToDate_L(val);
						pstmt.setObject(idx, dv);
						insertLog += " | " + val;
					}

//				if (CommonUtility.isDateTime(javatype)) {
//					Date dv = DateUtils.StrToDate(val, ConfigVal.dateFormateL);
//					Timestamp ts = new Timestamp(dv.getTime());
//					pstmt.setTimestamp(idx, ts);

					// 是数字又是空字符串，设置为null
				} else if (CommonUtility.isNum(javatype)) {
					val = val.trim();
					if (StrUtils.isNullOrEmpty(val)) { // 空字符串， 设置null
						pstmt.setObject(idx, null);
						insertLog += " | null ";
					} else if (NumberUtils.isParsable(val)) { // 可以转换为数字
						pstmt.setObject(idx, val);
						insertLog += " | " + val;
					} else {
						pstmt.setObject(idx, null); // 其他情况，字符串不能转为数字 设置null
						insertLog += " | null ";
					}
				} else {
					pstmt.setObject(idx, val);
					insertLog += " | " + val;
				}
			}

		}
		logger.info(insertLog);
		int count = pstmt.executeUpdate();

		msg = "Ok, Insert " + count + " ;\n" + insertLog;
		return msg;
	}

	/**
	 * 
	 * @param conn
	 * @param tableName
	 * @param fields
	 * @param fieldsValue 元素不能为null
	 * @return
	 * @throws Exception
	 */
	public static String execInsertBySheetFieldPo(Connection conn, String tableName, List<SheetFieldPo> fields,
			List<List<String>> rowVals) throws Exception {
		String msg = "";
		StringBuilder sql = new StringBuilder("insert into " + tableName + " (");
		StringBuilder values = new StringBuilder("");
		int size = fields.size();
		for (int i = 0; i < size; i++) {
			SheetFieldPo po = fields.get(i);
			sql.append(po.getColumnLabel().get());
			values.append(" ? ");
			sql.append(" ,");
			values.append(" ,");

		}
		String insert = sql.toString();
		String valstr = values.toString();
		if (insert.endsWith(",")) {
			insert = insert.substring(0, insert.length() - 1);
			valstr = valstr.substring(0, values.length() - 1);
		}

		insert += " ) VALUES (" + valstr + ")";

		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(insert);
		String insertLog = insert;

		for (List<String> fieldsValue : rowVals) {
			int idx = 0;
			for (int i = 0; i < size; i++) {
				SheetFieldPo fieldpo = fields.get(i);
				String val = fieldsValue.get(i);
				if (val == null) {
					val = "";
				}
				idx++;
				int javatype = fieldpo.getColumnType().get();
				String columnTypeName = fieldpo.getColumnTypeName().get();
				logger.info("javatype = " + javatype + " | " + columnTypeName);
				// 时间类型判断
				if (CommonUtility.isDateAndDateTime(javatype)) {
					// 空字符串 给字段复制null
					if (StrUtils.isNullOrEmpty(val.trim())) {
						pstmt.setObject(idx, null);
					} else {
						Date dv = StrUtils.StrToDate_L(val);
						pstmt.setObject(idx, dv);
						insertLog += " | " + val;
					}

					// 数字判断
				} else if (CommonUtility.isNum(javatype)) {
					val = val.trim();
					if (StrUtils.isNullOrEmpty(val)) { // 空字符串， 设置null
						pstmt.setObject(idx, null);
						insertLog += " | null ";
					} else if (NumberUtils.isParsable(val)) { // 可以转换为数字
						pstmt.setObject(idx, val);
						insertLog += " | " + val;
					} else {
						pstmt.setObject(idx, null); // 其他情况，字符串不能转为数字 设置null
						insertLog += " | null ";
					}

				} else {
					pstmt.setObject(idx, val);
					insertLog += " | " + val;
				}

			}
			logger.info(insertLog);
			pstmt.addBatch();
		}
		int[] count = pstmt.executeBatch();
		int execCountLen = count.length;
		logger.info("instert = " + execCountLen);
//		int count = pstmt.executeUpdate();

		msg = "Ok, Insert " + execCountLen + " ;\n" + insertLog;
		return msg;
	}

}
