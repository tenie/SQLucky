package net.tenie.fx.component.InfoTree;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.component.SqluckyTitledPane;
import net.tenie.Sqlucky.sdk.component.sheet.bottom.MyBottomSheet;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.TreeItemType;
import net.tenie.Sqlucky.sdk.po.component.ConnItemContainer;
import net.tenie.Sqlucky.sdk.po.component.ConnItemDbObjects;
import net.tenie.Sqlucky.sdk.po.component.MyTreeItem;
import net.tenie.Sqlucky.sdk.po.component.TreeNodePo;
import net.tenie.Sqlucky.sdk.po.db.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TreeObjAction;
import net.tenie.fx.component.container.AppWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.Caret.CaretVisibility;
import org.fxmisc.richtext.CodeArea;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 链接节点树
 * 
 * @author tenie
 *
 */
public class DBinfoTree extends SqluckyTitledPane {
	private static final Logger logger = LogManager.getLogger(DBinfoTree.class);
	public static TreeView<TreeNodePo> DBinfoTreeView;
	public static TreeItem<TreeNodePo> rootNode;
	private DBInfoTreeContextMenu contextMenu;
	// 操作按钮的的面板
	public static VBox operateVbox  ;

	public static DBinfoTreeFilter dbInfoTreeFilter; 
	public static HBox dbInfoTreeFilterPane;
	// 操作按钮集合
	public static List<Node> operateBtns ;
	
	public static TextField filterTextField;

	private VBox dbInfoTreeBtnPane ;

	public DBinfoTree() {
		super();
		createConnsTreeView();
		operateVbox = new VBox();
		// 查询过滤
		dbInfoTreeFilter = new DBinfoTreeFilter();
		dbInfoTreeFilterPane = dbInfoTreeFilter.createFilterPane(DBinfoTreeView);

		filterTextField = dbInfoTreeFilter.getTxt();
		operateBtns = new ArrayList<>();

		dbInfoTreeBtnPane = DBinfoTreeButtonFactory.createTreeViewbtn(this);
		this.setText("DB Connection");

		this.setBtnsBox(dbInfoTreeBtnPane);
		CommonUtils.addCssClass(this, "titledPane-color");
		this.setContent(DBinfoTreeView);
	}

	// db节点view
	public void createConnsTreeView() {
		Region icon = IconGenerator.svgImageDefActive("windows-globe");
		rootNode = new TreeItem<>(new TreeNodePo("Connections", icon));
		DBinfoTreeView = new TreeView<>(rootNode);
		DBinfoTreeView.getStyleClass().add("my-tag");
		DBinfoTreeView.setShowRoot(false);
		// TreeView 会拦截escape 按钮, 所以重新加上 escape的事件
		DBinfoTreeView.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if(e.getCode() == KeyCode.ESCAPE){
				CommonUtils.pressBtnESC();
			}
		});

		// 展示连接
		if (!rootNode.getChildren().isEmpty()) {
			// 选中节点
            DBinfoTreeView.getSelectionModel().select(rootNode.getChildren().getFirst());
        }
		// 双击
		DBinfoTreeView.setOnMouseClicked(e -> {
			// 单击
			if (e.getClickCount() == 2) {
				// 双击
				treeViewDoubleClick(e);
				AppWindow.treeView.refresh();
			}
		});
		// 右键菜单
		contextMenu = new DBInfoTreeContextMenu();
		ComponentGetter.dbInfoTreeContextMenu = contextMenu;
		DBinfoTreeView.setContextMenu(contextMenu);
		// 选中监听事件
		DBinfoTreeView.getSelectionModel().selectedItemProperty().addListener(treeViewContextMenu(DBinfoTreeView));
		DBinfoTreeView.getSelectionModel().select(rootNode);

		// 显示设置, 从TreeNodePo中的对象显示为 TreeItem 的名称和图标
		DBinfoTreeView.setCellFactory(new TreeNodeCellFactory());
	}

	// 恢复数据中保存的连接数据, 界面初始化的时候
	public static void recoverNode(List<SqluckyConnector> datas) {
		List<MyTreeItem<TreeNodePo>> ls = new ArrayList<>();
		if (datas != null && !datas.isEmpty()) {
			for (SqluckyConnector po : datas) {
				TreeNodePo tnpo = new TreeNodePo(po.getConnName(), IconGenerator.svgImageUnactive("unlink"));
				tnpo.setType(TreeItemType.CONNECT_INFO);
				var item = new MyTreeItem<TreeNodePo>(tnpo, po);
				po.setDbInfoTreeNode(item);
				ls.add(item);
			}
		}
		if (!ls.isEmpty()) {
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
								DBinfoTree.openConn(treeItem, false);
							}
						}
					});
				}
			});
		}
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

		if (!ls.isEmpty()) {
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
								DBinfoTree.openConn(treeItem, false);
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
		var childrenLs = rootNode.getChildren();
		int idx = 0;
		if(!childrenLs.isEmpty()){
			for(int i = 0; i< childrenLs.size() ; i++){
				var children = childrenLs.get(i);
				if(!children.getChildren().isEmpty()){
					continue;
				}else {
					idx = i ;
					break;
				}
			}
			childrenLs.add(idx, item);
		}else {
			rootNode.getChildren().addFirst(item);
		}
		// 选择新加的节点
		treeView.getSelectionModel().select(item);
	}

	// tree view 双击事件
	public void treeViewDoubleClick(MouseEvent mouseEvent) {
		if (mouseEvent.getClickCount() == 2) {
			TreeItem<TreeNodePo> item = DBinfoTree.getTrewViewCurrentItem();
			if (item == null) {
				return;
			}
			TreeItem<TreeNodePo> parentItem = item.getParent();
			if (parentItem == null) {
				return;
			}

			// 连接节点双击, 打开节点
			if (DBinfoTree.currentTreeItemIsConnNode()) {
				DBinfoTree.openConn(item, false);
				CodeArea codeArea = MyEditorSheetHelper.getCodeArea();
				if (codeArea != null) {
					codeArea.requestFocus();
					codeArea.setShowCaret(CaretVisibility.ON);
				}
				String connName = item.getValue().getName();
				DBConns.selectComboBoxItem(connName);
			} else {
				// 检查连接是否正常
				SqluckyConnector sqluckyConnector = item.getValue().getConnpo();
				boolean connError = sqluckyConnector.checkSqluckyConnector();
				if (connError) {
					return;
				}
				// Schemas 双击, 打开非默认的schema
				if (parentItem.getValue().getType() == TreeItemType.SCHEMA_ROOT) {
//					SqluckyConnector po = getSchameIsConnObj(item);
					// 获取当前schema node 所在的连接节点
					TreeItem<TreeNodePo> connRoot = item.getParent().getParent();
					// 获取当前节点的schema name
					String schemaName = item.getValue().getName();
					// 初始化schema中数据库对象的数据
					ConnItemDbObjects ci = new ConnItemDbObjects(sqluckyConnector, schemaName);
					item.getValue().setConnItem(ci);
					//
					ConnItemContainer connItemContainer = connRoot.getValue().getConnItemContainer(); // findConnItemParent(connRoot);
					if (connItemContainer != null) {
						connItemContainer.addConnItem(ci);
						connItemContainer.selectTable(schemaName);
					}

//				String defaultSchemaName = po.getDBConnectorInfoPo().getDefaultSchema();
//				if(StrUtils.isNullOrEmpty(defaultSchemaName)){
//					po.getDBConnectorInfoPo().setDefaultSchema(schemaName);
//				}
					// 重新连接
					sqluckyConnector.resetJdbcUrlStr(schemaName);
					ConnItemContainer.moveSchemaToTop(schemaName, parentItem);
				}
				// 表格
				else if (parentItem.getValue().getType() != null
						&& parentItem.getValue().getType() == TreeItemType.TABLE_ROOT) {
					TablePo table = item.getValue().getTable();
					TreeObjAction.showTableSql(sqluckyConnector, table);
				}
				// 视图
				else if (parentItem.getValue().getType() != null
						&& parentItem.getValue().getType() == TreeItemType.VIEW_ROOT) {
					TablePo table = item.getValue().getTable();
					TreeObjAction.showTableSql(sqluckyConnector, table);
				}
				// 函数
				else if (parentItem.getValue().getType() != null
						&& parentItem.getValue().getType() == TreeItemType.FUNCTION_ROOT) {
					FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
					String sqlStr = fpt.getDdl();
					if (StrUtils.isNullOrEmpty(sqlStr)) {
						sqlStr = sqluckyConnector.getExportDDL().exportCreateFunction(sqluckyConnector.getConn(), fpt.getSchema(),
								fpt.getName());
						if (StrUtils.isNotNullOrEmpty(sqlStr)) {
							fpt.setDdl(sqlStr);
						}
					}

					MyBottomSheet.showDdlSheet(sqluckyConnector, item.getValue().getName(), sqlStr, true, false);

				} // 过程
				else if (parentItem.getValue().getType() != null
						&& parentItem.getValue().getType() == TreeItemType.PROCEDURE_ROOT) {
					FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
					String sqlStr = fpt.getDdl();

					if (StrUtils.isNullOrEmpty(sqlStr)) {
						sqlStr = sqluckyConnector.getExportDDL().exportCreateProcedure(sqluckyConnector.getConn(), fpt.getSchema(),
								fpt.getName());
						if (StrUtils.isNotNullOrEmpty(sqlStr)) {
//						sqlStr = SqlFormatter.format(sqlStr);
							fpt.setDdl(sqlStr);
						}
					}

					if (!fpt.isProcedure()) {
						fpt.setProcedure(true);
					}
					MyBottomSheet.showProcedureSheet(sqluckyConnector, item.getValue().getName(), sqlStr);

				} // trigger
				else if (parentItem.getValue().getType() != null
						&& parentItem.getValue().getType() == TreeItemType.TRIGGER_ROOT) {
					FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
					String sqlStr = fpt.getDdl();
					if (StrUtils.isNullOrEmpty(sqlStr)) {
						sqlStr = sqluckyConnector.getExportDDL().exportCreateTrigger(sqluckyConnector.getConn(), fpt.getSchema(),
								fpt.getName());
						if (StrUtils.isNotNullOrEmpty(sqlStr)) {
							sqlStr = SqlFormatter.format(sqlStr);
							fpt.setDdl(sqlStr);
						}
					}
					MyBottomSheet.showDdlSheet(sqluckyConnector, item.getValue().getName(), sqlStr, false, false);

				} // index
				else if (parentItem.getValue().getType() != null
						&& parentItem.getValue().getType() == TreeItemType.INDEX_ROOT) {
					FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
					String sqlStr = fpt.getDdl();
					if (StrUtils.isNullOrEmpty(sqlStr)) {
						sqlStr = sqluckyConnector.getExportDDL().exportCreateIndex(sqluckyConnector.getConn(), fpt.getSchema(),
								fpt.getName());
						if (StrUtils.isNotNullOrEmpty(sqlStr)) {
							sqlStr = SqlFormatter.format(sqlStr);
							fpt.setDdl(sqlStr);
						}
					}

					MyBottomSheet.showDdlSheet(sqluckyConnector, item.getValue().getName(), sqlStr, false, false);

				} // Sequence
				else if (parentItem.getValue().getType() != null
						&& parentItem.getValue().getType() == TreeItemType.SEQUENCE_ROOT) {
					FuncProcTriggerPo fpt = item.getValue().getFuncProTri();
					String sqlStr = fpt.getDdl();
					if (StrUtils.isNullOrEmpty(sqlStr)) {
						sqlStr = sqluckyConnector.getExportDDL().exportCreateSequence(sqluckyConnector.getConn(), fpt.getSchema(),
								fpt.getName());
						if (StrUtils.isNotNullOrEmpty(sqlStr)) {
							sqlStr = SqlFormatter.format(sqlStr);
							fpt.setDdl(sqlStr);
						}
					}

					MyBottomSheet.showDdlSheet(sqluckyConnector, item.getValue().getName(), sqlStr, false, false);
				}
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
				if (newValue == null || DBinfoTreeView == null) {
                    return;
                }

				// 复制节点名称
				var nodeName = newValue.getValue().getName();
				contextMenu.copuNodeName(nodeName);

				// 获取链接的TreeItem
				if (Objects.equals(newValue, DBinfoTreeView.getRoot())) {
					contextMenu.setConnectDisable(true);
					contextMenu.setTableDisable(true);
					contextMenu.setRefreshDisable(true);
					contextMenu.setLinkDisable(true);
				} else if (DBinfoTree.isConns(newValue)) {
					if (newValue.getChildren().isEmpty()) {
						contextMenu.setLinkDisable(false);
						contextMenu.setRefreshDisable(true);
					} else {
						contextMenu.setLinkDisable(true);
						contextMenu.setRefreshDisable(false);
					}
					contextMenu.setConnectDisable(false);
					contextMenu.setTableDisable(true);
				} else if (nd != null && nd.getType() == TreeItemType.TABLE_ROOT) {
					// TABLE 父节点
					contextMenu.setNodeType(null);
					contextMenu.setConnectDisable(true);
					contextMenu.setTableDisable(true);
					contextMenu.setRefreshDisable(false);
					contextMenu.setLinkDisable(true);

					SqluckyConnector dbc = nd.getConnpo();
					contextMenu.setTableRootAction(newValue, dbc);
				} else if (nd != null && nd.getType() == TreeItemType.TABLE) {
					// 表
					contextMenu.setConnectDisable(true);
					contextMenu.setTableDisable(false);
					SqluckyConnector dbc = nd.getConnpo();
					String schema = nd.getTable().getTableSchema();
					String tablename = nd.getTable().getTableName();
					contextMenu.setTableAction(newValue, dbc, schema, tablename);
				} else if (nd != null && nd.getType() == TreeItemType.VIEW) {
					// 试图
					contextMenu.setConnectDisable(true);
					contextMenu.setViewFuncProcTriDisable(false);
					SqluckyConnector dbc = nd.getConnpo();
					String schema = nd.getTable().getTableSchema();
					String viewName = nd.getTable().getTableName();
					contextMenu.setViewAction(newValue, dbc, schema, viewName);
					contextMenu.setSelectMenuDisable(false, dbc, nd);
				} else if (nd != null && nd.getType() == TreeItemType.FUNCTION) {
					// 函数
					contextMenu.setNodeType(TreeItemType.FUNCTION);
					contextMenu.setConnectDisable(true);
					contextMenu.setViewFuncProcTriDisable(false);
					SqluckyConnector dbc = nd.getConnpo();

					String schema = nd.getFuncProTri().getSchema();
					String funcName = nd.getFuncProTri().getName();
					contextMenu.setFuncAction(newValue, dbc, schema, funcName);
				} else if (nd != null && nd.getType() == TreeItemType.PROCEDURE) {
					// 过程
					contextMenu.setNodeType(TreeItemType.PROCEDURE);
					contextMenu.setConnectDisable(true);
					contextMenu.setViewFuncProcTriDisable(false);
					SqluckyConnector dbc = nd.getConnpo();

					String schema = nd.getFuncProTri().getSchema();
					String procName = nd.getFuncProTri().getName();
					contextMenu.setProcAction(newValue, dbc, schema, procName);
				} else if (nd != null && nd.getType() == TreeItemType.TRIGGER) {
					// 触发器
					contextMenu.setNodeType(TreeItemType.TRIGGER);
					contextMenu.setConnectDisable(true);
					contextMenu.setViewFuncProcTriDisable(false);
					SqluckyConnector dbc = nd.getConnpo();

					String schema = nd.getFuncProTri().getSchema();
					String triggerName = nd.getFuncProTri().getName();
					contextMenu.setTriggerAction(newValue, dbc, schema, triggerName);
				} else {
					contextMenu.setNodeType(null);
					contextMenu.setConnectDisable(true);
					contextMenu.setTableDisable(true);
					contextMenu.setRefreshDisable(false);
					contextMenu.setLinkDisable(true);
				}

				if (!contextMenu.getRefresh().isDisable()) {
					TreeItem<TreeNodePo> connItem = connItem(newValue);
					contextMenu.setRefreshAction(connItem);
				}

			}
		};
	}

	private TreeItem<TreeNodePo> connItem(TreeItem<TreeNodePo> newValue) {
		if (DBinfoTree.isConns(newValue)) {
            return newValue;
        }
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
				if (!tnp.getChildren().isEmpty()) {
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

	// 根据连接名称来打开连接
	public static void openConn(String name) {
		TreeItem<TreeNodePo> item = DBinfoTree.getTreeItemByName(name);
		DBinfoTree.openConn(item, false);
	}
	// 静默打开数据库链接
    public static void silentOpenConn(String name) {
        TreeItem<TreeNodePo> item = DBinfoTree.getTreeItemByName(name);
        DBinfoTree.openConn(item, true);
    }
	// 打开连接按钮点击事件
	public static void openDbConn() {
		if (DBinfoTree.currentTreeItemIsConnNode()) {
			TreeItem<TreeNodePo> val = DBinfoTree.getTrewViewCurrentItem();
			if (val == null) {
                return;
            }
			DBinfoTree.openConn(val, false);
		}
	}

	/**
	 * 打开数据库链接
	 * @param item
	 * @param silent 是否静默, 为false, 打开失败会有弹窗警告
	 */
	public static void openConn(TreeItem<TreeNodePo> item, boolean silent) {
		// 判断 节点是否已经有子节点
		if (item.getChildren().isEmpty()) {

			Node nd = IconGenerator.svgImage("spinner", "red");
			RotateTransition rotateTransition = CommonUtils.rotateTransition(nd);
			item.getValue().setIcon(nd);
			AppWindow.treeView.refresh();

			Thread t = new Thread() {
				@Override
				public void run() {
					SqluckyConnector po1 = null;
					try {
						logger.info("backRunOpenConn()");
						String connName = item.getValue().getName();
						SqluckyConnector po = DBConns.get(connName);
						po1 = po;
						po1.setInitConnectionNodeStatus(true);
						var conntmp = po1.getConn();
						if (conntmp != null) {
							ConnItemContainer connItemContainer = new ConnItemContainer(po, item);
							TreeItem<TreeNodePo> subTreeItem = connItemContainer.getDataBasesSchemasRoot();
							Platform.runLater(() -> {
								item.getChildren().add(subTreeItem);
								item.getValue().setIcon(IconGenerator.svgImage("link", "#7CFC00"));
								connItemContainer.selectTable(po.getDefaultSchema());

								// 当 打开连接节点的时候, 放在第一个节点位置(便于查看)
								var itemPatent = item.getParent();
								itemPatent.getChildren().remove(item);
								Platform.runLater(() -> {
									itemPatent.getChildren().addFirst(item);
									AppWindow.treeView.refresh();
									// 下拉选切换打开的连接
									DBConns.selectComboBoxItem(connName);
									item.setExpanded(true);
									subTreeItem.setExpanded(true);
								});


							});
						} else {
							Platform.runLater(() -> {
								if(! silent){
									MyAlert.notification("Error", " Cannot connect ip:" + po.getHostOrFile() + " port:" + po.getPort() + "  !", MyAlert.NotificationType.Error);
								}
								item.getValue().setIcon(IconGenerator.svgImageUnactive("unlink"));
								AppWindow.treeView.refresh();
							});
						}
					} catch (Exception e) {
						logger.debug(e.getMessage());
						Platform.runLater(() -> {
							if(! silent){
								MyAlert.notification("Error", " Error !", MyAlert.NotificationType.Error);
							}
							item.getValue().setIcon(IconGenerator.svgImage("unlink", "red"));
							AppWindow.treeView.refresh();
						});
					} finally {
						DBConns.flushChoiceBoxGraphic();
                        if (po1 != null) {
                            po1.setInitConnectionNodeStatus(false);
                        }
						Platform.runLater(rotateTransition::stop);
                    }
				}
			};
			t.start();
		
		}
	}

	public VBox getDbInfoTreeBtnPane() {
		return dbInfoTreeBtnPane;
	}
}
