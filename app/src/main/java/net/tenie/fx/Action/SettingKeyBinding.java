package net.tenie.fx.Action;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

/*   @author tenie */
public final class SettingKeyBinding {
	private static Logger logger = LogManager.getLogger(SettingKeyBinding.class);

	public static String codeNameToSymbol(String codeName) {
		if ("Slash".equals(codeName)) {
			codeName = "/";
		} else if ("Period".equals(codeName)) {
			codeName = ".";
		} else if ("Minus".equals(codeName)) {
			codeName = "-";
		} else if ("Comma".equals(codeName)) {
			codeName = ",";
		} else if ("Back Quote".equals(codeName)) {
			codeName = "`";
		} else if ("Equals".equals(codeName)) {
			codeName = "=";
		} else if ("Open Bracket".equals(codeName)) {
			codeName = "[";
		} else if ("Close Bracket".equals(codeName)) {
			codeName = "]";
		} else if ("Back Slash".equals(codeName)) {
			codeName = "\\";
		} else if ("Semicolon".equals(codeName)) {
			codeName = ";";
		} else if ("Quote".equals(codeName)) {
			codeName = "'";
		} else {
			codeName = codeName.toUpperCase();
		}

		return codeName;

	}

	public static String windowsSetup(KeyEvent e) {
		String val = "";
		String codeName = e.getCode().getName();
		codeName = codeNameToSymbol(codeName);
		if (KeyCode.UNDEFINED == e.getCode()) {
			val = "";
		} else if (e.isControlDown() && e.isAltDown() && e.isShiftDown()) {
			if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.ALT || e.getCode() == KeyCode.SHIFT) {

			} else {
				val = "Ctrl + Alt + Shift + " + codeName;
			}

		} else if (e.isControlDown() && e.isAltDown()) {
			if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.ALT) {

			} else {
				val = "Ctrl + Alt + " + codeName;
			}

		} else if (e.isControlDown() && e.isShiftDown()) {
			if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.SHIFT) {

			} else {
				val = "Ctrl + Shift + " + codeName;
			}

		} else if (e.isAltDown() && e.isShiftDown()) {
			if (e.getCode() == KeyCode.ALT || e.getCode() == KeyCode.SHIFT) {

			} else {
				val = "Alt + Shift + " + codeName;
			}

		} else if (e.isControlDown()) {
			if (e.getCode() == KeyCode.CONTROL) {

			} else {
				val = "Ctrl + " + codeName;
			}
		} else if (e.isAltDown()) {
			if (e.getCode() == KeyCode.ALT) {

			} else {
				val = "ALT + " + codeName;
			}
		} else if (e.isShiftDown()) {
			if (e.getCode() == KeyCode.SHIFT) {

			} else {
				val = "Shift + " + codeName;
			}
		} else {
			logger.debug("其他 : " + codeName);
			val = codeName;
		}
		logger.debug(val);
		return val;
	}

	/**
	 * Command (or Cmd) ⌘ 
	 * Option (or Alt) ⌥
	 * Shift ⇧
	 * Control (or Ctrl) ⌃
	 * 
	 * Caps Lock ⇪
	 * @param e
	 * @return
	 */
	public static String macOsSetup(KeyEvent e) {
		String val = "";
		String codeName = e.getCode().getName();
		codeName = codeNameToSymbol(codeName);
		if (KeyCode.UNDEFINED == e.getCode()) {
			val = "";
		} else if (e.isShortcutDown() && e.isControlDown() && e.isAltDown() && e.isShiftDown()) {
			if (e.getCode() == KeyCode.COMMAND || e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.ALT || e.getCode() == KeyCode.SHIFT) {

			} else {
				val = "⌘ + ⌃ + ⌥ + ⇧ + " + codeName; // ⌘  
			}

		} 
		else if (e.isShortcutDown() && e.isAltDown() && e.isShiftDown()) {
			if (e.getCode() == KeyCode.COMMAND || e.getCode() == KeyCode.ALT || e.getCode() == KeyCode.SHIFT) {

			} else {
				val = "⌘ + ⌥ + ⇧ + " + codeName; // ⌘  
			}

		}else if (e.isShortcutDown() && e.isControlDown() && e.isAltDown()) {
			if (e.getCode() == KeyCode.COMMAND || e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.ALT) {

			} else {
				val = "⌘ + ⌥ + ⌃ + " + codeName; // ⌘  
			}

		}
		else if (e.isShortcutDown() && e.isAltDown()) {
			if (e.getCode() == KeyCode.COMMAND || e.getCode() == KeyCode.ALT) {

			} else {
				val = "⌘ + ⌥ + " + codeName;
			}

		} else if (e.isShortcutDown() && e.isShiftDown()) {
			if (e.getCode() == KeyCode.COMMAND || e.getCode() == KeyCode.SHIFT) {

			} else {
				val = "⌘ + ⇧ + " + codeName;
			}

		}  else if (e.isShortcutDown() && e.isControlDown()) {
			if (e.getCode() == KeyCode.COMMAND || e.getCode() == KeyCode.CONTROL) {

			} else {
				val = "⌘ + ⌃ + " + codeName;
			}

		} 
		else if (e.isControlDown() && e.isAltDown() && e.isShiftDown()) {
			if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.ALT || e.getCode() == KeyCode.SHIFT) {

			} else {
				val = "⌃ + ⌥ + ⇧ + " + codeName;
			}

		} else if (e.isControlDown() && e.isAltDown()) {
			if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.ALT) {

			} else {
				val = "⌃ + ⌥ + " + codeName;
			}

		} else if (e.isControlDown() && e.isShiftDown()) {
			if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.SHIFT) {

			} else {
				val = "⌃ + ⇧ + " + codeName;
			}

		} else if (e.isAltDown() && e.isShiftDown()) {
			if (e.getCode() == KeyCode.ALT || e.getCode() == KeyCode.SHIFT) {

			} else {
				val = "⌥ + ⇧ + " + codeName;
			}

		}else if (e.isShortcutDown()) {
			if (e.getCode() == KeyCode.COMMAND) {

			} else {
				val = "⌘ + " + codeName;
			}
		} else if (e.isControlDown()) {
			if (e.getCode() == KeyCode.CONTROL) {

			} else {
				val = "⌃ + " + codeName;
			}
		} else if (e.isAltDown()) {
			if (e.getCode() == KeyCode.ALT) {

			} else {
				val = "⌥ + " + codeName;
			}
		} else if (e.isShiftDown()) {
			if (e.getCode() == KeyCode.SHIFT) {

			} else {
				val = "⇧ + " + codeName;
			}
		} else {
			logger.debug("其他 : " + codeName);
			val = codeName;
		}
		logger.debug(val);
		return val;
	}

	public static void sceneEventFilter(Scene scene, Consumer<String> call) {
		scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			String val = "";
			boolean isMacOs = CommonUtils.isMacOS();
			
			if(isMacOs) {
				val = macOsSetup(e);
			}else {
				val = windowsSetup(e);
			}

			call.accept(val);

		});

	}



}
