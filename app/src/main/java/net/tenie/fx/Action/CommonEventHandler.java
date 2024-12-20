package net.tenie.fx.Action;

import java.io.File;
import java.io.IOException;

import SQLucky.app;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.stage.WindowEvent;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.sheet.bottom.MyBottomSheet;
import net.tenie.Sqlucky.sdk.component.sheet.bottom.MyBottomSheetAction;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.utility.AppCommonAction;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.GenerateSQLString;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.window.ConnectionEditor;

/**
 * 
 * @author tenie
 *
 */
public class CommonEventHandler {

	// 添加按钮点击事件
	public static EventHandler<Event> addConnEvent() {
		return new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
//				ConnectionEditor.ConnectionInfoSetting();
				new ConnectionEditor();
			}
		};
	}

	// 断开所有连接按钮点击事件
	public static EventHandler<Event> closeAllConnEvent() {
		return new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				ConnectionEditor.closeAllDbConn();
			}
		};
	}

	// 删除连接按钮点击事件
	public static EventHandler<Event> deleteConnEvent() {
		return new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				ConnectionEditor.deleteDbConn();
			}
		};
	}

	// 添加code area 面板
	public static EventHandler<Event> addCodeTab() {
		return new EventHandler<Event>() {
			@Override
			public void handle(Event e) {

				if(!ComponentGetter.rightTabPaneMasterDetailPane.isShowDetailNode()){
					MyEditorSheetHelper.addEmptyHighLightingEditor(ComponentGetter.mainTabPane);
				}else{
					// 按住control, 再按添加窗口按钮, 可以在rightTabPane中添加
					var kc = KeyCode.CONTROL;
					if(SettingKeyBinding.keyCode != null && SettingKeyBinding.keyCode.equals(kc)){
						MyEditorSheetHelper.addEmptyHighLightingEditor(ComponentGetter.rightTabPane);
					}else {
//						MyEditorSheetHelper.addEmptyHighLightingEditor();
						MyEditorSheetHelper.addEmptyHighLightingEditor(ComponentGetter.mainTabPane);
					}
				}


			}
		};
	}






	// save file
	public static EventHandler<Event> saveSQl() {
		return new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				MyEditorSheetHelper.saveSqlToFileAction();
			}
		};
	}

	// main page close event
	public static EventHandler<WindowEvent> mainCloseEvent() {
		return new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				// 主窗口关闭事件处理逻辑
				app.saveApplicationStatusInfo();
			}
		};
	}



	public static EventHandler<ActionEvent> columnDataClipboard(boolean isSelected, boolean isFile, String colName,
			MyBottomSheet mtd) {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				ObservableList<ResultSetRowPo> vals = MyBottomSheetAction.getValsHelper(mtd,isSelected);
				final File ff = CommonUtils.getFileHelper(isFile);
				Thread t = new Thread() {
					@Override
					public void run() {
						String sql = GenerateSQLString.columnStrHelper(vals, colName);
						if (StrUtils.isNotNullOrEmpty(sql)) {
							if (isFile) {
								if (ff != null) {
									try {
										FileTools.save(ff, sql);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							} else {
								CommonUtils.setClipboardVal(sql);
							}
						}
					}
				};
				t.start();
			}
		};
	}

}
