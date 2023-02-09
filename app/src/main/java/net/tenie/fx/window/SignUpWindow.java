package net.tenie.fx.window;

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
import javafx.scene.web.WebView;
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
public class SignUpWindow {
	// 编辑连接时记录连接状态
	public  static boolean editLinkStatus = false;
	private static Logger logger = LogManager.getLogger(SignUpWindow.class);
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

	
	public static void createWorkspaceConfigWindow() { 
		 
		   	WebView web = new WebView();
		   	web.getEngine().load("https://www.tenie.net/");
			List<Region> list = new ArrayList<>();
			HBox emailBox = new HBox(); 
			emailBox.getChildren().add(web);
		   	list.add(    null);
		   	list.add(    emailBox);
			layout(list);
	}
	public static void createWorkspaceConfigWindow2( ) {
		String email = "Email";
		String userName = "User Name"; 
		String passwordStr = "Password";
//		String remember =  "Remember Account";
		String emailStr = "xxx@xxx.xxx";
		
		String reg = "Registration code";
		
		Label lbEmail = new Label(email);   
		Label lbUserName= new Label(userName);  
		Label lbPassword = new Label(passwordStr);   
		
		
		HBox emailBox = new HBox(); 
		TextField emailTF = new TextField();
		emailTF.setPromptText(emailStr);
		Button sendCodeBtn = createSendCodeBtn(null); // 发送验证码按钮
		emailBox.getChildren().addAll(emailTF, sendCodeBtn);
		
		TextField tfUserName = new TextField();
		tfUserName.setPromptText(userName);
		
		PasswordField password = new PasswordField();
		password.setPromptText(passwordStr);
		
		
		
//		HBox hb2 = new HBox(); 
//		Label Remember = new Label(remember);
//	    JFXCheckBox rememberCB  = new JFXCheckBox();  
//	    
//	    hb2.getChildren().addAll(Remember, rememberCB );
	    
//		Button signInBtn = createSignInBtn( null  );
		Button signUpBtn = createSignUpBtn( null  );
		List<Region> list = new ArrayList<>();
  
		
		
		list.add(    lbEmail);
		list.add(    emailBox);
		
		list.add(    lbUserName);
		list.add(    tfUserName);
		
		list.add(    lbPassword);
		list.add(    password); 
		
//		list.add(    null);
//		list.add(    hb2); 
		
//		list.add(    signInBtn); 

		list.add(    signUpBtn);
		list.add(    null);
		layout(list);

	}  
	
	public static Button createSendCodeBtn(Function<String, SqluckyConnector> assembleSqlCon ) {
		String Send = "Send code ";
		Button SendBtn = new Button(Send); 
		SendBtn.setOnAction(e->{
//			SignInWindow.createWorkspaceConfigWindow();
		});
		return SendBtn;
	}
	
	public static Button createSignUpBtn(Function<String, SqluckyConnector> assembleSqlCon ) {
		String signUp = "Sign Up ";
		Button signUpBtn = new Button(signUp); 
		return signUpBtn;
	}
	
 
	// 组件布局
	public static void layout(List<Region> list) {
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
