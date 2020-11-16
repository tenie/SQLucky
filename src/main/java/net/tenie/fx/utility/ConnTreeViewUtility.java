package net.tenie.fx.utility;

import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import net.tenie.fx.PropertyPo.TreeItemType;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.lib.po.DBOptionHelper;
import net.tenie.lib.po.DbConnectionPo;
import net.tenie.lib.po.FuncProcTriggerPo;
import net.tenie.lib.po.TablePo;

/*   @author tenie */
public class ConnTreeViewUtility {

	// 渲染 默认连接节点
	public static TreeItem<TreeNodePo> showConnNode(DbConnectionPo connpo) {
		String defSch = connpo.getDefaultSchema();
		TreeItem<TreeNodePo> scheNode = CreateSchemaNode(connpo);
		TreeItem<TreeNodePo> tableNode = CreateTableNode(connpo, connpo.getDefaultSchema());
		TreeItem<TreeNodePo> viewNode = CreateViewNode(connpo, connpo.getDefaultSchema());
		TreeItem<TreeNodePo> func = CreateFunctionNode(connpo, connpo.getDefaultSchema());
		TreeItem<TreeNodePo> proc = CreateProceduresNode(connpo, connpo.getDefaultSchema());
		moveDefaultNodeToTopAddTable(defSch, scheNode, tableNode, viewNode, func, proc);
		return scheNode;
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

	// 创建表节点
	public static TreeItem<TreeNodePo> CreateTableNode(DbConnectionPo connpo, String sche) {
		TreeItem<TreeNodePo> Table = new TreeItem<>(new TreeNodePo("Table", TreeItemType.TABLE_ROOT,
				ImageViewGenerator.svgImage("window-restore", "blue"), connpo));
		List<TablePo> tabs = DBOptionHelper.getTabsName(connpo, sche, true);// connpo.getTabs(sche);
		System.out.println(tabs.size());
		ObservableList<TreeItem<TreeNodePo>> sourceList = FXCollections.observableArrayList();
		for (TablePo tbpo : tabs) {
			TreeNodePo po = new TreeNodePo(tbpo.getTableName(), TreeItemType.TABLE,
					ImageViewGenerator.svgImageUnactive("table"), connpo);
			po.setTable(tbpo);
			TreeItem<TreeNodePo> subitem = new TreeItem<>(po);
			sourceList.add(subitem);
		}
		Table.getChildren().setAll(sourceList);

		System.out.println(Table.getChildren().size());
		return Table;
	}

	// 创建View节点
	public static TreeItem<TreeNodePo> CreateViewNode(DbConnectionPo connpo, String sche) {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("View", TreeItemType.VIEW_ROOT,
				ImageViewGenerator.svgImage("object-group", "blue"), connpo));
		List<TablePo> tabs = DBOptionHelper.getViewsName(connpo, sche, true);// connpo.getViews(sche);
		System.out.println(tabs.size());
		for (TablePo tbpo : tabs) {
			TreeNodePo po = new TreeNodePo(tbpo.getTableName(), TreeItemType.VIEW,
					ImageViewGenerator.svgImageUnactive("table"), connpo);
			po.setTable(tbpo);
			TreeItem<TreeNodePo> subitem = new TreeItem<>(po);
			Table.getChildren().add(subitem);
		}

		return Table;
	}

	// 创建function节点
	public static TreeItem<TreeNodePo> CreateFunctionNode(DbConnectionPo connpo, String sche) {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("Function", TreeItemType.FUNCTION_ROOT,
				ImageViewGenerator.svgImage("gears", "blue"), connpo));
		List<FuncProcTriggerPo> vals = DBOptionHelper.getFunctions(connpo, sche, true);// connpo.getFunctions(sche);
		addFuncTreeItem(Table, vals, "gear", TreeItemType.FUNCTION, connpo);
		return Table;
	}

	// 创建function节点
	public static TreeItem<TreeNodePo> CreateProceduresNode(DbConnectionPo connpo, String sche) {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("Procedure", TreeItemType.PROCEDURE_ROOT,
				ImageViewGenerator.svgImage("puzzle-piece", "blue"), connpo));
		List<FuncProcTriggerPo> vals = DBOptionHelper.getProcedures(connpo, sche, true); // connpo.getProcedures(sche);

		addFuncTreeItem(Table, vals, "gear", TreeItemType.PROCEDURE, connpo);

		return Table;
	}

	// 默认的schema移动到第一位 , 遍历所有节点, 找默认节点, 从原位置删除, 后再插入到第一个位置
	// 并且添加tableNode
	public static void moveDefaultNodeToTopAddTable(String defSch, TreeItem<TreeNodePo> schemas,
			TreeItem<TreeNodePo> tableNode, TreeItem<TreeNodePo> viewNode, TreeItem<TreeNodePo> funcNode,
			TreeItem<TreeNodePo> procNode) {
		if (defSch != null) {
			ObservableList<TreeItem<TreeNodePo>> ls = schemas.getChildren();
			for (int i = 0; i < ls.size(); i++) {
				TreeItem<TreeNodePo> val = ls.get(i);
				if (val.getValue().getName().equals(defSch)) {
					val.getValue().setIcon(ImageViewGenerator.svgImage("database", "#7CFC00 "));
					val.getChildren().add(tableNode);
					val.getChildren().add(viewNode);
					val.getChildren().add(funcNode);
					val.getChildren().add(procNode);

					ls.remove(i);
					ls.add(0, val);
					break;
				}
			}
		}
	}

	// TreeItem 添加 子节点
	public static void addFuncTreeItem(TreeItem<TreeNodePo> parent, List<FuncProcTriggerPo> tabs, String img,
			TreeItemType type, DbConnectionPo connpo) {
		for (FuncProcTriggerPo po : tabs) {
			addTreeItem(parent, po, img, type, connpo);
		}
	}

	// TreeItem 添加 子节点
	public static void addTreeItem(TreeItem<TreeNodePo> parent, FuncProcTriggerPo fpt, String img, TreeItemType type,
			DbConnectionPo connpo) {
		TreeNodePo po = new TreeNodePo(fpt.getName());
		po.setType(type);
		po.setConnpo(connpo);
		po.setFuncProTri(fpt);
		TreeItem<TreeNodePo> subitem = new TreeItem<>(po);
		if (img != null && img.length() > 0) {
			Node iv = ImageViewGenerator.svgImageUnactive(img);
			po.setIcon(iv);
		}
		parent.getChildren().add(subitem);

	}
}
