package net.tenie.fx.component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;

import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupStringFilter;

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
import net.tenie.lib.db.DBTools;
import net.tenie.lib.po.DbConnectionPo;
import net.tenie.lib.tools.StrUtils;

public class MenuFactory {

	/**
	 * 数据表中列的右键弹出菜单
	 * @param colname
	 * @param type
	 * @param col
	 * @return
	 */
	public static ContextMenu DataTableColumnContextMenu(String colname, int type, FilteredTableColumn<ObservableList<StringProperty>, String> col) {
		
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
		dropCol.setGraphic(ImageViewGenerator.svgImageDefActive("clipboard"));
		dropCol.setOnAction(e ->  {
			// 获取当前表中的信息: 连接, 表面, schema, ExportDDL类, 然后导出drop语句
			String tableId = ComponentGetter.currentDataTabID();
			String connName = CacheTableDate.getConnName(tableId);
			String tableName =  CacheTableDate.getTableName(tableId);
			Connection conn = CacheTableDate.getDBConn(tableId);
			DbConnectionPo  dbc = DBConns.get(connName); 
			
			String  dropSql = dbc.getExportDDL().exportAlterTableDropColumn(conn, dbc.getDefaultSchema(), tableName, colname);
			if(StrUtils.isNotNullOrEmpty(dropSql)) {
				// 要被执行的函数
				Consumer< String >  caller = x ->{
					execDropSql(dropSql, conn);
				};
				Platform.runLater(() -> { 
					ModalDialog.showComfirmExec("Confirm drop!", "Execute Sql: " + dropSql +" ?", caller);
				});
			} 
			
		});
		
		MenuItem alterColumn = new MenuItem("Alter Column Date Type");
//		alterColumn.getStyleClass().add("myMenuItem");
		alterColumn.setGraphic(ImageViewGenerator.svgImageDefActive("clipboard"));
		alterColumn.setOnAction(e -> {  
			 FilteredTableView<ObservableList<StringProperty>> table = ComponentGetter.dataTableView();
			 ObservableList<TableColumn<ObservableList<StringProperty>, ?>> cols =  table.getColumns();
			 int idx = -1;
			 for(int i = 0; i < cols.size(); i++) {
				Object obj =  cols.get(i);
				if( Objects.equals(obj, col)) {
					idx = i;
					break;
				}
			 }
			 if(idx > -1) {
				 System.out.println(idx);
				 ObservableList<ObservableList<StringProperty>> obs =  table.getItems();
				 StringBuilder strb = new StringBuilder();
				 for(int i = 0 ; i < obs.size(); i++) {
					 ObservableList<StringProperty> vals = obs.get(i);
					 if (vals != null && vals.size() > 0) {
						    String vl = vals.get(idx).get(); 
							strb.append(vl);
							strb.append('\n');
					 }
					
				 }
				 String str = strb.toString();
				 System.out.println(str);
				 CommonUtility.setClipboardVal(str); 
				 
				 
			 }
		});
		
		MenuItem addColumn = new MenuItem("Add Column Name");
//		addColumn.getStyleClass().add("myMenuItem");
		addColumn.setGraphic(ImageViewGenerator.svgImageDefActive("clipboard"));
		addColumn.setOnAction(e -> {  
			
		});
		cm.getItems().addAll(filter, miActive, copyColData,   dropCol, alterColumn, addColumn);
		return cm;
	}
	
	public static void  execDropSql(String dropSql, Connection conn) {
		String sql[] = dropSql.split(";");
		for(int i=0; i< sql.length; i++) {
			String stmp = sql[i];
			if(StrUtils.isNotNullOrEmpty(stmp)) {
				try {
					DBTools.execDDL(conn, stmp);
				} catch (SQLException e1) { 
					Platform.runLater(() -> {
						ModalDialog.showErrorMsg("Sql Error", e1.getMessage());
					});
				}
			}
		}
		
	}
}
