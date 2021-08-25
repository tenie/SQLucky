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
import net.tenie.fx.PropertyPo.TablePo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.dao.ConnectionDao;

/**
 * 把TablePo对象的属性 赋值给 TreeItem显示(节点名称,图标)
 *    @author tenie
 *     */
public class TreeNodeCellFactory2 implements Callback<TreeView<TablePo>, TreeCell<TablePo>> {
 	@Override
	public TreeCell<TablePo> call(TreeView<TablePo> treeView) {
		TreeCell<TablePo> cell = new TreeCell<TablePo>() {
			  
	 
			@Override
			public void updateItem(TablePo item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					if(item != null ) {
						setText(item.getTableName());
					}else {
						setText("root");
					}
//					setGraphic(item.getIcon());
					

				}
			}
 

			private String getString() {
				return getItem() == null ? "" : getItem().getTableName();
			}

		};
 
		return cell;
	}
	 
}
