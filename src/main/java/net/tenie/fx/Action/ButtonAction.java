package net.tenie.fx.Action;

import java.sql.Connection;
import java.util.ArrayList;
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
import net.tenie.fx.dao.InsertDao;
import net.tenie.fx.dao.UpdateDao;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.fx.utility.EventAndListener.RunSQLHelper;
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
			DbTableDatePo ddlDmlpo = new DbTableDatePo();
			ddlDmlpo.addField("Info");
			ddlDmlpo.addField("Status");

			if (!modifyData.isEmpty()) {
				for (String key : modifyData.keySet()) {
					// 获取对应旧数据
					ObservableList<StringProperty> old = CacheTableDate.getold(key);
					ObservableList<StringProperty> newd = modifyData.get(key);
					// 拼接update sql
					try {
						String msg = UpdateDao.execUpdate(conn, tabName, newd, old, fpos);
						ObservableList<StringProperty> val = FXCollections.observableArrayList();
						val.add(new SimpleStringProperty(msg));
						val.add(new SimpleStringProperty("success"));
						val.add(new SimpleStringProperty(""));
						ddlDmlpo.addData(val);
					} catch (Exception e1) {
						e1.printStackTrace();
						saveBtn.setDisable(true);
						ObservableList<StringProperty> val = FXCollections.observableArrayList();
						val.add(new SimpleStringProperty(e1.getMessage()));
						val.add(new SimpleStringProperty("fail."));
						val.add(new SimpleStringProperty(""));
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
	
}
