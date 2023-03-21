package net.tenie.fx.plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import org.controlsfx.control.tableview2.FilteredTableView;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.db.PoDao;
import net.tenie.Sqlucky.sdk.db.ResultSetPo;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.PluginInfoPO;
import net.tenie.Sqlucky.sdk.po.SheetTableData;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.DBTools;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TableViewUtil;
import net.tenie.fx.main.Restart;

public class PluginManageWindow {
	private VBox pluginManageBox = new VBox();
	private FlowPane SearchPane = new FlowPane();
	private JFXButton searchBtn = new JFXButton("Search Plugin");
	private JFXTextField searchText = new JFXTextField();

	private VBox  pluginBox = new VBox();

	// 描述
	private MyCodeArea describe = new MyCodeArea();

	// 操作面板(下载, 退出)
	private FlowPane optionPane = new FlowPane();
	// 下载按钮
	private JFXButton download = new JFXButton("Download");
	private JFXButton disable = new JFXButton("Disable");
	private JFXButton enable = new JFXButton("Enable");
	
	// 所有插件表
	SheetTableData sheetDaV = null;
	FilteredTableView<ResultSetRowPo> allPluginTable = null;

	public PluginManageWindow() {
		searchBtn.setGraphic(IconGenerator.svgImageDefActive("search"));
		searchText.getStyleClass().add("myTextField");
		// 回车后触发查询按钮
		searchText.setOnKeyPressed(val->{
			 if(val.getCode() == KeyCode.ENTER ){ 
				 queryAction(searchText.getText());
			 }
		});
		searchBtn.setOnMouseClicked(e->{
			 queryAction(searchText.getText());
		});
		SearchPane.getChildren().addAll(searchBtn, searchText );
		SearchPane.setMinHeight(35);
		SearchPane.setPrefHeight(35);
		SearchPane.getStyleClass().add("topPadding5");

		// 操作面板
		optionPane.getChildren().addAll(download, disable, enable);
		optionPane.setMinHeight(35);
		optionPane.setPrefHeight(35);
		optionPane.getStyleClass().add("topPadding5");
		initBtn();
		
		describe.setPrefHeight(80);
		describe.setMinHeight(80);
		describe.setEditable(false);
		pluginManageBox.getChildren().addAll(SearchPane, pluginBox, describe, optionPane);
		VBox.setVgrow(pluginBox, Priority.ALWAYS);

	}
	
	public void initBtn() {
		download.setGraphic(IconGenerator.svgImageDefActive("cloud-download"));
		
		disable.setGraphic(IconGenerator.svgImageDefActive("toggle-off"));
		enable.setGraphic(IconGenerator.svgImageDefActive("toggle-on"));
		
		disable.setOnAction(e->{
			enableOrDisableAction(false);
		});

		enable.setOnAction(e->{
			enableOrDisableAction(true);
		});
		
		disable.setDisable(true);
		enable.setDisable(true);
		download.setDisable(true);
	}
	
	// 插件启用/禁用 
	public   void enableOrDisableAction(boolean isEnable) {
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
	
	String sql = "select" 
			+ " ID , "
			+ " PLUGIN_NAME as \"Name\" , "
			+ " VERSION ,"
			+ " case when PLUGIN_DESCRIBE is null then '' else PLUGIN_DESCRIBE end as \"Describe\" ,"
			+ " case when  DOWNLOAD_STATUS = 1 then '√' else '' end  as \"Download Status\" ,"
			+ " case when  RELOAD_STATUS = 1 then '√' else '' end  as  \"Load Status\" "
			+ " from PLUGIN_INFO";
	
	public void createTable() {
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
	public void queryAction(String  queryStr) {
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
	
	// 显示窗口
	public void show() {
		createTable();
		var stage = CreateModalWindow(pluginManageBox);
		stage.show();
		searchText.requestFocus();
	}
	
	// 创建一个窗体
	public static Stage CreateModalWindow(VBox vb) {
		Stage	stage = new Stage();
		vb.getStyleClass().add("myPluginManager-vbox");

		Scene scene = new Scene(vb);
		
		vb.setPrefWidth(720);
		vb.maxWidth(720);

		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
		scene.getAccelerators().put(escbtn, () -> {
			stage.close();
		});
		scene.getAccelerators().put(spacebtn, () -> {
			stage.close();
		});

		CommonUtility.loadCss(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		
		stage.getIcons().add(ComponentGetter.LogoIcons);
		stage.setMaximized(false);
		stage.setResizable(false);
		stage.setOnHidden(e->{
		});
		return stage;
	}
}
