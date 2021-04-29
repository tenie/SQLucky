package net.tenie.fx.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.tenie.fx.PropertyPo.CacheTabView;
import net.tenie.fx.PropertyPo.DbTableDatePo;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.container.DataViewTab;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class SelectDao {

	private static Logger logger = LogManager.getLogger(SelectDao.class);
	
	public static ObservableList<SqlFieldPo> resultSetMetaData(ResultSet rs ) throws SQLException {
		// 获取元数据
		ResultSetMetaData mdata = rs.getMetaData();
		// 获取元数据列数
		Integer columnnums = Integer.valueOf(mdata.getColumnCount());
		// 迭代元数据
		ObservableList<SqlFieldPo> fields = FXCollections.observableArrayList();
		for (int i = 1; i <= columnnums; i++) {
			SqlFieldPo po = new SqlFieldPo();
			po.setScale(mdata.getScale(i));
			po.setColumnName(mdata.getColumnName(i));
			po.setColumnClassName(mdata.getColumnClassName(i));
			po.setColumnDisplaySize(mdata.getColumnDisplaySize(i));
			po.setColumnLabel(mdata.getColumnLabel(i));
			po.setColumnType(mdata.getColumnType(i));
			po.setColumnTypeName(mdata.getColumnTypeName(i));
			fields.add(po);

		}
		return fields;
	}
	
	// 获取查询的结果, 返回字段名称的数据和 值的数据
	public static void selectSql(Connection conn, String sql, int limit,
			String tableid , DataViewTab dvt ) throws SQLException {
//		DbTableDatePo dpo = new DbTableDatePo();
		// DB对象
		PreparedStatement pstate = null;
		ResultSet rs = null;
		try {
			pstate = conn.prepareStatement(sql);
			// 计时
			long startTime=System.currentTimeMillis();   //获取开始时间   
		
			// 处理结果集
			rs = pstate.executeQuery();
			long endTime=System.currentTimeMillis(); //获取结束时间
			long usetime = endTime-startTime;
			double vt = usetime / 1000.0;
			logger.info("查询时间： "+usetime+"ms");
			dvt.setExecTime(vt); 
//			// 获取元数据
//			ResultSetMetaData mdata = rs.getMetaData();
//			// 获取元数据列数
//			Integer columnnums = Integer.valueOf(mdata.getColumnCount());
//			// 迭代元数据
//			ObservableList<SqlFieldPo> fields = FXCollections.observableArrayList();
//			for (int i = 1; i <= columnnums; i++) {
//				SqlFieldPo po = new SqlFieldPo();
//				po.setScale(mdata.getScale(i));
//				po.setColumnName(mdata.getColumnName(i));
//				po.setColumnClassName(mdata.getColumnClassName(i));
//				po.setColumnDisplaySize(mdata.getColumnDisplaySize(i));
//				po.setColumnLabel(mdata.getColumnLabel(i));
//				po.setColumnType(mdata.getColumnType(i));
//				po.setColumnTypeName(mdata.getColumnTypeName(i));
//				fields.add(po);
//				
//			}
			ObservableList<SqlFieldPo> fields = resultSetMetaData(rs);
			
			ObservableList<ObservableList<StringProperty>>  val ;
			// 数据
			if (limit > 0) {
				 val = execRs(limit, rs, fields, tableid);
			} else {
				 val =  execRs(rs, fields, tableid);
			}
			
			dvt.setColss(fields);
			dvt.setRawData(val);
			dvt.setRows(val.size());
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				rs.close();
		} 
	}
	
	
	//TODO 获取查询的结果, 返回字段名称的数据和 值的数据
	public static void callProcedure(Connection conn, String sql ,String tableid , DataViewTab dvt ) throws SQLException {
		// DB对象
		CallableStatement call = null;
		ResultSet rs = null;
		try {
			String callsql = "{call "+sql+"}";
			call = conn.prepareCall(callsql);
//			java.sql.Types.VARCHAR;
			call.registerOutParameter(3, java.sql.Types.VARCHAR);
			// 计时
			long startTime=System.currentTimeMillis();   //获取开始时间   
		
			// 处理结果集
		    call.execute(); 
		    call.getObject(4 );
//		    call.registerOutParameter(0, null);
//		    call.
		    
		    
		    
			long endTime=System.currentTimeMillis(); //获取结束时间
			long usetime = endTime-startTime;
			double vt = usetime / 1000.0;
			logger.info("查询时间： "+usetime+"ms");
			dvt.setExecTime(vt);  
			// 字段信息
			ObservableList<SqlFieldPo> fields = resultSetMetaData(rs);
			
			ObservableList<ObservableList<StringProperty>>  val ;
			// 数据
			 val = simpleExecRs(rs, fields); 
			
			dvt.setColss(fields);
			dvt.setRawData(val);
			dvt.setRows(val.size());
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				rs.close();
		} 
	}
	
	
	
	public static DbTableDatePo selectSqlField(Connection conn, String sql ) throws SQLException {
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
				SqlFieldPo po = new SqlFieldPo();
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
			if (rs != null)
				rs.close();
		}
		return dpo;
	}
	

	private static ObservableList<ObservableList<StringProperty>>  execRs(int limit, ResultSet rs, ObservableList<SqlFieldPo> fpo,
			String tableid) throws SQLException {
		int idx = 1;
		int rowNo = 0;
//		ObservableList<SqlFieldPo> fpo = dpo.getFields();
		int columnnums = fpo.size();
//		ObservableList<StringProperty> vals = FXCollections.observableArrayList();
		ObservableList<ObservableList<StringProperty>> allDatas = FXCollections.observableArrayList();
		while (rs.next()) {
			ObservableList<StringProperty> vals = FXCollections.observableArrayList();
			int rn = rowNo++;
			for (int i = 0; i < columnnums; i++) {
//				String field = fpo.get(i).getColumnLabel().get();
				int dbtype = fpo.get(i).getColumnType().get();
				StringProperty val;
				
				Object obj = rs.getObject(i + 1);
				if(obj == null) {
					val = new SimpleStringProperty("<null>");
				}else {
					if (CommonUtility.isDateTime(dbtype)) {
						java.sql.Timestamp ts = rs.getTimestamp(i + 1);
						Date d = new Date(ts.getTime());
						String v = StrUtils.dateToStr(d, ConfigVal.dateFormateL);
						val = new SimpleStringProperty(v);
					} else {
						String temp = rs.getString(i+1);
						val = new SimpleStringProperty(temp); 
					}
				}
				
				
				addStringPropertyChangeListener(val, rn, tableid, i, vals, dbtype);
				vals.add(val);
			}

			vals.add(new SimpleStringProperty(rn + ""));
//			dpo.addData(vals);
			allDatas.add(vals);

			if (idx == limit)
				break;
			idx++;
		}
		
		return allDatas;
	}

	

	public static ObservableList<ObservableList<StringProperty>>  simpleExecRs( ResultSet rs, ObservableList<SqlFieldPo> fpo ) throws SQLException {
		int idx = 1;
		int rowNo = 0; 
		int columnnums = fpo.size(); 
		ObservableList<ObservableList<StringProperty>> allDatas = FXCollections.observableArrayList();
		while (rs.next()) {
			ObservableList<StringProperty> vals = FXCollections.observableArrayList();
			int rn = rowNo++;
			for (int i = 0; i < columnnums; i++) { 
				int dbtype = fpo.get(i).getColumnType().get();
				StringProperty val;
				
				Object obj = rs.getObject(i + 1);
				if(obj == null) {
					val = new SimpleStringProperty("<null>");
				}else {
					if (CommonUtility.isDateTime(dbtype)) {
						java.sql.Timestamp ts = rs.getTimestamp(i + 1);
						Date d = new Date(ts.getTime());
						String v = StrUtils.dateToStr(d, ConfigVal.dateFormateL);
						val = new SimpleStringProperty(v);
					} else {
						String temp = rs.getString(i+1);
						val = new SimpleStringProperty(temp); 
					}
				}
				 
				vals.add(val);
			} 
			vals.add(new SimpleStringProperty(rn + "")); 
			allDatas.add(vals); 
			idx++;
		}
		
		return allDatas;
	}

	
	
	private static ObservableList<ObservableList<StringProperty>>   execRs(ResultSet rs, ObservableList<SqlFieldPo> fpo, String tableid)
			throws SQLException {
		return execRs(Integer.MAX_VALUE, rs, fpo, tableid);
	}

	
    // 数据单元格添加监听
	// 字段修改事件
	public static void addStringPropertyChangeListener(StringProperty val, int rowNo, String tabId, int idx,
			ObservableList<StringProperty> vals, int dbtype) {
		ChangeListener<String> cl = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				logger.info("addStringPropertyChangeListener ：newValue：" + newValue + " | oldValue =" + oldValue);
				logger.info("key ==" + tabId + "-" + rowNo);
				logger.info("observable = " + observable);
				// 如果类似是数字的, 新值不是数字, 还原
				if (CommonUtility.isNum(dbtype) && !StrUtils.isNumeric(newValue) && !"<null>".equals(newValue)) {
					Platform.runLater(() -> val.setValue(oldValue));
					return;
				}
				
				if(CommonUtility.isDateTime(dbtype) && "".equals(newValue )) {
					Platform.runLater(() -> val.setValue("<null>"));
				}
				
				ComponentGetter.dataPaneSaveBtn().setDisable(false);
				

				ObservableList<StringProperty> oldDate = FXCollections.observableArrayList();
				if (!CacheTabView.exist(tabId, rowNo)) {
					for (int i = 0; i < vals.size(); i++) {
						if (i == idx) {
							oldDate.add(new SimpleStringProperty(oldValue));
						} else {
							oldDate.add(new SimpleStringProperty(vals.get(i).get()));
						}
					}
					CacheTabView.addData(tabId, rowNo, vals, oldDate); // 数据修改缓存, 用于之后更新
				} else {
					CacheTabView.addData(tabId, rowNo, vals);
				}
			}
		};
		val.addListener(cl);
	}
}
