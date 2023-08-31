package net.tenie.plugin.DataModel;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.plugin.DataModel.po.DataModelTreeNodePo;

/**
 * 
 * @author tenie
 *
 */
public class DataModelNodeCellFactory
		implements Callback<TreeView<DataModelTreeNodePo>, TreeCell<DataModelTreeNodePo>> {
	private static Logger logger = LogManager.getLogger(DataModelNodeCellFactory.class);
	private static final DataFormat JAVA_FORMAT = DataFormat.PLAIN_TEXT;// new
																		// DataFormat("application/x-java-serialized-object");
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
				// 模型节点双击, 展示所有的表节点
				if (cell.getTreeItem().getValue().getIsModel()) {
					DataModelTabTree.modelInfoTreeAddTableTreeNode(cell.getTreeItem());
				} else {
					// 查询表的字段信息, 在表格中展示
					DataModelTabTree.showFields(cell.getTreeItem().getValue().getTableId());
				}
			}
		});

		cell.setOnDragDetected((MouseEvent event) -> dragDetected(event, cell, treeView));
		cell.setOnDragOver((DragEvent event) -> dragOver(event, cell, treeView));
		cell.setOnDragDropped((DragEvent event) -> drop(event, cell, treeView));
		cell.setOnDragDone((DragEvent event) -> clearDropLocation());
		return cell;
	}

	// 发现拖动 当你从一个Node上进行拖动的时候，会检测到拖动操作，将会执行这个
	private void dragDetected(MouseEvent event, TreeCell<DataModelTreeNodePo> treeCell,
			TreeView<DataModelTreeNodePo> treeView) {
		logger.info("dragDetected");

		draggedItem = treeCell.getTreeItem();
		if(draggedItem == null) {
			return;
		}

		// root can't be dragged
		if (draggedItem != null && draggedItem.getParent() == null) {
			ComponentGetter.dragTreeItemName = "";
			return;
		} else {
			if (draggedItem != null) {
				ComponentGetter.dragTreeItemName = draggedItem.getValue().getName();
				logger.info("ComponentGetter.dragTreeItemName =" + ComponentGetter.dragTreeItemName);

			}
		}

		Dragboard db = treeCell.startDragAndDrop(TransferMode.ANY);

		ClipboardContent content = new ClipboardContent();
		content.put(JAVA_FORMAT, draggedItem.getValue().getName());
		db.setContent(content);
		db.setDragView(treeCell.snapshot(null, null));
		event.consume();

	}

	private void dragOver(DragEvent event, TreeCell<DataModelTreeNodePo> treeCell,
			TreeView<DataModelTreeNodePo> treeView) {

		if (!event.getDragboard().hasContent(JAVA_FORMAT))
			return;
		TreeItem<DataModelTreeNodePo> thisItem = treeCell.getTreeItem();

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
	private void drop(DragEvent event, TreeCell<DataModelTreeNodePo> treeCell, TreeView<DataModelTreeNodePo> treeView) {
		logger.info("drop");
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (!db.hasContent(JAVA_FORMAT))
			return;

		TreeItem<DataModelTreeNodePo> thisItem = treeCell.getTreeItem();
		TreeItem<DataModelTreeNodePo> droppedItemParent = draggedItem.getParent();

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
		logger.info("clearDropLocation");
		ComponentGetter.dragTreeItemName = "";
		if (dropZone != null)
			dropZone.setStyle("");
	}

}
