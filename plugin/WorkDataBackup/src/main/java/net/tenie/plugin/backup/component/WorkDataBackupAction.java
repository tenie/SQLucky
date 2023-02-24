package net.tenie.plugin.backup.component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.DesUtil;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.ZipUtil;
import net.tenie.Sqlucky.sdk.utility.net.HttpPostFile;

public class WorkDataBackupAction {

	// 触发备份按钮
	public static void BackupBtn(BackupInfoPO po) {
		String backupName = po.getBackupName();
		if(StrUtils.isNullOrEmpty(backupName)) {
			MyAlert.errorAlert( "备份名称不能为空!");
			return;
		}
		String pKey = "";
		if(po.getUsePrivateKey()) {
			 pKey = po.getPrivateKey();
			 if(StrUtils.isNullOrEmpty(pKey)) {
					MyAlert.errorAlert( "使用密钥, 请输入密钥!");
					return;
			 }
		}
		
		//  校验备份类型
		if( po.getSaveDB() == false 
				&& po.getSaveModel() == false 
				&& po.getSaveScript() == false) {
			MyAlert.errorAlert( "选择要备份的类型!");
			return;
		} 
		
		if(po.getSaveDB()){
			backupDBInfo(backupName, pKey, "1", false);
		}
		if(po.getSaveScript()){
			backupScript(backupName, pKey, "2", false);
		}
		
		if(po.getSaveModel()){
//			backupScript(backupName, pKey, "3", false);
		}
	
	}
	
	// 备份链接信息
	public static void backupDBInfo(String backupName, String pKey, String backuptype, boolean isvip) {
		 List<String> jsonLs = new ArrayList<>();
		 AppComponent appcom = ComponentGetter.appComponent;
		 Map<String, SqluckyConnector> connMap = appcom.getAllConnector();
		 List<String> nameList = appcom.getAllConnectorName();
		 for(int i= 0; i<nameList.size() ; i++) {
			 String name = nameList.get(i);
			 SqluckyConnector sc = connMap.get(name);
			 DBConnectorInfoPo infopo = sc.getDBConnectorInfoPo();
			 String json =  infopo.toJson();
			 String encsStr = DesUtil.encrypt(json, pKey);
			 jsonLs.add(encsStr);
			// 非vip 只能同步2个
			if(isvip == false) {
				if(i == 10) {
					break;
				}
			}
		 }
		 
		 UploadData(jsonLs, backupName, backuptype);
	}
	// 备份脚本
	public static void backupScript(String backupName, String pKey, String backuptype, boolean isvip) {
		TreeItem<SqluckyTab> root = ComponentGetter.scriptTreeRoot;
		ObservableList<TreeItem<SqluckyTab>> ls = root.getChildren();
		List<String> vals = new ArrayList<>();
		var conn = SqluckyAppDB.getConn();
		try {
			for (int i= 0; i<ls.size() ; i++) {
				 
				var item = ls.get(i);
				SqluckyTab stab = item.getValue();
				stab.syncScriptPo(conn);
				var tmp = item.getValue().getDocumentPo();
				String jsonStr = tmp.toJsone();
				 String encsStr = DesUtil.encrypt(jsonStr);
				 vals.add(encsStr); 
				
				
				
				// 非vip 只能同步2个
				if(isvip == false) {
					if(i == 2) {
						break;
					}
				}
				
			}
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
		UploadData(vals, backupName, backuptype);
		
	}
	
	public static void UploadData(List<String> jsonLs, String backupName, String backuptype) {
		// 上传数据  
		String zipFile = "";
		try {
			 String tmpDir = FileUtils.getUserDirectoryPath();
			 zipFile =  stringZipFile(jsonLs.toString(), backupName+"_"+backuptype, tmpDir);
			 var map = postParam(backupName, backuptype);
			 HttpPostFile. postFile(ConfigVal.SQLUCKY_URL+"/sqlucky/confUpload", 
					 zipFile,
					 map
					 );
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			// 上传完删除zip
			if(!"".equals(zipFile)) {
				delTmpFile(zipFile);
			}
		}
	}
	
	
	// 讲str写入到文件并压缩文件
	public static String stringZipFile(String val, String fileName, String tmpDir){
		File file = new File(tmpDir, fileName);
		String strFile = file.getAbsolutePath();
		String zipFile = strFile + ".zip";
		try {
			FileTools.save(file, val);
			ZipUtil.zipFile(strFile, zipFile);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			delTmpFile(strFile);
		}
		return zipFile;
	}
	
	// 上传生成的文件
	public static void delTmpFile(String tmpfile) {
		File tmpf = new File(tmpfile);
		if(tmpf.exists()) {
			tmpf.delete();
		}
	}
	
	public static Map<String, String> postParam(String bakName ,String backupType){
		Map<String, String> map = new HashMap<>();
		map.put("EMAIL", ConfigVal.SQLUCKY_EMAIL); 
		map.put("PASSWORD",  ConfigVal.SQLUCKY_PASSWORD);
		map.put("BACKUP_NAME", bakName);
		map.put("BACKUP_TYPE", backupType);
		
		return map;
	}
	
	
	public static void main(String[] args) {
		String tmpDir = FileUtils.getUserDirectoryPath();
		stringZipFile("12111", "1111test11111", tmpDir);
	}
	
	
}
