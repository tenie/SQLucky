package net.tenie.plugin.note.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.LoadingAnimation;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.note.component.NoteOptionPanel;
import net.tenie.plugin.note.component.NoteTabTree;
import net.tenie.plugin.note.impl.NoteDelegateImpl;

public class NoteUtility {

	public static TreeItem<SqluckyTab> rootCache = null;
	public static boolean isFile = false;
	public static boolean isText = false;

	
	 public static void doubleClickItem(TreeItem<SqluckyTab> item) {
		 	SqluckyTab stb = item.getValue();
			File file = stb.getFile();
			if(! file.exists()) return;
			if(file.isFile()) {
				if(StrUtils.isNotNullOrEmpty(file.getAbsolutePath() ) ){
//					String fp = file.getAbsolutePath().toLowerCase();
//					if(fp.endsWith(".md") 
//					   || fp.endsWith(".text") 
//					   || fp.endsWith(".sql") 
//					   || fp.endsWith(".txt") 
//				    )  

					String charset = FileTools.detectFileCharset(file);
					if( charset != null ){
						boolean isExist = stb.existTabShow(); 
						if(! isExist) {
							String  val = CommonUtility.readFileText(file, charset);
							stb.setFileText(val);
							stb.mainTabPaneAddMyTab();
						}
					}else {
						CommonUtility.openExplorer(file);
					}
					
				}else {
					CommonUtility.openExplorer(file);
				}
				
			}else if(file.isDirectory()) {
				if(item.getChildren().size() == 0) {
					NoteUtility.openNoteDir(item, file);
					item.setExpanded(true);
				}
				
			}
	 }
	
	// 在系统的目录里打开
	public static void showInSystem(TreeView<SqluckyTab> NoteTabTreeView) {

		TreeItem<SqluckyTab> ctt = NoteTabTreeView.getSelectionModel().getSelectedItem();
		SqluckyTab tb = ctt.getValue();
		try {
			String fn = tb.getDocumentPo().getFileFullName();
			if (StrUtils.isNotNullOrEmpty(fn)) {
				File file = new File(fn);
				CommonUtility.openExplorer(file.getParentFile());
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	// 获取选中的treeItem
	public static TreeItem<SqluckyTab> currentTreeItem() {
		TreeItem<SqluckyTab> ctt = NoteTabTree.noteTabTreeView.getSelectionModel().getSelectedItem();
		return ctt;
	}

	// 获取选中的treeItem中的Tab
	public static SqluckyTab currentTreeItemSqluckyTab() {
		SqluckyTab val = currentTreeItem().getValue();
		return val;
	}

	public static File currentTreeItemFile() {
		SqluckyTab val = currentTreeItem().getValue();
		File file = val.getFile();
		return file;
	}

	// 重新载入
	public static void refreshAction(TreeView<SqluckyTab> NoteTabTreeView, TreeItem<SqluckyTab> rootNode,
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
	public static String cleanAction(TreeItem<SqluckyTab> rootNode) {
		NoteOptionPanel.txt.clear();
		rootNode.getChildren().clear();
		String filePath = "";
		ComponentGetter.appComponent.saveData(NoteDelegateImpl.pluginName, "dir_path", "");
		return filePath;
	}

	// delete file
	public static void deleteFile(TreeView<SqluckyTab> NoteTabTreeView) {
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
		MyAlert.myConfirmation("Delete  " + fileTyep + ": " + file.getAbsolutePath() + " ? ", stage, btns);
	}

	// 新建一个文件
	public static void newFile(TreeView<SqluckyTab> NoteTabTreeView, TreeItem<SqluckyTab> rootNode, String filePath) {
		var itm = NoteTabTreeView.getSelectionModel().getSelectedItem();
		var ParentNode = itm.getParent();
		if (Objects.equals(rootNode, ParentNode)) {
			newFileNode(rootNode, filePath);
		} else {
			var parentFile = ParentNode.getValue().getFile();
			newFileNode(ParentNode, parentFile.getAbsolutePath());
		}
	}

	public static void newFileNode(TreeItem<SqluckyTab> node, String fpval) {
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
	public static String openFolder(TreeItem<SqluckyTab> rootNode) {
		String filePath = "";
		File f = FileOrDirectoryChooser.showDirChooser("Select Directory", ComponentGetter.primaryStage);
		if (f != null && f.exists()) {
			filePath = f.getAbsolutePath();
			if (rootNode.getChildren().size() >= 0) {
				rootNode.getChildren().clear();
			}
			openNoteDir(rootNode, f);
			ComponentGetter.appComponent.saveData(NoteDelegateImpl.pluginName, "dir_path", filePath);

		}

		return filePath;

	}

	// TODO 打开sql文件
	public static void openNoteDir(TreeItem<SqluckyTab> node, File openFile) {
		try {
			Consumer<String> caller = x -> {
				File[] files = openFile.listFiles();
				if (files == null)
					return;

				TreeItem<SqluckyTab> fileRootitem = node;
				// 如果选择目录导入进来的情况
				if (node.equals(NoteTabTree.rootNode)) {
					fileRootitem = createItemNode(openFile);
					node.getChildren().add(fileRootitem);
				}

				TreeItem<SqluckyTab> tmpItem = fileRootitem;
				List<TreeItem<SqluckyTab>> ls = new ArrayList<>();
				for (var file : files) {
					TreeItem<SqluckyTab> item = createItemNode(file);
					if (item != null) {
						ls.add(item);
					}

				}
				if (ls.size() > 0) {
					Platform.runLater(() -> {
						tmpItem.getChildren().addAll(ls);
						tmpItem.setExpanded(true);
					});
				}
			};

			CommonUtility.runThread(caller);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建一个TreeItem
	 * 
	 * @param node
	 * @param file
	 * @return
	 */
	public static TreeItem<SqluckyTab> createItemNode(File file) {
		if (file.exists()) {
			DocumentPo fileNode = new DocumentPo();
			fileNode.setFileFullName(file.getAbsolutePath());
			fileNode.setTitle(file.getName());
			SqluckyTab mtb = ComponentGetter.appComponent.sqluckyTab(fileNode);

			mtb.setFile(file);
			Region icon;
			if (file.isFile()) {
				icon = ComponentGetter.appComponent.getIconDefActive("file-text-o");

			} else {
				icon = ComponentGetter.appComponent.getIconDefActive("folder");

			}
			mtb.setIcon(icon);

			TreeItem<SqluckyTab> item = new TreeItem<>(mtb);
			return item;
		}
		return null;

	}

	// 搜索
	public static void searchAction(String queryStr, String fileType) {
		if (StrUtils.isNullOrEmpty(queryStr)) {
			return;
		} else {
			queryStr = queryStr.toLowerCase();
		}
		var windowSceneRoot = ComponentGetter.primarySceneRoot; 
		LoadingAnimation.addLoading(windowSceneRoot, "Search....");
		
		String searchStr = queryStr;
		CommonUtility.runThread(v->{
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

					SqluckyTab stab = ComponentGetter.appComponent.sqluckyTab();
					TreeItem<SqluckyTab> tmpRoot = new TreeItem<>(stab);
					List<File> files = FileTools.getAllFileFromDir(file);
					Platform.runLater(()->{
						NoteTabTree.noteTabTreeView.setRoot(tmpRoot);
					});
					if (isFile) { // 文件名搜索
						for (var tmpfile : files) {
							LoadingAnimation.ChangeLabelText("Search : " + tmpfile.getName());
							String fileName = tmpfile.getName().toLowerCase();
							if (fileName.contains(searchStr)) {
								TreeItem<SqluckyTab> fileRootitem = NoteUtility.createItemNode(tmpfile);
								Platform.runLater(()->{
									tmpRoot.getChildren().add(fileRootitem);
								});
								
							}
						}
					}else if(isText) { // 文件内容搜索
						for (var tmpfile : files) {
							if(fileType.endsWith("*")) { // 如果结尾是* , 就规避二进制文件
								if(FileTools.isBinaryFile(tmpfile)) {
									continue ;
								}
							} 
							boolean match = StrUtils.matchFileName(fileType, tmpfile);
							if(match) {
								LoadingAnimation.ChangeLabelText("Search : " + tmpfile.getName());
								String charset = FileTools.detectFileCharset(tmpfile);
								if(charset != null ) {
									String textStr = FileTools.read(tmpfile, charset);
									if(textStr.contains(searchStr)) {
										TreeItem<SqluckyTab> fileRootitem = NoteUtility.createItemNode(tmpfile);
									
										Platform.runLater(()->{
											tmpRoot.getChildren().add(fileRootitem);
										});
									}
								}
							}
							
						}
					}
				}
			}
			NoteTabTree.noteTabTreeView.getSelectionModel().select(0);
			NoteTabTree.noteTabTreeView.refresh(); 
			LoadingAnimation.rmLoading(windowSceneRoot);
		});
	}
	/**
	 * 上下切换搜索的文件
	 */
	public static void downBtnChange() {
		var currentItem = currentTreeItem();
		if(currentItem == null) {
			var ls = NoteTabTree.noteTabTreeView.getRoot().getChildren();
			if(ls.size() > 0) {
				NoteTabTree.noteTabTreeView.getRoot().getChildren().get(0);
				NoteUtility.doubleClickItem( currentItem);
			}
			
		}else {	
			var ls = currentItem.getParent().getChildren();
			int idx  = ls.indexOf(currentItem);
			int next = idx + 1;
			if(ls.size() == next) {
				next = 0;
			}
			var nextItem = ls.get(next);
			NoteUtility.doubleClickItem( nextItem);
			NoteTabTree.noteTabTreeView.getSelectionModel().select(next);
		}
		
	}
	
}
