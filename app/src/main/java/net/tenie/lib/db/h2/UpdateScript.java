package net.tenie.lib.db.h2;

import java.sql.Connection;

import net.tenie.lib.db.DBTools;

public class UpdateScript {
	
	public static void execUpdate(Connection conn) {
		if ( H2Db.tabExist(conn, "SCRIPT_ARCHIVE") == false) {
			DBTools.execDDLNoErr(conn, SqlTextDao.SCRIPT_ARCHIVE);
		}
	 
		String addCol = "ALTER TABLE SQL_TEXT_SAVE  ADD `SCRIPT_ID` INT(11) NOT NULL ";
		DBTools.execDDLNoErr(conn, addCol);
	}
}
