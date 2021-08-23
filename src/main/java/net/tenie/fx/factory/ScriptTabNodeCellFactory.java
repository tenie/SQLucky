package net.tenie.fx.factory;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.MyTab;
import net.tenie.fx.component.container.ScriptTabTree;

/**
 * 
 * @author tenie
 *
 */
public class ScriptTabNodeCellFactory implements Callback<TreeView<MyTab>, TreeCell<MyTab>> { 
 

	@Override
	public TreeCell<MyTab> call(TreeView<MyTab> treeView) {
		Button clean = new Button(); 
		TreeCell<MyTab> cell = new TreeCell<MyTab>() {
		
//			private TextField textField;

//			@Override
//			public void startEdit() {
//				super.startEdit();
//
////				if (textField == null) {
////					createTextField();
////				}
//				setText(null);
//				setGraphic(textField);
//				textField.selectAll();
//			}

//			@Override
//			public void cancelEdit() {
//				super.cancelEdit();
//				setText(getItem().getTitle());
////				setGraphic(getItem().getIcon());
//			}
			
			
			@Override
			public void updateItem(MyTab item, boolean empty) {
				super.updateItem(item, empty);
//				if (empty) {
//					setText(null);
//					setGraphic(null);
//				} else {
////					setGraphic(item.getIcon());
//					setText(item.getScriptPo().getTitle());
//
//				}
				
				// 给cell 内容添加 button
				   // If the cell is empty we don't show anything.
	            if (isEmpty()) {
	                setGraphic(null);
	                setText(null);
	            } else {
	                // We only show the custom cell if it is a leaf, meaning it has
	                // no children.
	                if (this.getTreeItem().isLeaf()) {

	                    // A custom HBox that will contain your check box, label and
	                    // button.
	                	 AnchorPane pn = new AnchorPane();

//	                    CheckBox checkBox = new CheckBox();
	                    Label label = new Label(item.getScriptPo().getTitle());
//	                    Button clean = new Button();  
	                    
	                    pn.getChildren().add(label);
	                    pn.getChildren().add(clean);
	                    
	                    AnchorPane.setRightAnchor(clean, 5.0);
	                    setGraphic(pn);
	                    
	                    setText(null);
	                } else {
	                    // If this is the root we just display the text.
//	                    setGraphic(null);
	                    setText(item.getScriptPo().getTitle());
	                }
	            }
				
				
			}

//			private void createTextField() {
//				textField = new TextField(getString());
//				textField.setOnKeyReleased((KeyEvent t) -> {
//					if (t.getCode() == KeyCode.ENTER) {
//						ScriptPo item = getItem();
//						item.setTitle(textField.getText());
//						commitEdit(item);
//					} else if (t.getCode() == KeyCode.ESCAPE) {
//						cancelEdit();
//					}
//				});
//			}

//			private String getString() {
//				return getItem() == null ? "" : getItem().getScriptPo().getTitle();
//			}

		};
		clean.setMaxSize(12, 12); 
  		clean.setGraphic(ImageViewGenerator.svgImageUnactive("times-circle" , 14));
  		clean.getStyleClass().add("myCleanBtn");
  		clean.setVisible(false); //clean 按钮默认不显示, 只有在鼠标进入搜索框才显示
  		clean.setOnAction(e->{
  			var rootNode =ScriptTabTree.ScriptTreeView.getRoot();
  			ScriptTabTree.closeAction(rootNode);
  		});
  		
  		cell.setOnMouseClicked(e->{ 
			if(cell.isSelected()) {
				clean.setVisible(true);
			}
		
		});
  		cell.setOnMouseEntered(e->{
			if(cell.isSelected()) {
				clean.setVisible(true);
			}
		
		});
  		
		cell.setOnMouseExited(e->{
			clean.setVisible(false);
		});
		
		return cell;
	}
	    
}
