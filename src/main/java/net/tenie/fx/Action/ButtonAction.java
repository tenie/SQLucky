package net.tenie.fx.Action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.tableview2.FilteredTableView;

import com.jfoenix.controls.JFXButton;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import net.tenie.fx.PropertyPo.CacheTabView;
import net.tenie.fx.PropertyPo.DbConnectionPo;
//import net.tenie.fx.PropertyPo.CacheTabView;
import net.tenie.fx.PropertyPo.DbTableDatePo;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.PropertyPo.TablePo;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.MyCodeArea;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.component.TreeItem.TreeObjCache;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.Db2ErrorCode;
import net.tenie.fx.config.DbVendor;
import net.tenie.fx.dao.DeleteDao;
import net.tenie.fx.dao.InsertDao;
import net.tenie.fx.dao.UpdateDao;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.fx.window.MyAlert;
import net.tenie.lib.tools.StrUtils;

public class ButtonAction {
	
	
	public static void dataSave() {
		Button saveBtn = ComponentGetter.dataPaneSaveBtn();
		String tabId = ComponentGetter.currentDataTabID(); //saveBtn.getParent().getId();
		String tabName = CacheTabView.getTableName(tabId);
//		ObservableList<ObservableList<StringProperty>> alldata = CacheTabView.getData(tabId);
		Connection conn = CacheTabView.getDbConn(tabId);
		var dpo = CacheTabView.getDbConnection(tabId);
		if (tabName != null && tabName.length() > 0) {
			// 字段
			ObservableList<SqlFieldPo> fpos = CacheTabView.getFields(tabId);
			// 待保存数据
			Map<String, ObservableList<StringProperty>> modifyData = CacheTabView.getModifyData(tabId);
			// 执行sql 后的信息 (主要是错误后显示到界面上)
			DbTableDatePo ddlDmlpo = DbTableDatePo.executeInfoPo();

			if (!modifyData.isEmpty()) {
				for (String key : modifyData.keySet()) {
					// 获取对应旧数据
					ObservableList<StringProperty> old = CacheTabView.getold(tabId, key);
					ObservableList<StringProperty> newd = modifyData.get(key);
					// 拼接update sql
					try {
						String msg = UpdateDao.execUpdate(conn, tabName, newd, old, fpos);
						
						ObservableList<StringProperty> val = FXCollections.observableArrayList();
						val.add(RunSQLHelper.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ));
						val.add(RunSQLHelper.createReadOnlyStringProperty(msg)); 
						val.add(RunSQLHelper.createReadOnlyStringProperty("success")); 
						val.add(RunSQLHelper.createReadOnlyStringProperty("" ));
						
//						val.add(new SimpleStringProperty(msg));
//						val.add(new SimpleStringProperty("success"));
//						val.add(new SimpleStringProperty(""));
						ddlDmlpo.addData(val);
					} catch (Exception e1) {
						e1.printStackTrace();
						saveBtn.setDisable(true);
						ObservableList<StringProperty> val = FXCollections.observableArrayList();
						String 	msg = "failed : " + e1.getMessage();
						if(dpo.getDbVendor().toUpperCase().equals( DbVendor.db2.toUpperCase())) {
							msg += "\n"+Db2ErrorCode.translateErrMsg(msg);
						}
						val.add(RunSQLHelper.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ));
						val.add(RunSQLHelper.createReadOnlyStringProperty(msg)); 
						val.add(RunSQLHelper.createReadOnlyStringProperty("failed")); 
						val.add(RunSQLHelper.createReadOnlyStringProperty("" ));
						
						ddlDmlpo.addData(val);
					}
				}
				CacheTabView.rmUpdateData(tabId);
			}

			// 插入操作
			List<ObservableList<StringProperty>> dataList = CacheTabView.getAppendData(tabId);
			for (ObservableList<StringProperty> os : dataList) {
				try {
					String msg = InsertDao.execInsert(conn, tabName, os, fpos);
					ObservableList<StringProperty> val = FXCollections.observableArrayList();
					val.add(new SimpleStringProperty(msg));
					val.add(new SimpleStringProperty("success"));
					val.add(new SimpleStringProperty(""));
					ddlDmlpo.addData(val);

					// 删除缓存数据
					CacheTabView.rmAppendData(tabId);
					// 对insert 的数据保存后 , 不能再修改
					List<StringProperty> templs = new ArrayList<>();
					for (int i = 0; i < fpos.size(); i++) {
						StringProperty sp = os.get(i);
						StringProperty newsp = new SimpleStringProperty(sp.get());
						templs.add(newsp);
						CommonUtility.prohibitChangeListener(newsp, sp.get());
					}
					os.clear();
					for (int i = 0; i < templs.size(); i++) {
						StringProperty newsp = templs.get(i);
						os.add(newsp);
					}

				} catch (Exception e1) {
					e1.printStackTrace();
//					saveBtn.setDisable(true);
					ObservableList<StringProperty> val = FXCollections.observableArrayList();
					val.add(new SimpleStringProperty(e1.getMessage()));
					val.add(new SimpleStringProperty("fail."));
					val.add(new SimpleStringProperty(""));
					ddlDmlpo.addData(val);
				}
			}

			// 保存按钮禁用
//			FlowPane fp = (FlowPane) saveBtn.getParent();
//			fp.getChildren().get(0).setDisable(true);
			saveBtn.setDisable(true);
			RunSQLHelper.showExecuteSQLInfo(ddlDmlpo);

		}

	} 
	
	public static void deleteData() { 
		// 获取当前的table view
		FilteredTableView<ObservableList<StringProperty>> table = ComponentGetter.dataTableView();
		String tabId = table.getId();

		String tabName = CacheTabView.getTableName(tabId);
		Connection conn = CacheTabView.getDbConn(tabId);
		ObservableList<SqlFieldPo> fpos = CacheTabView.getFields(tabId);

		ObservableList<ObservableList<StringProperty>> vals = table.getSelectionModel().getSelectedItems();
		
		// 行号集合
		List<String> temp = new ArrayList<>();

		// 执行sql 后的信息 (主要是错误后显示到界面上)
		DbTableDatePo ddlDmlpo = DbTableDatePo.executeInfoPo();

		try {
			for (int i = 0; i < vals.size(); i++) {
				ObservableList<StringProperty> sps = vals.get(i);
				String ro = sps.get(sps.size() - 1).get();
				temp.add(ro);
				String msg = DeleteDao.execDelete(conn, tabName, sps, fpos);
				ObservableList<StringProperty> val = FXCollections.observableArrayList();

				val.add(RunSQLHelper.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ));
				val.add(RunSQLHelper.createReadOnlyStringProperty(msg)); 
				val.add(RunSQLHelper.createReadOnlyStringProperty("success")); 
				val.add(RunSQLHelper.createReadOnlyStringProperty("" ));
				
				ddlDmlpo.addData(val);

			}
			for (String str : temp) {
				CacheTabView.deleteTabDataRowNo(tabId, str);
			}

		} catch (Exception e1) {
			ObservableList<StringProperty> val = FXCollections.observableArrayList();					
			val.add(RunSQLHelper.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ));
			val.add(RunSQLHelper.createReadOnlyStringProperty(e1.getMessage() )); 
			val.add(RunSQLHelper.createReadOnlyStringProperty("fail.")); 
			val.add(RunSQLHelper.createReadOnlyStringProperty("" ));
			
			ddlDmlpo.addData(val);
		} finally {
			RunSQLHelper.showExecuteSQLInfo(ddlDmlpo);
		}
	
	}
	// 复制选择的 行数据 插入到表格末尾
	public static void copyData() {

		// 获取当前的table view
		FilteredTableView<ObservableList<StringProperty>> table = ComponentGetter.dataTableView();

		String tabId = table.getId();
		// 获取字段属性信息
		ObservableList<SqlFieldPo> fs = CacheTabView.getFields(tabId);
		
		// 选中的行数据
		ObservableList<ObservableList<StringProperty>> vals = ComponentGetter.dataTableViewSelectedItems();
		try {
			// 遍历选中的行
			for (int i = 0; i < vals.size(); i++) {
				// 一行数据, 提醒: 最后一列是行号
				ObservableList<StringProperty> sps = vals.get(i);
				// copy 一行
				ObservableList<StringProperty> item = FXCollections.observableArrayList();
				int newLineidx = ConfigVal.newLineIdx++;
				for (int j = 0 ; j < fs.size(); j++) {
					StringProperty strp = sps.get(j);
				 
					StringProperty newsp = new SimpleStringProperty(strp.get());
					int dataType = fs.get(j).getColumnType().get();
					CommonUtility.newStringPropertyChangeListener(newsp, dataType);
					item.add(newsp);
				}
				item.add(new SimpleStringProperty(newLineidx + "")); // 行号， 新行的行号没什么用
				CacheTabView.appendDate(tabId, newLineidx, item); // 可以防止在map中被覆盖
				table.getItems().add(item);

			}
			table.scrollTo(table.getItems().size() - 1);

			// 保存按钮亮起
			ComponentGetter.dataPaneSaveBtn().setDisable(false);
		} catch (Exception e2) {
			MyAlert.errorAlert( e2.getMessage());
		}
	
	}
	
	//refreshData
	public static void refreshData(boolean isLock) {
		String id = ComponentGetter.currentDataTabID(); 
		String sql = CacheTabView.getSelectSQl(id);
		Connection conn = CacheTabView.getDbConn(id);
	    String connName = 	CacheTabView.getConnName(id);
		if (conn != null) {
			//TODO 关闭当前tab
			var dataTab = ComponentGetter.dataTab;
			int selidx = dataTab.getSelectionModel().getSelectedIndex(); 
//			dataTab.getTabs().remove(selidx); 
			CommonAction.clearDataTable(selidx);
			RunSQLHelper.runSQLMethodRefresh( DBConns.get(connName),  sql, selidx+"", isLock);
		}	
	}
	
	//addData // 添加一行数据
	public static void addData() {
		var vbox = ComponentGetter.currentDataVbox();//(VBox) btn.getParent().getParent();
		var tbv = (FilteredTableView<ObservableList<StringProperty>>) vbox.getChildren().get(1);
		tbv.scrollTo(0);
		String tabid = ComponentGetter.currentDataTabID() ;// btn.getParent().getId();
		int newLineidx = ConfigVal.newLineIdx++;
		ObservableList<SqlFieldPo> fs = CacheTabView.getFields(tabid);
		ObservableList<StringProperty> item = FXCollections.observableArrayList();
		for (int i = 0; i < fs.size(); i++) {
			SimpleStringProperty sp = new SimpleStringProperty();
			// 添加监听. 保存时使用 newLineIdx
			CommonUtility.newStringPropertyChangeListener(sp, fs.get(i).getColumnType().get());
			item.add(sp);
		}
		item.add(new SimpleStringProperty(newLineidx + "")); // 行号， 没什么用
		CacheTabView.appendDate(tabid, newLineidx, item); // 可以防止在map中被覆盖
		tbv.getItems().add(0, item);

		// 发生亮起保存按钮
		AnchorPane fp = ComponentGetter.dataAnchorPane(tbv);
		fp.getChildren().get(0).setDisable(false);
	
	}
	
	
	
	// 判断是否需要删除两边的单引号'
	private static String needTrimChar(String value) {
		String rs = value;
		char c1 = value.charAt(0);
		char c2 = value.charAt(value.length() - 1 );
		if( c1 == c2 && c1 == '\'') {
			rs =  StrUtils.trimChar(value, "'");
		}
		return rs;
	}
	
	// 更新查询结果中所有数据对应列的值
	public static void updateAllColumn(int colIdx,String value) {
		RsVal rv = CommonAction.tableInfo();
		value = needTrimChar(value);
		if("null".equals(value)) {
			value = "<null>";
		}
		FilteredTableView<ObservableList<StringProperty>> dataTableView = rv.dataTableView;
		ObservableList<ObservableList<StringProperty>> alls = dataTableView.getItems();
		for(ObservableList<StringProperty> ls : alls) {
			StringProperty  tmp = ls.get(colIdx);
			tmp.setValue(value);
		}
		dataSave();
	}
	
	// 更新查询结果中选中的数据 对应列的值
	public static void updateSelectedDataColumn(int colIdx,String value) {
		RsVal rv = CommonAction.tableInfo();
		value = needTrimChar(value);
		if("null".equals(value)) {
			value = "<null>";
		}
		 
		ObservableList<ObservableList<StringProperty>> alls = ComponentGetter.dataTableViewSelectedItems();
		for(ObservableList<StringProperty> ls : alls) {
			StringProperty  tmp = ls.get(colIdx);
			tmp.setValue(value);
		}
		dataSave();
	}
	
	
	// 获取tree 节点中的 table 的sql
	public static void findTable() {
		RsVal rv = CommonAction.tableInfo();
		DbConnectionPo dbcp = rv.dbconnPo;
		if(dbcp == null ) {
			return ;
		}
		String tbn = rv.tableName;
		String key = dbcp.getConnName() + "_" +dbcp.getDefaultSchema();
		List<TablePo> tbs = TreeObjCache.tableCache.get(key);
		
		TablePo tbrs = null; 
		for(TablePo po: tbs) {
			if( po.getTableName().equals(tbn) ){
				tbrs = po;
				break;
			}
		}
		if( tbrs != null)
		TreeObjAction.showTableSql(dbcp, tbrs, tbn);
		
	}
	
	
	/**
	 * bookmark next
	 * @param isNext true: 从上往下找
	 */
	public static void nextBookmark( boolean isNext) {
		  
		MyCodeArea codeArea = (MyCodeArea) SqlEditor.getCodeArea();  
		int idx = codeArea.getCurrentParagraph(); // 获取当前行号
		List<String> strs = codeArea.getMylineNumber().getLineNoList();
		
		
		int moveto = -1;
		if(strs !=null && strs.size() > 0) {
			List<Integer> rs = StrUtils.StrListToIntList(strs);
			if(! isNext) {
				rs.sort(Comparator.comparing(Integer::intValue).reversed()); 
			} 
			moveto = rs.get(0) - 1;
			for(Integer v : rs) {
				int i = v - 1;
				
				if(isNext) {
					if(idx < i ) {
						moveto = i ; 
						break;
					}
				}else {
					if(idx > i ) {
						moveto = i ; 
						break;
					}
				}
				
				
			}
		}
		if(moveto > -1 ) {
			codeArea.moveTo(moveto, 0);
			codeArea.showParagraphAtTop(moveto < 10 ? 0 : (moveto - 9));
		}
	
	}
}
