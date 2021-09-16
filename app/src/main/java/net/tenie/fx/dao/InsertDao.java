package net.tenie.fx.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.lib.reflex.BuildObject;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/*   @author tenie */
public class InsertDao {

	private static Logger logger = LogManager.getLogger(InsertDao.class);
	/**
	 * 提供一行表格数据, 做插入保存
	 * @param conn			数据库连接
	 * @param tableName		表
	 * @param data			列的数据
	 * @param fpos			列的数据字段类型
	 * @return
	 * @throws Exception
	 */
	public static String execInsert(Connection conn, String tableName, ObservableList<StringProperty> data,
			ObservableList<SqlFieldPo> fpos) throws Exception {
		String msg = "";
		StringBuilder sql = new StringBuilder("insert into " + tableName + " (");
		StringBuilder values = new StringBuilder("");
		int size = fpos.size();
		for (int i = 0; i < size; i++) {
			SqlFieldPo po = fpos.get(i);
			String temp = data.get(i).get();
			if (StrUtils.isNotNullOrEmpty(temp) && !"<null>".equals(temp)) {
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
		logger.info(insert);
		int idx = 0;
		for (int i = 0; i < size; i++) {
			String val = data.get(i).get();
			if (StrUtils.isNotNullOrEmpty(val) && !"<null>".equals(val)) {
				idx++;
				String type = fpos.get(i).getColumnClassName().get();
				int javatype = fpos.get(i).getColumnType().get();

				if (CommonUtility.isDateTime(javatype)) {
					Date dv = StrUtils.StrToDate(val, ConfigVal.dateFormateL);
					Timestamp ts = new Timestamp(dv.getTime());
					pstmt.setTimestamp(idx, ts);
					logger.info(idx + "  " + ts);
				} else {
					Object obj = BuildObject.buildObj(type, val);
					pstmt.setObject(idx, obj);
					logger.info(idx + "  " + obj);
				}
			}

		}
		int count = pstmt.executeUpdate();

		msg = "Ok, Insert " + count;
		return msg;
	}
}
