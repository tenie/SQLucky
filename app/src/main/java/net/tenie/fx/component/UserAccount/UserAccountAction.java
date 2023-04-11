package net.tenie.fx.component.UserAccount;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.utility.JsonTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.net.HttpUtil;
import net.tenie.lib.db.h2.AppDao;

public class UserAccountAction {
	
	
	public static boolean  singIn(String email, String password, boolean saveDB) {
		boolean success = singInCheck(email, password);
		if(success) {
			ConfigVal.SQLUCKY_EMAIL.set(email);
			ConfigVal.SQLUCKY_PASSWORD.set(password);
			
			if(saveDB) {
				saveUser( email, password);
			}
		}else {
			
		}
		return success;
		
	}
	
	/**
	 * 保存登入信息
	 * @param conn
	 * @param name
	 * @return
	 */
	public static void saveUser(String email, String password) {
		Connection conn = SqluckyAppDB.getConn();
		AppDao.saveConfig(conn, "SQLUCKY_REMEMBER", ConfigVal.SQLUCKY_VIP.get() ?  "1" : "0");
		AppDao.saveConfig(conn, "SQLUCKY_EMAIL", email);
		AppDao.saveConfig(conn, "SQLUCKY_PASSWORD", password); 
		AppDao.saveConfig(conn, "SQLUCKY_VIP", ConfigVal.SQLUCKY_VIP.get() ?  "1" : "0");
		SqluckyAppDB.closeConn(conn);
//		try {
//			String delSql = "DELETE FROM SQLUCKY_USER";
//			DBTools.execDML(conn, delSql);
//			String sql = "insert into SQLUCKY_USER (EMAIL, PASSWORD) values ( '" + email + "' , '" + password + "' )";
//
//			DBTools.execDML(conn, sql);
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	
	public static void delUser() {
		 Connection conn = SqluckyAppDB.getConn();
		 AppDao.deleteConfigKey(conn, "SQLUCKY_REMEMBER");
		 AppDao.deleteConfigKey(conn, "SQLUCKY_EMAIL");
		 AppDao.deleteConfigKey(conn, "SQLUCKY_PASSWORD");
		 AppDao.deleteConfigKey(conn, "SQLUCKY_VIP");
		 SqluckyAppDB.closeConn(conn);
	}
	
	
	public static void rememberUser(boolean tf) {
		 Connection conn = SqluckyAppDB.getConn();
		 if(tf) {
			 AppDao.saveConfig(conn, "SQLUCKY_REMEMBER", "1");
		 }else {
			 AppDao.deleteConfigKey(conn, "SQLUCKY_REMEMBER");
		 }
		
		 SqluckyAppDB.closeConn(conn);
	}
	/**
	 * 登入检查, 账号没问题返回true
	 * @param email
	 * @param password
	 * @return
	 */
	public static boolean singInCheck(String email, String password) {
		boolean success = false;
		try {
			Map<String, String> vals = new HashMap<>();
			vals.put("email", email);	
			vals.put("password", password);
			String content = 
					HttpUtil.post(ConfigVal.getSqluckyServer()+"/sqlucky/login", vals);
//			String content = Request.post(ConfigVal.getSqluckyServer()+"/sqlucky/login")
//			        .bodyForm(Form.form().add("email", email).add("password", password).build())
//			        .execute().returnContent().asString();
			SqluckyUser user = JsonTools.strToObj(content, SqluckyUser.class);
			if(user.getIsVip() != null ) {
				if(user.getIsVip() == 1) {
					ConfigVal.SQLUCKY_VIP.set(true);
				}
				success = true;
			}else {
				ConfigVal.SQLUCKY_VIP.set(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return success;
		} 
		
		return success;
	}
	
	// 程序启动的时候恢复保存的账号信息
	public static void appLanuchInitAccount() {
		// 账号恢复
		Connection conn = SqluckyAppDB.getConn();
		String remember = AppDao.readConfig(conn, "SQLUCKY_REMEMBER");
		if (StrUtils.isNotNullOrEmpty(remember) && "1".equals(remember)) {
			String sky_email = AppDao.readConfig(conn, "SQLUCKY_EMAIL");
			String sky_pw = AppDao.readConfig(conn, "SQLUCKY_PASSWORD");
			String sky_vip = AppDao.readConfig(conn, "SQLUCKY_VIP");
			
			ConfigVal.SQLUCKY_EMAIL.set(sky_email);
			ConfigVal.SQLUCKY_PASSWORD.set(sky_pw);
			ConfigVal.SQLUCKY_REMEMBER.set(true);
			ConfigVal.SQLUCKY_VIP.set("1".equals(sky_vip) ?  true: false);
		}
		// 读取 自定义的 服务器地址
		String hostUrl = AppDao.readConfig(conn, "SQLUCKY_URL_CUSTOM");
		if (StrUtils.isNotNullOrEmpty(hostUrl) ) {
			ConfigVal.SQLUCKY_URL_CUSTOM =  hostUrl;
		}
		String remSet = AppDao.readConfig(conn, "SQLUCKY_REMEMBER_SETTINGS");
		if (StrUtils.isNotNullOrEmpty(remSet)  && "1".equals(remSet) ) {
			ConfigVal.SQLUCKY_REMEMBER_SETTINGS.set(true);
		}
		SqluckyAppDB.closeConn(conn);

	}
	
	// 保存自定义的url 地址
	public static void saveHostValAccount(String hostVal) {
		Connection conn = SqluckyAppDB.getConn();
		AppDao.saveConfig(conn, "SQLUCKY_URL_CUSTOM", hostVal);
		AppDao.saveConfig(conn, "SQLUCKY_REMEMBER_SETTINGS", "1");
		
		SqluckyAppDB.closeConn(conn);
	}
	// 删除自定义的url 地址
	public static void delHostValAccount() {
		Connection conn = SqluckyAppDB.getConn();
//		ConfigVal.SQLUCKY_URL_CUSTOM = "";
		ConfigVal.SQLUCKY_REMEMBER_SETTINGS.set(false);
		AppDao.deleteConfigKey(conn, "SQLUCKY_URL_CUSTOM");
		AppDao.deleteConfigKey(conn, "SQLUCKY_REMEMBER_SETTINGS");
		SqluckyAppDB.closeConn(conn);
	}
}
