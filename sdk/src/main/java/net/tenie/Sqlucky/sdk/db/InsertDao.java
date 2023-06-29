package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;

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
					Date dv = StrUtils.StrToDate_L(val);
					pstmt.setObject(idx, dv);
//				if (CommonUtility.isDateTime(javatype)) {
//					Date dv = DateUtils.StrToDate(val, ConfigVal.dateFormateL);
//					Timestamp ts = new Timestamp(dv.getTime());
//					pstmt.setTimestamp(idx, ts);
					insertLog += " | " + dv;
				} else if (CommonUtility.isNum(javatype) && StrUtils.isNullOrEmpty(val.trim())) {
					pstmt.setObject(idx, null);
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

	public static String execInsertBySheetFieldPo(Connection conn, String tableName, List<SheetFieldPo> fields,
			List<String> fieldsValue) throws Exception {
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

		int idx = 0;
		for (int i = 0; i < size; i++) {
			SheetFieldPo fieldpo = fields.get(i);
			String val = fieldsValue.get(i);
			idx++;
			String type = fieldpo.getColumnClassName().get();
			int javatype = fieldpo.getColumnType().get();
			String columnTypeName = fieldpo.getColumnTypeName().get();
			logger.info("javatype = " + javatype + " | " + columnTypeName);
			if (CommonUtility.isDateAndDateTime(javatype)) {
				Date dv = StrUtils.StrToDate_L(val);
				pstmt.setObject(idx, dv);
				insertLog += " | " + dv;
			} else if (CommonUtility.isNum(javatype) && StrUtils.isNullOrEmpty(val.trim())) {
				pstmt.setObject(idx, null);
			} else {
				pstmt.setObject(idx, val);
				insertLog += " | " + val;
			}

		}
		logger.info(insertLog);
		int count = pstmt.executeUpdate();

		msg = "Ok, Insert " + count + " ;\n" + insertLog;
		return msg;
	}
}
