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
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SelectDao;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.subwindow.TableDataDetail;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.MyOption;
import net.tenie.Sqlucky.sdk.utility.ParseSQL;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * 公用组件
 * 
 * @author tenie
 *
 */
public class SdkComponent {

	private static Logger logger = LogManager.getLogger(SdkComponent.class);

	private static int windowsUiBugTag = 0;
	 
	
	/**
	 * 锁btn
	 * 
	 * @param mytb
	 * @return
	 */
	public static JFXButton createLockBtn(SqluckyBottomSheet mytb) {
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
				mytb.getTableData().setLock(false);
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

	// 数据展示tableView StringProperty FilteredTableView<ResultSetRowPo>
	public static FilteredTableView<ResultSetRowPo> creatFilteredTableView() {
		FilteredTableView<ResultSetRowPo> table = new FilteredTableView<>();

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

		FilteredTableColumn<ResultSetRowPo, Number> tc = new FilteredTableColumn<>();

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
			TableCell<ResultSetRowPo, Number> cell = new TableCell<>() {
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
	public static FilteredTableColumn<ResultSetRowPo, String> createColumn(String colname, int colIdx) {
		FilteredTableColumn<ResultSetRowPo, String> col = new FilteredTableColumn<>();
		col.setCellFactory(MyTextField2TableCell2.forTableColumn());
		col.setText(colname);
		Label label = new Label();
		col.setGraphic(label);
		// 通过下标从ObservableList 获取对应列显示的字符串值
		col.setCellValueFactory(new ResultSetCellValueFactory(colIdx));
		return col;
	}
	public static ObservableList<FilteredTableColumn<ResultSetRowPo, String>> createTableColForInfo(ObservableList<SheetFieldPo> cols) {
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
	private static FilteredTableColumn<ResultSetRowPo, String> createColumnForShowInfo(String colname,
			int colIdx, Double colnameWidth) {
		FilteredTableColumn<ResultSetRowPo, String> col = SdkComponent.createColumn(colname, colIdx);
		CacheDataTableViewShapeChange.setColWidth(col, colname, colnameWidth);
		return col;
	}
 

	/**
	 * sql 查询结果生成表格
	 * @param sql
	 * @param conn
	 * @param tableName
	 * @param fieldWidthMap
	 * @return
	 */
	public static SheetDataValue sqlToSheet(String sql, Connection conn, String tableName, Map<String, Double> fieldWidthMap) {

		try {
			FilteredTableView<ResultSetRowPo> table = SdkComponent.creatFilteredTableView();
			// 查询的 的语句可以被修改
//			table.editableProperty().bind(new SimpleBooleanProperty(false));
//			table.setEditable(false);

			// 获取表名
			if (tableName == null || "".equals(tableName)) {
				tableName = ParseSQL.tabName(sql);
				if (StrUtils.isNullOrEmpty(tableName)) {
					tableName = "Table Name Not Finded";
				}
			}

			logger.info("tableName= " + tableName + "\n sql = " + sql);
			SheetDataValue sheetDaV = new SheetDataValue();
			sheetDaV.setSqlStr(sql);
			sheetDaV.setTable(table);
			sheetDaV.setTabName(tableName);
			sheetDaV.setLock(false);
			sheetDaV.setConn(conn);

			SelectDao.selectSql(sql, Integer.MAX_VALUE, sheetDaV);

			ObservableList<ResultSetRowPo> allRawData = sheetDaV.getDataRs().getDatas();
			ObservableList<SheetFieldPo> colss = sheetDaV.getColss();

			if(fieldWidthMap != null ) {
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
			var tableColumns = SdkComponent.createTableColForInfo(colss);
			// 设置 列的 右键菜单
			table.getColumns().addAll(tableColumns);
			table.setItems(allRawData);
			
			return sheetDaV;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 数据模型查询字段时， 对展示列宽度做调整
	 * 
	 * @param sql
	 * @param conn
	 * @param tableName
	 * @param optionNodes  按钮等组件的集合
	 * @param fieldWidthMap
	 * @throws Exception
	 */
	public static SheetDataValue dataModelQueryFieldsShow(String sql, Connection conn, String tableName, List<Node> optionNodes,
			Map<String, Double> fieldWidthMap) throws Exception {
		SheetDataValue sheetDaV = null;
		try {
		    sheetDaV = sqlToSheet(sql, conn, tableName, fieldWidthMap);
			// 如果查询到数据才展示
			if(sheetDaV.getTable().getItems().size() > 0) {
				// 渲染界面
				SqluckyBottomSheet mtd = ComponentGetter.appComponent.tableViewSheet(sheetDaV, optionNodes);
				mtd.show();

			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return sheetDaV;
	}

	// 查询时等待画面
	public static Tab maskTab(String waittbName) {
		Tab waitTb = new Tab(waittbName);
		MaskerPane masker = new MaskerPane();
		waitTb.setContent(masker);
		return waitTb;
	}

	public static Tab waitTb;
	private static final String WAITTB_NAME = "Loading...";
	static {
		waitTb = maskTab(WAITTB_NAME);
	}

	// 查询等待
	// 等待加载动画 页面, 删除不要的页面, 保留 锁定的页面, -1表示最后添加
	public static Tab addWaitingPane(int tabIdx) {
		Platform.runLater(() -> {
			TabPane dataTab = ComponentGetter.dataTabPane;
			if (tabIdx > -1) {
				dataTab.getTabs().add(tabIdx, waitTb);
			} else {
				dataTab.getTabs().add(waitTb);
			}
			dataTab.getSelectionModel().select(waitTb);

		});
		return waitTb;
	}

	// 移除 等待加载动画 页面
	public static void rmWaitingPane() {
		Platform.runLater(() -> {
			TabPane dataTab = ComponentGetter.dataTabPane;
			if (dataTab.getTabs().contains(waitTb)) {
				dataTab.getTabs().remove(waitTb);
			}
			
//			CommonUtility.delayRunThread(v->{
//				Platform.runLater(()->{
//					if(dataTab.getTabs().size() == 0) {
//						SdkComponent.hideBottom(); 
//					} 
//				});
//			}, 1000);
//			if (dataTab.getTabs().size() == 0) {
//				SdkComponent.hideBottom();
//			}

		});

	}


	// 删除空白页, 保留锁定页
	public static void deleteEmptyTab(TabPane dataTab) {
		// 判断是否已经到达最大tab显示页面
		// 删除旧的 tab
		List<Tab> ls = new ArrayList<>();
		for(int i = 0; i < dataTab.getTabs().size() ;i++) {
			Tab tab = dataTab.getTabs().get(i);
			MyBottomSheet nd =  (MyBottomSheet) tab.getUserData();
			if(nd == null) continue;
			Boolean tf = nd.getTableData().isLock();
			if(tf != null && tf) {
				logger.info("lock  "  );
			}else {
				ls.add(tab);
			}
		}
		if( ls.size()> 0 ) {
			Platform.runLater(()->{
				ls.forEach(nd->{
					dataTab.getTabs().remove(nd);
					
				});
//				System.gc();
				MyOption.gc(SdkComponent.class, "deleteEmptyTab");
			});
		}
		
	}
	
	// 延迟1秒隐藏
//	public static void dalayHideBottom() {
//		CommonUtility.delayRunThread(v->{
//			Platform.runLater(()->{
//				SdkComponent.hideBottom(); 
//			});
//		}, 1000);
//	}
	
	public static void hideBottom() {
		
		JFXButton btn =   CommonButtons.hideBottom; //   AllButtons.btns.get("hideBottom");
		boolean showStatus = !ComponentGetter.masterDetailPane.showDetailNodeProperty().getValue();
		hideShowBottomHelper(showStatus, btn);
		if(showStatus ) { 
			SdkComponent.escapeWindowsUiBug(); 
		}
	}
	
	public static void hideBottomPane() {
		
		JFXButton btn =   CommonButtons.hideBottom; //   AllButtons.btns.get("hideBottom");
		hideShowBottomHelper(false, btn);
		 
	}
	
	
	//TODO 显示或隐藏 数据面板, 修改控制按钮图标
	public static void hideShowBottomHelper(boolean isShow, JFXButton btn) {
		ComponentGetter.masterDetailPane.setShowDetailNode(isShow);
		if (isShow) {
			btn.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-down"));
		} else {
			btn.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-up"));
		}

	}

	// 底部数据展示面板是否显示
	public static void showDetailPane() {
		JFXButton btn =    CommonButtons.hideBottom; //  AllButtons.btns.get("hideBottom");
		boolean showStatus = !ComponentGetter.masterDetailPane.showDetailNodeProperty().getValue();
		if (showStatus) {
			hideShowBottomHelper(true, btn); 
			escapeWindowsUiBug();
		}
	}
	

	// 避免windows UI bug, 选择一下输入框
	public static void escapeWindowsUiBug() {
		if( windowsUiBugTag == 0 ){
			windowsUiBugTag = 1;
			
			Thread th = new Thread() {
				public void run() {
					try {
						Thread.sleep(700);
						Platform.runLater(() -> {
							ComponentGetter.maxRowsTextField.requestFocus();
						 
						});
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
			};
			th.start();
			
		    th = new Thread() {
				public void run() {
					try {
						Thread.sleep(900);
						Platform.runLater(() -> {
						    SqluckyEditor.getCodeArea().requestFocus(); 
						 
						});
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
				}
			};
			th.start();
		}
		
		
	}

	public static void clearDataTable(int tbIdx) {
		TabPane tabPane = ComponentGetter.dataTabPane; 
		var tb = tabPane.getTabs().get(tbIdx);
		long begintime = System.currentTimeMillis();
		tb.setContent(null); 
		tabPane.getTabs().remove(tb);
		long endtime = System.currentTimeMillis();
		long costTime = (endtime - begintime);
		logger.info("关闭使用时间 = "+ costTime);
		
//		CommonUtility.delayRunThread(v->{
//			Platform.runLater(()->{
//				if(tabPane.getTabs().size() == 0) {
//					SdkComponent.hideBottom(); 
//				} 
//			});
//		}, 1000);
		
		 
	}
	// 关闭 数据页, 清理缓存
	public static void clearDataTable(Tab tb) {
		TabPane tabPane = ComponentGetter.dataTabPane; 
		long begintime = System.currentTimeMillis();
		tb.setContent(null); 
		tabPane.getTabs().remove(tb);
		long endtime = System.currentTimeMillis();
		long costTime = (endtime - begintime);
//		System.gc();
		MyOption.gc(SdkComponent.class, "clearDataTable");
		logger.info("关闭使用时间 = "+ costTime);
//		CommonUtility.delayRunThread(v->{
//			Platform.runLater(()->{
//				if(tabPane.getTabs().size() == 0) {
//					SdkComponent.hideBottom(); 
//				} 
//			});
//		}, 200);
		
	}
	
	/**
	 * 数据table关闭的时候 
	 */
	public static EventHandler<Event> dataTabCloseReq( MyBottomSheet tb) {
		return new EventHandler<Event>() {
			public void handle(Event e) { 
				SdkComponent.clearDataTable( tb.getTab());
//				tb.getTableData().clean();
				
				tb.clean();
				
				 
			}
		};
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
