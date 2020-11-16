package net.tenie.fx.utility.EventAndListener;

import net.tenie.fx.component.*;
import com.jfoenix.controls.JFXButton;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.config.ConfigVal;

/*   @author tenie */
public final class SettingKeyCodeCombination {
	public static void Setting() {
		Scene scene = ComponentGetter.primaryscene;
		KeyCombination cx = KeyCombination.keyCombination("shortcut+X");

		KeyCodeCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCodeCombination.SHORTCUT_DOWN);
		KeyCodeCombination ctrlT = new KeyCodeCombination(KeyCode.T, KeyCodeCombination.SHORTCUT_DOWN);
		KeyCodeCombination ctrlI = new KeyCodeCombination(KeyCode.I, KeyCodeCombination.SHORTCUT_DOWN);
		KeyCodeCombination ctrlW = new KeyCodeCombination(KeyCode.W, KeyCodeCombination.SHORTCUT_DOWN);
		KeyCodeCombination ctrlSLASH = new KeyCodeCombination(KeyCode.SLASH, KeyCodeCombination.SHORTCUT_DOWN);
		KeyCodeCombination ctrlShiftF = new KeyCodeCombination(KeyCode.F, KeyCodeCombination.SHORTCUT_DOWN,
				KeyCodeCombination.SHIFT_DOWN);
		KeyCodeCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.SHORTCUT_DOWN);
		KeyCodeCombination ctrlP = new KeyCodeCombination(KeyCode.P, KeyCodeCombination.SHORTCUT_DOWN);
		KeyCodeCombination ctrlE = new KeyCodeCombination(KeyCode.E, KeyCodeCombination.SHORTCUT_DOWN);
		KeyCodeCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCodeCombination.SHORTCUT_DOWN);
		KeyCodeCombination ctrlR = new KeyCodeCombination(KeyCode.R, KeyCodeCombination.SHORTCUT_DOWN);
		KeyCodeCombination F3 = new KeyCodeCombination(KeyCode.F3);
		KeyCodeCombination F1 = new KeyCodeCombination(KeyCode.F1);

		KeyCodeCombination ctrlShiftX = new KeyCodeCombination(KeyCode.X, KeyCodeCombination.SHORTCUT_DOWN,
				KeyCodeCombination.SHIFT_DOWN);
		KeyCodeCombination ctrlShiftY = new KeyCodeCombination(KeyCode.Y, KeyCodeCombination.SHORTCUT_DOWN,
				KeyCodeCombination.SHIFT_DOWN);
		KeyCodeCombination ctrlShiftT = new KeyCodeCombination(KeyCode.T, KeyCodeCombination.SHORTCUT_DOWN,
				KeyCodeCombination.SHIFT_DOWN);
		KeyCodeCombination ctrlShiftR = new KeyCodeCombination(KeyCode.R, KeyCodeCombination.SHORTCUT_DOWN,
				KeyCodeCombination.SHIFT_DOWN);
		KeyCodeCombination ctrlShiftD = new KeyCodeCombination(KeyCode.D, KeyCodeCombination.SHORTCUT_DOWN,
				KeyCodeCombination.SHIFT_DOWN);
//        KeyCodeCombination ctrlD = new KeyCodeCombination(KeyCode.D, KeyCodeCombination.SHORTCUT_DOWN);
		JFXButton runbtn = AllButtons.btns.get("runbtn");
		JFXButton stopbtn = AllButtons.btns.get("stopbtn");

		scene.getAccelerators().put(ctrlShiftD, () -> {
			CommonAction.shortcutShowDataDatil();
		});

		// 查找 替换
		scene.getAccelerators().put(ctrlF, () -> {
			CommonAction.findReplace(false);
		});
		// 查找 替换
		scene.getAccelerators().put(ctrlR, () -> {
			CommonAction.findReplace(true);
		});

		// 查找
		scene.getAccelerators().put(F3, () -> {
			FindReplaceEditor.findSelectedString();
		});

		// 运行SQL
		scene.getAccelerators().put(ctrlEnter, () -> {
			if (!runbtn.disabledProperty().getValue()) {
				RunSQLHelper.runSQLMethod(runbtn, stopbtn);
			}
		});

		// 停止真正运行的sql
		scene.getAccelerators().put(ctrlI, () -> {
			RunSQLHelper.stopSQLMethod(runbtn, stopbtn);
		});
		// 添加代码窗口
		scene.getAccelerators().put(ctrlT, () -> {
			SqlEditor.addCodeEmptyTabMethod();
		});
		// close code tab
		scene.getAccelerators().put(ctrlW, () -> {
			SqlEditor.closeEditor();

		});
		// 注释代码
		scene.getAccelerators().put(ctrlSLASH, () -> {
			CommonAction.addAnnotationSQLTextSelectText();

		});
		// format sql
		scene.getAccelerators().put(ctrlShiftF, () -> {
			CommonAction.formatSqlText();
		});

		// save file
		scene.getAccelerators().put(ctrlS, () -> {
			CommonAction.saveSqlAction();

		});

		scene.getAccelerators().put(ctrlE, () -> {
			ComponentGetter.primaryscene.lookup("#" + ConfigVal.pageSize + "row");

		});

	}

	private static void fireEvent(JFXButton btn) {
		btn.fireEvent(new Event(MouseEvent.MOUSE_CLICKED));
	}

}
