package net.tenie.Sqlucky.sdk.po.component;

import java.util.Set;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.TreeItemType;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;

/**
 * 
 * @author tenie
 *
 */
public class ConnItemContainer {
	private TreeItem<TreeNodePo> parentNode;
	private TreeItem<TreeNodePo> dataBasesSchemasRoot;
	private SqluckyConnector connpo;

	// 创建空的对象, 包含一个空的schema Node
	public ConnItemContainer(SqluckyConnector connpo) {
		this.connpo = connpo;
		dataBasesSchemasRoot = CreateEmptySchemaNode(connpo);
//		this.connpo.setItemContainer(this);
	}

	/**
	 * 创建一个有数据的schema Node, 初始化了所有的数据库对象数据
	 * 
	 * @param connpo 数据库连接对象, 用于初始化整个节点数据
	 * @param node   父节点缓存
	 */
	public ConnItemContainer(SqluckyConnector connpo, TreeItem<TreeNodePo> node) {
		this.parentNode = node;
		this.connpo = connpo;
		String defSch = connpo.getDefaultSchema();
		dataBasesSchemasRoot = CreateDataBasesSchemasRootNode(connpo);
		moveSchemaToTop(defSch, dataBasesSchemasRoot);
		// 创建子节点
		ConnItemDbObjects ci = showConnNode(connpo, defSch);
		// 将自己缓存到数对象中
//		ComponentGetter.dbInfoTree.getConnItemParent().add(this);
		parentNode.getValue().setConnItemContainer(this);
		dataBasesSchemasRoot.getValue().setConnItem(ci);
	}

	public ConnItemDbObjects showConnNode(SqluckyConnector connpo, String schemaName) {
		ConnItemDbObjects ci = new ConnItemDbObjects(connpo, schemaName);
		addConnItem(ci);
		return ci;
	}

	// 根据给定的数据库对象, 将他加入到对应的schema node下
	public void addConnItem(ConnItemDbObjects ci) {
		ObservableList<TreeItem<TreeNodePo>> ls = dataBasesSchemasRoot.getChildren();
		for (int i = 0; i < ls.size(); i++) {
			TreeItem<TreeNodePo> val = ls.get(i);
			String dbObjSchemaName = ci.getSchemaName();
			String currentSchemaName = val.getValue().getName();
			// 如果2个名称相同, 就移除schema node中的子节点, 将ConnItemDbObjects的对象的节点加入
			if (currentSchemaName.equals(dbObjSchemaName)) {
				TreeItem<TreeNodePo> item = ci.getParentNode();
				ls.remove(i);
				ls.add(i, item);
				TreeNodePo itemTreeNodePo = item.getValue();
				itemTreeNodePo.setConnItem(ci);
				break;
			}
		}
	}

	// 将数据库对象节点放入 schema node 下
	public void addChildren(ConnItemDbObjects ci) {
		TreeItem<TreeNodePo> item = ci.getParentNode();
		dataBasesSchemasRoot.getChildren().add(item);
	}

	// 根据给定的schema Name 选择它的子节点下的table节点
	public void selectTable(String itemName) {
		for (int i = 0; i < dataBasesSchemasRoot.getChildren().size(); i++) {
			String scheName = dataBasesSchemasRoot.getChildren().get(i).getValue().getName();
			if (itemName.equals(scheName)) {
				var nd = dataBasesSchemasRoot.getChildren().get(i).getValue().getConnItem().getTableNode();
				Platform.runLater(() -> {
//					AppWindow.treeView.getSelectionModel().select(nd);
					ComponentGetter.treeView.getSelectionModel().select(nd);
				});
				break;
			}
		}
	}

	// 默认的schema移动到第一位 , 遍历所有节点, 找默认节点, 从原位置删除, 后再插入到第一个位置
	public static void moveSchemaToTop(String schemaName, TreeItem<TreeNodePo> TreeItemSchemas) {
		if (schemaName != null) {
			ObservableList<TreeItem<TreeNodePo>> ls = TreeItemSchemas.getChildren();
			for (int i = 0; i < ls.size(); i++) {
				TreeItem<TreeNodePo> val = ls.get(i);
				if (val.getValue().getName().equals(schemaName)) {
					val.getValue().setIcon(IconGenerator.svgImage("database", "#7CFC00 "));
					ls.remove(i);
					ls.add(0, val);
//					break;
				}else{
					val.getValue().setIcon(IconGenerator.svgImageUnactive("database"));
					if( val.isExpanded()){
						val.setExpanded(false);
					}
				}
			}
		}
	}

	// 创建表节点, 空的节点, 没有子节点
	public static TreeItem<TreeNodePo> CreateEmptySchemaNode(SqluckyConnector connpo) {

		TreeItem<TreeNodePo> schemas = new TreeItem<TreeNodePo>(
				new TreeNodePo("Schemas", IconGenerator.svgImage("th-list", "#FFD700"), connpo));
		return schemas;
	}

	// 获取所有的schema, 并构建node
	public static TreeItem<TreeNodePo> CreateDataBasesSchemasRootNode(SqluckyConnector connpo) {
		// 获取放schema 的根节点名称
		String dataBasesName = connpo.dbRootNodeName();
		// 创建一个schema node , 将数据库数据放入
		TreeItem<TreeNodePo> dataBasesSchemasRoot = new TreeItem<TreeNodePo>(new TreeNodePo(dataBasesName, TreeItemType.SCHEMA_ROOT,
				IconGenerator.svgImage("th-list", "#FFD700"), connpo));
		// 获取schema 数据
		Set<String> set = connpo.settingSchema();
		for (String sche : set) {
			TreeItem<TreeNodePo> item = new TreeItem<>(
					new TreeNodePo(sche, TreeItemType.SCHEMA, IconGenerator.svgImageUnactive("database"), connpo));
			dataBasesSchemasRoot.getChildren().add(item);
		}
		return dataBasesSchemasRoot;
	}

	public TreeItem<TreeNodePo> getDataBasesSchemasRoot() {
		return dataBasesSchemasRoot;
	}

	public SqluckyConnector getConnpo() {
		return connpo;
	}

	public TreeItem<TreeNodePo> getParentNode() {
		return parentNode;
	}

	public void setParentNode(TreeItem<TreeNodePo> parentNode) {
		this.parentNode = parentNode;
	}

	public void setConnpo(SqluckyConnector connpo) {
		this.connpo = connpo;
	}

}
