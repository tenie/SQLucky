package net.tenie.plugin.backup.component;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.tableview2.FilteredTableView;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SqluckyTableView;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.JsonTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.net.HttpUtil;

public class QueryBackupController implements Initializable {
	private static String httpUrl = ConfigVal.getSqluckyServer()+"/sqlucky/queryAllBackup";
	private static String delUrl = ConfigVal.getSqluckyServer()+"/sqlucky/delBackup";
	@FXML private Button selectBtn;
	@FXML private Button queryBtn;
	@FXML private VBox	queryBox;
	@FXML private Button delBtn;
	
	@FXML private TextField queryVal;
	
	MaskerPane masker = new MaskerPane();
	
	private String selectDataID = "";
	private static SimpleStringProperty  idVal = new SimpleStringProperty("");
	private static SimpleStringProperty  nameVal = new SimpleStringProperty("");
	private FilteredTableView<ResultSetRowPo>   dataTable ;
	private static Stage  stage ;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 查询按钮
		queryBtn.setOnAction(e->{
			String queryText = queryVal.getText();
			queryHelper(queryText);
		});
		
		// selectBtn
//		selectBtn.setDisable(dataTable.getSelectionModel().isEmpty());
		selectBtn.setOnAction(v->{
			stage.close();
		});
		
//		dataTable.getSelectionModel().isEmpty()
		// delete btn
		delBtn.disableProperty().bind(selectBtn.disabledProperty());
		delBtn.setOnAction(v->{
			try {
				boolean sure = MyAlert.myConfirmationShowAndWait("Delete " + nameVal.get() + "?");
				if(sure) {
					// 调用删除函数
					deleteBakInfo(idVal.get(), nameVal.get());
					idVal.set("");
					nameVal.set("");
					String queryText = queryVal.getText();
					queryHelper(queryText);
				}
				
			} catch (Exception e) {
				MyAlert.errorAlert("服务器报错");
				e.printStackTrace();
			
			}
		});
		
		
		// 表放入界面
		queryHelper("");
	}
	
	//
	private void queryHelper(String bakName) {
		if(queryBox.getChildren().size() > 1) {
			queryBox.getChildren().remove(1);
		}
		queryBox.getChildren().add(masker);
		Thread th =  new Thread(()->{
			String bakInfoJson = null;
			try {
				bakInfoJson = queryBakInfoFromServer(bakName);
			} catch (Exception e) {
				Platform.runLater( ()->{
					MyAlert.errorAlert("服务器请求失败", true);
					System.out.println("???????????");
					stage.close();
				});
				
				
				e.printStackTrace();
			}
			if(bakInfoJson != null) {
				dataTable = createBakInfoShowTable(bakInfoJson);
				Platform.runLater(()->{
					queryBox.getChildren().remove(masker);
					queryBox.getChildren().add(dataTable);
					selectBtn.setDisable(dataTable.getSelectionModel().isEmpty());
				});
			}
		});
		th.start();
	}
	
	 
	
	/**
	 * 从服务器获取之前备份的文件信息 FatchAllBackupName
	 * @return
	 * @throws Exception 
	 */
	private  String  queryBakInfoFromServer(String bakName) throws Exception {
		Map<String, String> pamas = new HashMap<>();
		pamas.put("EMAIL", ConfigVal.SQLUCKY_EMAIL.get());
		pamas.put("PASSWORD", ConfigVal.SQLUCKY_PASSWORD.get());
		pamas.put("BAK_NAME", bakName);
		
		String bakInfoJson = HttpUtil.post1(httpUrl, pamas);
		 
		return bakInfoJson;
	}
	
	// 调用后台删除操作, 返回删除的对象json
	private boolean deleteBakInfo(String id, String bakname) throws Exception {
		Map<String, String> pamas = new HashMap<>();
		pamas.put("EMAIL", ConfigVal.SQLUCKY_EMAIL.get());
		pamas.put("PASSWORD", ConfigVal.SQLUCKY_PASSWORD.get());
		pamas.put("BAK_ID",  id);
		pamas.put("BAK_NAME",  bakname);
		String bakInfoJson = HttpUtil.post1(delUrl, pamas);
		if(StrUtils.isNotNullOrEmpty(bakInfoJson)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 根据服务器返回的数据创建展示的表
	 * @param ls
	 * @return
	 */
	private   FilteredTableView<ResultSetRowPo>  createBakInfoShowTable(String bakInfoJson ){
		List<SqluckyBackup> ls = JsonTools.jsonToList(bakInfoJson, SqluckyBackup.class);
		List<String >  colName = new ArrayList<>();
		colName.add("Backup Name"); 
		colName.add("Created At");
		colName.add("ID");
		
		List<String> hiddenCol =  new ArrayList<>();
		hiddenCol.add("ID");
		
		List<Map<String, String>> vals =	toMap(ls);
		
		var sheetDaV = SqluckyTableView.dataToSheet(colName, vals, hiddenCol);
		// 获取表
		FilteredTableView<ResultSetRowPo>  table = sheetDaV.getInfoTable();
		// 表不可编辑
		table.editableProperty().bind(new SimpleBooleanProperty(false));
		table.getSelectionModel().selectedItemProperty().addListener((ob, ov ,nv)->{
			if(nv != null ) {
				selectBtn.setDisable(false);
				selectDataID = nv.getValueByFieldName("ID");
				String selectDataBAKName = nv.getValueByFieldName("Backup Name");
				System.out.println("selectDataID = " + selectDataID);
				idVal.setValue(selectDataID);
				nameVal.setValue(selectDataBAKName);
			}
		});
		return table;
	}
	
	private static  List<Map<String, String>>  toMap(List<SqluckyBackup>  ls){
		List<Map<String, String>> vals = new ArrayList<>();
		 
		SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SqluckyBackup sbp : ls) {
			String name = sbp.getBackupName();
			Date created = sbp.getCreatedAt();
			String dastr =  sdfs.format(created);
			Map<String, String> tmpMap = new HashMap<>();
			tmpMap.put("Backup Name", name);
			tmpMap.put("Created At", dastr);
			tmpMap.put("ID", sbp.getId().toString());
			vals.add(tmpMap);
		} 
		
		return vals;
	} 
	 
	static public Map<String, SimpleStringProperty>  showFxml() {
		String fxml = "/workBackupFxml/queryBackup.fxml";
		Parent root = null;
		try {
		    stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
//			stage.initOwner(stg);
			stage.setTitle("Query");
//			URL url = getClass().getResource(fxml);
			URL url =  WorkDataBackupController.class.getResource(fxml);
			root = FXMLLoader.load(url);
			Scene scene = new Scene(root);
		    CommonUtility.loadCss(scene); 
			stage.setScene(scene);
			stage.show();
			
			Image	img = ComponentGetter.LogoIcons; //new Image(DataTransferWindow.class.getResourceAsStream(ConfigVal.appIcon));
			stage.getIcons().add(img);
			stage.setOnCloseRequest(ev->{
				stage.hide();
				ev.consume();
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		idVal = new SimpleStringProperty();
		nameVal = new SimpleStringProperty();
		Map<String, SimpleStringProperty> rsMap = new HashMap<>();
		rsMap.put("id", idVal);
		rsMap.put("name", nameVal);
		return rsMap;
	}
	

}
