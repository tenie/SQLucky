package net.tenie.fx.component.UserAccount;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;

import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.utility.DBTools;

public class UserAccountAction {
	
	
	public static boolean  singIn(String email, String password, boolean saveDB) {
		boolean success = singInCheck(email, password);
		if(success) {
			ConfigVal.SQLUCKY_EMAIL = email;
			ConfigVal.SQLUCKY_PASSWORD = password;
			
			if(saveDB) {
				Connection conn = SqluckyAppDB.getConn();
				saveUser(conn, email, password);
				SqluckyAppDB.closeConn(conn);
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
	public static void saveUser(Connection conn, String email, String password) {
		try {
			String delSql = "DELETE FROM SQLUCKY_USER";
			DBTools.execDML(conn, delSql);
			String sql = "insert into SQLUCKY_USER (EMAIL, PASSWORD) values ( '" + email + "' , '" + password + "' )";

			DBTools.execDML(conn, sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean singInCheck(String email, String password) {
		boolean success = false;
		
		try {
			String content = Request.post("http://127.0.0.1:8088/sqlucky/login")
			        .bodyForm(Form.form().add("email", email).add("password", password).build())
			        .execute().returnContent().asString();
			if(content != null && "ok".equals(content)) {
				success = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return success;
		} 
		
		return success;
	}
}
