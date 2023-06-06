package net.tenie.Sqlucky.sdk.utility;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.tenie.Sqlucky.sdk.component.MyCellOperateButton;
import net.tenie.Sqlucky.sdk.component.MyTableCellButton;
import net.tenie.Sqlucky.sdk.component.MyTableCellTextField3;
import net.tenie.Sqlucky.sdk.component.ResultSetCellValueFactory;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SelectInfoTableDao;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.SheetTableData;
import net.tenie.Sqlucky.sdk.subwindow.TableDataDetail;

/**
 * 公用组件
 * 
 * @author tenie
 *
 */
public class TableViewUtil {

	private static Logger logger = LogManager.getLogger(TableViewUtil.class);

	public static String createTabId() {
		int tableIdx = ConfigVal.tableIdx++;
		return tableIdx + "";
	}

	/**
	 * sql 查询结果生成表格
	 * 
	 * @param sql
	 * @param conn
	 * @param tableName
	 * @param fieldWidthMap
	 * @return
	 */
	public static SheetTableData sqlToSheet(String sql, Connection conn, String tableName,
			Map<String, Double> fieldWidthMap ) {

		 return sqlToSheet(sql, conn, tableName, fieldWidthMap, null);
	}
	

	/**
	 * sql 查询结果生成表格
	 * 
	 * @param sql
	 * @param conn
	 * @param tableName
	 * @param fieldWidthMap
	 * @return
	 */
	public static SheetTableData sqlToSheet(String sql, Connection conn, String tableName,
			Map<String, Double> fieldWidthMap ,  List<String> hiddenCol) {

		try {
			FilteredTableView<ResultSetRowPo> table = TableViewUtil.creatInfoTableView();
			// 查询的 的语句可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(true));

			// 获取表名
			if (tableName == null || "".equals(tableName)) {
				tableName = ParseSQL.tabName(sql);
				if (StrUtils.isNullOrEmpty(tableName)) {
					tableName = "Table Name Not Finded";
				}
			}

			logger.info("tableName= " + tableName + "\n sql = " + sql);
			SheetTableData sheetDaV = new SheetTableData();
			sheetDaV.setSqlStr(sql);
			sheetDaV.setInfoTable(table);
			sheetDaV.setTabName(tableName);
			sheetDaV.setLock(false);
			sheetDaV.setConn(conn);

			SelectInfoTableDao.selectSql(sql, sheetDaV);

			ObservableList<ResultSetRowPo> allRawData = sheetDaV.getInfoTableVals().getDatas();
			ObservableList<SheetFieldPo> colss = sheetDaV.getColss();

			if (fieldWidthMap != null) {
				// 给字段设置显示宽度
				for (var sfpo : colss) {
					Double val = fieldWidthMap.get(sfpo.getColumnLabel().getValue());
					if (val != null) {
						sfpo.setColumnWidth(val);
					}
				}
			}

			// table 添加列和数据
			// 表格添加列
			var tableColumns = TableViewUtil.createTableColForInfo(colss);
			
			
			// 设置隐藏列
			if(hiddenCol != null && hiddenCol.size() >0) {
				for(var column : tableColumns) {
					String colName = column.getText();
					if(hiddenCol.contains(colName)) {
						column.setVisible(false);
					}
				}
			}
			// 设置 列的 右键菜单
			table.getColumns().addAll(tableColumns);
			table.setItems(allRawData);

			return sheetDaV;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	


	// 数据展示tableView StringProperty
	public static FilteredTableView<ResultSetRowPo> creatInfoTableView() {
		FilteredTableView<ResultSetRowPo> table = new FilteredTableView<>();

		table.rowHeaderVisibleProperty().bind(new SimpleBooleanProperty(true));
		table.setPlaceholder(new Label());
		// 可以选中多行
		table.getSelectionModel().selectionModeProperty().bind(Bindings.when(new SimpleBooleanProperty(true))
				.then(SelectionMode.MULTIPLE).otherwise(SelectionMode.SINGLE));

		String tableIdx = createTabId();
		table.setId(tableIdx);
		table.getStyleClass().add("myTableTag");

		// 启用 隐藏列的控制按钮
		table.tableMenuButtonVisibleProperty().setValue(true);

		return table;
	}

	// 创建列
	public static FilteredTableColumn<ResultSetRowPo, String> createColumn(String colname, int colIdx) {
		FilteredTableColumn<ResultSetRowPo, String> col = new FilteredTableColumn<>();
		col.setCellFactory(MyTableCellTextField3.forTableColumn());
		col.setText(colname);
		Label label = new Label();
		col.setGraphic(label);
		// 通过下标从ObservableList 获取对应列显示的字符串值

		col.setCellValueFactory(new ResultSetCellValueFactory(colIdx)); // new ResultSetCellValueFactory(colIdx)
		return col;
	}

	public static ObservableList<FilteredTableColumn<ResultSetRowPo, String>> createTableColForInfo(
			ObservableList<SheetFieldPo> cols) {
		int len = cols.size();
		ObservableList<FilteredTableColumn<ResultSetRowPo, String>> colList = FXCollections.observableArrayList();
		for (int i = 0; i < len; i++) {
			String colname = cols.get(i).getColumnLabel().get();
			Double colnameWidth = cols.get(i).getColumnWidth();
			FilteredTableColumn<ResultSetRowPo, String> col = null;
			// isInfo 展示执行信息 
			col = createColumnForShowInfo(colname, i, colnameWidth);
			colList.add(col);
		}

		return colList;
	}

	/**
	 * 创建列
	 */
	private static FilteredTableColumn<ResultSetRowPo, String> createColumnForShowInfo(String colname, int colIdx,
			Double colnameWidth) {
		FilteredTableColumn<ResultSetRowPo, String> col = TableViewUtil.createColumn(colname, colIdx);
		setColWidth(col, colname, colnameWidth);
		return col;
	}

	static public void setColWidth(FilteredTableColumn<ResultSetRowPo, String> col, String colname, Double cusWidth) {
		// 设置列的长度
		Double width;
		if (cusWidth != null) {
			width = cusWidth;
		} else {
			width = (colname.length() * 10.0) + 15;
			if (width < 90)
				width = 100.0;
		}

		col.setMinWidth(width);
		col.setPrefWidth(width);

	}

	// 创建一个表
	// 数据展示tableView StringProperty
	public static FilteredTableView<ObservableList<StringProperty>> creatFilteredTableView2() {
		FilteredTableView<ObservableList<StringProperty>> table = new FilteredTableView<ObservableList<StringProperty>>();

		table.rowHeaderVisibleProperty().bind(new SimpleBooleanProperty(true));
		table.setPlaceholder(new Label());
		// 可以选中多行
		table.getSelectionModel().selectionModeProperty().bind(Bindings.when(new SimpleBooleanProperty(true))
				.then(SelectionMode.MULTIPLE).otherwise(SelectionMode.SINGLE));

		String tableIdx = createTabId();
		table.setId(tableIdx);
		table.getStyleClass().add("myTableTag");

		FilteredTableColumn<ObservableList<StringProperty>, Number> tc = new FilteredTableColumn<>();
		// 点击 行号, 显示一个 当前行的明细窗口
		tc.setCellFactory(col -> {
			TableCell<ObservableList<StringProperty>, Number> cell = new TableCell<ObservableList<StringProperty>, Number>() {
				@Override
				public void updateItem(Number item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					this.setGraphic(null);
					if (!empty) {
						int rowIndex = this.getIndex();
						this.setText((rowIndex + 1) + "");
						this.setOnMouseClicked(e -> {
							if (e.getClickCount() == 2) {
								TableDataDetail.show();
							}
						});
					}
				}
			};
			return cell;
		});

		table.setRowHeader(tc);
		// 启用 隐藏列的控制按钮
		table.tableMenuButtonVisibleProperty().setValue(true);

		return table;
	}
	
	
	// 提供数据生成表格
	public static SheetTableData dataToSheet(List<String> fieldNameLs ,
			List<Map<String, String>> vals , List<String> hiddenCol) {
		try {
			FilteredTableView<ResultSetRowPo> table = TableViewUtil.creatInfoTableView();
			// 查询的 的语句可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(true));
			SheetTableData sheetDaV = new SheetTableData();
			sheetDaV.setSqlStr("");
			sheetDaV.setInfoTable(table);
			sheetDaV.setTabName("");
			sheetDaV.setLock(false);

			ObservableList<SheetFieldPo> fields = createSheetFieldPo(fieldNameLs);
			ResultSetPo setPo = fetchCellVal(vals, fields);
			sheetDaV.setColss(fields);
			sheetDaV.setInfoTableVals(setPo);

			ObservableList<ResultSetRowPo> allRawData = sheetDaV.getInfoTableVals().getDatas();
			ObservableList<SheetFieldPo> colss = sheetDaV.getColss();

			// table 添加列和数据
			// 表格添加列
			ObservableList<FilteredTableColumn<ResultSetRowPo, String>> tableColumns 
				= TableViewUtil.createTableColForInfo(colss);
			// 设置隐藏列
			if(hiddenCol != null && hiddenCol.size() >0) {
				for(var column : tableColumns) {
					String colName = column.getText();
					if(hiddenCol.contains(colName)) {
						column.setVisible(false);
					}
				}
			}
			
			// 设置 列的  
			table.getColumns().addAll(tableColumns);
			table.setItems(allRawData);

			return sheetDaV;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// 生成表格需要的字段信息
	public static ObservableList<SheetFieldPo> createSheetFieldPo(List<String> fieldNameLs ) {
		ObservableList<SheetFieldPo> fields = FXCollections.observableArrayList();
		for(String name : fieldNameLs){
			SheetFieldPo po = new SheetFieldPo();
			po.setColumnLabel(name);
			fields.add(po);
		} 
		return fields;
	}
	
	//将数据转换为cell 
	public static ResultSetPo fetchCellVal(  List<Map<String, String>> vals,
			ObservableList<SheetFieldPo> fpo 
			) throws SQLException {
		ResultSetPo setPo = new ResultSetPo(fpo);
		int columnnums = fpo.size(); 
		for (Map<String, String> map : vals) {
			ResultSetRowPo rowpo = setPo.creatRow();
			for (int i = 0; i < columnnums; i++) {
				SheetFieldPo fieldpo = fpo.get(i); 
				StringProperty val;

				String cellVal = map.get(fieldpo.getColumnLabel().get());
				if(cellVal != null ) {
					val = new SimpleStringProperty(cellVal);
				}else {
					val = new SimpleStringProperty("");
				}
				
				rowpo.addCell(val, null, fieldpo);
			}
		}
		return setPo;
	}


	// Show db Table index foregin key TableView
	public static TableView<ResultSetRowPo>  dbTableIndexFkTableView(List<String> fieldNameLs ,
			List<Map<String, String>> vals ,List<MyCellOperateButton> btnvals ) {
		try {
			FilteredTableView<ResultSetRowPo> tableView = TableViewUtil.creatInfoTableView();
			// 查询的 的语句可以被修改
			tableView.editableProperty().bind(new SimpleBooleanProperty(true));

			ObservableList<SheetFieldPo> fields = createSheetFieldPo(fieldNameLs);
			ResultSetPo setPo = fetchCellVal(vals, fields);
			

			ObservableList<ResultSetRowPo> allRawData = setPo.getDatas(); //sheetDaV.getInfoTableVals().getDatas();
			// table 添加列和数据
			// 表格添加列
			ObservableList<FilteredTableColumn<ResultSetRowPo, String>> tableColumns 
				= TableViewUtil.createTableColForInfo(fields);
			FilteredTableColumn<ResultSetRowPo, String> column = tableColumns.get(tableColumns.size()-1);
		
			MyTableCellButton  btncell = new MyTableCellButton( btnvals);
			column.setCellFactory(btncell.callback());
			// 设置 列的  
			tableView.getColumns().addAll(tableColumns);
			tableView.setItems(allRawData);

			return tableView;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
