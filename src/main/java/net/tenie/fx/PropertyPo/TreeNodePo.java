package net.tenie.fx.PropertyPo;

import java.io.Serializable;

import javafx.scene.Node;
import net.tenie.lib.po.DbConnectionPo;
import net.tenie.lib.po.FuncProcTriggerPo;
import net.tenie.lib.po.TablePo;

/*   @author tenie */
public class TreeNodePo implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private TreeItemType type;
	private Node icon;
	private Object parent;
	private String TableDDL;
	private TablePo table;
	private FuncProcTriggerPo funcProTri;
	private DbConnectionPo connpo;

	public TreeNodePo(String name, Node cion) {
		super();
		this.name = name;
		this.icon = cion;
	}

	public TreeNodePo(String name, Node cion, DbConnectionPo connpo) {
		super();
		this.name = name;
		this.icon = cion;
		this.connpo = connpo;
	}

	public TreeNodePo(String name, TreeItemType type, DbConnectionPo connpo) {
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

	public TreeNodePo(String name, TreeItemType type, Node cion, DbConnectionPo connpo) {
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

	public DbConnectionPo getConnpo() {
		return connpo;
	}

	public void setConnpo(DbConnectionPo connpo) {
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

}
