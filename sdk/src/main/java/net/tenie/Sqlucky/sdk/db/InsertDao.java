package net.tenie.Sqlucky.sdk.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.po.ImportFieldPo;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
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
				if (CommonUtils.isDateAndDateTime(javatype)) {
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
				} else if (CommonUtils.isNum(javatype)) {
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
	public static String execInsertByExcelField_bak(Connection conn, String tableName, List<ImportFieldPo> fields,
			List<List<String>> rowVals) throws Exception {
		String msg = "";
		String insertLog = "";
		String valLog = "";
		String logString = "";
		try {

			StringBuilder sql = new StringBuilder("insert into " + tableName + " (");
			StringBuilder values = new StringBuilder("");
			int size = fields.size();
			for (int i = 0; i < size; i++) {
				ImportFieldPo po = fields.get(i);
				sql.append(po.getColumnLabel().get());

				if (StrUtils.isNotNullOrEmpty(po.getFixedValue().get())) {
					String tmp = po.getFixedValue().get();
					values.append(tmp);
				} else {
					values.append(" ? ");
				}

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
			insertLog = insert;

			for (List<String> fieldsValue : rowVals) {
				int idx = 0;
				for (int i = 0; i < size; i++) {
					ImportFieldPo fieldpo = fields.get(i);
					String val = fieldsValue.get(i);
					if (val == null) {
						val = "";
					}
					idx++;
					int javatype = fieldpo.getColumnType().get();
					String columnTypeName = fieldpo.getColumnTypeName().get();
//					logger.info("javatype = " + javatype + " | " + columnTypeName);
//					logString += "javatype = " + javatype + " | " + columnTypeName;
					valLog += " | " + val;
					if (StrUtils.isNotNullOrEmpty(fieldpo.getFixedValue().get())) {
						String tmp = fieldpo.getFixedValue().get();
//						values.append(tmp);
						System.out.println(tmp);
						idx--;
					}
					// 时间类型判断
					else if (CommonUtils.isDateAndDateTime(javatype)) {
						// 空字符串 给字段复制null
						if (StrUtils.isNullOrEmpty(val.trim())) {
							pstmt.setObject(idx, null);
						} else {
							Date dv = StrUtils.StrToDate_L(val);
							pstmt.setObject(idx, dv);

						}

						// 数字判断
					} else if (CommonUtils.isNum(javatype)) {
						val = val.trim();
						if (StrUtils.isNullOrEmpty(val)) { // 空字符串， 设置null
							pstmt.setObject(idx, null);
						} else if (NumberUtils.isParsable(val)) { // 可以转换为数字
							pstmt.setObject(idx, val);
						} else {
							pstmt.setObject(idx, null); // 其他情况，字符串不能转为数字 设置null
						}

					} else {
						pstmt.setObject(idx, val);
					}

				}
				logger.info(insertLog);
				pstmt.addBatch();
			}
			logger.info(logString);
			int[] count = pstmt.executeBatch();
			int execCountLen = count.length;
			logger.info("instert = " + execCountLen);

			msg = "Insert " + execCountLen + " ;\n" + insertLog + "; \n" + valLog;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage() + " : excel Value = " + valLog + " ;\n sql = " + insertLog);
		}
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
	public static String execInsertByExcelField(Connection conn, String tableName, List<ImportFieldPo> fields,
			List<List<String>> rowVals, String saveSqlfileStr, boolean onlySaveSql) throws Exception {
		String msg = "";
		String insertLog = "";
		String valLog = "";
		String logString = "";
		File saveSqlFile = new File(saveSqlfileStr);

		List<String> inSqls = new ArrayList<>();
		try {

			StringBuilder sql = new StringBuilder("insert into " + tableName + " (");
			int size = fields.size();
			for (int i = 0; i < size; i++) {
				ImportFieldPo po = fields.get(i);
				sql.append(po.getColumnLabel().get());
				sql.append(" ,");

			}
			String insert = sql.toString();
			if (insert.endsWith(",")) {
				insert = insert.substring(0, insert.length() - 1);
			}

			insert += " ) VALUES ( ";

			Statement sm = conn.createStatement();
			insertLog = insert;
			inSqls = new ArrayList<>();

			for (List<String> fieldsValue : rowVals) {
				String insertValue = "";
				for (int i = 0; i < size; i++) {
					ImportFieldPo fieldpo = fields.get(i);
					String val = fieldsValue.get(i);
					if (val == null) {
						val = "";
					}
					int javatype = fieldpo.getColumnType().get();
//					String columnTypeName = fieldpo.getColumnTypeName().get();
//					logger.info("javatype = " + javatype + " | " + columnTypeName);
//					logString += "javatype = " + javatype + " | " + columnTypeName;
					valLog += " | " + val;
					// 先判断是不是固定值, 比如使用了序列, 默认值之类的, 因为输入的固定值可以直接作为sql一部分不需要多余操作
					if (StrUtils.isNotNullOrEmpty(fieldpo.getFixedValue().get())) {
						String tmp = fieldpo.getFixedValue().get();
						insertValue += tmp;
					}
					// 时间类型判断
					else if (CommonUtils.isDateAndDateTime(javatype)) {
						// 空字符串 给字段复制null
						if (StrUtils.isNullOrEmpty(val.trim())) {
							insertValue += "null";
						} else {
							insertValue += "'" + val + "'";

						}

						// 数字判断
					} else if (CommonUtils.isNum(javatype)) {
						val = val.trim();
						if (StrUtils.isNullOrEmpty(val)) { // 空字符串， 设置null
							insertValue += "null";
						} else if (NumberUtils.isParsable(val)) { // 可以转换为数字
							insertValue += val;
						} else {
							// 其他情况，字符串不能转为数字 设置null
							insertValue += "null";
						}

					} else {// 字符串
						String tmpVal = transferredSingleQuotation(val);
						insertValue += "'" + tmpVal + "'";
					}
					insertValue += " ,";

				}
				if (insertValue.endsWith(",")) {
					insertValue = insertValue.substring(0, insertValue.length() - 1);
//					insert += insertValue + " )";
					insertValue = insert + insertValue + " )";
//					logger.info(insertValue);
					if (onlySaveSql) {
						inSqls.add(insertValue + ";");
					} else {
						sm.addBatch(insertValue);
						inSqls.add(insertValue + ";");
					}

				}

			}
			logger.info(logString);

			if (onlySaveSql) {
//				if (StrUtils.isNotNullOrEmpty(saveSqlfileStr) && saveSqlFile.exists()) {
//					FileUtils.writeLines(saveSqlFile, inSqls, true);
//				}

				writeSqlFile(saveSqlfileStr, saveSqlFile, inSqls);
			} else {
				int[] count = sm.executeBatch();
				int execCountLen = count.length;
				logger.info("instert = " + execCountLen);
				msg = "Insert " + execCountLen + " ;\n" + insertLog + "; \n" + valLog;

				writeSqlFile(saveSqlfileStr, saveSqlFile, inSqls);
//				if (StrUtils.isNotNullOrEmpty(saveSqlfileStr) && saveSqlFile.exists()) {
//					FileUtils.writeLines(saveSqlFile, inSqls, true);
//				}

			}

		} catch (Exception e) {
			e.printStackTrace();

			writeSqlFile(saveSqlfileStr, saveSqlFile, inSqls);
			throw new Exception(e.getMessage() + inSqls);
		}
		return msg;
	}

	private static void writeSqlFile(String saveSqlfileStr, File saveSqlFile, List<String> inSqls) throws IOException {
		if (StrUtils.isNotNullOrEmpty(saveSqlfileStr) && saveSqlFile.exists()) {
			FileUtils.writeLines(saveSqlFile, inSqls, true);
		}
	}

	/**
	 * 将文本字符串中的单引号转换成可以在sql中使用的字符串('转义为'')
	 * 
	 * @param val
	 * @return
	 */
	public static String transferredSingleQuotation(String val) {
		String str = val;
		if (val.indexOf("'") > -1) {
			str = val.replaceAll("'", "''");
		}
		return str;
	}

	// sql 字符串中的单引号转移为 ''
	public static String sqlStrTransferredSingleQuotation(String val) {
		String str = val;
		int lastIndex = str.lastIndexOf("'");
		str = str.substring(1, lastIndex);

		if (str.indexOf("'") > -1) {
			str = str.replaceAll("'", "''");
		}
		str = "'" + str + "'";
		return str;
	}

	// 将字符串值转为可以拼接到sql中的字符串
	public static String stringToDBString(String val) {
		String rsVal = "";
		if ((val.startsWith("'") && val.endsWith("'"))) {
			rsVal = val;
		} else if (val.startsWith("\"") && val.endsWith("\"")) {
			rsVal = "'" + val.substring(1, val.lastIndexOf("\"")) + "'";
		} else {
			rsVal = "'" + val + "'";
		}

		return rsVal;
	}

	public static String trimQuotation(String val) {
		String rsVal = "";
		if ((val.startsWith("'") && val.endsWith("'"))) {
			rsVal = val.substring(1, val.lastIndexOf("'"));
		} else if (val.startsWith("\"") && val.endsWith("\"")) {
			rsVal = val.substring(1, val.lastIndexOf("\""));
		} else {
			rsVal = val;
		}

		return rsVal;
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
	public static String execInsertByCsvField(Connection conn, String tableName, List<ImportFieldPo> fields,
			List<List<String>> rowVals, String saveSqlfile, boolean onlySaveSql, boolean saveSql) throws Exception {
		String msg = "";
		String insertLog = "";
		String valLog = "";

		List<String> inSqls = new ArrayList<>();
		try {

			StringBuilder sql = new StringBuilder("insert into " + tableName + " (");
			int size = fields.size();
			for (int i = 0; i < size; i++) {
				ImportFieldPo po = fields.get(i);
				sql.append(po.getColumnLabel().get());
				sql.append(" ,");

			}
			String insert = sql.toString();
			if (insert.endsWith(",")) {
				insert = insert.substring(0, insert.length() - 1);
			}

			insert += " ) VALUES ( ";

			Statement sm = conn.createStatement();
			insertLog = insert;

			for (List<String> fieldsValue : rowVals) {
				String insertValue = "";
				for (int i = 0; i < size; i++) {
					ImportFieldPo fieldpo = fields.get(i);
					String val = fieldsValue.get(i);
					if (val == null) {
						val = "";
					} else {
						val = val.trim();
					}
					int javatype = fieldpo.getColumnType().get();
//					String columnTypeName = fieldpo.getColumnTypeName().get();
//					logger.info("javatype = " + javatype + " | " + columnTypeName);
					valLog += " | " + val;
					// 固定值
					if (StrUtils.isNotNullOrEmpty(fieldpo.getFixedValue().get())) {
						String tmp = fieldpo.getFixedValue().get();
						insertValue += tmp;
					}
					// 时间类型判断
					else if (CommonUtils.isDateAndDateTime(javatype)) {
						// 空字符串 给字段复制null
						if (StrUtils.isNullOrEmpty(val.trim())) {
							insertValue += "NULL";
						} else {
							val = trimQuotation(val); // 去除引号
							String tmpval = val.toUpperCase();
							if ("NULL".equals(tmpval)) {
								insertValue += "NULL";
							} else {
								insertValue += stringToDBString(val);
							}
						}

						// 数字判断
					} else if (CommonUtils.isNum(javatype)) {
						val = val.trim();
						if (StrUtils.isNullOrEmpty(val)) { // 空字符串， 设置null
							insertValue += "NULL";
						} else {
							val = trimQuotation(val);
							if (NumberUtils.isParsable(val)) { // 可以转换为数字
								insertValue += val;
							} else {
								// 其他情况，字符串不能转为数字 设置null
								insertValue += "NULL";
							}
						}

					} else {// 字符串
						if ("NULL".equals(val.toUpperCase())) {
							insertValue += "NUll";
						} else {
							String valtmp = stringToDBString(val);
							valtmp = sqlStrTransferredSingleQuotation(valtmp);
							insertValue += valtmp; // stringToDBString(val);
						}
					}
					insertValue += " ,";

				}
				if (insertValue.endsWith(",")) {
					insertValue = insertValue.substring(0, insertValue.length() - 1);
					insertValue = insert + insertValue + " )";
					logger.info(insertValue);
					if (onlySaveSql) {
						inSqls.add(insertValue + ";");
					} else {
						sm.addBatch(insertValue);
						inSqls.add(insertValue + ";");
					}

				}
			}
			if (saveSql) {
				FileUtils.writeLines(new File(saveSqlfile), inSqls, true);
			}
			if (onlySaveSql == false) {
				int[] count = sm.executeBatch();
				int execCountLen = count.length;
				logger.info("instert = " + execCountLen);
				msg = "Insert " + execCountLen + " ;\n" + insertLog + "; \n" + valLog;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(inSqls);
			throw new Exception(e.getMessage() + " : excel Value = " + valLog + " ;\n sql = " + insertLog);
		}
		return msg;
	}

}
