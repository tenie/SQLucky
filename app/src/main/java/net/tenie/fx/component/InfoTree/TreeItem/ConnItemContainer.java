package net.tenie.fx.component.InfoTree.TreeItem;

import java.util.Set;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.TreeItemType;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.component.AppWindowComponentGetter;
/**
 * 
 * @author tenie
 *
 */
public class ConnItemContainer {
	private TreeItem<TreeNodePo> parentNode;
	private TreeItem<TreeNodePo> schemaNode;
	private SqluckyConnector connpo;

	// 创建空的对象, 包含一个空的schema Node
	public ConnItemContainer(SqluckyConnector connpo) {
		this.connpo = connpo;
		schemaNode = CreateEmptySchemaNode(connpo);
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
		schemaNode = CreateSchemaNode(connpo);
		moveSchemaToTop(defSch, schemaNode);
		// 创建子节点
		ConnItemDbObjects ci = showConnNode(connpo, defSch);
		// 将自己缓存到数对象中
//		ComponentGetter.dbInfoTree.getConnItemParent().add(this);
		parentNode.getValue().setConnItemContainer(this);
		schemaNode.getValue().setConnItem(ci);
	}

	public ConnItemDbObjects showConnNode(SqluckyConnector connpo, String schemaName) {
		ConnItemDbObjects ci = new ConnItemDbObjects(connpo, schemaName);
		addConnItem(ci);
		return ci;
	}

	// 根据给定的数据库对象, 将他加入到对应的schema node下
	public void addConnItem(ConnItemDbObjects ci) {
		ObservableList<TreeItem<TreeNodePo>> ls = schemaNode.getChildren();
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
		schemaNode.getChildren().add(item);
	}

	// 根据给定的schema Name 选择它的子节点下的table节点
	public void selectTable(String itemName) {
		for (int i = 0; i < schemaNode.getChildren().size(); i++) {
			String scheName = schemaNode.getChildren().get(i).getValue().getName();
			if (itemName.equals(scheName)) {
				var nd = schemaNode.getChildren().get(i).getValue().getConnItem().getTableNode();
				Platform.runLater(()->{
					AppWindowComponentGetter.treeView.getSelectionModel().select(nd);
				});
				break;
			}
		}
	}

	// 默认的schema移动到第一位 , 遍历所有节点, 找默认节点, 从原位置删除, 后再插入到第一个位置
	public void moveSchemaToTop(String defSch, TreeItem<TreeNodePo> schemas) {
		if (defSch != null) {
			ObservableList<TreeItem<TreeNodePo>> ls = schemas.getChildren();
			for (int i = 0; i < ls.size(); i++) {
				TreeItem<TreeNodePo> val = ls.get(i);
				if (val.getValue().getName().equals(defSch)) {
					val.getValue().setIcon(IconGenerator.svgImage("database", "#7CFC00 "));
					ls.remove(i);
					ls.add(0, val);
					break;
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
	public static TreeItem<TreeNodePo> CreateSchemaNode(SqluckyConnector connpo) {
		//判断是不是mysql
		String nodeName = connpo.dbRootNodeName(); 
		// 创建一个schema node , 将数据库数据放入
		TreeItem<TreeNodePo> schemas = new TreeItem<TreeNodePo>(
				new TreeNodePo( nodeName, TreeItemType.SCHEMA_ROOT, IconGenerator.svgImage("th-list", "#FFD700"), connpo));
		// 获取schema 数据
		Set<String> set = connpo.settingSchema();
		for (String sche : set) {
			TreeItem<TreeNodePo> item = new TreeItem<>(
					new TreeNodePo(sche, TreeItemType.SCHEMA, IconGenerator.svgImageUnactive("database"), connpo));
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
