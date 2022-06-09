package net.tenie.fx.Action;

import java.io.File;
import java.io.IOException;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheetUtility;
import net.tenie.Sqlucky.sdk.po.SqlFieldPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.GenerateSQLString;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.component.MyTab;
import net.tenie.fx.component.dataView.MyTabData;
import net.tenie.fx.window.ConnectionEditor;

/**
 * 
 * @author tenie
 *
 */
public class CommonEventHandler {
	
	/**
	 * 数据table关闭的时候 
	 */
	public static EventHandler<Event> dataTabCloseReq( MyTabData tb) {
		return new EventHandler<Event>() {
			public void handle(Event e) { 
				tb.getTableData().clean();
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
				MyTab.addCodeEmptyTabMethod();
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
 

	

	

	public static EventHandler<ActionEvent> InsertSQLClipboard(boolean isSelected, boolean isFile) {
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				ObservableList<SqlFieldPo> fs = SqluckyBottomSheetUtility.getFields();
				String tableName = SqluckyBottomSheetUtility.getTableName();
				final ObservableList<ObservableList<StringProperty>> fvals = SqluckyBottomSheetUtility.getValsHelper(isSelected);

				final File ff = CommonUtility.getFileHelper(isFile);

				Thread t = new Thread() {
					public void run() {
						String sql = GenerateSQLString.insertSQLHelper(fvals, tableName, fs);
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
				ObservableList<SqlFieldPo> fs = SqluckyBottomSheetUtility.getFields();				
				ObservableList<ObservableList<StringProperty>> vals = SqluckyBottomSheetUtility.getValsHelper(isSelected);
				final File ff = CommonUtility.getFileHelper(isFile);
				Thread t = new Thread() {
					public void run() {
						String sql = GenerateSQLString.csvStrHelper(vals, fs);
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
				ObservableList<SqlFieldPo> fs = SqluckyBottomSheetUtility.getFields(); 
				ObservableList<ObservableList<StringProperty>> vals = SqluckyBottomSheetUtility.getValsHelper(isSelected);
				final File ff = CommonUtility.getFileHelper(isFile);
				Thread t = new Thread() {
					public void run() {
						String sql = GenerateSQLString.txtStrHelper(vals, fs);
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
				ObservableList<SqlFieldPo> fs = SqluckyBottomSheetUtility.getFields(); 
				ObservableList<ObservableList<StringProperty>> vals = SqluckyBottomSheetUtility.getValsHelper(isSelected);
				final File ff = CommonUtility.getFileHelper(isFile);
				Thread t = new Thread() {
					public void run() {
						String sql = GenerateSQLString.columnStrHelper(vals, fs, colName);
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
								CommonUtility.setClipboardVal(sql);
							}
						}
					}
				};
				t.start();

			}

		};
	}

	
	
	// 导出表的字段, 使用逗号分割
	public static EventHandler<ActionEvent> commaSplitTableFields() {
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) { 
				ObservableList<SqlFieldPo> fs = SqluckyBottomSheetUtility.getFields();
				Thread t = new Thread() {
					public void run() {
						int size = fs.size();
						StringBuilder  fieldsName = new StringBuilder("");
						for (int i = 0; i < size; i++) {
							SqlFieldPo po = fs.get(i);
							String name = po.getColumnName().get();
							fieldsName.append( name );
							fieldsName.append( ", \n" );
							 
						}
						if (StrUtils.isNotNullOrEmpty(fieldsName)) { 
								String rsStr = fieldsName.toString().trim();
								CommonUtility.setClipboardVal(fieldsName.substring(0, rsStr.length()-1)); 
						}
					}
				};
				t.start();

			}

		};
	}
	
	// 导出表的字段包含类型, 使用逗号分割
	public static EventHandler<ActionEvent> commaSplitTableFiledsIncludeType() {
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				ObservableList<SqlFieldPo> fs = SqluckyBottomSheetUtility.getFields();
				Thread t = new Thread() {
					public void run() {
						int size = fs.size();
						StringBuilder fieldsName = new StringBuilder("");
						for (int i = 0; i < size; i++) {
							SqlFieldPo po = fs.get(i);
							String name = po.getColumnName().get();
							fieldsName.append(name);
							fieldsName.append(", --");
							fieldsName.append(po.getColumnTypeName().get());
							fieldsName.append("\n");

						}
						if (StrUtils.isNotNullOrEmpty(fieldsName)) { 
							CommonUtility.setClipboardVal(fieldsName.toString());
						}
					}
				};
				t.start();

			}

		};
	}
	
}
