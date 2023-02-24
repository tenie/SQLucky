package net.tenie.plugin.backup.component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

public class WorkDataBackupController implements Initializable {

	@FXML private Button bakBtn; 
	@FXML private TextField bakName;
	@FXML private TextField privateKey;
	@FXML private JFXCheckBox dbCB;
	@FXML private JFXCheckBox scriptCB;
	@FXML private JFXCheckBox modelCB;
	@FXML private JFXCheckBox pkCB;
	
	// 下载界面
	@FXML private Button syncBtn;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		 uploadPage();
		 downloadPage();
	}

	public void uploadPage() {
		// 按钮亮起
		bakBtn.setDisable(true);
		bakBtn.disableProperty().bind(bakName.textProperty().isEmpty());

		// 密钥默认不能输入只有勾选了才能输入
		privateKey.disableProperty().bind(pkCB.selectedProperty().not());
		// 使用密钥选择, 密钥输入框焦点
		pkCB.selectedProperty().addListener((ob, old, nw) -> {
			if (!nw) {
				privateKey.clear();
			} else {
				privateKey.requestFocus();
			}
		});

		// 备份按钮
		bakBtn.setOnAction(e -> {
			// 登入校验
			String sqlEmail = ConfigVal.SQLUCKY_EMAIL;
			String sqlPW = ConfigVal.SQLUCKY_PASSWORD;
			if(StrUtils.isNullOrEmpty(sqlEmail) || StrUtils.isNullOrEmpty(sqlPW)) {
				AppComponent app =  ComponentGetter.appComponent;
				app.showSingInWindow();
				return ;
			}
			BackupInfoPO po = new BackupInfoPO(bakName, privateKey, dbCB, scriptCB, modelCB, pkCB);
			WorkDataBackupAction.BackupBtn(po);
		});
	}
	
	public void downloadPage() {
		syncBtn.setOnAction(e->{
			
		});
	}
	 
	
	static public void showFxml() {
		String fxml = "workdataBackup.fxml";
		try {
			Stage  stage = new Stage();
		    ComponentGetter.dataTransferStage = stage;
			stage.initModality(Modality.WINDOW_MODAL);
//			stage.initOwner(stg);
			stage.setTitle("Top Stage With Modality");

			URL url = WorkDataBackupController.class.getResource(fxml);
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
