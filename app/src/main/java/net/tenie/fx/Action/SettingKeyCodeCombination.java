package net.tenie.fx.Action;

import net.tenie.fx.PropertyPo.ProcedureFieldPo;
import net.tenie.fx.component.*;
import net.tenie.fx.config.DBConns;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SqlcukyEditor;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;

/*   @author tenie */
public final class SettingKeyCodeCombination {
	private static Logger logger = LogManager.getLogger(SettingKeyCodeCombination.class);
	public static void Setting() {
		Scene scene = ComponentGetter.primaryscene;
		
		
		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		
		KeyCombination cx = KeyCombination.keyCombination("shortcut+X");

		KeyCodeCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCodeCombination.SHORTCUT_DOWN);
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
		KeyCodeCombination F9 = new KeyCodeCombination(KeyCode.F9);

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
		JFXButton runbtn = AllButtons.btns.get("runbtn");
		JFXButton stopbtn = AllButtons.btns.get("stopbtn");
		JFXButton runFunPro = AllButtons.btns.get("runFunPro");

		scene.getAccelerators().put(altSlash, () -> {
			var codeArea = SqlcukyEditor.getCodeArea();
			if ( CommonUtility.isMacOS() ) {
				Platform.runLater(()->{
					int ar = codeArea.getAnchor();
					String str = codeArea.getText(ar -1 , ar);
					if(str.equals("÷")) {
						codeArea.deleteText(ar - 1, ar);
					}
				});  
			}else if ( CommonUtility.isLinuxOS() ) {
				Platform.runLater(()->{
					int ar = codeArea.getAnchor();
					String str = codeArea.getText(ar -1 , ar);
					if(str.equals("/")) {
						codeArea.deleteText(ar - 1, ar);
					}
				});  
			}
			
//			Bounds  bd = codeArea.caretBoundsProperty().getValue().get();
//			double x = bd.getCenterX();
//			double y = bd.getCenterY();
			if (codeArea.isFocused()) {
				SqlcukyEditor.currentMyTab().getSqlCodeArea().callPopup();
				
//				SqlEditor.callPopup( codeArea);
			}
			
			
		});
		
		scene.getAccelerators().put(F9, () -> {
		
			
//			VBox b = ComponentGetter.mainWindow;
//			ObservableList<Node> ls = b.parentProperty().get().getChildrenUnmodifiable();
//			ContextMenu cn = (ContextMenu) ls.get(1); 
			
//			  n = ls.get(1);
//			
//			n.autoHideProperty().set(false);
			
		});
		
		scene.getAccelerators().put(escbtn, () -> {
			CommonAction.pressBtnESC();
		});
		
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
//			boolean b = runbtn.disabledProperty().getValue();
			if (!runbtn.disabledProperty().getValue()) {
				RunSQLHelper.runSQLMethod();
			}
		});

		// 停止真正运行的sql
		scene.getAccelerators().put(ctrlI, () -> {
			RunSQLHelper.stopSQLMethod();
		});
		// 添加代码窗口
		scene.getAccelerators().put(ctrlT, () -> {
			MyTab.addCodeEmptyTabMethod();
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

		});
		
		scene.getAccelerators().put(F1, () -> {
			
			// test caret
			var codeArea = SqlcukyEditor.getCodeArea();
			Bounds  bd = codeArea.caretBoundsProperty().getValue().get();
			double x = bd.getCenterX();
			double y = bd.getCenterY();
			double z = bd.getCenterZ();
			
			System.out.println(x);
			System.out.println(y);
			System.out.println(z);
			 
//			MyAutoComplete.showPop(x, y+7, ""); 
			SqlcukyEditor.currentMyTab().getSqlCodeArea().showAutoComplete(x, y, ""); 
		});
		
		
		// 书签查找
		scene.getAccelerators().put(F2, () -> {  
//			ButtonAction.nextBookmark(true);
			SqlcukyEditor.currentMyTab().getSqlCodeArea().nextBookmark(true);
		});
		
		
		
		
		scene.getAccelerators().put(F4, () -> {
			
			try {
				callProcedure(DBConns.getCurrentConnectPO().getConn());
			} catch (SQLException e) { 
				e.printStackTrace();
			}
//			List<String > fields = new ArrayList<>();
//			fields.add("111");
//			fields.add("222");
//			fields.add("33");
//			new ProcedureExecuteWindow(fields, "ffff");
//			
			
			
//			CommonAction.escapeWindowsUiBug();
//			JFXComboBox<Label> connsComboBox = ButtonFactory.connsComboBox; 
//			int i = connsComboBox.getSelectionModel().getSelectedIndex();
//			if(i < 0) {
//				connsComboBox.getSelectionModel().select(0);
//			}else {
//				connsComboBox.getSelectionModel().select(i);
//			}
			
			
//			System.out.println( ComponentGetter.currentDataTabID());
//			CodeArea codeArea  = SqlEditor.getCodeArea(); 
//			 
//			codeArea.setShowCaret(CaretVisibility.ON);;
//			MouseEvent me = myEvent.mouseEvent(MouseEvent.MOUSE_CLICKED, codeArea);
//			Event.fireEvent(codeArea, me);
//			ButtonFactory.rows.requestFocus(); 
//			codeArea.requestFocus();
//		System.out.println(codeArea.focusedProperty().get());
//		
//		codeArea.requestFocus();
//		System.out.println(codeArea.focusedProperty().get());
//	    	codeArea.deselect();
//	    	codeArea.setFocusTraversable(true);
////	    	codeArea.getShowCaret()
//	    	ComponentGetter.codeAreaAnchor =  codeArea.getAnchor();
	    	
		});
		
	}
	//TODO 获取查询的结果, 返回字段名称的数据和 值的数据
		public static void callProcedure(Connection conn   ) throws SQLException {
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
			    

	            Object objRtn =   call.getObject(2 );
	            System.out.println(objRtn);
//			    call.registerOutParameter(0, null);
//			    call.
			    
			     
	 
			} catch (SQLException e) {
				throw e;
			} finally {
				if (rs != null)
					rs.close();
			} 
		}
		
		
		
		public static List<String> callProcedure(Connection conn , String proName, List<ProcedureFieldPo> pfp ) throws SQLException {
			// DB对象
			CallableStatement call = null;
			ResultSet rs = null;
			List<String> val = new ArrayList<>();
			try {
				if(pfp.size() > 0) {
					String callsql = "{call " + proName+ "(";
					for(int i = 0 ; i < pfp.size(); i++) {
						callsql += "? ,";
					}
					
					callsql = callsql.substring(0, callsql.lastIndexOf(","));
					callsql += " ) }"; 
					call = conn.prepareCall(callsql);
					
					for(int i = 0 ; i < pfp.size(); i++) {
						ProcedureFieldPo po = pfp.get(i);
						if (po.isIn()) {
							call.setObject( i+1, po.getValue());
						}
						if(po.isOut()) { 
							call.registerOutParameter( i+1, CommonConst.PROCEDURE_TYPE.get(po.getTypeName()));
						}
					}
					// 处理结果集
				    call.execute();  
				    for(int i = 0 ; i < pfp.size(); i++) {
				    	ProcedureFieldPo po = pfp.get(i);
				    	if(po.isOut()) { 
				    		 Object objRtn =   call.getObject( i+1 );
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