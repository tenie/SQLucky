package net.tenie.fx.window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jfoenix.controls.JFXCheckBox;

import javafx.beans.property.SimpleBooleanProperty;
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
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.fx.component.UserAccount.UserAccountAction;

/**
 * 
 * @author tenie
 *
 */
public class SignInWindow {
	// 编辑连接时记录连接状态
	public  static boolean editLinkStatus = false;
	private static Logger logger = LogManager.getLogger(SignInWindow.class);
	
	private static Stage stageWindow = null ;
	
	public static void show() {
		if(stageWindow == null ) { 
			SignInWindow.createWorkspaceConfigWindow();
		}else {
			stageWindow.requestFocus();
		}
		
		
	}
	public static Stage CreateModalWindow(VBox vb) {

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

		CommonUtility.loadCss(scene);
		stageWindow.initModality(Modality.WINDOW_MODAL);
		stageWindow.setScene(scene);
		
		stageWindow.getIcons().add( ComponentGetter.LogoIcons);
		stageWindow.setMaximized(false);
		stageWindow.setResizable(false);
		stageWindow.setOnCloseRequest(v->{
			stageWindow = null;
		});
		return stageWindow;
	}

	public static void createWorkspaceConfigWindow( ) {
		String email = "Email"; 
		String passwordStr = "Password";
		String remember =  "Remember Account";
		
		Label lbemail= new Label(email);  
		Label lbPassword = new Label(passwordStr);   
		
		TextField tfemail = new TextField();
		tfemail.setPromptText(email);
	
		
		PasswordField password = new PasswordField();
		password.setPromptText(passwordStr);
		
		
		
		HBox hb2 = new HBox(); 
		Label Remember = new Label(remember);
	    JFXCheckBox rememberCB  = new JFXCheckBox();  
	    rememberCB.selectedProperty().addListener(v->{
	    	boolean iss = rememberCB.isSelected();
	    	UserAccountAction.rememberUser(iss);
	    	System.out.println("iss = " + iss);
	    });
	   
	    
	    hb2.getChildren().addAll(Remember, rememberCB );
	    // 登入函数
	    Function<String, Boolean> singAction = v->{
	    	String emailVal = tfemail.getText();
	    	String passwordVal = password.getText();
	    	if(emailVal == null || emailVal.trim().length() == 0) {
	    		MyAlert.errorAlert( "Email不能为空");
	    		return false;
	    	}
	    	
	    	if(passwordVal == null || passwordVal.trim().length() == 0) {
	    		MyAlert.errorAlert( "密码不能为空");
	    		return false;
	    	}
	    	
	    	boolean tf = rememberCB.isSelected();
	    	boolean seccuss = UserAccountAction.singIn(emailVal, passwordVal, tf );
	    	
	    	if(seccuss) {
	    		MyAlert.infoAlert("成功", "成功"); 
	    		ConfigVal.SQLUCKY_EMAIL = emailVal;
	    		ConfigVal.SQLUCKY_PASSWORD = passwordVal;
	    		ConfigVal.SQLUCKY_REMEMBER = tf;
	    	}else {
	    		MyAlert.errorAlert( "失败");
	    		ConfigVal.SQLUCKY_EMAIL = "";
	    		ConfigVal.SQLUCKY_PASSWORD = "";
	    		UserAccountAction.delUser();
	    		
	    	}
	    	return true;
	    };
		Button signInBtn = createSignInBtn( singAction  );
		signInBtn.setDisable(true);
		tfemail.textProperty().addListener(e->{
			if( tfemail.getText().trim().length() == 0 || password.getText().trim().length() == 0) {
				signInBtn.setDisable(true);
			}else {
				signInBtn.setDisable(false);
			}
		});
		password.textProperty().addListener(e->{
			if( password.getText().trim().length() == 0 || tfemail.getText().trim().length() == 0 ) {
				signInBtn.setDisable(true);
			}else {
				signInBtn.setDisable(false);
			}
		});
//		Button signUpBtn = createSignUpBtn( null  );
		
		
		  // 登入函数
	    Function<String, Boolean> singOutAction = v->{
	    	tfemail.setText("");
			password.setText("");
			rememberCB.setSelected(false);
	    	return true;
	    };
		Button signOutBtn = createSigOutBtn(singOutAction);
		// 如果已经登入过, 获取登入信息
		String siEmail = ConfigVal.SQLUCKY_EMAIL;
		String siPw = ConfigVal.SQLUCKY_PASSWORD;
		boolean sky_remb = ConfigVal.SQLUCKY_REMEMBER;
		
		if( !"".equals(siEmail) && !"".equals(siPw)  ) {
			tfemail.setText(siEmail);
			password.setText(siPw);
			rememberCB.setSelected(sky_remb);
			
		}
		
		
		
		List<Region> list = new ArrayList<>();
  
		list.add(    lbemail);
		list.add(    tfemail);
		
		list.add(    lbPassword);
		list.add(    password); 
		
		list.add(    null);
		list.add(    hb2); 
		
		list.add(    signInBtn); 
		list.add(    signOutBtn); 
//		list.add(    signUpBtn);
		
		layout(list);

	}  
	
	public static Button createSignInBtn(Function<String, Boolean> sinbtn ) {
		String SignIn = "Sign in ";
		Button SignInBtn = new Button(SignIn); 
		SignInBtn.setOnAction(V->{
//			UserAccountAction.singIn(SignIn, SignIn, editLinkStatus)
			sinbtn.apply(SignIn);
		});
		return SignInBtn;
	}
	
	public static Button createSignUpBtn(Function<String, String> sup ) {
		String signUp = "Sign Up ";
		Button signUpBtn = new Button(signUp); 
		signUpBtn.setOnAction(e->{
			SignUpWindow.createWorkspaceConfigWindow();
		});
		return signUpBtn;
	}
	
	public static Button createSigOutBtn(Function<String, Boolean> sup ) {
		String signOut = "Sign Out ";
		Button signOutBtn = new Button(signOut); 
		signOutBtn.setOnAction(e->{
			ConfigVal.SQLUCKY_EMAIL = "";
			ConfigVal.SQLUCKY_PASSWORD = "";
			ConfigVal.SQLUCKY_USERNAME = "";
			ConfigVal.SQLUCKY_REMEMBER = false;
			
			
			UserAccountAction.delUser();
			sup.apply("");
		});
		return signOutBtn;
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
