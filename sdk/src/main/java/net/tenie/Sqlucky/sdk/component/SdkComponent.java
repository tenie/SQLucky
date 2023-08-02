package net.tenie.Sqlucky.sdk.component;

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SelectDao;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SelectExecInfo;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.subwindow.TableDataDetail;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.MyOption;
import net.tenie.Sqlucky.sdk.utility.ParseSQL;
import net.tenie.Sqlucky.sdk.utility.SqluckyEditorUtils;
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
//	public static JFXButton createLockBtn(MyBottomSheet mytb) {
//		// 锁
//		JFXButton lockbtn = new JFXButton();
//		if (mytb.getTableData().isLock()) {
//			lockbtn.setGraphic(IconGenerator.svgImageDefActive("lock"));
//		} else {
//			lockbtn.setGraphic(IconGenerator.svgImageDefActive("unlock"));
//		}
//		lockbtn.setOnMouseClicked(e -> {
//			if (mytb.getTableData().isLock()) {
//				lockbtn.setGraphic(IconGenerator.svgImageDefActive("unlock"));
//				mytb.getTableData().setLock(false);
//			} else {
//				lockbtn.setGraphic(IconGenerator.svgImageDefActive("lock"));
//				mytb.getTableData().setLock(true);
//			}
//
//		});
//
//		return lockbtn;
//	}

	public static String createTabId() {
		int tableIdx = ConfigVal.tableIdx++;
		return tableIdx + "";
	}

	// 数据展示tableView StringProperty FilteredTableView<ResultSetRowPo>
	public static FilteredTableView<ResultSetRowPo> creatFilteredTableView(MyBottomSheet myBottomSheet) {
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
								TableDataDetail.show(myBottomSheet);
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
		col.setCellFactory(MyTableCellTextField2.forTableColumn());
		col.setText(colname);
		Label label = new Label();
		col.setGraphic(label);
		// 通过下标从ObservableList 获取对应列显示的字符串值
		col.setCellValueFactory(new ResultSetCellValueFactory(colIdx));
		return col;
	}

	/**
	 * 根据字段创建所有列
	 * 
	 * @param cols
	 * @return
	 */
	public static ObservableList<FilteredTableColumn<ResultSetRowPo, String>> createTableColForInfo(
			ObservableList<SheetFieldPo> cols) {
		return createTableColForInfo(cols, null);
	}

	/**
	 * 根据字段创建所有列, 通过editableColName 设置可以编辑的列
	 * 
	 * @param cols
	 * @param editableColName
	 * @return
	 */
	public static ObservableList<FilteredTableColumn<ResultSetRowPo, String>> createTableColForInfo(
			ObservableList<SheetFieldPo> cols, List<String> editableColName) {
		int len = cols.size();
		ObservableList<FilteredTableColumn<ResultSetRowPo, String>> colList = FXCollections.observableArrayList();
		for (int i = 0; i < len; i++) {
			String colname = cols.get(i).getColumnLabel().get();
			Double colnameWidth = cols.get(i).getColumnWidth();
			FilteredTableColumn<ResultSetRowPo, String> col = null;
			// isInfo 展示执行信息（错误/成功的信息)
			col = createColumnForShowInfo(colname, i, colnameWidth);

			// 如果有启用编辑列, 那么对非编辑的列设值不可编辑
			if (editableColName != null && editableColName.size() > 0) {
				if (editableColName.contains(colname)) {
					col.setEditable(true);
				} else {
					col.setEditable(false);
				}
			}

			colList.add(col);
		}

		return colList;
	}

	/**
	 * 创建列
	 */
	private static FilteredTableColumn<ResultSetRowPo, String> createColumnForShowInfo(String colname, int colIdx,
			Double colnameWidth) {
		FilteredTableColumn<ResultSetRowPo, String> col = SdkComponent.createColumn(colname, colIdx);
		CacheDataTableViewShapeChange.setColWidth(col, colname, colnameWidth);
		return col;
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
	public static MyBottomSheet sqlToSheet(String sql, SqluckyConnector sqluckyConn, String tableName,
			Map<String, Double> fieldWidthMap, List<String> editableColName) {

		try {
			MyBottomSheet myBottomSheet = new MyBottomSheet(tableName);
			SheetDataValue sheetDaV = myBottomSheet.getTableData();// new SheetDataValue();
			FilteredTableView<ResultSetRowPo> table = sheetDaV.getTable();
//			FilteredTableView<ResultSetRowPo> table = SdkComponent.creatFilteredTableView(myBottomSheet);
			// 查询的 的语句可以被修改
//			table.editableProperty().bind(new SimpleBooleanProperty(false));
			table.setEditable(true);

			// 获取表名
			if (tableName == null || "".equals(tableName)) {
				tableName = ParseSQL.tabName(sql);
				if (StrUtils.isNullOrEmpty(tableName)) {
					tableName = "Table Name Not Finded";
				}
			}

			logger.info("tableName= " + tableName + "\n sql = " + sql);

			sheetDaV.setSqlStr(sql);
//			sheetDaV.setTable(table);
			sheetDaV.setTabName(tableName);
			sheetDaV.setLock(false);
			sheetDaV.setConn(sqluckyConn.getConn());

			SelectExecInfo execInfo = SelectDao.selectSql2(sql, Integer.MAX_VALUE, sqluckyConn);

			sheetDaV.setSelectExecInfo(execInfo);
			ObservableList<ResultSetRowPo> allRawData = sheetDaV.getDataRs().getDatas();
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
			var tableColumns = SdkComponent.createTableColForInfo(colss, editableColName);
			// 设置 列的 右键菜单
			table.getColumns().addAll(tableColumns);
			table.setItems(allRawData);
			// 表格选中事件, 对表格中的字段添加修改监听
			table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
				//
				if (newValue != null) {
//					List<Button> btns = null;
//					if (sheetDaV.getBtnMap() != null) {
//						var btnNodes = sheetDaV.getBtnMap().values();
//						btns = new ArrayList<>();
//						btns.addAll(btnNodes);
//					}
					newValue.cellAddChangeListener();// btns
				}
			});

			return myBottomSheet;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
		List<MyBottomSheet> ls2 = new ArrayList<>();
		for (int i = 0; i < dataTab.getTabs().size(); i++) {
			Tab tab = dataTab.getTabs().get(i);
			MyBottomSheet nd = (MyBottomSheet) tab.getUserData();
			if (nd == null)
				continue;
			Boolean tf = nd.getTableData().isLock();
			if (tf != null && tf) {
				logger.info("lock  ");
			} else {
				ls.add(tab);
				ls2.add(nd);
			}
		}

		if (ls.size() > 0) {
			Platform.runLater(() -> {
				ls.forEach(nd -> {
					nd.setUserData(null);
					nd.setContent(null);
					dataTab.getTabs().remove(nd);
				});
//				System.gc();
				ls.clear();
//				MyOption.gc(SdkComponent.class, "deleteEmptyTab");

			});
		}

		if (ls2.size() > 0) {
			Platform.runLater(() -> {
				ls2.forEach(nd -> {
					nd.clean();
				});
				ls2.clear();
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

		JFXButton btn = CommonButtons.hideBottom; // AllButtons.btns.get("hideBottom");
		boolean showStatus = !ComponentGetter.masterDetailPane.showDetailNodeProperty().getValue();
		hideShowBottomHelper(showStatus, btn);
		if (showStatus) {
			SdkComponent.escapeWindowsUiBug();
		}
	}

	public static void hideBottomPane() {

		JFXButton btn = CommonButtons.hideBottom; // AllButtons.btns.get("hideBottom");
		hideShowBottomHelper(false, btn);

	}

	// TODO 显示或隐藏 数据面板, 修改控制按钮图标
	public static void hideShowBottomHelper(boolean isShow, JFXButton btn) {
		if (isShow) {
			btn.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-down"));
			double val = ComponentGetter.masterDetailPane.getDividerPosition();
			if (val > 0.85) {
				ComponentGetter.masterDetailPane.setDividerPosition(0.6);
			}
			ComponentGetter.masterDetailPane.setShowDetailNode(isShow);
		} else {
			btn.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-up"));
			ComponentGetter.masterDetailPane.setShowDetailNode(isShow);
		}

	}

	// 底部数据展示面板是否显示
	public static void showDetailPane() {
		JFXButton btn = CommonButtons.hideBottom; // AllButtons.btns.get("hideBottom");
		boolean showStatus = !ComponentGetter.masterDetailPane.showDetailNodeProperty().getValue();
		if (showStatus) {
			hideShowBottomHelper(true, btn);
			escapeWindowsUiBug();
		}
	}

	// 避免windows UI bug, 选择一下输入框
	public static void escapeWindowsUiBug() {
		if (windowsUiBugTag == 0) {
			windowsUiBugTag = 1;

			Thread th = new Thread() {
				@Override
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
				@Override
				public void run() {
					try {
						Thread.sleep(900);
						Platform.runLater(() -> {
							SqluckyEditorUtils.getCodeArea().requestFocus();

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
		logger.info("关闭使用时间 = " + costTime);

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
		if (!tabPane.getTabs().contains(tb)) {
			return;
		}
		long begintime = System.currentTimeMillis();
		tb.setContent(null);
		tb.setUserData(null);
		tabPane.getTabs().remove(tb);
		long endtime = System.currentTimeMillis();
		long costTime = (endtime - begintime);
		MyOption.gc(SdkComponent.class, "clearDataTable");
		logger.info("关闭使用时间 = " + costTime);

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
	public static EventHandler<Event> dataTabCloseReq(MyBottomSheet tb) {
		return new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				SdkComponent.clearDataTable(tb.getTab());
				tb.clean();

			}
		};
	}

	// 创建一个表
	// 数据展示tableView StringProperty
//	public static FilteredTableView<ObservableList<StringProperty>> creatFilteredTableView2() {
//		FilteredTableView<ObservableList<StringProperty>> table = new FilteredTableView<ObservableList<StringProperty>>();
//
//		table.rowHeaderVisibleProperty().bind(new SimpleBooleanProperty(true));
//		table.setPlaceholder(new Label());
//		// 可以选中多行
//		table.getSelectionModel().selectionModeProperty().bind(Bindings.when(new SimpleBooleanProperty(true))
//				.then(SelectionMode.MULTIPLE).otherwise(SelectionMode.SINGLE));
//
//		String tableIdx = createTabId();
//		table.setId(tableIdx);
//		table.getStyleClass().add("myTableTag");
//
//		FilteredTableColumn<ObservableList<StringProperty>, Number> tc = new FilteredTableColumn<>();
//		// 点击 行号, 显示一个 当前行的明细窗口
//		tc.setCellFactory(col -> {
//			TableCell<ObservableList<StringProperty>, Number> cell = new TableCell<ObservableList<StringProperty>, Number>() {
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
//
//		table.setRowHeader(tc);
//		// 启用 隐藏列的控制按钮
//		table.tableMenuButtonVisibleProperty().setValue(true);
//
//		return table;
//	}
}
