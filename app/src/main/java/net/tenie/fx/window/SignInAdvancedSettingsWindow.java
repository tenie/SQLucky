package net.tenie.fx.window;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXCheckBox;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.fx.component.UserAccount.UserAccountAction;

/**
 * 高级设置(设置服务器地址)窗口
 * @author tenie
 *
 */
public class SignInAdvancedSettingsWindow {
	// 编辑连接时记录连接状态
	private static Logger logger = LogManager.getLogger(SignInAdvancedSettingsWindow.class);
	
	private static Stage stageWindow = null ;
	private TextField tfhost; 
	private JFXCheckBox rememberCB ;
	
	public static void show(String title) {
		SignInAdvancedSettingsWindow window = new SignInAdvancedSettingsWindow();
		window.initWindow(title);
	}
	public static Stage CreateModalWindow(VBox vb,  String title) {
		stageWindow = new Stage();
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
			stageWindow.close();
			
		});
		scene.getAccelerators().put(spacebtn, () -> {
			stageWindow.close();
			
		});

		CommonUtils.loadCss(scene);
		stageWindow.initModality(Modality.APPLICATION_MODAL);
//		stageWindow.initModality(Modality.WINDOW_MODAL);
		stageWindow.setScene(scene);
		
		stageWindow.getIcons().add( ComponentGetter.LogoIcons);
		stageWindow.setTitle(title);
		stageWindow.setMaximized(false);
		stageWindow.setResizable(false);
		stageWindow.setOnCloseRequest(v->{
			stageWindow = null;
		});
		return stageWindow;
	}

	public  void initWindow( String title) {
		Label host= new Label("Host Url");    
		tfhost = new TextField();
		tfhost.setPromptText("https://example.com");
		
		Label Remember = new Label("Remember Settings");
	    rememberCB  = new JFXCheckBox();  
	    rememberCB.selectedProperty().addListener(v->{
	    	boolean iss = rememberCB.isSelected();
	    	UserAccountAction.rememberUser(iss);
	    	logger.debug("iss = " + iss);
	    });

	    // 保存按钮
		Button saveBtn = createSaveBtn(  );
		saveBtn.setDisable(true);
		tfhost.textProperty().addListener(e->{
			if( tfhost.getText().trim().length() == 0 ) {
				saveBtn.setDisable(true);
			}else {
				saveBtn.setDisable(false);
			}
		});
		
		 // 清空
//		Button cleanBtn = createCleanBtn( );
		// 如果已经登入过, 获取登入信息
		String cfhost = ConfigVal.SQLUCKY_URL_CUSTOM; 
		boolean cf_remb = ConfigVal.SQLUCKY_REMEMBER_SETTINGS.get();
		
		if( !"".equals(cfhost)   ) {
			tfhost.setText(cfhost); 
			rememberCB.setSelected(cf_remb);
		}
		
		List<Region> list = new ArrayList<>();
		list.add(    host);
		list.add(    tfhost); 
		
		list.add(    Remember);
		list.add(    rememberCB); 
		
		list.add(    saveBtn); 
		// 取消按钮
		Button cancel = new Button("Cancel");
		cancel.setOnAction(e->{
			stageWindow.close();
		});
		list.add(    cancel); 
//		list.add(    cleanBtn); 
		
		list.add(    null); 
		list.add(    null); 
		
		layout(list, title);
	}  
	// 保存按钮
	public  Button createSaveBtn( ) {
		String name = "Save ";
		Button saveBtn = new Button(name); 
		saveBtn.setOnAction(V -> {
			String hostval = tfhost.getText();
			if (hostval == null || hostval.trim().length() == 0) {
				MyAlert.errorAlert("Host不能为空");
				return;
			}
			ConfigVal.SQLUCKY_URL_CUSTOM = hostval;
			boolean tf = rememberCB.isSelected();
			ConfigVal.SQLUCKY_REMEMBER_SETTINGS.set(tf);
			if (tf) {
				UserAccountAction.saveHostValAccount(hostval);
				ConfigVal.SQLUCKY_LOGIN_STATUS.set(false);
			} else {
				UserAccountAction.delHostValAccount();
			}
			stageWindow.close();
		});
		return saveBtn;
	}
	
	public Button createCleanBtn( ) {
		String name = "Clean";
		Button cleanBtn = new Button(name); 
		cleanBtn.setOnAction(e->{
			tfhost.setText("");
	    	rememberCB.setSelected(false);
	    	ConfigVal.SQLUCKY_URL_CUSTOM = "" ;
	    	UserAccountAction.delHostValAccount();
		});
		return cleanBtn;
	}
	
	// 组件布局
	public static void layout(List<Region> list, String titleStage) {
		String sign = "Set custom server";
		VBox vb = new VBox();
		Label title = new Label(sign);
		title.setPadding(new Insets(15));
		AppComponent appComponent = ComponentGetter.appComponent; 
		title.setGraphic(appComponent.getIconDefActive("gears"));
		vb.getChildren().add(title);
		GridPane grid = new GridPane();
		vb.getChildren().add(grid);
		vb.setPadding( new Insets(5));
		Stage stage = CreateModalWindow(vb, titleStage);
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
