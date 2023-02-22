package net.tenie.plugin.backup.component;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;

public class WorkDataBackupAction {

	// 触发备份按钮
	public static void BackupBtn() {
		backupDBInfo(false);
		backupScript(false);
	}
	
	public static void backupDBInfo(boolean isvip) {
		
	}
	
	public static void backupScript(boolean isvip) {
		TreeItem<SqluckyTab> root = ComponentGetter.scriptTreeRoot;
		ObservableList<TreeItem<SqluckyTab>> ls = root.getChildren();
		List<String> vals = new ArrayList<>();
		var conn = SqluckyAppDB.getConn();
		try {
			int idx = 0;
			for (int i= 0; i<ls.size() ; i++) {
				 
				var item = ls.get(i);
				SqluckyTab stab = item.getValue();
				stab.syncScriptPo(conn);
				var tmp = item.getValue().getDocumentPo();
				vals.add(tmp.toJsone());
				
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
//		System.out.println(vals);
	}
}
