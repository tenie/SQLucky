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
import net.tenie.Sqlucky.sdk.utility.JsonTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.ZipUtil;
import net.tenie.Sqlucky.sdk.utility.net.HttpDownloadFile;
import net.tenie.Sqlucky.sdk.utility.net.HttpUtil;

public class WorkDataBackupAction {
	private static String httpUploadUrl = ConfigVal.getSqluckyServer()+"/sqlucky/backupFileUpload";
	private static String httpDownloadUrl = ConfigVal.getSqluckyServer()+"/sqlucky/backupDownload";
	
	public static String localSaveDir() {
		String tmpDir = FileUtils.getUserDirectoryPath();
		tmpDir += "/.sqlucky";
		return tmpDir;
	}
	// 检查密钥最小长度
	public static boolean checkMinLength(String val) {
		if( val.length() >= 8) {
			return true;
		}
		return false;
	}
	// 触发备份按钮
	public static void BackupBtn(BackupInfoPO po) {
		File diskPath = null;
		File sf = null;
		try {
			String backupName = po.getBackupName();
			if (StrUtils.isNullOrEmpty(backupName)) {
				MyAlert.errorAlert("备份名称不能为空!");
				return;
			}
			String pKey = "";
			if (po.getUsePrivateKey()) {
				pKey = po.getPrivateKey();
				if (StrUtils.isNullOrEmpty(pKey)) {
					MyAlert.errorAlert("使用密钥, 请输入密钥!");
					return;
				}
				if( checkMinLength(pKey) == false) {
					MyAlert.errorAlert("密钥字符长度不小于8位!");
					return;
				}
				
			}

			// 校验备份类型
			if (po.getSaveDB() == false && po.getSaveModel() == false && po.getSaveScript() == false) {
				MyAlert.errorAlert("选择要备份的类型!");
				return;
			}
			// 创建一个 zip文件
			String saveDir = localSaveDir();
			// 创建一个临时保存的目录
			sf = new File(saveDir, backupName);
			sf.mkdir();
			String saveBakDir = sf.getAbsolutePath();

			if (po.getSaveDB()) {
				backupDBInfo(saveBakDir, pKey, "1", ConfigVal.SQLUCKY_VIP.get());
			}
			if (po.getSaveScript()) {
				backupScript(saveBakDir, pKey, "2", ConfigVal.SQLUCKY_VIP.get());
			}

			if (po.getSaveModel()) {
//				backupScript(backupName, pKey, "3", false);
			}
			// 使用 密钥, 在压缩包里放一个空文件
			if (po.getUsePrivateKey()) {
				stringToFile(" ",  "PK", saveBakDir);
			}
			
			diskPath = new File(saveDir, backupName + ".zip");

			ZipUtil.ZipDirectory(saveBakDir, diskPath.getAbsolutePath());
			// 上传 zip
			var map = postParam(backupName, "4");
			HttpUtil.postFile(httpUploadUrl, diskPath.getAbsolutePath(), map);
			
		} catch (IOException e) {
			e.printStackTrace();
			MyAlert.errorAlert("失败");
			return ;
		} finally {
			if (diskPath != null && diskPath.exists()) {
				diskPath.delete();
			}

			if (sf != null && sf.exists()) {
				FileTools.deleteDir(sf);
			} 
			
		}
		MyAlert.infoAlert("ok", "ok");

	}
	
	/**
	 *  备份链接信息
	 * @param tmpDir 保存的目录
	 * @param pKey   密钥
	 * @param backuptype  备份类型 . 数据库链接 1
	 * @param isvip 是否vip
	 * @return
	 */
	public static String backupDBInfo(String tmpDir, String pKey, String backuptype, boolean isvip) {
		 List<String> jsonLs = new ArrayList<>();
		 AppComponent appcom = ComponentGetter.appComponent;
		 Map<String, SqluckyConnector> connMap = appcom.getAllConnector();
		 List<String> nameList = appcom.getAllConnectorName();
		 for(int i= 0; i<nameList.size() ; i++) {
			 String name = nameList.get(i);
			 SqluckyConnector sc = connMap.get(name);
			 DBConnectorInfoPo infopo = sc.getDBConnectorInfoPo();
			 String json =  infopo.toJsonStr();
			 String encsStr = DesUtil.encrypt(json, pKey);
			 jsonLs.add(encsStr);
			// 非vip 只能同步2个
			if(isvip == false) {
				if(i == 10) {
					break;
				}
			}
		 }
		 String strFile = "";
		 // 将 字符串 写入到文件
		 if (jsonLs.size() > 0) {
			String jsonStr = JsonTools.listToJson(jsonLs);
		    strFile = stringToFile(jsonStr,  backuptype, tmpDir);
		 }
	
		return strFile;
	}
	// 备份脚本
	public static String backupScript(String tmpDir, String pKey, String backuptype, boolean isvip) {
		TreeItem<SqluckyTab> root = ComponentGetter.scriptTreeRoot;
		ObservableList<TreeItem<SqluckyTab>> ls = root.getChildren();
		List<String> vals = new ArrayList<>();
		var conn = SqluckyAppDB.getConn();
		try {
			for (int i = 0; i < ls.size(); i++) {
				var item = ls.get(i);
				SqluckyTab stab = item.getValue();
				stab.syncScriptPo(conn);
				var tmp = item.getValue().getDocumentPo();
				String jsonStr = tmp.toJsone();
				String encsStr = DesUtil.encrypt(jsonStr,  pKey);
				vals.add(encsStr);

				// 非vip 只能同步2个
				if (isvip == false) {
					if (i == 2) {
						break;
					}
				}

			}
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
		
		 String strFile = "";
		 // 将 字符串 写入到文件
		 if (vals.size() > 0) { 
			String jsonStr = JsonTools.listToJson(vals);
		    strFile = stringToFile(jsonStr,  backuptype, tmpDir);
		 }
		
		return strFile;
	}
 
	/**
	 * str写入到文件并压缩文件
	 * @param val
	 * @param fileName
	 * @param tmpDir
	 * @return
	 */
	public static String stringToFile(String val, String fileName, String tmpDir){
		File file = new File(tmpDir, fileName);
		String strFile = file.getAbsolutePath();
		try {
			FileTools.save(file, val);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strFile;
	}
	/**
	 * 上传备份文件需要的post参数
	 * @param bakName
	 * @param backupType
	 * @return
	 */
	public static Map<String, String> postParam(String bakName ,String backupType){
		Map<String, String> map = new HashMap<>();
		map.put("EMAIL", ConfigVal.SQLUCKY_EMAIL.get()); 
		map.put("PASSWORD",  ConfigVal.SQLUCKY_PASSWORD.get());
		map.put("BACKUP_NAME", bakName);
		map.put("BACKUP_TYPE", backupType);
		
		return map;
	}
	
	public static Map<String, String> downloadParam(String bakID, String bakName){
		Map<String, String> map = new HashMap<>();
		map.put("EMAIL", ConfigVal.SQLUCKY_EMAIL.get()); 
		map.put("PASSWORD",  ConfigVal.SQLUCKY_PASSWORD.get());
		map.put("BACKUP_ID", bakID);
		map.put("BACKUP_NAME", bakName);
		
		return map;
	}
	
	
	/**
	 * 下载备份文件
	 */
	public static File downloadBackup(String id ,String bakName) {
		Map<String , String> hm =  downloadParam(id, bakName);
		String saveDir = localSaveDir();
		File sf = new File(saveDir, bakName);
		HttpDownloadFile.getInfo(httpDownloadUrl, hm, sf.getAbsolutePath());
		return sf;
	}
	
	/**
	 * 解压缩 备份文件
	 * @param bakZipFile zip文件
	 * @param bakName    备份文件名称
	 * @return 
	 */
	public static Map<String, File> unZipBackupFile(File bakZipFile, String bakName){
		Map<String, File> allBak = new HashMap<>();
		String saveDir = localSaveDir(); 
		File uzipDir = new File(saveDir, "unzipdir");
		try {
			ZipUtil.UnzipFile(bakZipFile.getAbsolutePath(), uzipDir.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		File bakdir = new File(uzipDir, bakName);
		if(bakdir.exists()) {
			File[] files = bakdir.listFiles();
			if(files != null && files.length > 0) {
				for(int i = 0 ; i < files.length; i++) {
					var tmp = files[i];
					allBak.put(tmp.getName(), tmp);
				}
			}
		}
		return allBak;
	}
	
	/**
	 * 解析数据
	 * @param bakFile
	 * @throws Exception 
	 */
	public static List<DBConnectorInfoPo> parseBackupFile(File bakFile, String pKey) throws Exception {
		String jsonStr = FileTools.read(bakFile, "UTF-8");
		List<String> ls = JsonTools.jsonToList(jsonStr, String.class);
		List<DBConnectorInfoPo> pols = new ArrayList<>();
		try {
			for (String item : ls) {
				String jsonval = DesUtil.decrypt(item, pKey);
				DBConnectorInfoPo po = DBConnectorInfoPo.toPo(jsonval);
				pols.add(po);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return pols;
	}
	
	public static void main(String[] args) throws IOException {
//		File zipFIle = new File("C:\\Users\\tenie\\.sqlucky" , "yeeerwewe" );
//		ZipUtil.UnzipFile(zipFIle.getAbsolutePath(), "C:\\Users\\tenie\\.sqlucky\\unzipdir");
		stringToFile(" ",  "Pk", "C:\\Users\\tenie\\.sqlucky\\kkk1");
//		System.out.println("???");
	}
}
