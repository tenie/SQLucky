package net.tenie.fx.component.InfoTree;

import com.jfoenix.controls.JFXButton;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.component.InfoTree.TreeItem.ConnItemContainer;
import net.tenie.fx.component.InfoTree.TreeItem.ConnItemDbObjects;
import net.tenie.fx.component.InfoTree.TreeItem.MyTreeItem;

/**
 * 
 * @author tenie
 *
 */
public class DBinfoTreeFilter {
	// 界面上所有链接节点的临时缓存， 之后的查找过滤都是对tempAllConnNode操作
	private ObservableList<TreeItem<TreeNodePo>> tempAllConnNode = FXCollections.observableArrayList();
	// 缓存查找到的节点容器， 查找到的数据库对象放入filtList， filtList之后放入新的根节点用来界面展示
	private ObservableList<TreeItem<TreeNodePo>> filtList = FXCollections.observableArrayList();
	// 界面上的根节点
	private TreeItem<TreeNodePo> rootNode;
	private TextField txt;

	public DBinfoTreeFilter() {
	}

	public TextField getTxtField() {
		return txt;
	}

	/**
	 * 创建查询树节点的输入框
	 * 
	 * @param treeView
	 * @return
	 */
	public AnchorPane createFilterPane(TreeView<TreeNodePo> treeView) {
		txt = new TextField();
		ComponentGetter.dbInfoFilter = txt;
		AnchorPane filterPane = new AnchorPane();
		filterPane.setPrefHeight(30);
		filterPane.setMinHeight(30);
		JFXButton query = new JFXButton();
		query.setGraphic(IconGenerator.svgImageDefActive("search"));
		query.setOnAction(e -> {
			txt.requestFocus();
		});

		txt.setPrefWidth(230);
		txt.setPrefHeight(25);
		txt.setMaxHeight(25);
		txt.getStyleClass().add("myTextField");
		int x = 0;
		query.setLayoutX(x);
		query.setLayoutY(1);
		x += 35;
		txt.setLayoutX(x);
		txt.setLayoutY(1);

		Button clean = new Button();

		AnchorPane.setLeftAnchor(clean, 220.0);
		AnchorPane.setTopAnchor(clean, 5.0);
		clean.setMaxSize(12, 12);

		clean.setGraphic(IconGenerator.svgImageUnactive("times-circle", 14));
		clean.getStyleClass().add("myCleanBtn");
		clean.setVisible(false); // clean 按钮默认不显示, 只有在鼠标进入搜索框才显示

		clean.setOnAction(e -> {
			txt.clear();
		});

		filterPane.setOnMouseEntered(e -> {
			clean.setVisible(true);
		});
		filterPane.setOnMouseExited(e -> {
			clean.setVisible(false);
		});

		filterPane.getChildren().addAll(query, txt, clean);

		txt.textProperty().addListener((o, oldVal, newVal) -> {
			ComponentGetter.dbTitledPane.setExpanded(true);
			// 缓存,
			ObservableList<TreeItem<TreeNodePo>> allConnNode = treeView.getRoot().getChildren();
			if (tempAllConnNode.size() < allConnNode.size()) {
				tempAllConnNode.clear();
				tempAllConnNode.addAll(allConnNode);
				rootNode = treeView.getRoot();
			}

			// 恢复
			if (StrUtils.isNullOrEmpty(newVal)) {
				if (rootNode != null) {
					treeView.setRoot(rootNode);
				}
			}

			// 查询时
			if (StrUtils.isNotNullOrEmpty(newVal)) {
				filtList.clear();
				// 遍历每一个连接节点, 在节点下查找到了数据, 就会返回一个新节点对象, 最后使用新节点创建一个新的树
				for (int i = 0; i < tempAllConnNode.size(); i++) {
					// 获取一个链接节点
					TreeItem<TreeNodePo> connNode = tempAllConnNode.get(i);
					// 查找过滤后， 得到一个新的链接节点
					TreeItem<TreeNodePo> nConnNode = linkTreeItemOption(connNode, newVal);
					// 新节点不是NULL 缓存
					if (nConnNode != null) {
						filtList.add(nConnNode);
					}
				}
				// 创建一个新的树根, 将查询数据挂在新的上面
				TreeItem<TreeNodePo> filterRootNode = new TreeItem<>(
						new TreeNodePo("Connections", IconGenerator.svgImageDefActive("windows-globe")));
				//
				filterRootNode.getChildren().addAll(filtList);
				// 使用新的树根
				treeView.setRoot(filterRootNode);

				if (filtList.size() > 0) {
					// 展开treeview
					CommonAction.unfoldTreeView();
				}

			}

		});

		return filterPane;
	}

	/*
	 * 传递连接节点, 对其进行过滤 如果节点包含查询内容就返回一个新的节点, 否则返回null
	 */
	private TreeItem<TreeNodePo> linkTreeItemOption(TreeItem<TreeNodePo> conn, String queryStr) {
		// 1. 首先看节点是否激活的(有子节点?)
		if (conn.getChildren().size() > 0) {
			ConnItemContainer cip = conn.getValue().getConnItemContainer();
			if (cip != null && cip.getSchemaNode().getChildren().size() > 0) {
				// 获取Schema的节点集合
				ObservableList<TreeItem<TreeNodePo>> vals = cip.getSchemaNode().getChildren();

				ConnItemContainer filterCIContainer = new ConnItemContainer(cip.getConnpo());
				SqluckyConnector connpo = filterCIContainer.getConnpo();

				// 2. 遍历每个schema节点
				for (int i = 0; i < vals.size(); i++) {
					TreeItem<TreeNodePo> sche = vals.get(i);
					int count = 0;
					int sz = 0;
					// 如果schema节点 是激活的
					if (sche.getChildren().size() > 0) {
						// 从schema节点下获取数据对象
						ConnItemDbObjects ci = sche.getValue().getConnItem();
						// 创建一个新的数据对象, 来存储过滤后的数据filterConnObjs
						ConnItemDbObjects filterCIDO = new ConnItemDbObjects();
						filterCIDO.initConnItem(connpo, ci.getSchemaName());

						// 开始查找, 从表开始
						count += filterHelper(ci.getTableNode(), queryStr, filterCIDO.getTableNode(),
								filterCIDO.getParentNode());

						count += filterHelper(ci.getViewNode(), queryStr, filterCIDO.getViewNode(),
								filterCIDO.getParentNode());

						count += filterHelper(ci.getFuncNode(), queryStr, filterCIDO.getFuncNode(),
								filterCIDO.getParentNode());

						count += filterHelper(ci.getProcNode(), queryStr, filterCIDO.getProcNode(),
								filterCIDO.getParentNode());

						count += filterHelper(ci.getTriggerNode(), queryStr, filterCIDO.getTriggerNode(),
								filterCIDO.getParentNode());

						count += filterHelper(ci.getIndexNode(), queryStr, filterCIDO.getIndexNode(),
								filterCIDO.getParentNode());

						count += filterHelper(ci.getSequenceNode(), queryStr, filterCIDO.getSequenceNode(),
								filterCIDO.getParentNode());

						// 如果找到了数据, 将新的数据对象, 放入schema数据对象
						if (count > 0) {
							filterCIContainer.addChildren(filterCIDO);
						}
					}
				}
				// schema数据对象有数据的 情况, 创建一个新的连接节点并返回它
				if (filterCIContainer.getSchemaNode().getChildren().size() > 0) {
					MyTreeItem<TreeNodePo> filterLinkTreeItem = new MyTreeItem<TreeNodePo>(
							new TreeNodePo(connpo.getConnName(), IconGenerator.svgImage("link", "#7CFC00")), connpo);

					filterLinkTreeItem.getChildren().add(filterCIContainer.getSchemaNode());
					filterLinkTreeItem.getValue().setConnItemContainer(filterCIContainer);
					return filterLinkTreeItem;
				}

			}
		}

		return null;
	}

	/**
	 * 
	 * @param parentNode     父节点（比包含所有表格的那个节点）
	 * @param queryStr       要查询的字符串
	 * @param parentNewNode  新父节点（把查找到的子节点放到这个新父节点下）
	 * @param dbObjNewParent 新父节点的父节点(存放所有数据库对象集合的节点)
	 * @return 返回一个统计结果， 找到了几个节点
	 */
	private static int filterHelper(TreeItem<TreeNodePo> parentNode, String queryStr,
			TreeItem<TreeNodePo> parentNewNode, TreeItem<TreeNodePo> dbObjNewParent) {
		int count = 0;
		if (parentNode != null) {
			var val = filter(parentNode.getChildren(), queryStr);
			int sz = val.size();
			if (sz > 0) {
				parentNewNode.getChildren().setAll(val);
				dbObjNewParent.getChildren().add(parentNewNode);
				count += val.size();
			}
		}
		return count;
	}

	/**
	 * 从TreeItem 集合中找出值 和 查询的字符串做contains， 如果匹配把TreeItem放入到新的集合里
	 * 
	 * @param val TreeItem 集合
	 * @param str 查询的字符串
	 * @return 新集合包含里了查找到的 个别TreeItem
	 */
	private static ObservableList<TreeItem<TreeNodePo>> filter(ObservableList<TreeItem<TreeNodePo>> val, String str) {
		ObservableList<TreeItem<TreeNodePo>> rs = FXCollections.observableArrayList();
		String temp = str.toUpperCase();
		val.forEach(v -> {
			if (v.getValue().getName().toUpperCase().contains(temp)) {
				rs.add(v);
			}
		});
		return rs;
	}

}
