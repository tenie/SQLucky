package net.tenie.Sqlucky.sdk.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.SqluckyCodeAreaHolder;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
/**
 * 
 * @author tenie
 *extends Tab
 */
public class MyBottomSheet  implements SqluckyBottomSheet{
	public  SqluckyCodeAreaHolder sqlArea;
	private SheetDataValue tableData;
	private boolean isDDL = false;
	private Button saveBtn;
	private Button detailBtn;
	private int idx;
	private Tab tab ;
	 
	public void clean() {
		if(sqlArea != null ) {
			this.sqlArea = null;
		}
		if(tableData != null ) {
			this.tableData.clean();
			tableData = null;
		}
		if(saveBtn != null ) {
			this.saveBtn = null;
		}
		if(detailBtn != null ) {
			this.detailBtn = null;
		}
		if(tab != null ) {
			this.tab.setContent(null);
			this.tab = null;
		}
	}

	public MyBottomSheet(SheetDataValue data, int idx, boolean disable) {
		tab = new Tab(data.getTabName());
		this.tableData = data;
		this.idx = idx;
		tab.setOnCloseRequest(SdkComponent.dataTabCloseReq(this));
		tab.setContextMenu(tableViewMenu());
		tab.setUserData(this);
	}
	

	public MyBottomSheet(String tabName) {
		tab =  new Tab(tabName);
		tab.setOnCloseRequest(SdkComponent.dataTabCloseReq(this));
		tab.setContextMenu(tableViewMenu());
		if (tableData == null) {
			tableData = new SheetDataValue();
		}
		
		tab.setUserData(this);
	}
	public MyBottomSheet( SheetDataValue data) {
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
				SdkComponent.clearDataTable(tab);
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
					SdkComponent.clearDataTable(tab);
				});

				ComponentGetter.dataTabPane.getTabs().clear();
				ComponentGetter.dataTabPane.getTabs().add(this.tab);

			}

		});

		contextMenu.getItems().addAll(closeAll, closeOther);
		return contextMenu;
	}

	public void show() {
		Platform.runLater(() -> {
			var dataTab = ComponentGetter.dataTabPane;
			if (isDDL) {
				dataTab.getTabs().add(this.tab);
			} else {
				if (idx > -1) {
					dataTab.getTabs().add(idx, this.tab);
				} else {
					dataTab.getTabs().add(this.tab);
				}
			}

			SdkComponent.showDetailPane();
			dataTab.getSelectionModel().select(this.tab);
		});
	}

	@Override
	public SheetDataValue getTableData() {
		return tableData;
	}

	public void setTableData(SheetDataValue tableData) {
		this.tableData = tableData;
	}

	public SqluckyCodeAreaHolder getSqlArea() {
		return sqlArea;
	}

	public void setSqlArea(SqluckyCodeAreaHolder sqlArea) {
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


	public int getIdx() {
		return idx;
	}


	public void setIdx(int idx) {
		this.idx = idx;
	}


	public Tab getTab() {
		return tab;
	}


	public void setTab(Tab tab) {
		this.tab = tab;
	}
	
}
