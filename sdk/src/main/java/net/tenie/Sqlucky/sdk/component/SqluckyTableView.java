package net.tenie.Sqlucky.sdk.component;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SelectDao;
import net.tenie.Sqlucky.sdk.db.SelectInfoTableDao;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.SheetTableData;
import net.tenie.Sqlucky.sdk.subwindow.TableDataDetail;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.ParseSQL;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * 公用组件
 * 
 * @author tenie
 *
 */
public class SqluckyTableView {

	private static Logger logger = LogManager.getLogger(SqluckyTableView.class);

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
			Map<String, Double> fieldWidthMap) {

		try {
			FilteredTableView<ResultSetRowPo> table = SqluckyTableView.creatInfoTableView();
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
			var tableColumns = SqluckyTableView.createTableColForInfo(colss);
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

//		FilteredTableColumn<ResultSetRowPo, Number> tc = new FilteredTableColumn<>();

		// 点击 行号, 显示一个 当前行的明细窗口
//		tc.setCellFactory(col -> {
//			TableCell<ResultSetRowPo, Number> cell = new TableCell<>() {
//				@Override
//				public void updateItem(Number item, boolean empty) {
//					super.updateItem(item, empty);
//					this.setText(null);
//					this.setGraphic(null);
//					if (!empty) {
//						int rowIndex = this.getIndex();
//						this.setText((rowIndex + 1) + "");
//						this.setOnMouseClicked(e -> {
//							if (e.getClickCount() == 2) {
//								TableDataDetail.show();
//							}
//						});
//					}
//				}
//			};
//			return cell;
//		});

//		table.setRowHeader(tc);
		// 启用 隐藏列的控制按钮
		table.tableMenuButtonVisibleProperty().setValue(true);

		return table;
	}

	// 创建列
	public static FilteredTableColumn<ResultSetRowPo, String> createColumn(String colname, int colIdx) {
		FilteredTableColumn<ResultSetRowPo, String> col = new FilteredTableColumn<>();
		col.setCellFactory(MyTextField2TableCell3.forTableColumn());
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
			// isInfo 展示执行信息（错误/成功的信息)
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
		FilteredTableColumn<ResultSetRowPo, String> col = SqluckyTableView.createColumn(colname, colIdx);
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
}
