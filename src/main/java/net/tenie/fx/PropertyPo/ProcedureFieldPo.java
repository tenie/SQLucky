package net.tenie.fx.PropertyPo;

public class ProcedureFieldPo {
	private String name;
	private String typeName;
	private int type;
	private boolean isOut;
	private boolean isIn;
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
	@Override
	public String toString() {
		return "ProcedureFieldPo [name=" + name + ", typeName=" + typeName + ", type=" + type + ", isOut=" + isOut
				+ ", isIn=" + isIn + "]";
	}
	
	
	
}
