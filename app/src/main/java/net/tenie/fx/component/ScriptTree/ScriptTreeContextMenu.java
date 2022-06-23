package net.tenie.fx.component.ScriptTree;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.component.MyTab;
/**
 * 
 * @author tenie
 *
 */
public class ScriptTreeContextMenu {
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
			MyTab.addCodeEmptyTabMethod();
		}); 
		
		MenuItem save = new MenuItem("Save");
		save.setOnAction(e -> {
			ScriptTreeAction.saveAction();
//			TreeItem<MyTab> ctt = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItem();
//			MyTab mtab = ctt.getValue();
//			CommonAction.saveSqlAction(mtab);
		}); 
			
		
		MenuItem Import = new MenuItem("Import...");
		Import.setOnAction(e -> {
			CommonAction.openSqlFile();
		}); 
		
		MenuItem folder = new MenuItem("Show In Folder");
		folder.setOnAction(e -> {
			ScriptTreeAction.showInFloder();
//			TreeItem<MyTab> ctt = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItem();
//			MyTab tb = ctt.getValue(); 
//			String fn = tb.getDocumentPo().getFileFullName();
//			if(StrUtils.isNotNullOrEmpty(fn)) {
//				File file = new File(fn); 
//				CommonUtility.openExplorer(file.getParentFile());
//			}
				
			
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
