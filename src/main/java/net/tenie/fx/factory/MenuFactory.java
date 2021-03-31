package net.tenie.fx.factory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupStringFilter;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import net.tenie.fx.Action.ButtonAction;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.RsVal;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.PropertyPo.CacheTableDate;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.dao.GenerateSQLString;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.fx.utility.MyPopupNumberFilter;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.fx.window.ModalDialog;
import net.tenie.fx.window.MyAlert;
import net.tenie.lib.db.DBTools;
import net.tenie.lib.po.DbConnectionPo;
import net.tenie.lib.tools.StrUtils;

public class MenuFactory {
	static final int DROP_COLUMN = 1;
	static final int ALTER_COLUMN = 2;
	static final int ADD_COLUMN = 3;
	/**
	 * 数据表中列的右键弹出菜单
	 * @param colname
	 * @param type
	 * @param col
	 * @return
	 */
	public static ContextMenu DataTableColumnContextMenu(String colname, int type, FilteredTableColumn<ObservableList<StringProperty>, String> col, int colIdx) {
		
		// 过滤框
		PopupFilter<ObservableList<StringProperty>, String> popupFilter ;
		if (CommonUtility.isNum(type)) {
			// 过滤框
			popupFilter = new MyPopupNumberFilter(col);
			col.setOnFilterAction(e -> popupFilter.showPopup());
			popupFilter.getStyleClass().add("mypopup");

		} else {
			// 过滤框
			popupFilter = new PopupStringFilter(col);
			col.setOnFilterAction(e -> popupFilter.showPopup());
			popupFilter.getStyleClass().add("mypopup");
		}
		
		// 右点菜单
		ContextMenu cm = new ContextMenu();
		MenuItem miActive = new MenuItem("Copy Column Name");
//		miActive.getStyleClass().add("myMenuItem");
		miActive.setGraphic(ImageViewGenerator.svgImageDefActive("clipboard"));
		miActive.setOnAction(e -> { // 粘贴板赋值
			CommonUtility.setClipboardVal(colname);
		});
		Menu copyColData = new Menu("Copy Column Data");
		copyColData.setGraphic(ImageViewGenerator.svgImageDefActive("columns"));
		
		MenuItem selectCopyColData = new MenuItem("Copy Select Column Data");
		selectCopyColData.setGraphic(ImageViewGenerator.svgImageDefActive("clipboard"));
		selectCopyColData.setOnAction(CommonEventHandler.columnDataClipboard(true, false ,  colname)  );
		
		MenuItem AllCopyColData = new MenuItem("Copy All Column Data");
		AllCopyColData.setGraphic(ImageViewGenerator.svgImageDefActive("clipboard"));
		AllCopyColData.setOnAction(CommonEventHandler.columnDataClipboard(false, false ,  colname)  );
		copyColData.getItems().addAll(selectCopyColData, AllCopyColData);
		
		MenuItem filter = new MenuItem("Filter");
		filter.setGraphic(ImageViewGenerator.svgImageDefActive("filter"));
		filter.setOnAction(e->{
			popupFilter.showPopup();
		});
		
		// drop column
		MenuItem dropCol = new MenuItem("Drop Column: "+ colname);
//		dropCol.getStyleClass().add("myMenuItem");
		dropCol.setGraphic(ImageViewGenerator.svgImageDefActive("eraser"));
		dropCol.setOnAction(e ->  {
			RsVal rv = exportSQL(DROP_COLUMN, colname);
			if(StrUtils.isNotNullOrEmpty(rv.sql)) {
				// 要被执行的函数
				Consumer< String >  caller = x ->{ 
					execExportSql(rv.sql, rv.conn ,rv.dbconnPo);
				};
				ModalDialog.showComfirmExec("Confirm drop!", "Execute Sql: " + rv.sql +" ?", caller);		
			} 
			
		});
		
		MenuItem alterColumn = new MenuItem("Alter Column Date Type"); 
		alterColumn.setGraphic(ImageViewGenerator.svgImageDefActive("exchange"));
		alterColumn.setOnAction(e -> { 
			
			Consumer< String >  caller = x ->{ 
				if(StrUtils.isNullOrEmpty(x.trim())) return;
				String str = colname + " " + x;
				RsVal rv = exportSQL(ALTER_COLUMN, str);
				execExportSql(rv.sql, rv.conn, rv.dbconnPo);
			};
			ModalDialog.showExecWindow("Alter "+colname +" Date Type: input words like 'CHAR(10) ", "", caller);
		});
		
		MenuItem addColumn = new MenuItem("Add New Column"); 
		addColumn.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
		addColumn.setOnAction(e -> {  
			RsVal rv = CommonAction.tableInfo();
			Consumer< String >  caller = x ->{
				if(StrUtils.isNullOrEmpty(x.trim())) return;
				RsVal rv2= exportSQL(ADD_COLUMN, x);
				execExportSql(rv2.sql, rv2.conn,  rv.dbconnPo);
			};
			ModalDialog.showExecWindow(rv.tableName +" add column : input words like 'MY_COL CHAR(10)'", "", caller);
		});
		
		
		Menu updateMenu = new Menu("Update Column Data");
		updateMenu.setGraphic(ImageViewGenerator.svgImageDefActive("edit"));
		MenuItem updateTableColumn = new MenuItem("Update: Table is  Column Value"); 
		updateTableColumn.setGraphic(ImageViewGenerator.svgImageDefActive("table"));
		updateTableColumn.setOnAction(e -> {  
			RsVal rv = CommonAction.tableInfo();
			String sql = "UPDATE " + rv.tableName + " SET " + colname + " = " ;
			Consumer< String >  caller = x ->{
				if(StrUtils.isNullOrEmpty(x.trim())) return;
				String strsql = sql + x;
				execExportSql(strsql, rv.conn,  rv.dbconnPo);
			};
			ModalDialog.showExecWindow("Execute : "+ sql +" ? : input your value", "", caller);
		});
		
		MenuItem updateCurrentPageColumn = new MenuItem("Update: Current All Data is  Column Value"); 
		updateCurrentPageColumn.setGraphic(ImageViewGenerator.svgImageDefActive("file-text-o"));
		updateCurrentPageColumn.setOnAction(e -> {  
			RsVal rv = CommonAction.tableInfo();
			Consumer< String >  caller = x ->{
				if(StrUtils.isNullOrEmpty(x.trim())) return;
				ButtonAction.updateAllColumn(colIdx, x);
			};
			ModalDialog.showExecWindow("Execute : Update Current "+ rv.tableName +" Column :" +colname+" data ? : input your value", "", caller);
		});
		
		MenuItem updateSelectColumn = new MenuItem("Update: Selected Data is Column Value"); 
		updateSelectColumn.setGraphic(ImageViewGenerator.svgImageDefActive("indent"));
		updateSelectColumn.setOnAction(e -> {
			RsVal rv = CommonAction.tableInfo();
			Consumer< String >  caller = x ->{
				if(StrUtils.isNullOrEmpty(x.trim())) return;
				ButtonAction.updateSelectedDataColumn(colIdx, x);
			};
			ModalDialog.showExecWindow("Execute : Update Selected "+ rv.tableName +" Column :" +colname+" data ? : input your value", "", caller);
		
		});
		
		updateMenu.getItems().addAll(updateTableColumn, updateCurrentPageColumn, updateSelectColumn);
		
		cm.getItems().addAll(filter, miActive, copyColData,   dropCol, alterColumn, addColumn, updateMenu);
		cm.setOnShowing(e->{
			ObservableList<ObservableList<StringProperty>> alls = ComponentGetter.dataTableViewSelectedItems();
			if( alls.size() == 0) {
				updateSelectColumn.setDisable(true);
			}else {
				updateSelectColumn.setDisable(false);
			}
		});
		return cm;
	}
	


	
	// 导出SQL
	private static RsVal exportSQL(int ty, String colname) {
		RsVal rv = CommonAction.tableInfo();
		try {
			// 获取当前表中的信息: 连接, 表面, schema, ExportDDL类, 然后导出drop语句

			String sql = "";
			if (DROP_COLUMN == ty) {
				sql = rv.dbc.getExportDDL().exportAlterTableDropColumn(rv.conn, rv.dbc.getDefaultSchema(), rv.tableName,
						colname);
			} else if (ALTER_COLUMN == ty) {
				sql = rv.dbc.getExportDDL().exportAlterTableModifyColumn(rv.conn, rv.dbc.getDefaultSchema(),
						rv.tableName, colname);
			} else if (ADD_COLUMN == ty) {
				sql = rv.dbc.getExportDDL().exportAlterTableAddColumn(rv.conn, rv.dbc.getDefaultSchema(), rv.tableName,
						colname);
			}

			rv.sql = sql;
		} catch (Exception e) {
			MyAlert.errorAlert( e.getMessage());		
			
		}
		return rv;
	}
	// 执行导出的sql
	public static void  execExportSql(String sql, Connection conn, DbConnectionPo dbconnPo) {
//		String idx =  "" + ComponentGetter.dataTab.getSelectionModel().getSelectedIndex();
		JFXButton runFunPro = AllButtons.btns.get("runFunPro");
		RunSQLHelper.runSQLMethod(dbconnPo, conn, sql, "", runFunPro);
		
	}
	
	
	
	
//	treeView 右键菜单
	public static ContextMenu CreateTreeViewConnMenu() {
			ContextMenu contextMenu = new ContextMenu();

			MenuItem add = new MenuItem("Add Connection");
			add.setOnAction(e -> {
				ConnectionEditor.ConnectionInfoSetting();
			});
			add.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));

			MenuItem link = new MenuItem("Open Connection");
			link.setOnAction(CommonEventHandler.openConnEvent());
			link.setGraphic(ImageViewGenerator.svgImageDefActive("link"));

			MenuItem unlink = new MenuItem("Close Connection");
			unlink.setOnAction(CommonEventHandler.closeConnEvent());
			unlink.setGraphic(ImageViewGenerator.svgImageDefActive("unlink"));

			MenuItem Edit = new MenuItem("Edit Connection");
			Edit.setOnAction(CommonEventHandler.editConnEvent());
			Edit.setGraphic(ImageViewGenerator.svgImageDefActive("edit"));

			MenuItem delete = new MenuItem("Delete Connection");
			delete.setOnAction(e -> {
				ConnectionEditor.ConnectionInfoSetting();
			});
			delete.setGraphic(ImageViewGenerator.svgImageDefActive("trash"));

			contextMenu.getItems().addAll(add, link, unlink, Edit, delete);

			return contextMenu;
		}
	
	
	//行号 右键菜单
	public static ContextMenu CreateLineNoMenu(List<String> lineNoList , Label lineNo) {
			ContextMenu contextMenu = new ContextMenu();

			MenuItem add = new MenuItem("Add Bookmark");
			add.setOnAction(e -> { 
				if(lineNoList.contains(lineNo.getText())) {
					lineNo.setGraphic(ImageViewGenerator.svgImageDefActive("NULL",12));
					lineNoList.remove(lineNo.getText());
				}else {
					lineNo.setGraphic(ImageViewGenerator.svgImageDefActive("chevron-circle-right", 12));
					lineNoList.add(lineNo.getText());
				} 
				
			});
			add.setGraphic(ImageViewGenerator.svgImageDefActive("chevron-circle-right"));

			MenuItem next = new MenuItem("Next");
			next.setOnAction(e->{
				ButtonAction.nextBookmark(true);
			});
			next.setGraphic(ImageViewGenerator.svgImageDefActive("chevron-circle-down"));

			MenuItem previous = new MenuItem("Previous");
			previous.setOnAction(e->{
				ButtonAction.nextBookmark(false);
			});
			previous.setGraphic(ImageViewGenerator.svgImageDefActive("chevron-circle-up"));

			 

			contextMenu.getItems().addAll(add, next, previous);

			return contextMenu;
		}
}





