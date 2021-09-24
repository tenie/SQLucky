package net.tenie.plugin.note.component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.SaveFile;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.note.impl.NoteDelegateImpl;


/**
 * 
 * @author tenie
 *
 */
public class NoteTabTree {

	public static TreeView<SqluckyTab> NoteTabTreeView;
//	private ContextMenu  menu;
	private static TreeItem<SqluckyTab> rootNode;
	String filePath = "";
	 
	
	
	public NoteTabTree() {
		 createScriptTreeView();
	}

	// db节点view
	public TreeView<SqluckyTab> createScriptTreeView() {
		SqluckyTab stab = ComponentGetter.appComponent.sqluckyTab();
		rootNode = new TreeItem<>(stab);
		TreeView<SqluckyTab> treeView = new TreeView<>(rootNode);
		treeView.getStyleClass().add("my-tag");
		treeView.setShowRoot(false); 
		// 展示连接
		if (rootNode.getChildren().size() > 0)
			treeView.getSelectionModel().select(rootNode.getChildren().get(0)); // 选中节点
		// 双击
//		treeView.setOnMouseClicked(e -> {
//			treeViewDoubleClick(e);
//		});
		// 右键菜单
		ContextMenu contextMenu = createContextMenu();
		treeView.setContextMenu(contextMenu);
		// 选中监听事件
//		treeView.getSelectionModel().selectedItemProperty().addListener(treeViewContextMenu(treeView));
		treeView.getSelectionModel().select(rootNode);

		NoteTabTreeView = treeView;

		// 显示设置
		treeView.setCellFactory(new NoteTabNodeCellFactory());
		 
		filePath = ComponentGetter.appComponent.fetchData(NoteDelegateImpl.pluginName, "dir_path");
		
		recoverNode(rootNode, filePath);
		return treeView;
	}
	
	
//	// 恢复数据中保存的连接数据
	public static void recoverNode(TreeItem<SqluckyTab> rootNode, String filePath) { 
		File file = new File(filePath);
		if(file.exists()) {
			openNoteDir(rootNode , file);
		}
		
	}

	// 所有连接节点
	public static ObservableList<TreeItem<SqluckyTab>> allTreeItem() {
		ObservableList<TreeItem<SqluckyTab>> val = NoteTabTreeView.getRoot().getChildren();
		return val;
	}

	   

	// 获取当前选中的节点
	public static TreeItem<SqluckyTab> getScriptViewCurrentItem() {
		TreeItem<SqluckyTab> ctt = NoteTabTreeView.getSelectionModel().getSelectedItem();
		return ctt;
	}

	 

	// 给root节点加元素 
	public static void treeRootAddItem(TreeItem<SqluckyTab> item) { 
		TreeItem<SqluckyTab> rootNode = NoteTabTreeView.getRoot();
		rootNode.getChildren().add(item);		
	}
	// 给root节点加元素 
		public static void treeRootAddItem(SqluckyTab  mytab) {
			TreeItem<SqluckyTab> item = new TreeItem<SqluckyTab> (mytab); 
			treeRootAddItem(item);
		}

	// tree view 双击事件
//	public void treeViewDoubleClick(MouseEvent mouseEvent) { 
//		if (mouseEvent.getClickCount() == 2) {
//			openMyTab();
//		}
//	}
	
	public static void openMyTab() {
		TreeItem<SqluckyTab> item = NoteTabTreeView.getSelectionModel().getSelectedItem();
		var mytab = item.getValue(); 
		if(mytab != null && mytab.getDocumentPo() != null) {
			mytab.mainTabPaneAddMyTab();
		}
	}
	 
	public static  List<DocumentPo>  allScriptPo() {
		 ObservableList<TreeItem<SqluckyTab>> ls = allTreeItem();
		 List<DocumentPo> list = new ArrayList<>();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 list.add(mytb.getDocumentPo());
		 }
		 
		 return list;
	}
	public static  List<SqluckyTab>  allMyTab() {
		 ObservableList<TreeItem<SqluckyTab>> ls = allTreeItem();
		 List<SqluckyTab> list = new ArrayList<>();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 list.add(mytb);
		 } 
		 return list;
	}

	
	public static SqluckyTab findMyTabByScriptPo(DocumentPo scpo) {
		 ObservableList<TreeItem<SqluckyTab>> ls = allTreeItem();
		 for(var ti: ls) {
			 var mytb = ti.getValue();
			 var tmp =  mytb.getDocumentPo();
			 if(tmp.equals(scpo)) {
				 return mytb;
			 }
					 
		 } 
		 return null;
	}
	
	public static void closeNodeConfirmation( TreeItem<SqluckyTab> treeitem) {
		SqluckyTab stb = treeitem.getValue();
		var parentNode = treeitem.getParent();
		if(stb.isModify() ) {
			// 是否保存
			final Stage stage = new Stage(); 
			// 1 保存
			JFXButton okbtn = new JFXButton("Yes");
			okbtn.getStyleClass().add("myAlertBtn");
			okbtn.setOnAction(value -> { 
				stb.saveTextAction();
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
			MyAlert.myConfirmation("Save " + StrUtils.trimRightChar(stb.getTitle(), "*") + "?", stage, btns);
			
		}else {
			removeItem(parentNode, treeitem);
		}
	}
	
	public static void removeItem(TreeItem<SqluckyTab> nodeItem, TreeItem<SqluckyTab> subItem) {
		var stb = subItem.getValue();
		nodeItem.getChildren().remove(subItem);
		ComponentGetter.appComponent.tabPaneRemoveSqluckyTab(stb);
	}
	
	// 关闭一个脚本 Node cell
	public static void closeAction(TreeItem<SqluckyTab> node) {
		
		SqluckyTab stb = node.getValue();
		File nodeFile = stb.getFile();
		// 文件: 判断是否需要保存修改
		if(nodeFile.isFile()) { 
			closeNodeConfirmation(node);
		}
		// 目录: 没有打开过直接关闭
		else if( nodeFile.isDirectory()) {
			// 没有展开的目录, 直接移除
			if(node.getChildren().size() == 0){
				rootNode.getChildren().remove(node);
			}else {
				// 展开的目录, 递归关闭子项
				for(var subNode : node.getChildren()) {
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
			rootNode.getChildren().clear();
			filePath = "";
			ComponentGetter.appComponent.saveData(NoteDelegateImpl.pluginName, "dir_path", "");
		}); 
		 
	    MenuItem Refresh = new MenuItem("Refresh ");
	    Refresh.setOnAction(e -> {	    	
	    	var itm = NoteTabTreeView.getSelectionModel().getSelectedItem();
			var ParentNode = itm.getParent();
			if(Objects.equals(rootNode, ParentNode)) {
    			if(rootNode.getChildren().size() > 0) {
    	    		rootNode.getChildren().clear();
    	    	}
    	    	openNoteDir(ParentNode , new File(filePath));
    		}else {
    			if(ParentNode.getChildren().size() > 0) {
    				ParentNode.getChildren().clear();
    	    	}
    			var parentFile = ParentNode.getValue().getFile();
    			openNoteDir(ParentNode , parentFile);
    		}	 
		}); 
	    
	    MenuItem newFile = new MenuItem("New File ");
	    newFile.setOnAction(e -> {	    	
	    	var itm = NoteTabTreeView.getSelectionModel().getSelectedItem();
			var ParentNode = itm.getParent();
			if(Objects.equals(rootNode, ParentNode)) {	
				newFileNode(rootNode, filePath);
    		}else {
    			var parentFile = ParentNode.getValue().getFile();
    			newFileNode(ParentNode, parentFile.getAbsolutePath());
    		}	 
		}); 
	    
	    
		MenuItem Open = new MenuItem("Open Folder");
		Open.setOnAction(e -> {
			File f = FileOrDirectoryChooser.showDirChooser("Select Directory", ComponentGetter.primaryStage );
			if(f !=null && f.exists() ) {
				filePath = f.getAbsolutePath();
				if(rootNode.getChildren().size() >= 0) {
					rootNode.getChildren().clear();
				}
				openNoteDir(rootNode, f);
				ComponentGetter.appComponent.saveData(NoteDelegateImpl.pluginName, "dir_path", filePath);
			
			}
		}); 
		 
		MenuItem close = new MenuItem("Close");
		close.setOnAction(e -> {
			var itm = NoteTabTreeView.getSelectionModel().getSelectedItem();
			NoteTabTree.closeAction(itm);
		}); 
		
		MenuItem deleteFile = new MenuItem("Delete File");
		deleteFile.setOnAction(e -> {
			var itm = NoteTabTreeView.getSelectionModel().getSelectedItem();
			File file = itm.getValue().getFile();
			String fileTyep = "File";
			if(file.isDirectory()) {
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
			MyAlert.myConfirmation("Delete  " + fileTyep+ ": "+ file.getAbsolutePath() + " ? ", stage, btns);
			
			
			
		}); 
		
		
		MenuItem showInFolder = new MenuItem("Show In Folder");
		showInFolder.setOnAction(e -> {
			TreeItem<SqluckyTab> ctt = NoteTabTreeView.getSelectionModel().getSelectedItem();
			SqluckyTab tb = ctt.getValue(); 
			try {
				String fn = tb.getDocumentPo().getFileFullName();
				if(StrUtils.isNotNullOrEmpty(fn)) {
					File file = new File(fn); 
					CommonUtility.openExplorer(file.getParentFile());
				}
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}); 
		
		contextMenu.getItems().addAll( 
				Open, 
				close,
				new SeparatorMenuItem(), 
				newFile,
				deleteFile,
				Refresh,
				Clean  , 
				new SeparatorMenuItem(), 
				showInFolder
				);
		contextMenu.setOnShowing(e->{
			var itm = NoteTabTreeView.getSelectionModel().getSelectedItem();
			
			if( itm != null &&
				itm.getValue() !=null && 
				itm.getValue().getFile() != null &&
				itm.getValue().getFile().isFile()) {
				deleteFile.setDisable(false);
			}else {
				deleteFile.setDisable(true);
			}
			
			if( itm != null &&
				itm.getValue() !=null && 
				itm.getValue().getFile() != null ) {
				showInFolder.setDisable(false);
			}else {
				showInFolder.setDisable(true);
			}
			
			 
			
		});
		return contextMenu;
	}
	
	//TODO 打开sql文件
	public static void openNoteDir(TreeItem<SqluckyTab> node , File openFile) {
		try {
			Consumer< String >  caller  = x->{
				File[] files = openFile.listFiles();
				if(files == null) return ;
				List<TreeItem<SqluckyTab>> ls = new ArrayList<>();
				for(var file : files) {
					TreeItem<SqluckyTab> item = createItemNode(node, file);
					if(item !=null) {
						ls.add(item);
					}
					
				} 
				if(ls.size() > 0) {
					Platform.runLater(()->{
						node.getChildren().addAll(ls); 
					}); 
				}
			};
			
			CommonUtility.runThread(caller);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static TreeItem<SqluckyTab> createItemNode(TreeItem<SqluckyTab> node , File file) {
		if( file.exists() ) {
			DocumentPo fileNode = new DocumentPo(); 
			fileNode.setFileFullName(file.getAbsolutePath());  
			fileNode.setTitle(file.getName()); 
			SqluckyTab mtb = ComponentGetter.appComponent.sqluckyTab(fileNode); 
			
			mtb.setFile(file);
			Region icon ;
			if(file.isFile()) {
				  icon =  ComponentGetter.appComponent.getIconDefActive("file-text-o");
				 
			}else {
				  icon =  ComponentGetter.appComponent.getIconDefActive("folder");
				 
			}
			mtb.setIcon(icon);
			
			TreeItem<SqluckyTab> item = new TreeItem<>(mtb);
			return item;
		}
		return null;
		
	}
	
	public static void  newFileNode(TreeItem<SqluckyTab> node, String fpval) {
		FileChooser fc =  FileOrDirectoryChooser.getFileChooser("New ", "new " , new File(fpval)); 
		File file = fc.showSaveDialog( ComponentGetter.primaryStage);
		try {
			SaveFile.save(file, "");
		} catch (IOException e1) { 
			e1.printStackTrace();
		}
		if(file.getAbsolutePath().startsWith(fpval)) {
			if(node.getChildren().size() > 0) {
				node.getChildren().clear();
	    	}
	    	openNoteDir(node , new File(fpval));
		} 
	}
}
