package net.tenie.Sqlucky.sdk.po.db;

/**
 * 存储过程
 * @author tenie
 *
 */
public class ProcedureFieldPo {
	private String name;
	private String typeName;
	private int type;
	private boolean isOut;  
	private boolean isIn;
	private String value;   // 调用存储过程时传递的值
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public boolean isOut() {
		return isOut;
	}
	public void setOut(boolean isOut) {
		this.isOut = isOut;
	}
	public boolean isIn() {
		return isIn;
	}
	public void setIn(boolean isIn) {
		this.isIn = isIn;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "ProcedureFieldPo [name=" + name + ", typeName=" + typeName + ", type=" + type + ", isOut=" + isOut
				+ ", isIn=" + isIn + ", value=" + value + "]";
	}
	
	
	 
	
	
	
}
