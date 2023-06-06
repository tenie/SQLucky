package net.tenie.fx.window;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXCheckBox;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.TextFieldSetup;
import net.tenie.fx.component.UserAccount.UserAccountAction;

/**
 * 快捷键绑定
 * @author tenie
 *
 */
public class KeysBindWindow {

	// 编辑连接时记录连接状态
	private static Logger logger = LogManager.getLogger(KeysBindWindow.class);
	
	private Stage stageWindow = null ;
	public static void show(String title) {
		KeysBindWindow window = new KeysBindWindow();
		window.initWindow(title); 
	}
	// 创建窗口
	public Stage CreateModalWindow(VBox vb,  String title) {
		SqluckyStage sqlStage = new SqluckyStage(vb);
		stageWindow = sqlStage.getStage();
		Scene scene = sqlStage.getScene();
		vb.getStyleClass().add("connectionEditor");
		
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

		stageWindow.initModality(Modality.APPLICATION_MODAL);
		
		stageWindow.setTitle(title);
		stageWindow.setMaximized(false);
		stageWindow.setResizable(false);
		stageWindow.setOnCloseRequest(v->{
			stageWindow = null;
		});
		return stageWindow;
	}

	// 初始化控件
	public  void initWindow( String title) {
		
		
		Label lb1 = new Label("Auto Complete");  
		Label lb2 = new Label("Comment code");   
		
		TextField autoC = new TextField();  
		 
		
		   
		
//		layout(list, title);
	}  
	
	private String SignIn = "Sign in ";
	private String signedIn = "Signed In";
	// 登入按钮
	// 注册按钮
	public Button createSignUpBtn(Function<String, String> sup ) {
		String signUp = "Sign Up ";
		Button signUpBtn = new Button(signUp); 
		signUpBtn.setOnAction(e->{
			SignUpWindow.createWorkspaceConfigWindow();
		});
		return signUpBtn;
	}
 
	// 控件布局, 并显示窗口
	public void layout(List<Region> list, String titleStage) {
		String sign = "Sign in ";
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
