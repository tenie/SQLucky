package net.tenie.fx.Action;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import net.tenie.Sqlucky.sdk.component.CommonButtons;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.FindReplaceTextPanel;
import net.tenie.Sqlucky.sdk.component.SqluckyEditor;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.po.db.ProcedureFieldPo;
import net.tenie.Sqlucky.sdk.subwindow.ModalDialog;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.fx.component.MyAreaTab;

/*   @author tenie */
public final class SettingKeyCodeCombination {
	private static Logger logger = LogManager.getLogger(SettingKeyCodeCombination.class);

	private static Map<String, Consumer<String>> keyAction;

	private static void initKeyAction() {
		if (keyAction == null) {
			keyAction = new HashMap<>();

		}
	}

	public static void Setting(Scene scene) {
		initKeyAction();

		Consumer<String> call = keyVal -> {
			Consumer<String> action = keyAction.get(keyVal);
			action.accept("");
		};
		sceneEventFilter(scene, call);
	}

	public static void sceneEventFilter(Scene scene, Consumer<String> call) {
		scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			String val = "";
			if (KeyCode.UNDEFINED == e.getCode()) {
				val = "";
			} else if (e.isControlDown() && e.isAltDown() && e.isShiftDown()) {
				if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.ALT || e.getCode() == KeyCode.SHIFT) {

				} else {
					val = "Ctrl + Alt + Shift + " + e.getCode().getName();
					System.out.println(val);
				}

			} else if (e.isControlDown() && e.isAltDown()) {
				if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.ALT) {

				} else {
					val = "Ctrl + Alt + " + e.getCode().getName();
					System.out.println(val);
				}

			} else if (e.isControlDown() && e.isShiftDown()) {
				if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.SHIFT) {

				} else {
					val = "Ctrl + Shift + " + e.getCode().getName();
					System.out.println(val);
				}

			} else if (e.isAltDown() && e.isShiftDown()) {
				if (e.getCode() == KeyCode.ALT || e.getCode() == KeyCode.SHIFT) {

				} else {
					val = "Alt + Shift + " + e.getCode().getName();
					System.out.println(val);
				}

			} else if (e.isControlDown()) {
				if (e.getCode() == KeyCode.CONTROL) {

				} else {
					val = "Ctrl + " + e.getCode().getName();
					System.out.println(val);
				}
			} else if (e.isAltDown()) {
				if (e.getCode() == KeyCode.ALT) {

				} else {
					val = "ALT + " + e.getCode().getName();
					System.out.println(val);
				}
			} else if (e.isShiftDown()) {
				if (e.getCode() == KeyCode.SHIFT) {

				} else {
					val = "Shift + " + e.getCode().getName();
					System.out.println(val);
				}
			} else {
				System.out.println("其他 : " + e.getCode().getName());
				val = e.getCode().getName();
				System.out.println(val);
			}

			call.accept(val);
		});
	}

	public static void Setting2() {
		Scene scene = ComponentGetter.primaryscene;

		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);

		KeyCombination cx = KeyCombination.keyCombination("alt+/");

		KeyCombination tmpF11 = KeyCombination.keyCombination("F11");

		KeyCodeCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCodeCombination.SHORTCUT_DOWN);
		KeyCodeCombination altR = new KeyCodeCombination(KeyCode.R, KeyCodeCombination.ALT_DOWN);

		KeyCodeCombination altSlash = new KeyCodeCombination(KeyCode.SLASH, KeyCodeCombination.ALT_DOWN);
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
		KeyCodeCombination F2 = new KeyCodeCombination(KeyCode.F2);
		KeyCodeCombination F4 = new KeyCodeCombination(KeyCode.F4);
		KeyCodeCombination F5 = new KeyCodeCombination(KeyCode.F5);

		KeyCodeCombination F6 = new KeyCodeCombination(KeyCode.F6);
		KeyCodeCombination F9 = new KeyCodeCombination(KeyCode.F9);
		KeyCodeCombination F11 = new KeyCodeCombination(KeyCode.F11);

		KeyCodeCombination ctrlO = new KeyCodeCombination(KeyCode.O, KeyCodeCombination.SHORTCUT_DOWN);

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
		JFXButton runbtn = CommonButtons.runbtn; // AllButtons.btns.get("runbtn");
		JFXButton stopbtn = CommonButtons.stopbtn; // AllButtons.btns.get("stopbtn");
		JFXButton runFunPro = CommonButtons.runFunPro; // AllButtons.btns.get("runFunPro");

		scene.getAccelerators().put(tmpF11, () -> {

			var stage = ComponentGetter.primaryStage;
			if (stage.isMaximized()) {
				stage.setMaximized(false);
			} else {
				stage.setMaximized(true);
			}

		});

//		scene.getAccelerators().put(altSlash, () -> {
		scene.getAccelerators().put(cx, () -> {
			var codeArea = SqluckyEditor.getCodeArea();
			if (codeArea.isFocused()) {
				if (CommonUtility.isMacOS()) {
					Platform.runLater(() -> {
						int ar = codeArea.getAnchor();
						String str = codeArea.getText(ar - 1, ar);
						if (str.equals("÷")) {
							codeArea.deleteText(ar - 1, ar);
						}
					});
				} else if (CommonUtility.isLinuxOS()) {
					Platform.runLater(() -> {
						int ar = codeArea.getAnchor();
						String str = codeArea.getText(ar - 1, ar);
						if (str.equals("/")) {
							codeArea.deleteText(ar - 1, ar);
						}
					});
				}

				SqluckyEditor.currentMyTab().getSqlCodeArea().callPopup();
			}

		});

		scene.getAccelerators().put(F9, () -> {
			System.gc();
			ModalDialog.Confirmation("Finded line data");
//			CodeArea code = SqluckyEditor.getCodeArea();
//			var pgs = code.getParagraphs();
//			String tmp = "";
//			for(int i = 0; i < pgs.size(); i++) {
//				var val = code.getParagraphSelection(i);
//				if(val.getStart() > 0 || val.getEnd() > 0) {
//					tmp += pgs.get(i).getText()+ "\n"; 
//				} 
//			}
//			System.out.println(tmp);

		});

		scene.getAccelerators().put(escbtn, () -> {
			CommonAction.pressBtnESC();
		});

		scene.getAccelerators().put(ctrlShiftD, () -> {
			CommonAction.shortcutShowDataDatil();
		});

		// 查找 替换
		scene.getAccelerators().put(ctrlF, () -> {
			CommonUtility.findReplace(false);
		});
		// 查找 替换
		scene.getAccelerators().put(ctrlR, () -> {
			CommonUtility.findReplace(true);
		});

		// 查找
		scene.getAccelerators().put(F3, () -> {
			FindReplaceTextPanel.findSelectedString();
		});

		// 运行SQL
		scene.getAccelerators().put(ctrlEnter, () -> {
			if (!runbtn.disabledProperty().getValue()) {
				RunSQLHelper.runSQLMethod();
			}
		});
		// 运行 选中行中的 SQL
		scene.getAccelerators().put(altR, () -> {
			if (!runbtn.disabledProperty().getValue()) {
				RunSQLHelper.runCurrentLineSQLMethod();
			}
		});

		// RunSQLHelper.runCurrentLineSQLMethod();

		// 停止真正运行的sql
		scene.getAccelerators().put(ctrlI, () -> {
			RunSQLHelper.stopSQLMethod();
		});
		// 添加代码窗口
		scene.getAccelerators().put(ctrlT, () -> {
			MyAreaTab.addCodeEmptyTabMethod();
		});
		// close code tab
//		scene.getAccelerators().put(ctrlW, () -> {
//			System.out.println("???");
//			SqlEditor.closeEditor();
		// 关闭数据窗口
//			logger.info(ctrlW);
//			Tab t = ComponentGetter.dataTab.getSelectionModel().getSelectedItem();
//			ComponentGetter.dataTab.getTabs().remove(t);

//		});
		// 注释代码
//		scene.getAccelerators().put(ctrlSLASH, () -> {
//			CommonAction.addAnnotationSQLTextSelectText();
//
//		});
		// format sql
		scene.getAccelerators().put(ctrlShiftF, () -> {
			CommonAction.formatSqlText();
		});

		// save file
		scene.getAccelerators().put(ctrlS, () -> {
//			CommonAction.saveSqlAction();
			CommonAction.ctrlAndSAction();

		});

		scene.getAccelerators().put(ctrlE, () -> {
			ComponentGetter.primaryscene.lookup("#" + ConfigVal.pageSize + "row");

		});

		// 压缩代码
		scene.getAccelerators().put(ctrlP, () -> {
			CommonAction.pressSqlText();
//			CodeArea code = SqlcukyEditor.getCodeArea();
//			 code.moveTo(0);
		});

		scene.getAccelerators().put(F5, () -> {
//			CodeArea code = SqlcukyEditor.getCodeArea();
//			int idx =	code.getCaretPosition();  // 光标位置		
//			System.out.println(idx);
		});

		scene.getAccelerators().put(F1, () -> {
//			MyAlert.notification("Error", "Connect is Closed!", MyAlert.NotificationType.Error);
//			ComponentGetter.appComponent.currentDBInfoNodeType();
//			ModalDialog.Confirmation("mmm");
//			MyAlert.myConfirmation("??" ,  v->{
//				System.out.println(1111);
//			});
//			AppComponent.currentDBInfoNodeTyp
//			// test caret
//			var codeArea = SqlcukyEditor.getCodeArea();
//			Bounds  bd = codeArea.caretBoundsProperty().getValue().get();
//			double x = bd.getCenterX();
//			double y = bd.getCenterY();
//			double z = bd.getCenterZ();
//			
//			System.out.println(x);
//			System.out.println(y);
//			System.out.println(z);
//			 
////			MyAutoComplete.showPop(x, y+7, ""); 
//			SqlcukyEditor.currentMyTab().getSqlCodeArea().showAutoComplete(x, y, ""); 
		});

		// 书签查找
		scene.getAccelerators().put(F2, () -> {
//			ButtonAction.nextBookmark(true);
			SqluckyEditor.currentMyTab().getSqlCodeArea().nextBookmark(true);
		});

		scene.getAccelerators().put(F4, () -> {
//			CommonAction.dbInfoTreeQuery(AppWindowComponentGetter.dbInfoTreeFilter);
//			StackPane root = ComponentGetter.primarySceneRoot;
//			LoadingAnimation.addLoading(root);
		});

		scene.getAccelerators().put(F6, () -> {
//			CommonAction.dbInfoTreeQuery(AppWindowComponentGetter.dbInfoTreeFilter);
			StackPane root = ComponentGetter.primarySceneRoot;
			LoadingAnimation.rmLoading(root);
		});

//		scene.getAccelerators().put(F4, () -> { 
//			try {
//				callProcedure(DBConns.getCurrentConnectPO().getConn());
//			} catch (SQLException e) { 
//				e.printStackTrace();
//			} 
//		});

	}

	// TODO 获取查询的结果, 返回字段名称的数据和 值的数据
	public static void callProcedure(Connection conn) throws SQLException {
		// DB对象
		CallableStatement call = null;
		ResultSet rs = null;
		try {

//				String callsql = "{call   myProcedure4(?,?)}";
			String callsql = "{call   F_GETSEQDBID2(?,?)}";
			call = conn.prepareCall(callsql);
			call.setObject(1, "ACCIDENT_VEHICLE_ACCOUNT");
//				call.setObject(2, "222");
			call.registerOutParameter(2, java.sql.Types.BIGINT);
//				java.sql.Types.VARCHAR;
//				call.registerOutParameter(3, java.sql.Types.VARCHAR);

			// 处理结果集
			call.execute();

			Object objRtn = call.getObject(2);
//	            System.out.println(objRtn);
//			    call.registerOutParameter(0, null);
//			    call.

		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	public static List<String> callProcedure(Connection conn, String proName, List<ProcedureFieldPo> pfp)
			throws SQLException {
		// DB对象
		CallableStatement call = null;
		ResultSet rs = null;
		List<String> val = new ArrayList<>();
		try {
			if (pfp.size() > 0) {
				String callsql = "{call " + proName + "(";
				for (int i = 0; i < pfp.size(); i++) {
					callsql += "? ,";
				}

				callsql = callsql.substring(0, callsql.lastIndexOf(","));
				callsql += " ) }";
				call = conn.prepareCall(callsql);

				for (int i = 0; i < pfp.size(); i++) {
					ProcedureFieldPo po = pfp.get(i);
					if (po.isIn()) {
						call.setObject(i + 1, po.getValue());
					}
					if (po.isOut()) {
						call.registerOutParameter(i + 1, CommonConst.PROCEDURE_TYPE.get(po.getTypeName()));
					}
				}
				// 处理结果集
				call.execute();
				for (int i = 0; i < pfp.size(); i++) {
					ProcedureFieldPo po = pfp.get(i);
					if (po.isOut()) {
						Object objRtn = call.getObject(i + 1);
						val.add(objRtn.toString());
					}

				}

			}

			return val;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				rs.close();
		}
	}

	private static void fireEvent(JFXButton btn) {
		btn.fireEvent(new Event(MouseEvent.MOUSE_CLICKED));
	}

}
