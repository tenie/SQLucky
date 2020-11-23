package net.tenie.fx.component.container;

import java.util.ArrayList;
import java.util.List;
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
	private List<ConnItem> connItems = new ArrayList<>();
	
	
	
	public ConnItemParent(DbConnectionPo connpo, TreeItem<TreeNodePo> root) {
		this.root = root;
		
		String defSch = connpo.getDefaultSchema();
		setSchemaNode(CreateSchemaNode(connpo)); 
		moveDefaultNodeToTopAddTable(defSch, schemaNode );
		showConnNode(connpo, defSch);

	}
	public  void  showConnNode(DbConnectionPo connpo, String schemaName) {
		ConnItem ci = new ConnItem(connpo, schemaName);
		addConnItem(ci);  
		
//		ObservableList<TreeItem<TreeNodePo>> ls = schemaNode.getChildren();
//		for (int i = 0; i < ls.size(); i++) {
//			TreeItem<TreeNodePo> val = ls.get(i);
//			if (val.getValue().getName().equals(schemaName)) {
//				ConnItem ci = new ConnItem(connpo, schemaName);
//				ConnItem.add(ci);
//				TreeItem<TreeNodePo> item = ci.getParentNode(); 
//				ls.remove(i);
//				ls.add(i, item); 
//				ComponentGetter.treeView.getSelectionModel().select(ci.getTableNode()); // 选择新加的节点
//				break;
//			}
//		} 
		
	}
	
	
	public void addConnItem(ConnItem ci) {
		connItems.add(ci);
		ObservableList<TreeItem<TreeNodePo>> ls = schemaNode.getChildren();
		for (int i = 0; i < ls.size(); i++) {
			TreeItem<TreeNodePo> val = ls.get(i);
			if (val.getValue().getName().equals(ci.getSchemaName())) {  
				TreeItem<TreeNodePo> item = ci.getParentNode(); 
				ls.remove(i);
				ls.add(i, item);  
				break;
			}
		}   
	}
	
	public void selectTable( ) {
		ConnItem item = connItems.get(0);
		ComponentGetter.treeView.getSelectionModel().select(item.getTableNode()); // 选择新加的节点
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
	public List<ConnItem> getConnItem() {
		return connItems;
	}
	public void setConnItem(List<ConnItem> connItem) {
		connItems = connItem;
	}
	public TreeItem<TreeNodePo> getRoot() {
		return root;
	}
	public void setRoot(TreeItem<TreeNodePo> root) {
		this.root = root;
	}
	
	
}
