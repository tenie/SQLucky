package net.tenie.plugin.DataModel;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import net.tenie.plugin.DataModel.po.DataModelTreeNodePo;


/**
 * 
 * @author tenie
 *
 */
public class DataModelNodeCellFactory implements Callback<TreeView<DataModelTreeNodePo>, TreeCell<DataModelTreeNodePo>> { 
//	private static Logger logger = LogManager.getLogger(NoteTabNodeCellFactory.class);
	private static final String DROP_HINT_STYLE = "-fx-border-color: #eea82f; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3";
	private TreeCell<DataModelTreeNodePo> dropZone;
	private TreeItem<DataModelTreeNodePo> draggedItem;

	@Override
	public TreeCell<DataModelTreeNodePo> call(TreeView<DataModelTreeNodePo> treeView) {
		Button clean = new Button(); 
		TreeCell<DataModelTreeNodePo> cell = new TreeCell<DataModelTreeNodePo>() {
		
			@Override
			public void updateItem(DataModelTreeNodePo item, boolean empty) {
				super.updateItem(item, empty);				
	            if (isEmpty()) {
	                setGraphic(null);
	                setText(null);
	            } else {	                
	                	Node icon = item.getIcon();
	                    Label label = new Label(item.getName());
	                    label.setGraphic(icon);
	                    setGraphic(label);
	                    setText(null);
	            }
			}
		};
//		clean.setMaxSize(12, 12); 
//  		clean.setGraphic(ComponentGetter.getIconUnActive("times-circle"));
//  		clean.getStyleClass().add("myCleanBtn");
//  		clean.setVisible(false); //clean 按钮默认不显示, 只有在鼠标进入搜索框才显示
//  		clean.setOnAction(e->{
//  			var it = cell.getTreeItem();
//  		}); 
  		 
//  		cell.setOnMouseEntered(e->{
//			if(cell.isSelected()) {
//				clean.setVisible(true);
//			}
//		
//		});
//  		
//		cell.setOnMouseExited(e->{
//			clean.setVisible(false);
//		});
		
		cell.setOnMouseClicked(e -> {
//			if (cell.isSelected()) {
//				clean.setVisible(true);
//			}
			if (e.getClickCount() == 2) {
				if (cell == null || cell.getTreeItem() == null)
					return;
				if (cell.getTreeItem().getValue().getIsModel()) {
					DataModelTabTree.modelInfoTreeAddTableTreeNode(cell.getTreeItem());
				}else {
					DataModelTabTree.showFields(cell.getTreeItem().getValue().getTableId());	
				}
			}
//			if(e.getClickCount() == 2) {
//				if(cell == null || cell.getTreeItem() == null) return;
//				DataModelTreeNodePo stb = cell.getTreeItem().getValue();
//				File file = stb.getFile();
//				if(! file.exists()) return;
//				if(file.isFile()) {
//					if(StrUtils.isNotNullOrEmpty(file.getAbsolutePath() ) ){
//						String fp = file.getAbsolutePath().toLowerCase();
//						if(fp.endsWith(".md") 
//						   || fp.endsWith(".text") 
//						   || fp.endsWith(".sql") 
//						   || fp.endsWith(".txt") 
//					    ) {
//							String  val = CommonUtility.readFileText(file, "UTF-8");
//							stb.setFileText(val);
//							stb.mainTabPaneAddMyTab();
//							
//						}else {
//							CommonUtility.openExplorer(file);
//						}
//						
//					}else {
//						CommonUtility.openExplorer(file);
//					}
//					
//				}else if(file.isDirectory()) {
//					if(cell.getTreeItem().getChildren().size() == 0) {
//						DataModelTabTree.openNoteDir(cell.getTreeItem(), file);
//						cell.getTreeItem().setExpanded(true);
//					}
//					
//				}
//				
//			}
		});
		
		return cell;
	}
	

	  
  
	    
}
