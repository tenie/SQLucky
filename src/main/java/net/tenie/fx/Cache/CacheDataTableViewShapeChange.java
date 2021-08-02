package net.tenie.fx.Cache;

import java.util.HashMap;
import java.util.Map;

import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
 
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import net.tenie.fx.Action.myEvent;

public class CacheDataTableViewShapeChange {
	static private Map<String, DataTableViewShapePo> tableColumnWidth = new HashMap<>();
	static private Map<String, Double> tableScrollHorizontal = new HashMap<>();

	/**
	 * 设置表的水平滚顶条位置
	 * 
	 * @param tableName
	 * @param table
	 */
	static public void setHorizontal(String tableName, FilteredTableView<ObservableList<StringProperty>> table) {
		Platform.runLater(() -> {
			ScrollBar horizontalBar = null;
			// 获取到 ScrollBar的 父节点, 然后遍历子节点, 获取第三个元素就是 水平ScrollBar了
			Node nodes = table.lookup(".virtual-flow");
//			StackPane sp = (StackPane) table.lookup(".show-hide-columns-button");
//			MouseEvent me = myEvent.mouseEvent(MouseEvent.MOUSE_PRESSED , sp);
//			Event.fireEvent(sp, me  ); 
			if (nodes instanceof Parent) {
				javafx.scene.Parent p = (Parent) nodes;
				ObservableList<Node> ls = p.getChildrenUnmodifiable();
//				   System.out.println(ls.size());
////				   for(Node n : ls) {
////					   System.out.println(n.getClass());
////				   }
				if (ls.size() > 3 && ls.get(3) instanceof javafx.scene.control.ScrollBar) {
					horizontalBar = (javafx.scene.control.ScrollBar) ls.get(3);
				}

			}
			if (horizontalBar != null) {
				var val = getHorizontal(tableName);
				if (val != null) {
					Double maxval = horizontalBar.getMax();
					if (val < maxval) {
						horizontalBar.setValue(val);
					}
				}
				// 水平滚动条位置值变化监听, 保存新值
				horizontalBar.valueProperty().addListener((obs, oldValue, newValue) -> {
//					System.out.println("addListener ===" +oldValue +" | " + newValue );
					CacheDataTableViewShapeChange.saveHorizontal(tableName, newValue.doubleValue());
				});

			}
		});

	}

	// 保存对应表的水平滚顶条位置
	static public void saveHorizontal(String tableName, Double horizontal) {
		tableScrollHorizontal.put(tableName, horizontal);
	}

	// 获取表的水平滚顶条位置
	static public Double getHorizontal(String tableName) {
		return tableScrollHorizontal.get(tableName);
	}

	/**
	 * 根据表名和列名得到列的宽, 找不到就返回null
	 * 
	 * @param tableName
	 * @param ColumnName
	 * @return
	 */
	static public Double getWidth(String tableName, String ColumnName) {
		DataTableViewShapePo po = tableColumnWidth.get(tableName);
		if (po != null) {
			Double col = po.getColumn().get(ColumnName);
			return col;
		}
		return null;
	}

	static public void saveWidth(String tableName, String ColumnName, Double width) {
		var val = tableColumnWidth.get(tableName);
		if (val == null) {
			DataTableViewShapePo po = new DataTableViewShapePo(tableName, ColumnName, width);
			tableColumnWidth.put(tableName, po);
		} else {
			val.saveVal(ColumnName, width);
		}
	}

	/**
	 * 使用缓存中列宽的值来设置列宽, 没用缓存就单独计算
	 * 
	 * @param col
	 * @param tableName
	 * @param colname
	 * @param augmentation
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static public void setColWidth(FilteredTableColumn<ObservableList<StringProperty>, String>  col, String tableName, String colname, boolean augmentation) {
		// 设置列的长度
		Double width;
		Double cacheWidth = CacheDataTableViewShapeChange.getWidth(tableName, colname);
		if (colname.equals("Execute SQL Info")) {
			width = 550.0;
		} else if (colname.equals("Execute SQL")) {
			width = 600.0;
		} else {
			width = (colname.length() * 10.0) + 15;
			if (width < 90)
				width = 100.0;
			if (augmentation) {
				width = 200.0;
			}
		}

		if (cacheWidth == null) {
			col.setMinWidth(width);
			col.setPrefWidth(width);
		} else {
			col.setMinWidth(width);
			col.setPrefWidth(cacheWidth);
		}
		// 添加外形拖尺寸变化事件
		col.widthProperty().addListener((ChangeListener<Number>) (ov, t, t1) -> {
			CacheDataTableViewShapeChange.saveWidth(tableName, colname, Double.valueOf(t1.longValue()));
		});
		
		// 列被拖到位置事件
//		col.get
//		col.getSouthNode()t
//		col.addEventHandler(MouseEvent.ANY, e ->{
//				  String str = col.getText();
//                  System.out.println("Cell row index: " + str);
//			});
		
	}

	
	public static void  setTableHeader(FilteredTableView<ObservableList<StringProperty>>  table) {
		
//		NestedTableColumnHeader h2 =	 (impl.org.controlsfx.tableview2.NestedTableColumnHeader2) table.lookup(".column-header-background .nested-column-header");
		
//		var childs = h2.getChildrenUnmodifiable();
//		for(Node cd : childs) {
//			cd.setOnDragDetected(e->{ 
//				System.out.println(  "???");
//			});
//		}
//		
//		var hds =	table.getColumns() ; //table.getRowHeader().getColumns();
//		table.getColumns().get(0).cell
//		for(TableColumn header : hds) {
//			Node nd =  header.getStyleableNode();
//			nd.setOnDragDetected(e->{ 
//				System.out.println(  header.getText());
//			});
////			header.addEventHandler(MouseEvent.DRAG_DETECTED, e ->{
////				  String str = header.getText();
////                  System.out.println("Cell row index: " + str);
////			});
//		}
	}

}
