package net.tenie.plugin.MybatisGenerator.po;

import java.io.Serializable;

import javafx.scene.Node;

/**
 * 界面上展示节点需要的数据结构
 * @author tenie
 *
 */
public class DataModelTreeNodePo implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private Long modelId;
	private Long tableId;
	private Node icon;
	private Node unactiveIcon;
	private Node activeIcon;
	private Boolean isModel;
	private Boolean isModelTable;
	private boolean isActive = false;
	
	private DataModelInfoPo infopo;
	private DataModelTablePo tablepo;
	
//	private TreeCell<DataModelTreeNodePo> cell;
	
	

	public DataModelTreeNodePo(String name, Node cion) {
		super();
		this.name = name;
		this.icon = cion;
	}
	
	// 模型 创建tree node 
	public DataModelTreeNodePo(DataModelInfoPo infopo) {
		super();
		this.infopo = infopo;
		this.name = infopo.getName();
		this.modelId = infopo.getId();
		this.isModel = true;
		this.isModelTable = false;
//		this.icon = cion;
	}
	// 模型中的表创建 tree node
	public DataModelTreeNodePo(DataModelTablePo tablepo) {
		super();
		this.tablepo = tablepo;
		this.name = tablepo.getDefKey();
		this.tableId = tablepo.getItemId();
		this.isModel = false;
		this.isModelTable = true;
//		this.icon = cion;
	}
	//DataModelTreeNodePo
	
	

	public DataModelTreeNodePo() {
		super();
		this.name = "DataModelTreeRootNode";
		this.icon = null;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public Node getUnactiveIcon() {
		return unactiveIcon;
	}

	public void setUnactiveIcon(Node unactiveIcon) {
		this.unactiveIcon = unactiveIcon;
	}

	public Node getActiveIcon() {
		return activeIcon;
	}

	public void setActiveIcon(Node activeIcon) {
		this.activeIcon = activeIcon;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Node getIcon() {
		if(isActive) {
			icon = activeIcon;
		}else {
			icon = unactiveIcon;
		}
		return icon;
	}



	public void setIcon(Node icon) {
		this.icon = icon;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	public Boolean getIsModel() {
		return isModel;
	}



	public void setIsModel(Boolean isModel) {
		this.isModel = isModel;
	}



	 


	public Boolean getIsModelTable() {
		return isModelTable;
	}



	public DataModelInfoPo getInfopo() {
		return infopo;
	}

	public void setInfopo(DataModelInfoPo infopo) {
		this.infopo = infopo;
	}

	public DataModelTablePo getTablepo() {
		return tablepo;
	}

	public void setTablepo(DataModelTablePo tablepo) {
		this.tablepo = tablepo;
	}

	public void setIsModelTable(Boolean isModelTable) {
		this.isModelTable = isModelTable;
	}


	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((icon == null) ? 0 : icon.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		DataModelTreeNodePo other = (DataModelTreeNodePo) obj;
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
		return true;
	}


	
}
