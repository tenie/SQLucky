package net.tenie.fx.Action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import net.tenie.Sqlucky.sdk.component.CommonButtons;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DbTableDatePo;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;
import net.tenie.Sqlucky.sdk.po.db.ProcedureFieldPo;
import net.tenie.Sqlucky.sdk.po.db.SqlData;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.AppCommonAction;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.DateUtils;
import net.tenie.Sqlucky.sdk.utility.ParseSQL;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.TableViewUtils;
import net.tenie.fx.Action.sqlExecute.ProcedureAction;
import net.tenie.fx.Action.sqlExecute.RunSqlStatePo;
import net.tenie.fx.Action.sqlExecute.SelectAction;
import net.tenie.fx.dao.DmlDdlDao;

/**
 * 
 * @author tenie
 *
 */
public class RunSQLHelper {

	// 执行状态, 失败赋值为0， 成功为1
	public static Map<Long, Integer> RUN_STATUS = new HashMap<>();

	private static Logger logger = LogManager.getLogger(RunSQLHelper.class);
	private static Thread thread;
	private static JFXButton runbtn;
	private static JFXButton runLinebtn;
	private static JFXButton stopbtn;
	private static JFXButton otherbtn;
	
	public  static boolean isRunning = false;

	ExecutorService service = Executors.newFixedThreadPool(1);
	private static SqluckyConnector tmpSqlConn;

	// 获取执行状态，成功或失败
	public static Integer runStatus(Long key) {
		Integer val = RUN_STATUS.get(key);
		if (val != null) {
			if (val == 1 || val == 0) {
				RUN_STATUS.remove(key);
			}
		}
		return val;
	}

	@SuppressWarnings("restriction")
	private static void runMain(RunSqlStatePo state) {
		if (isRunning) {
			MyAlert.errorAlert("有查询在进行中, 请稍等!");
			return;
		}
		try {
			isRunning = true;
			settingBtn();
			if (!state.getSqlConn().isAlive()) {
				MyAlert.errorAlert("连接中断, 请重新连接!");
				return;
			}
			// 等待加载动画
			SdkComponent.addWaitingPane(state.getTidx(), state.getIsRefresh());
			List<SqlData> allsqls = new ArrayList<>();

			// 获取sql 语句
			String sqlstr = state.getSqlStr();
			// 执行创建存储过程函数, 触发器等
			if (state.getIsCreateFunc()) {
				if (StrUtils.isNotNullOrEmpty(sqlstr)) {
					SqlData sq = new SqlData(sqlstr, 0, sqlstr.length());
					allsqls.add(sq);
				} else {
					String str = MyEditorSheetHelper.getCurrentCodeAreaSQLText();
					SqlData sq = new SqlData(str, 0, str.length());
					allsqls.add(sq);
				}
				// 执行传入的sql, 非界面上的sql
			} else if (StrUtils.isNotNullOrEmpty(sqlstr)) { // 执行指定sql
				allsqls = SqluckyAppDB.epurateSql(sqlstr);
			} else {
				// 获取将要执行的sql 语句 , 如果有选中就获取选中的sql
				allsqls = SqluckyAppDB.willExecSql(state.getIsCurrentLine());
			}
			String waitShowSqlStr  = "";
			for(int k = 0; k < allsqls.size(); k++) {
//				var sqltmp = allsqls.get(k);
//				sqltmp.sql()
				waitShowSqlStr +=	allsqls.get(k).sql() + "\n";
			}
		
			// 执行sql
			var rsVal = execSqlList(allsqls, state.getSqlConn(), state);

			// 执行sql的状态保存
			if (state.getStatusKey() != null) {
				RUN_STATUS.put(state.getStatusKey(), rsVal);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isRunning) {
				settingBtn();
				state.setCallProcedureFields(null);
				state.setIsCallFunc(false);
				SdkComponent.rmWaitingPane();
			}

			isRunning = false;
		}

	}

	// 执行查询sql 并拼装成一个表, 多个sql生成多个表
	private static Integer execSqlList(List<SqlData> allsqls, SqluckyConnector sqluckyConn, RunSqlStatePo state)
			throws SQLException {
		Integer rsVal = 1;
		String sqlstr;
		String sql;
		Connection conn = sqluckyConn.getConn();
	
		int sqllenght = allsqls.size();
		DbTableDatePo ddlDmlpo = DbTableDatePo.setExecuteInfoPo();
		List<SqlData> errObj = new ArrayList<>();

		for (int i = 0; i < sqllenght; i++) {
			sqlstr = allsqls.get(i).sql();
			sql = StrUtils.trimComment(sqlstr, "--");
			int type = ParseSQL.parseType(sql);
			String msg = "";
			
			SdkComponent.setWaitTabLabelText(sql);
			try {
				if (state.getIsCallFunc()) { // 调用存储过程
					ProcedureAction.procedureAction(sql, sqluckyConn, state.getCallProcedureFields(), state.getTidx(),
							state.getIsLock(), thread, state.getIsRefresh());
				} else if (type == ParseSQL.SELECT) { // 调用查询
					SelectAction.selectAction(sql, sqluckyConn, state.getTidx(), state.getIsLock(), thread,
							state.getIsRefresh());
				} else {
					if (type == ParseSQL.UPDATE) {
						msg = DmlDdlDao.updateSql2(conn, sql);
						logger.info("add update sql: " + sql);
					} else if (type == ParseSQL.INSERT) {
						msg = DmlDdlDao.insertSql2(conn, sql);
						logger.info("add insert sql: " + sql);
					} else if (type == ParseSQL.DELETE) {
						msg = DmlDdlDao.deleteSql2(conn, sql);
						logger.info("add DELETE sql: " + sql);
					} else if (type == ParseSQL.DROP) {
						msg = DmlDdlDao.dropSql2(conn, sql);
						logger.info("add DROP sql: " + sql);
					} else if (type == ParseSQL.ALTER) {
						msg = DmlDdlDao.alterSql2(conn, sql);
						logger.info("add ALTER sql: " + sql);
					} else if (type == ParseSQL.CREATE) {
						msg = DmlDdlDao.createSql2(conn, sql);
						logger.info("add CREATE sql: " + sql);
					} else {
						msg = DmlDdlDao.otherSql2(conn, sql);
						logger.info("add OTEHR sql: " + sql);
					}

				}
			} catch (Exception e) {
				msg = "failed : " + e.getMessage();
				msg += "\n" + sqluckyConn.translateErrMsg(msg);
				SqlData sd = allsqls.get(i);
				errObj.add(sd);
				rsVal = 0;
			}
			if (StrUtils.isNotNullOrEmpty(msg)) {
				// 如果只有一行ddl执行
				if (sqllenght == -1 && !msg.startsWith("failed")) {
					final String msgVal = msg;
					Platform.runLater(() -> {
						MyAlert.showNotifiaction(msgVal);
//						TableViewUtils.rmWaitingPane(true);
					});
				} else {
					// 显示字段是只读的
					ObservableList<SheetFieldPo> fls = ddlDmlpo.getFields();
					var row = ddlDmlpo.addRow();
					ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.dateToStrL(new Date())),
							fls.get(0));
					ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(msg), fls.get(1));
					int endIdx = sqlstr.length() > 100 ? 100 : sqlstr.length();
					ddlDmlpo.addData(row,
							CommonUtils.createReadOnlyStringProperty(sqlstr.substring(0, endIdx) + " ... "),
							fls.get(2));
				}
			}

		}
		// 如果 ddlDmlpo 中有 msg的信息 就会显示到界面上
		TableViewUtils.showInfo(ddlDmlpo, thread);
		// 如果是执行的界面上的sql, 那么对错误的sql渲染为红色
		if (StrUtils.isNullOrEmpty(state.getSqlStr())) {
			Platform.runLater(() -> {
				if (errObj.size() > 0) {
					for (SqlData sd : errObj) {
						int bg = sd.begin();
						MyEditorSheetHelper.ErrorHighlighting(bg, sd.sql());
					}
				}
			});
		}
		return rsVal;
	}

	// 在子线程执行 运行sql 的任务
	public static Thread createThread(Consumer<RunSqlStatePo> action, RunSqlStatePo state) {
		return new Thread() {
			@Override
			public void run() {
				logger.info("线程启动了" + this.getName());
				action.accept(state);
				logger.info("线程结束了" + this.getName());
			}
		};
	}

	// 设置 按钮状态
	public static void settingBtn() {
		if (runbtn == null) {
			runbtn = CommonButtons.runbtn;
			otherbtn = CommonButtons.runFunPro;
			runLinebtn = CommonButtons.runLinebtn;
		}
		if (stopbtn == null) {
			stopbtn = CommonButtons.stopbtn;
		}

		runbtn.setDisable(stopbtn.disabledProperty().getValue());
		otherbtn.setDisable(stopbtn.disabledProperty().getValue());
		runLinebtn.setDisable(stopbtn.disabledProperty().getValue());
		stopbtn.setDisable(!runbtn.disabledProperty().getValue());
		ComponentGetter.connComboBox.setDisable(runbtn.disabledProperty().getValue());
	}

	public static void settingBtn(JFXButton run, boolean runt, JFXButton stop, boolean stopt, JFXButton btn) {
		run.setDisable(runt);
		btn.setDisable(runt);
		stop.setDisable(stopt);
	}

	// 检查db连接状态
	private static boolean checkDBconn() {
		ComboBox<Label> conns = ComponentGetter.connComboBox;
		boolean warn = false;
		if (conns.getValue() == null || StrUtils.isNullOrEmpty(conns.getValue().getText())) {
			warn = true;
			MyAlert.notification("Error", "Please , choose alive DB connection!", MyAlert.NotificationType.Error);
			return warn;
		}
		String val = conns.getValue().getText();
		SqluckyConnector po = DBConns.get(val);
		if (po == null || !po.isAlive()) {
			warn = true;
			MyAlert.notification("Error", "Please ,  connect DB !", MyAlert.NotificationType.Error);
		}
		return warn;
	}

	// 刷新
	public static Long refresh(SqluckyConnector sqlConn, String sqlv, String tabIdxv, boolean isLockv) {
		Long statusKey = CommonUtils.dateTime();

		RUN_STATUS.put(statusKey, -1);

		RunSqlStatePo state = new RunSqlStatePo(sqlv, sqlConn);
		state.setTidx(tabIdxv);
		state.setIsRefresh(true);
		state.setIsLock(isLockv);
		state.setStatusKey(statusKey);

		thread = createThread(RunSQLHelper::runMain, state);
		thread.start();
		return statusKey;
	}

	public static void runCurrentLineSQLMethod() {
		Boolean isCurrentLine = true;
		runSQLMethod(null, null, false, isCurrentLine);
	}

	public static void runSQLMethod() {
		runSQLMethod(null, null, false, null);
	}

	public static void runCreateFuncSQLMethod() {
		runSQLMethod(null, null, true, null);
	}

	// 调用函数/存储过程
	public static void callProcedure(String sqlv, SqluckyConnector sqlConn, List<ProcedureFieldPo> fields) {
		if (checkDBconn())
			return;
		Connection connv = sqlConn.getConn();
		try {
			if (connv == null) {
				return;
			} else if (connv.isClosed()) {
				MyAlert.notification("Error", "Connect is Closed!", MyAlert.NotificationType.Error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		RunSqlStatePo state = new RunSqlStatePo(sqlv, sqlConn);
		state.setIsCallFunc(true);
		state.setCallProcedureFields(fields);

		thread = createThread(RunSQLHelper::runMain, state);
		thread.start();
	}

	public static void runSQLMethod(String sqlv, String tabIdxv, boolean isCreateFunc, Boolean isCurrentLine) {
		if (checkDBconn())
			return;
		SqluckyConnector sqlConn = AppCommonAction.getDbConnectionPoByComboBoxDbConnName();
		Connection connv = sqlConn.getConn();
		try {
			if (connv == null) {
				return;
			} else if (connv.isClosed()) {
				MyAlert.notification("Error", "Connect is Closed!", MyAlert.NotificationType.Error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		RunSqlStatePo state = new RunSqlStatePo(sqlv, sqlConn);
		state.setTidx(tabIdxv);
		if (isCurrentLine != null) {
			state.setIsCurrentLine(isCurrentLine);
		}

		state.setIsCreateFunc(isCreateFunc);

		thread = createThread(RunSQLHelper::runMain, state);
		thread.start();
	}

	/**
	 * 根据自己提供的SqluckyConnector, 来执行sql
	 * 
	 * @param sqlConn
	 * @param sqlv
	 * @param isCreateFunc 执行create 语句
	 */
	public static void runSQL(SqluckyConnector sqlConn, String sqlv, boolean isCreateFunc) {
		Connection connv = sqlConn.getConn();
		try {
			if (connv == null) {
				return;
			} else if (connv.isClosed()) {
				MyAlert.notification("Error", "Connect is Closed!", MyAlert.NotificationType.Error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		RunSqlStatePo state = new RunSqlStatePo(sqlv, sqlConn);
		state.setIsCreateFunc(isCreateFunc);

		thread = createThread(RunSQLHelper::runMain, state);
		thread.start();
	}

	/*
	 * 查看table ddl界面 执行查询按钮, 不刷新底部tab
	 */
	public static void runSelectSqlLockTabPane(SqluckyConnector sqlConn, String sqlv) {
		Connection connv = sqlConn.getConn();
		try {
			if (connv == null) {
				return;
			} else if (connv.isClosed()) {
				MyAlert.notification("Error", "Connect is Closed!", MyAlert.NotificationType.Error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		RunSqlStatePo state = new RunSqlStatePo(sqlv, sqlConn);
		state.setIsRefresh(true);
		thread = createThread(RunSQLHelper::runMain, state);
		thread.start();
	}

	// stop 入口
	public static void stopSQLMethod() {
		if (thread != null && !stopbtn.disabledProperty().getValue()) {
			thread.interrupt();
			logger.info("线程是否被中断：" + thread.isInterrupted());
			tmpSqlConn.closeConn();
			tmpSqlConn.getConn();
		}
	}

}
