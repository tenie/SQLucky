package net.tenie.plugin.backup.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jfoenix.controls.JFXCheckBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;

/**
 * 
 * @author tenie
 *
 */
public class WorkDataBackupEditorWindow {
	// 编辑连接时记录连接状态
	public  static boolean editLinkStatus = false;
	private static Logger logger = LogManager.getLogger(WorkDataBackupEditorWindow.class);
	public static Stage CreateModalWindow(VBox vb) {

		final Stage stage = new Stage();
		vb.getStyleClass().add("connectionEditor");

		Scene scene = new Scene(vb);
		
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

	public static void createWorkspaceConfigWindow( ) {
		String userName = "User Name"; 
		String passwordStr = "Password";
		String urlStr = "https://example.com";
		Label lbserverName = new Label("URL");   
		Label lbUserName= new Label(userName);  
		Label lbPassword = new Label(passwordStr);   
		
		TextField tfserverName = new TextField();
		tfserverName.setPromptText(urlStr);
		
		TextField tfUserName = new TextField();
		tfUserName.setPromptText(userName);
		
		PasswordField password = new PasswordField();
		password.setPromptText(passwordStr);
		
		
		
		HBox hb1 = new HBox();
		Label Remember = new Label("Remember Account ");
	    JFXCheckBox rememberCB  = new JFXCheckBox(); 
//	    hb1.getChildren().addAll(Remember, rememberCB);
	    
	    HBox hb2 = new HBox();
	    Label autoUp = new Label("Auto Upload ");
	    JFXCheckBox autoUpCB  = new JFXCheckBox(); 
	    hb2.getChildren().addAll(Remember, rememberCB,autoUp, autoUpCB);
	    
		
		Label lbWorkspace = new Label("Workspace");   
		List<String> liWs = new ArrayList<>();
		liWs.add("Local Workspace");
		ObservableList<String>   workspaces   = FXCollections.observableArrayList(liWs);
		ChoiceBox<String> cbWorkspace = new ChoiceBox<String>(workspaces);
		Tooltip tt = new Tooltip("select workspace"); 
		tt.setShowDelay(new Duration(100));
		cbWorkspace.setTooltip(tt);
		cbWorkspace.setPrefWidth(250);
		cbWorkspace.setMinWidth(250);
		
		

	 
		Button uploadBtn = createUploadBtn( tfserverName, tfUserName , password);//new Button("Test");
		Button downloadBtn = createDownloadBtn( null  );
		Button saveBtn = createSwitchBtn(  null ) ; // new Button("Save"); 
		
		List<Region> list = new ArrayList<>();
		list.add(    lbWorkspace);
		list.add(    cbWorkspace);
		list.add(    saveBtn);
		list.add(    null);
		
		
		list.add(    lbserverName);
		list.add(    tfserverName);
		
		list.add(    lbUserName);
		list.add(    tfUserName);
		
		list.add(    lbPassword);
		list.add(    password); 
		
		list.add(    hb1);
		list.add(    hb2); 
		
		list.add(    downloadBtn);
		list.add(    uploadBtn);
		layout(list);

	}  
	
	public static Button createDownloadBtn(Function<String, SqluckyConnector> assembleSqlCon ) {
		Button testBtn = new Button("Download "); 
		return testBtn;
	}
	public static Button createUploadBtn(TextField tfserverName, TextField tfUserName , PasswordField password) {
		Button UploadBtn = new Button("Upload Loacl Data ");
		UploadBtn.setOnMouseClicked(e->{
			String url = tfserverName.getText();
			String userName = tfUserName.getText();
			String pw = password.getText();
			Map<String, String> pama = new HashMap<>();
			pama.put("userName", userName);
			pama.put("password", pw);
//			pama.put("fileName", ConfigVal.H2_DB_FULL_FILE_NAME);
			
			
			net.tenie.Sqlucky.sdk.utility.net.HttpPostFile.exec(url, ConfigVal.H2_DB_FULL_FILE_NAME, pama );
		});
		return UploadBtn;
	}
	
	public static Button createSwitchBtn(Function<String, SqluckyConnector> assembleSqlCon  ) {
		Button saveBtn = new Button("Switch");
		return saveBtn;
	}
	// 组件布局
	public static void layout(List<Region> list) {
		VBox vb = new VBox();
		Label title = new Label("Edit Info	");
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
