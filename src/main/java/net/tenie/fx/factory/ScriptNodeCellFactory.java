package net.tenie.fx.factory;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import net.tenie.fx.PropertyPo.DbConnectionPo;
import net.tenie.fx.PropertyPo.ScriptPo;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.dao.ConnectionDao;

/**
 * 
 * @author tenie
 *
 */
public class ScriptNodeCellFactory implements Callback<TreeView<ScriptPo>, TreeCell<ScriptPo>> {
//	private static Logger logger = LogManager.getLogger(ScriptNodeCellFactory.class);
 

	@Override
	public TreeCell<ScriptPo> call(TreeView<ScriptPo> treeView) {
		TreeCell<ScriptPo> cell = new TreeCell<ScriptPo>() {
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
			public void updateItem(ScriptPo item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
//					setGraphic(item.getIcon());
					setText(item.getTitle());

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

			private String getString() {
				return getItem() == null ? "" : getItem().getTitle();
			}

		};
 
		return cell;
	}
	    
}
