package net.tenie.Sqlucky.sdk.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.po.DataTableViewShapePo;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;


public class CacheDataTableViewShapeChange {
	static private Map<String, DataTableViewShapePo> tableColumnWidth = new HashMap<>();
	static private Map<String, Double> tableScrollHorizontal = new HashMap<>();
	static private Map<String, List<String>> colOrder = new HashMap<>();

	public static void setDataTableViewShapeCache(String tableName, FilteredTableView<ResultSetRowPo> table , ObservableList<SheetFieldPo> colss) {
		CommonUtility.threadAwait(1);
		Platform.runLater(() -> { 
			// 列移动缓存
			CacheDataTableViewShapeChange.setTableHeader(table, tableName );			
			
			// 水平滚顶条的缓存
			CacheDataTableViewShapeChange.setHorizontal(table, tableName );
			
			// 添加 tooltip
			CacheDataTableViewShapeChange.setTableHeaderTooltip(table , colss);
		});
		
	}
	
	
	/**
	 * 设置表的水平滚顶条位置
	 * 
	 * @param tableName
	 * @param table
	 */
	public static void setHorizontal( FilteredTableView<ResultSetRowPo> table ,  String tableName) {
			ScrollBar horizontalBar = null;
			// 获取到 ScrollBar的 父节点, 然后遍历子节点, 获取第三个元素就是 水平ScrollBar了
			Node nodes = table.lookup(".virtual-flow");
			if (nodes instanceof Parent) {
				javafx.scene.Parent p = (Parent) nodes;
				ObservableList<Node> ls = p.getChildrenUnmodifiable();
				if (ls.size() > 3 && ls.get(3) instanceof javafx.scene.control.ScrollBar) {
					horizontalBar = (javafx.scene.control.ScrollBar) ls.get(3);
				}

			}
			if (horizontalBar != null) {
				var val = getHorizontal(tableName);
				if (val != null) {
					Double maxval = horizontalBar.getMax();
					if (val <= maxval) {
						horizontalBar.setValue(val);
					}
				}
				// 水平滚动条位置值变化监听, 保存新值
				horizontalBar.valueProperty().addListener((obs, oldValue, newValue) -> {
//					System.out.println("addListener ===" +oldValue +" | " + newValue );
					CacheDataTableViewShapeChange.saveHorizontal(tableName, newValue.doubleValue());
				});

			}
	}
	
	private static  TableColumn<ResultSetRowPo, ?> gettableColumnByName(ObservableList<TableColumn<ResultSetRowPo, ?>> tabcols ,String name){
		for(var col : tabcols) {
			var colname = col.getText();
			if(colname.equals(name)) {
				return col;
			} 
		}
		return null;
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
	static public void setColWidthByCache(FilteredTableColumn<ResultSetRowPo, String>  col, String tableName, String colname) {
		// 设置列的长度
		Double width;
		Double cacheWidth = CacheDataTableViewShapeChange.getWidth(tableName, colname);
//		if (colname.equals("Execute SQL Info")) {
//			width = 550.0;
//		} else if (colname.equals("Execute SQL")) {
//			width = 600.0;
//		} else {
			width = (colname.length() * 10.0) + 15;
			if (width < 100)
				width = 110.0;
//			if (augmentation) {
//				width = 200.0;
//			}
//		}

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
	}
	
	static public void setColWidth(FilteredTableColumn<ResultSetRowPo, String>  col, String colname , Double cusWidth) {
		// 设置列的长度
		Double width;
		if(cusWidth !=null) {
			width = cusWidth;
		}else {
				width = (colname.length() * 10.0) + 15;
				if (width < 90)
					width = 100.0;
		}
		
		col.setMinWidth(width);
		col.setPrefWidth(width);
		 	
	}
	

	// tableview 每个列头的移动事件
	public static void setTableHeader(FilteredTableView<ResultSetRowPo> table, String tableName) {
		try {
			Node h2 = table.lookup(".nested-column-header");
			if(h2 == null ) return ;
			javafx.scene.Parent p = (Parent) h2;
			ObservableList<Node> ls = p.getChildrenUnmodifiable();
			for (var lb : ls) {
				var rd = lb.getOnMouseReleased();
				lb.setOnMouseReleased(e -> {
					rd.handle(e);
					List<String> col_list = new ArrayList<>();
					for (var col : table.getColumns()) {
//					System.out.println(col.getText());
						col_list.add(col.getText());
					}
					colOrder.put(tableName, col_list);
				});
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
	// 设置 tooltip
	public static void setTableHeaderTooltip(FilteredTableView<ResultSetRowPo> table,
			ObservableList<SheetFieldPo> colss) {
		try {
			Node colHeader = table.lookup(".nested-column-header");
			if(colHeader == null ) return ;
			javafx.scene.Parent p = (Parent) colHeader;
			ObservableList<Node> ls = p.getChildrenUnmodifiable();
			for (var lb : ls) {
				Label label = (Label) lb.lookup(".label");
				if (label != null) {
					String name = label.getText();
					if (StrUtils.isNotNullOrEmpty(name)) {
						String typeName = findColType(colss, name);
						var ttip = MyTooltipTool.instance(typeName);
						label.setTooltip(ttip);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String findColType(ObservableList<SheetFieldPo> colss , String name) {
		if(colss != null ) {
			for(var po:colss) {
				String poname = po.getColumnLabel().get();
				if(name.equals(poname)) {
					String tyNa = po.getColumnTypeName().get() + "(" + po.getColumnDisplaySize().get();
					if (po.getScale() != null && po.getScale().get() > 0) {
						tyNa += ", " +po.getScale().get();
					}
					tyNa += ")"; 
					return tyNa;
				}
			}
		}
		return "";
	}
	
	// 重新设置位置
	public static void colReorder(String tableName, ObservableList<SheetFieldPo> colss , FilteredTableView<ResultSetRowPo> table) {
		List<String> tmporder = colOrder.get(tableName);
		
		ObservableList<TableColumn<ResultSetRowPo, ?>> tabcols = table.getColumns(); 
		if(        tmporder !=null 
				&& tmporder.size()>0 
				&& colss.size() > 0 
				&& tabcols.size() > 0
				&& tmporder.size() == colss.size() ) {
			boolean same = true; 

			for(SheetFieldPo col: colss) {
				String colname =col.getColumnLabel().get(); 
				// 看列名是否包含在缓存中的列名列表中
				if( ! tmporder.contains(colname)) {
					same = false;
					break;
				}
			}
			if(same) { 
			    ObservableList<TableColumn<ResultSetRowPo, ?>> tabNewCos = FXCollections.observableArrayList();
			    boolean positionSame = true;
			    for(int i = 0 ; i < tmporder.size(); i++) {
					String colname =  tmporder.get(i);
					
					var colm  = tabcols.get(i);
					var colm_name = colm.getText(); 
					
					if(colm_name.equals( colname)) {
//						System.out.println("位置相同...");
						tabNewCos.add(colm);  
					}else {
						positionSame = false;
						colm =  gettableColumnByName(tabcols , colname );
						tabNewCos.add(colm);
//						System.out.println("重新设置位置为= " + i);
					} 
					
				}
				if( positionSame ) {
					tabNewCos.clear();
				}else {
					tabcols.clear();
					tabcols.setAll(tabNewCos);
				}
			}
		}
	}
}
