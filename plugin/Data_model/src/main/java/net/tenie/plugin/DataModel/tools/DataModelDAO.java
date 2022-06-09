package net.tenie.plugin.DataModel.tools;

import java.util.ArrayList;
import java.util.List;

import net.tenie.Sqlucky.sdk.db.PoDao;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
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
			SqluckyAppDB.closeConn();
		}
		

		return rs;
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
			SqluckyAppDB.closeConn();
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
			SqluckyAppDB.closeConn();
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
			SqluckyAppDB.closeConn();
		}
		
		return rs;
	}
	
	
	
	
}
