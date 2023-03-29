package net.tenie.plugin.DataModel.tools;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfoenix.controls.JFXButton;

import javafx.collections.ObservableList;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheetUtility;
import net.tenie.Sqlucky.sdk.db.PoDao;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.UpdateDao;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.DataModel.po.DataModelInfoPo;
import net.tenie.plugin.DataModel.po.DataModelTableFieldsPo;
import net.tenie.plugin.DataModel.po.DataModelTablePo;

public class DataModelDAO {
	/**
	 * 查询所有的模型
	 * @return
	 */
	public static List<DataModelInfoPo> selectDMInfo() {
		DataModelInfoPo po = new DataModelInfoPo();
		var conn = SqluckyAppDB.getConn();
		List<DataModelInfoPo> rs = new ArrayList<>();
		try {
			rs = PoDao.select(conn, po);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
		
		return rs;
	}
	/**
	 * 查询指定模型
	 * @return
	 */
	public static DataModelInfoPo selectDMInfo(Long mid) {
		DataModelInfoPo po = new DataModelInfoPo();
		po.setId(mid);
		var conn = SqluckyAppDB.getConn();
		List<DataModelInfoPo> rs = new ArrayList<>();
		
		DataModelInfoPo val = null;
		try {
			rs = PoDao.select(conn, po);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
		if(rs != null && rs.size() >0 ) {
			val = rs.get(0);
		}

		return val;
	}
	/**
	 * 根据名称找模型, 找不到返回null
	 * @param name
	 * @return
	 */
	public static DataModelInfoPo selectDMInfoByName(String  name) {
		DataModelInfoPo po = new DataModelInfoPo();
		po.setName(name);
		var conn = SqluckyAppDB.getConn();
		List<DataModelInfoPo> rs = new ArrayList<>();
		
		DataModelInfoPo val = null;
		try {
			rs = PoDao.select(conn, po);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
		if(rs != null && rs.size() >0 ) {
			val = rs.get(0);
		}

		return val;
	}
	

	
	/**
	 * 通过模型id , 修改模型名称
	 * @param mid
	 * @param nn
	 */
	public static void updateModelName(Long mid, String nn) {
		DataModelInfoPo po = new DataModelInfoPo();
		po.setId(mid);
		
		DataModelInfoPo valpo = new DataModelInfoPo();
		valpo.setName(nn);
		var conn = SqluckyAppDB.getConn();
		 
		try {
			PoDao.update(conn, po, valpo);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
	}
	
	/**
	 * 根据模型ID， 找出所有表
	 * @return
	 */
	public static List<DataModelTablePo> selectDMTable(Long dmId) {
		DataModelTablePo po = new DataModelTablePo();
		po.setModelId(dmId);
		var conn = SqluckyAppDB.getConn();
		List<DataModelTablePo> rs = new ArrayList<>();
		try {
			rs = PoDao.select(conn, po);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
		

		return rs;
	}
	
	/**
	 * 根据表ID， 找表数据
	 * @return
	 */
	public static DataModelTablePo selectTableById(Long tableId) {
		DataModelTablePo po = new DataModelTablePo();
		po.setItemId(tableId);
		
		DataModelTablePo val = null;
		var conn = SqluckyAppDB.getConn();
		List<DataModelTablePo> rs = new ArrayList<>();
		try {
			rs = PoDao.select(conn, po);
			if(rs != null && rs.size() > 0 ) {
				val = rs.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
		

		return val;
	}
	
	/**
	 * 根据表id , 找出表所有的字段
	 */
	public static List<DataModelTableFieldsPo> selectTableFields(Long tableId) {
		List<DataModelTableFieldsPo> rs = new ArrayList<>();
		DataModelTableFieldsPo po = new DataModelTableFieldsPo();
		po.setTableId(tableId);
		var conn = SqluckyAppDB.getConn();
		
		try {
			rs = PoDao.select(conn, po);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
		
		return rs;
	}
	
	/*
	 * 对数据库表的信息修改(name, comment) 进行保存
	 */
	public static void saveTableInfo(JFXButton saveBtn, ResultSetPo resultSetPo, 
			Long TABLE_ID,    Connection conn ) {
		// 待保存数据
		 ObservableList<ResultSetRowPo> modifyData = SqluckyBottomSheetUtility.getModifyData();
		// 执行sql 后的信息 (主要是错误后显示到界面上)
		DbTableDatePo ddlDmlpo = DbTableDatePo.setExecuteInfoPo();
		boolean btnDisable = true;
		if (!modifyData.isEmpty()) {
			for (ResultSetRowPo val : modifyData) {
				try {
					// 字段名称
					String nameVal = val.getValueByFieldName("NAME");
					// 字段备注信息
					String commentVal = val.getValueByFieldName("COMMENT");
					// 数据库表的字段
					String FIELD = val.getValueByFieldName("FIELD");
					
					DataModelTableFieldsPo condpo = new DataModelTableFieldsPo();
					condpo.setTableId(TABLE_ID);
					condpo.setDefKey(FIELD);
					
					DataModelTableFieldsPo valpo = new DataModelTableFieldsPo();
					valpo.setDefName(nameVal);
					valpo.setComment(commentVal);
					int i = PoDao.update(conn, condpo, valpo);
					String msg = "update : " + i + "Line. " + FIELD;
					
					if(StrUtils.isNotNullOrEmpty(msg)) {
						var fds = ddlDmlpo.getFields();
						var row = ddlDmlpo.addRow();
						ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ), fds.get(0));
						ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(msg), fds.get(1));
						ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("success"), fds.get(2));
					}

				} catch (Exception e1) {
					e1.printStackTrace();
					btnDisable = false;
					String 	msg = "failed : " + e1.getMessage();
					msg += "\n"+msg;
					var fds = ddlDmlpo.getFields();
					var row = ddlDmlpo.addRow();
					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ), fds.get(0));
					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(msg), fds.get(1));
					ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty("failed"), fds.get(2));
				}
			}
			SqluckyBottomSheetUtility.rmUpdateData();
		}

		// 插入操作
//		ObservableList<ResultSetRowPo> dataList = SqluckyBottomSheetUtility.getAppendData();
//		for (ResultSetRowPo os : dataList) {
//			try {
//				ObservableList<ResultSetCellPo> cells = os.getRowDatas();
//				String msg = InsertDao.execInsert(conn, tabName, cells);
//				var fds = ddlDmlpo.getFields();
//				var row = ddlDmlpo.addRow();
//				ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ), fds.get(0));
//				ddlDmlpo.addData(row, new SimpleStringProperty(msg), fds.get(1));
//				ddlDmlpo.addData(row, new SimpleStringProperty("success"), fds.get(2));
//
//				// 对insert 的数据保存后 , 不能再修改
////				ObservableList<ResultSetCellPo> cells = os.getRowDatas();
//				for (int i = 0; i < cells.size(); i++) {
//					var cellpo = cells.get(i);
//					StringProperty sp = cellpo.getCellData();
//					CommonUtility.prohibitChangeListener(sp, sp.get());
//				}
//
//			} catch (Exception e1) {
//				e1.printStackTrace();
//				btnDisable = false;
//				var fs = ddlDmlpo.getFields();
//				var row = ddlDmlpo.addRow();
//				ddlDmlpo.addData(row, CommonUtility.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ), fs.get(0));
//				ddlDmlpo.addData(row, new SimpleStringProperty(e1.getMessage()), fs.get(1));
//				ddlDmlpo.addData(row, new SimpleStringProperty("failed"), fs.get(2));
//			}
//		}
		// 删除缓存数据
		SqluckyBottomSheetUtility.rmAppendData();

		// 保存按钮禁用
		saveBtn.setDisable(btnDisable);
		DataModelUtility.showExecuteSQLInfo(ddlDmlpo, null);
	}
	
	
	
	
}
