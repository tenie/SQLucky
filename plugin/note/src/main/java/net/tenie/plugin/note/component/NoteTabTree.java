package net.tenie.plugin.note.component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.jfoenix.controls.JFXButton;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.note.impl.NoteDelegateImpl;
import net.tenie.plugin.note.utility.NoteUtility;

/**
 * 
 * @author tenie
 *
 */
public class NoteTabTree {

	public static StackPane noteStackPane = new StackPane();
	public static TreeView<MyEditorSheet> noteTabTreeView;
	public static TreeItem<MyEditorSheet> rootNode;
	public static String filePath = "";
	private static VBox optionBox;
	private static NoteOptionPanel optPane;

	public NoteTabTree() {

		optPane = new NoteOptionPanel();
		noteTabTreeView = createScriptTreeView();
		optionBox = optPane.getOptionVbox();
		noteStackPane.getChildren().add(noteTabTreeView);
		noteStackPane.getStyleClass().add("myStackPane");

	}

	// db节点view
	public TreeView<MyEditorSheet> createScriptTreeView() {
//		MyEditorSheet sheet = ComponentGetter.appComponent.sqluckyTab();
		rootNode = new TreeItem<>(null);
		TreeView<MyEditorSheet> treeView = new TreeView<>(rootNode);
		treeView.getStyleClass().add("my-tag");
		treeView.setShowRoot(false);
		// 展示连接
		if (rootNode.getChildren().size() > 0)
			treeView.getSelectionModel().select(rootNode.getChildren().get(0)); // 选中节点
		// 右键菜单
		ContextMenu contextMenu = createContextMenu();
		treeView.setContextMenu(contextMenu);
		// 选中监听事件
		treeView.getSelectionModel().select(rootNode);

		// cell显示设置(图标），cell 双击设置
		treeView.setCellFactory(new NoteTabNodeCellFactory(optPane, noteTabTreeView));

		recoverNode(rootNode);
		return treeView;
	}

//	// 恢复数据中保存的连接数据
	public void recoverNode(TreeItem<MyEditorSheet> rootNode) {

		Consumer<String> cr = v -> {
			filePath = ComponentGetter.appComponent.fetchData(NoteDelegateImpl.pluginName, "dir_path");
			File file = new File(filePath);
			if (file.exists()) {
				NoteUtility.openNoteDir(rootNode, file);
			}
		};
		CommonUtils.addInitTask(cr);

	}

	// 所有连接节点
	public static ObservableList<TreeItem<MyEditorSheet>> allTreeItem() {
		ObservableList<TreeItem<MyEditorSheet>> val = noteTabTreeView.getRoot().getChildren();
		return val;
	}

	// 获取当前选中的节点
	public static TreeItem<MyEditorSheet> getScriptViewCurrentItem() {
		TreeItem<MyEditorSheet> ctt = noteTabTreeView.getSelectionModel().getSelectedItem();
		return ctt;
	}

	// 给root节点加元素
	public static void treeRootAddItem(TreeItem<MyEditorSheet> item) {
		TreeItem<MyEditorSheet> rootNode = noteTabTreeView.getRoot();
		rootNode.getChildren().add(item);
	}

	// 给root节点加元素
	public static void treeRootAddItem(MyEditorSheet mytab) {
		TreeItem<MyEditorSheet> item = new TreeItem<MyEditorSheet>(mytab);
		treeRootAddItem(item);
	}

	public static void openMyTab() {
		TreeItem<MyEditorSheet> item = noteTabTreeView.getSelectionModel().getSelectedItem();
		var mytab = item.getValue();
		if (mytab != null && mytab.getDocumentPo() != null) {
			mytab.showMyTab();
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

	public static void closeNodeConfirmation(TreeItem<MyEditorSheet> treeitem) {
		MyEditorSheet stb = treeitem.getValue();
		var parentNode = treeitem.getParent();
		if (stb.isModify()) {
			// 是否保存
			final Stage stage = new Stage();
			// 1 保存
			JFXButton okbtn = new JFXButton("Yes");
			okbtn.getStyleClass().add("myAlertBtn");
			okbtn.setOnAction(value -> {
//				stb.saveTextAction();
				MyEditorSheetHelper.saveSqlAction(stb);
				removeItem(parentNode, treeitem);
				stage.close();
			});

			// 2 不保存
			JFXButton Nobtn = new JFXButton("No");
			Nobtn.setOnAction(value -> {
				removeItem(parentNode, treeitem);
				stage.close();
			});
			// 取消
			JFXButton cancelbtn = new JFXButton("Cancel");
			cancelbtn.setOnAction(value -> {
				stage.close();
			});

			List<Node> btns = new ArrayList<>();
			btns.add(cancelbtn);
			btns.add(Nobtn);
			btns.add(okbtn);
			MyAlert.myConfirmation("Save " + StrUtils.trimRightChar(stb.getTitle(), "*") + "?", stage, btns, false);

		} else {
			removeItem(parentNode, treeitem);
		}
	}

	public static void removeItem(TreeItem<MyEditorSheet> nodeItem, TreeItem<MyEditorSheet> subItem) {
		MyEditorSheet stb = subItem.getValue();
		nodeItem.getChildren().remove(subItem);
		ComponentGetter.appComponent.tabPaneRemoveSqluckyTab(stb);
	}

	// 关闭一个脚本 Node cell
	public static void closeAction(TreeItem<MyEditorSheet> node) {

		MyEditorSheet stb = node.getValue();
		File nodeFile = stb.getFile();
		// 文件: 判断是否需要保存修改
		if (nodeFile.isFile()) {
			closeNodeConfirmation(node);
		}
		// 目录: 没有打开过直接关闭
		else if (nodeFile.isDirectory()) {
			// 没有展开的目录, 直接移除
			if (node.getChildren().size() == 0) {
				rootNode.getChildren().remove(node);
			} else {
				// 展开的目录, 递归关闭子项
				for (var subNode : node.getChildren()) {
					closeAction(subNode);
//					node.getChildren().remove(subNode);
				}
				rootNode.getChildren().remove(node);
			}
		}

	}

	// 菜单
	public ContextMenu createContextMenu() {

		ContextMenu contextMenu = new ContextMenu();

		MenuItem Clean = new MenuItem("Clean ");
		Clean.setOnAction(e -> {
			NoteTabTree.filePath = NoteUtility.cleanAction(NoteTabTree.rootNode);
		});

		MenuItem newFile = new MenuItem("New File ");
		newFile.setOnAction(e -> {
			NoteUtility.newFile(noteTabTreeView, rootNode, filePath);
		});

		// 打开目录
		MenuItem Open = new MenuItem("Import note Folder");
		Open.setOnAction(e -> {
			filePath = NoteUtility.openFolder(rootNode);
		});

		MenuItem deleteFile = new MenuItem("Delete File");
		deleteFile.setOnAction(e -> {
			NoteUtility.deleteFile(NoteTabTree.noteTabTreeView);
		});

		MenuItem showInFolder = new MenuItem("Show In Folder");
		showInFolder.setOnAction(e -> {
			NoteUtility.showInSystem(NoteTabTree.noteTabTreeView);
		});

		contextMenu.getItems().addAll(Open,
//				close,
				new SeparatorMenuItem(), newFile, deleteFile,
//				Refresh,
				Clean, new SeparatorMenuItem(), showInFolder);
		contextMenu.setOnShowing(e -> {
			var itm = noteTabTreeView.getSelectionModel().getSelectedItem();

			if (itm != null && itm.getValue() != null && itm.getValue().getFile() != null
					&& itm.getValue().getFile().isFile()) {
				deleteFile.setDisable(false);
			} else {
				deleteFile.setDisable(true);
			}

			if (itm != null && itm.getValue() != null && itm.getValue().getFile() != null) {
				showInFolder.setDisable(false);
			} else {
				showInFolder.setDisable(true);
			}

		});
		return contextMenu;
	}

	public static VBox getOptionBox() {
		return optionBox;
	}

}
