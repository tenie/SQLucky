package net.tenie.fx.component.ScriptTree;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.po.SqlcukyTitledPaneInfoPo;
import net.tenie.Sqlucky.sdk.po.component.ConnItemContainer;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.main.SQLucky;
import net.tenie.lib.db.h2.AppDao;

/**
 * 
 * @author tenie
 *
 */
public class ScriptTabTree {

	private static Logger logger = LogManager.getLogger(ScriptTabTree.class);

	public static TreeView<MyEditorSheet> ScriptTreeView;
	public static TreeItem<MyEditorSheet> rootNode;
	List<ConnItemContainer> connItemParent = new ArrayList<>();
	private ScriptTreeContextMenu menu;

	public ScriptTabTree() {
		createScriptTreeView();
	}

	// 节点view
	public TreeView<MyEditorSheet> createScriptTreeView() {
//		rootNode = new TreeItem<>(new MyEditorSheet());
		rootNode = new TreeItem<>(null);
		ComponentGetter.scriptTreeRoot = rootNode;
		ScriptTreeView = new TreeView<>(rootNode);
		ScriptTreeView.getStyleClass().add("my-tag");
		ScriptTreeView.setShowRoot(false);
		// 展示连接
		if (rootNode.getChildren().size() > 0)
			ScriptTreeView.getSelectionModel().select(rootNode.getChildren().get(0)); // 选中节点
		// 双击
		ScriptTreeView.setOnMouseClicked(e -> {
			treeViewDoubleClick(e);
		});
		// 右键菜单
		menu = new ScriptTreeContextMenu(rootNode);
		ContextMenu contextMenu = menu.getContextMenu();
		ScriptTreeView.setContextMenu(contextMenu);
		// 选中监听事件
//		treeView.getSelectionModel().selectedItemProperty().addListener(treeViewContextMenu(treeView));
		ScriptTreeView.getSelectionModel().select(rootNode);

//		ScriptTreeView = treeView;

		// 显示设置
		ScriptTreeView.setCellFactory(new ScriptTabNodeCellFactory());

		// 恢复
		recoverScriptNode();
		return ScriptTreeView;
	}

	// 恢复数据中保存的连接数据
	public static void recoverScriptNode() {
		List<DocumentPo> scriptDatas;
		// 上次的激活页面
		Tab activateMyTab = null;
		// 从系统中打开 .sql文件时, 大概这个sql的编辑页面
		Tab sysOpenFileTB = null;

		Connection H2conn = SqluckyAppDB.getConn();
		try {
			// 读取 上次左侧script tree中的所有数据
			scriptDatas = AppDao.readScriptPo(H2conn);
		} finally {
			SqluckyAppDB.closeConn(H2conn);
		}
		//
		List<TreeItem<MyEditorSheet>> treeItems = new ArrayList<>();
		List<MyEditorSheet> myEditorSheets = new ArrayList<>(); // myAreaTabs
		// 将DocumentPo 对象转换位tree的节点对象
		if (scriptDatas != null && scriptDatas.size() > 0) {
			ConfigVal.pageSize = scriptDatas.size();
			for (DocumentPo po : scriptDatas) {
//				MyEditorSheet tb = new MyEditorSheet(po, true);
				MyEditorSheet myEditorSheet = MyEditorSheetHelper.createHighLightingEditor(po);// new MyEditorSheet(po);
				TreeItem<MyEditorSheet> item = new TreeItem<>(myEditorSheet);
				treeItems.add(item);
				// 将需要恢复代码编辑框, 缓存到集合中
				if (po.getOpenStatus() != null && po.getOpenStatus() == 1) {
					myEditorSheets.add(myEditorSheet);
					// 设置上次激活的编辑页面
					if (po.getIsActivate() == 1) {
						activateMyTab = myEditorSheet.getTab();
					}

					// 在操作系统中通过鼠标双击打开的文件, 如果再在以前打开过就直接选中
					if (StrUtils.isNotNullOrEmpty(SQLucky.sysOpenFile)) {
						var filePath = po.getFileFullName();
						if (StrUtils.isNotNullOrEmpty(filePath)) {
							if (SQLucky.sysOpenFile.equals(filePath)) {
								sysOpenFileTB = myEditorSheet.getTab();
								logger.info("**** filePath = " + filePath);
							}
						}
					}

				}
			}
		}

		Tab tmpSysOpenFileTB = sysOpenFileTB;
		Tab activateTmpMyTab = activateMyTab;
		// 页面显示后 执行下吗
		Consumer<String> cr = v -> {
			if (treeItems.size() > 0) {
				Platform.runLater(() -> {
					rootNode.getChildren().addAll(treeItems);
					// 恢复代码编辑框
					if (myEditorSheets.size() > 0) {
						MyEditorSheetHelper.mainTabPaneAddAllMyTabs(myEditorSheets);

						// 系统打开文件触发启动APP时, 恢复历史中的文件
						if (tmpSysOpenFileTB != null) {
							logger.info("系统打开文件触发启动APP时, 恢复历史中的文件 ");
							ComponentGetter.mainTabPane.getSelectionModel().select(tmpSysOpenFileTB);
						} else if (StrUtils.isNotNullOrEmpty(SQLucky.sysOpenFile)) { // 系统打开文件触发启动APP时, 新开一个 脚本文件
							logger.info("系统打开文件触发启动APP时, 新开一个 脚本文件 ");
							File sif = new File(SQLucky.sysOpenFile);
							CommonAction.openSqlFile(sif);
						} else if (activateTmpMyTab != null) {// 恢复选中上次选中页面
							logger.info(" 恢复选中上次选中页面");
							ComponentGetter.mainTabPane.getSelectionModel().select(activateTmpMyTab);
						}
					}
					// 没有tab被添加, 添加一新的
					if (myEditorSheets.size() == 0) {
//						MyAreaTab.addCodeEmptyTabMethod();
						MyEditorSheetHelper.addEmptyHighLightingEditor();
					}
				});
			} else {
				Platform.runLater(() -> {
//					MyAreaTab.addCodeEmptyTabMethod();
					MyEditorSheetHelper.addEmptyHighLightingEditor();
				});
			}

		};
		CommonUtils.addInitTask(cr);
	}

	// 使用外部数据还原script tree节点
	public static void recoverFromDocumentPos(List<DocumentPo> scriptDatas) {
		Tab activateMyTab = null;
		List<TreeItem<MyEditorSheet>> treeItems = new ArrayList<>();
		List<MyEditorSheet> myAreaTabs = new ArrayList<>();
		// 将DocumentPo 对象转换位tree的节点对象
		if (scriptDatas != null && scriptDatas.size() > 0) {
			ConfigVal.pageSize = scriptDatas.size();
			for (DocumentPo po : scriptDatas) {
				// 使用外部数据, 还原将数据保存到数据库,
				// 只要确保DocumentPo id为null, new MyAreaTab时会保存到数据库
				po.setId(null);
				MyEditorSheet tb = MyEditorSheetHelper.createHighLightingEditor(po);// new MyEditorSheet(po, true);
				TreeItem<MyEditorSheet> item = new TreeItem<>(tb);
				treeItems.add(item);
				// 将需要恢复代码编辑框, 缓存到集合中
				if (po.getOpenStatus() != null && po.getOpenStatus() == 1) {
					myAreaTabs.add(tb);
					// 设置上次激活的编辑页面
					if (po.getIsActivate() == 1) {
						activateMyTab = tb.getTab();
					}
				}

			}
		}

		Tab activateTmpMyTab = activateMyTab;
		// 页面显示后 执行下吗
		if (treeItems.size() > 0) {
			Platform.runLater(() -> {
				rootNode.getChildren().addAll(treeItems);
				// 恢复代码编辑框
				if (myAreaTabs.size() > 0) {
//					MyAreaTab.mainTabPaneAddAllMyTabs(myAreaTabs);
					MyEditorSheetHelper.mainTabPaneAddAllMyTabs(myAreaTabs);
					if (activateTmpMyTab != null) {// 恢复选中上次选中页面
						logger.info(" 恢复选中上次选中页面");
						ComponentGetter.mainTabPane.getSelectionModel().select(activateTmpMyTab);
					}
				}
				// 没有tab被添加, 添加一新的
				if (myAreaTabs.size() == 0) {
//					MyAreaTab.addCodeEmptyTabMethod();
					MyEditorSheetHelper.addEmptyHighLightingEditor();
				}
			});
		} else {
			Platform.runLater(() -> {
//				MyAreaTab.addCodeEmptyTabMethod();
				MyEditorSheetHelper.addEmptyHighLightingEditor();
			});
		}

	}

	// 使用外部数据还原script tree节点, 清空节点和 清空tabpane打开的编辑tab
	public static void cleanOldAndRecover(List<DocumentPo> scriptDatas) {
		rootNode.getChildren().clear();
		var myTabPane = ComponentGetter.mainTabPane;
		myTabPane.getTabs().clear();

		recoverFromDocumentPos(scriptDatas);
	}

	// 所有连接节点
	public static ObservableList<TreeItem<MyEditorSheet>> allTreeItem() {
		ObservableList<TreeItem<MyEditorSheet>> val = ScriptTreeView.getRoot().getChildren();
		return val;
	}

	// 获取当前选中的节点
	public static TreeItem<MyEditorSheet> getScriptViewCurrentItem() {
		TreeItem<MyEditorSheet> ctt = ScriptTreeView.getSelectionModel().getSelectedItem();
		return ctt;
	}

	// 给root节点加元素
	public static void treeRootAddItem(TreeItem<MyEditorSheet> item) {
		TreeItem<MyEditorSheet> rootNode = ScriptTreeView.getRoot();
		rootNode.getChildren().add(item);
	}

	// 给root节点加元素
	public static void treeRootAddItem(MyEditorSheet mytab) {
		TreeItem<MyEditorSheet> item = new TreeItem<>(mytab);
		treeRootAddItem(item);
	}

	// tree view 双击事件
	public void treeViewDoubleClick(MouseEvent mouseEvent) {
		if (mouseEvent.getClickCount() == 2) {
			openEditor();
		}
	}

	public static void openEditor() {
		TreeItem<MyEditorSheet> item = ScriptTreeView.getSelectionModel().getSelectedItem();
		MyEditorSheet sheet = item.getValue();
		if (sheet != null && sheet.getDocumentPo() != null) {
			sheet.showEditor();
		}
	}

	public static List<DocumentPo> allScriptPo() {
		ObservableList<TreeItem<MyEditorSheet>> ls = allTreeItem();
		List<DocumentPo> list = new ArrayList<>();
		for (var ti : ls) {
			var mytb = ti.getValue();
			list.add(mytb.getDocumentPo());
		}

		return list;
	}

	public static List<MyEditorSheet> allMyTab() {
		ObservableList<TreeItem<MyEditorSheet>> ls = allTreeItem();
		List<MyEditorSheet> list = new ArrayList<>();
		for (var ti : ls) {
			var mytb = ti.getValue();
			list.add(mytb);
		}
		return list;
	}

	public static MyEditorSheet findMyTabByScriptPo(DocumentPo scpo) {
		ObservableList<TreeItem<MyEditorSheet>> ls = allTreeItem();
		for (var ti : ls) {
			var mytb = ti.getValue();
			var tmp = mytb.getDocumentPo();
			if (tmp.equals(scpo)) {
				return mytb;
			}

		}
		return null;
	}

	// 关闭一个脚本 Node cell
	public static void closeAction(TreeItem<MyEditorSheet> rootNode) {

		ObservableList<TreeItem<MyEditorSheet>> myTabItemList = rootNode.getChildren();
		TreeItem<MyEditorSheet> ctt = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItem();
		MyEditorSheet sheet = ctt.getValue();

		String title = sheet.getTitle();// CommonUtility.tabText(tb);
		String sql = sheet.getAreaText(); // SqlEditor.getTabSQLText(tb);
		if (title.endsWith("*") && sql != null && sql.trim().length() > 0) {
			// 是否保存
			final Stage stage = new Stage();

			// 1 保存
			JFXButton okbtn = new JFXButton("Yes(Y)");
			okbtn.getStyleClass().add("myAlertBtn");
			okbtn.setOnAction(value -> {
//				文件保存到磁盘
				MyEditorSheetHelper.saveSqlAction(sheet);
				removeNode(myTabItemList, ctt, sheet);
				sheet.destroySheet();
				stage.close();
			});

			// 2 不保存
			JFXButton Nobtn = new JFXButton("No(N)");
			Nobtn.setOnAction(value -> {
				removeNode(myTabItemList, ctt, sheet);
				sheet.destroySheet();
				stage.close();
			});
			// 取消
			JFXButton cancelbtn = new JFXButton("Cancel(C)");
			cancelbtn.setOnAction(value -> {
				stage.close();
			});

			List<Node> btns = new ArrayList<>();

			btns.add(cancelbtn);
			btns.add(Nobtn);
			btns.add(okbtn);

			MyAlert.myConfirmation("Save " + StrUtils.trimRightChar(title, "*") + "?", stage, btns, false);
		} else {
			removeNode(myTabItemList, ctt, sheet);
		}

	}

	// 从ScriptTabTree 中移除一个节点
	public static void removeNode(ObservableList<TreeItem<MyEditorSheet>> myTabItemList, TreeItem<MyEditorSheet> ctt,
			MyEditorSheet tb) {
		var conn = SqluckyAppDB.getConn();
		try {
			var myTabPane = ComponentGetter.mainTabPane;
			if (myTabPane.getTabs().contains(tb)) {
				myTabPane.getTabs().remove(tb);
			}
			myTabItemList.remove(ctt);

			var scpo = tb.getDocumentPo();
			AppDao.deleteScriptArchive(conn, scpo);

		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}

	// TitledPane
	public TitledPane scriptTitledPane() {

		ScriptTreeButtonPanel sbtnPanel = new ScriptTreeButtonPanel();

		TitledPane scriptTitledPane = new TitledPane();
		scriptTitledPane.setText("Script");
		scriptTitledPane.setUserData(new SqlcukyTitledPaneInfoPo("Script", sbtnPanel.getOptionHbox()));

		CommonUtils.addCssClass(scriptTitledPane, "titledPane-color");
		scriptTitledPane.setContent(ScriptTreeView);

		// 图标切换
		CommonUtils.addInitTask(v -> {
			Platform.runLater(() -> {
				CommonUtils.setLeftPaneIcon(scriptTitledPane, ComponentGetter.iconScript, ComponentGetter.uaIconScript);
			});

		});

		return scriptTitledPane;
	}

	// treeView 右键菜单属性设置
//		public   ChangeListener<TreeItem<MyTab>> treeViewContextMenu(TreeView<MyTab> treeView) {
//			return new ChangeListener<TreeItem<MyTab>>() {
//				@Override
//				public void changed(ObservableValue<? extends TreeItem<MyTab>> observable,
//						TreeItem<MyTab> oldValue, TreeItem<MyTab> newValue) {
//					
//				 
//					 
//					
//					
////					if(! menu.getRefresh().isDisable()) {
////						TreeItem<TreeNodePo>  connItem = ConnItem(newValue);
////						menu.setRefreshAction(connItem);
////					}
//
//				}
//			};
//		}

}
