package net.tenie.plugin.note.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.FindReplaceTextPanel;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.note.component.MyNoteEditorSheet;
import net.tenie.plugin.note.component.NoteOptionPanel;
import net.tenie.plugin.note.component.NoteTabTree;
import net.tenie.plugin.note.impl.NoteDelegateImpl;

public class NoteUtility {

	public static TreeItem<MyNoteEditorSheet> rootCache = null;
	public static boolean isFile = false;
	public static boolean isText = false;

	public static void doubleClickItem(TreeItem<MyNoteEditorSheet> item) {
		MyNoteEditorSheet sheet = item.getValue();
		File file = sheet.getFile();
		if (!file.exists())
			return;
		if (file.isFile()) {
			if (StrUtils.isNotNullOrEmpty(file.getAbsolutePath())) {
				String charset = FileTools.detectFileCharset(file);
				if (charset != null) {
					boolean isExist = sheet.existTabShow();
					if (!isExist) {
						String val = CommonUtils.readFileText(file, charset);
						sheet.setFileText(val);
						if(sheet.getSqluckyEditor()!= null && sheet.getSqluckyEditor().getCodeArea() != null){
							sheet.getSqluckyEditor().getCodeArea().clear();
							sheet.getSqluckyEditor().getCodeArea().insertText(0, val);
						}

						sheet.showEditor();
					}
				} else {
					CommonUtils.openExplorer(file);
				}

			} else {
				CommonUtils.openExplorer(file);
			}

		} else if (file.isDirectory()) {
			if (item.getChildren().size() == 0) {
				NoteUtility.openNoteDir(item, file);
				item.setExpanded(true);
			}

		}
	}

	// 在系统的目录里打开
	public static void showInSystem(TreeView<MyNoteEditorSheet> NoteTabTreeView) {

		TreeItem<MyNoteEditorSheet> ctt = NoteTabTreeView.getSelectionModel().getSelectedItem();
		MyNoteEditorSheet tb = ctt.getValue();
		try {
			if (tb.getDocumentPo() != null) {
				String fn = tb.getDocumentPo().getFileFullName();
				if (StrUtils.isNotNullOrEmpty(fn)) {
					File file = new File(fn);
					CommonUtils.openExplorer(file.getParentFile());
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	// 获取选中的treeItem
	public static TreeItem<MyNoteEditorSheet> currentTreeItem() {
		TreeItem<MyNoteEditorSheet> ctt = NoteTabTree.noteTabTreeView.getSelectionModel().getSelectedItem();
		return ctt;
	}

	// 获取选中的treeItem中的Tab中的file
	public static File currentTreeItemFile() {
		var cit = currentTreeItem();
		if (cit != null) {
			MyNoteEditorSheet val = currentTreeItem().getValue();
			File file = val.getFile();
			return file;
		}
		return null;
	}

	// 重新载入
	public static void refreshAction(TreeView<MyNoteEditorSheet> NoteTabTreeView, TreeItem<MyNoteEditorSheet> rootNode,
			String filePath) {
		var itm = NoteTabTreeView.getSelectionModel().getSelectedItem();
		var ParentNode = itm.getParent();
		if (Objects.equals(rootNode, ParentNode)) {
			if (rootNode.getChildren().size() > 0) {
				rootNode.getChildren().clear();
			}
			if (StrUtils.isNotNullOrEmpty(filePath)) {
				NoteUtility.openNoteDir(ParentNode, new File(filePath));
			}

		} else {
			if (ParentNode.getChildren().size() > 0) {
				ParentNode.getChildren().clear();
			}
			var parentFile = ParentNode.getValue().getFile();
			NoteUtility.openNoteDir(ParentNode, parentFile);
		}

	}

	// 关闭界面上打开的note目录
	public static String cleanAction(TreeItem<MyNoteEditorSheet> rootNode) {
		NoteOptionPanel.txt.clear();
		rootNode.getChildren().clear();
		String filePath = "";
		ComponentGetter.appComponent.saveData(NoteDelegateImpl.pluginName, "dir_path", "");
		return filePath;
	}

	// delete file
	public static void deleteFile(TreeView<MyNoteEditorSheet> NoteTabTreeView) {
		var itm = NoteTabTreeView.getSelectionModel().getSelectedItem();
		File file = itm.getValue().getFile();
		String fileTyep = "File";
		if (file.isDirectory()) {
			fileTyep = "Folder";
		}

		List<Node> btns = new ArrayList<>();
		final Stage stage = new Stage();
		JFXButton okbtn = new JFXButton("Yes");
		okbtn.getStyleClass().add("myAlertBtn");
		okbtn.setOnAction(value -> {
			file.delete();
			var pa = itm.getParent();
			pa.getChildren().remove(itm);
			stage.close();
		});

		// 取消
		JFXButton cancelbtn = new JFXButton("Cancel");
		cancelbtn.setOnAction(value -> {
			stage.close();
		});

		btns.add(cancelbtn);
		btns.add(okbtn);
		MyAlert.myConfirmation("Delete  " + fileTyep + ": " + file.getAbsolutePath() + " ? ", stage, btns, false);
	}

	// 新建一个文件
	public static void newFile(TreeView<MyNoteEditorSheet> NoteTabTreeView, TreeItem<MyNoteEditorSheet> rootNode,
			String filePath) {
		var itm = NoteTabTreeView.getSelectionModel().getSelectedItem();
		var ParentNode = itm.getParent();
		if (Objects.equals(rootNode, ParentNode)) {
			newFileNode(rootNode, filePath);
		} else {
			var parentFile = ParentNode.getValue().getFile();
			newFileNode(ParentNode, parentFile.getAbsolutePath());
		}
	}

	public static void newFileNode(TreeItem<MyNoteEditorSheet> node, String fpval) {
		FileChooser fc = FileOrDirectoryChooser.getFileChooser("New ", "new ", new File(fpval));
		File file = fc.showSaveDialog(ComponentGetter.primaryStage);
		try {
			FileTools.save(file, "");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (file.getAbsolutePath().startsWith(fpval)) {
			if (node.getChildren().size() > 0) {
				node.getChildren().clear();
			}
			NoteUtility.openNoteDir(node, new File(fpval));
		}
	}

	// 打开目录
	public static String openFolder(TreeItem<MyNoteEditorSheet> rootNode) {
		String filePath = "";
		File f = FileOrDirectoryChooser.showDirChooser("Select Directory", ComponentGetter.primaryStage);
		if (f != null && f.exists()) {
			filePath = f.getAbsolutePath();
			openNoteDir(rootNode, f);

			savePath(filePath);
		}

		return filePath;

	}

	// 打开文件
	public static String openFile() {
		String filePath = "";
		File f = FileOrDirectoryChooser.showOpenAllFile("Select File", ComponentGetter.primaryStage);
		if (f != null && f.exists()) {
			filePath = f.getAbsolutePath();
			openNoteFile(f);
			savePath(filePath);
		}

		return filePath;

	}

	/**
	 * 保存多个笔记的目录数
	 */
	public static void savePath(String filePath) {
		String count = ComponentGetter.appComponent.fetchData(NoteDelegateImpl.pluginName, "path_count");
		Integer countInt = 0;
		if (StrUtils.isNotNullOrEmpty(count)) {
			countInt = Integer.valueOf(count);
		}
		countInt += 1;
		ComponentGetter.appComponent.saveData(NoteDelegateImpl.pluginName, "dir_path_" + countInt, filePath);
		ComponentGetter.appComponent.saveData(NoteDelegateImpl.pluginName, "path_count", countInt+"");
	}

	/**
	 * 获取所以的note目录
	 * @return
	 */
	public static List<String> fetchAllPath() {
		String count = ComponentGetter.appComponent.fetchData(NoteDelegateImpl.pluginName, "path_count");
		Integer countInt = 0;
		List<String> pathList = new ArrayList<>();
		if (StrUtils.isNotNullOrEmpty(count)) {
			countInt = Integer.valueOf(count);
			for (int i = 0 ; i <= countInt ; i++){
				String pathVal = ComponentGetter.appComponent.fetchData(NoteDelegateImpl.pluginName, "dir_path_" + i);
				pathList.add(pathVal);
			}
		}
		return pathList;
	}

	// 打开sql文件
	public static void openNoteDir(TreeItem<MyNoteEditorSheet> node, File openFile) {
		if(openFile.isDirectory()){
			List<File> fileList = new ArrayList<>();
			File[] files = openFile.listFiles();
			if (files == null){
				return;
			}else {
				fileList.addAll(List.of(files));
			}
			TreeItem<MyNoteEditorSheet> fileRootitem = node;
			// 如果选择目录导入进来的情况
			if (node.equals(NoteTabTree.rootNode)) {
				fileRootitem = createItemNode(openFile);
				node.getChildren().add(fileRootitem);
			}

			TreeItem<MyNoteEditorSheet> tmpItem = fileRootitem;
			List<TreeItem<MyNoteEditorSheet>> ls = new ArrayList<>();
			for (var file : fileList) {
				TreeItem<MyNoteEditorSheet> item = createItemNode(file);
				if (item != null) {
					ls.add(item);
				}

			}
			if (ls.size() > 0) {
				Platform.runLater(() -> {
					tmpItem.getChildren().addAll(ls);
					tmpItem.setExpanded(true);
					NoteTabTree.noteTabTreeView.getSelectionModel().select(tmpItem);
				});
			}
		}else {
			TreeItem<MyNoteEditorSheet> fileRootitem = node;
			// 如果选择目录导入进来的情况
			if (node.equals(NoteTabTree.rootNode)) {
				fileRootitem = createItemNode(openFile);
				node.getChildren().add(fileRootitem);
			}
		}
	}

	// 打开sql文件
	public static void openNoteFile(File openFile) {
		var fileRootitem = createItemNode(openFile);
		NoteTabTree.rootNode.getChildren().add(fileRootitem);
	}

	/**
	 * 创建一个TreeItem
	 *
	 * @param file
	 * @return
	 */
	public static TreeItem<MyNoteEditorSheet> createItemNode(File file) {
		if (file.exists()) {
			DocumentPo fileNode = new DocumentPo(false);
			fileNode.setFileFullName(file.getAbsolutePath());
			fileNode.setTitle(file.getName());
			if (file.getAbsolutePath().toLowerCase().endsWith(".sql")) {
				fileNode.setType(DocumentPo.IS_SQL);
			} else {
				fileNode.setType(DocumentPo.IS_TEXT);
			}

			MyNoteEditorSheet mtb = new MyNoteEditorSheet(fileNode, null);// ComponentGetter.appComponent.MyNoteEditorSheet(fileNode);

			mtb.setFile(file);
			Region icon;
			if (file.isFile()) {
				icon = IconGenerator.svgImage("file-text-o", "#C1C1C3 ");
			} else {
				icon = IconGenerator.svgImage("folder", "#ACBDE8 ");
			}
			mtb.setIcon(icon);

			TreeItem<MyNoteEditorSheet> item = new TreeItem<>(mtb);
			return item;
		}
		return null;

	}

	// 搜索
	public static void searchAction(String queryStr, String fileType, Button down, Button up, Button stopbtn) {
		if (StrUtils.isNullOrEmpty(queryStr)) {
			return;
		} else {
			queryStr = queryStr.toLowerCase();
		}
		var windowSceneRoot = NoteTabTree.noteStackPane;
		LoadingAnimation.addLoading(windowSceneRoot, "Search....", 14);

		String searchStr = queryStr;
		CommonUtils.runThread(v -> {
			try {

				File selectfile = NoteUtility.currentTreeItemFile();
				List<File> searchDirs = new ArrayList<>();
				if (selectfile == null) {
					var nodels = NoteTabTree.rootNode.getChildren();
					for (var subNd : nodels) {
						var tmpfile = subNd.getValue().getFile();
						searchDirs.add(tmpfile);
					}

				} else {
					searchDirs.add(selectfile);
				}
				for (var file : searchDirs) {
					if (file.isDirectory()) {
						rootCache = NoteTabTree.rootNode;

						MyNoteEditorSheet stab = new MyNoteEditorSheet("", null);// ComponentGetter.appComponent.MyNoteEditorSheet();
						TreeItem<MyNoteEditorSheet> tmpRoot = new TreeItem<>(stab);
						Platform.runLater(() -> {
							NoteTabTree.noteTabTreeView.setRoot(tmpRoot);
						});

						Function<File, Boolean> caller = tmpfile -> {
							if (isStopSearch()) {
								return true;
							}
							if (isFile) { // 文件名搜索
								LoadingAnimation.ChangeLabelText("Search: \n" + tmpfile.getName());
								String fileName = tmpfile.getName().toLowerCase();
								if (fileName.contains(searchStr)) {
									TreeItem<MyNoteEditorSheet> fileRootitem = NoteUtility.createItemNode(tmpfile);
									Platform.runLater(() -> {
										tmpRoot.getChildren().add(fileRootitem);
									});

								}

							} else if (isText) { // 文件内容搜索
								if (fileType.endsWith("*")) { // 如果结尾是* , 就规避二进制文件
									if (FileTools.isBinaryFile(tmpfile)) {
										return isStopSearch();
									}
								}
								boolean match = StrUtils.matchFileName(fileType, tmpfile);
								if (match) {
									LoadingAnimation.ChangeLabelText("Search: \n" + tmpfile.getName());
									String charset = FileTools.detectFileCharset(tmpfile);
									if (charset != null) {
										String textStr = FileTools.read(tmpfile, charset);
										if (textStr.toLowerCase().contains(searchStr)) {
											TreeItem<MyNoteEditorSheet> fileRootitem = NoteUtility.createItemNode(tmpfile);

											Platform.runLater(() -> {
												tmpRoot.getChildren().add(fileRootitem);
												down.setDisable(false);
												up.setDisable(false);
											});
										}
									}
								}

							}
							return isStopSearch();
						};

						FileTools.getAllFileFromDir(file, caller);

					}
				}
			} finally {
				NoteTabTree.noteTabTreeView.getSelectionModel().select(0);
				NoteTabTree.noteTabTreeView.refresh();
				LoadingAnimation.rmLoading(windowSceneRoot);
				down.setDisable(false);
				up.setDisable(false);
				stopbtn.setDisable(true);
			}

		});
	}

	private static volatile boolean stopTag = false;

	public static boolean getStopTag() {
		return stopTag;
	}

	public static void beginSearch() {
		stopTag = false;
	}

	public static void stopSearch() {
		stopTag = true;
	}

	public static boolean isStopSearch() {
		return getStopTag();
	}

	public static MyNoteEditorSheet openNextNote(ObservableList<TreeItem<MyNoteEditorSheet>> ls, int next) {
		if (ls != null && next < ls.size()) {
			var nextItem = ls.get(next);
			NoteUtility.doubleClickItem(nextItem);
			NoteTabTree.noteTabTreeView.getSelectionModel().select(next);
			return nextItem.getValue();
		}
		return null;
	}

	/**
	 * 上下切换搜索的文件
	 */
	public static void downUpBtnChange(boolean isUp, String txt) {
		TreeItem<MyNoteEditorSheet> currentItem = currentTreeItem();
		if (currentItem == null) {
			var ls = NoteTabTree.noteTabTreeView.getRoot().getChildren();
			if (ls.size() > 0) {
				currentItem = NoteTabTree.noteTabTreeView.getRoot().getChildren().get(0);
			}
		}
		if (currentItem == null) {
			return;
		}

		int startIdx = 0;
		if (isUp) {
			startIdx = -1;
		}

		// 判断当前节点是否大开着
		MyNoteEditorSheet currentSktb = currentItem.getValue();
		boolean isfind = false;
		if (currentSktb.isShowing()) {
			FindReplaceTextPanel fpanel = currentSktb.getFindReplacePanel();
			if (fpanel == null) {
				CommonUtils.findReplace(false, txt, currentSktb);
				fpanel = currentSktb.getFindReplacePanel();
				isfind = fpanel.findStringStopFromCodeArea(txt, startIdx, !isUp, false);
			} else {
				isfind = fpanel.findStringStopFromCodeArea(txt, null, !isUp, false);
			}

			if (!isfind && currentSktb.isShowing()) {
				// 获取所有搜索到的文件节点
				var ls = currentItem.getParent().getChildren();
				int idx = ls.indexOf(currentItem);
				int next;
				if (isUp) {
					next = idx - 1;
					if (next < 0) {
						next = ls.size() - 1;
					}
				} else {
					next = idx + 1;
					if (ls.size() == next) {
						next = 0;
					}

				}

				MyNoteEditorSheet skTab = openNextNote(ls, next);
			}

		} else {
			// 没有展示的情况, 先展示再查找
			NoteUtility.doubleClickItem(currentItem);
			NoteTabTree.noteTabTreeView.getSelectionModel().select(currentItem);

			// 展示之后开始查找
			Platform.runLater(() -> {
				int startIdx2 = 0;
				if (isUp) {
					startIdx2 = -1;
				}
				FindReplaceTextPanel fpanel = currentSktb.getFindReplacePanel();
				if (fpanel == null) {
					CommonUtils.findReplace(false, txt, currentSktb);
					fpanel = currentSktb.getFindReplacePanel();
					fpanel.findStringStopFromCodeArea(txt, startIdx2, !isUp, false);
				} else {
					fpanel.findStringStopFromCodeArea(txt, null, !isUp, false);
				}
			});

		}

	}

}
