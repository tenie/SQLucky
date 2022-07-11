package net.tenie.plugin.MybatisGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.plugin.MybatisGenerator.po.DataModelInfoPo;
import net.tenie.plugin.MybatisGenerator.tools.DataModelDAO;
import net.tenie.plugin.MybatisGenerator.tools.DataModelUtility;

/**
 * 
 * @author tenie
 *
 */
public class DataModelImportWindow {
	// 编辑连接时记录连接状态
	public  static boolean editLinkStatus = false;
	// 
	public  static StackPane wdroot;
	public  static Stage stage;
	
	private static Logger logger = LogManager.getLogger(DataModelImportWindow.class);
	public static Stage CreateModalWindow(VBox vb) {

	    stage = new Stage();
		
	    wdroot =  new StackPane(vb);
		Scene scene = new Scene(wdroot);
		CommonUtility.loadCss(scene);
		
		vb.getStyleClass().add("connectionEditor");
	
		vb.setPrefWidth(400);
		vb.maxWidth(400);
		AnchorPane bottomPane = new AnchorPane();
		bottomPane.setPadding(new Insets(10));

		vb.getChildren().add(bottomPane);
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
		
		stage.getIcons().add( ComponentGetter.LogoIcons);
		stage.setMaximized(false);
		stage.setResizable(false);
		return stage;
	}

	public static void createModelImportWindow( ) {
		String filePath = "File path"; 
		
		Label lbFilePath= new Label(filePath);  
		
		TextField tfFilePath = new TextField();
		tfFilePath.setPromptText(filePath);
		tfFilePath.setDisable(true);
		
		
				
		
		String modelName = "Model name";
		Label lbModelName = new Label(modelName);  
		TextField tfModelName = new TextField();
		tfModelName.setPromptText(modelName);
		tfModelName.setDisable(true);
		tfModelName.disableProperty().bind( tfFilePath.textProperty().isEmpty());
		
		 
	    
		// 文件选择按钮
		var selectFile = openFileBtn(tfFilePath, tfModelName ); 
		HBox fileBox = new HBox();
		fileBox.getChildren().addAll( tfFilePath, selectFile);
		var savebtn = saveBtn( tfModelName );
		savebtn.disableProperty().bind( tfFilePath.textProperty().isEmpty());
		var cancel = cancelBtn();
				 
		
		List<Region> list = new ArrayList<>();
		list.add(    lbFilePath);
		list.add(    fileBox);
		list.add(    lbModelName);
		list.add(    tfModelName);
		
		list.add(    savebtn);
		list.add(    cancel);
		
		
		layout(list);

	}  
	
	public static DataModelInfoPo tmpDataModelPoVal  = null;
	public static Button openFileBtn(TextField tfFilePath ,TextField tfModelName ) {
		Button saveBtn = new Button("...");
		saveBtn.setOnMouseClicked(e->{
			// 获取文件
			File jsonFile = FileOrDirectoryChooser.showOpenJsonFile("Open", ComponentGetter.primaryStage);
			if(jsonFile != null) {
				tmpDataModelPoVal = null;
				String path = jsonFile.getAbsolutePath();
				// 读模型数据
				try {
					tmpDataModelPoVal = DataModelUtility.readJosnModel("UTF-8", jsonFile);
					String mdName = tmpDataModelPoVal.getName();
					
					tfFilePath.setText(path);
					tfModelName.setText(mdName);
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		
			
		});
		return saveBtn;
	}
	
	public static Button saveBtn(TextField tfModelName) {
		Button saveBtn = new Button("Save");
		saveBtn.getStyleClass().add("myAlertBtn");
		saveBtn.setOnMouseClicked(e->{
			String mdName = "";
			if(tmpDataModelPoVal != null ) {
				if(tfModelName.getText() != null && tfModelName.getText().length() > 0) {
					mdName = tfModelName.getText();
					
				}else {
					mdName = tmpDataModelPoVal.getName();
				}
				// 查找模型名称是否存在， 存在不能保存
				var mdpo = DataModelDAO.selectDMInfoByName( mdName);
				//同名模型存在
				if(mdpo !=null ) {
					MyAlert.errorAlert("Exist model name: " + mdName +", Please,  Rename !");
					return ;
				}
				
				tmpDataModelPoVal.setName(mdName);
				
				DataModelUtility.saveDataModelToDB(wdroot, tmpDataModelPoVal, v->{
					Platform.runLater(()->{
						stage.close();
					});
				});
				
			}
			
			
		});
		return saveBtn;
	}
	
	
	public static Button cancelBtn() {
		Button cancelBtn = new Button("Cancel");
		cancelBtn.getStyleClass().add("myAlertBtn");
		cancelBtn.setOnMouseClicked(e->{
			stage.close();
			
		});
		return cancelBtn;
	}
	
	// 组件布局
	public static void layout(List<Region> list    ) {
		VBox vb = new VBox();
		Label title = new Label("Import Model Json File");
		title.setPadding(new Insets(15));
		AppComponent appComponent = ComponentGetter.appComponent; 
		title.setGraphic(appComponent.getIconDefActive("gears"));
		vb.getChildren().add(title);
		GridPane grid = new GridPane();
		vb.getChildren().add(grid);
		vb.setPadding( new Insets(5));
		Stage stage = CreateModalWindow(vb);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 10, 10, 10));
		
		
		int i = 0;
		int j = 0;
		
		for(int k = 0 ; k< list.size() ; k+=2) {
			var node1 = list.get(k);
			var node2 = list.get(k+1);
			int idxi= i++;
			int idxj= j++;
			if(node1 !=null ) grid.add(node1, 0, idxi);
			if(node2 !=null ) grid.add(node2, 1, idxj);
		}
		
	 
		stage.show();
	}
	
}
