package net.tenie.fx.plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import net.tenie.Sqlucky.sdk.utility.JsonTools;
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
	
	/**
	 * 创建插件数据显示的表格
	 * @param window
	 * @param pluginBox
	 * @param describe
	 * @param enable
	 * @param disable
	 */
	public static void createTable(PluginManageWindow window, VBox  pluginBox , MyCodeArea describe, JFXButton enable , JFXButton disable,JFXButton download  ) {
		Connection conn = SqluckyAppDB.getConn();
		try {
		    // 查询
			SheetTableData sheetDaV   = TableViewUtil.sqlToSheet(sql, conn, "PLUGIN_INFO", null);
			// 获取表
			FilteredTableView<ResultSetRowPo>  allPluginTable = sheetDaV.getInfoTable();
			  window.setSheetDaV(sheetDaV);
			  window.setAllPluginTable(allPluginTable);
			// 表不可编辑
			allPluginTable.editableProperty().bind(new SimpleBooleanProperty(false));
			// 选中事件
			allPluginTable.getSelectionModel().selectedItemProperty().addListener((ob, ov ,nv)->{
				describe.clear();
				if(nv == null) return;
				String strDescribe = nv.getValueByFieldName("Describe");
				 
				describe.appendText(strDescribe);
				String downloadStatus = nv.getValueByFieldName("Download Status");
				if("".equals(downloadStatus)) {
					disable.setDisable(true);
					enable.setDisable(true);
					download.setDisable(false);
				}else { 
					download.setDisable(true);
					String loadStatus = nv.getValueByFieldName("Load Status");
					if("√".equals(loadStatus)) {
						disable.setDisable(false);
						enable.setDisable(true);
					}else {
						disable.setDisable(true);
						enable.setDisable(false);
					}
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
	public static void queryServerPluginInfo(SheetTableData sheetDaV , FilteredTableView<ResultSetRowPo> allPluginTable) {
		if( CommonUtility.isLogin("Please Login First") == false) {
			return ;
		}
		Map<String, String> vals = new HashMap<>();
		vals.put("EMAIL", ConfigVal.SQLUCKY_EMAIL.get());	
		vals.put("PASSWORD", ConfigVal.SQLUCKY_PASSWORD.get());
		String content = "";
		Connection conn = SqluckyAppDB.getConn();
		try {
			content = HttpUtil.post(ConfigVal.getSqluckyServer()+"/sqlucky/queryAllPlugin", vals);
			System.out.println("content ==" + content);
			 List<PluginInfoPO> ls=  JsonTools.jsonToList(content, PluginInfoPO.class);
			 System.out.println(ls);
			 List<Object> localVals = PoDao.selectFieldVal(conn, new  PluginInfoPO(), "PLUGIN_CODE");
			 int count = 0;
			 for(PluginInfoPO info : ls) {
				 	String tmpPluginCode = info.getPluginCode();
				 	if(localVals.contains(tmpPluginCode)) {
				 		continue;
				 	}
				 	PluginInfoPO ppo = new PluginInfoPO();
					ppo.setPluginCode(info.getPluginCode());
					ppo.setPluginName(info.getPluginName());
					ppo.setPluginCode(info.getPluginCode());
					ppo.setPluginDescribe(info.getPluginDescribe());
					ppo.setVersion(info.getVersion());
					ppo.setDownloadStatus(0);
					ppo.setReloadStatus(0);
					
					ppo.setCreatedTime(new Date());
					PoDao.insert(conn, ppo);
					count++;
			 }
			 
			 MyAlert.alert("同步插件信息", "同步到新插件" + count + "个");
			 if(count > 0) {
					PluginManageAction.queryAction("", sheetDaV , allPluginTable);
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
		
	}
	
	public static void downloadPlugin(FilteredTableView<ResultSetRowPo>  allPluginTable) {
		//
		String pluginName = getSelectPluginName(allPluginTable);
		Map<String, String> vals = new HashMap<>();
		vals.put("EMAIL", ConfigVal.SQLUCKY_EMAIL.get());	
		vals.put("PASSWORD", ConfigVal.SQLUCKY_PASSWORD.get());
		vals.put("PLUGIN_NAME", pluginName);
		
		
		String modelPath = CommonUtility.sqluckyAppModsPath();
		HttpUtil.downloadByPost(ConfigVal.getSqluckyServer()+"/sqlucky/pluginDownload",modelPath, vals);
	}
	
	
	
	public static String getSelectPluginName(FilteredTableView<ResultSetRowPo>  allPluginTable) {
		ResultSetRowPo  selectRow = allPluginTable.getSelectionModel().getSelectedItem();
		String name = selectRow.getValueByFieldName("Name");
		return name;
	}
	
	public static void main(String[] args) {
		
		String  v = System.getProperty("jdk.module.path");
		System.out.println(v);
//		System.out.println(System.getProperties().toString());
//		String str = "[{\"id\":1,\"pluginName\":\"test plugin name\",\"pluginCode\":\"1111\",\"pluginDescribe\":\"test test test \",\"comment\":\"test commtest comm\",\"filePath\":\"D:/test/instw.db\",\"createdAt\":null,\"updatedAt\":null}]";
//		 List<PluginInfoPO> ls=  JsonTools.jsonToList(str, PluginInfoPO.class);
//		 System.out.println(ls);
	}
 
}
