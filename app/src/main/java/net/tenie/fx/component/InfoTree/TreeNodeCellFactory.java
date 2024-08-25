package net.tenie.fx.component.InfoTree;

import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.*;
import javafx.util.Callback;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.po.TreeItemType;
import net.tenie.Sqlucky.sdk.po.component.TreeNodePo;
import net.tenie.fx.dao.ConnectionDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * 把TreeNodePo对象的属性 赋值给 TreeItem显示(节点名称,图标)
 *    @author tenie
 *     */
public class TreeNodeCellFactory implements Callback<TreeView<TreeNodePo>, TreeCell<TreeNodePo>> {
	private static Logger logger = LogManager.getLogger(TreeNodeCellFactory.class);
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
					if(item != null) {
						setGraphic(item.getIcon());
						setText(item.getName()+"");
					} else {
						setText(null);
						setGraphic(null);
					}
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
		logger.info("dragDetected"); 
		
		draggedItem = treeCell.getTreeItem();

		// 当连接节点是打开的时候禁止拖动
		TreeNodePo nodePo = draggedItem.getValue();
		TreeItemType nodePoType =  nodePo.getType();
		if(nodePoType.equals(TreeItemType.CONNECT_INFO)){
			var ch = draggedItem.getChildren();
			if(!ch.isEmpty()){
				return;
			}
		}

		if(draggedItem == null) {
			return;
		}
		// root can't be dragged
		if (draggedItem.getParent() == null) {
			ComponentGetter.dragTreeItemName = "";
			return;
		}else {
			ComponentGetter.dragTreeItemName =  draggedItem.getValue().getName();
			logger.info("ComponentGetter.dragTreeItemName =" +ComponentGetter.dragTreeItemName);
		}
			
		Dragboard db = treeCell.startDragAndDrop(TransferMode.ANY);

		ClipboardContent content = new ClipboardContent();
		content.put(JAVA_FORMAT, draggedItem.getValue().getName());
		db.setContent(content);
		db.setDragView(treeCell.snapshot(null, null));
		event.consume();
	
	}

	private void dragOver(DragEvent event, TreeCell<TreeNodePo> treeCell, TreeView<TreeNodePo> treeView) {
  
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
		logger.info("drop");
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (!db.hasContent(JAVA_FORMAT)){
			return;
		}

		// 当前鼠标所在位置的节点对象
		TreeItem<TreeNodePo> thisItem = treeCell.getTreeItem();
		TreeItem<TreeNodePo> droppedItemParent = draggedItem.getParent();

		// 只能同一个父节点下换位置, 否则不动
		if (Objects.equals(droppedItemParent, thisItem.getParent())) {
			boolean addNext = false;
			// 如果当前节点是打开的, 判断他下面的节点是不是也打开的, 也是打开的就不能插入他们之间
			if(!thisItem.getChildren().isEmpty()){
				TreeItem<TreeNodePo> thisItemNextSibling  = thisItem.nextSibling();
                addNext = thisItemNextSibling.getChildren().isEmpty();
			}else {
				addNext = true;
			}
			// 插入当前节点之后
			if(addNext){
				droppedItemParent.getChildren().remove(draggedItem);
				// 获取当前位置(thisItem) 的下表, 在当前位置后面添加被拖动的对象
				int indexInParent = thisItem.getParent().getChildren().indexOf(thisItem);
				thisItem.getParent().getChildren().add(indexInParent + 1, draggedItem);
				success = true;
			}


		//如果鼠标位置的节点是拖动的对象的父节点, 那么将拖动对象放在父节点的第一个位置
		} else if (Objects.equals(droppedItemParent, thisItem)) {
			droppedItemParent.getChildren().remove(draggedItem);
			droppedItemParent.getChildren().add(0, draggedItem);
			success = true;

		}

		if(success){
			// 保持位置
			String dragName = draggedItem.getValue().getName();
			String sibName = "";
			var sibling = draggedItem.nextSibling();
			if(sibling != null ){
				sibName = sibling.getValue().getName();
			}
			ConnectionDao.updatePosition(dragName, sibName );
		}
		treeView.getSelectionModel().select(draggedItem);
		event.setDropCompleted(success);
	}

	private void savePosition(){

	}

	private void clearDropLocation() {
		logger.info("clearDropLocation");
		ComponentGetter.dragTreeItemName = "";
		if (dropZone != null)
			dropZone.setStyle("");
	}
}
