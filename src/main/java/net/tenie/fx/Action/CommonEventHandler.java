package net.tenie.fx.Action;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.fxmisc.richtext.CodeArea;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.PropertyPo.CacheTabView;
//import net.tenie.fx.PropertyPo.CacheTableDate;
import net.tenie.fx.PropertyPo.DbTableDatePo;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.CommonFileChooser;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.SqlCodeAreaHighLightingHelper;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.dao.DeleteDao;
import net.tenie.fx.dao.GenerateSQLString;
import net.tenie.fx.dao.InsertDao;
import net.tenie.fx.dao.UpdateDao;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.fx.window.ModalDialog;
import net.tenie.fx.window.MyAlert;
import net.tenie.fx.window.TableRowDataDetail;
import net.tenie.lib.io.SaveFile;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class CommonEventHandler {
	/**
	 * tab 关闭时：阻止关闭最后一个
	 */
	public static EventHandler<Event> tabCloseReq(TabPane myTabPane) {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				if (myTabPane.getTabs().size() == 1) { // 如果只有一个窗口就不能关闭
					e.consume();
				}
			}
		};
	}

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

	//TODO 代码输入时, 修改tab 的名称加上* ,意味未保存
	public static EventHandler<KeyEvent> codeAreaChange(CodeArea code) {
		return new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {  
				String s = e.getCode().getName();
				KeyCode kc =  e.getCode();
				if(	KeyCode.TAB == kc) {
					e.consume();					 
				} else {
//					logger.info(s);
					Tab tb = SqlEditor.mainTabPaneSelectedTab();
					if (tb != null) {
						String title = CommonUtility.tabText(tb);  
						if (!title.endsWith("*")) { 
							CommonUtility.setTabName(tb, title + "*");
						}
						SqlCodeAreaHighLightingHelper.applyHighlighting(code);
					}
				}

			}
		};
	}
 

	
 


 

	private static ObservableList<ObservableList<StringProperty>> getValsHelper(boolean isSelected, String tableid) {
		ObservableList<ObservableList<StringProperty>> vals = null;
		if (isSelected) {
			vals = ComponentGetter.dataTableViewSelectedItems();
		} else {
			vals =  CacheTabView.getTabData(tableid);//CacheTableDate.getData(tableid);
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
				String tableid = ComponentGetter.dataTableViewID();
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
				String tableid = ComponentGetter.dataTableViewID();
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
				String tableid = ComponentGetter.dataTableViewID();
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
				String tableid = ComponentGetter.dataTableViewID();
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
