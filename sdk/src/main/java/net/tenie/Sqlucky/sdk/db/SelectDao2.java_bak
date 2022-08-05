package net.tenie.Sqlucky.sdk.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheetUtility;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.ProcedureFieldPo;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * 
 * @author tenie
 *
 */
public class SelectDao2 {

	private static Logger logger = LogManager.getLogger(SelectDao2.class);
	
	public static ObservableList<SheetFieldPo> resultSetMetaData(ResultSet rs ) throws SQLException {
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
			fields.add(po);

		}
		return fields;
	}
	
	// 获取查询的结果, 返回字段名称的数据和 值的数据
	public static void selectSql( String sql, int limit, SheetDataValue dvt ) throws SQLException {
		SqluckyConnector dpo  = dvt.getDbConnection();
		Connection conn = null;
		if(dpo != null) {
			 conn = dpo.getConn();
		}else {
			conn =  dvt.getConn();
		}
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
			ObservableList<SheetFieldPo> fields = resultSetMetaData(rs);
			
			ObservableList<ObservableList<StringProperty>>  val ;
			// 数据
			if (limit > 0) {
				 val = execRs(limit, rs, fields,   dpo);
			} else {
				 val =  execRs(rs, fields, dpo);
			}
			
			dvt.setColss(fields);
			dvt.setRawData(val);
			dvt.setRows(val.size());
		} catch (SQLException e) {
			throw e;
		} finally {
			System.out.println("finally");
			if (rs != null)
				rs.close();
		} 
	}
	
	
	
	
	public static List<String> callProcedure(Connection conn , String proName, List<ProcedureFieldPo> pfp ) throws SQLException {
		// DB对象
		CallableStatement call = null;
		ResultSet rs = null;
		List<String> val = new ArrayList<>();
		try {
			if(pfp.size() > 0) {
				String callsql = "{call " + proName+ "(";
				for(int i = 0 ; i < pfp.size(); i++) {
					callsql += "? ,";
				}
				
				callsql = callsql.substring(0, callsql.lastIndexOf(","));
				callsql += " ) }"; 
				call = conn.prepareCall(callsql);
				
				for(int i = 0 ; i < pfp.size(); i++) {
					ProcedureFieldPo po = pfp.get(i);
					if (po.isIn()) {
						call.setObject( i+1, po.getValue());
					}
					if(po.isOut()) { 
						call.registerOutParameter( i+1, CommonConst.PROCEDURE_TYPE.get(po.getTypeName()));
					}
				}
				// 处理结果集
			    call.execute();  
			    for(int i = 0 ; i < pfp.size(); i++) {
			    	ProcedureFieldPo po = pfp.get(i);
			    	if(po.isOut()) { 
			    		 Object objRtn =   call.getObject( i+1 );
			    		 val.add(objRtn.toString());
			    	}
			    	
				}
				
			}  
			 

		
		return val;	 
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				rs.close();
		} 
	}	

	
	//TODO 获取查询的结果, 返回字段名称的数据和 值的数据
	public static void callProcedure(Connection conn, String proName ,String tableid , SheetDataValue dvt ,List<ProcedureFieldPo> pfp  ) throws SQLException {
		// DB对象
		CallableStatement call = null;
		ResultSet rs = null; 
		ObservableList<SheetFieldPo> fields = FXCollections.observableArrayList();
		ObservableList<ObservableList<StringProperty>>  val = FXCollections.observableArrayList();
		ObservableList<StringProperty> rowval = FXCollections.observableArrayList();
		try {
			
			if(pfp.size() > 0) {
				String callsql = "{call " + proName+ "(";
				for(int i = 0 ; i < pfp.size(); i++) {
					callsql += "? ,";
				}
				
				callsql = callsql.substring(0, callsql.lastIndexOf(","));
				callsql += " ) }"; 
				
				call = conn.prepareCall(callsql);
				
				for(int i = 0 ; i < pfp.size(); i++) {
					ProcedureFieldPo po = pfp.get(i);
					if (po.isIn()) {
						call.setObject( i+1, po.getValue());
					}
					if(po.isOut()) { 
						call.registerOutParameter( i+1, CommonConst.PROCEDURE_TYPE.get(po.getTypeName()));
					}
				}
				
				// 计时
				long startTime=System.currentTimeMillis();   //获取开始时间 
				// 数据库调用
				call.execute(); 
				long endTime=System.currentTimeMillis(); //获取结束时间
				long usetime = endTime-startTime;
				double vt = usetime / 1000.0; 
				logger.info("查询时间： "+usetime+"ms");
				dvt.setExecTime(vt);  

				// 处理结果集
				for(int i = 0 ; i < pfp.size(); i++) {
			    	ProcedureFieldPo po = pfp.get(i);
			    	if(po.isOut()) {
			    		 Object objRtn =   call.getObject( i+1 );
			    		 rowval.add(CommonUtility.createReadOnlyStringProperty(objRtn.toString()));
			    		 
			    		// 字段信息
			    		 SheetFieldPo sfpo  = new SheetFieldPo();
			    		 sfpo.setScale(0);
			    		 sfpo.setColumnName( po.getName());
			    		 sfpo.setColumnClassName("");
			    		 sfpo.setColumnDisplaySize(0);
			    		 sfpo.setColumnLabel( po.getName());
			    		 sfpo.setColumnType(po.getType());
			    		 sfpo.setColumnTypeName(po.getTypeName());
						 fields.add(sfpo);
			    	}
			    	
				}
				
			}   
			
		
			// 数据
//			 val = simpleExecRs(rs, fields); 
			 if(rowval.size() > 0) {
				 val.add(rowval);
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
			if (rs != null)
				rs.close();
		}
		return dpo;
	}
	

	private static ObservableList<ObservableList<StringProperty>>  execRs(int limit, ResultSet rs, ObservableList<SheetFieldPo> fpo,
			  SqluckyConnector dpo) throws SQLException {
		int idx = 1;
		int rowNo = 0;
		int columnnums = fpo.size();
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
						if (dpo != null) {
							val = dpo.DateToStringStringProperty(rs.getObject(i + 1));
						} else {
							// TODO dpo null 的情况下
							Date dv = (Date) rs.getObject(i + 1);
							String v = StrUtils.dateToStr(dv, ConfigVal.dateFormateL);
							val = new SimpleStringProperty(v);
						}

					} else {
						String temp = rs.getString(i+1);
						val = new SimpleStringProperty(temp); 
					}
				}
				
				
				addStringPropertyChangeListener(val, rn, i, vals, dbtype);
				vals.add(val);
			}

			vals.add(new SimpleStringProperty(rn + ""));
			allDatas.add(vals);

			if (idx == limit)
				break;
			idx++;
		}
		
		return allDatas;
	}

	

	public static ObservableList<ObservableList<StringProperty>>  simpleExecRs( ResultSet rs, ObservableList<SheetFieldPo> fpo ) throws SQLException {
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

	
	
	private static ObservableList<ObservableList<StringProperty>>   execRs(ResultSet rs, ObservableList<SheetFieldPo> fpo,   SqluckyConnector dpo)
			throws SQLException {
		return execRs(Integer.MAX_VALUE, rs, fpo, dpo);
	}

	
    // 数据单元格添加监听
	// 字段修改事件
	public static void addStringPropertyChangeListener(
			StringProperty val,
			int rowNo , 
			int idx,
			ObservableList<StringProperty> vals,
			int dbtype) {
		ChangeListener<String> cl = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				logger.info("addStringPropertyChangeListener ：newValue：" + newValue + " | oldValue =" + oldValue);
//				logger.info("key ==" + tabId + "-" + rowNo);
				logger.info("observable = " + observable);
				// 如果类似是数字的, 新值不是数字, 还原
				if (CommonUtility.isNum(dbtype) && !StrUtils.isNumeric(newValue) && !"<null>".equals(newValue)) {
					Platform.runLater(() -> val.setValue(oldValue));
					return;
				}
				
				if(CommonUtility.isDateTime(dbtype) && "".equals(newValue )) {
					Platform.runLater(() -> val.setValue("<null>"));
				}
				if(SqluckyBottomSheetUtility.dataPaneSaveBtn() != null ) {
					SqluckyBottomSheetUtility.dataPaneSaveBtn().setDisable(false);
				}
				

				ObservableList<StringProperty> oldDate = FXCollections.observableArrayList();
				if (!SqluckyBottomSheetUtility.exist( rowNo)) {
					for (int i = 0; i < vals.size(); i++) {
						if (i == idx) {
							oldDate.add(new SimpleStringProperty(oldValue));
						} else {
							oldDate.add(new SimpleStringProperty(vals.get(i).get()));
						}
					}
					SqluckyBottomSheetUtility.addData( rowNo, vals, oldDate); // 数据修改缓存, 用于之后更新
				} else {
					SqluckyBottomSheetUtility.addData( rowNo, vals);
				}
			}
		};
		val.addListener(cl);
	}
}
