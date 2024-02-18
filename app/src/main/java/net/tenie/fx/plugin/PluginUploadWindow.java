package net.tenie.fx.plugin;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.ui.UiTools;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * 插件上传界面
 * @author tenie
 *
 */
public class PluginUploadWindow {
	private Stage stage;
	private VBox pluginUploadBox = new VBox();


	public PluginUploadWindow() {



		Label pluginName = new Label("Plugin Name");
		TextField tfPluginName = new TextField();
		tfPluginName.getStyleClass().add("myTextField");
		tfPluginName.setDisable(true);

		Label pluginPackage = new Label("Plugin Package ");
		TextField tfPluginPackage = new TextField();
		tfPluginPackage.getStyleClass().add("myTextField");
		tfPluginPackage.setDisable(true);


		Label pluginDescribe = new Label("Plugin Describe");
		TextField tfPluginDescribe = new TextField();
		tfPluginDescribe.getStyleClass().add("myTextField");
		tfPluginDescribe.setDisable(true);


		Label pluginvVersion = new Label("Plugin Version");
		TextField tfPluginVersion = new TextField();
		tfPluginVersion.getStyleClass().add("myTextField");
		tfPluginVersion.setDisable(true);

		// 选择文件
		String filePath = "File path";
		Label lbFilePath = new Label(filePath);

		TextField tfFilePath = new TextField();
		tfFilePath.getStyleClass().add("myTextField");
		tfFilePath.setPromptText(filePath);
		tfFilePath.setOnMouseClicked(v -> {
			String fileVal = tfFilePath.getText();
			if (StrUtils.isNullOrEmpty(fileVal)) {
				File selectFile = FileOrDirectoryChooser.showOpen(filePath, "jar", stage);
				if(selectFile != null ){
					tfFilePath.setText(selectFile.getAbsolutePath());
				}

			}
		});

		HBox fileBox = new HBox();
		Button selectFile = UiTools.openJarFileBtn(tfFilePath, stage);
		selectFile.getStyleClass().add("myAlertBtn");
		fileBox.getChildren().addAll(tfFilePath, selectFile);
		HBox.setHgrow(tfFilePath, Priority.ALWAYS);

		tfFilePath.textProperty().addListener((a,b,c)->{
			if(c != null && !c.isEmpty()){
                try {
                    SqluckyPluginDelegate spd =	ReadJarLoadSqluckyPluginDelegate.loadClass(c);
               		if(spd != null){
						tfPluginName.setText(spd.pluginName());
						tfPluginDescribe.setText(spd.pluginDescribe());
						tfPluginVersion.setText(spd.version());
						tfPluginPackage.setText(spd.pluginCode());
					}
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
		});

		// 上传按钮
		Button uploadBtn = new Button("上传");
		uploadBtn.getStyleClass().add("myAlertBtn");

		BooleanProperty tf = new SimpleBooleanProperty(true);
		uploadBtn.disableProperty().bind( tf);
		uploadBtn.setOnAction(e->{
			String pName =tfPluginName.getText();
			String pDescribe =tfPluginDescribe.getText();
		    String pVersion =tfPluginVersion.getText();
			String jarFile = tfFilePath.getText();
			String pPackage = tfPluginPackage.getText();
			// 先验证有没有上传权限
			boolean authority = PluginManageAction.checkUploadAuthority();
			if(authority == false){
				Platform.runLater(()->{
					MyAlert.errorAlert("没有上传权限", true);
					stage.close();
				});
				return;
			}
            try {
                PluginManageAction.uploadPluginFile(pName, pDescribe, pVersion, jarFile, pPackage);
            } catch (IOException ex) {
				Platform.runLater(()->{
					MyAlert.errorAlert("上传失败", true);
					stage.close();
				});

                throw new RuntimeException(ex);
            }
			Platform.runLater(()->{
				MyAlert.alertWait("上传成功");
				stage.close();
			});

        });
		textFieldCheckEmpty(List.of(tfPluginName,tfPluginDescribe,tfPluginVersion,  tfFilePath, tfPluginPackage), tf);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 10, 10, 10));
		grid.setMinWidth(300.0);

		int idxi = 1;
		ColumnConstraints c1 = new ColumnConstraints();
		ColumnConstraints c2= new ColumnConstraints();
		c1.setMinWidth(100.0);
		c2.setMinWidth(200.0);
		grid.getColumnConstraints().addAll(c1, c2);

		grid.add(lbFilePath, 0, idxi);
		grid.add(fileBox, 1,idxi++);
		grid.add(pluginName, 0, idxi);
		grid.add(tfPluginName, 1,idxi++);

		grid.add(pluginPackage, 0, idxi);
		grid.add(tfPluginPackage, 1,idxi++);

		grid.add(pluginDescribe, 0, idxi);
		grid.add(tfPluginDescribe, 1,idxi++);
		grid.add(pluginvVersion, 0, idxi);
		grid.add(tfPluginVersion, 1,idxi++);

		grid.add(uploadBtn, 1,idxi);
		pluginUploadBox.getChildren().add(grid);


	}

	/**
	 *
	 * @param TextFieldList
	 * @param tf
	 */
	private void textFieldCheckEmpty(List<TextField> TextFieldList ,BooleanProperty tf){
		for(TextField textf : TextFieldList){
			textf.setMinWidth(180.0);
			textf.setMinWidth(180.0);
			textf.textProperty().addListener((a,b, c)->{
				boolean tfval = textFieldCheckEmpty(TextFieldList);
				if(tfval){
					tf.setValue(true);
				}else {
					tf.setValue(false);
				}
			});
		}

	}

	private boolean textFieldCheckEmpty(List<TextField> TextFieldList){
		boolean tf = false;
		for(TextField textf : TextFieldList){
			tf = tf || textf.getText().trim().isEmpty();
		}
		return tf;
	}
	
	// 显示窗口
	public void show() {

		var stage = CreateModalWindow(pluginUploadBox);
		stage.show();
	}
	
	// 创建一个窗体
	public   Stage CreateModalWindow(VBox vb) {
		vb.getStyleClass().add("myPluginManager-vbox");
		vb.setPrefWidth(450);
		vb.maxWidth(450);

		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
		
		SqluckyStage sqlStage = new SqluckyStage(vb);

		stage = sqlStage.getStage();
		stage.setTitle("Plugin Upload");
		Scene scene = sqlStage.getScene();
		
		scene.getAccelerators().put(escbtn, () -> {
			stage.close();
		});
		scene.getAccelerators().put(spacebtn, () -> {
			stage.close();
		});

		stage.initModality(Modality.APPLICATION_MODAL);
		
		stage.setMaximized(false);
		stage.setResizable(false);
		stage.setOnHidden(e->{
		});
		return stage;
	}

}
