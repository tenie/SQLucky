package net.tenie.Sqlucky.sdk.po;

 
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class PoInfo {
	private Class cls;
	private String tabName;
	private String clsName;
	private LinkedList<String> mthName = new LinkedList();
	private LinkedList<String> colName = new LinkedList();
	private LinkedList<Class> colType = new LinkedList();

	
	public PoInfo(Object obj) {
		this.cls = obj.getClass();
		this.tabName = this.parseTabName(obj.getClass());
		this.clsName = this.cls.getName().substring(this.cls.getName().lastIndexOf(".") + 1);
		Method[] methods = this.cls.getMethods();
		
		Class lc = List.class;
		for (int i = 0; i < methods.length; ++i) {
			if (methods[i].getName().startsWith("set")) {
				Class tmpc = methods[i].getParameterTypes()[0]; 
				if(tmpc.equals(lc)) {
					continue;
				}
				
				this.mthName.addLast(methods[i].getName().substring(3));
				this.colName.addLast(this.parseColName(methods[i]));
				this.colType.addLast(methods[i].getParameterTypes()[0]);
			}
		}

	}

	public String getClsName() {
		return this.clsName;
	}

	public Class getCls() {
		return this.cls;
	}

	public String getTabName() {
		return this.tabName;
	}

	public String getColName(int idx) {
		return this.colName.get(idx);
	}

	public Class getColType(int idx) {
		return this.colType.get(idx);
	}

	public int getColSize() {
		return this.colName.size();
	}

	public String getMethodName(int idx) {
		return this.mthName.get(idx);
	}

	public Object getColVal(Object bean, int idx) throws Exception {
		String colName =  this.mthName.get(idx);
		String lowCN = colName.toLowerCase();
		var ms = this.cls.getMethods();
		for(Method mth : ms) {
			var mName = mth.getName();
			if(mName.toLowerCase().endsWith( lowCN)) {
				if(mName.startsWith("set")) {
					continue;
				}else {
					return mth.invoke(bean);
				}
				
			}
			
			
			
		}
		return null;
//		Method mth = this.cls.getMethod("get" + (String) this.mthName.get(idx));
//		if(mth == null) {
//			mth = this.cls.getMethod("is" + (String) this.mthName.get(idx));
//		}
//		return mth.invoke(bean);
	}

	public void setColVal(Object bean, int idx, Object val) throws Exception {
		Method mth = this.cls.getMethod("set" + this.mthName.get(idx), this.colType.get(idx));
		mth.invoke(bean, val);
	}

	private String parseTabName(Class cls) {
		String name = cls.getName().substring(cls.getName().lastIndexOf(".") + 1);
		String tabName = name.substring(0, name.length() - 2);
		StringBuffer sbuf = new StringBuffer();
		char[] chs = tabName.toCharArray();

		for (int i = 0; i < chs.length; ++i) {
			if (chs[i] <= 'Z' && chs[i] >= 'A' && i != 0) {
				sbuf.append('_');
			}

			sbuf.append(chs[i]);
		}

		return sbuf.toString();
	}

	private String parseColName(Method meth) {
		String name = meth.getName().substring(3);
		StringBuffer sbuf = new StringBuffer();
		char[] chs = name.toCharArray();

		for (int i = 0; i < chs.length; ++i) {
			if (chs[i] <= 'Z' && chs[i] >= 'A' && i != 0) {
				sbuf.append('_');
			}

			sbuf.append(chs[i]);
		}

		return sbuf.toString();
	}
}