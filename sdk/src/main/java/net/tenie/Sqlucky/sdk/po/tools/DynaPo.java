package net.tenie.Sqlucky.sdk.po.tools;


import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.utility.CommonUtility;
 

public class DynaPo implements Serializable {
	private static Logger logger = LogManager.getLogger(CommonUtility.class);
	private HashMap<Object, Object> dataMap = new HashMap(1);
	private String name = null;

	public DynaPo(String name) {
		this.name = name;
	}

	public void add(Object key, Object val) {
		this.dataMap.put(key, val);
	}

	public Object get(Object key) {
		return this.dataMap.get(key);
	}

	public Object remove(Object key) {
		return this.dataMap.remove(key);
	}

	public Iterator getNames() {
		return this.dataMap.keySet().iterator();
	}

	public boolean containsKey(String key) {
		return this.dataMap.containsKey(key);
	}

	public boolean containsValue(String val) {
		return this.dataMap.containsValue(val);
	}

	public void clear() {
		this.dataMap.clear();
	}

	public void add(Object key, int val) {
		this.add(key, (Object) (new Integer(val)));
	}

	public void add(Object key, long val) {
		this.add(key, (Object) (new Long(val)));
	}

	public void add(Object key, float val) {
		this.add(key, (Object) (new Float(val)));
	}

	public void add(Object key, double val) {
		this.add(key, (Object) (new Double(val)));
	}

	public void add(Object key, Object[] val) {
		this.add(key, (Object) val);
	}

	public int getInt(Object key) {
		Object obj = this.get(key);
		return obj != null ? Integer.valueOf(obj.toString()) : 0;
	}

	public long getLong(Object key) {
		Object obj = this.get(key);
		return obj != null ? Long.valueOf(obj.toString()) : 0L;
	}

	public float getFloat(Object key) {
		Object obj = this.get(key);
		return obj != null ? Float.valueOf(obj.toString()) : 0.0F;
	}

	public double getDouble(Object key) {
		Object obj = this.get(key);
		return obj != null ? Double.valueOf(obj.toString()) : 0.0D;
	}

	public Object[] getObjArray(Object key) {
		return (Object[]) this.get(key);
	}

	public String getString(Object key) {
		return (String) this.get(key);
	}

	public Date getDate(Object key) {
		return (Date) this.get(key);
	}

	public String getBeanName() {
		return this.name;
	}

	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("DynaBean:" + this.name + "\n");
		Iterator ite = this.dataMap.keySet().iterator();
		Object key = null;
		Object obj = null;

		while (ite.hasNext()) {
			key = ite.next();
			obj = this.dataMap.get(key);
			sbuf.append("\t[" + key + "=" + obj + "]\n");
		}

		return sbuf.toString();
	}

	public String toXMLString() {
		StringBuilder sbuf = new StringBuilder();
		sbuf.append("<" + this.name + ">\n");
		Iterator ite = this.dataMap.keySet().iterator();
		Object key = null;

		while (ite.hasNext()) {
			key = ite.next();
			sbuf.append("\t<" + key + ">");
			sbuf.append(this.objToString(this.dataMap.get(key)));
			sbuf.append("</" + key + ">\n");
		}

		sbuf.append("</" + this.name + ">\n");
		return sbuf.toString();
	}

	private String objToString(Object obj) {
		if (obj == null) {
			return "";
		} else if (!(obj instanceof Integer) && !(obj instanceof Long)) {
			if (obj instanceof Float) {
				return "<![CDATA[" + ObjFormater.decimalFormat((Float) obj) + "]]>";
			} else if (obj instanceof Double) {
				return "<![CDATA[" + ObjFormater.decimalFormat((Double) obj) + "]]>";
			} else if (obj instanceof Date) {
				return "<![CDATA[" + ObjFormater.dateFormat((Date) obj) + "]]>";
			} else {
				return obj instanceof String ? "<![CDATA[" + obj + "]]>" : obj.toString();
			}
		} else {
			return "<![CDATA[" + obj.toString() + "]]>";
		}
	}
}
