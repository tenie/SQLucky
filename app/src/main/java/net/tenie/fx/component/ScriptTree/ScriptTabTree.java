package net.tenie.fx.component.ScriptTree;

import SQLucky.app;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.component.SqluckyTitledPane;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.AppCommonAction;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.lib.db.h2.AppDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 
 * @author tenie
 *
 */
public class ScriptTabTree extends SqluckyTitledPane {
	private static final Logger logger = LogManager.getLogger(ScriptTabTree.class);

	public static TreeView<MyEditorSheet> ScriptTreeView;
	public static TreeItem<MyEditorSheet> rootNode;

    public ScriptTabTree() {
		createScriptTreeView();
		var btnsBox = new ScriptTreeButtonPanel();
		this.setBtnsBox(btnsBox);
		this.setText("Script");

		CommonUtils.addCssClass(this, "titledPane-color");
		this.setContent(ScriptTreeView);
	}

	// 节点view
	public void createScriptTreeView() {
		rootNode = new TreeItem<>(null);
		ComponentGetter.scriptTreeRoot = rootNode;
		ScriptTreeView = new TreeView<>(rootNode);
		ScriptTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		ComponentGetter.scriptTreeView = ScriptTreeView;
		ScriptTreeView.getStyleClass().add("my-tag");
		ScriptTreeView.setShowRoot(false);
		// 展示连接
		if (!rootNode.getChildren().isEmpty()) {
            ScriptTreeView.getSelectionModel().select(rootNode.getChildren().getFirst()); // 选中节点
        }
		// 双击
		ScriptTreeView.setOnMouseClicked(e -> openEditor());
		// 右键菜单
        ScriptTreeContextMenu menu = new ScriptTreeContextMenu(rootNode);
		ContextMenu contextMenu = menu.getContextMenu();
		ScriptTreeView.setContextMenu(contextMenu);
		ScriptTreeView.getSelectionModel().select(rootNode);
		// 显示设置
		ScriptTreeView.setCellFactory(new ScriptTabNodeCellFactory());

	}

	// 恢复数据中保存的连接数据
	public static void recoverScriptNode() {
		List<DocumentPo> scriptDatas = null;
		// 上次的激活页面
		boolean activateMyTab = false;
		// 从系统中打开 .sql文件时, 大概这个sql的编辑页面
		Tab sysOpenFileTB = null;
		Connection H2conn = SqluckyAppDB.getConn();
		try {
			// 读取 上次左侧script tree中的所有数据
            if (H2conn != null) {
                scriptDatas = AppDao.readScriptPo(H2conn);
            }
        } finally {
			SqluckyAppDB.closeConn(H2conn);
		}
		//
		List<TreeItem<MyEditorSheet>> treeItems = new ArrayList<>();
		List<MyEditorSheet> myEditorSheets = new ArrayList<>();
		// 将DocumentPo 对象转换位tree的节点对象
		if (scriptDatas != null && !scriptDatas.isEmpty()) {
			ConfigVal.pageSize = scriptDatas.size();
			for (DocumentPo po : scriptDatas) {
				MyEditorSheet myEditorSheet = MyEditorSheetHelper.createHighLightingEditor(po);
				TreeItem<MyEditorSheet> item = myEditorSheet.getTreeItem();

				treeItems.add(item);
				// 将需要恢复代码编辑框, 缓存到集合中
				if (po.getOpenStatus() != null && po.getOpenStatus() == 1) {
					myEditorSheets.add(myEditorSheet);
					// 有上次激活的编辑页面
					if (po.getIsActivate() == 1) {
						activateMyTab = true;
					}

					// 在操作系统中通过鼠标双击打开的文件, 如果再在以前打开过就直接选中
					if (StrUtils.isNotNullOrEmpty(app.sysOpenFile)) {
						var filePath = po.getExistFileFullName();
						if (StrUtils.isNotNullOrEmpty(filePath)) {
							if (app.sysOpenFile.equals(filePath)) {
								sysOpenFileTB = myEditorSheet;
                                logger.info("**** filePath = {}", filePath);
							}
						}
					}
				}
			}
		}

		Tab tmpSysOpenFileTB = sysOpenFileTB;
		boolean activateMyTabTmp = activateMyTab;
		if (!treeItems.isEmpty()) {
			Platform.runLater(() -> {
				rootNode.getChildren().addAll(treeItems);
				// 恢复代码编辑框
				if (!myEditorSheets.isEmpty()) {
					Consumer<String> activateCall = MyEditorSheetHelper.mainTabPaneAddAllMyTabs(myEditorSheets);

					// 系统打开文件触发启动APP时, 恢复历史中的文件
					if (tmpSysOpenFileTB != null) {
						logger.info("系统打开文件触发启动APP时, 恢复历史中的文件 ");
						ComponentGetter.mainTabPane.getSelectionModel().select(tmpSysOpenFileTB);
					} else if (StrUtils.isNotNullOrEmpty(app.sysOpenFile)) { // 系统打开文件触发启动APP时, 新开一个 脚本文件
						logger.info("系统打开文件触发启动APP时, 新开一个 脚本文件 ");
						File sif = new File(app.sysOpenFile);
						AppCommonAction.openSqlFile(sif);
					} else if (activateMyTabTmp) {
						// 恢复选中上次选中页面
						logger.info(" 恢复选中上次选中页面1");
						activateCall.accept("");
					}
				}
				// 没有tab被添加, 添加一新的
				if (myEditorSheets.isEmpty()) {
					MyEditorSheetHelper.addEmptyHighLightingEditor();
				}
			});
		} else {
			Platform.runLater(MyEditorSheetHelper::addEmptyHighLightingEditor);
		}
	}

	// 使用外部数据还原script tree节点
	public static void recoverFromDocumentPos(List<DocumentPo> scriptDatas) {
		Tab activateMyTab = null;
		List<TreeItem<MyEditorSheet>> treeItems = new ArrayList<>();
		List<MyEditorSheet> myAreaTabs = new ArrayList<>();
		// 将DocumentPo 对象转换位tree的节点对象
		if (scriptDatas != null && !scriptDatas.isEmpty()) {
			ConfigVal.pageSize = scriptDatas.size();
			for (DocumentPo po : scriptDatas) {
				// 使用外部数据, 还原将数据保存到数据库,
				// 只要确保DocumentPo id为null, new MyAreaTab时会保存到数据库
				po.setId(null);
				MyEditorSheet tb = MyEditorSheetHelper.createHighLightingEditor(po);
				TreeItem<MyEditorSheet> item = tb.getTreeItem();
				treeItems.add(item);
				// 将需要恢复代码编辑框, 缓存到集合中
				if (po.getOpenStatus() != null && po.getOpenStatus() == 1) {
					myAreaTabs.add(tb);
					// 设置上次激活的编辑页面
					if (po.getIsActivate() == 1) {
						activateMyTab = tb;
					}
				}
			}
		}

		Tab activateTmpMyTab = activateMyTab;
		// 页面显示后 执行下吗
		if (!treeItems.isEmpty()) {
			Platform.runLater(() -> {
				rootNode.getChildren().addAll(treeItems);
				// 恢复代码编辑框
				if (!myAreaTabs.isEmpty()) {
					MyEditorSheetHelper.mainTabPaneAddAllMyTabs(myAreaTabs);
					if (activateTmpMyTab != null) {// 恢复选中上次选中页面
						logger.info(" 恢复选中上次选中页面");
						ComponentGetter.mainTabPane.getSelectionModel().select(activateTmpMyTab);
					}
				}
				// 没有tab被添加, 添加一新的
				if (myAreaTabs.isEmpty()) {
					MyEditorSheetHelper.addEmptyHighLightingEditor();
				}
			});
		} else {
			Platform.runLater(MyEditorSheetHelper::addEmptyHighLightingEditor);
		}
	}

	// 使用外部数据还原script tree节点, 清空节点和 清空tabpane打开的编辑tab
	public static void cleanOldAndRecover(List<DocumentPo> scriptDatas) {
		rootNode.getChildren().clear();
		var myTabPane = ComponentGetter.mainTabPane;
		myTabPane.getTabs().clear();
		var rightTabPane = ComponentGetter.rightTabPane;
		rightTabPane.getTabs().clear();

		recoverFromDocumentPos(scriptDatas);
	}

	// 所有连接节点
	public static ObservableList<TreeItem<MyEditorSheet>> allTreeItem() {
        return ScriptTreeView.getRoot().getChildren();
	}

	// 给root节点加元素
	public static void treeRootAddItem(TreeItem<MyEditorSheet> item) {
		TreeItem<MyEditorSheet> rootNode = ScriptTreeView.getRoot();
		rootNode.getChildren().add(item);
	}

	// 给root节点加元素
	public static void treeRootAddItem(MyEditorSheet mytab) {
		TreeItem<MyEditorSheet> item = mytab.getTreeItem();
		treeRootAddItem(item);
	}

	// 显示脚本tab
	public static void openEditor() {
		List<TreeItem<MyEditorSheet>>  selectedItems = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItems();
		if(selectedItems != null && !selectedItems.isEmpty()) {
			for (var item : selectedItems) {
				MyEditorSheet sheet = item.getValue();
				if (sheet != null && sheet.getDocumentPo() != null) {
					int tabPosition = sheet.getDocumentPo().getTabPosition();
					var myTabPane = ComponentGetter.mainTabPane;
					var rightTabPane = ComponentGetter.rightTabPane;
					if(0 == tabPosition){
						sheet.showEditor(myTabPane);
					}else {
						sheet.showEditor(rightTabPane);
					}
				}
			}
		}
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
		List<TreeItem<MyEditorSheet>>  selectedItems = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItems();
		if(selectedItems != null && !selectedItems.isEmpty()){
			for(var ctt: selectedItems){
				MyEditorSheet sheet = ctt.getValue();

				String title = sheet.getTitle();
				String sql = sheet.getAreaText();
				if (title.endsWith("*") && sql != null && !sql.trim().isEmpty()) {
					// 是否保存
					final Stage stage = new Stage();

					// 1 保存 文件保存到磁盘
					JFXButton okbtn = new JFXButton("Yes(Y)");
					okbtn.getStyleClass().add("myAlertOkBtn");
					okbtn.setOnAction(value -> {
						removeTreeNode(myTabItemList, ctt, sheet);
						sheet.saveDiskAndDestroyTab();
						stage.close();
					});

					// 2 不保存
					JFXButton Nobtn = new JFXButton("No(N)");
					Nobtn.getStyleClass().add("myAlertBtn");

					Nobtn.setOnAction(value -> {
						removeTreeNode(myTabItemList, ctt, sheet);
						sheet.closeTab();
						stage.close();
					});
					// 取消
					JFXButton cancelbtn = new JFXButton("Cancel(C)");
					cancelbtn.getStyleClass().add("myAlertBtn");
					cancelbtn.setOnAction(value -> stage.close());

					List<Node> btns = new ArrayList<>();
					btns.add(cancelbtn);
					btns.add(Nobtn);
					btns.add(okbtn);
					MyAlert.myConfirmation("Save " + StrUtils.trimRightChar(title, "*") + "?", stage, btns, false);
				} else {
					removeTreeNode(myTabItemList, ctt, sheet);
				}
			}
		}
	}

	// 从ScriptTabTree 中移除一个节点
	public static void removeTreeNode(ObservableList<TreeItem<MyEditorSheet>> myTabItemList, TreeItem<MyEditorSheet> ctt,
									  MyEditorSheet tb) {
		var myTabPane = ComponentGetter.mainTabPane;
		var rightTabPane = ComponentGetter.rightTabPane;
		if (myTabPane.getTabs().contains(tb)) {
			myTabPane.getTabs().remove(tb);
		} else {
			rightTabPane.getTabs().remove(tb);
		}

		myTabItemList.remove(ctt);
		var scPo = tb.getDocumentPo();

		var conn = SqluckyAppDB.getConn();
		try {
			if (conn != null) {
				AppDao.deleteScriptArchive(conn, scPo);
			}
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}
}
