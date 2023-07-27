package net.tenie.Sqlucky.sdk.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.SheetTableData;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;

/**
 * 只读的表查询
 * 
 * @author tenie
 *
 */
public class SelectInfoTableDao {

	private static Logger logger = LogManager.getLogger(SelectInfoTableDao.class);

	public static ResultSetPo selectTableData(ResultSet rs, ObservableList<SheetFieldPo> fields, SqluckyConnector dpo)
			throws SQLException {
		ResultSetPo setPo = new ResultSetPo(fields);
		// 数据
		execRs(rs, fields, dpo, setPo);

		return setPo;
	}

	// 获取查询的结果, 返回字段名称的数据和 值的数据
	public static void selectSql(String sql, SheetTableData std) throws SQLException {
		SqluckyConnector dpo = std.getDbConnection();
		Connection conn = null;
		if (dpo != null) {
			conn = dpo.getConn();
		} else {
			conn = std.getConn();
		}
		// DB对象
		PreparedStatement pstate = null;
		ResultSet rs = null;
		try {
			pstate = conn.prepareStatement(sql);
			// 计时
			long startTime = System.currentTimeMillis(); // 获取开始时间

			// 处理结果集
			rs = pstate.executeQuery();
			long endTime = System.currentTimeMillis(); // 获取结束时间
			long usetime = endTime - startTime;
			double vt = usetime / 1000.0;
			logger.info("查询时间： " + usetime + "ms");
			std.setExecTime(vt);
//			// 获取元数据
			ObservableList<SheetFieldPo> fields = SelectDao.resultSetMetaData(rs);
			ResultSetPo setPo = selectTableData(rs, fields, dpo);

			std.setColss(fields);
			std.setInfoTableVals(setPo);
			std.setRows(setPo.size());
		} catch (SQLException e) {
			throw e;
		} finally {
//			System.out.println("finally");
			if (rs != null)
				rs.close();
		}
	}

	public static void execRs(ResultSet rs, ObservableList<SheetFieldPo> fpo, SqluckyConnector sqluckyConn,
			ResultSetPo setPo) throws SQLException {
		int rowNo = 0;
		int columnnums = fpo.size();

		while (rs.next()) {
			ResultSetRowPo rowpo = setPo.creatRow();
//			ObservableList<ResultSetCellPo> rowDatas = FXCollections.observableArrayList();
			int rn = rowNo++;
			for (int i = 0; i < columnnums; i++) {
				SheetFieldPo fieldpo = fpo.get(i);
				int dbtype = fieldpo.getColumnType().get();
				StringProperty val;
				Object obj = rs.getObject(i + 1);
				if (obj == null) {
					val = new SimpleStringProperty("<null>");
				} else {
					if (CommonUtility.isDateAndDateTime(dbtype)) {
						Object objtmp = rs.getObject(i + 1);
						var dateStr = sqluckyConn.DateTimeToString(objtmp, dbtype);
						val = new SimpleStringProperty(dateStr);
//						if (dpo != null) {
//							var v = dpo.DateToStringStringProperty(rs.getObject(i + 1), dbtype);
//							val = new SimpleStringProperty(v);
//						} else {
//							// TODO dpo null 的情况下
//							Date dv = (Date) rs.getObject(i + 1);
////							String v = StrUtils.dateToStr(dv, ConfigVal.dateFormateL);
//
//							String v = CommonUtility.DateOrDateTimeToString(dbtype, dv);
//							val = new SimpleStringProperty(v);
//						}

					} else {
						String temp = rs.getString(i + 1);
						val = new SimpleStringProperty(temp);
					}
				}
				rowpo.addCell(val, obj, fieldpo);
			}
		}

	}

	// 数据单元格添加监听
	// 字段修改事件
//	public static void addStringPropertyChangeListener(
//			StringProperty val,
//			int rowNo,
//			int idx,
//			ObservableList<ResultSetCellPo> rowDatas,
//			int dbtype,
//			ResultSetPo setPo ) {
//		ChangeListener<String> cl = new ChangeListener<String>() {
//			@Override
//			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//				logger.info("add String Property Change Listener ：newValue：" + newValue + " | oldValue =" + oldValue);
////				logger.info("key ==" + tabId + "-" + rowNo);
//				logger.info("observable = " + observable);
//				// 如果类似是数字的, 新值不是数字, 还原
//				if (CommonUtility.isNum(dbtype) && !StrUtils.isNumeric(newValue) && !"<null>".equals(newValue)) {
//					Platform.runLater(() -> val.setValue(oldValue));
//					return;
//				}
//
//				if (CommonUtility.isDateTime(dbtype) && "".equals(newValue)) {
//					Platform.runLater(() -> val.setValue("<null>"));
//				}
//				if (SqluckyBottomSheetUtility.dataPaneSaveBtn() != null) {
//					SqluckyBottomSheetUtility.dataPaneSaveBtn().setDisable(false);
//				}
//				
//
////				ObservableList<StringProperty> oldDate = FXCollections.observableArrayList();
////				if (!SqluckyBottomSheetUtility.exist( rowNo)) {
////					for (int i = 0; i < rowDatas.size(); i++) {
////						if (i == idx) {
////							oldDate.add(new SimpleStringProperty(oldValue));
////						} else {
////							oldDate.add(rowDatas.get(i).getCellData());
////						}
////					}
////					SqluckyBottomSheetUtility.addData( rowNo, vals, oldDate); // 数据修改缓存, 用于之后更新
////				} else {
////					SqluckyBottomSheetUtility.addData( rowNo, vals);
////				}
//			}
//		};
//		val.addListener(cl);
//	}
}
