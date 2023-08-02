package net.tenie.fx.component.ScriptTree;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;

/**
 * 
 * @author tenie
 *
 */
public class ScriptTabNodeCellFactory implements Callback<TreeView<MyEditorSheet>, TreeCell<MyEditorSheet>> {
	private static Logger logger = LogManager.getLogger(ScriptTabNodeCellFactory.class);
	private static final String DROP_HINT_STYLE = "-fx-border-color: #eea82f; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3";
	private TreeCell<MyEditorSheet> dropZone;
	private TreeItem<MyEditorSheet> draggedItem;

	@Override
	public TreeCell<MyEditorSheet> call(TreeView<MyEditorSheet> treeView) {
		Button clean = new Button();
		TreeCell<MyEditorSheet> cell = new TreeCell<MyEditorSheet>() {

			@Override
			public void updateItem(MyEditorSheet item, boolean empty) {
				super.updateItem(item, empty);
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
						Label label = new Label(item.getDocumentPo().getTitle());
//	                    Button clean = new Button();  

						pn.getChildren().add(label);
						pn.getChildren().add(clean);

						AnchorPane.setRightAnchor(clean, 5.0);
						setGraphic(pn);

						setText(null);
					} else {
						// If this is the root we just display the text.
//	                    setGraphic(null);
						setText(item.getDocumentPo().getTitle());
					}
				}

			}

//			private String getString() {
//				return getItem() == null ? "" : getItem().getScriptPo().getTitle();
//			}

		};
		clean.setMaxSize(12, 12);
		clean.setGraphic(IconGenerator.svgImageUnactive("times-circle", 14));
		clean.getStyleClass().add("myCleanBtn");
		clean.setVisible(false); // clean 按钮默认不显示, 只有在鼠标进入搜索框才显示
		clean.setOnAction(e -> {
			var rootNode = ScriptTabTree.ScriptTreeView.getRoot();
			ScriptTabTree.closeAction(rootNode);
		});

		cell.setOnMouseClicked(e -> {
			if (cell.isSelected()) {
				clean.setVisible(true);
			}

		});
		cell.setOnMouseEntered(e -> {
			if (cell.isSelected()) {
				clean.setVisible(true);
			}

		});

		cell.setOnMouseExited(e -> {
			clean.setVisible(false);
		});

		cell.setOnDragDetected((MouseEvent event) -> dragDetected(event, cell, treeView));
		cell.setOnDragOver((DragEvent event) -> dragOver(event, cell, treeView));
		cell.setOnDragDropped((DragEvent event) -> drop(event, cell, treeView));
		cell.setOnDragDone((DragEvent event) -> clearDropLocation());

		return cell;
	}

	// 发现拖动 当你从一个Node上进行拖动的时候，会检测到拖动操作，将会执行这个
	private void dragDetected(MouseEvent event, TreeCell<MyEditorSheet> treeCell, TreeView<MyEditorSheet> treeView) {
		logger.info("dragDetected");

		draggedItem = treeCell.getTreeItem();

		// root can't be dragged
		if (draggedItem.getParent() == null) {
			return;
		}

		Dragboard db = treeCell.startDragAndDrop(TransferMode.ANY);

		ClipboardContent content = new ClipboardContent();
		content.put(DataFormat.PLAIN_TEXT, draggedItem.getValue().getDocumentPo().getTitle());
		db.setContent(content);
		db.setDragView(treeCell.snapshot(null, null));
		event.consume();

	}

	private void dragOver(DragEvent event, TreeCell<MyEditorSheet> treeCell, TreeView<MyEditorSheet> treeView) {

		if (!event.getDragboard().hasContent(DataFormat.PLAIN_TEXT))
			return;
		TreeItem<MyEditorSheet> thisItem = treeCell.getTreeItem();

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
	private void drop(DragEvent event, TreeCell<MyEditorSheet> treeCell, TreeView<MyEditorSheet> treeView) {
		logger.info("drop");
		Dragboard db = event.getDragboard();
		boolean success = false;
		if (!db.hasContent(DataFormat.PLAIN_TEXT))
			return;

		TreeItem<MyEditorSheet> thisItem = treeCell.getTreeItem();
		TreeItem<MyEditorSheet> droppedItemParent = draggedItem.getParent();

//		// 只能同一个父节点下换位置, 否则不动
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
		if (dropZone != null)
			dropZone.setStyle("");
	}

}
