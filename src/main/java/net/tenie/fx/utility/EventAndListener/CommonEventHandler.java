package net.tenie.fx.utility.EventAndListener;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.ShowTableRowDateDetailAction;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.PropertyPo.CacheTableDate;
import net.tenie.fx.PropertyPo.DbTableDatePo;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.CommonFileChooser;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ConnectionEditor;
import net.tenie.fx.component.ModalDialog;
import net.tenie.fx.component.SqlCodeAreaHighLightingHelper;
import net.tenie.fx.component.SqlEditor;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.dao.DeleteDao;
import net.tenie.fx.dao.GenerateSQLString;
import net.tenie.fx.dao.InsertDao;
import net.tenie.fx.dao.UpdateDao;
import net.tenie.fx.utility.CommonUtility;
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
	 * 数据table关闭的时候新建一个空白表
	 */
	public static EventHandler<Event> dataTabCloseReq(TabPane tabPane, Tab tb) {
		return new EventHandler<Event>() {
			public void handle(Event e) {

				long begintime = System.currentTimeMillis();
				String idVal = tb.getId();
				if (idVal != null) {
					CacheTableDate.clear(idVal);
				}
				tb.setContent(null);
				long endtime = System.currentTimeMillis();
				long costTime = (endtime - begintime);
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
	public static EventHandler openConnEvent() {
		return new EventHandler() {
			public void handle(Event e) {
				ConnectionEditor.openDbConn();
			}
		};
	}

	// 断开连接按钮点击事件
	public static EventHandler closeConnEvent() {
		return new EventHandler() {
			public void handle(Event e) {
				ConnectionEditor.closeDbConn();
			}
		};
	}

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
	public static EventHandler editConnEvent() {
		return new EventHandler() {
			public void handle(Event e) {
				ConnectionEditor.editDbConn();
			}
		};
	}

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
//					System.out.println(s);
//					CommonAction.addString("\t");
//					CodeArea code = SqlEditor.getCodeArea();
//					code.getText()
					
					 
				} else {
//					System.out.println(s);
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

	public static EventHandler<Event> saveDate(JFXButton saveBtn) {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				String tabId = saveBtn.getParent().getId();
				String tabName = CacheTableDate.getTableName(tabId);
				ObservableList<ObservableList<StringProperty>> alldata = CacheTableDate.getData(tabId);
				Connection conn = CacheTableDate.getDBConn(tabId);
				if (tabName != null && tabName.length() > 0) {
					// 字段
					ObservableList<SqlFieldPo> fpos = CacheTableDate.getCols(tabId);
					// 待保存数据
					Map<String, ObservableList<StringProperty>> modifyData = CacheTableDate.getModifyData(tabId);
					// 执行sql 后的信息 (主要是错误后显示到界面上)
					DbTableDatePo ddlDmlpo = new DbTableDatePo();
					ddlDmlpo.addField("Info");
					ddlDmlpo.addField("Status");

					if (!modifyData.isEmpty()) {
						for (String key : modifyData.keySet()) {
							// 获取对应旧数据
							ObservableList<StringProperty> old = CacheTableDate.getold(key);
							ObservableList<StringProperty> newd = modifyData.get(key);
							// 拼接update sql
							try {
								String msg = UpdateDao.execUpdate(conn, tabName, newd, old, fpos);
								ObservableList<StringProperty> val = FXCollections.observableArrayList();
								val.add(new SimpleStringProperty(msg));
								val.add(new SimpleStringProperty("success"));
								val.add(new SimpleStringProperty(""));
								ddlDmlpo.addData(val);
							} catch (Exception e1) {
								e1.printStackTrace();
								saveBtn.setDisable(true);
								ObservableList<StringProperty> val = FXCollections.observableArrayList();
								val.add(new SimpleStringProperty(e1.getMessage()));
								val.add(new SimpleStringProperty("fail."));
								val.add(new SimpleStringProperty(""));
								ddlDmlpo.addData(val);
							}
						}
						CacheTableDate.rmUpdateData(tabId);
					}

					// 插入操作
					List<ObservableList<StringProperty>> dataList = CacheTableDate.getAppendData(tabId);
					for (ObservableList<StringProperty> os : dataList) {
						try {
							String msg = InsertDao.execInsert(conn, tabName, os, fpos);
							ObservableList<StringProperty> val = FXCollections.observableArrayList();
							val.add(new SimpleStringProperty(msg));
							val.add(new SimpleStringProperty("success"));
							val.add(new SimpleStringProperty(""));
							ddlDmlpo.addData(val);

							// 删除缓存数据
							CacheTableDate.rmAppendData(tabId);
							// 对insert 的数据保存后 , 不能再修改
							List<StringProperty> templs = new ArrayList<>();
							for (int i = 0; i < fpos.size(); i++) {
								StringProperty sp = os.get(i);
								StringProperty newsp = new SimpleStringProperty(sp.get());
								templs.add(newsp);
								CommonUtility.prohibitChangeListener(newsp, sp.get());
							}
							os.clear();
							for (int i = 0; i < templs.size(); i++) {
								StringProperty newsp = templs.get(i);
								os.add(newsp);
							}

						} catch (Exception e1) {
							e1.printStackTrace();
							saveBtn.setDisable(true);
							ObservableList<StringProperty> val = FXCollections.observableArrayList();
							val.add(new SimpleStringProperty(e1.getMessage()));
							val.add(new SimpleStringProperty("fail."));
							val.add(new SimpleStringProperty(""));
							ddlDmlpo.addData(val);
						}
					}

					// 保存按钮禁用
					FlowPane fp = (FlowPane) saveBtn.getParent();
					fp.getChildren().get(0).setDisable(true);
					RunSQLHelper.showExecuteSQLInfo(ddlDmlpo);

				}

			}
		};
	}

	// Show line data
	public static EventHandler<Event> showLineDetail(JFXButton saveBtn) {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				ShowTableRowDateDetailAction.show(saveBtn);
			}
		};
	}

	public static EventHandler<Event> refreshData(JFXButton btn) {
		return new EventHandler<Event>() {
			public void handle(Event e) {

				String id = btn.getParent().getId();
				Tab tb = CacheTableDate.getTab(id);
				String sql = CacheTableDate.getSelectSQl(id);
				Connection conn = CacheTableDate.getDBConn(id);
				if (conn != null) {
					// 关闭当前tab
					CommonUtility.setTabName(tb, "");
					String idx = "" + ComponentGetter.dataTab.getSelectionModel().getSelectedIndex();
					JFXButton runFunPro = AllButtons.btns.get("runFunPro");
					RunSQLHelper.runSQLMethod(conn, sql, idx, runFunPro);
				}
			}
		};
	}

	// 添加一行数据
	public static EventHandler<Event> addData(JFXButton btn) {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				VBox vbox = (VBox) btn.getParent().getParent();
				FilteredTableView<ObservableList<StringProperty>> tbv = (FilteredTableView<ObservableList<StringProperty>>) vbox
						.getChildren().get(1);
				tbv.scrollTo(0);
				String tabid = btn.getParent().getId();
				int newLineidx = ConfigVal.newLineIdx++;
				ObservableList<SqlFieldPo> fs = CacheTableDate.getCols(tabid);
				ObservableList<StringProperty> item = FXCollections.observableArrayList();
				for (int i = 0; i < fs.size(); i++) {
					SimpleStringProperty sp = new SimpleStringProperty();
					// 添加监听. 保存时使用 newLineIdx
					CommonUtility.newStringPropertyChangeListener(sp, fs.get(i).getColumnType().get());
					item.add(sp);
				}
				item.add(new SimpleStringProperty(newLineidx + "")); // 行号， 没什么用
				CacheTableDate.appendDate(tabid, newLineidx, item); // 可以防止在map中被覆盖
				tbv.getItems().add(0, item);

				// 发生亮起保存按钮
				FlowPane fp = ComponentGetter.dataFlowPane(tbv);
				fp.getChildren().get(0).setDisable(false);
			}

		};
	}

	public static EventHandler<Event> deleteData(JFXButton btn) {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				// 获取当前的table view
				FilteredTableView<ObservableList<StringProperty>> table = ComponentGetter.dataTableView();
				String tabId = table.getId();

				String tabName = CacheTableDate.getTableName(tabId);
				Connection conn = CacheTableDate.getDBConn(tabId);
				ObservableList<SqlFieldPo> fpos = CacheTableDate.getCols(tabId);

				ObservableList<ObservableList<StringProperty>> vals = table.getSelectionModel().getSelectedItems();
				List<String> temp = new ArrayList<>();

				// 执行sql 后的信息 (主要是错误后显示到界面上)
				DbTableDatePo ddlDmlpo = new DbTableDatePo();
				ddlDmlpo.addField("Info");
				ddlDmlpo.addField("Status");

				try {
					for (int i = 0; i < vals.size(); i++) {
						ObservableList<StringProperty> sps = vals.get(i);
						String ro = sps.get(sps.size() - 1).get();
						temp.add(ro);
						String msg = DeleteDao.execDelete(conn, tabName, sps, fpos);
						ObservableList<StringProperty> val = FXCollections.observableArrayList();
						val.add(new SimpleStringProperty(msg));
						val.add(new SimpleStringProperty("success"));
						val.add(new SimpleStringProperty(""));
						ddlDmlpo.addData(val);

					}
					for (String str : temp) {
						CacheTableDate.deleteTabDataRowNo(tabId, str);
					}

				} catch (Exception e1) {
					ObservableList<StringProperty> val = FXCollections.observableArrayList();
					val.add(new SimpleStringProperty(e1.getMessage()));
					val.add(new SimpleStringProperty("fail."));
					val.add(new SimpleStringProperty(""));
					ddlDmlpo.addData(val);
				} finally {
					RunSQLHelper.showExecuteSQLInfo(ddlDmlpo);
				}
			}

		};
	}

	// 复制选择的 行数据 插入到表格末尾
	public static EventHandler<Event> copyData(JFXButton btn) {
		return new EventHandler<Event>() {
			public void handle(Event e) {
				// 获取当前的table view
				FilteredTableView<ObservableList<StringProperty>> table = ComponentGetter.dataTableView();

				String tabId = table.getId();

//				String tabName = CacheTableDate.getTableName(tabId);
//				Connection conn = CacheTableDate.getDBConn(tabId);
				ObservableList<SqlFieldPo> fs = CacheTableDate.getCols(tabId);

				ObservableList<ObservableList<StringProperty>> vals = ComponentGetter.dataTableViewSelectedItems();// table.getSelectionModel().getSelectedItems();
//				int seIdx = table.getSelectionModel().getSelectedIndex();
//				List<String> temp = new ArrayList<>();

				try {

					for (int i = 0; i < vals.size(); i++) {
						// 选中的行
						ObservableList<StringProperty> sps = vals.get(i);
						// copy 一行
						ObservableList<StringProperty> item = FXCollections.observableArrayList();
						int newLineidx = ConfigVal.newLineIdx++;
//						for (StringProperty strp : sps) {
						for (int j = 0 ; j < sps.size(); j++) {
							StringProperty strp = sps.get(j);
						 
							StringProperty newsp = new SimpleStringProperty(strp.get());
							CommonUtility.newStringPropertyChangeListener(newsp, fs.get(i).getColumnType().get());
							item.add(newsp);
						}
						item.add(new SimpleStringProperty(newLineidx + "")); // 行号， 没什么用
						CacheTableDate.appendDate(tabId, newLineidx, item); // 可以防止在map中被覆盖
						table.getItems().add(item);

					}
					table.scrollTo(table.getItems().size() - 1);

					// 保存按钮亮起
					ComponentGetter.dataFlowSaveBtn().setDisable(false);
				} catch (Exception e2) {
					ModalDialog.errorAlert("Error", e2.getMessage());
				}
			}

		};
	}

	private static ObservableList<ObservableList<StringProperty>> getValsHelper(boolean isSelected, String tableid) {
		ObservableList<ObservableList<StringProperty>> vals = null;
		if (isSelected) {
			vals = ComponentGetter.dataTableViewSelectedItems();
		} else {
			vals = CacheTableDate.getData(tableid);
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
				ObservableList<SqlFieldPo> fs = CacheTableDate.getCols(tableid);
				String tableName = CacheTableDate.getTableName(tableid);
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
				ObservableList<SqlFieldPo> fs = CacheTableDate.getCols(tableid);
				String tableName = CacheTableDate.getTableName(tableid);
				ObservableList<ObservableList<StringProperty>> vals = getValsHelper(isSelected, tableid);
				final File ff = getFileHelper(isFile);
				Thread t = new Thread() {
					public void run() {
						String sql = GenerateSQLString.csvStrHelper(vals, tableName, fs);
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
				ObservableList<SqlFieldPo> fs = CacheTableDate.getCols(tableid);
				String tableName = CacheTableDate.getTableName(tableid);

				ObservableList<ObservableList<StringProperty>> vals = getValsHelper(isSelected, tableid);
				final File ff = getFileHelper(isFile);
				Thread t = new Thread() {
					public void run() {
						String sql = GenerateSQLString.txtStrHelper(vals, tableName, fs);
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
