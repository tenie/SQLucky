package net.tenie.fx.window;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jfoenix.controls.JFXCheckBox;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.TextFieldSetup;
import net.tenie.fx.component.UserAccount.UserAccountAction;

/**
 * 
 * @author tenie
 *
 */
public class SignInWindow {
	// 编辑连接时记录连接状态
	private static Logger logger = LogManager.getLogger(SignInWindow.class);
	
	private Stage stageWindow = null ;
	private TextField tfemail;
	private PasswordField password;
	private JFXCheckBox rememberCB;
	private Button signInBtn ;
	public static void show(String title) {
		SignInWindow window = new SignInWindow();
		window.initWindow(title); 
	}
	// 创建窗口
	public Stage CreateModalWindow(VBox vb,  String title) {
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
		stageWindow.initModality(Modality.APPLICATION_MODAL);
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

	// 初始化控件
	public  void initWindow( String title) {
		
		String email = "Email"; 
		String passwordStr = "Password";
		String remember =  "Remember Account";
		
		Label lbemail= new Label(email);  
		Label lbPassword = new Label(passwordStr);   
		
		tfemail = new TextField();
		tfemail.setPromptText(email);
		TextFieldSetup.setMaxLength(tfemail, 100);
		
		password = new PasswordField();
		password.setPromptText(passwordStr);
		TextFieldSetup.setMaxLength(password, 50);
		 
		tfemail.disableProperty().bind(ConfigVal.SQLUCKY_LOGIN_STATUS);
		password.disableProperty().bind(ConfigVal.SQLUCKY_LOGIN_STATUS);
		
		
		HBox hb2 = new HBox(); 
		Label Remember = new Label(remember);
	    rememberCB  = new JFXCheckBox();  
	    rememberCB.selectedProperty().addListener(v->{
	    	boolean iss = rememberCB.isSelected();
	    	UserAccountAction.rememberUser(iss);
	    	logger.debug("iss = " + iss);
	    });
	   
	    
	    hb2.getChildren().addAll(Remember, rememberCB );
	    // 登入函数
	    signInBtn = createSignInBtn();
		signInBtn.setDisable(true);
		tfemail.textProperty().addListener(e->{
			if(ConfigVal.SQLUCKY_LOGIN_STATUS.get() == false) {
				if( tfemail.getText().trim().length() == 0 || password.getText().trim().length() == 0) {
					signInBtn.setDisable(true);
				}else {
					signInBtn.setDisable(false);
				}
			}
			
		});
		password.textProperty().addListener(e->{
			if(ConfigVal.SQLUCKY_LOGIN_STATUS.get() == false) {
				if( password.getText().trim().length() == 0 || tfemail.getText().trim().length() == 0 ) {
					signInBtn.setDisable(true);
				}else {
					signInBtn.setDisable(false);
				}
			}
		});
		
		// 退出登入
		Button signOutBtn = createSigOutBtn( );
		signOutBtn.disableProperty().bind(signInBtn.disabledProperty().not());
		// 高级设置
		Button setbtn = createAdvancedSettings();
		// 如果已经登入过, 获取登入信息
		String siEmail = ConfigVal.SQLUCKY_EMAIL.get();
		String siPw = ConfigVal.SQLUCKY_PASSWORD.get();
		boolean sky_remb = ConfigVal.SQLUCKY_REMEMBER.get();
		
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
		
		
		list.add(    hb2); 
		list.add(    null);
		
		list.add(    signInBtn);
		list.add(    null);
		
		list.add(    signOutBtn); 
		list.add(    null);
		
		list.add(    setbtn); 
		list.add(    null); 
		
		layout(list, title);
	}  
	
	private String SignIn = "Sign in ";
	private String signedIn = "Signed In";
	// 登入按钮
	public Button createSignInBtn( ) {
		
		Button SignInBtn = null; 
		if(ConfigVal.SQLUCKY_LOGIN_STATUS.get()) {
			SignInBtn = new Button(signedIn); 
		}else {
			SignInBtn = new Button(SignIn); 
		}
		SignInBtn.setOnAction(V->{ 
	    	String emailVal = tfemail.getText();
	    	String passwordVal = password.getText();
	    	if(emailVal == null || emailVal.trim().length() == 0) {
	    		MyAlert.errorAlert( "Email不能为空");
	    		return ;
	    	}
	    	
	    	if(passwordVal == null || passwordVal.trim().length() < 16) {
	    		MyAlert.errorAlert( "密码不能小于16位");
	    		return ;
	    	}
	    	
	    	 
	    	
	    	boolean tf = rememberCB.isSelected();
	    	boolean seccuss = UserAccountAction.singIn(emailVal, passwordVal, tf );
	    	
	    	if(seccuss) {
	    		MyAlert.alert("登入成功", "登入成功"); 
	    		ConfigVal.SQLUCKY_EMAIL.set(emailVal);
	    		ConfigVal.SQLUCKY_PASSWORD.set(passwordVal);
	    		ConfigVal.SQLUCKY_REMEMBER.set(tf);
	    		ConfigVal.SQLUCKY_LOGIN_STATUS.set(true);
	    		signInBtn.setText(signedIn);
	    		signInBtn.setDisable(true);
	    		stageWindow.close();
	    	}else {
	    		MyAlert.errorAlert( "失败");
	    		ConfigVal.SQLUCKY_EMAIL.set("");
	    		ConfigVal.SQLUCKY_PASSWORD.set("");
	    		UserAccountAction.delUser();
	    		ConfigVal.SQLUCKY_LOGIN_STATUS.set(false);
	    		signInBtn.setText(SignIn);
	    		signInBtn.setDisable(false);
	    	}
	    
		});
		return SignInBtn;
	}
	// 注册按钮
	public Button createSignUpBtn(Function<String, String> sup ) {
		String signUp = "Sign Up ";
		Button signUpBtn = new Button(signUp); 
		signUpBtn.setOnAction(e->{
			SignUpWindow.createWorkspaceConfigWindow();
		});
		return signUpBtn;
	}
	// 退出按钮
	public Button createSigOutBtn() {
		String signOut = "Sign Out ";
		Button signOutBtn = new Button(signOut); 
		signOutBtn.disableProperty().bind(ConfigVal.SQLUCKY_EMAIL.isEmpty());
		signOutBtn.setOnAction(e->{
			ConfigVal.SQLUCKY_EMAIL.set("");
			ConfigVal.SQLUCKY_PASSWORD.set("");
			ConfigVal.SQLUCKY_USERNAME.set("");
			ConfigVal.SQLUCKY_REMEMBER.set(false);
			ConfigVal.SQLUCKY_LOGIN_STATUS.set(false);
			// 删除app数据库中的用户信息
			UserAccountAction.delUser();
			tfemail.setText("");
			password.setText("");
			rememberCB.setSelected(false);
			
			signInBtn.setDisable(false);
			signInBtn.setText(SignIn);
		});
		return signOutBtn;
	}
	
	// 高级设置按钮
	public Button createAdvancedSettings() {
		String btnName = "Advanced Settings";
		Button btn = new Button(btnName); 
		btn.setOnAction(v->{
			SignInAdvancedSettingsWindow.show("");
		});
		return btn;
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
