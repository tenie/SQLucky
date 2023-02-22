package net.tenie.plugin.backup.component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;

public class WorkDataBackupController implements Initializable {

	@FXML private Button bakBtn; 
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		bakBtn.setOnAction(e->{
			WorkDataBackupAction.BackupBtn();
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
