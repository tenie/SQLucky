package net.tenie.fx.component.InfoTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.fxmisc.richtext.Caret.CaretVisibility;
import org.fxmisc.richtext.CodeArea;

import com.github.vertical_blank.sqlformatter.SqlFormatter;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyBottomSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SqlcukyTitledPaneInfoPo;
import net.tenie.Sqlucky.sdk.po.TreeItemType;
import net.tenie.Sqlucky.sdk.po.db.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TreeObjAction;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.component.InfoTree.TreeItem.ConnItemContainer;
import net.tenie.fx.component.InfoTree.TreeItem.ConnItemDbObjects;
import net.tenie.fx.component.InfoTree.TreeItem.MyTreeItem;
import net.tenie.fx.component.container.AppWindow;
import net.tenie.fx.dao.ConnectionDao;

/**
 * 链接节点树
 * 
 * @author tenie
 *
 */
public class DBinfoTree {

	public static TreeView<TreeNodePo> DBinfoTreeView;
	public static TreeItem<TreeNodePo> rootNode;

//	public static Region icon;
	private DBInfoTreeContextMenu menu;
	// 缓存 激活的ConnItemContainer
	List<ConnItemContainer> connItemParent = new ArrayList<>();

	// 操作按钮的的面板
	public static VBox operateVbox  ;

	public static DBinfoTreeFilter dbInfoTreeFilter; 
	public static AnchorPane dbInfoTreeFilterPane;
	// 操作按钮集合
	public static List<Node> operateBtns ;
	
	public static TextField filterTextField;
	
	public DBinfoTree() {
		
		createConnsTreeView();
		
		operateVbox = new VBox();
		// 查询过滤
		dbInfoTreeFilter = new DBinfoTreeFilter();
		dbInfoTreeFilterPane = dbInfoTreeFilter.createFilterPane(this.DBinfoTreeView);
		 
		filterTextField = 	dbInfoTreeFilter.getTxt();
		AppWindow.dbInfoTreeFilter = dbInfoTreeFilterPane;
		operateBtns = new ArrayList<>();
	}

	// db节点view
	public TreeView<TreeNodePo> createConnsTreeView() {
		Region icon = IconGenerator.svgImageDefActive("windows-globe");
		rootNode = new TreeItem<>(new TreeNodePo("Connections", icon));
		DBinfoTreeView = new TreeView<>(rootNode);
		DBinfoTreeView.getStyleClass().add("my-tag");
		DBinfoTreeView.setShowRoot(false);

		// 读取数据库数据
		List<SqluckyConnector> datas = ConnectionDao.recoverConnObj();
		// 恢复数据中保存的连接数据
		recoverNode(datas);
		// 展示连接
		if (rootNode.getChildren().size() > 0)
			DBinfoTreeView.getSelectionModel().select(rootNode.getChildren().get(0)); // 选中节点
		// 双击
		DBinfoTreeView.setOnMouseClicked(e -> {
			treeViewDoubleClick(e);
			if (e.getClickCount() == 1) {
				AppWindow.treeView.refresh();
			}
		});
		// 右键菜单
		menu = new DBInfoTreeContextMenu();
		ContextMenu contextMenu = menu.getContextMenu();
		ComponentGetter.dbInfoTreeContextMenu = contextMenu;
		DBinfoTreeView.setContextMenu(contextMenu);
		// 选中监听事件
		DBinfoTreeView.getSelectionModel().selectedItemProperty().addListener(treeViewContextMenu(DBinfoTreeView));
		DBinfoTreeView.getSelectionModel().select(rootNode);

//		DBinfoTreeView = treeView;

		// 显示设置, 从TreeNodePo中的对象显示为 TreeItem 的名称和图标
		DBinfoTreeView.setCellFactory(new TreeNodeCellFactory());
		return DBinfoTreeView;
	}

	// 恢复数据中保存的连接数据, 界面初始化的时候
	public static void recoverNode(List<SqluckyConnector> datas) {
		List<MyTreeItem<TreeNodePo>> ls = new ArrayList<>();
		if (datas != null && datas.size() > 0) {
			for (SqluckyConnector po : datas) {
				TreeNodePo tnpo = new TreeNodePo(po.getConnName(), IconGenerator.svgImageUnactive("unlink"));
				tnpo.setType(TreeItemType.CONNECT_INFO);
				var item = new MyTreeItem<TreeNodePo>(tnpo, po);
				po.setDbInfoTreeNode(item);
				ls.add(item);
			}
		}
		Consumer<String> cr = v -> {
			if (ls.size() > 0) {
				// 连接方法缓存
				Platform.runLater(() -> {
					rootNode.getChildren().addAll(ls);
					DBConns.addAll(datas);
				});
				Platform.runLater(() -> {
					// 打开自动连接的连接
					for (MyTreeItem treeItem : ls) {
						Platform.runLater(() -> {
							SqluckyConnector scp = treeItem.getSqluckyConn();
							if (scp != null) {
								boolean autoConn = scp.getAutoConnect();
								if (autoConn) {
									CommonAction.openConn(treeItem);
								}
							}
						});
					}
				});
			}
		};
		CommonUtils.addInitTask(cr);
	}

	// 清空root, 然后插入新节点
	public static void cleanRootRecoverNodeFromList(List<SqluckyConnector> datas) {
		rootNode.getChildren().clear();
		List<MyTreeItem<TreeNodePo>> ls = new ArrayList<>();
		if (datas != null && datas.size() > 0) {
			for (SqluckyConnector po : datas) {
				TreeNodePo tnpo = new TreeNodePo(po.getConnName(), IconGenerator.svgImageUnactive("unlink"));
				tnpo.setType(TreeItemType.CONNECT_INFO);
				var item = new MyTreeItem<TreeNodePo>(tnpo, po);
				ls.add(item);
			}
		}

		if (ls.size() > 0) {
			// 连接方法缓存
			Platform.runLater(() -> {
				rootNode.getChildren().addAll(ls);
				DBConns.addAll(datas);
			});
			Platform.runLater(() -> {
				// 打开自动连接的连接
				for (MyTreeItem treeItem : ls) {
					Platform.runLater(() -> {
						SqluckyConnector scp = treeItem.getSqluckyConn();
						if (scp != null) {
							boolean autoConn = scp.getAutoConnect();
							if (autoConn) {
								CommonAction.openConn(treeItem);
							}
						}
					});
				}
			});
		}

	}

	// 所有连接节点
	public static ObservableList<TreeItem<TreeNodePo>> allconnsItem() {
		ObservableList<TreeItem<TreeNodePo>> val = DBinfoTreeView.getRoot().getChildren();
		return val;
	}

	// 判断treeItem是一个连接的根节点
	public static boolean isConns(TreeItem<TreeNodePo> item) {
		boolean tf = false;
		if (DBinfoTreeView != null && DBinfoTreeView.getRoot() != null && item != null) {
			if (DBinfoTreeView.getRoot().getChildren() != null) {
				return DBinfoTreeView.getRoot().getChildren().contains(item);
			}
		}

		return tf;
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

	// 判断当前选中的节点是连接节点
	public static boolean currentTreeItemIsConnNode(TreeItem<TreeNodePo> ctt) {
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
		TreeView<TreeNodePo> treeView = AppWindow.treeView;
		TreeItem<TreeNodePo> rootNode = treeView.getRoot();
		rootNode.getChildren().add(item);
		treeView.getSelectionModel().select(item); // 选择新加的节点
	}

	// tree view 双击事件
	public void treeViewDoubleClick(MouseEvent mouseEvent) {
		if (mouseEvent.getClickCount() == 2) {
			TreeItem<TreeNodePo> item = DBinfoTree.getTrewViewCurrentItem();
			if (item == null)
				return;
			TreeItem<TreeNodePo> parentItem = item.getParent();
			if (parentItem == null)
				return;
			// 连接节点双击, 打开节点
			if (DBinfoTree.currentTreeItemIsConnNode()) {
				CommonAction.openConn(item);
				CodeArea codeArea = MyEditorSheetHelper.getCodeArea();
				if (codeArea != null) {
					codeArea.requestFocus();
					codeArea.setShowCaret(CaretVisibility.ON);
				}

//				item.setExpanded(true);
			} // Schemas 双击, 打开非默认的schema
			else if (parentItem != null && parentItem.getValue().getType() == TreeItemType.SCHEMA_ROOT) {
				SqluckyConnector po = getSchameIsConnObj(item);
				// 获取当前schema node 所在的连接节点
				TreeItem<TreeNodePo> connRoot = item.getParent().getParent();
				// 获取当前节点的schema name
				String schemaName = item.getValue().getName();
				// 初始化schema中数据库对象的数据
				ConnItemDbObjects ci = new ConnItemDbObjects(po, schemaName);
				item.getValue().setConnItem(ci);
				//
				ConnItemContainer connItemContainer = connRoot.getValue().getConnItemContainer(); // findConnItemParent(connRoot);
				if (connItemContainer != null) {
					connItemContainer.addConnItem(ci);
					connItemContainer.selectTable(schemaName);
				}
				// TODO
			}
			// 表格
			else if (parentItem.getValue().getType() != null
					&& parentItem.getValue().getType() == TreeItemType.TABLE_ROOT) {
				SqluckyConnector dpo = item.getValue().getConnpo();
				TablePo table = item.getValue().getTable();
				TreeObjAction.showTableSql(dpo, table);
			}
			// 视图
			else if (parentItem.getValue().getType() != null
					&& parentItem.getValue().getType() == TreeItemType.VIEW_ROOT) {
				SqluckyConnector sqluckyconn = item.getValue().getConnpo();
				TablePo table = item.getValue().getTable();
				TreeObjAction.showTableSql(sqluckyconn, table);
			}
			// 函数
			else if (parentItem.getValue().getType() != null
					&& parentItem.getValue().getType() == TreeItemType.FUNCTION_ROOT) {
				SqluckyConnector sqluckyconn = item.getValue().getConnpo();
				FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
				String sqlStr = fpt.getDdl();
				if (StrUtils.isNullOrEmpty(sqlStr)) {
					sqlStr = sqluckyconn.getExportDDL().exportCreateFunction(sqluckyconn.getConn(), fpt.getSchema(),
							fpt.getName());
					if (StrUtils.isNotNullOrEmpty(sqlStr)) {
//						sqlStr = SqlFormatter.format(sqlStr);
						fpt.setDdl(sqlStr);
					}
				}
//				new DataViewTab().showDdlPanel(item.getValue().getName(), sqlStr, true);
//				SqluckyBottomSheet mtd = ComponentGetter.appComponent.ddlSheet(sqluckyconn, item.getValue().getName(),
//						sqlStr, true, false);
//				mtd.show();
				MyBottomSheet.showDdlSheet(sqluckyconn, item.getValue().getName(), sqlStr, true, false);

			} // 过程
			else if (parentItem.getValue().getType() != null
					&& parentItem.getValue().getType() == TreeItemType.PROCEDURE_ROOT) {
				SqluckyConnector sqluckyconn = item.getValue().getConnpo();
				FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
				String sqlStr = fpt.getDdl();

				if (StrUtils.isNullOrEmpty(sqlStr)) {
					sqlStr = sqluckyconn.getExportDDL().exportCreateProcedure(sqluckyconn.getConn(), fpt.getSchema(),
							fpt.getName());
					if (StrUtils.isNotNullOrEmpty(sqlStr)) {
//						sqlStr = SqlFormatter.format(sqlStr);
						fpt.setDdl(sqlStr);
					}
				}

				if (!fpt.isProcedure()) {
					fpt.setProcedure(true);
				}
//				new DataViewTab().showProcedurePanel(item.getValue().getName(), sqlStr, true);
//				SqluckyBottomSheet mtd = ComponentGetter.appComponent.ProcedureSheet(sqluckyconn,
//						item.getValue().getName(), sqlStr, true);
//				mtd.show();
				MyBottomSheet.showProcedureSheet(sqluckyconn, item.getValue().getName(), sqlStr, true);

			} // trigger
			else if (parentItem.getValue().getType() != null
					&& parentItem.getValue().getType() == TreeItemType.TRIGGER_ROOT) {
				SqluckyConnector sqluckyconn = item.getValue().getConnpo();
				FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
				String sqlStr = fpt.getDdl();
				if (StrUtils.isNullOrEmpty(sqlStr)) {
					sqlStr = sqluckyconn.getExportDDL().exportCreateTrigger(sqluckyconn.getConn(), fpt.getSchema(),
							fpt.getName());
					if (StrUtils.isNotNullOrEmpty(sqlStr)) {
						sqlStr = SqlFormatter.format(sqlStr);
						fpt.setDdl(sqlStr);
					}
				}
//				new DataViewTab().showDdlPanel(item.getValue().getName(), sqlStr, false);
//				SqluckyBottomSheet mtd = ComponentGetter.appComponent.ddlSheet(sqluckyconn, item.getValue().getName(),
//						sqlStr, false, false);
//				mtd.show();
				MyBottomSheet.showDdlSheet(sqluckyconn, item.getValue().getName(), sqlStr, false, false);

			} // index
			else if (parentItem.getValue().getType() != null
					&& parentItem.getValue().getType() == TreeItemType.INDEX_ROOT) {
				SqluckyConnector sqluckyconn = item.getValue().getConnpo();
				FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
				String sqlStr = fpt.getDdl();
				if (StrUtils.isNullOrEmpty(sqlStr)) {
					sqlStr = sqluckyconn.getExportDDL().exportCreateIndex(sqluckyconn.getConn(), fpt.getSchema(),
							fpt.getName());
					if (StrUtils.isNotNullOrEmpty(sqlStr)) {
						sqlStr = SqlFormatter.format(sqlStr);
						fpt.setDdl(sqlStr);
					}
				}
//				new DataViewTab().showDdlPanel(item.getValue().getName(), sqlStr, false);
//				SqluckyBottomSheet mtd = ComponentGetter.appComponent.ddlSheet(sqluckyconn, item.getValue().getName(),
//						sqlStr, false, false);
//				mtd.show();
				MyBottomSheet.showDdlSheet(sqluckyconn, item.getValue().getName(), sqlStr, false, false);

			} // Sequence
			else if (parentItem.getValue().getType() != null
					&& parentItem.getValue().getType() == TreeItemType.SEQUENCE_ROOT) {
				SqluckyConnector sqluckyconn = item.getValue().getConnpo();
				FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
				String sqlStr = fpt.getDdl();
				if (StrUtils.isNullOrEmpty(sqlStr)) {
					sqlStr = sqluckyconn.getExportDDL().exportCreateSequence(sqluckyconn.getConn(), fpt.getSchema(),
							fpt.getName());
					if (StrUtils.isNotNullOrEmpty(sqlStr)) {
						sqlStr = SqlFormatter.format(sqlStr);
						fpt.setDdl(sqlStr);
					}
				}
//				new DataViewTab().showDdlPanel(item.getValue().getName(), sqlStr, false);
//				SqluckyBottomSheet mtd = ComponentGetter.appComponent.ddlSheet(sqluckyconn, item.getValue().getName(),
//						sqlStr, false, false);
//				mtd.show();
				MyBottomSheet.showDdlSheet(sqluckyconn, item.getValue().getName(), sqlStr, false, false);
			}

		}
	}

	// treeView 右键菜单属性设置
	public ChangeListener<TreeItem<TreeNodePo>> treeViewContextMenu(TreeView<TreeNodePo> treeView) {
		return new ChangeListener<TreeItem<TreeNodePo>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<TreeNodePo>> observable,
					TreeItem<TreeNodePo> oldValue, TreeItem<TreeNodePo> newValue) {

				// 如果是table 节点 启用add new column
				TreeNodePo nd = newValue != null ? newValue.getValue() : null;
				if (newValue == null || DBinfoTreeView == null)
					return;

				// 复制节点名称
				var nodeName = newValue.getValue().getName();
				menu.copuNodeName(nodeName);

				// 获取链接的TreeItem
				if (Objects.equals(newValue, DBinfoTreeView.getRoot())) { // root
					menu.setConnectDisable(true);
					menu.setTableDisable(true);
					menu.setRefreshDisable(true);
					menu.setLinkDisable(true);
				} else if (DBinfoTree.isConns(newValue)) {
					if (newValue.getChildren().size() == 0) {
						menu.setLinkDisable(false);
						menu.setRefreshDisable(true);
					} else {
						menu.setLinkDisable(true);
						menu.setRefreshDisable(false);
					}
					menu.setConnectDisable(false);
					menu.setTableDisable(true);
					// TABLE
				} else if (nd != null && nd.getType() == TreeItemType.TABLE) {
					menu.setConnectDisable(true);
					menu.setTableDisable(false);
					SqluckyConnector dbc = nd.getConnpo();
					String schema = nd.getTable().getTableSchema();
					String tablename = nd.getTable().getTableName();
					menu.setTableAction(newValue, dbc, schema, tablename);
				} else if (nd != null && nd.getType() == TreeItemType.VIEW) {
					// TODO
					menu.setConnectDisable(true);
					menu.setViewFuncProcTriDisable(false);
					SqluckyConnector dbc = nd.getConnpo();
					String schema = nd.getTable().getTableSchema();
					String viewName = nd.getTable().getTableName();
//					var tabpo = nd.getTable();
					menu.setViewAction(newValue, dbc, schema, viewName);
					menu.setSelectMenuDisable(false, dbc, nd);
				} else if (nd != null && nd.getType() == TreeItemType.FUNCTION) {
					// TODO
					menu.setNodeType(TreeItemType.FUNCTION);
					menu.setConnectDisable(true);
					menu.setViewFuncProcTriDisable(false);
					SqluckyConnector dbc = nd.getConnpo();

					String schema = nd.getFuncProTri().getSchema();
					String funcName = nd.getFuncProTri().getName();
					menu.setFuncAction(newValue, dbc, schema, funcName);
				} else if (nd != null && nd.getType() == TreeItemType.PROCEDURE) {
					// TODO
					menu.setNodeType(TreeItemType.PROCEDURE);
					menu.setConnectDisable(true);
					menu.setViewFuncProcTriDisable(false);
					SqluckyConnector dbc = nd.getConnpo();

					String schema = nd.getFuncProTri().getSchema();
					String procName = nd.getFuncProTri().getName();
					menu.setProcAction(newValue, dbc, schema, procName);
				} else if (nd != null && nd.getType() == TreeItemType.TRIGGER) {
					// TODO
					menu.setNodeType(TreeItemType.TRIGGER);
					menu.setConnectDisable(true);
					menu.setViewFuncProcTriDisable(false);
					SqluckyConnector dbc = nd.getConnpo();

					String schema = nd.getFuncProTri().getSchema();
					String triggerName = nd.getFuncProTri().getName();
					menu.setTriggerAction(newValue, dbc, schema, triggerName);
				} else {
					menu.setNodeType(null);
					menu.setConnectDisable(true);
					menu.setTableDisable(true);
					menu.setRefreshDisable(false);
					menu.setLinkDisable(true);
				}

				if (!menu.getRefresh().isDisable()) {
					TreeItem<TreeNodePo> connItem = ConnItem(newValue);
					menu.setRefreshAction(connItem);
				}

			}
		};
	}

	private TreeItem<TreeNodePo> ConnItem(TreeItem<TreeNodePo> newValue) {
		if (DBinfoTree.isConns(newValue))
			return newValue;
		TreeItem<TreeNodePo> connItem = null;
		TreeItem<TreeNodePo> parent = newValue.getParent();
		while (parent != null) {
			if (parent.getValue().getType() != null && parent.getValue().getType() == TreeItemType.SCHEMA_ROOT) {

				return parent.getParent();
			} else {
				parent = parent.getParent();
			}
		}
		return connItem;
	}

	// 根据链接名称,获取链接Node
	public static TreeItem<TreeNodePo> getConnNode(String dbName) {
//			TreeItem<TreeNodePo> conn =
		TreeItem<TreeNodePo> root = AppWindow.treeView.getRoot();
		// 遍历tree root 找到对于的数据库节点
		for (TreeItem<TreeNodePo> connNode : root.getChildren()) {
			if (connNode.getValue().getName().equals(dbName)) {
				return connNode;
			}

		}
		return null;
	}

	// 获取schema节点的 TreeNodePo
	public static TreeNodePo getSchemaTableNodePo(String schema) {
		Label lb = ComponentGetter.connComboBox.getValue();
		if (lb != null) {
			String str = lb.getText();
			TreeItem<TreeNodePo> tnp = DBinfoTree.getConnNode(str);
			if (StrUtils.isNullOrEmpty(schema)) {
				SqluckyConnector dbpo = DBConns.get(str);
				schema = dbpo.getDefaultSchema();
			}

			if (tnp != null) {
				if (tnp.getChildren().size() > 0) {
					ObservableList<TreeItem<TreeNodePo>> lsShc = tnp.getChildren().get(0).getChildren();
					for (TreeItem<TreeNodePo> sche : lsShc) {
						if (sche.getValue().getName().equals(schema)) {
							return sche.getValue();
						}
					}
				}

			}
		}
		return null;
	}

	// 根据链接名称,获取链接Node
	public static TreeItem<TreeNodePo> getSchemaNode(String dbName, String SchemaName) {
		TreeItem<TreeNodePo> connNode = getConnNode(dbName);
		if (connNode != null) {
			TreeItem<TreeNodePo> schemaParent = connNode.getChildren().get(0);
			for (TreeItem<TreeNodePo> schNode : schemaParent.getChildren()) {
				if (schNode.getValue().getName().equals(SchemaName)) {
					return schNode;
				}
			}
		}

		return null;
	}

	// 获取库的连接对象
	public static SqluckyConnector getSchameIsConnObj(TreeItem<TreeNodePo> item) {
		String connName = item.getParent().getParent().getValue().getName();
		return DBConns.get(connName);
	}

	// TitledPane
	public TitledPane dbInfoTitledPane(Pane treeBtnPane) {
		TitledPane dbTitledPane = new TitledPane();
		dbTitledPane.setText("DB Connection");
		dbTitledPane.setUserData(new SqlcukyTitledPaneInfoPo("Sqlucky DB Connection", treeBtnPane));
		CommonUtils.addCssClass(dbTitledPane, "titledPane-color");
		dbTitledPane.setContent(DBinfoTreeView);

		// 图标切换
		CommonUtils.addInitTask(v -> {
			Platform.runLater(() -> {
				CommonUtils.setLeftPaneIcon(dbTitledPane, ComponentGetter.iconInfo, ComponentGetter.uaIconInfo);
			});

		});

		return dbTitledPane;
	}
}
