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
import net.tenie.fx.PropertyPo.CacheTableDate;
import net.tenie.fx.PropertyPo.DbTableDatePo;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.MyCodeArea;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.component.TreeItem.TreeObjCache;
import net.tenie.fx.dao.InsertDao;
import net.tenie.fx.dao.UpdateDao;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.lib.po.DbConnectionPo;
import net.tenie.lib.po.TablePo;
import net.tenie.lib.tools.StrUtils;

public class ButtonAction {
	
	
	public static void dataSave(Button saveBtn) {
		String tabId = saveBtn.getParent().getId();
		String tabName = CacheTableDate.getTableName(tabId);
//		ObservableList<ObservableList<StringProperty>> alldata = CacheTableDate.getData(tabId);
		Connection conn = CacheTableDate.getDBConn(tabId);
		if (tabName != null && tabName.length() > 0) {
			// 字段
			ObservableList<SqlFieldPo> fpos = CacheTableDate.getCols(tabId);
			// 待保存数据
			Map<String, ObservableList<StringProperty>> modifyData = CacheTableDate.getModifyData(tabId);
			// 执行sql 后的信息 (主要是错误后显示到界面上)
//			DbTableDatePo ddlDmlpo = new DbTableDatePo();
			DbTableDatePo ddlDmlpo = DbTableDatePo.executeInfoPo();
//			ddlDmlpo.addField("Info");
//			ddlDmlpo.addField("Status");
//			ddlDmlpo.addField("Current Time");
//			ddlDmlpo.addField("Execute SQL Info");
//			ddlDmlpo.addField("Execute SQL");
			

			if (!modifyData.isEmpty()) {
				for (String key : modifyData.keySet()) {
					// 获取对应旧数据
					ObservableList<StringProperty> old = CacheTableDate.getold(key);
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
						
						val.add(RunSQLHelper.createReadOnlyStringProperty(StrUtils.dateToStrL( new Date()) ));
						val.add(RunSQLHelper.createReadOnlyStringProperty(e1.getMessage())); 
						val.add(RunSQLHelper.createReadOnlyStringProperty("fail")); 
						val.add(RunSQLHelper.createReadOnlyStringProperty("" ));
						
						
//						val.add(new SimpleStringProperty(e1.getMessage()));
//						val.add(new SimpleStringProperty("fail."));
//						val.add(new SimpleStringProperty(""));
						ddlDmlpo.addData(val);
					}
				}
				CacheTableDate.rmUpdateData(tabId);
			}

			// 插入操作
			List<ObservableList<StringProperty>> dataList = CacheTableDate.getAppendData(tabId);
			for (ObservableList<StringProperty> os : dataList) {
				try {
					String msg = InsertDao.execInsert(conn, tabName, os, fpos);
					ObservableList<StringProperty> val = FXCollections.observableArrayList();
					val.add(new SimpleStringProperty(msg));
					val.add(new SimpleStringProperty("success"));
					val.add(new SimpleStringProperty(""));
					ddlDmlpo.addData(val);

					// 删除缓存数据
					CacheTableDate.rmAppendData(tabId);
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
		FilteredTableView<ObservableList<StringProperty>> dataTableView = rv.dataTableView;
		ObservableList<ObservableList<StringProperty>> alls = dataTableView.getItems();
		for(ObservableList<StringProperty> ls : alls) {
			StringProperty  tmp = ls.get(colIdx);
			tmp.setValue(value);
		}
		dataSave(rv.saveBtn);
	}
	
	// 更新查询结果中选中的数据 对应列的值
	public static void updateSelectedDataColumn(int colIdx,String value) {
		RsVal rv = CommonAction.tableInfo();
		value = needTrimChar(value);
		 
		ObservableList<ObservableList<StringProperty>> alls = ComponentGetter.dataTableViewSelectedItems();
		for(ObservableList<StringProperty> ls : alls) {
			StringProperty  tmp = ls.get(colIdx);
			tmp.setValue(value);
		}
		dataSave(rv.saveBtn);
	}
	
	
	// 获取tree 节点中的 table
	public static void findTable() {
		RsVal rv = CommonAction.tableInfo();
		DbConnectionPo dbcp = rv.dbc;
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
