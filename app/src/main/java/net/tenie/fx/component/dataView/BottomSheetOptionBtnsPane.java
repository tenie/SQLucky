package net.tenie.fx.component.dataView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.jfoenix.controls.JFXButton;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheetUtility;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.component.MyBottomSheet;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.ResultSetCellPo;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.subwindow.ModalDialog;
import net.tenie.Sqlucky.sdk.subwindow.TableDataDetail;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.ButtonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.window.ProcedureExecuteWindow;

/**
 * 
 * @author tenie
 *
 */
public class BottomSheetOptionBtnsPane extends AnchorPane {

	public  BottomSheetOptionBtnsPane( List<Node> btnLs) {
		super();
		initObj(  btnLs, null, null, null);
	}
	public BottomSheetOptionBtnsPane( List<Node> btnLs, String time, String rows, String connName) {
		super();
		initObj(btnLs, time, rows, connName);

	}
	
	public void initObj(List<Node> btnLs, String time, String rows, String connName) {
		CommonUtility.addCssClass(this, "data-table-btn-anchor-pane");
		this.prefHeight(25);
		
		

		// 隐藏按钮
		JFXButton hideBottom = new JFXButton();
		hideBottom.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-down"));
		hideBottom.setOnMouseClicked(CommonEventHandler.hideBottom());
		

		// 计时/查询行数
		String info = "";
		if (StrUtils.isNotNullOrEmpty(time)) {
			info = connName + " : " + time + " s / " + rows + " rows";
		}
		Label lb = new Label(info);
		
		// 按钮摆放的容器
		HBox btnHbox = new HBox();
		// 将按钮放入容器
		if(btnLs != null) {
			for(var nd : btnLs) {
				if(nd instanceof Label) {
					nd.getStyleClass().add("padding5");
				}
				btnHbox.getChildren().add(nd);
			}
		}
		
		
		this.getChildren().addAll(btnHbox,  hideBottom, lb);

		AnchorPane.setRightAnchor(hideBottom, 0.0);

		AnchorPane.setTopAnchor(lb, 3.0);
		AnchorPane.setRightAnchor(lb, 40.0);
	}
	
	/**
	 * sql 查询数据后要操作的按钮
	 * @param mytb
	 * @param disable
	 * @return
	 */
	public static List<Node> sqlDataOptionBtns( MyBottomSheet mytb,  boolean disable){
		List<Node> ls = new ArrayList<>();
		JFXButton saveBtn = new JFXButton();
		saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
		saveBtn.setOnMouseClicked(e -> {
			ButtonAction.dataSave();
		});
		saveBtn.setTooltip(MyTooltipTool.instance("Save data"));
		saveBtn.setDisable(true);
		mytb.setSaveBtn(saveBtn);

		JFXButton detailBtn = new JFXButton();
		detailBtn.setGraphic(IconGenerator.svgImageDefActive("search-plus"));
		detailBtn.setOnMouseClicked(e -> {
			TableDataDetail.show();
		});
		detailBtn.setTooltip(MyTooltipTool.instance("current line detail "));
		detailBtn.setDisable(disable);

		mytb.setDetailBtn(detailBtn);

		JFXButton tableSQLBtn = new JFXButton();
		tableSQLBtn.setGraphic(IconGenerator.svgImageDefActive("table"));
		tableSQLBtn.setOnMouseClicked(e -> {
			ButtonAction.findTable();
		});
		tableSQLBtn.setTooltip(MyTooltipTool.instance("Table SQL"));
		tableSQLBtn.setDisable(disable);

		// refresh
		JFXButton refreshBtn = new JFXButton();
		refreshBtn.setGraphic(IconGenerator.svgImageDefActive("refresh"));
		refreshBtn.setOnMouseClicked(e -> {
			refreshData(ComponentGetter.currentDataTab().getTableData().isLock());
		});
		refreshBtn.setTooltip(MyTooltipTool.instance("refresh table "));
		refreshBtn.setDisable(disable);

		// 添加一行数据
		JFXButton addBtn = new JFXButton();
		addBtn.setGraphic(IconGenerator.svgImageDefActive("plus-square"));

		addBtn.setOnMouseClicked(e -> {
			addData(saveBtn);
		});
		addBtn.setTooltip(MyTooltipTool.instance("add new data "));
		addBtn.setDisable(disable);

		JFXButton minusBtn = new JFXButton();
		minusBtn.setGraphic(IconGenerator.svgImage("minus-square", "#EC7774"));

		minusBtn.setOnMouseClicked(e -> {
			ButtonAction.deleteData();
		});
		minusBtn.setTooltip(MyTooltipTool.instance("delete data "));
		minusBtn.setDisable(disable);

//	    	 files-o
		JFXButton copyBtn = new JFXButton();
		copyBtn.setGraphic(IconGenerator.svgImageDefActive("files-o"));
		copyBtn.setOnMouseClicked(e -> {
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

		MenuItem all = new MenuItem("All Data to Clipboard ");
		all.setOnAction(CommonEventHandler.InsertSQLClipboard(false, false));
		MenuItem allfile = new MenuItem("All Data to file");
		allfile.setOnAction(CommonEventHandler.InsertSQLClipboard(false, true));

		insertSQL.getItems().addAll(selected, selectedfile, all, allfile);

		Menu csv = new Menu("Export CSV Format ");
		MenuItem csvselected = new MenuItem("Selected Data to Clipboard ");
		csvselected.setOnAction(CommonEventHandler.csvStrClipboard(true, false));
		MenuItem csvselectedfile = new MenuItem("Selected Data to file");
		csvselectedfile.setOnAction(CommonEventHandler.csvStrClipboard(true, true));

		MenuItem csvall = new MenuItem("All Data to Clipboard ");
		csvall.setOnAction(CommonEventHandler.csvStrClipboard(false, false));
		MenuItem csvallfile = new MenuItem("All Data to file");
		csvallfile.setOnAction(CommonEventHandler.csvStrClipboard(false, true));

		csv.getItems().addAll(csvselected, csvselectedfile, csvall, csvallfile);

		Menu txt = new Menu("Export TXT Format ");
		MenuItem txtselected = new MenuItem("Selected Data to Clipboard ");
		txtselected.setOnAction(CommonEventHandler.txtStrClipboard(true, false));
		MenuItem txtselectedfile = new MenuItem("Selected Data to file");
		txtselectedfile.setOnAction(CommonEventHandler.txtStrClipboard(true, true));

		MenuItem txtall = new MenuItem("All Data to Clipboard ");
		txtall.setOnAction(CommonEventHandler.txtStrClipboard(false, false));
		MenuItem txtallfile = new MenuItem("All Data to file");
		txtallfile.setOnAction(CommonEventHandler.txtStrClipboard(false, true));

		txt.getItems().addAll(txtselected, txtselectedfile, txtall, txtallfile);

		Menu fieldNames = new Menu("Export Table Field Name ");
		MenuItem CommaSplit = new MenuItem("Comma splitting");
		CommaSplit.setOnAction(CommonEventHandler.commaSplitTableFields());

		MenuItem CommaSplitIncludeType = new MenuItem("Comma splitting Include Field Type");
		CommaSplitIncludeType.setOnAction(CommonEventHandler.commaSplitTableFiledsIncludeType());

		fieldNames.getItems().addAll(CommaSplit, CommaSplitIncludeType);

		exportBtn.getItems().addAll(insertSQL, csv, txt, fieldNames);
		
		
		// 锁
		JFXButton lockbtn = SdkComponent.createLockBtn(mytb);

		// 保存按钮监听 : 保存亮起, 锁住
		saveBtn.disableProperty().addListener(e -> {
			if (!saveBtn.disableProperty().getValue()) {
				if (mytb.getTableData().isLock()) {
					lockbtn.setGraphic(IconGenerator.svgImageDefActive("lock"));
				} else {
					lockbtn.setGraphic(IconGenerator.svgImageDefActive("unlock"));

				}
			}
		});
		ls.add(lockbtn);
		ls.add(saveBtn);
		ls.add(detailBtn);
		ls.add(tableSQLBtn);
		ls.add(refreshBtn);
		ls.add(addBtn);
		ls.add(minusBtn);
		ls.add(copyBtn);
		ls.add(exportBtn);
		
		return ls;
	}
	
	/**
	 * 数据库对象（如表，视图）的ddl语句， 操作按钮
	 * @param mytb
	 * @param ddl
	 * @param isRunFunc
	 * @param isProc
	 * @param name
	 * @return
	 */
	public static List<Node> DDLOptionBtns(MyBottomSheet mytb, String ddl, boolean isRunFunc, boolean isProc, String name) {
		List<Node> ls = new ArrayList<>();
		// 锁
		JFXButton lockbtn = SdkComponent.createLockBtn(mytb);
		ls.add(lockbtn);
		// 保存
		JFXButton saveBtn = new JFXButton();
		saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
		saveBtn.setOnMouseClicked(e -> {
			// TODO 保存存储过程
			RunSQLHelper.runSQLMethod(mytb.getSqlArea().getCodeArea().getText(), null, true, null);
			saveBtn.setDisable(true);

		});
		saveBtn.setTooltip(MyTooltipTool.instance("save"));
		saveBtn.setDisable(true);
		mytb.setSaveBtn(saveBtn);
		ls.add(saveBtn);
		

		// 编辑
		JFXButton editBtn = new JFXButton();
		editBtn.setGraphic(IconGenerator.svgImageDefActive("edit"));
		editBtn.setOnMouseClicked(e -> {
			if (mytb.getSqlArea() != null) {
				MyCodeArea codeArea = mytb.getSqlArea().getCodeArea();
				codeArea.setEditable(true);
				saveBtn.setDisable(false);
				ButtonFactory.lockLockBtn(mytb, lockbtn);

			}
		});
		editBtn.setTooltip(MyTooltipTool.instance("Edit"));
		ls.add(editBtn);


		// 运行按钮
		if (isRunFunc) {
			JFXButton runFuncBtn = new JFXButton();
			runFuncBtn.setGraphic(IconGenerator.svgImageDefActive("play"));
			runFuncBtn.setOnMouseClicked(e -> {
				Consumer<String> caller;
				ButtonFactory.lockLockBtn(mytb, lockbtn);
				if (isProc) {
					var fields = CommonUtility.getProcedureFields(ddl);
					if (fields.size() > 0) {
						// 有参数的存储过程
						new ProcedureExecuteWindow(name, fields);
					} else {
						// 调用无参数的存储过程
						caller = x -> {
							SqluckyConnector dpo = DBConns.getCurrentConnectPO();
							RunSQLHelper.callProcedure(name, dpo, fields);
						};
						ModalDialog.showExecWindow("Run Procedure", name, caller);

					}

				} else {
					caller = x -> {
						SqluckyConnector dpo = DBConns.getCurrentConnectPO();
						String sql = dpo.getExportDDL().exportCallFuncSql(x);
						RunSQLHelper.refresh(dpo, sql, null, false);
					};
					ModalDialog.showExecWindow("Run function", name + "()", caller);
				}

			});
			runFuncBtn.setTooltip(MyTooltipTool.instance("Run"));
			ls.add(runFuncBtn);
		}
		
		
		
		 
		return ls;
	}
	
	
	

	// 刷新查询结果
	public static void refreshData(boolean isLock) {
		String sql = SqluckyBottomSheetUtility.getSelectSQL();
		Connection conn = SqluckyBottomSheetUtility.getDbconn();
		String connName = SqluckyBottomSheetUtility.getConnName();
		if (conn != null) {
			// TODO 关闭当前tab
			var dataTab = ComponentGetter.dataTabPane;
			int selidx = dataTab.getSelectionModel().getSelectedIndex();
			SdkComponent.clearDataTable(selidx);
			RunSQLHelper.refresh(DBConns.get(connName), sql, selidx + "", isLock);
		}
	}

	// 添加一行数据
	public static void addData(JFXButton saveBtn) {
		SqluckyBottomSheet mtd = ComponentGetter.currentDataTab();
		var tbv = mtd.getTableData().getTable();

		tbv.scrollTo(0);
		ResultSetPo rspo = SqluckyBottomSheetUtility.getResultSet();
		ResultSetRowPo rowpo = rspo.createAppendNewRow(); 
		ObservableList<SheetFieldPo> fs = rspo.getFields();
		for (int i = 0; i < fs.size(); i++) {
			SheetFieldPo fieldpo = fs.get(i);
			SimpleStringProperty sp = new SimpleStringProperty("<null>");
			rowpo.addCell(sp, fieldpo); 
		}
		tbv.getItems().add(0, rowpo);

		// 点亮保存按钮
		saveBtn.setDisable(false);
	}
}
