package net.tenie.fx.Action;

import java.io.File;
import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheetUtility;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.GenerateSQLString;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.component.MyAreaTab;
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
				ConnectionEditor.ConnectionInfoSetting();
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
				MyAreaTab.addCodeEmptyTabMethod();
			}
		};
	}

	// hide left
	public static EventHandler<Event> hideLift() {
		return new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				CommonAction.hideLeft();
			}
		};
	}

	//
	public static EventHandler<Event> hideBottom() {
		return new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				SdkComponent.hideBottom();
			}
		};
	}

	// save file
	public static EventHandler<Event> saveSQl() {
		return new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				CommonAction.saveSqlAction();
			}
		};
	}

	// main page close event
	public static EventHandler<WindowEvent> mainCloseEvent() {
		return new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				CommonAction.mainPageClose();
			}
		};
	}

	// ...
	public static EventHandler<Event> demo() {
		return new EventHandler<Event>() {
			@Override
			public void handle(Event e) {

			}
		};
	}

//	public static EventHandler<ActionEvent> InsertSQLClipboard(boolean isSelected, boolean isFile) {
//		return new EventHandler<ActionEvent>() {
//			public void handle(ActionEvent e) {
//				File tmpFile = null;
//				if (isFile) {
//					tmpFile = CommonUtility.getFilePathHelper("sql");
//				}
//				final File ff = tmpFile;
//				LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
//					Thread t = new Thread() {
//						public void run() {
//							String tableName = SqluckyBottomSheetUtility.getTableName();
//							final ObservableList<ResultSetRowPo> fvals = SqluckyBottomSheetUtility
//									.getValsHelper(isSelected);
//
//							String sql = GenerateSQLString.insertSQLHelper(fvals, tableName);
//							if (StrUtils.isNotNullOrEmpty(sql)) {
//								if (isFile) {
//									if (ff != null) {
//										try {
//											FileTools.save(ff, sql);
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//									}
//								} else {
//									CommonUtility.setClipboardVal(sql);
//								}
//
//							}
//						}
//					};
//					t.start();
//				});
//
//			}
//
//		};
//	}

//	public static EventHandler<ActionEvent> csvStrClipboard(boolean isSelected, boolean isFile) {
//		return new EventHandler<ActionEvent>() {
//			public void handle(ActionEvent e) {
//
//				File tmpFile = null;
//				if (isFile) {
//					tmpFile = CommonUtility.getFilePathHelper("csv");
//				}
//				final File ff = tmpFile;
//
//				LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
//					Thread t = new Thread() {
//						public void run() {
//							ObservableList<ResultSetRowPo> vals = SqluckyBottomSheetUtility.getValsHelper(isSelected);
//							String sql = GenerateSQLString.csvStrHelper(vals);
//							if (StrUtils.isNotNullOrEmpty(sql)) {
//								if (isFile) {
//									if (ff != null) {
//										try {
//											FileTools.save(ff, sql);
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//									}
//								} else {
//									CommonUtility.setClipboardVal(sql);
//								}
//							}
//						}
//					};
//					t.start();
//				});
//
//			}
//
//		};
//	}

	/**
	 * 将table中的数据以普通文本方式导出
	 * 
	 * @param isSelected
	 * @param isFile
	 * @return
	 */
//	public static EventHandler<ActionEvent> txtStrClipboard(boolean isSelected, boolean isFile) {
//		return new EventHandler<ActionEvent>() {
//			public void handle(ActionEvent e) {
//				ObservableList<ResultSetRowPo> vals = SqluckyBottomSheetUtility.getValsHelper(isSelected);
//				final File ff = CommonUtility.getFileHelper(isFile);
//				Thread t = new Thread() {
//					public void run() {
//						String sql = GenerateSQLString.txtStrHelper(vals);
//						if (StrUtils.isNotNullOrEmpty(sql)) {
//							if (isFile) {
//								if (ff != null) {
//									try {
//										FileTools.save(ff, sql);
//									} catch (IOException e) {
//										e.printStackTrace();
//									}
//								}
//							} else {
//								CommonUtility.setClipboardVal(sql);
//							}
//						}
//					}
//				};
//				t.start();
//
//			}
//
//		};
//	}

	public static EventHandler<ActionEvent> columnDataClipboard(boolean isSelected, boolean isFile, String colName,
			SqluckyBottomSheet mtd) {
		return new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				ObservableList<ResultSetRowPo> vals = SqluckyBottomSheetUtility.getValsHelper(isSelected, mtd);
				final File ff = CommonUtility.getFileHelper(isFile);
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
								CommonUtility.setClipboardVal(sql);
							}
						}
					}
				};
				t.start();

			}

		};
	}

//	// 导出表的字段, 使用逗号分割
//	public static EventHandler<ActionEvent> commaSplitTableFields() {
//		return new EventHandler<ActionEvent>() {
//			public void handle(ActionEvent e) {
//				LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
//					ObservableList<SheetFieldPo> fs = SqluckyBottomSheetUtility.getFields();
//					Thread t = new Thread() {
//						public void run() {
//							int size = fs.size();
//							StringBuilder fieldsName = new StringBuilder("");
//							for (int i = 0; i < size; i++) {
//								SheetFieldPo po = fs.get(i);
//								String name = po.getColumnName().get();
//								fieldsName.append(name);
//								fieldsName.append(", \n");
//
//							}
//							if (StrUtils.isNotNullOrEmpty(fieldsName.toString())) {
//								String rsStr = fieldsName.toString().trim();
//								CommonUtility.setClipboardVal(fieldsName.substring(0, rsStr.length() - 1));
//							}
//						}
//					};
//					t.start();
//
//				});
//
//			}
//
//		};
//	}

	// 导出表的字段包含类型, 使用逗号分割
//	public static EventHandler<ActionEvent> commaSplitTableFiledsIncludeType() {
//		return new EventHandler<ActionEvent>() {
//			public void handle(ActionEvent e) {
//				LoadingAnimation.primarySceneRootLoadingAnimation("Exporting ...", v -> {
//					ObservableList<SheetFieldPo> fs = SqluckyBottomSheetUtility.getFields();
//					Thread t = new Thread() {
//						public void run() {
//							int size = fs.size();
//							StringBuilder fieldsName = new StringBuilder("");
//							for (int i = 0; i < size; i++) {
//								SheetFieldPo po = fs.get(i);
//								String name = po.getColumnName().get();
//								fieldsName.append(name);
//								fieldsName.append(", --");
//								fieldsName.append(po.getColumnTypeName().get());
//								fieldsName.append("\n");
//
//							}
//							if (StrUtils.isNotNullOrEmpty(fieldsName.toString())) {
//								CommonUtility.setClipboardVal(fieldsName.toString());
//							}
//						}
//					};
//					t.start();
//				});
//			}
//
//		};
//	}

}
