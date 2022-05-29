package net.tenie.Sqlucky.sdk.po.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.utility.CommonUtility;

public class PoDao {
	private static Logger logger = LogManager.getLogger(CommonUtility.class);
//	public static void main(String[] args) throws Exception {
//		TablePo po = new TablePo();
//		po.setId(1);
//		po.setTableName("22");
//		PoDao.insert(null, po);
//	}

	public static void insert(Connection conn, Object bean) throws Exception {
		if (bean != null ) {
			PreparedStatement ps = null;
			LinkedList params = new LinkedList();

			try {
				PoInfo binfo = PoDaoUtil.getDataBeanInfo(bean);
				String sql = PoDaoUtil.getInsertSql(binfo, bean);
				System.out.println(sql);
				ps = conn.prepareStatement(sql);
				int size = binfo.getColSize();
				Object obj = null;
				int idx = 1;

				for (int i = 0; i < size; ++i) {
					obj = binfo.getColVal(bean, i);
					if (obj != null) {
						setObj(ps, idx, binfo.getColType(i), obj);
						params.addLast(obj.toString());
						++idx;
					}
				}

				logger.debug(sql + " " + params);
				ps.execute();
			} catch (Throwable var13) {
				throw new Exception("Exception", var13);
			} finally {
				clean(ps, (ResultSet) null);
			}
		}
	}
	public static Long getReturnId(PreparedStatement pstmt) {
		ResultSet rs = null;
		
		Long id = null;
		try {
			rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getLong(1);
			}
		} catch (SQLException e) { 
			e.printStackTrace();
		} finally { 
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
		return id;
	}
	public static Long insertReturnID(Connection conn, Object bean) throws Exception {
		if (bean != null ) {
			PreparedStatement ps = null;
			LinkedList params = new LinkedList();

			try {
				PoInfo binfo = PoDaoUtil.getDataBeanInfo(bean);
				String sql = PoDaoUtil.getInsertSql(binfo, bean);
				ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				int size = binfo.getColSize();
				Object obj = null;
				int idx = 1;

				for (int i = 0; i < size; ++i) {
					obj = binfo.getColVal(bean, i);
					if (obj != null) {
						setObj(ps, idx, binfo.getColType(i), obj);
						params.addLast(obj.toString());
						++idx;
					}
				}

				logger.debug(sql + " " + params);
				ps.execute();
				return getReturnId(ps);
			} catch (Throwable var13) {
				throw new Exception("Exception", var13);
			} finally {
				clean(ps, (ResultSet) null);
			}
		}
		return null;
	}
	

	private static void setObj(PreparedStatement ps, int idx, Class type, Object val) throws Exception {
		if (type.equals(Date.class)) {
			if (val == null) {
				ps.setNull(idx, 93);
			} else {
				ps.setTimestamp(idx, PoDaoUtil.toSqlDate((Date) val));
			}
		} else if (type.equals(String.class)) {
			if (val == null) {
				ps.setNull(idx, 1);
			} else {
				ps.setObject(idx, val);
			}
		} else if (val == null) {
			ps.setNull(idx, 2);
		} else {
			ps.setObject(idx, val);
		}

	}

	public static void   insert(Connection conn, List beans) throws Exception {
		for (int i = 0; i < beans.size(); ++i) {
			insert(conn,  beans.get(i));
		}

	}

	public static int update(Connection conn, Object condition, Object value) throws Exception {
		PreparedStatement ps = null;
		LinkedList params = new LinkedList();

		try {
//			POValidate.getInstance().validate(condition, 2);
			PoInfo binfo = PoDaoUtil.getDataBeanInfo(condition);
			String sql = PoDaoUtil.getUpdateSql(binfo, condition, value);
			ps = conn.prepareStatement(sql);
			int size = binfo.getColSize();
			int idx = 1;
			Object obj = null;

			int i;
			for (i = 0; i < size; ++i) {
				obj = binfo.getColVal(value, i);
				if (obj != null) {
					if (binfo.getColType(i).equals(Date.class)) {
						ps.setTimestamp(idx, PoDaoUtil.toSqlDate((Date) obj));
					} else {
						ps.setObject(idx, obj);
					}

					++idx;
					params.addLast(obj.toString());
				}
			}

			for (i = 0; i < size; ++i) {
				obj = binfo.getColVal(condition, i);
				if (obj != null) {
					if (binfo.getColType(i).equals(Date.class)) {
						ps.setTimestamp(idx, PoDaoUtil.toSqlDate((Date) obj));
					} else {
						ps.setObject(idx, binfo.getColVal(condition, i));
					}

					++idx;
					params.addLast(obj.toString());
				}
			}

			logger.debug(sql + " " + params);
			int var12 = ps.executeUpdate();
			return var12;
		} catch (Throwable var15) {
			throw new Exception("Exception", var15);
		} finally {
			clean(ps, (ResultSet) null);
		}
	}
 

	public static int delete(Connection conn, Object condition) throws Exception {
		PreparedStatement ps = null;
		LinkedList params = new LinkedList();

		try {
//			POValidate.getInstance().validate(condition, 4);
			PoInfo binfo = PoDaoUtil.getDataBeanInfo(condition);
			String sql = PoDaoUtil.getDeleteSql(binfo, condition);
			ps = conn.prepareStatement(sql);
			int size = binfo.getColSize();
			int idx = 1;
			Object obj = null;

			for (int i = 0; i < size; ++i) {
				if (binfo.getColVal(condition, i) != null) {
					obj = binfo.getColVal(condition, i);
					if (binfo.getColType(i).equals(Date.class)) {
						ps.setObject(idx, PoDaoUtil.toSqlDate((Date) obj));
					} else {
						ps.setObject(idx, obj);
					}

					++idx;
					params.addLast(obj.toString());
				}
			}

			logger.debug(sql + " " + params);
			int var11 = ps.executeUpdate();
			return var11;
		} catch (Throwable var14) {
			throw new Exception("Exception", var14);
		} finally {
			clean(ps, (ResultSet) null);
		}
	}

	 

 

	public static int count(Connection conn, Object condition) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		LinkedList params = new LinkedList();

		int var11;
		try {
			PoInfo binfo = PoDaoUtil.getDataBeanInfo(condition);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT COUNT(*) AS POCOUNT FROM " + binfo.getTabName() + " WHERE 1=1");
			int size = binfo.getColSize();

			int idx;
			for (idx = 0; idx < size; ++idx) {
				if (binfo.getColVal(condition, idx) != null) {
					sql.append(" AND " + binfo.getColName(idx) + "=?");
				}
			}

			ps = conn.prepareStatement(sql.toString());
			idx = 1;

			for (int i = 0; i < size; ++i) {
				if (binfo.getColVal(condition, i) != null) {
					ps.setObject(idx, binfo.getColVal(condition, i));
					++idx;
					params.addLast(binfo.getColVal(condition, i).toString());
				}
			}

			logger.debug(sql + " " + params);
			rs = ps.executeQuery();
			rs.next();
			var11 = rs.getInt("POCOUNT");
		} catch (Throwable var14) {
			throw new Exception("Exception", var14);
		} finally {
			clean(ps, rs);
		}

		return var11;
	}

	public static <T> List<T> select(Connection conn, T condition) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		LinkedList params = new LinkedList();

		try {
//			POValidate.getInstance().validate(condition, 8);
			PoInfo binfo = PoDaoUtil.getDataBeanInfo(condition);
			String sql = PoDaoUtil.getSelectSql(binfo, condition);
			ps = conn.prepareStatement(sql);
			int size = binfo.getColSize();
			int idx = 1;

			for (int i = 0; i < size; ++i) {
				if (binfo.getColVal(condition, i) != null) {
					ps.setObject(idx, binfo.getColVal(condition, i));
					++idx;
					params.addLast(binfo.getColVal(condition, i).toString());
				}
			}

			logger.debug(sql + " " + params);

			rs = ps.executeQuery();
			List var11 = PoDaoUtil.getDataBeansFromResultSet(condition, rs);
			return var11;
		} catch (Throwable var14) {
			throw new Exception("Exception", var14);
		} finally {
			clean(ps, rs);
		}
	}

 
	public static List<DynaPo> select(Connection conn, Object condition, String dynaBeanName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		LinkedList params = new LinkedList();

		List var12;
		try {
//			POValidate.getInstance().validate(condition, 8);
			PoInfo binfo = PoDaoUtil.getDataBeanInfo(condition);
			String sql = PoDaoUtil.getSelectSql(binfo, condition);
			ps = conn.prepareStatement(sql);
			int size = binfo.getColSize();
			int idx = 1;

			for (int i = 0; i < size; ++i) {
				if (binfo.getColVal(condition, i) != null) {
					ps.setObject(idx, binfo.getColVal(condition, i));
					++idx;
					params.addLast(binfo.getColVal(condition, i).toString());
				}
			}

			logger.debug(sql + " " + params);
			rs = ps.executeQuery();
			var12 = PoDaoUtil.getDynaBeansFromResultSet(dynaBeanName, rs);
		} catch (Throwable var15) {
			throw new Exception("Exception", var15);
		} finally {
			clean(ps, rs);
		}

		return var12;
	}

  
 
  

	public static void clean(Statement ps, ResultSet rs) throws Exception {
		try {
			if (rs != null) {
				rs.close();
			}

			if (ps != null) {
				ps.close();
			}

		} catch (Throwable var3) {
			throw new Exception("Exception", var3);
		}
	}
}