package net.tenie.fx.component.container;

import java.util.Set;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.lib.po.DbConnectionPo;

public class ConnItemParent {
	private TreeItem<TreeNodePo> root;
	private TreeItem<TreeNodePo> schemaNode; 
	private DbConnectionPo connpo;
//	private Map<String , ConnItem> connItems = new HashMap<>();
	
	public ConnItemParent(DbConnectionPo connpo) {
		this.connpo = connpo;
		schemaNode = CreateEmptySchemaNode(connpo);
	}
	
	public ConnItemParent(DbConnectionPo connpo, TreeItem<TreeNodePo> root) {
		this.root = root;
		this.connpo = connpo;
		String defSch = connpo.getDefaultSchema();
		schemaNode = CreateSchemaNode(connpo); 
		moveDefaultNodeToTopAddTable(defSch, schemaNode );
		showConnNode(connpo, defSch);

	}
	public  void  showConnNode(DbConnectionPo connpo, String schemaName) {
		ConnItem ci = new ConnItem(connpo, schemaName);
		addConnItem(ci);
	}
	
	
	public void addConnItem(ConnItem ci) {
//		connItems.add(ci);
//		connItems.put(ci.getSchemaName(), ci);
		ObservableList<TreeItem<TreeNodePo>> ls = schemaNode.getChildren();
		for (int i = 0; i < ls.size(); i++) {
			TreeItem<TreeNodePo> val = ls.get(i);
			if (val.getValue().getName().equals(ci.getSchemaName())) {  
				TreeItem<TreeNodePo> item = ci.getParentNode(); 
				ls.remove(i);
				ls.add(i, item);  
				item.getValue().setConnItem(ci);
				break;
			}
		}   
	}
	
	public void addChildren(ConnItem ci) {
//		String name = ci.getSchemaName();
		TreeItem<TreeNodePo> item = ci.getParentNode(); 
		schemaNode.getChildren().add(item);
	}
	
	public void selectTable(String itemName) { 
		for (int i = 0; i < schemaNode.getChildren().size(); i++) {
			 String scheName = schemaNode.getChildren().get(i).getValue().getName();
			 if(itemName.equals(scheName)) {
//				 schemaNode.getChildren().get(i).setExpanded(true);
				 ComponentGetter.treeView.getSelectionModel()
				 	.select(schemaNode.getChildren().get(i).getValue().getConnItem().getTableNode() );
			 }
		}
//		ConnItem item = connItems.get(key);
//		ComponentGetter.treeView.getSelectionModel().select(item.getTableNode()); // 选择新加的节点
	}
	
	// 默认的schema移动到第一位 , 遍历所有节点, 找默认节点, 从原位置删除, 后再插入到第一个位置
		// 并且添加tableNode
	public  void moveDefaultNodeToTopAddTable(String defSch, TreeItem<TreeNodePo> schemas ) {
		if (defSch != null) {
			ObservableList<TreeItem<TreeNodePo>> ls = schemas.getChildren();
			for (int i = 0; i < ls.size(); i++) {
				TreeItem<TreeNodePo> val = ls.get(i);
				if (val.getValue().getName().equals(defSch)) {
					val.getValue().setIcon(ImageViewGenerator.svgImage("database", "#7CFC00 "));
					ls.remove(i);
					ls.add(0, val); 
					break;
				}
			}
		}
	}

	// 创建表节点
	public static TreeItem<TreeNodePo> CreateEmptySchemaNode(DbConnectionPo connpo) {

		TreeItem<TreeNodePo> schemas = new TreeItem<TreeNodePo>(
				new TreeNodePo("Schemas", ImageViewGenerator.svgImage("th-list", "#FFD700"), connpo));
	 
		return schemas;
	}
	public static TreeItem<TreeNodePo> CreateSchemaNode(DbConnectionPo connpo) {

		TreeItem<TreeNodePo> schemas = new TreeItem<TreeNodePo>(
				new TreeNodePo("Schemas", ImageViewGenerator.svgImage("th-list", "#FFD700"), connpo));
		// 获取schema 数据
		Set<String> set = connpo.settingSchema();
		for (String sche : set) {
			TreeItem<TreeNodePo> item = new TreeItem<>(
					new TreeNodePo(sche, ImageViewGenerator.svgImageUnactive("database"), connpo));
			schemas.getChildren().add(item);
		}
		return schemas;
	}
	
	public TreeItem<TreeNodePo> getSchemaNode() {
		return schemaNode;
	}

	public void setSchemaNode(TreeItem<TreeNodePo> schemaNode) {

		this.schemaNode = schemaNode;
	}
	 
//	public Map<String, ConnItem> getConnItems() {
//		return connItems;
//	}
//
//	public void setConnItems(Map<String, ConnItem> connItems) {
//		this.connItems = connItems;
//	}

	public TreeItem<TreeNodePo> getRoot() {
		return root;
	}
	public void setRoot(TreeItem<TreeNodePo> root) {
		this.root = root;
	}

	public DbConnectionPo getConnpo() {
		return connpo;
	}

	public void setConnpo(DbConnectionPo connpo) {
		this.connpo = connpo;
	}
	
	
}
