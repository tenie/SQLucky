package net.tenie.fx.Po;

import java.io.Serializable;

import javafx.scene.Node;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DBNodeInfoPo;
import net.tenie.Sqlucky.sdk.po.TreeItemType;
import net.tenie.Sqlucky.sdk.po.db.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.fx.component.InfoTree.TreeItem.ConnItemContainer;
import net.tenie.fx.component.InfoTree.TreeItem.ConnItemDbObjects;

/**
 * treeView 节点保存的数据对象
 * 
 * @author tenie
 *
 */
public class TreeNodePo implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private TreeItemType type;
	private Node icon;
	private Object parent;
	private String TableDDL;
	private TablePo table;
	private FuncProcTriggerPo funcProTri;
	private SqluckyConnector connpo;
	private ConnItemContainer connItemContainer;
	private ConnItemDbObjects connItem;

	public TreeNodePo(String name, Node cion) {
		super();
		this.name = name;
		this.icon = cion;
	}

	public TreeNodePo(String name, Node cion, SqluckyConnector connpo) {
		super();
		this.name = name;
		this.icon = cion;
		this.connpo = connpo;
	}

	public TreeNodePo(String name, TreeItemType type, SqluckyConnector connpo) {
		super();
		this.name = name;
		this.type = type;
		this.connpo = connpo;
	}

	public TreeNodePo(String name, TreeItemType type, Node cion) {
		super();
		this.name = name;
		this.type = type;
		this.icon = cion;
	}

	public TreeNodePo(String name, TreeItemType type, Node cion, SqluckyConnector connpo) {
		super();
		this.name = name;
		this.type = type;
		this.icon = cion;
		this.connpo = connpo;
	}

	public TreeNodePo(String name, TreeItemType type, Object parent, Node cion) {
		super();
		this.name = name;
		this.type = type;
		this.parent = parent;
		this.icon = cion;
	}

	public ConnItemDbObjects getConnItem() {
		return connItem;
	}

	public void setConnItem(ConnItemDbObjects connItem) {
		this.connItem = connItem;
	}

	public SqluckyConnector getConnpo() {
		return connpo;
	}

	public void setConnpo(SqluckyConnector connpo) {
		this.connpo = connpo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public TablePo getTable() {
		return table;
	}

	public void setTable(TablePo table) {
		this.table = table;
	}

	public TreeNodePo() {
		super();
	}

	public TreeNodePo(String name) {
		super();
		this.name = name;
	}

	public Node getIcon() {
		return icon;
	}

	public void setIcon(Node icon) {
		this.icon = icon;
	}

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

	public FuncProcTriggerPo getFuncProTri() {
		return funcProTri;
	}

	public void setFuncProTri(FuncProcTriggerPo funcProTri) {
		this.funcProTri = funcProTri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((TableDDL == null) ? 0 : TableDDL.hashCode());
		result = prime * result + ((icon == null) ? 0 : icon.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeNodePo other = (TreeNodePo) obj;
		if (TableDDL == null) {
			if (other.TableDDL != null)
				return false;
		} else if (!TableDDL.equals(other.TableDDL))
			return false;
		if (icon == null) {
			if (other.icon != null)
				return false;
		} else if (!icon.equals(other.icon))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TreeNodePo [name=" + name + ", type=" + type + ", icon=" + icon + ", parent=" + parent + ", TableDDL="
				+ TableDDL + "]";
	}

	public ConnItemContainer getConnItemContainer() {
		return connItemContainer;
	}

	public void setConnItemContainer(ConnItemContainer connItemContainer) {
		this.connItemContainer = connItemContainer;
	}

	public DBNodeInfoPo getDbNodeInfoPo() {
		DBNodeInfoPo po = new DBNodeInfoPo();
		po.setConnpo(connpo);
		po.setFuncProTri(funcProTri);
		po.setIcon(icon);
		po.setName(name);
		po.setParent(parent);
		po.setTable(table);
		po.setTableDDL(TableDDL);
		po.setType(type);
		return po;
	}

}
