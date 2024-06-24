package net.tenie.Sqlucky.sdk.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.ParseSQL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.SelectExecInfo;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.db.ProcedureFieldPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

/**
 * 
 * @author tenie
 *
 */
public class SelectDao {

	private static Logger logger = LogManager.getLogger(SelectDao.class);

	public static ObservableList<SheetFieldPo> resultSetMetaData(ResultSet rs) throws SQLException {
		// 获取元数据
		ResultSetMetaData mdata = rs.getMetaData();
		// 获取元数据列数
		Integer columnnums = Integer.valueOf(mdata.getColumnCount());
		// 迭代元数据
		ObservableList<SheetFieldPo> fields = FXCollections.observableArrayList();
		for (int i = 1; i <= columnnums; i++) {
			SheetFieldPo po = new SheetFieldPo();
			po.setScale(mdata.getScale(i));
			po.setColumnName(mdata.getColumnName(i));
			po.setColumnClassName(mdata.getColumnClassName(i));
			po.setColumnDisplaySize(mdata.getColumnDisplaySize(i));
			po.setColumnLabel(mdata.getColumnLabel(i));
			po.setColumnType(mdata.getColumnType(i));
			po.setColumnTypeName(mdata.getColumnTypeName(i));
//			String schemaName = mdata.getSchemaName(i);
			fields.add(po);

		}
		return fields;
	}

	// 获取查询的结果, 返回字段名称的数据和 值的数据
	public static SelectExecInfo selectSql2(String sql, int limit, SqluckyConnector sqluckyConn) throws Exception {
		Connection conn = sqluckyConn.getConn();
		SelectExecInfo execInfo = new SelectExecInfo();

		// DB对象
		PreparedStatement pstate = null;
		ResultSet rs = null;
		// 获取limit 的sql
		int type = ParseSQL.parseType(sql);
		if(type == ParseSQL.SELECT){
			sql = sqluckyConn.getExportDDL().limitSelectSql(sql, limit);
		}

		try {
			logger.info("查询sql ： " + sql );
			pstate = conn.prepareStatement(sql);

			ComponentGetter.setCurrentSqlStatement(pstate);
			// 计时
			long startTime = System.currentTimeMillis(); // 获取开始时间

			// 处理结果集
			rs = pstate.executeQuery();
			long endTime = System.currentTimeMillis(); // 获取结束时间
			long usetime = endTime - startTime;
			double vt = usetime / 1000.0;
			logger.info("查询时间： " + usetime + "ms");
			execInfo.setExecTime(vt);
			if( Thread.currentThread().isInterrupted() ){
				throw new  InterruptedException();
			}
//			// 获取元数据
			ObservableList<SheetFieldPo> fields = resultSetMetaData(rs);
			ResultSetPo setPo = new ResultSetPo(fields);

			// 数据
			if (limit > 0) {
				execRs(limit, rs, sqluckyConn, setPo);
			} else {
				execRs(rs, sqluckyConn, setPo);
			}
			int rowSize = setPo.getDatas().size();

			execInfo.setDataRs(setPo);
			execInfo.setColss(fields);
			execInfo.setRowSize(rowSize);
		} catch (Exception e) {
			throw e;
		} finally {
			logger.debug("finally : selectSql()");
			if (rs != null) {
                rs.close();
            }
		}

		return execInfo;
	}

	public static ResultSetPo selectSqlToRS(String sql, SqluckyConnector sqlConn) throws SQLException {
		Connection conn = sqlConn.getConn();
		ResultSetPo setPo = null;
		// DB对象
		PreparedStatement pstate = null;
		ResultSet rs = null;
		try {
			pstate = conn.prepareStatement(sql);
			// 处理结果集
			rs = pstate.executeQuery();
//			// 获取元数据
			ObservableList<SheetFieldPo> fields = resultSetMetaData(rs);
			setPo = new ResultSetPo(fields);

			// 数据
			execDBRs(rs, sqlConn, setPo);

		} catch (SQLException e) {
			throw e;
		} finally {
			logger.debug("finally: selectSql() return ResultSetPo");
			if (rs != null) {
                rs.close();
            }
		}
		return setPo;
	}

	public static List<String> callProcedure(Connection conn, String proName, List<ProcedureFieldPo> pfp)
			throws SQLException {
		// DB对象
		CallableStatement call = null;
		ResultSet rs = null;
		List<String> val = new ArrayList<>();
		try {
			if (pfp.size() > 0) {
				String callsql = "{call " + proName + "(";
				for (int i = 0; i < pfp.size(); i++) {
					callsql += "? ,";
				}

				callsql = callsql.substring(0, callsql.lastIndexOf(","));
				callsql += " ) }";
				call = conn.prepareCall(callsql);

				for (int i = 0; i < pfp.size(); i++) {
					ProcedureFieldPo po = pfp.get(i);
					if (po.isIn()) {
						call.setObject(i + 1, po.getValue());
					}
					if (po.isOut()) {
						call.registerOutParameter(i + 1, CommonConst.PROCEDURE_TYPE.get(po.getTypeName()));
					}
				}
				// 处理结果集
				call.execute();
				for (int i = 0; i < pfp.size(); i++) {
					ProcedureFieldPo po = pfp.get(i);
					if (po.isOut()) {
						Object objRtn = call.getObject(i + 1);
						val.add(objRtn.toString());
					}

				}

			}

			return val;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
                rs.close();
            }
		}
	}

	// TODO 获取查询的结果, 返回字段名称的数据和 值的数据
	public static void callProcedure(Connection conn, String proName, String tableid, SheetDataValue dvt,
			List<ProcedureFieldPo> pfp) throws SQLException {
		// DB对象
		CallableStatement call = null;
		ResultSet rs = null;
		ObservableList<SheetFieldPo> fields = FXCollections.observableArrayList();
		ObservableList<ObservableList<StringProperty>> val = FXCollections.observableArrayList();
		ObservableList<StringProperty> rowval = FXCollections.observableArrayList();
		try {
			if (pfp.size() > 0) {
				String callsql = "{call " + proName + "(";
				for (int i = 0; i < pfp.size(); i++) {
					callsql += "? ,";
				}

				callsql = callsql.substring(0, callsql.lastIndexOf(","));
				callsql += " ) }";

				call = conn.prepareCall(callsql);
				ComponentGetter.setCurrentSqlStatement(call);
				for (int i = 0; i < pfp.size(); i++) {
					ProcedureFieldPo po = pfp.get(i);
					if (po.isIn()) {
						call.setObject(i + 1, po.getValue());
					}
					if (po.isOut()) {
						call.registerOutParameter(i + 1, CommonConst.PROCEDURE_TYPE.get(po.getTypeName()));
					}
				}

				// 计时
				long startTime = System.currentTimeMillis(); // 获取开始时间
				// 数据库调用
				call.execute();
				long endTime = System.currentTimeMillis(); // 获取结束时间
				long usetime = endTime - startTime;
				double vt = usetime / 1000.0;
				logger.info("查询时间： " + usetime + "ms");
				dvt.setExecTime(vt);

				// 处理结果集
				for (int i = 0; i < pfp.size(); i++) {
					ProcedureFieldPo po = pfp.get(i);
					if (po.isOut()) {
						Object objRtn = call.getObject(i + 1);
						rowval.add(CommonUtils.createReadOnlyStringProperty(objRtn.toString()));

						// 字段信息
						SheetFieldPo sfpo = new SheetFieldPo();
						sfpo.setScale(0);
						sfpo.setColumnName(po.getName());
						sfpo.setColumnClassName("");
						sfpo.setColumnDisplaySize(0);
						sfpo.setColumnLabel(po.getName());
						sfpo.setColumnType(po.getType());
						sfpo.setColumnTypeName(po.getTypeName());
						fields.add(sfpo);
					}

				}

			}

			// 数据
//			 val = simpleExecRs(rs, fields); 
			if (rowval.size() > 0) {
				val.add(rowval);
			}
//			 dvt.set
			dvt.setColss(fields);
//			dvt.setRawData(val);
			dvt.setRows(val.size());
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
                rs.close();
            }
		}
	}

	public static DbTableDatePo selectSqlField(Connection conn, String sql) throws SQLException {
		DbTableDatePo dpo = new DbTableDatePo();
		// DB对象
		PreparedStatement pstate = null;
		ResultSet rs = null;
		try {
			pstate = conn.prepareStatement(sql);
			// 处理结果集
			rs = pstate.executeQuery();
			// 获取元数据
			ResultSetMetaData mdata = rs.getMetaData();
			// 获取元数据列数
			Integer columnnums = Integer.valueOf(mdata.getColumnCount());

			for (int i = 1; i <= columnnums; i++) {
				SheetFieldPo po = new SheetFieldPo();
				po.setScale(mdata.getScale(i));
				po.setColumnName(mdata.getColumnName(i));
				po.setColumnClassName(mdata.getColumnClassName(i));
				po.setColumnDisplaySize(mdata.getColumnDisplaySize(i));
				po.setColumnLabel(mdata.getColumnLabel(i));
				po.setColumnType(mdata.getColumnType(i));
				po.setColumnTypeName(mdata.getColumnTypeName(i));
				dpo.addField(po);

			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
                rs.close();
            }
		}
		return dpo;
	}

	private static void execRs(int limit, ResultSet rs, SqluckyConnector sqluckyConn, ResultSetPo setPo)
			throws SQLException , InterruptedException{
		int idx = 1;
		ObservableList<SheetFieldPo> fpo = setPo.getFields();
		int columnnums = fpo.size();
		while (rs.next()) {
			if( Thread.currentThread().isInterrupted() ){
				throw new  InterruptedException();
			}
			ResultSetRowPo rowpo = new ResultSetRowPo(setPo);

			for (int i = 0; i < columnnums; i++) {
				SheetFieldPo fieldpo = fpo.get(i);
				String clabel = fieldpo.getColumnLabel().get();

				String cclaz = fieldpo.getColumnClassName().get();
//				System.out.println("field name = " + clabel + " | class name = " + cclaz );

				int dbtype = fieldpo.getColumnType().get();
				StringProperty val;
//				Date valDate = null;
				Object obj = null;
				try {
					obj = rs.getObject(i + 1);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (obj == null) {
					val = new SimpleStringProperty("<null>");
				} else {
					if (CommonUtils.isDateAndDateTime(dbtype)) {
						Object objtmp = rs.getObject(i + 1);
						String dateStr = sqluckyConn.DateTimeToString(objtmp, dbtype);
						if (dateStr != null) {
							val = new SimpleStringProperty(dateStr);
						} else {
							val = new SimpleStringProperty("<null>");
						}
//						if (sqluckyConn != null) {
//							
//						}
//						else {
//							// TODO dpo null 的情况下
//							Date dv = (Date) rs.getObject(i + 1);
//							String v = CommonUtility.DateOrDateTimeToString(dbtype, dv);
////							String v = StrUtils.dateToStr(dv, ConfigVal.dateFormateL);
//							val = new SimpleStringProperty(v);
//						}

					} else {
						String temp = rs.getString(i + 1);
						val = new SimpleStringProperty(temp);
					}
				}
				rowpo.addCell(val, obj, fieldpo);
			}

			setPo.addRow(rowpo);
			// CELL 监听
//			rowpo.cellAddChangeListener();
			if (idx == limit) {
				break;
			}

			idx++;
		}
	}

	private static void execRs(ResultSet rs, SqluckyConnector dpo, ResultSetPo setPo) throws Exception {
		execRs(Integer.MAX_VALUE, rs, dpo, setPo);
	}

	/**
	 * 从数据库返回集中获取数据后转换为对象
	 * 
	 * @param rs
	 * @param setPo
	 * @throws SQLException
	 */
	public static void execDBRs(ResultSet rs, SqluckyConnector sqluckyConn, ResultSetPo setPo) throws SQLException {
		ObservableList<SheetFieldPo> fpo = setPo.getFields();
		int columnnums = fpo.size();
		while (rs.next()) {
			ResultSetRowPo rowpo = new ResultSetRowPo(setPo);

			for (int i = 0; i < columnnums; i++) {
				SheetFieldPo fieldpo = fpo.get(i);
				int dbtype = fieldpo.getColumnType().get();
				StringProperty val;
				Object obj = rs.getObject(i + 1);
				if (obj == null) {
					val = new SimpleStringProperty("<null>");
				} else {
					if (CommonUtils.isDateAndDateTime(dbtype)) {
						var dateStr = sqluckyConn.DateTimeToString(obj, dbtype);
						val = new SimpleStringProperty(dateStr);
					} else {
						String temp = rs.getString(i + 1);
						val = new SimpleStringProperty(temp);
					}
				}
				rowpo.addCell(val, obj, fieldpo);
			}
			setPo.addRow(rowpo);
		}

	}

	// 执行sql只返回第一个字段的list
	public static String selectOne(Connection conn, String sql) {
		ResultSet rs = null;
		String str = "";
		try {
			rs = conn.createStatement().executeQuery(sql);
			if (rs.next()) {
				str = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		return str;
	}
}
