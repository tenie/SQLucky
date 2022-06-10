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
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.note.impl.NoteDelegateImpl;

public class NoteUtility {
	
	// 
	public static void refreshAction(TreeView<SqluckyTab> NoteTabTreeView, TreeItem<SqluckyTab> rootNode, String filePath) {
    	var itm = NoteTabTreeView.getSelectionModel().getSelectedItem();
		var ParentNode = itm.getParent();
		if (Objects.equals(rootNode, ParentNode)) {
			if (rootNode.getChildren().size() > 0) {
				rootNode.getChildren().clear();
			}
			if (StrUtils.isNotNullOrEmpty(filePath)) {
				NoteUtility.openNoteDir(ParentNode, new File(filePath));
			}

		}else {
			if(ParentNode.getChildren().size() > 0) {
				ParentNode.getChildren().clear();
	    	}
			var parentFile = ParentNode.getValue().getFile();
			NoteUtility.openNoteDir(ParentNode , parentFile);
		}	 
	
	}
	
	// 关闭界面上打开的note目录
	public static String cleanAction(TreeItem<SqluckyTab> rootNode) {
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
	}
	
	// 新建一个文件
	public static void newFile(TreeView<SqluckyTab> NoteTabTreeView , TreeItem<SqluckyTab> rootNode, String filePath) {
		var itm = NoteTabTreeView.getSelectionModel().getSelectedItem();
		var ParentNode = itm.getParent();
		if(Objects.equals(rootNode, ParentNode)) {	
			newFileNode(rootNode, filePath);
		}else {
			var parentFile = ParentNode.getValue().getFile();
			newFileNode(ParentNode, parentFile.getAbsolutePath());
		}	 
	}
	
	public static void  newFileNode(TreeItem<SqluckyTab> node, String fpval) {
		FileChooser fc =  FileOrDirectoryChooser.getFileChooser("New ", "new " , new File(fpval)); 
		File file = fc.showSaveDialog( ComponentGetter.primaryStage);
		try {
			FileTools.save(file, "");
		} catch (IOException e1) { 
			e1.printStackTrace();
		}
		if(file.getAbsolutePath().startsWith(fpval)) {
			if(node.getChildren().size() > 0) {
				node.getChildren().clear();
	    	}
			NoteUtility.openNoteDir(node , new File(fpval));
		} 
	}
	
	// 打开目录
	public static String openFolder( TreeItem<SqluckyTab> rootNode) {
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
				List<TreeItem<SqluckyTab>> ls = new ArrayList<>();
				for (var file : files) {
					TreeItem<SqluckyTab> item = createItemNode(node, file);
					if (item != null) {
						ls.add(item);
					}

				}
				if (ls.size() > 0) {
					Platform.runLater(() -> {
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
}
