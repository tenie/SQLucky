package net.tenie.fx.component.container;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import com.github.vertical_blank.sqlformatter.SqlFormatter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.TreeObjAction;
import net.tenie.fx.PropertyPo.TreeItemType;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.TreeItem.ConnItemContainer;
import net.tenie.fx.component.TreeItem.ConnItemDbObjects;
import net.tenie.fx.component.TreeItem.MyTreeItem;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.dao.ConnectionDao;
import net.tenie.fx.factory.MenuFactory;
import net.tenie.fx.factory.TaskCellFactory;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.po.DBOptionHelper;
import net.tenie.lib.po.DbConnectionPo;
import net.tenie.lib.po.FuncProcTriggerPo;
import net.tenie.lib.po.TablePo;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class DBinfoTree {

	public static TreeView<TreeNodePo> DBinfoTreeView;
	public static ContextMenu contextMenu;
	private TreeView<TreeNodePo> treeView;
	private ObservableList<TreeItem<TreeNodePo>> connsNode;
	// 缓存 激活的ConnItemContainer
	List<ConnItemContainer> connItemParent = new ArrayList<>(); 
	
	
	public DBinfoTree() {
		treeView = createConnsTreeView();
	}

	// db节点view
	public TreeView<TreeNodePo> createConnsTreeView() {
		TreeItem<TreeNodePo> rootNode = new TreeItem<>(
				new TreeNodePo("Connections", ImageViewGenerator.svgImageDefActive("windows-globe"))); //connectdevelop  windows-globe
		TreeView<TreeNodePo> treeView = new TreeView<>(rootNode);
		treeView.getStyleClass().add("my-tag");
		try {
			// 恢复数据中保存的连接数据
			Connection H2conn = H2Db.getConn();
			List<DbConnectionPo> datas = ConnectionDao.selectData(H2conn);
			if (datas != null && datas.size() > 0) {
				for (DbConnectionPo po : datas) {
					MyTreeItem<TreeNodePo> item = new MyTreeItem<>(
							new TreeNodePo(po.getConnName(), ImageViewGenerator.svgImageUnactive("unlink")));
					rootNode.getChildren().add(item);
					DBConns.add(po.getConnName(), po);

				}

			}
			connsNode = rootNode.getChildren();

			// 展示连接
			if (rootNode.getChildren().size() > 0)
				treeView.getSelectionModel().select(rootNode.getChildren().get(0)); // 选中节点
			// 双击
			treeView.setOnMouseClicked(e -> {
				treeViewDoubleClick(e);
			});
			// 右键菜单
			contextMenu = MenuFactory.CreateTreeViewConnMenu();	//CreateConnMenu(); // ComponentGetter.getConnMenu();
			treeView.setContextMenu(contextMenu);
			// 选中监听事件
			treeView.getSelectionModel().selectedItemProperty().addListener(treeViewContextMenu(treeView));
		} finally {
			H2Db.closeConn();
		}
		DBinfoTreeView = treeView;
		treeView.setCellFactory(new TaskCellFactory());
		return treeView;
	}

	// 所有连接节点
	public static ObservableList<TreeItem<TreeNodePo>> allconnsItem() {
		ObservableList<TreeItem<TreeNodePo>> val = DBinfoTreeView.getRoot().getChildren();
		return val;
	}

	// 判断treeItem是一个连接的根节点
	public static boolean isConns(TreeItem<TreeNodePo> item) {
		return DBinfoTreeView.getRoot().getChildren().contains(item);
	}

	// 删除 取连接节点 根据名字
	public static void rmTreeItemByName(String name) {
		ObservableList<TreeItem<TreeNodePo>> ls = allconnsItem();
		ls.removeIf(item -> {
			return item.getValue().getName().equals(name);
		});
	}

	// 判断当前选中的节点是连接节点
	public static boolean currentTreeItemIsConnNode() {
		TreeItem<TreeNodePo> ctt = getTrewViewCurrentItem();
		return allconnsItem().contains(ctt);
	}

	// 获取当前选中的节点
	public static TreeItem<TreeNodePo> getTrewViewCurrentItem() {
		TreeItem<TreeNodePo> ctt = DBinfoTreeView.getSelectionModel().getSelectedItem();
		return ctt;
	}

	// 根据名称获取连接节点
	public static TreeItem<TreeNodePo> getTreeItemByName(String name) {
		ObservableList<TreeItem<TreeNodePo>> ls = allconnsItem();
		for (TreeItem<TreeNodePo> item : ls) {
			if (item.getValue().getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	// 给root节点加元素（db连接节点）
	public static void treeRootAddItem(TreeItem<TreeNodePo> item) {
		TreeView<TreeNodePo> treeView = ComponentGetter.treeView;
		TreeItem<TreeNodePo> rootNode = treeView.getRoot();
		rootNode.getChildren().add(item);
		treeView.getSelectionModel().select(item); // 选择新加的节点
	}

	// tree view 双击事件
	public void treeViewDoubleClick(MouseEvent mouseEvent) {
		if (mouseEvent.getClickCount() == 2) {
			TreeItem<TreeNodePo> item = DBinfoTree.getTrewViewCurrentItem();
			TreeItem<TreeNodePo> parentItem = item.getParent();
			// 连接节点双击, 打开节点
			if (DBinfoTree.currentTreeItemIsConnNode()) {
				ConnectionEditor.openConn(item);
//				item.setExpanded(true);
			} // Schemas 双击, 打开非默认的schema
			else if (parentItem != null && "Schemas".equals(parentItem.getValue().getName())) {
				DbConnectionPo po = ComponentGetter.getSchameIsConnObj(item);
				// 获取当前schema node 所在的连接节点
				TreeItem<TreeNodePo> connRoot = item.getParent().getParent();
				// 获取当前节点的schema name
				String schemaName = item.getValue().getName();
				// 初始化schema中数据库对象的数据
				ConnItemDbObjects ci = new ConnItemDbObjects(po, schemaName); 
				item.getValue().setConnItem(ci);
				// 
				ConnItemContainer connItemContainer = connRoot.getValue().getConnItemContainer(); //findConnItemParent(connRoot);
				if(connItemContainer != null ) {
					connItemContainer.addConnItem(ci);
					connItemContainer.selectTable(schemaName);
				}
				//TODO 
			}
			// 表格
			else if (parentItem.getValue().getType() != null && 
					 parentItem.getValue().getType() == TreeItemType.TABLE_ROOT) {
				DbConnectionPo dpo = item.getValue().getConnpo();
				TablePo table = item.getValue().getTable();
//				String createTableSql = table.getDdl();
//				if (StrUtils.isNullOrEmpty(createTableSql)) {
//					createTableSql = DBOptionHelper.getCreateTableSQL(dpo, table.getTableSchema(),
//							table.getTableName());
//					createTableSql = SqlFormatter.format(createTableSql);
//					table.setDdl(createTableSql);
//				}
//				DataViewTab.showDdlPanel(item.getValue().getName(), createTableSql);
				TreeObjAction.showTableSql(dpo, table, item.getValue().getName());
			}
			// 视图
			else if (parentItem.getValue().getType() != null && 
					 parentItem.getValue().getType() == TreeItemType.VIEW_ROOT) {
				DbConnectionPo dpo = item.getValue().getConnpo(); 
				TablePo table = item.getValue().getTable();
				TreeObjAction.showTableSql(dpo, table, item.getValue().getName());
//				String sqlStr = table.getDdl();
//				if (StrUtils.isNullOrEmpty(sqlStr)) {
//					sqlStr = DBOptionHelper.getViewSQL(dpo, table.getTableSchema(), table.getTableName());
//					sqlStr = SqlFormatter.format(sqlStr);
//					table.setDdl(sqlStr);
//				}
//				DataViewTab.showDdlPanel(item.getValue().getName(), sqlStr);

			}
			// 函数
			else if (parentItem.getValue().getType() != null
					&& parentItem.getValue().getType() == TreeItemType.FUNCTION_ROOT) {
				DbConnectionPo dpo = item.getValue().getConnpo();
				FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
				String sqlStr = fpt.getDdl();
				if(StrUtils.isNullOrEmpty(sqlStr)) { 
					sqlStr = dpo.getExportDDL().exportCreateFunction(dpo.getConn(), fpt.getSchema(), fpt.getName());
					if(StrUtils.isNotNullOrEmpty(sqlStr)) {
						sqlStr = SqlFormatter.format(sqlStr);
						fpt.setDdl(sqlStr);
					}
				}
				DataViewTab.showDdlPanel(item.getValue().getName(), sqlStr);

			} // 过程
			else if (parentItem.getValue().getType() != null
					&& parentItem.getValue().getType() == TreeItemType.PROCEDURE_ROOT) {
				DbConnectionPo dpo = item.getValue().getConnpo();
				FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
				String sqlStr = fpt.getDdl(); 
				
				if(StrUtils.isNullOrEmpty(sqlStr)) { 
					sqlStr = dpo.getExportDDL().exportCreateProcedure(dpo.getConn(), fpt.getSchema(), fpt.getName());
					if(StrUtils.isNotNullOrEmpty(sqlStr)) {
						sqlStr = SqlFormatter.format(sqlStr);
						fpt.setDdl(sqlStr);
					}
				}
				DataViewTab.showDdlPanel(item.getValue().getName(), sqlStr);

			} // trigger
			else if (parentItem.getValue().getType() != null
					&& parentItem.getValue().getType() == TreeItemType.TRIGGER_ROOT) {
				DbConnectionPo dpo = item.getValue().getConnpo();
				FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
				String sqlStr = fpt.getDdl(); 
				if(StrUtils.isNullOrEmpty(sqlStr)) { 
					sqlStr = dpo.getExportDDL().exportCreateTrigger(dpo.getConn(), fpt.getSchema(), fpt.getName());
					if(StrUtils.isNotNullOrEmpty(sqlStr)) {
						sqlStr = SqlFormatter.format(sqlStr);
						fpt.setDdl(sqlStr);
					}
				} 
				DataViewTab.showDdlPanel(item.getValue().getName(), sqlStr);

			}// index
			else if (parentItem.getValue().getType() != null
					&& parentItem.getValue().getType() == TreeItemType.INDEX_ROOT) {
				DbConnectionPo dpo = item.getValue().getConnpo();
				FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
				String sqlStr = fpt.getDdl(); 
				if(StrUtils.isNullOrEmpty(sqlStr)) { 
					sqlStr = dpo.getExportDDL().exportCreateIndex(dpo.getConn(), fpt.getSchema(), fpt.getName());
					if(StrUtils.isNotNullOrEmpty(sqlStr)) {
						sqlStr = SqlFormatter.format(sqlStr);
						fpt.setDdl(sqlStr);
					}
				}
				DataViewTab.showDdlPanel(item.getValue().getName(), sqlStr);

			}// Sequence
			else if (parentItem.getValue().getType() != null
					&& parentItem.getValue().getType() == TreeItemType.SEQUENCE_ROOT) {
				DbConnectionPo dpo = item.getValue().getConnpo();
				FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
				String sqlStr = fpt.getDdl();  
				if(StrUtils.isNullOrEmpty(sqlStr)) { 
					sqlStr = dpo.getExportDDL().exportCreateSequence(dpo.getConn(), fpt.getSchema(), fpt.getName());
					if(StrUtils.isNotNullOrEmpty(sqlStr)) {
						sqlStr = SqlFormatter.format(sqlStr);
						fpt.setDdl(sqlStr);
					}
				} 
				DataViewTab.showDdlPanel(item.getValue().getName(), sqlStr);

			}

		}
	}

	// treeView 右键菜单属性设置
	public static ChangeListener<TreeItem<TreeNodePo>> treeViewContextMenu(TreeView<TreeNodePo> treeView) {
		return new ChangeListener<TreeItem<TreeNodePo>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<TreeNodePo>> observable,
					TreeItem<TreeNodePo> oldValue, TreeItem<TreeNodePo> newValue) {
				// 非连接节点禁用右键的菜单
				if (!DBinfoTree.isConns(newValue)) {
					ContextMenu contextMenu = treeView.getContextMenu();
					for (MenuItem item : contextMenu.getItems()) {
						if (!item.getText().equals("Add Connection")) {
							item.setDisable(true);
						}
					}
				} else {
					ContextMenu contextMenu = treeView.getContextMenu();
					contextMenu.getItems().forEach(item -> {
						item.setDisable(false);
					});
				}

			}
		};
	}

//	// treeView 右键菜单
//	public static ContextMenu CreateTreeViewConnMenu() {
//		contextMenu = new ContextMenu();
//
//		MenuItem add = new MenuItem("Add Connection");
//		add.setOnAction(e -> {
//			ConnectionEditor.ConnectionInfoSetting();
//		});
//		add.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
//
//		MenuItem link = new MenuItem("Open Connection");
//		link.setOnAction(CommonEventHandler.openConnEvent());
//		link.setGraphic(ImageViewGenerator.svgImageDefActive("link"));
//
//		MenuItem unlink = new MenuItem("Close Connection");
//		unlink.setOnAction(CommonEventHandler.closeConnEvent());
//		unlink.setGraphic(ImageViewGenerator.svgImageDefActive("unlink"));
//
//		MenuItem Edit = new MenuItem("Edit Connection");
//		Edit.setOnAction(CommonEventHandler.editConnEvent());
//		Edit.setGraphic(ImageViewGenerator.svgImageDefActive("edit"));
//
//		MenuItem delete = new MenuItem("Delete Connection");
//		delete.setOnAction(e -> {
//			ConnectionEditor.ConnectionInfoSetting();
//		});
//		delete.setGraphic(ImageViewGenerator.svgImageDefActive("trash"));
//
//		contextMenu.getItems().addAll(add, link, unlink, Edit, delete);
//
//		return contextMenu;
//	}

	public TreeView<TreeNodePo> getTreeView() {
		return treeView;
	}

	public void setTreeView(TreeView<TreeNodePo> treeView) {
		this.treeView = treeView;
	}
 
}
