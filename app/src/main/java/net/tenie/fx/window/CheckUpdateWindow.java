package net.tenie.fx.window;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.po.SheetTableData;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.DBTools;
import net.tenie.Sqlucky.sdk.utility.JsonTools;
import net.tenie.Sqlucky.sdk.utility.net.HttpUtil;

/**
 * 
 * @author tenie
 *
 */
public class CheckUpdateWindow {
	// 编辑连接时记录连接状态
	private static Logger logger = LogManager.getLogger(CheckUpdateWindow.class);
	
	private Stage stageWindow = null ;
	public static void show(String title) {
		CheckUpdateWindow window = new CheckUpdateWindow();
		window.layout(title);
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

  
	 
	// 注册按钮
	public Button createSignUpBtn(Function<String, String> sup ) {
		String signUp = "Checking ";
		Button signUpBtn = new Button(signUp); 
		signUpBtn.setOnAction(e->{
			SignUpWindow.createWorkspaceConfigWindow();
		});
		return signUpBtn;
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
	public  void downloadNewPatch(SheetTableData sheetDaV, FilteredTableView<ResultSetRowPo>  allPluginTable) {
		LoadingAnimation.addLoading("Download ...");
		
		CommonUtility.runThread(v->{
			try {
//				int currentSelectIndex = allPluginTable.getSelectionModel().getSelectedIndex();
				
				Map<String, String> vals = new HashMap<>();
				if(CommonUtility.isWinOS()) {
					vals.put("OS", "win");	
				}else if(CommonUtility.isLinuxOS()) {
					vals.put("OS", "linux");	
				}else if(CommonUtility.isMacOS()) {
					vals.put("OS", "mac");	
				} 
				vals.put("CLIENT_VERSION", ConfigVal.version); 
				
				String dir = DBTools.dbFilePath() + "newPatch";
				File patchDir = new File(dir);
				if(patchDir.exists()) {
					patchDir.deleteOnExit();
				}else {
					patchDir.mkdir();
				}
				// fileName 是新下载的文件名
				String fileName = HttpUtil.downloadAppPatchByPostToDir(ConfigVal.getSqluckyServer()+"/sqlucky/downloadNewPatch",dir, vals);
				
				File pluginFile = new File(fileName);
				if(pluginFile.exists()) {
					// 更新操作
					
				}
			} finally {
				LoadingAnimation.rmLoading();
			}
		
		});
	}
 
	// 控件布局, 并显示窗口
	public void layout( String titleStage) {
		VBox vb = new VBox();
		List<Region> list = new ArrayList<>();
		// 下载按钮
		Button btn = new Button("Download"); 
		btn.setDisable(true);
		btn.setOnAction(v->{
			CommonUtility.OpenURLInBrowser("https://github.com/tenie/SQLucky/releases");
		});
		list.add(null);
		list.add(btn);
		
		String sign = "Checking ";
		Label title = new Label(sign);
		title.setPadding(new Insets(15));
		Node nd = IconGenerator.svgImage("icomoon-spinner9", 30 , "#7CFC00"); 
		CommonUtility.rotateTransition(nd);
		title.setGraphic(nd);
		vb.getChildren().add(title);
		
		//服务器检测版本
		CommonUtility.runThread(str->{
			String msg = "检测失败";
			try {
				String version = newAppVersionCode();//HttpUtil.get(ConfigVal.getSqluckyServer()+"/sqlucky/version");
				if(version != null) {
					if(ConfigVal.version.equals(version)) {
						msg = "已经是最新版本!";
//						appVersion = version;
					}else {
						msg = "当前版本: " + ConfigVal.version + "; 最新版本: " + version;
						btn.setDisable(false);
					}
				}
				
			} catch (Exception e) {
				msg = "检测失败";
			}
			
			String showMsg = msg;
			Platform.runLater(()->{
				title.setText(showMsg); 
				var svg = IconGenerator.sqluckyLogoSVG();
				title.setGraphic(svg); 
			});
			
			
		});		
		
		
		
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
	
	/**
	 * 获取github上最新版本的版本号
	 * @return
	 */
	private String newAppVersionCode() {
		String url = "https://api.github.com/repos/tenie/SQLucky/releases/latest";
		String valSr = HttpUtil.get(url);
		String version = JsonTools.getJsonKeyValue(valSr, "tag_name");
		return version;
	}
	
}
