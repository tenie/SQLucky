package net.tenie.fx.component.dataView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.tableview2.FilteredTableView;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SqlFieldPo;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.RsVal;
import net.tenie.fx.component.CodeArea.HighLightingCodeArea;

public class MyTabData extends Tab {
	private MyTabDataValue tableData;
	public HighLightingCodeArea sqlArea;
	private boolean isDDL = false;
	private Button saveBtn;
	private Button detailBtn;
	private int idx;
	 

	public MyTabData(MyTabDataValue data, int idx, boolean disable) {
		this(data.getTabName());
		this.tableData = data;
		this.idx = idx;

	}

	private MyTabData(String tabName) {
		super(tabName);
		this.setOnCloseRequest(CommonEventHandler.dataTabCloseReq(this));
		this.setContextMenu(tableViewMenu());
		if (tableData == null) {
			tableData = new MyTabDataValue();
		}
	}

	// 数据
	public static MyTabData dtTab(MyTabDataValue data, int idx, boolean disable) {
		MyTabData rs = new MyTabData(data, idx, disable);
		String time = rs.getTableData().getExecTime() == 0 ? "0" : rs.getTableData().getExecTime() + "";
		String rows = rs.getTableData().getRows() == 0 ? "0" : rs.getTableData().getRows() + "";
		VBox dataPane = dataBox(rs, disable, time, rows);
		rs.setContent(dataPane);
		return rs;
	}

	// TODO 表, 视图 等 数据库对象的ddl语句
	public static MyTabData ddlTab(String name, String ddl, boolean isRunFunc) {
		var mtb = new MyTabData(name);
		mtb.setDDL(true);
		HighLightingCodeArea sqlArea = new HighLightingCodeArea(null);
		mtb.setSqlArea(sqlArea);
		VBox box = DDLBox(mtb, ddl, isRunFunc, false, name);
		mtb.setContent(box);
		return mtb;
	}

	public static MyTabData ProcedureTab(String name, String ddl, boolean isRunFunc) {
		var mtb = new MyTabData(name);
		mtb.setDDL(true);
		HighLightingCodeArea sqlArea = new HighLightingCodeArea(null);
		mtb.setSqlArea(sqlArea);
		VBox box = DDLBox(mtb, ddl, isRunFunc, true, name);
		mtb.setContent(box);
		return mtb;
	}

	public static MyTabData EmptyTab(String name, String message) {
		var mtb = new MyTabData(name);
		mtb.setDDL(true);
		HighLightingCodeArea sqlArea = new HighLightingCodeArea(null);
		mtb.setSqlArea(sqlArea);
		VBox box = DDLBox(mtb, message, false, false, name);
		mtb.setContent(box);
		return mtb;
	}

//		
	public static Tab maskTab(String waittbName) {
		Tab waitTb = new Tab(waittbName);
		MaskerPane masker = new MaskerPane();
		waitTb.setContent(masker);
		return waitTb;
	}


	// 数据tab中的组件
	public static VBox DDLBox(MyTabData mtb, String ddl, boolean isRunFunc, boolean isProc, String name) {
		VBox vb = new VBox();

		StackPane sp = mtb.getSqlArea().getCodeAreaPane(ddl, false);
		// 表格上面的按钮
		AnchorPane fp = new DdlOptionBtnsPane(mtb, ddl, isRunFunc, isProc, name); // ddlOptionBtnsPane(ddl, isRunFunc,
																					// isProc, name);
		vb.getChildren().add(fp);
		vb.getChildren().add(sp);
		VBox.setVgrow(sp, Priority.ALWAYS);
		return vb;
	}

	// 数据tab中的组件
	public static VBox dataBox(MyTabData mtb, boolean disable, String time, String rows) {
		var dataPane = new VBox();
		var fp = new DataTableOptionBtnsPane(mtb, disable, time, rows, mtb.getTableData().getConnName(),
				  mtb.getTableData().isLock());
		dataPane.getChildren().add(fp);
		dataPane.getChildren().add(mtb.getTableData().getTable());
		VBox.setVgrow(mtb.getTableData().getTable(), Priority.ALWAYS);
		return dataPane;
	}

	// 右键菜单
	public ContextMenu tableViewMenu() {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem closeAll = new MenuItem("Close ALl");
		closeAll.setOnAction(e -> {
			List<Tab> ls = new ArrayList<>();
			for (Tab tab : ComponentGetter.dataTabPane.getTabs()) {
				ls.add(tab);
			}
			ls.forEach(tab -> {
				CommonAction.clearDataTable(tab);
			});
			ComponentGetter.dataTabPane.getTabs().clear();
		});

		MenuItem closeOther = new MenuItem("Close Other");
		closeOther.setOnAction(e -> {
			int size = ComponentGetter.dataTabPane.getTabs().size();
			if (size > 1) {
				List<Tab> ls = new ArrayList<>();
				for (Tab tab : ComponentGetter.dataTabPane.getTabs()) {

					if (!Objects.equals(tab, this)) {
						ls.add(tab);
					}
				}
				ls.forEach(tab -> {
					CommonAction.clearDataTable(tab);
				});

				ComponentGetter.dataTabPane.getTabs().clear();
				ComponentGetter.dataTabPane.getTabs().add(this);

			}

		});

		contextMenu.getItems().addAll(closeAll, closeOther);
		return contextMenu;
	}

	public void show() {
		Platform.runLater(() -> {
			var dataTab = ComponentGetter.dataTabPane;
			if (isDDL) {
				dataTab.getTabs().add(this);
			} else {
				if (idx > -1) {
					dataTab.getTabs().add(idx, this);
				} else {
					dataTab.getTabs().add(this);
				}
			}

			CommonAction.showDetailPane();
			dataTab.getSelectionModel().select(this);
		});
	}

	// 获取当前数据表的Tab
	public static MyTabData currentDataTab() {
		MyTabData tab = (MyTabData) ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem();
		return tab;
	}

	// 获取当前的表格
	@SuppressWarnings("unchecked")
	public static FilteredTableView<ObservableList<StringProperty>> dataTableView() {
		MyTabData mtd = currentDataTab();
		var table = mtd.getTableData().getTable();
		return table;
	}

	// 获取当前表格选择的数据
	public static ObservableList<ObservableList<StringProperty>> dataTableViewSelectedItems() {
		ObservableList<ObservableList<StringProperty>> vals = dataTableView().getSelectionModel().getSelectedItems();
		return vals;
	}

	public static MyTabDataValue myTabValue() {
		MyTabData mtd = currentDataTab();
		MyTabDataValue dv = mtd.getTableData();
		return dv;
	}
	
//	// 获取所有数据
	public static ObservableList<ObservableList<StringProperty>> getTabData() {
		MyTabDataValue dvt = MyTabData.myTabValue();
		if (dvt != null) {
			return dvt.getRawData();
		}
		return null;
	}

//	// 获取字段
	public static ObservableList<SqlFieldPo> getFields() {
		MyTabDataValue dvt = MyTabData.myTabValue();
		if (dvt != null) {
			return dvt.getColss();
		}
		return null;
	}

//	// 获取tableName
	public static String getTableName() {
		MyTabDataValue dvt = MyTabData.myTabValue();
		if (dvt != null) {
			return dvt.getTabName();
		}
		return "";
	}
//	

	// 获取当前表中的信息: 连接, 表面, schema, ExportDDL类, 然后导出drop语句
	public static RsVal tableInfo() {
		MyTabDataValue dataObj = MyTabData.myTabValue();
		String connName = "";
		String tableName = "";
		Connection conn = null;
		ObservableList<ObservableList<StringProperty>> alldata = null;
		SqluckyConnector cntor = null;
		FilteredTableView<ObservableList<StringProperty>> dataTableView = null;
		if (dataObj != null) {
			connName = dataObj.getConnName();
			tableName = dataObj.getTabName();
			cntor = dataObj.getDbConnection();
			conn = cntor.getConn();

			alldata = dataObj.getRawData();

			dataTableView = dataObj.getTable();

		}
		RsVal rv = new RsVal();
		rv.conn = conn;
		rv.dbconnPo = cntor;
		rv.tableName = tableName;
		rv.alldata = alldata;
		rv.dataTableView = dataTableView;
		return rv;
	}

	// 获取 当前table view 的控制面板
	public static AnchorPane optionPane() {
		if (ComponentGetter.dataTabPane == null || ComponentGetter.dataTabPane.getSelectionModel() == null
				|| ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem() == null)
			return null;
		Node vb = ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem().getContent();
		if (vb != null) {
			VBox vbx = (VBox) vb;
			AnchorPane fp = (AnchorPane) vbx.getChildren().get(0);
			return fp;
		}
		return null;
	}

	// 获取当前数据页面 中的 某个按钮
	public static Button getDataOptionBtn(String btnName) {
		AnchorPane fp = optionPane();
		if (fp == null)
			return null;
		Optional<Node> fn = fp.getChildren().stream().filter(v -> {
			return v.getId().equals(btnName);
		}).findFirst();
		Button btn = (Button) fn.get();

		return btn;
	}

	// 获取当前table view 的保存按钮
	public static Button dataPaneSaveBtn() {
		return MyTabData.currentDataTab().getSaveBtn();
	}

	// 获取当前table view 的详细按钮
	public static Button dataPaneDetailBtn() {
		return MyTabData.currentDataTab().getDetailBtn();
	}

	public static boolean exist(int row) {
		MyTabDataValue dvt = MyTabData.myTabValue();
		if (dvt != null) {
			Map<String, ObservableList<StringProperty>> oldval = dvt.getNewLineDate();
			if (null != oldval.get(row + "")) {
				return true;
			}
		}
		return false;
	}

	public static void addData(int rowNo, ObservableList<StringProperty> newDate,
			ObservableList<StringProperty> oldDate) {
		if (!exist(rowNo)) {
			addDataOldVal(rowNo, oldDate);
		}
		addDataNewLine(rowNo, newDate);

	}

	public static void addData(int rowNo, ObservableList<StringProperty> newDate) {
		addDataNewLine(rowNo, newDate);
	}

	public static String getSelectSQL() {
		MyTabDataValue dvt = MyTabData.myTabValue();
		if (dvt != null) {
			return dvt.getSqlStr();
		}
		return "";
	}

//	// 添加一行新数据
	public static void addDataNewLine(int rowNo, ObservableList<StringProperty> vals) {
		MyTabDataValue dvt = MyTabData.myTabValue();
		if (dvt != null) {
			Map<String, ObservableList<StringProperty>> map = dvt.getNewLineDate();
			map.put(rowNo + "", vals);
		}
	}

	
	public static void addDataOldVal(int rowNo, ObservableList<StringProperty> vals) {
		MyTabDataValue dvt = MyTabData.myTabValue();
		if (dvt != null) {
			Map<String, ObservableList<StringProperty>> map = dvt.getOldval();
			map.put(rowNo + "", vals);
		}
	}

	public static ObservableList<StringProperty> getold(String row) {
		var ov = getOldval();
		return ov.get(row);
	}

	public static Map<String, ObservableList<StringProperty>> getOldval() {
		MyTabDataValue dvt = MyTabData.myTabValue();
		if (dvt != null) {
			var v = dvt.getOldval();
			return v;
		}
		return null;
	}

	public static Map<String, ObservableList<StringProperty>> getModifyData() {
		return getNewLineDate();
	}

	public static Map<String, ObservableList<StringProperty>> getNewLineDate() {
		MyTabDataValue dvt = MyTabData.myTabValue();
		if (dvt != null) {
			var v = dvt.getNewLineDate();
			return v;
		}
		return null;
	}

	public static void rmUpdateData() {
		MyTabDataValue dvt = MyTabData.myTabValue();
		if (dvt != null) {
			dvt.getNewLineDate().clear();
			dvt.getOldval().clear();
		}
	}

	public static void rmAppendData() {
		MyTabData mtd = currentDataTab();
		MyTabDataValue dvt = mtd.getTableData();
		if (dvt != null) {
			dvt.getAppendData().clear();

		}
	}

	public static void appendDate(int rowNo, ObservableList<StringProperty> newDate) {
		MyTabDataValue dvt = MyTabData.myTabValue();
		if (dvt != null) {
			Map<String, ObservableList<StringProperty>> map = dvt.getAppendData();
			map.put(rowNo + "", newDate);
		}
	}

	public static List<ObservableList<StringProperty>> getAppendData() {
		MyTabDataValue dvt = MyTabData.myTabValue();
		if (dvt != null) {
			List<ObservableList<StringProperty>> dataList = new ArrayList<>();

			var map = dvt.getAppendData();
			for (String key : map.keySet()) {
				dataList.add(map.get(key));
			}
			return dataList;
		}
		return null;

	}

	public static void deleteTabDataRowNo(String no) {
		ObservableList<ObservableList<StringProperty>> ol = getTabData();
		if (ol == null)
			return;
		for (int i = 0; i < ol.size(); i++) {
			ObservableList<StringProperty> sps = ol.get(i);
			int len = sps.size();
			String dro = sps.get(len - 1).get();
			if (dro.equals(no)) {
				ol.remove(i);
				break;
			}
		}

	}

	public static Connection getDbconn() {
		return getDbConnection().getConn();
	}

	public static String getConnName() {
		MyTabDataValue dvt = MyTabData.myTabValue();
		if (dvt != null) {
			return dvt.getConnName();
		}
		return "";
	}

	public static SqluckyConnector getDbConnection() {
		MyTabDataValue dvt = MyTabData.myTabValue();
		return dvt.getDbConnection();
	}

	 

	public MyTabDataValue getTableData() {
		return tableData;
	}

	public void setTableData(MyTabDataValue tableData) {
		this.tableData = tableData;
	}

	public HighLightingCodeArea getSqlArea() {
		return sqlArea;
	}

	public void setSqlArea(HighLightingCodeArea sqlArea) {
		this.sqlArea = sqlArea;
	}

	public boolean isDDL() {
		return isDDL;
	}

	public void setDDL(boolean isDDL) {
		this.isDDL = isDDL;
	}

	public Button getSaveBtn() {
		return saveBtn;
	}

	public void setSaveBtn(Button saveBtn) {
		this.saveBtn = saveBtn;
	}

	public Button getDetailBtn() {
		return detailBtn;
	}

	public void setDetailBtn(Button detailBtn) {
		this.detailBtn = detailBtn;
	}
	
}
