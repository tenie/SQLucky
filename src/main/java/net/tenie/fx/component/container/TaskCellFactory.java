package net.tenie.fx.component.container;

import java.util.Objects;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.SqlEditor;

/*   @author tenie */
public class TaskCellFactory implements Callback<TreeView<TreeNodePo>, TreeCell<TreeNodePo>> {
	private static final DataFormat JAVA_FORMAT = new DataFormat("application/x-java-serialized-object");
	private static final String DROP_HINT_STYLE = "-fx-border-color: #eea82f; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3";
	private TreeCell<TreeNodePo> dropZone;
	private TreeItem<TreeNodePo> draggedItem;

	@Override
	public TreeCell<TreeNodePo> call(TreeView<TreeNodePo> treeView) {
		TreeCell<TreeNodePo> cell = new TreeCell<TreeNodePo>() {
			private TextField textField;

			@Override
			public void startEdit() {
				super.startEdit();

				if (textField == null) {
					createTextField();
				}
				setText(null);
				setGraphic(textField);
				textField.selectAll();
			}

			@Override
			public void cancelEdit() {
				super.cancelEdit();
				setText(getItem().getName());
				setGraphic(getItem().getIcon());
			}

			@Override
			public void updateItem(TreeNodePo item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					setGraphic(item.getIcon());
					setText(item.getName());

				}
			}

			private void createTextField() {
				textField = new TextField(getString());
				textField.setOnKeyReleased((KeyEvent t) -> {
					if (t.getCode() == KeyCode.ENTER) {
						TreeNodePo item = getItem();
						item.setName(textField.getText());
						commitEdit(item);
					} else if (t.getCode() == KeyCode.ESCAPE) {
						cancelEdit();
					}
				});
			}

			private String getString() {
				return getItem() == null ? "" : getItem().getName();
			}

		};

		cell.setOnDragDetected((MouseEvent event) -> dragDetected(event, cell, treeView));
		cell.setOnDragOver((DragEvent event) -> dragOver(event, cell, treeView));
		cell.setOnDragDropped((DragEvent event) -> drop(event, cell, treeView));
		cell.setOnDragDone((DragEvent event) -> clearDropLocation());

		return cell;
	}
	
	/**
	    当你从一个Node上进行拖动的时候，会检测到拖动操作，将会执行这个EventHandler
	     setOnDragDetected(new EventHandler<MouseEvent>());  
	  当你拖动到目标控件的时候，会执行这个事件回调。     
  	    setOnDragEntered(new EventHandler<DragEvent>()); 
	  当你拖动移出目标控件的时候，执行这个操作。
	  setOnDragExited(new EventHandler<DragEvent>());  
	  当你拖动到目标上方的时候，会不停的执行。
	  setOnDragOver(new EventHandler<DragEvent>()); 
	  当你拖动到目标并松开鼠标的时候，执行这个DragDropped事件。  
  		setOnDragDropped(new EventHandler<DragEvent>());  
          当你拖动并松手的时候，执行Drag完成操作。		
  		setOnDragDone(new EventHandler<DragEvent>()); 

	 */
 

	// 发现拖动 当你从一个Node上进行拖动的时候，会检测到拖动操作，将会执行这个
	private void dragDetected(MouseEvent event, TreeCell<TreeNodePo> treeCell, TreeView<TreeNodePo> treeView) {
		System.out.println("dragDetected"); 
		
		draggedItem = treeCell.getTreeItem();

		// root can't be dragged
		if (draggedItem.getParent() == null) {
			ComponentGetter.dragTreeItemName = "";
			return;
		}else {
			ComponentGetter.dragTreeItemName =  draggedItem.getValue().getName();
			System.out.println("ComponentGetter.dragTreeItemName =" +ComponentGetter.dragTreeItemName);
		}
			
		Dragboard db = treeCell.startDragAndDrop(TransferMode.ANY);

		ClipboardContent content = new ClipboardContent();
		content.put(JAVA_FORMAT, draggedItem.getValue().getName());
		db.setContent(content);
		db.setDragView(treeCell.snapshot(null, null));
		event.consume();
	
	}

	private void dragOver(DragEvent event, TreeCell<TreeNodePo> treeCell, TreeView<TreeNodePo> treeView) {
		System.out.println("dragOver");
		 
		
//		//当前坐标转屏幕坐标
//		double minX = getLayoutBounds.getMinX();
//		//左上角Y
//		double minY = getLayoutBounds.getMinY();
//		Point2D localToScreen = SqlEditor.getCodeArea().localToScreen(minX, minY);
//		double screenX = localToScreen.getX();
//		double screenY = localToScreen.getY();
//		System.out.println("screenX = " + screenX + " | screenY =" + screenY);
//		
		if (!event.getDragboard().hasContent(JAVA_FORMAT))
			return;
		TreeItem<TreeNodePo> thisItem = treeCell.getTreeItem();

		// can't drop on itself
		if (draggedItem == null || thisItem == null || thisItem == draggedItem)
			return;
		// ignore if this is the root
		if (draggedItem.getParent() == null) {
			clearDropLocation();
			return;
		}

		event.acceptTransferModes(TransferMode.ANY);
		if (!Objects.equals(dropZone, treeCell)) {
			clearDropLocation();
			this.dropZone = treeCell;
			dropZone.setStyle(DROP_HINT_STYLE);
		}
		
		 
	}

	// 放下后执行
	private void drop(DragEvent event, TreeCell<TreeNodePo> treeCell, TreeView<TreeNodePo> treeView) {
		System.out.println("drop");
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (!db.hasContent(JAVA_FORMAT))
			return;

		TreeItem<TreeNodePo> thisItem = treeCell.getTreeItem();
		TreeItem<TreeNodePo> droppedItemParent = draggedItem.getParent();

		// 只能同一个父节点下换位置, 否则不动
		if (Objects.equals(droppedItemParent, thisItem.getParent())) {
			droppedItemParent.getChildren().remove(draggedItem);
			int indexInParent = thisItem.getParent().getChildren().indexOf(thisItem);
			thisItem.getParent().getChildren().add(indexInParent + 1, draggedItem);

		}
		if (Objects.equals(droppedItemParent, thisItem)) {
			droppedItemParent.getChildren().remove(draggedItem);
			droppedItemParent.getChildren().add(0, draggedItem);

		}

		treeView.getSelectionModel().select(draggedItem);
		event.setDropCompleted(success);
	}

	private void clearDropLocation() {
		System.out.println("clearDropLocation");
		ComponentGetter.dragTreeItemName = "";
		if (dropZone != null)
			dropZone.setStyle("");
	}
}
