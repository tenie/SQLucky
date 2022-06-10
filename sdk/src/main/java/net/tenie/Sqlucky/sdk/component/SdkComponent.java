package net.tenie.Sqlucky.sdk.component;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;

import com.jfoenix.controls.JFXButton;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SelectDao;
import net.tenie.Sqlucky.sdk.po.BottomSheetDataValue;
import net.tenie.Sqlucky.sdk.po.SqlFieldPo;
import net.tenie.Sqlucky.sdk.subwindow.TableDataDetail;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.ParseSQL;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
/**
 * 公用组件
 * @author tenie
 *
 */
public class SdkComponent {

	private static Logger logger = LogManager.getLogger(SdkComponent.class);
	
	/**
	 * 锁btn
	 * @param mytb
	 * @return
	 */
	public static  JFXButton createLockBtn(SqluckyBottomSheet mytb ) {
		// 锁
		JFXButton lockbtn = new JFXButton();
		if (mytb.getTableData().isLock()) {
			lockbtn.setGraphic(IconGenerator.svgImageDefActive("lock"));
		} else {
			lockbtn.setGraphic(IconGenerator.svgImageDefActive("unlock"));
		}
		lockbtn.setOnMouseClicked(e -> {
			if (mytb.getTableData().isLock()) {
				lockbtn.setGraphic(IconGenerator.svgImageDefActive("unlock"));
				mytb.getTableData().setLock(true);
			} else {
				lockbtn.setGraphic(IconGenerator.svgImageDefActive("lock"));
				mytb.getTableData().setLock(true);
			}

		});

		return lockbtn;
	}

	
	public static String createTabId() {
		int tableIdx = ConfigVal.tableIdx++;
		return tableIdx + "";
	}

	// 数据展示tableView StringProperty
	public static FilteredTableView<ObservableList<StringProperty>> creatFilteredTableView() {
		FilteredTableView<ObservableList<StringProperty>> table = new FilteredTableView<ObservableList<StringProperty>>();

		table.rowHeaderVisibleProperty().bind(new SimpleBooleanProperty(true));
		table.setPlaceholder(new Label());
		// 可以选中多行
		table.getSelectionModel().selectionModeProperty().bind(Bindings.when(new SimpleBooleanProperty(true))
				.then(SelectionMode.MULTIPLE).otherwise(SelectionMode.SINGLE));

		// 选中监听
//			ListChangeListener<ObservableList<StringProperty>> indicesListener = new ListChangeListener<ObservableList<StringProperty>>() {
//				@Override
//				public void onChanged(Change<? extends ObservableList<StringProperty>> c) {
//					while (c.next()) {
		//
//					}
//				}
//			};
//			table.getSelectionModel().getSelectedItems().addListener(indicesListener);

		String tableIdx = createTabId();
		table.setId(tableIdx);
		table.getStyleClass().add("myTableTag");

		FilteredTableColumn<ObservableList<StringProperty>, Number> tc = new FilteredTableColumn<>();

//			tc.setCellValueFactory(cal -> { 
//				ObservableList<StringProperty> obs = cal.getValue(); 
//				int sz = obs.size();
//				StringProperty sp = obs.get(sz - 1); 
//				IntegerProperty sum = new SimpleIntegerProperty(); 
//				sum.setValue(Integer.valueOf(sp.get()) + 1);
//				return sum;
//			});

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

	// 创建列
	/**
	 * @param colname
	 * @param type
	 * @param typeName
	 * @param colIdx
	 * @param augmentation
	 * @param iskey
	 * @param isInfo
	 * @param dvt
	 * @return
	 */
	public static FilteredTableColumn<ObservableList<StringProperty>, String> createColumn(String colname, int colIdx) {
		FilteredTableColumn<ObservableList<StringProperty>, String> col = new FilteredTableColumn<ObservableList<StringProperty>, String>();
		col.setCellFactory(MyTextField2TableCell2.forTableColumn());
		col.setText(colname);
		Label label = new Label();
		col.setGraphic(label);
		// 通过下标从ObservableList 获取对应列显示的字符串值
		col.setCellValueFactory(new StringPropertyListValueFactory(colIdx));
		return col;
	}
	
	public static ObservableList<FilteredTableColumn<ObservableList<StringProperty>, String>> createTableColForInfo( ObservableList<SqlFieldPo> cols) {
		int len = cols.size();
		ObservableList<FilteredTableColumn<ObservableList<StringProperty>, String>> colList = FXCollections.observableArrayList();
		for (int i = 0; i < len; i++) {
			String colname = cols.get(i).getColumnLabel().get();
			Double colnameWidth = cols.get(i).getColumnWidth();
			FilteredTableColumn<ObservableList<StringProperty>, String> col = null;
			// isInfo 展示执行信息（错误/成功的信息) 
			col = createColumnForShowInfo(colname, i, colnameWidth);
			colList.add(col);
		}
		
		return colList;
	}
	/**创建列
	 * @param colname
	 * @param type
	 * @param typeName
	 * @param colIdx
	 * @param augmentation
	 * @param iskey
	 * @param isInfo
	 * @param dvt
	 * @return
	 */
	private static FilteredTableColumn<ObservableList<StringProperty>, String> createColumnForShowInfo(String colname, int colIdx, Double colnameWidth ) {
		FilteredTableColumn<ObservableList<StringProperty>, String> col = SdkComponent.createColumn(colname, colIdx);
		CacheDataTableViewShapeChange.setColWidth(col, colname, colnameWidth);
		return col;
	}
	
	
	public static void sqlResultShow(String sql,  Connection conn, String tableName, List<Node> nodeList) throws Exception {
		try { 
//		    Connection conn = dpo.getConn();
			FilteredTableView<ObservableList<StringProperty>> table = SdkComponent.creatFilteredTableView();
			// 查询的 的语句可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(false));
			
		    // 获取表名
			if(tableName == null || "".equals(tableName)) {
				tableName = ParseSQL.tabName(sql);
				if(StrUtils.isNullOrEmpty(tableName)) {
					tableName = "Table Name Not Finded";
				}
			}
			
			logger.info("tableName= " + tableName + "\n sql = " + sql);
			BottomSheetDataValue sheetDaV = new BottomSheetDataValue();
//			sheetDaV.setDbConnection(dpo); 
//			String connectName = DBConns.getCurrentConnectName();
			sheetDaV.setSqlStr(sql);
			sheetDaV.setTable(table);
			sheetDaV.setTabName(tableName);
//			sheetDaV.setConnName(connectName);
			sheetDaV.setLock(false);
			sheetDaV.setConn(conn);

			SelectDao.selectSql(sql, ConfigVal.MaxRows, sheetDaV); 
			
			ObservableList<ObservableList<StringProperty>> allRawData = sheetDaV.getRawData();
			ObservableList<SqlFieldPo> colss = sheetDaV.getColss();
			   
			
			// table 添加列和数据 
			// 表格添加列
			var tableColumns = SdkComponent.createTableColForInfo( colss ); 
			// 设置 列的 右键菜单
//			setDataTableContextMenu(tableColumns, colss);
			table.getColumns().addAll(tableColumns);
			table.setItems(allRawData);  

			
			// 渲染界面
		 
			SqluckyBottomSheet mtd = ComponentGetter.appComponent.tableViewSheet(sheetDaV, nodeList);
//			rmWaitingPane();
			mtd.show();
			 
		} catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}
	}
	
	
	/**
	 * 数据模型查询字段时， 对展示列宽度做调整
	 * @param sql
	 * @param conn
	 * @param tableName
	 * @param nodeList
	 * @param fieldWidthMap
	 * @throws Exception
	 */
	public static void dataModelQueryFieldsShow(String sql,  Connection conn, String tableName, List<Node> nodeList, Map<String, Double> fieldWidthMap) throws Exception {
		try { 
			FilteredTableView<ObservableList<StringProperty>> table = SdkComponent.creatFilteredTableView();
			// 查询的 的语句可以被修改
			table.editableProperty().bind(new SimpleBooleanProperty(false)); 
			
		    // 获取表名
			if(tableName == null || "".equals(tableName)) {
				tableName = ParseSQL.tabName(sql);
				if(StrUtils.isNullOrEmpty(tableName)) {
					tableName = "Table Name Not Finded";
				}
			}
			
			logger.info("tableName= " + tableName + "\n sql = " + sql);
			BottomSheetDataValue sheetDaV = new BottomSheetDataValue();
			sheetDaV.setSqlStr(sql);
			sheetDaV.setTable(table);
			sheetDaV.setTabName(tableName);
			sheetDaV.setLock(false);
			sheetDaV.setConn(conn);

			SelectDao.selectSql(sql, ConfigVal.MaxRows, sheetDaV); 
			
			ObservableList<ObservableList<StringProperty>> allRawData = sheetDaV.getRawData();
			ObservableList<SqlFieldPo> colss = sheetDaV.getColss();
			   
			// 给字段设置显示宽度
			for(var sfpo : colss) {
				Double val = fieldWidthMap.get(sfpo.getColumnLabel().getValue());
				if(val != null) {
					sfpo.setColumnWidth(val);
				}
			}
			
			// table 添加列和数据 
			// 表格添加列
			var tableColumns = SdkComponent.createTableColForInfo( colss ); 
			// 设置 列的 右键菜单
//			setDataTableContextMenu(tableColumns, colss);
			table.getColumns().addAll(tableColumns);
			table.setItems(allRawData);  

			
			// 渲染界面
		 
			SqluckyBottomSheet mtd = ComponentGetter.appComponent.tableViewSheet(sheetDaV, nodeList);
			mtd.show();
			 
		} catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}
	}
	
}
