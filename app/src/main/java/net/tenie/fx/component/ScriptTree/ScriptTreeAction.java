package net.tenie.fx.component.ScriptTree;

import java.io.File;

import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

public class ScriptTreeAction {

	public static void showInFloder() {
		TreeItem<MyEditorSheet> ctt = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItem();
		MyEditorSheet sheet = ctt.getValue();
		String fn = sheet.getDocumentPo().getFileFullName();
		if (StrUtils.isNotNullOrEmpty(fn)) {
			File file = new File(fn);
			CommonUtils.openExplorer(file.getParentFile());
		}
	}

	public static void saveAction() {
		TreeItem<MyEditorSheet> ctt = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItem();
		MyEditorSheet sheet = ctt.getValue();
//		CommonAction.saveSqlAction(sheet);
		MyEditorSheetHelper.saveSqlAction(sheet);
	}
}
