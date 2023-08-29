package net.tenie.Sqlucky.sdk.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.po.DynaPo;
import net.tenie.Sqlucky.sdk.po.PoInfo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.ObjFormater;

public class PoDaoUtil {
	private static Logger logger = LogManager.getLogger(CommonUtils.class);
	
	private static HashMap<Class<?>, PoInfo> beanInfoMap = new HashMap<Class<?>, PoInfo>();


	public static final int APPEND_HEAD = 1;

	public static final int APPEND_TAIL = 2;

	public static PoInfo getDataBeanInfo(Object bean) {
		if (bean == null)
			return null;
		if (!beanInfoMap.containsKey(bean.getClass()))
			beanInfoMap.put(bean.getClass(), new PoInfo(bean));
		return beanInfoMap.get(bean.getClass());
	}

	public static String getInsertSql(PoInfo info, Object bean) throws Exception {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("insert into " + info.getTabName() + "( ");
		int size = info.getColSize();
		int colSize = 0;
		int i;
		for (i = 0; i < size; i++) {
			if (info.getColVal(bean, i) != null) {
				sbuf.append(String.valueOf(info.getColName(i)) + ",");
				colSize++;
			}
		}
		sbuf.deleteCharAt(sbuf.length() - 1);
		sbuf.append(") values(");
		for (i = 0; i < colSize; i++)
			sbuf.append("?,");
		sbuf.deleteCharAt(sbuf.length() - 1);
		sbuf.append(")");
		return sbuf.toString();
	}

	public static String getUpdateSql(PoInfo info, Object condition, Object value) throws Exception {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("update " + info.getTabName() + " set ");
		int size = info.getColSize();
		int tmp = 0;
		int i;
		for (i = 0; i < size; i++) {
			if (info.getColVal(value, i) != null) {
				sbuf.append(String.valueOf(info.getColName(i)) + "=?,");
				tmp++;
			}
		}
		if (tmp > 0)
			sbuf.deleteCharAt(sbuf.length() - 1);
		sbuf.append(" where 1=1 ");
		tmp = 0;
		for (i = 0; i < size; i++) {
			if (info.getColVal(condition, i) != null) {
				sbuf.append(" and " + info.getColName(i) + "=? ");
				tmp++;
			}
		}
		return sbuf.toString();
	}

	public static String getDeleteSql(PoInfo info, Object condition) throws Exception {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("delete from " + info.getTabName() + " where 1=1 ");
		int size = info.getColSize();
		for (int i = 0; i < size; i++) {
			if (info.getColVal(condition, i) != null)
				sbuf.append(" and " + info.getColName(i) + "=? ");
		}
		return sbuf.toString();
	}

	public static String getSelectSql(PoInfo info, Object condition) throws Exception {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("select ");
		int size = info.getColSize();
		int i;
		for (i = 0; i < size; i++)
			sbuf.append(String.valueOf(info.getColName(i)) + ",");
		sbuf.deleteCharAt(sbuf.length() - 1);
		sbuf.append(" from " + info.getTabName() + " where 1=1 ");
		for (i = 0; i < size; i++) {
			if (info.getColVal(condition, i) != null)
				sbuf.append(" and " + info.getColName(i) + "=? ");
		}
		return sbuf.toString();
	}
	public static String getSelectSqlByField(PoInfo info, Object condition, String fieldName) throws Exception {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("select " + fieldName);
		int size = info.getColSize();
		int i;
		sbuf.append(" from " + info.getTabName() + " where 1=1 ");
		for (i = 0; i < size; i++) {
			if (info.getColVal(condition, i) != null)
				sbuf.append(" and " + info.getColName(i) + "=? ");
		}
		return sbuf.toString();
	}

	public static Timestamp toSqlDate(Date dt) {
		if (dt == null)
			return null;
		return new Timestamp(dt.getTime());
	}

	public static Date toJavaDate(Timestamp dt) {
		if (dt == null)
			return null;
		return new Date(dt.getTime());
	}

	public static String beanToXmlString(Object bean) {
		StringBuilder sbuf = new StringBuilder();
		PoInfo info = getDataBeanInfo(bean);
		sbuf.append("<" + info.getTabName().toUpperCase() + ">\n");
		int size = info.getColSize();
		Object val = null;
		for (int i = 0; i < size; i++) {
			try {
				val = info.getColVal(bean, i);
				sbuf.append("\t<" + info.getColName(i).toUpperCase() + ">");
				if (val == null) {
					sbuf.append("<![CDATA[]]>");
				} else if (info.getColType(i).equals(String.class)) {
					sbuf.append("<![CDATA[" + info.getColVal(bean, i) + "]]>");
				} else if (info.getColType(i).equals(Date.class)) {
					sbuf.append("<![CDATA[" + ObjFormater.dateFormat((Date) info.getColVal(bean, i)) + "]]>");
				} else if (info.getColType(i).equals(Float.class)) {
					sbuf.append("<![CDATA[" + ObjFormater.decimalFormat(((Float) info.getColVal(bean, i)).floatValue())
							+ "]]>");
				} else if (info.getColType(i).equals(Double.class)) {
					sbuf.append("<![CDATA["
							+ ObjFormater.decimalFormat(((Double) info.getColVal(bean, i)).doubleValue()) + "]]>");
				} else {
					sbuf.append("<![CDATA[" + info.getColVal(bean, i) + "]]>");
				}
				sbuf.append("</" + info.getColName(i).toUpperCase() + ">\n");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		sbuf.append("</" + info.getTabName().toUpperCase() + ">\n");
		return sbuf.toString();
	}

	public static List<DynaPo> getDynaBeansFromResultSet(String beanName, ResultSet rs) throws Exception {
		HashMap<String, Class> lhm = getResultSetMetaData(rs);
		LinkedList<DynaPo> result = new LinkedList<DynaPo>();
		Iterator<String> ite = null;
		while (rs.next()) {
			DynaPo bean = new DynaPo(beanName);
			ite = lhm.keySet().iterator();
			while (ite.hasNext()) {
				String key = ite.next();
				Class val = lhm.get(key);
				if (String.class.equals(val)) {
					bean.add(key, rs.getString(key));
					continue;
				}
				if (Integer.class.equals(val)) {
					bean.add(key, rs.getInt(key));
					continue;
				}
				if (Long.class.equals(val)) {
					bean.add(key, rs.getLong(key));
					continue;
				}
				if (Float.class.equals(val)) {
					bean.add(key, rs.getFloat(key));
					continue;
				}
				if (Double.class.equals(val)) {
					bean.add(key, rs.getDouble(key));
					continue;
				}
				if (Date.class.equals(val)) {
					Object obj = rs.getObject(key);
					
					if(obj != null && obj instanceof LocalDateTime) {
						LocalDateTime ldt = (LocalDateTime) obj; 
						Date dv = Date.from( ldt.atZone( ZoneId.systemDefault()).toInstant());
						bean.add(key, dv);
					}else {
						bean.add(key, (rs.getTimestamp(key) == null) ? null : new Date(rs.getTimestamp(key).getTime()));
					}
					
					continue;
				}
//				if (LocalDateTime.class.equals(val)) {
//					bean.add(key, (rs.getTimestamp(key) == null) ? null : new Date(rs.getTimestamp(key).getTime()));
//					continue;
//				}
				bean.add(key, rs.getObject(key));
			}
			result.addLast(bean);
		}
		return result;
	}

	public static <T> List<T> getDataBeansFromResultSet(T bean, ResultSet rs) throws Exception {
		PoInfo binfo = getDataBeanInfo(bean);
		LinkedList<T> result = new LinkedList<T>();
		String colName = null;
		Class colType = null;
		Object val = null;
		while (rs.next()) {
			Object dataBean = binfo.getCls().newInstance();
			for (int i = 0; i < binfo.getColSize(); i++) {
				colName = binfo.getColName(i);
				colType = binfo.getColType(i);
				val = rs.getObject(colName);
				if (val != null)
					if (colType.equals(Date.class)) {
						binfo.setColVal(dataBean, i, (rs.getTimestamp(colName) == null) ? null
								: new Date(rs.getTimestamp(colName).getTime()));
					} else if (colType.equals(Integer.class)) {
						binfo.setColVal(dataBean, i, rs.getInt(colName));
					} else if (colType.equals(Long.class)) {
						binfo.setColVal(dataBean, i, rs.getLong(colName));
					} else if (colType.equals(Float.class)) {
						binfo.setColVal(dataBean, i, rs.getFloat(colName));
					} else if (colType.equals(Double.class)) {
						binfo.setColVal(dataBean, i, rs.getDouble(colName));
					} else if (colType.equals(String.class)) {
						binfo.setColVal(dataBean, i, rs.getString(colName));
					} else {
						binfo.setColVal(dataBean, i, val);
					}
			}
			result.addLast((T) dataBean);
		}
		return result;
	}

	public static HashMap<String, Class> getResultSetMetaData(ResultSet rs) throws Exception {
		HashMap<String, Class> lhs = new HashMap<>(5);
		ResultSetMetaData rsmd = rs.getMetaData();
		int colSize = rsmd.getColumnCount();
		for (int i = 1; i < colSize + 1; i++)
			lhs.put(rsmd.getColumnName(i).toUpperCase(),
					getJavaType(rsmd.getColumnName(i), rsmd.getColumnType(i), rsmd.getPrecision(i), rsmd.getScale(i)));
		return lhs;
	}

	private static Class getJavaType(String colName, int colType, int colPrecision, int colScale) throws Exception {
		Class cls = null;
		String jType = null;
		switch (colType) {
			case -7 :
				jType = "BIT";
				cls = Integer.class;
				break;
			case -6 :
				jType = "TINYINT";
				cls = Integer.class;
				break;
			case -5 :
				jType = "BIGINT";
				cls = Long.class;
				break;
			case -4 :
				jType = "LONGVARBINARY";
				break;
			case -3 :
				jType = "VARBINARY";
				break;
			case -2 :
				jType = "BINARY";
				break;
			case -1 :
				jType = "LONGVARCHAR";
				break;
			case 0 :
				jType = "NULL";
				break;
			case 1 :
				jType = "CHAR";
				cls = String.class;
				break;
			case 2 :
				jType = "NUMERIC";
				if (colScale == -127) {
					cls = Double.class;
				} else if (colPrecision == 0 && colScale <= 0) {
					cls = Double.class;
				} else if (colScale == 0) {
					if (colPrecision > 9) {
						cls = Long.class;
					} else {
						cls = Integer.class;
					}
				} else if (colScale > 0) {
					if (colPrecision > 9) {
						cls = Double.class;
					} else {
						cls = Float.class;
					}
				}
				break;
			case 3 :
				jType = "NUMERIC";
				if (colScale == -127) {
					cls = Double.class;
				} else if (colPrecision == 0 && colScale <= 0) {
					cls = Double.class;
				} else if (colScale == 0) {
					if (colPrecision > 9) {
						cls = Long.class;
					} else {
						cls = Integer.class;
					}
				} else if (colScale > 0) {
					if (colPrecision > 9) {
						cls = Double.class;
					} else {
						cls = Float.class;
					}
				}
				break;
			case 4 :
				jType = "INTEGER";
				cls = Integer.class;
				break;
			case 5 :
				jType = "SMALLINT";
				cls = Integer.class;
				break;
			case 6 :
				jType = "FLOAT";
				cls = Float.class;
				break;
			case 7 :
				jType = "REAL";
				break;
			case 8 :
				jType = "DOUBLE";
				cls = Double.class;
				break;
			case 12 :
				jType = "VARCHAR";
				cls = String.class;
				break;
			case 16 :
				jType = "BOOLEAN";
				break;
			case 70 :
				jType = "DATALINK";
				break;
			case 91 :
				jType = "DATE";
				cls = Date.class;
				break;
			case 92 :
				jType = "TIME";
				break;
			case 93 :
				jType = "TIMESTAMP";
				cls = Date.class;
				break;
			case 1111 :
				jType = "OTHER";
				break;
			case 2000 :
				jType = "JAVA_OBJECT";
				break;
			case 2001 :
				jType = "DISTINCT";
				break;
			case 2002 :
				jType = "STRUCT";
				break;
			case 2003 :
				jType = "ARRAY";
				break;
			case 2004 :
				jType = "BLOB";
				break;
			case 2005 :
				jType = "CLOB";
				break;
			case 2006 :
				jType = "REF";
				break;
			default :
				jType = "JDBC_NOT_SUPPORT";
		}

		if (cls != null) {
			return cls;
		} else {
			throw new Exception("Not supported column type! colName=" + colName + " jdbcType=" + jType + " type="
					+ colType + " precision=" + colPrecision + " Scale=" + colScale);
		}
	}

	public static void setParamsToPreparedStatment(PreparedStatement ps, List params) throws Exception {
		Object item = null;
		int idx = 1;
		for (int i = 0; i < params.size(); i++) {
			item = params.get(i);
			if (item instanceof Date) {
				ps.setTimestamp(idx++, toSqlDate((Date) item));
			} else if (item instanceof Collection) {
				Object[] vals = ((Collection) item).toArray();
				for (int j = 0; j < vals.length; j++)
					ps.setObject(idx++, vals[j]);
			} else {
				ps.setObject(idx++, item);
			}
		}
	}

	public static String appendLength(String source, int length, int appendType, char padding) {
		if (source == null)
			return null;
		if (source.length() >= length)
			return source;
		StringBuilder sbd = new StringBuilder(source);
		if (appendType == 1) {
			for (int i = length - source.length(); i > 0; i--)
				sbd.insert(0, padding);
		} else {
			for (int i = source.length(); i < length; i++)
				sbd.append(padding);
		}
		return sbd.toString();
	}
}