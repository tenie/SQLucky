package net.tenie.plugin.note.utility;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
//import net.tenie.Sqlucky.sdk.component.FindReplaceTextPanel;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.component.editor.FindReplaceTextBox;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.note.component.MyNoteEditorSheet;
import net.tenie.plugin.note.component.NoteOptionPanel;
import net.tenie.plugin.note.component.NoteTabTree;
import net.tenie.plugin.note.impl.NoteDelegateImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteUtility {

	public static void doubleClickItem(TreeItem<MyNoteEditorSheet> item) {
		if(item == null) {
			return;
		}
		MyNoteEditorSheet sheet = item.getValue();
		File file = sheet.getFile();
		if (!file.exists()) {
            return;
        }
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
						var myTabPane = ComponentGetter.mainTabPane;
						sheet.showEditor(myTabPane);
					}
				} else {
					CommonUtils.openExplorer(file);
				}

			} else {
				CommonUtils.openExplorer(file);
			}

		} else if (file.isDirectory()) {
			if (item.getChildren().size() == 0) {
				NoteUtility.openNoteDir(item, file, false);
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
				String fn = tb.getDocumentPo().getExistFileFullName();
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
				NoteUtility.openNoteDir(ParentNode, new File(filePath), false);
			}

		} else {
			if (ParentNode.getChildren().size() > 0) {
				ParentNode.getChildren().clear();
			}
			var parentFile = ParentNode.getValue().getFile();
			NoteUtility.openNoteDir(ParentNode, parentFile, false);
		}

	}

	// 关闭界面上打开的note目录
	public static void cleanAction(TreeItem<MyNoteEditorSheet> rootNode) {
		NoteOptionPanel.txt.clear();
		rootNode.getChildren().clear();
		ComponentGetter.appComponent.deletePluginAllData(NoteDelegateImpl.pluginName);
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
//		var itm = NoteTabTreeView.getSelectionModel().getSelectedItem();
//		var ParentNode = itm.getParent();
//		if (Objects.equals(rootNode, ParentNode)) {
//			newFileNode(rootNode, filePath);
//		} else {
//			var parentFile = ParentNode.getValue().getFile();
//			newFileNode(ParentNode, parentFile.getAbsolutePath());
//		}

		File file = FileOrDirectoryChooser.showSaveText("Save", "", ComponentGetter.primaryStage);
		if(file != null){
			System.out.println(file.getAbsolutePath());
            try {
				if(!file.getName().contains(".")){
					file = new File(file.getAbsolutePath()+".txt");
				}
				if(!file.exists()){
					file.createNewFile();
				}
				openFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//
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
			NoteUtility.openNoteDir(node, new File(fpval), false);
		}
	}

	// 打开目录
	public static String openFolder(TreeItem<MyNoteEditorSheet> rootNode) {
		String filePath = "";
		File f = FileOrDirectoryChooser.showDirChooser("Select Directory", ComponentGetter.primaryStage);
		if (f != null && f.exists()) {
			List<TreeItem<MyNoteEditorSheet>> children = rootNode.getChildren();
			for(var  treeItem: children){
				String fPath = treeItem.getValue().getFile().getAbsolutePath();
				if(fPath.equals(f.getAbsolutePath())){
					treeItem.setExpanded(true);
					return "";
				}
			}

			TreeItem<MyNoteEditorSheet>  rootItem = openNoteDir(rootNode, f, false);
			savePath(rootItem);
		}

		return filePath;

	}

	/**
	 * treeView 中插入一个文件节点
	 * @return
	 */
	public static String openFile() {
		File f = FileOrDirectoryChooser.showOpenAllFile("Select File", ComponentGetter.primaryStage);
		return	openFile(f);
	}

	/**
	 * treeView 中插入一个文件节点
	 * @param f
	 * @return
	 */
	public static String openFile(File f) {
		String filePath = "";
		if (f != null && f.exists()) {
			filePath = f.getAbsolutePath();
			List<TreeItem<MyNoteEditorSheet>>  children = NoteTabTree.noteTabTreeView.getRoot().getChildren();
			for(var  treeItem: children){
				String fPath = treeItem.getValue().getFile().getAbsolutePath();
				if(fPath.equals(f.getAbsolutePath())){
					NoteTabTree.noteTabTreeView.getSelectionModel().select(treeItem);
					return "";
				}
			}
			TreeItem<MyNoteEditorSheet>  treeItem = openNoteFile(f);
			savePath(treeItem);
		}
		return filePath;
	}

	/**
	 * 保存多个笔记的目录数
	 */
	public static void savePath(TreeItem<MyNoteEditorSheet> rootItem) {
		MyNoteEditorSheet sheet = rootItem.getValue();
		File file =  sheet.getFile();
		String filePath = file.getAbsolutePath();

		ComponentGetter.appComponent.saveData(NoteDelegateImpl.pluginName, filePath, filePath);

	}

	/**
	 * 移除保存的路径
	 * @param rootItem
	 */
	public static void rmSavePath(TreeItem<MyNoteEditorSheet> rootItem) {
		String filePath = rootItem.getValue().getFile().getAbsolutePath();
		ComponentGetter.appComponent.deleteData(NoteDelegateImpl.pluginName, filePath);
	}

	// 打开sql文件
    public static TreeItem<MyNoteEditorSheet> openNoteDir(TreeItem<MyNoteEditorSheet> node, File openFile,
        boolean needHideFile) {
        TreeItem<MyNoteEditorSheet> rootItem = null;
        // if(openFile.isDirectory()){
        List<File> AllList = new ArrayList<>();
		File[] dirList = openFile.listFiles(File::isDirectory);
		File[] fileList = openFile.listFiles(File::isFile);
        if (dirList != null) {
            AllList.addAll(List.of(dirList));
        }
        if (fileList != null) {
            AllList.addAll(List.of(fileList));
        }

        if(AllList.isEmpty()){
			return null;
		}

        TreeItem<MyNoteEditorSheet> fileRootitem = node;
        // 如果选择目录导入进来的情况
        if (node.equals(NoteTabTree.rootNode)) {
            fileRootitem = createItemNode(openFile);
            if (fileRootitem != null) {
                fileRootitem.getValue().setIsRootItem(true, fileRootitem);
                rootItem = fileRootitem;
                node.getChildren().add(fileRootitem);
            }

        }

        TreeItem<MyNoteEditorSheet> tmpItem = fileRootitem;
        List<TreeItem<MyNoteEditorSheet>> ls = new ArrayList<>();
        for (var file : AllList) {
            if (!needHideFile && file.getName().startsWith(".")) {
                continue;
            }
            TreeItem<MyNoteEditorSheet> item = createItemNode(file);
            if (item != null) {
                ls.add(item);
            }

        }
        if (!ls.isEmpty()) {
            Platform.runLater(() -> {
                if (tmpItem != null) {
                    tmpItem.getChildren().addAll(ls);
                    tmpItem.setExpanded(true);
                    NoteTabTree.noteTabTreeView.getSelectionModel().select(tmpItem);
                }

            });
        }
        // } else {
        // TreeItem<MyNoteEditorSheet> fileRootitem = node;
        // // 如果选择目录导入进来的情况
        // if (node.equals(NoteTabTree.rootNode)) {
        // fileRootitem = createItemNode(openFile);
        // node.getChildren().add(fileRootitem);
        // }
        // }

        return rootItem;
    }

	// 打开sql文件
	public static TreeItem<MyNoteEditorSheet>  openNoteFile(File openFile) {
		TreeItem<MyNoteEditorSheet>  fileRootitem = createItemNode(openFile);
		MyNoteEditorSheet myNote = fileRootitem.getValue();
		myNote.setIsRootItem(true, fileRootitem);
		NoteTabTree.rootNode.getChildren().add(fileRootitem);
		return fileRootitem;
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

	public static void openNextNote(ObservableList<TreeItem<MyNoteEditorSheet>> ls, int next) {
		if (ls != null && next < ls.size()) {
			var nextItem = ls.get(next);
			NoteUtility.doubleClickItem(nextItem);
			NoteTabTree.noteTabTreeView.getSelectionModel().select(next);
		}
	}

	/**
	 * 上下切换搜索的文件
	 */
	public static void downUpBtnChange(boolean isUp, String txt) {
		TreeItem<MyNoteEditorSheet> currentItem = currentTreeItem();
		if (currentItem == null) {
			var ls = NoteTabTree.noteTabTreeView.getRoot().getChildren();
			if (!ls.isEmpty()) {
				currentItem = NoteTabTree.noteTabTreeView.getRoot().getChildren().getFirst();
			}
		}
		if (currentItem == null) {
			return;
		}
		// 查找开始位置设置, 默认从头开始
		int startIdx = 0;
		if (isUp) {
			startIdx = -1;
		}

		// 判断当前节点是否打开着
		MyNoteEditorSheet myNoteEditorSheet = currentItem.getValue();
		boolean isfind = false;  // 是否找到
		// 如果 sheet 是选中的情况下(文件已经打开的状态)
		if (myNoteEditorSheet.isSelecting()) {
			MyCodeArea myCodeArea = myNoteEditorSheet.getSqluckyEditor().getCodeArea();
			// 判断查找面板是否已经打开
			if( myCodeArea.findIsShowing()){
				// 已经打开就不指定查找的开始位置(从当前位置开始查找)
				isfind = myCodeArea.getFindReplaceTextBox().findStringStopFromCodeArea(txt, null , !isUp, false);
			}else {
				// 先打开查找面包
				FindReplaceTextBox findReplaceTextBox = myCodeArea.showFindReplaceTextBox(false, txt);
				// 向下找(从0下标位置)开始找, 向上找重尾部开始向上找
				isfind = findReplaceTextBox.findStringStopFromCodeArea(txt, startIdx, !isUp, false);
			}

			// 判断是否找到, 没找到切换到下一个文件(打开下一个sheet)
			if (!isfind && myNoteEditorSheet.isSelecting()) {
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
				// 打开下一个文件
				openNextNote(ls, next);
				// 调用查找
				downUpBtnChange(isUp, txt);
			}

		} else {// 没有打开文件的情况, 先打开文件再查找
			NoteUtility.doubleClickItem(currentItem);
			NoteTabTree.noteTabTreeView.getSelectionModel().select(currentItem);

			// 展示之后开始查找
			Platform.runLater(() -> {
				int startIdx2 = 0;
				if (isUp) {
					startIdx2 = -1;
				}
				MyCodeArea myCodeArea = myNoteEditorSheet.getSqluckyEditor().getCodeArea();
				if( myCodeArea.findIsShowing()){
					myCodeArea.getFindReplaceTextBox().findStringStopFromCodeArea(txt, null , !isUp, false);
				}else {
					FindReplaceTextBox findReplaceTextBox = myCodeArea.showFindReplaceTextBox(false, txt);
					findReplaceTextBox.findStringStopFromCodeArea(txt, startIdx2, !isUp, false);
				}
			});

		}

	}

}
