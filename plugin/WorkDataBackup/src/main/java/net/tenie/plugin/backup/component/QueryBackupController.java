package net.tenie.plugin.backup.component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SqluckyTableView;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.net.HttpUtil;

public class QueryBackupController implements Initializable {
	private static String httpUrl = ConfigVal.SQLUCKY_URL+"/sqlucky/confUpload";
	@FXML private Button selectBtn;
	@FXML private VBox	queryBox;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		List<String >  colName = new ArrayList<>();
		List<Map<String, String>> vals = new ArrayList<>();
		colName.add("col1");
		
		Map<String, String> m1 = new HashMap<>();
		m1.put("col1", "v1");
		Map<String, String> m2 = new HashMap<>();
		m2.put("col1", "v2");
		
		vals.add(m1);
		vals.add(m2);
		
		
		var sheetDaV = SqluckyTableView.dataToSheet(colName, vals);
		// 获取表
		var allPluginTable = sheetDaV.getInfoTable();
		// 表不可编辑
		allPluginTable.editableProperty().bind(new SimpleBooleanProperty(false));
		// 选中事件
//		allPluginTable.getSelectionModel().selectedItemProperty().addListener( );
		// 表放入界面
		queryBox.getChildren().add(allPluginTable);
	}
	
	
	private static void httpGetData() {
		HttpUtil.post1(null, null);
	}
	
	static public void showFxml() {
		String fxml = "/workBackupFxml/queryBackup.fxml";
		try {
			Stage  stage = new Stage();
		    ComponentGetter.dataTransferStage = stage;
			stage.initModality(Modality.WINDOW_MODAL);
//			stage.initOwner(stg);
			stage.setTitle("Query");
//			URL url = getClass().getResource(fxml);
			URL url =  WorkDataBackupController.class.getResource(fxml);
			Parent root = FXMLLoader.load(url);
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
	}
	

}
