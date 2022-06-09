package net.tenie.fx.component.dataView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.po.BottomSheetDataValue;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.component.CodeArea.HighLightingCodeArea;

public class MyTabData extends Tab implements SqluckyBottomSheet{
	private BottomSheetDataValue tableData;
	public HighLightingCodeArea sqlArea;
	private boolean isDDL = false;
	private Button saveBtn;
	private Button detailBtn;
	private int idx;
	private BottomSheetOptionBtnsPane dtBtnPane;
//	private List<Button> optionBtns;
	 

	public MyTabData(BottomSheetDataValue data, int idx, boolean disable) {
		this(data.getTabName());
		this.tableData = data;
		this.idx = idx;
	}
	

	public MyTabData(String tabName) {
		super(tabName);
		this.setOnCloseRequest(CommonEventHandler.dataTabCloseReq(this));
		this.setContextMenu(tableViewMenu());
		if (tableData == null) {
			tableData = new BottomSheetDataValue();
		}
	}
	public MyTabData( BottomSheetDataValue data) {
		this(data.getTabName());
		this.tableData = data;
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

//	// 获取当前数据表的Tab
//	public static Tab currentDataTab() {
//		Tab tab =  ComponentGetter.dataTabPane.getSelectionModel().getSelectedItem();
//		SqluckyBottomSheet sheet = (SqluckyBottomSheet) tab;
//		return tab;
//	}

	

//	public static BottomSheetDataValue myTabValue() {
//		SqluckyBottomSheet mtd = ComponentGetter.currentDataTab();
//		BottomSheetDataValue dv = mtd.getTableData();
//		return dv;
//	}
	
////	// 获取所有数据
//	public static ObservableList<ObservableList<StringProperty>> getTabData() {
//		BottomSheetDataValue dvt = MyTabData.myTabValue();
//		if (dvt != null) {
//			return dvt.getRawData();
//		}
//		return null;
//	}

////	// 获取字段
//	public static ObservableList<SqlFieldPo> getFields() {
//		BottomSheetDataValue dvt = MyTabData.myTabValue();
//		if (dvt != null) {
//			return dvt.getColss();
//		}
//		return null;
//	}

////	// 获取tableName
//	public static String getTableName() {
//		BottomSheetDataValue dvt = MyTabData.myTabValue();
//		if (dvt != null) {
//			return dvt.getTabName();
//		}
//		return "";
//	}
//	

	



	 
	@Override
	public BottomSheetDataValue getTableData() {
		return tableData;
	}

	public void setTableData(BottomSheetDataValue tableData) {
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
