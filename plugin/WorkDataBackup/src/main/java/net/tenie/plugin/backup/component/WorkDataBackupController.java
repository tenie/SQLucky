package net.tenie.plugin.backup.component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle; 
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.TextFieldSetup;
import net.tenie.plugin.backup.po.BackupInfoPO;
import net.tenie.plugin.backup.po.DownloadBackupPo;
/**
 * 备份主界面fxml 的controller
 * @author tenie
 *
 */
public class WorkDataBackupController implements Initializable {

	@FXML private Button bakBtn; 
	@FXML private TextField bakName;
	@FXML private TextField privateKey;
	@FXML private CheckBox dbCB;
	@FXML private CheckBox scriptCB;
	@FXML private CheckBox modelCB;
	@FXML private CheckBox pkCB;
	
	// 下载界面
	@FXML private Button syncBtn;
	
	
	@FXML private TextField selBakName;
	@FXML private TextField recoverPK;
//	@FXML private CheckBox useRPK;
	
	@FXML private Button downloadMergeBtn;
	@FXML private Button downloadOverlapBtn;
	@FXML private Button downloadBtn;
	
	private SimpleStringProperty idVal = new  SimpleStringProperty("");
	private SimpleStringProperty nameVal  = new  SimpleStringProperty("");;
	private DownloadBackupPo  rsVal ;
	
	@FXML private CheckBox bakdbCB;
	@FXML private CheckBox bakscriptCB;
//	private	File bakFile;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	 	 modelCB.setVisible(false);
		 downloadBtn.setVisible(false);
		 // 下载页面 使用使用框默认禁用
		 bakdbCB.setDisable(true);
		 bakscriptCB.setDisable(true);
//		 useRPK.setDisable(true);
		 TextFieldSetup.setMaxLength(privateKey, 50);
		 TextFieldSetup.setMaxLength(recoverPK, 50); 
		 TextFieldSetup.setMaxLength(bakName, 50); 
		 
		 
		 uploadPage();
		 downloadPage();
		 CommonUtility.isLogin("Use Backup must Login");
	}
	
	
	public void uploadPage() {
		// 按钮亮起
		bakBtn.setDisable(true);
		bakBtn.disableProperty().bind(bakName.textProperty().isEmpty());

		// 密钥默认不能输入只有勾选了才能输入
		privateKey.disableProperty().bind(pkCB.selectedProperty().not());
		// 使用密钥选择, 密钥输入框焦点
		pkCB.selectedProperty().addListener((ob, old, nw) -> {
			if (!nw) {
				privateKey.clear();
			} else {
				privateKey.requestFocus();
			}
		});

		// 备份按钮
		bakBtn.setOnAction(e -> {
			// 登入校验
			if(CommonUtility.isLogin("Use Backup must Login") == false) {
				return ;
			}
			
			// 
			bakBtn.disableProperty().unbind();
			bakBtn.setDisable(true);
			LoadingAnimation.loadingAnimation("Backup...", v->{
				BackupInfoPO po = new BackupInfoPO(bakName, privateKey, dbCB, scriptCB, modelCB, pkCB);
				WorkDataBackupAction.BackupBtn(po );
				Platform.runLater(()->{
					bakBtn.setDisable(false);
					bakBtn.disableProperty().bind(bakName.textProperty().isEmpty());
				});
			});
			
			
		});
	}
	// 下载页面初始化
	public void downloadPage() {
		
		// 还原备份文件按钮, 默认不启用
		downloadOverlapBtn.setDisable(true); 
		downloadMergeBtn.setDisable(true);
		// 当有文件名称了, 就可以当按钮启用
		selBakName.textProperty().addListener((ob, ol, nw)->{
			if(nw != null ) {
				if(nw.length() > 0) {
					downloadOverlapBtn.setDisable(false);
					downloadMergeBtn.setDisable(false);
				}else {
					downloadOverlapBtn.setDisable(true);
					downloadMergeBtn.setDisable(true);
				}
			}
		});
		// 恢复密钥, 如果是不启用, 就清空内容
		recoverPK.disableProperty().addListener((ob, old, nw) -> {
			if(recoverPK.isDisable()) {
				recoverPK.clear();
			}else {
				recoverPK.requestFocus();
			}
		});
		recoverPK.setDisable(true);
//		recoverPK.disableProperty().bind(useRPK.selectedProperty().not());
//		useRPK.selectedProperty().addListener((ob, old, nw) -> {
//			if (!nw) {
//				recoverPK.clear();
//			} else {
//				recoverPK.requestFocus();
//			}
//		});
		
		// 获取服务器上的备份名称
		syncBtn.setOnAction(e->{
			// 登入校验
			if(CommonUtility.isLogin("Use Backup must Login") == false) {
				return ;
			}
			// 开始之前先禁用选中框 
			 bakdbCB.setDisable(true);
			 bakscriptCB.setDisable(true);
			 recoverPK.setDisable(true);
//			 useRPK.setDisable(true);
			
			 idVal.set("");
			 nameVal.set("");
			// 获取选中备份页面的返回值
		    rsVal = QueryBackupController.showFxml();
			if(rsVal != null) {
				idVal.set(rsVal.getIdVal().get());
				nameVal.set(rsVal.getNameVal().get());
				selBakName.textProperty().bind(nameVal);
				// 判断 文件中有哪几类备份文件, 来开启使用勾选框
				Map<String, File> allFile = rsVal.fileDetail();
				if (allFile.get("1") != null) {
					 bakdbCB.setDisable(false);
					
				}
				if (allFile.get("2") != null) {
					 bakscriptCB.setDisable(false);
				}
				if (allFile.get("PK") != null) {
//					useRPK.setDisable(false); 
					recoverPK.setDisable(false);
				}
				
//				Map<String, File> allFile = WorkDataBackupAction.unZipBackupFile(bakZip, nameVal.get());
				
			}
			
		});
		
		
//		// 下载按钮
//		downloadBtn.setOnAction(e->{
//			// 登入校验
//			if(isLogin() == false) {
//				return ;
//			}
//			if(idVal != null ) {
//				WorkDataBackupAction.downloadBackup(idVal.get(), nameVal.get());
//				AppComponent appComponent = ComponentGetter.appComponent; 
//			}
//			
//		});
		
		// 下载覆盖
		downloadOverlapBtn.setOnAction(e->{
			if(rsVal.fileDetail().isEmpty() == false) {
				if(bakdbCB.isSelected() || bakscriptCB.isSelected() ) {
					// 提示覆盖后不开还原
					boolean isContinue = MyAlert.myConfirmationShowAndWait("本地数据将被覆盖, 继续?");
					if(isContinue) {
						LoadingAnimation.loadingAnimation("Loading...", v->{
							WorkDataBackupAction.downloadOverlap(
									downloadOverlapBtn, downloadMergeBtn,
									recoverPK, rsVal.fileDetail(),
									bakdbCB.isSelected(), bakscriptCB.isSelected());
						});
						
					}
				}
			}
		});
		// 合并
		downloadMergeBtn.setOnAction(e->{
			if(rsVal.fileDetail().isEmpty() == false) {
				if(bakdbCB.isSelected() || bakscriptCB.isSelected() ) {
					// 提示覆盖后不开还原
//					boolean isContinue = MyAlert.myConfirmationShowAndWait("和本地数据合并, 名称相同会被添加*符予以区分, 继续?");
//					if(isContinue) {
					LoadingAnimation.loadingAnimation("Loading...", v->{
						WorkDataBackupAction.downloadMerge(
								downloadOverlapBtn, downloadMergeBtn,
								recoverPK, rsVal.fileDetail(),
								bakdbCB.isSelected(), bakscriptCB.isSelected());
					});
						
//					}
				}
			}
		});
	}
	 
	
	 public static void showFxml() {
		String fxml = "/workBackupFxml/workdataBackup.fxml";
		try {
			URL url = WorkDataBackupController.class.getResource(fxml);
			Parent root = FXMLLoader.load(url);
			
			SqluckyStage sqlStage = new SqluckyStage(root);
			Stage stage = sqlStage.getStage();
			
//			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("");
			
			stage.setResizable(false);
			stage.show();

			stage.setOnCloseRequest(ev -> {
				stage.hide();
				ev.consume();
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
