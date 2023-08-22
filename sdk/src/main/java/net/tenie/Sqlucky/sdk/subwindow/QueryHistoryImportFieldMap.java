package net.tenie.Sqlucky.sdk.subwindow;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.SheetTableData;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TableViewUtils;

public class QueryHistoryImportFieldMap {
	TableView<ResultSetRowPo> tabView;
	private String importType; // excel, csv
	
	private String id;
	
	public QueryHistoryImportFieldMap(  String importType) {
//		this.conn = conn;
		
		this.importType = importType;
	
	}
	public void show() {
		// 删除按钮
		Button dBtn = delBtn();
		// 选中按钮
		Button sBtn = selectBtn();
		
		List<Node> nodes = new ArrayList<>();
		nodes.add(dBtn);
		nodes.add(sBtn);
		
		tabView = tableView(importType);
		QueryWindow qwind = new QueryWindow();
		qwind.showWindow( tabView, nodes, "选择之前匹配过的");
	}
	
	public TableView<ResultSetRowPo> tableView( String type){
		Connection conn =	SqluckyAppDB.getConn();
		try {
			String sql = "select ID, TABLE_NAME, CREATED_TIME from IMPORT_FIELD_MAP  where  type= '"+type+"' ";
			SheetTableData std = TableViewUtils.sqlToSheet(sql, conn, "IMPORT_FIELD_MAP");
			return std.getInfoTable();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}
	
	public Button delBtn() {
		Button delbtn = new Button("删除");
		delbtn.setOnAction(v->{
			String idval =  getIdVal();
			if(StrUtils.isNotNullOrEmpty(idval)) {
				int idx = tabView.getSelectionModel().getSelectedIndex();
				tabView.getItems().remove(idx);
				delId(idval);
			}
			
			
		});
		return delbtn;
	}
	
	private void delId(String idval) {
		String sql = "delete from IMPORT_FIELD_MAP where id =  " + idval;
		SqluckyAppDB.execDDL(sql);
	}
	
	
	public Button selectBtn() {
		Button selectBtn = new Button("选择");
		selectBtn.setOnAction(v->{
			id = getIdVal();
			MyAlert.infoAlert(id);
		});
		
		return selectBtn;
	}
	
	public String getIdVal() {
		ResultSetRowPo rs =	tabView.getSelectionModel().getSelectedItem();
		if(rs != null) {
			String id = rs.getValueByFieldName("ID");
			return id;
		}
		return "";
	}
}
