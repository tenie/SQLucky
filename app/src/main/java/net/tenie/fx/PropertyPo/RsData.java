package net.tenie.fx.PropertyPo;

import java.sql.Timestamp;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*   @author tenie */
public class RsData {
	private Map<String, Object> vals;

	public RsData() {
		vals = new HashMap<String, Object>();
	}

	public void put(String name, Object val) {
		vals.put(name.toUpperCase(), val);
	}

	public Object getObject(String key) {
		return vals.get(key.toUpperCase());
	}

	public Set<String> keySet() {
		return vals.keySet();
	}

	public String getString(String key) {
		Object o = getObject(key);
		if (o != null) {
			return String.valueOf(o);
		}
		return null;
	}

	public Integer getInteger(String key) {
		Object o = getObject(key);
		if (o != null) {
			return (int) o;
		}
		return null;
	}

	public Long getLong(String key) {
		Object o = getObject(key);
		if (o != null) {
			return (long) o;
		}
		return null;
	}

	public Double getDouble(String key) {
		Object o = getObject(key);
		if (o != null) {
			return (double) o;
		}
		return null;
	}

	public Timestamp getTimestamp(String key) {
		Object o = getObject(key);
		if (o != null) {
			return (Timestamp) o;
		}
		return null;
	}

	public Time getTime(String key) {
		Object o = getObject(key);
		if (o != null) {
			return (Time) o;
		}
		return null;
	}

	public Date getDate(String key) {
		Object o = getObject(key);
		if (o != null) {
			return (Date) o;
		}
		return null;
	}

	public Float getFloat(String key) {
		Object o = getObject(key);
		if (o != null) {
			return (Float) o;
		}
		return null;
	}

}
