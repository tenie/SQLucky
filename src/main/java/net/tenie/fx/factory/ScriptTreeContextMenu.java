package net.tenie.fx.factory;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXButton;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.MenuAction;
import net.tenie.fx.PropertyPo.DbConnectionPo;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.MyTab;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.component.container.DBinfoTree;
import net.tenie.fx.component.container.ScriptTabTree;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.fx.window.ModalDialog;
import net.tenie.fx.window.TableDataDetail;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.db.h2.SqlTextDao;
import net.tenie.lib.tools.StrUtils;

public class ScriptTreeContextMenu {
//	public static List<MenuItem> menuItems = new ArrayList<>();
	private ContextMenu contextMenu;  
	private MenuItem close; 
	
	
	public ScriptTreeContextMenu(TreeItem<MyTab> rootNode) {

		contextMenu = new ContextMenu();  
		
		close = new MenuItem("Close");
		close.setOnAction(e -> {
			closeAction(rootNode);
		}); 
		 
		MenuItem Open = new MenuItem("Open");
		Open.setOnAction(e -> {
			ScriptTabTree.openMyTab();
		}); 
		
	 
		MenuItem New = new MenuItem("New");
		New.setOnAction(e -> {
			SqlEditor.addCodeEmptyTabMethod();
		}); 
		
		MenuItem save = new MenuItem("Save");
		save.setOnAction(e -> {
			TreeItem<MyTab> ctt = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItem();
			MyTab mtab = ctt.getValue();
			CommonAction.saveSqlAction(mtab);
		}); 
			
		
		MenuItem Import = new MenuItem("Import...");
		Import.setOnAction(e -> {
			CommonAction.openSqlFile("UTF-8");
		}); 
		
		MenuItem folder = new MenuItem("Show In Folder");
		folder.setOnAction(e -> {
			TreeItem<MyTab> ctt = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItem();
			MyTab tb = ctt.getValue(); 
			try {
				String fn = tb.getScriptPo().getFileName();
				if(StrUtils.isNotNullOrEmpty(fn)) {
					File file = new File(fn); 
					Desktop.getDesktop().open(file.getParentFile());
				}
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}); 
		
		contextMenu.getItems().addAll( 
				folder,
				Import,
				new SeparatorMenuItem(),
				New,
				Open,
				save,
				close );
	
	}
	 
	 

	public ContextMenu getContextMenu() {
		return contextMenu;
	}


	public void setContextMenu(ContextMenu contextMenu) {
		this.contextMenu = contextMenu;
	}

	
	private void removeNode(ObservableList<TreeItem<MyTab>>  myTabItemList, TreeItem<MyTab> ctt, MyTab tb ) {
		try { 
			
			var conn = H2Db.getConn();
			if (SqlEditor.myTabPane.getTabs().contains(tb)) {
				SqlEditor.myTabPane.getTabs().remove(tb);
			}
			myTabItemList.remove(ctt);

			var scpo = tb.getScriptPo();
			SqlTextDao.deleteScriptArchive(conn, scpo);
		} finally {
			H2Db.closeConn();
		}
	}
	
	private void closeAction(TreeItem<MyTab> rootNode) {

		ObservableList<TreeItem<MyTab>>  myTabItemList = rootNode.getChildren();
		TreeItem<MyTab> ctt = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItem();
		MyTab tb = ctt.getValue();
		
		String title = CommonUtility.tabText(tb);
		String sql = SqlEditor.getTabSQLText(tb);
		if (title.endsWith("*") && sql.trim().length() > 0) {
			// 是否保存
			final Stage stage = new Stage();

			// 1 保存
			JFXButton okbtn = new JFXButton("Yes");
			okbtn.getStyleClass().add("myAlertBtn");
			okbtn.setOnAction(value -> {
				CommonAction.saveSqlAction(tb);
				removeNode(myTabItemList, ctt, tb);
				stage.close();
			});

			// 2 不保存
			JFXButton Nobtn = new JFXButton("No");
			Nobtn.setOnAction(value -> {
				removeNode(myTabItemList, ctt, tb);
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

			ModalDialog.myConfirmation("Save " + StrUtils.trimRightChar(title, "*") + "?", stage, btns);
		}else {
			removeNode(myTabItemList, ctt, tb);
		}
		

	}
	
	
}
