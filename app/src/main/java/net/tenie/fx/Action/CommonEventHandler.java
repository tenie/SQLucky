package net.tenie.fx.Action;

import java.io.File;
import java.io.IOException;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.stage.WindowEvent;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Cache.CacheTabView;
import net.tenie.fx.component.CommonFileChooser;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.component.container.DataViewTab;
import net.tenie.fx.dao.GenerateSQLString;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.fx.utility.SaveFile;
import net.tenie.fx.window.ConnectionEditor;


/*   @author tenie */
public class CommonEventHandler {
	
	/**
	 * 数据table关闭的时候 
	 */
	public static EventHandler<Event> dataTabCloseReq( Tab tb) {
		return new EventHandler<Event>() {
			public void handle(Event e) { 
				CommonAction.clearDataTable( tb);
				 
			}
		};
	}

	// 添加按钮点击事件
	public static EventHandler<Event> addConnEvent() {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				ConnectionEditor.ConnectionInfoSetting();
			}
		};
	}

	// 打开连接按钮点击事件
//	public static EventHandler openConnEvent() {
//		return new EventHandler() {
//			public void handle(Event e) {
//				ConnectionEditor.openDbConn();
//			}
//		};
//	}

	// 断开连接按钮点击事件
//	public static EventHandler closeConnEvent() {
//		return new EventHandler() {
//			public void handle(Event e) {
//				ConnectionEditor.closeDbConn();
//			}
//		};
//	}

	// 断开所有连接按钮点击事件
	public static EventHandler<Event> closeAllConnEvent() {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				ConnectionEditor.closeAllDbConn();
			}
		};
	}

	// 删除连接按钮点击事件
	public static EventHandler<Event> deleteConnEvent() {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				ConnectionEditor.deleteDbConn();
			}
		};
	}

	// 编辑连接节点信息
//	public static EventHandler editConnEvent() {
//		return new EventHandler() {
//			public void handle(Event e) {
//				ConnectionEditor.closeDbConn();
//				ConnectionEditor.editDbConn();
//			}
//		};
//	}

	// 添加code area 面板
	public static EventHandler<Event> addCodeTab() {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				SqlEditor.addCodeEmptyTabMethod();
			}
		};
	}

	// hide left
	public static EventHandler<Event> hideLift() {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				CommonAction.hideLeft();
			}
		};
	}

	//
	public static EventHandler<Event> hideBottom() {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				CommonAction.hideBottom();
			}
		};
	}

	// save file
	public static EventHandler<Event> saveSQl() {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				CommonAction.saveSqlAction();
			}
		};
	}

	// main page close event
	public static EventHandler<WindowEvent> mainCloseEvent() {
		return new EventHandler<WindowEvent>() {
			public void handle(WindowEvent e) {
				CommonAction.mainPageClose();
			}
		};
	}

	// ...
	public static EventHandler<Event> demo() {
		return new EventHandler<Event>() {
			public void handle(Event e) {

			}
		};
	}
 

	private static ObservableList<ObservableList<StringProperty>> getValsHelper(boolean isSelected, String tableid) {
		ObservableList<ObservableList<StringProperty>> vals = null;
		if (isSelected) {
			vals = DataViewTab.dataTableViewSelectedItems();
		} else {
			vals =  CacheTabView.getTabData(tableid);
		}
		return vals;
	}

	private static File getFileHelper(boolean isFile) {
		File file = null;
		if (isFile) {
			file = CommonFileChooser.showSaveDefault("Save", ComponentGetter.primaryStage);
		}
		return file;
	}

	public static EventHandler<ActionEvent> InsertSQLClipboard(boolean isSelected, boolean isFile) {
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				String tableid = DataViewTab.dataTableViewID();
				ObservableList<SqlFieldPo> fs = CacheTabView.getFields(tableid);//CacheTableDate.getCols(tableid);
				String tableName = CacheTabView.getTableName(tableid);                 //CacheTableDate.getTableName(tableid);
				final ObservableList<ObservableList<StringProperty>> fvals = getValsHelper(isSelected, tableid);

				final File ff = getFileHelper(isFile);

				Thread t = new Thread() {
					public void run() {
						String sql = GenerateSQLString.insertSQLHelper(fvals, tableName, fs);
						if (StrUtils.isNotNullOrEmpty(sql)) {
							if (isFile) {
								if (ff != null) {
									try {
										SaveFile.save(ff, sql);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							} else {
								CommonUtility.setClipboardVal(sql);
							}

						}
					}
				};
				t.start();

			}

		};
	}

	public static EventHandler<ActionEvent> csvStrClipboard(boolean isSelected, boolean isFile) {
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				String tableid = DataViewTab.dataTableViewID();
				ObservableList<SqlFieldPo> fs = CacheTabView.getFields(tableid); //CacheTableDate.getCols(tableid);
				
				ObservableList<ObservableList<StringProperty>> vals = getValsHelper(isSelected, tableid);
				final File ff = getFileHelper(isFile);
				Thread t = new Thread() {
					public void run() {
						String sql = GenerateSQLString.csvStrHelper(vals, fs);
						if (StrUtils.isNotNullOrEmpty(sql)) {
							if (isFile) {
								if (ff != null) {
									try {
										SaveFile.save(ff, sql);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							} else {
								CommonUtility.setClipboardVal(sql);
							}
						}
					}
				};
				t.start();

			}

		};
	}

	public static EventHandler<ActionEvent> txtStrClipboard(boolean isSelected, boolean isFile) {
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				String tableid = DataViewTab.dataTableViewID();
				ObservableList<SqlFieldPo> fs = CacheTabView.getFields(tableid); //CacheTableDate.getCols(tableid); 

				ObservableList<ObservableList<StringProperty>> vals = getValsHelper(isSelected, tableid);
				final File ff = getFileHelper(isFile);
				Thread t = new Thread() {
					public void run() {
						String sql = GenerateSQLString.txtStrHelper(vals, fs);
						if (StrUtils.isNotNullOrEmpty(sql)) {
							if (isFile) {
								if (ff != null) {
									try {
										SaveFile.save(ff, sql);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							} else {
								CommonUtility.setClipboardVal(sql);
							}
						}
					}
				};
				t.start();

			}

		};
	}
	
	public static EventHandler<ActionEvent> columnDataClipboard(boolean isSelected, boolean isFile, String colName) {
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				// 通过id 从缓存中获取数据
				String tableid = DataViewTab.dataTableViewID();
				ObservableList<SqlFieldPo> fs = CacheTabView.getFields(tableid); //CacheTableDate.getCols(tableid);

				ObservableList<ObservableList<StringProperty>> vals = getValsHelper(isSelected, tableid);
				final File ff = getFileHelper(isFile);
				Thread t = new Thread() {
					public void run() {
						String sql = GenerateSQLString.columnStrHelper(vals, fs, colName);
						if (StrUtils.isNotNullOrEmpty(sql)) {
							if (isFile) {
								if (ff != null) {
									try {
										SaveFile.save(ff, sql);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							} else {
								CommonUtility.setClipboardVal(sql);
							}
						}
					}
				};
				t.start();

			}

		};
	}

}
