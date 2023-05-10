package net.tenie.Sqlucky.sdk.po;

import javafx.scene.Node;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.db.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;

// 数据库连接节点属性数据
public class DBNodeInfoPo {
	private String name;
	private TreeItemType type;
	private Node icon;
	private Object parent;
	private String TableDDL;
	private TablePo table;
	private FuncProcTriggerPo funcProTri;
	private SqluckyConnector connpo;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TreeItemType getType() {
		return type;
	}
	public void setType(TreeItemType type) {
		this.type = type;
	}
	public Node getIcon() {
		return icon;
	}
	public void setIcon(Node icon) {
		this.icon = icon;
	}
	public Object getParent() {
		return parent;
	}
	public void setParent(Object parent) {
		this.parent = parent;
	}
	public String getTableDDL() {
		return TableDDL;
	}
	public void setTableDDL(String tableDDL) {
		TableDDL = tableDDL;
	}
	public TablePo getTable() {
		return table;
	}
	public void setTable(TablePo table) {
		this.table = table;
	}
	public FuncProcTriggerPo getFuncProTri() {
		return funcProTri;
	}
	public void setFuncProTri(FuncProcTriggerPo funcProTri) {
		this.funcProTri = funcProTri;
	}
	public SqluckyConnector getConnpo() {
		return connpo;
	}
	public void setConnpo(SqluckyConnector connpo) {
		this.connpo = connpo;
	}
	
	
}
