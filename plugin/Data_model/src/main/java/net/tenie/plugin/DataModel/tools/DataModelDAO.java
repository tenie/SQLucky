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
	
	
	
	
}
