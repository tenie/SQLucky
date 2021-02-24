package net.tenie.fx.component;

import java.sql.Connection;
import java.sql.SQLException;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import net.tenie.fx.Action.MyPopupNumberFilter;
import net.tenie.fx.PropertyPo.CacheTableDate;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.dao.GenerateSQLString;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.fx.utility.EventAndListener.CommonEventHandler;
import net.tenie.fx.utility.EventAndListener.RunSQLHelper;
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
	public static ContextMenu DataTableColumnContextMenu(String colname, int type, FilteredTableColumn<ObservableList<StringProperty>, String> col) {
		
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
			rsVal rv = exportSQL(DROP_COLUMN, colname);
			if(StrUtils.isNotNullOrEmpty(rv.sql)) {
				// 要被执行的函数
				Consumer< String >  caller = x ->{
					execExportSql(rv.sql, rv.conn);
				};
				ModalDialog.showComfirmExec("Confirm drop!", "Execute Sql: " + rv.sql +" ?", caller);		
			} 
			
		});
		
		MenuItem alterColumn = new MenuItem("Alter Column Date Type"); 
		alterColumn.setGraphic(ImageViewGenerator.svgImageDefActive("exchange"));
		alterColumn.setOnAction(e -> { 
			
			Consumer< String >  caller = x ->{ 
				String str = colname + " " + x;
				rsVal rv = exportSQL(ALTER_COLUMN, str);
				execExportSql(rv.sql, rv.conn);
			};
			ModalDialog.showExecWindow("Alter "+colname +" Date Type: input words like 'CHAR(10) ", "", caller);
		});
		
		MenuItem addColumn = new MenuItem("Add New Column"); 
		addColumn.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
		addColumn.setOnAction(e -> {  
			rsVal rv = tableInfo();
			Consumer< String >  caller = x ->{  
				rsVal rv2= exportSQL(ADD_COLUMN, x);
				execExportSql(rv2.sql, rv2.conn);
			};
			ModalDialog.showExecWindow(rv.tableName +" add column : input words like 'MY_COL CHAR(10)'", "", caller);
		});
		
		MenuItem updateColumn = new MenuItem("Update Column Value"); 
		updateColumn.setGraphic(ImageViewGenerator.svgImageDefActive("plus-square-o"));
		updateColumn.setOnAction(e -> {  
			rsVal rv = tableInfo();
			String sql = "UPDATE " + rv.tableName + " SET " + colname + " = " ;
			Consumer< String >  caller = x ->{   
				String strsql = sql + x;
				execExportSql(strsql, rv.conn);
			};
			ModalDialog.showExecWindow("Execute : "+ sql +" ? : input your value", "", caller);
		});
		
		
		cm.getItems().addAll(filter, miActive, copyColData,   dropCol, alterColumn, addColumn, updateColumn);
		return cm;
	}
	

	// 获取当前表中的信息: 连接, 表面, schema, ExportDDL类, 然后导出drop语句
	private static rsVal tableInfo() {
		String tableId = ComponentGetter.currentDataTabID();
		String connName = CacheTableDate.getConnName(tableId);
		String tableName =  CacheTableDate.getTableName(tableId);
		Connection conn = CacheTableDate.getDBConn(tableId);
		DbConnectionPo  dbc = DBConns.get(connName); 
		rsVal rv = new rsVal();
		rv.conn = conn; 
		rv.tableName = tableName;
		rv.dbc =  dbc; 
		return rv;
	}
	
	// 导出SQL
	private static rsVal exportSQL(int ty, String colname) {
		// 获取当前表中的信息: 连接, 表面, schema, ExportDDL类, 然后导出drop语句
		rsVal rv =  tableInfo();
		String sql ="";
		if(DROP_COLUMN == ty) {
			sql = rv.dbc.getExportDDL().exportAlterTableDropColumn(rv.conn, rv.dbc.getDefaultSchema(), rv.tableName, colname);
		}else if(ALTER_COLUMN == ty) {
			sql = rv.dbc.getExportDDL().exportAlterTableModifyColumn(rv.conn, rv.dbc.getDefaultSchema(), rv.tableName, colname);	
		}else if(ADD_COLUMN == ty) {
			sql = rv.dbc.getExportDDL().exportAlterTableAddColumn(rv.conn, rv.dbc.getDefaultSchema(), rv.tableName, colname);	
		}
		 
		rv.sql = sql; 
		return rv;
	}
	// 执行导出的sql
	public static void  execExportSql(String sql, Connection conn) {
//		String idx =  "" + ComponentGetter.dataTab.getSelectionModel().getSelectedIndex();
		JFXButton runFunPro = AllButtons.btns.get("runFunPro");
		RunSQLHelper.runSQLMethod(conn, sql, "", runFunPro);
		
	}
}


class rsVal{
	String sql;
	String tableName;
	Connection conn;
	DbConnectionPo  dbc ;
}