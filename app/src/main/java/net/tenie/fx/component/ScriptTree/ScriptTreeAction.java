package net.tenie.fx.component.ScriptTree;

import java.io.File;

import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.component.MyAreaTab;

public class ScriptTreeAction {
	
	public static void showInFloder() {
		TreeItem<SqluckyTab> ctt = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItem();
		SqluckyTab tb = ctt.getValue(); 
		String fn = tb.getDocumentPo().getFileFullName();
		if(StrUtils.isNotNullOrEmpty(fn)) {
			File file = new File(fn); 
			CommonUtility.openExplorer(file.getParentFile());
		}
	}
	
	
	public static void saveAction() {
		TreeItem<SqluckyTab> ctt = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItem();
		SqluckyTab mtab = ctt.getValue();
		CommonAction.saveSqlAction(mtab);
	}
}
