package net.tenie.fx.utility;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import net.sf.jsqlparser.JSQLParserException;
//import net.sf.jsqlparser.parser.CCJSqlParserUtil;
//import net.sf.jsqlparser.statement.Statement;
//import net.sf.jsqlparser.statement.select.Select;
//import net.sf.jsqlparser.util.TablesNamesFinder;
import net.tenie.lib.db.h2.SqlTextDao;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/*   @author tenie */
public class ParseSQL {
	private static Logger logger = LogManager.getLogger(ParseSQL.class);

	public static final int SELECT = 1;
	public static final int UPDATE = 2;
	public static final int INSERT = 3;
	public static final int DELETE = 4;
	public static final int DROP   = 5;
	public static final int ALTER  = 6;
	public static final int CREATE = 7;
	public static final int OTHER  = 8;

	public static int parseType(String sql) {
		String temp = StrUtils.trimComment(sql, "--");
		if (StrUtils.beginWithNotSensitive(temp, "SELECT")) {
			return SELECT;
		} else if (StrUtils.beginWithNotSensitive(temp, "WITH")) {
			return SELECT;
		} else if (StrUtils.beginWithNotSensitive(temp, "VALUES")) {
			return SELECT;
		} else if (StrUtils.beginWithNotSensitive(temp, "EXPLAIN")) {
			return SELECT;
		} else if (StrUtils.beginWithNotSensitive(temp, "UPDATE")) {
			return UPDATE;
		} else if (StrUtils.beginWithNotSensitive(temp, "INSERT")) {
			return INSERT;
		} else if (StrUtils.beginWithNotSensitive(temp, "DELETE")) {
			return DELETE;
		} else if (StrUtils.beginWithNotSensitive(temp, "DROP")) {
			return DROP;
		} else if (StrUtils.beginWithNotSensitive(temp, "ALTER")) {
			return ALTER;
		} else if (StrUtils.beginWithNotSensitive(temp, "CREATE")) {
			return CREATE;
		}
		return OTHER;
	}

	// 获取 表名
	public static String tabName(String sql) {
		String temp = StrUtils.trimComment(sql, "--");
		temp = StrUtils.clearBlank(temp);
		temp = temp.toUpperCase();
		String val = "";
		String key = "";
		int type = parseType(temp);
		switch (type) {
		case SELECT:
			key = " FROM ";
			return findSelectTabName(temp, key);
		case UPDATE:
			key = "UPDATE ";
			break;
		case INSERT:
			key = "INSERT INTO ";
			break;
		case CREATE:
			key = "CREATE TABLE ";
			break;
		case DELETE:
			key = "DELETE FROM";
			break;
		case DROP:
			key = "DROP TABLE";
			break;
		case ALTER:
			key = "ALTER ";
			break;
		}
		val = findOtherSqlTabName(temp, key);

		return val;
	}

	private static String findOtherSqlTabName(String sql, String key) {
		String val = "";
		String temp = sql;
		int idx = temp.indexOf(key, 0);
		int klen = key.length();
		if (idx == 0) {
			temp = temp.substring(0, klen);
			int i = temp.indexOf(' ');
			if (i > 0) {
				val = temp.substring(0, i);
			} else {
				val = temp;
			}
		}

		return val;
	}

	private static String findSelectTabName(String sql, String key) {
		String val = "";
		String temp = sql;
		int idx = sql.indexOf(key, 0);
		int klen = key.length();
		while (idx > 0) {
			temp = temp.substring(idx + klen).trim();
			logger.info(temp);
			if (StrUtils.beginWith(temp, "(")) {
				idx = temp.indexOf(key, 0);
			} else {
				temp = temp.replace(")", " ");
				int i = temp.indexOf(' ');
				if (i > 0) {
					val = temp.substring(0, i);
				} else {
					val = temp;
				}
				break;
			}
		}
		return val;
	}

//	public static List<String> test_select_table(String sql) throws JSQLParserException {
//		Statement statement = CCJSqlParserUtil.parse(sql);
//		Select selectStatement = (Select) statement;
//		TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
//		return tablesNamesFinder.getTableList(selectStatement);
//	}
//
//	public static String tabName(Statement selectStatement) {
//		TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
//		List<String> val = tablesNamesFinder.getTableList(selectStatement);
//		if (val != null && val.size() > 0) {
//			return val.get(0);
//		}
//		return "";
//	}

}
