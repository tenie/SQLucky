package net.tenie.fx.component.dataView;

import java.sql.Connection;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.po.SqlFieldPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.ButtonAction;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.window.TableDataDetail;
import net.tenie.lib.tools.IconGenerator;

/**
 * 
 * @author tenie
 *
 */
public class DataTableOptionBtnsPane extends  AnchorPane{
	
	public DataTableOptionBtnsPane(MyTabData mytb,  boolean disable, String time , String rows, String connName  , boolean isLock) {
		super();
		CommonUtility.addCssClass(this, "data-table-btn-anchor-pane");
		this.prefHeight(25);
		JFXButton saveBtn = new JFXButton();
		saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
		saveBtn.setOnMouseClicked( e->{ 
				ButtonAction.dataSave();
		});
		saveBtn.setTooltip(MyTooltipTool.instance("Save data"));
		saveBtn.setDisable(true);
		mytb.setSaveBtn(saveBtn);

		JFXButton detailBtn = new JFXButton();
		detailBtn.setGraphic(IconGenerator.svgImageDefActive("search-plus")); 
		detailBtn.setOnMouseClicked( e->{
			TableDataDetail.show();
		});
		detailBtn.setTooltip(MyTooltipTool.instance("current line detail "));
		detailBtn.setDisable(disable);
		
		mytb.setDetailBtn(detailBtn);
		
		JFXButton tableSQLBtn = new JFXButton();
		tableSQLBtn.setGraphic(IconGenerator.svgImageDefActive("table")); 
		tableSQLBtn.setOnMouseClicked( e->{
			ButtonAction.findTable();
		});
		tableSQLBtn.setTooltip(MyTooltipTool.instance("Table SQL"));
		tableSQLBtn.setDisable(disable);
		

		// refresh
		JFXButton refreshBtn = new JFXButton();
		refreshBtn.setGraphic(IconGenerator.svgImageDefActive("refresh")); 
		refreshBtn.setOnMouseClicked( e->{  
			refreshData(isLock) ;
		});
		refreshBtn.setTooltip(MyTooltipTool.instance("refresh table "));
		refreshBtn.setDisable(disable);
		

		// 添加一行数据
		JFXButton addBtn = new JFXButton();
		addBtn.setGraphic(IconGenerator.svgImageDefActive("plus-square"));

		addBtn.setOnMouseClicked(e->{ 
			addData(saveBtn);
		});
		addBtn.setTooltip(MyTooltipTool.instance("add new data "));
		addBtn.setDisable(disable);

		JFXButton minusBtn = new JFXButton();
		minusBtn.setGraphic(IconGenerator.svgImage("minus-square", "#EC7774" , false));

		minusBtn.setOnMouseClicked( e->{ 
			ButtonAction.deleteData(); 
		});
		minusBtn.setTooltip(MyTooltipTool.instance("delete data "));
		minusBtn.setDisable(disable);

//	    	 files-o
		JFXButton copyBtn = new JFXButton();
		copyBtn.setGraphic(IconGenerator.svgImageDefActive("files-o"));
		copyBtn.setOnMouseClicked( e->{ 
			ButtonAction.copyData();
		});
		copyBtn.setTooltip(MyTooltipTool.instance("copy selected row data "));
		copyBtn.setDisable(disable);
		

		MenuButton exportBtn = new MenuButton();
		exportBtn.setGraphic(IconGenerator.svgImageDefActive("share-square-o"));
		exportBtn.setTooltip(MyTooltipTool.instance("Export data"));
		exportBtn.setDisable(disable);

		Menu insertSQL = new Menu("Export Insert SQL Format ");
		MenuItem selected = new MenuItem("Selected Data to Clipboard ");
		selected.setOnAction(CommonEventHandler.InsertSQLClipboard(true, false));
		MenuItem selectedfile = new MenuItem("Selected Data to file");
		selectedfile.setOnAction(CommonEventHandler.InsertSQLClipboard(true, true));

		MenuItem all = new MenuItem("ALl Data to Clipboard ");
		all.setOnAction(CommonEventHandler.InsertSQLClipboard(false, false));
		MenuItem allfile = new MenuItem("ALl Data to file");
		allfile.setOnAction(CommonEventHandler.InsertSQLClipboard(false, true));

		insertSQL.getItems().addAll(selected, selectedfile, all, allfile);

		Menu csv = new Menu("Export CSV Format ");
		MenuItem csvselected = new MenuItem("Selected Data to Clipboard ");
		csvselected.setOnAction(CommonEventHandler.csvStrClipboard(true, false));
		MenuItem csvselectedfile = new MenuItem("Selected Data to file");
		csvselectedfile.setOnAction(CommonEventHandler.csvStrClipboard(true, true));

		MenuItem csvall = new MenuItem("ALl Data to Clipboard ");
		csvall.setOnAction(CommonEventHandler.csvStrClipboard(false, false));
		MenuItem csvallfile = new MenuItem("ALl Data to file");
		csvallfile.setOnAction(CommonEventHandler.csvStrClipboard(false, true));

		csv.getItems().addAll(csvselected, csvselectedfile, csvall, csvallfile);

		Menu txt = new Menu("Export TXT Format ");
		MenuItem txtselected = new MenuItem("Selected Data to Clipboard ");
		txtselected.setOnAction(CommonEventHandler.txtStrClipboard(true, false));
		MenuItem txtselectedfile = new MenuItem("Selected Data to file");
		txtselectedfile.setOnAction(CommonEventHandler.txtStrClipboard(true, true));

		MenuItem txtall = new MenuItem("ALl Data to Clipboard ");
		txtall.setOnAction(CommonEventHandler.txtStrClipboard(false, false));
		MenuItem txtallfile = new MenuItem("ALl Data to file");
		txtallfile.setOnAction(CommonEventHandler.txtStrClipboard(false, true));

		txt.getItems().addAll(txtselected, txtselectedfile, txtall, txtallfile);
		
		Menu filedNames = new Menu("Export Table Filed Name ");
		MenuItem CommaSplit = new MenuItem("Comma splitting");
		CommaSplit.setOnAction(CommonEventHandler.commaSplitTableFileds());
		
		MenuItem CommaSplitIncludeType = new MenuItem("Comma splitting Include Field Type");
		CommaSplitIncludeType.setOnAction(CommonEventHandler.commaSplitTableFiledsIncludeType());
		
		filedNames.getItems().addAll(CommaSplit, CommaSplitIncludeType);

		exportBtn.getItems().addAll(insertSQL, csv, txt, filedNames);
		
		//隐藏按钮
		JFXButton hideBottom = new JFXButton(); 
		hideBottom.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-down"));
		hideBottom.setOnMouseClicked(CommonEventHandler.hideBottom()); 
		// 锁
		JFXButton lockbtn = ButtonFactory.createLockBtn(mytb);  
		
		//保存按钮监听 : 保存亮起, 锁住
		saveBtn.disableProperty().addListener(e->{
			if( ! saveBtn.disableProperty().getValue() ) {
				if (mytb.getTableData().isLock()) {
					lockbtn.setGraphic(IconGenerator.svgImageDefActive("lock"));
				} else {
					lockbtn.setGraphic(IconGenerator.svgImageDefActive("unlock"));

				}
			}
		});
		
		
		//计时/查询行数
		String info = ""; 
		if(StrUtils.isNotNullOrEmpty(time)) {
			 info = connName+ " : "+ time+ " s / "+rows+" rows";
		}
		Label lb = new Label(info);
		
		
		
		this.getChildren().addAll(saveBtn, detailBtn, tableSQLBtn, refreshBtn, addBtn, minusBtn, copyBtn, exportBtn, 
				hideBottom, lockbtn, lb);
		Double fix = 30.0;
		int i = 0;
		AnchorPane.setLeftAnchor(detailBtn , fix * ++i ) ;
		AnchorPane.setLeftAnchor(tableSQLBtn , fix * ++i ) ;
		AnchorPane.setLeftAnchor(refreshBtn , fix * ++i);
		
		AnchorPane.setLeftAnchor(addBtn , fix * ++i ) ;
		AnchorPane.setLeftAnchor(minusBtn , fix * ++i);
		AnchorPane.setLeftAnchor(copyBtn , fix * ++i ) ;
		AnchorPane.setLeftAnchor(exportBtn , fix * ++i);
		
		AnchorPane.setRightAnchor(hideBottom, 0.0);
		AnchorPane.setRightAnchor(lockbtn, 35.0);
		
		AnchorPane.setTopAnchor(lb, 3.0);
		AnchorPane.setRightAnchor(lb, 70.0);
		 
	}
	
	//refreshData
		public static void refreshData(boolean isLock) {
//			String id = DataViewTab.currentDataTabID(); 
			String sql = MyTabData.getSelectSQL();
			Connection conn = MyTabData.getDbconn();
		    String connName = 	MyTabData.getConnName();
			if (conn != null) {
				//TODO 关闭当前tab
				var dataTab = ComponentGetter.dataTabPane;
				int selidx = dataTab.getSelectionModel().getSelectedIndex(); 
//				dataTab.getTabs().remove(selidx); 
				CommonAction.clearDataTable(selidx);
				RunSQLHelper.runSQLMethodRefresh( DBConns.get(connName),  sql, selidx+"", isLock);
			}	
		}
		
		// 添加一行数据
		public static void addData(JFXButton saveBtn) {
			MyTabData  mtd = MyTabData.currentDataTab();
			var tbv = mtd.getTableData().getTable();
			
			tbv.scrollTo(0);
			int newLineidx = ConfigVal.newLineIdx++;
			ObservableList<SqlFieldPo> fs = MyTabData.getFields();
			ObservableList<StringProperty> item = FXCollections.observableArrayList();
			for (int i = 0; i < fs.size(); i++) {
				SimpleStringProperty sp = new SimpleStringProperty();
				// 添加监听. 保存时使用 newLineIdx
				CommonUtility.newStringPropertyChangeListener(sp, fs.get(i).getColumnType().get());
				item.add(sp);
			}
			item.add(new SimpleStringProperty(newLineidx + "")); // 行号， 没什么用
			MyTabData.appendDate( newLineidx, item); // 可以防止在map中被覆盖
			tbv.getItems().add(0, item);

			// 点亮保存按钮
//			AnchorPane fp = dataAnchorPane(tbv);
//			fp.getChildren().get(0).setDisable(false);
			saveBtn.setDisable(false);
		}
		
//		// 获取数据表的 控制按钮列表
//		public static AnchorPane dataAnchorPane(FilteredTableView<ObservableList<StringProperty>> table) {
//			VBox vb = (VBox) table.getParent();
//			AnchorPane fp = (AnchorPane) vb.getChildren().get(0);
//			return fp;
//		}
}
