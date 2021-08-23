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
			ScriptTabTree.closeAction(rootNode);
			// closeAction(rootNode);
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

	
	
	
	
	
}
