package net.tenie.fx.plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.controlsfx.control.tableview2.FilteredTableView;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.PoDao;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.PluginInfoPO;
import net.tenie.Sqlucky.sdk.po.SheetTableData;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.DBTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TableViewUtil;
import net.tenie.Sqlucky.sdk.utility.net.HttpUtil;
import net.tenie.fx.main.Restart;

public class PluginManageAction { 
 
	
	// 插件启用/禁用 
	public static  void enableOrDisableAction(boolean isEnable, FilteredTableView<ResultSetRowPo> allPluginTable) {
			ResultSetRowPo  selectRow = allPluginTable.getSelectionModel().getSelectedItem();
			
			String id = selectRow.getValueByFieldName("ID");
			
			PluginInfoPO infoPo = new PluginInfoPO();
			infoPo.setId(Integer.valueOf(id));
			PluginInfoPO valInfoPo = new PluginInfoPO();
			if(isEnable) {
				valInfoPo.setReloadStatus(1);
			}else {
				valInfoPo.setReloadStatus(0);
			}
			
			Connection conn = SqluckyAppDB.getConn();
			try {
				PoDao.update(conn, infoPo, valInfoPo);
			} catch (Exception e1) {
				e1.printStackTrace();
			}finally {
				SqluckyAppDB.closeConn(conn);
			}
			if(isEnable) {
				selectRow.setValueByFieldName("Load Status", "√");
			}else {
				selectRow.setValueByFieldName("Load Status", "");
			}
			allPluginTable.getSelectionModel().getTableView().refresh();
			Consumer< String >  ok = x -> Restart.reboot();
			 
			MyAlert.myConfirmation("Setting up requires reboot , ok ? ", ok, null);
		 
	}
	
	static String sql = "select" 
			+ " ID , "
			+ " PLUGIN_NAME as \"Name\" , "
			+ " VERSION ,"
			+ " case when PLUGIN_DESCRIBE is null then '' else PLUGIN_DESCRIBE end as \"Describe\" ,"
			+ " case when  DOWNLOAD_STATUS = 1 then '√' else '' end  as \"Download Status\" ,"
			+ " case when  RELOAD_STATUS = 1 then '√' else '' end  as  \"Load Status\" "
			+ " from PLUGIN_INFO";
	
	public static void createTable(SheetTableData sheetDaV  , VBox  pluginBox , MyCodeArea describe, JFXButton enable , JFXButton disable, FilteredTableView<ResultSetRowPo> allPluginTable ) {
		Connection conn = SqluckyAppDB.getConn();
		try {
		    // 查询
			sheetDaV = TableViewUtil.sqlToSheet(sql, conn, "PLUGIN_INFO", null);
			// 获取表
			allPluginTable = sheetDaV.getInfoTable();
			// 表不可编辑
			allPluginTable.editableProperty().bind(new SimpleBooleanProperty(false));
			// 选中事件
			allPluginTable.getSelectionModel().selectedItemProperty().addListener((ob, ov ,nv)->{
				describe.clear();
				String strDescribe = nv.getValueByFieldName("Describe");
				 
				describe.appendText(strDescribe);
				String loadStatus = nv.getValueByFieldName("Load Status");
				if("√".equals(loadStatus)) {
					disable.setDisable(false);
					enable.setDisable(true);
				}else {
					disable.setDisable(true);
					enable.setDisable(false);
				}
			});
			// 表放入界面
			pluginBox.getChildren().add(allPluginTable);
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}
	
	// 根据输入字符串查询
	public static void queryAction(String  queryStr, SheetTableData sheetDaV , FilteredTableView<ResultSetRowPo> allPluginTable) {
		Connection conn = SqluckyAppDB.getConn();
		String mysql = sql;
		if(StrUtils.isNotNullOrEmpty(queryStr)) {
			 queryStr = queryStr.toLowerCase();
			 mysql = sql + "\n where LOWER(PLUGIN_NAME) like '%"+queryStr+"%' or LOWER(PLUGIN_DESCRIBE) like '%"+queryStr+"%'";
		}
		try {
			ResultSetPo set =	DBTools.simpleSelect(conn, mysql, sheetDaV.getColss(), sheetDaV.getDbConnection());
			if(set != null ) {
				allPluginTable.setItems(set.getDatas());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}
	
 
	// 同步服务器的插件信息
	public static void queryServerPluginInfo() {
		if( CommonUtility.isLogin("Use Backup must Login") == false) {
			return ;
		}
		Map<String, String> vals = new HashMap<>();
		vals.put("EMAIL", ConfigVal.SQLUCKY_EMAIL.get());	
		vals.put("PASSWORD", ConfigVal.SQLUCKY_PASSWORD.get());
		String content = "";
		try {
			content = HttpUtil.post(ConfigVal.getSqluckyServer()+"/sqlucky/queryAllPlugin", vals);
			System.out.println("content ==" + content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
 
}
