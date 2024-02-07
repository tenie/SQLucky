package net.tenie.fx.plugin;

import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
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
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.po.SheetTableData;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.ui.UiTools;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.controlsfx.control.tableview2.FilteredTableView;

import java.io.File;
import java.io.IOException;

/**
 * 插件上传界面
 * @author tenie
 *
 */
public class PluginUploadWindow {
	private Stage stage;
	private VBox pluginUploadBox = new VBox();
	private JFXTextField searchText = new JFXTextField();


	public PluginUploadWindow() {



		Label pluginName = new Label("Plugin Name");
		TextField tfPluginName = new TextField();
		tfPluginName.getStyleClass().add("myTextField");


		Label pluginDescribe = new Label("Plugin Describe");
		TextField tfPluginDescribe = new TextField();
		tfPluginDescribe.getStyleClass().add("myTextField");


		Label pluginvVersion = new Label("Plugin version");
		TextField tfPluginVersion = new TextField();
		tfPluginVersion.getStyleClass().add("myTextField");

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
		Button selectFile = UiTools.openExcelFileBtn(tfFilePath, stage);
		selectFile.getStyleClass().add("myAlertBtn");
		fileBox.getChildren().addAll(tfFilePath, selectFile);
		HBox.setHgrow(tfFilePath, Priority.ALWAYS);



		// 上传按钮
		Button uploadBtn = new Button("上传");
		uploadBtn.getStyleClass().add("myAlertBtn");
		tfPluginName.textProperty().isEmpty();
		uploadBtn.disableProperty().bind(tfPluginName.textProperty().isEmpty());
//		uploadBtn.disableProperty().bind(
//				new ObservableValueBase<Boolean>() {
//			@Override
//			public Boolean getValue() {
//				String pName =tfPluginName.getText();
//				String pDescribe =tfPluginDescribe.getText();
//				String pVersion =tfPluginVersion.getText();
//				String jarFile = tfFilePath.getText();
//				if(pName.isEmpty() || pDescribe.isEmpty() || pVersion.isEmpty() || jarFile.isEmpty()){
//					return true;
//				}
//				return false;
//			}
//		});
		uploadBtn.setOnAction(e->{
			String pName =tfPluginName.getText();
			String pDescribe =tfPluginDescribe.getText();
		    String pVersion =tfPluginVersion.getText();
			String jarFile = tfFilePath.getText();
            try {
                PluginManageAction.uploadPluginFile(pName, pDescribe, pVersion, jarFile);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 10, 10, 10));
		grid.setMinWidth(300.0);

		int idxi = 1;
		ColumnConstraints c1 = new ColumnConstraints();
		ColumnConstraints c2= new ColumnConstraints();
		c1.setMinWidth(100.0);
		c2.setMinWidth(160.0);
		grid.getColumnConstraints().addAll(c1, c2);
		grid.add(pluginName, 0, idxi);
		grid.add(tfPluginName, 1,idxi++);
		grid.add(pluginDescribe, 0, idxi);
		grid.add(tfPluginDescribe, 1,idxi++);
		grid.add(pluginvVersion, 0, idxi);
		grid.add(tfPluginVersion, 1,idxi++);
		grid.add(lbFilePath, 0, idxi);
		grid.add(fileBox, 1,idxi++);
		grid.add(uploadBtn, 1,idxi);
		pluginUploadBox.getChildren().add(grid);


	}

   
	
	// 显示窗口
	public void show() {

		var stage = CreateModalWindow(pluginUploadBox);
		stage.show();
		searchText.requestFocus();
	}
	
	// 创建一个窗体
	public   Stage CreateModalWindow(VBox vb) {
		vb.getStyleClass().add("myPluginManager-vbox");
		vb.setPrefWidth(720);
		vb.maxWidth(720);

		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
		
		SqluckyStage sqlStage = new SqluckyStage(vb);
		stage = sqlStage.getStage();
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
