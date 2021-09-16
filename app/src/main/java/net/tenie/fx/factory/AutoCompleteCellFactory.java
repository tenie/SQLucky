package net.tenie.fx.factory;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import net.tenie.Sqlucky.sdk.po.TablePo;

/**
 * 把TablePo对象的属性 赋值给 TreeItem显示(节点名称,图标)
 *    @author tenie
 *     */
public class AutoCompleteCellFactory implements Callback<TreeView<TablePo>, TreeCell<TablePo>> {
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
						setGraphic(null);
					}else {
						setText("root");
						setGraphic(null);
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
