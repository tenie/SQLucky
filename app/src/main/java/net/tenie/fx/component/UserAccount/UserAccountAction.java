package net.tenie.fx.component.UserAccount;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;

import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.utility.DBTools;
import net.tenie.Sqlucky.sdk.utility.JsonTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.lib.db.h2.AppDao;

public class UserAccountAction {
	
	
	public static boolean  singIn(String email, String password, boolean saveDB) {
		boolean success = singInCheck(email, password);
		if(success) {
			ConfigVal.SQLUCKY_EMAIL = email;
			ConfigVal.SQLUCKY_PASSWORD = password;
			
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
		AppDao.saveConfig(conn, "SQLUCKY_REMEMBER", ConfigVal.SQLUCKY_VIP ?  "1" : "0");
		AppDao.saveConfig(conn, "SQLUCKY_EMAIL", email);
		AppDao.saveConfig(conn, "SQLUCKY_PASSWORD", password); 
		AppDao.saveConfig(conn, "SQLUCKY_VIP", ConfigVal.SQLUCKY_VIP ?  "1" : "0");
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
			String content = Request.post(ConfigVal.getSqluckyServer()+"/sqlucky/login")
			        .bodyForm(Form.form().add("email", email).add("password", password).build())
			        .execute().returnContent().asString();
			SqluckyUser user = JsonTools.strToObj(content, SqluckyUser.class);
			if(user.getIsVip() != null ) {
				if(user.getIsVip() == 1) {
					ConfigVal.SQLUCKY_VIP = true;
				}
				success = true;
			}else {
				ConfigVal.SQLUCKY_VIP = false;
			}
		} catch (IOException e) {
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
			
			ConfigVal.SQLUCKY_EMAIL = sky_email;
			ConfigVal.SQLUCKY_PASSWORD = sky_pw;
			ConfigVal.SQLUCKY_REMEMBER = true;
			ConfigVal.SQLUCKY_VIP =   "1".equals(sky_vip) ?  true: false;
		}

		SqluckyAppDB.closeConn(conn);

	}
}
