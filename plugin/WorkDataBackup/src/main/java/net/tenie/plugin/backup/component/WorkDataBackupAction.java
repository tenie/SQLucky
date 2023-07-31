package net.tenie.plugin.backup.component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.DBTools;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.DesUtil;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.JsonTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.ZipUtil;
import net.tenie.Sqlucky.sdk.utility.net.HttpUtil;
import net.tenie.plugin.backup.po.BackupInfoPO;

/**
 * 备份相关的一些执行函数
 * 
 * @author tenie
 *
 */
public class WorkDataBackupAction {
	private static String httpUploadUrl() {
		return ConfigVal.getSqluckyServer() + "/sqlucky/backupFileUpload";
	}

	private static String httpDownloadUrl() {
		return ConfigVal.getSqluckyServer() + "/sqlucky/backupDownload";

	}

	public static String localSaveDir() {
		return DBTools.dbFilePath();
	}

	// 检查密钥最小长度
	public static boolean checkMinLength(String val) {
		if (val.length() >= 8) {
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
				if (checkMinLength(pKey) == false) {
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
			String type = "";
			String usePrivateKey = "N";
			if (po.getSaveDB()) {
				int length = backupDBInfo(saveBakDir, pKey, "1", ConfigVal.SQLUCKY_VIP.get());
				type += "Connection " + length + ", ";
			}
			if (po.getSaveScript()) {

				int length = backupScript(saveBakDir, pKey, "2", ConfigVal.SQLUCKY_VIP.get());
				type += "Script " + length;
			}

			if (po.getSaveModel()) {
//				backupScript(backupName, pKey, "3", false);
			}
			// 使用 密钥, 在压缩包里放一个空文件
			if (po.getUsePrivateKey()) {
				usePrivateKey = "Y";
				stringToFile(" ", "PK", saveBakDir);
			}

			diskPath = new File(saveDir, backupName + ".zip");

			ZipUtil.ZipDirectory(saveBakDir, diskPath.getAbsolutePath());

			// 上传 zip
			var map = postParam(backupName, type, usePrivateKey);
			HttpUtil.postFile(httpUploadUrl(), diskPath.getAbsolutePath(), map);

		} catch (IOException e) {
			e.printStackTrace();
			MyAlert.errorAlert("失败");
			return;
		} finally {
			if (diskPath != null && diskPath.exists()) {
				diskPath.delete();
			}

			if (sf != null && sf.exists()) {
				FileTools.deleteDir(sf);
			}

		}
		MyAlert.infoAlert("ok");

	}

	/**
	 * 备份链接信息
	 * 
	 * @param tmpDir     保存的目录
	 * @param pKey       密钥
	 * @param backuptype 备份类型 . 数据库链接 1
	 * @param isvip      是否vip
	 * @return
	 */
	public static int backupDBInfo(String tmpDir, String pKey, String backuptype, boolean isvip) {
		List<String> jsonLs = new ArrayList<>();
		AppComponent appcom = ComponentGetter.appComponent;
		Map<String, SqluckyConnector> connMap = appcom.getAllConnector();
		List<String> nameList = appcom.getAllConnectorName();
		int i = 0;
		for (i = 0; i < nameList.size(); i++) {
			String name = nameList.get(i);
			SqluckyConnector sc = connMap.get(name);
			DBConnectorInfoPo infopo = sc.getDBConnectorInfoPo();
			String json = infopo.toJsonStr();
			String encsStr = DesUtil.encrypt(json, pKey);
			jsonLs.add(encsStr);
			// 非vip 只能同步2个
			if (isvip == false) {
				if (i == 10) {
					break;
				}
			}
		}
		String strFile = "";
		// 将 字符串 写入到文件
		if (jsonLs.size() > 0) {
			String jsonStr = JsonTools.listToJson(jsonLs);
			strFile = stringToFile(jsonStr, backuptype, tmpDir);
		}

		return i > 0 ? i + 1 : i;
	}

	// 备份脚本
	public static int backupScript(String tmpDir, String pKey, String backuptype, boolean isvip) {
		TreeItem<SqluckyTab> root = ComponentGetter.scriptTreeRoot;
		ObservableList<TreeItem<SqluckyTab>> ls = root.getChildren();
		List<String> vals = new ArrayList<>();
		var conn = SqluckyAppDB.getConn();
		int i = 0;
		try {

			for (i = 0; i < ls.size(); i++) {
				var item = ls.get(i);
				SqluckyTab stab = item.getValue();
				stab.syncScriptPo(conn);
				DocumentPo tmp = item.getValue().getDocumentPo();
				String jsonStr = tmp.toJsone();
				String encsStr = DesUtil.encrypt(jsonStr, pKey);
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
			strFile = stringToFile(jsonStr, backuptype, tmpDir);
		}

		return i > 0 ? i + 1 : i;
	}

	/**
	 * str写入到文件并压缩文件
	 * 
	 * @param val
	 * @param fileName
	 * @param tmpDir
	 * @return
	 */
	public static String stringToFile(String val, String fileName, String tmpDir) {
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
	 * 
	 * @param bakName
	 * @param backupType
	 * @return
	 */
	public static Map<String, String> postParam(String bakName, String backupType, String usePrivatekey) {
		Map<String, String> map = new HashMap<>();
		map.put("EMAIL", ConfigVal.SQLUCKY_EMAIL.get());
		map.put("PASSWORD", ConfigVal.SQLUCKY_PASSWORD.get());
		map.put("BACKUP_NAME", bakName);
		map.put("BACKUP_TYPE", backupType);
		map.put("BACKUP_PRIVATE", usePrivatekey);

		return map;
	}

	public static Map<String, String> downloadParam(String bakID, String bakName) {
		Map<String, String> map = new HashMap<>();
		map.put("EMAIL", ConfigVal.SQLUCKY_EMAIL.get());
		map.put("PASSWORD", ConfigVal.SQLUCKY_PASSWORD.get());
		map.put("BACKUP_ID", bakID);
		map.put("BACKUP_NAME", bakName);

		return map;
	}

	/**
	 * 下载备份文件
	 */
	public static File downloadBackup(String id, String bakName) {
		Map<String, String> hm = downloadParam(id, bakName);
		String saveDir = localSaveDir();
		File sf = new File(saveDir, bakName);
		HttpUtil.downloadByPost(httpDownloadUrl(), sf.getAbsolutePath(), hm);
		return sf;
	}

	/**
	 * 解压缩 备份文件
	 * 
	 * @param bakZipFile zip文件
	 * @param bakName    备份文件名称
	 * @return
	 */
	public static Map<String, File> unZipBackupFile(File bakZipFile, String bakName) {
		Map<String, File> allBak = new HashMap<>();
		String saveDir = localSaveDir();
		File uzipDir = new File(saveDir, "unzipdir");
		try {
			ZipUtil.UnzipFile(bakZipFile.getAbsolutePath(), uzipDir.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		File bakdir = new File(uzipDir, bakName);
		if (bakdir.exists()) {
			File[] files = bakdir.listFiles();
			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					var tmp = files[i];
					allBak.put(tmp.getName(), tmp);
				}
			}
		}
		return allBak;
	}

	/**
	 * 将连接信息的文件转换为 DBConnectorInfoPo 对象
	 * 
	 * @param bakFile
	 * @throws Exception
	 */
	public static List<DBConnectorInfoPo> decrypDBInfoFile(File bakFile, String pKey) throws Exception {
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

	/**
	 * 将script文件 转为对象
	 * 
	 * @param bakFile
	 * @param pKey
	 * @return
	 * @throws Exception
	 */
	public static List<DocumentPo> decrypScriptFile(File bakFile, String pKey) throws Exception {
		String jsonStr = FileTools.read(bakFile, "UTF-8");
		List<String> ls = JsonTools.jsonToList(jsonStr, String.class);
		List<DocumentPo> pols = new ArrayList<>();
		try {
			for (String item : ls) {
				String jsonval = DesUtil.decrypt(item, pKey);
				DocumentPo po = DocumentPo.toPo(jsonval);
				pols.add(po);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return pols;
	}

	// 下载覆盖
	public static void downloadOverlap(Button downloadOverlapBtn, Button downloadMergeBtn, TextField recoverPK,
			Map<String, File> allFile, boolean dbInfo, boolean script) {
		try {
			// 登入校验
			if (CommonUtility.isLogin("Use Backup must Login") == false) {
				return;
			}
			// 密钥检查
			String pkey = "";
			if (recoverPK.isDisabled() == false) {
				pkey = recoverPK.getText();
				if (WorkDataBackupAction.checkMinLength(pkey) == false) {
					MyAlert.errorAlert("密钥字符长度不小于8位!");
					return;
				}
			}

			downloadOverlapBtn.setDisable(true);
			downloadMergeBtn.setDisable(true);
			// 检查是否使用密钥的tag文件
			if (allFile.get("PK") != null) {
				if (recoverPK.getText().length() == 0) {
					MyAlert.errorAlert("备份文件已加密, 需要密钥才可操作!");
					return;
				}
			}

			// 执行中记录是否发生过错误
			boolean tf = false;
			AppComponent appComponent = ComponentGetter.appComponent;
			// dbinfo 信息覆盖
			if (dbInfo && allFile.get("1") != null) {
				List<DBConnectorInfoPo> pols = null;
				try {
					pols = WorkDataBackupAction.decrypDBInfoFile(allFile.get("1"), pkey);
				} catch (Exception e2) {
					MyAlert.errorAlert("数据解压失败, 可能密钥不正确!");
					return;
				}
				// 使用新的数据重建界面上的链接节点树的数据
				if (pols != null && pols.size() > 0) {
					appComponent.recreateDBinfoTreeData(pols);
				} else {
					MyAlert.errorAlert("数据库连接数据为空, 不操作!");
					tf = true;
				}
			}

			// 脚本 覆盖
			if (script && allFile.get("2") != null) {
				List<DocumentPo> DocumentPoList = null;
				try {
					DocumentPoList = WorkDataBackupAction.decrypScriptFile(allFile.get("2"), pkey);
				} catch (Exception e2) {
					MyAlert.errorAlert("数据解压失败, 可能密钥不正确!");
					return;
				}

				if (DocumentPoList != null && DocumentPoList.size() > 0) {
					appComponent.recreateScriptTreeData(DocumentPoList);
				} else {
					MyAlert.errorAlert("脚本数据为空, 不操作!");
					tf = true;
				}

			}
			MyAlert.infoAlert("已完成!");
		} finally {
			downloadOverlapBtn.setDisable(false);
			downloadMergeBtn.setDisable(false);
		}

	}

	// 下载合并
	public static void downloadMerge(Button downloadOverlapBtn, Button downloadMergeBtn, TextField recoverPK,
			Map<String, File> allFile, boolean dbInfo, boolean script) {
		try {
			// 登入校验
			if (CommonUtility.isLogin("Use Backup must Login") == false) {
				return;
			}
			// 密钥检查
			String pkey = "";
			if (recoverPK.isDisabled() == false) {
				pkey = recoverPK.getText();
				if (WorkDataBackupAction.checkMinLength(pkey) == false) {
					MyAlert.errorAlert("密钥字符长度不小于8位!");
					return;
				}
			}

			downloadOverlapBtn.setDisable(true);
			downloadMergeBtn.setDisable(true);
			// 检查是否使用密钥的tag文件
			if (allFile.get("PK") != null) {
				if (recoverPK.getText().length() == 0) {
					MyAlert.errorAlert("备份文件已加密, 需要密钥才可操作!");
					return;
				}
			}
			// 执行中记录是否发生过错误
			boolean tf = false;
			AppComponent appComponent = ComponentGetter.appComponent;
			// dbinfo 信息Merge
			if (dbInfo && allFile.get("1") != null) {
				boolean isContinue = MyAlert.myConfirmationShowAndWait("连接信息和本地数据合并, 名称相同会被添加*符予以区分, 继续?");
				if (isContinue) {
					List<DBConnectorInfoPo> pols = null;
					try {
						pols = WorkDataBackupAction.decrypDBInfoFile(allFile.get("1"), pkey);
					} catch (Exception e2) {
						MyAlert.errorAlert("数据解压失败, 可能密钥不正确!");
						return;
					}
					// 连接合并操作
					if (pols != null && pols.size() > 0) {
						appComponent.mergeDBinfoTreeData(pols);
					} else {
						MyAlert.errorAlert("数据库连接数据为空, 不操作!");
						tf = true;
					}
				}
			}

			// 脚本 Merge
			if (script && allFile.get("2") != null) {

				boolean isContinue = MyAlert.myConfirmationShowAndWait("Script和本地数据合并, 名称和内容相同会跳过, 继续?");
				if (isContinue) {
					List<DocumentPo> DocumentPoList = null;
					try {
						DocumentPoList = WorkDataBackupAction.decrypScriptFile(allFile.get("2"), pkey);
					} catch (Exception e2) {
						MyAlert.errorAlert("数据解压失败, 可能密钥不正确!");
						return;
					}
					// 脚本合并操作
					if (DocumentPoList != null && DocumentPoList.size() > 0) {
						appComponent.mergeScriptTreeData(DocumentPoList);
					} else {
						MyAlert.errorAlert("脚本数据为空, 不操作!");
						tf = true;
					}
				}

			}
			MyAlert.infoAlert("已完成!");
		} finally {
			downloadOverlapBtn.setDisable(false);
			downloadMergeBtn.setDisable(false);
		}

	}

}
