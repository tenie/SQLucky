package net.tenie.lib.db.h2;

import java.sql.Connection;

import net.tenie.Sqlucky.sdk.utility.DBTools;

public class UpdateScript {
	
	public static void execUpdate(Connection conn) {
		if ( AppDao.tabExist(conn, "SCRIPT_ARCHIVE") == false) {
			DBTools.execDDLNoErr(conn, AppDao.SCRIPT_ARCHIVE);
		}
	 
		String addCol = "ALTER TABLE SQL_TEXT_SAVE  ADD `SCRIPT_ID` INT(11) NOT NULL ";
		DBTools.execDDLNoErr(conn, addCol);
	}
}
