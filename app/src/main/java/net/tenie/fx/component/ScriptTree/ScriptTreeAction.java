package net.tenie.fx.component.ScriptTree;

import java.io.File;
import java.util.List;

import javafx.scene.control.TreeItem;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

public class ScriptTreeAction {

	public static void showInFloder() {
		List<TreeItem<MyEditorSheet>> selectedItems = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItems();
		if(selectedItems != null && !selectedItems.isEmpty()) {
			for (var item : selectedItems) {
				MyEditorSheet sheet = item.getValue();
				String fn = sheet.getDocumentPo().getExistFileFullName();
				if (StrUtils.isNotNullOrEmpty(fn)) {
					File file = new File(fn);
					CommonUtils.openExplorer(file.getParentFile());
				}
			}
		}
	}

	public static void saveAction() {
		List<TreeItem<MyEditorSheet>> selectedItems = ScriptTabTree.ScriptTreeView.getSelectionModel().getSelectedItems();
		if(selectedItems != null && !selectedItems.isEmpty()) {
			for (var item : selectedItems) {
				MyEditorSheet sheet = item.getValue();
				MyEditorSheetHelper.saveSqlToFileAction(sheet);
			}
		}


	}
}
