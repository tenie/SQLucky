package net.tenie.fx.Action;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.tenie.Sqlucky.sdk.sql.SqlParser;
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

	private static final Logger logger = LogManager.getLogger(RunSQLHelper.class);
	private static Thread thread;

	private static JFXButton runbtn;
	private static JFXButton stopbtn;
	private static JFXButton otherbtn;
	
	public  static boolean isRunning = false;
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
				allsqls = SqluckyAppDB.willExecSql( ! state.getIsCurrentLine());
			}
			// 执行sql SqluckyConnector
			tmpSqlConn = state.getSqlConn();
			var rsVal = execSqlList(allsqls, tmpSqlConn, state);

			// 执行sql的状态保存
			if (state.getStatusKey() != null) {
				RUN_STATUS.put(state.getStatusKey(), rsVal);

			}
		} catch (Exception e) {
			e.printStackTrace();
			if( ! thread.isInterrupted()){
				thread.interrupt();
			}
		} finally {
			if (isRunning) {
				settingBtn();
				state.setCallProcedureFields(null);
				state.setIsCallFunc(false);
				SdkComponent.rmWaitingPane();
			}
			tmpSqlConn = null;
			ComponentGetter.sqlStatement = null;

			isRunning = false;
		}

	}

	// 执行查询sql 并拼装成一个表, 多个sql生成多个表
	private static Integer execSqlList(List<SqlData> allsqls, SqluckyConnector sqluckyConn, RunSqlStatePo state) {
		int rsVal = 1;
		String execSql;
	
		int sqllenght = allsqls.size();
		DbTableDatePo ddlDmlpo = DbTableDatePo.setExecuteInfoPo();
		List<SqlData> errObj = new ArrayList<>();

		for (int i = 0; i < sqllenght; i++) {
				String msg = "";

			execSql = allsqls.get(i).sql();
			try {
				if (execSql.length() < 10) {
					throw new RuntimeException("Illegal Sql : " + execSql);
				}
				int type = ParseSQL.parseType(execSql);

				SdkComponent.setWaitTabLabelText(execSql);

				if (state.getIsCallFunc()) { // 调用存储过程
					ProcedureAction.procedureAction(execSql, sqluckyConn, state.getCallProcedureFields(), state.getTidx(), state.getIsLock());
				} else if (type == ParseSQL.SELECT || type == ParseSQL.OTHER_QUERY ) { // 调用查询
					SelectAction.selectAction(execSql, sqluckyConn, state.getTidx(), state.getIsLock(), state.getSelectLimit(), type);

				} else {
					Connection conn = sqluckyConn.getConn();
					if (type == ParseSQL.UPDATE) {
						msg = DmlDdlDao.updateSql2(conn, execSql);
						logger.info("add update sql: " + execSql);
					} else if (type == ParseSQL.INSERT) {
						msg = DmlDdlDao.insertSql2(conn, execSql);
						logger.info("add insert sql: " + execSql);
					} else if (type == ParseSQL.DELETE) {
						msg = DmlDdlDao.deleteSql2(conn, execSql);
						logger.info("add DELETE sql: " + execSql);
					} else if (type == ParseSQL.DROP) {
						msg = DmlDdlDao.dropSql2(conn, execSql);
						logger.info("add DROP sql: " + execSql);
					} else if (type == ParseSQL.ALTER) {
						msg = DmlDdlDao.alterSql2(conn, execSql);
						logger.info("add ALTER sql: " + execSql);
					} else if (type == ParseSQL.CREATE) {
						msg = DmlDdlDao.createSql2(conn, execSql);
						logger.info("add CREATE sql: " + execSql);
					} else {
						msg = DmlDdlDao.otherSql2(conn, execSql);
						logger.info("add OTEHR sql: " + execSql);
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
					ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(DateUtils.timeToStr(new Date())),
							fls.get(0));
					ddlDmlpo.addData(row, CommonUtils.createReadOnlyStringProperty(msg), fls.get(1));
//					int endIdx = execSql.length() > 400 ? 400 : execSql.length();

					String pressString = StrUtils.pressString(execSql);
					ddlDmlpo.addData(row,
							CommonUtils.createReadOnlyStringProperty(pressString),
							fls.get(2));
				}
			}

		}
		// 如果 ddlDmlpo 中有 msg的信息 就会显示到界面上
		TableViewUtils.showInfo(ddlDmlpo);
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
//			runLinebtn = CommonButtons.runLinebtn;
		}
		if (stopbtn == null) {
			stopbtn = CommonButtons.stopbtn;
		}

		runbtn.setDisable(stopbtn.disabledProperty().getValue());
		otherbtn.setDisable(stopbtn.disabledProperty().getValue());
//		runLinebtn.setDisable(stopbtn.disabledProperty().getValue());
		stopbtn.setDisable(!runbtn.disabledProperty().getValue());
		ComponentGetter.connComboBox.setDisable(runbtn.disabledProperty().getValue());
	}

	// 检查db连接状态
	private static boolean checkDBConn() {
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

	public static void runSQLMethod() {
		runSQLMethod(null, null, false, null);
	}

	public static void runCreateFuncSQLMethod() {
		runSQLMethod(null, null, true, null);
	}

	// 调用函数/存储过程
	public static void callProcedure(String sqlv, SqluckyConnector sqlConn, List<ProcedureFieldPo> fields) {
		if (checkDBConn()){
			return;
		}

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

	//
	public static void  runAction(){
		String selectStr = MyEditorSheetHelper.getCurrentCodeAreaSQLSelectedText();
		// 没有选中文本, 获取当前行sql是否合法, 合法执行当前行
		if(StrUtils.isNullOrEmpty(selectStr)){
			String str = MyEditorSheetHelper.getCurrentLineText();
			System.out.println(str);
			boolean valid = SqlParser.isValidSql(str);
			if(valid){
				MyEditorSheetHelper.selectCurrentLine();
				Platform.runLater(()->
						RunSQLHelper.runSQLMethod(null, null, false, true) );
				return;
			}

		}else { // 执行选中的sql
			Platform.runLater(()->
					RunSQLHelper.runSQLMethod(null, null, false, true)
			);
			return;
		}
		Platform.runLater(()->RunSQLHelper.runSQLMethod());
	}


	public static void runActionCurrentLine() {
		MyEditorSheetHelper.selectCurrentLine();
		Platform.runLater(() -> {
			RunSQLHelper.runSQLMethod(null, null, false, true);
		});
	}


	public static void runSQLMethod(String sqlv, String tabIdxv, boolean isCreateFunc, Boolean isCurrentLine) {
		if (checkDBConn()){
			return;
		}

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

	/**
	 * 根据RunSqlStatePo中的值来执行sql
	 * @param sqlConn
	 * @param state
	 */
	public static void runSQLByRunSqlStatePo(SqluckyConnector sqlConn, RunSqlStatePo state) {
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
		thread = createThread(RunSQLHelper::runMain, state);
		thread.start();
	}


	/**
	 * 查看table ddl界面 执行查询按钮, 不刷新底部tab
	 * @param sqlConn
	 * @param sqlv
	 * @param limit 限制查询的行数, 查询20条
	 */
	public static void runSelectSqlLockTabPane(SqluckyConnector sqlConn, String sqlv, Integer limit) {
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
		state.setSelectLimit(limit);
		thread = createThread(RunSQLHelper::runMain, state);
		thread.start();
	}

	// stop 入口
	public static void stopSQLMethod() {
		if (thread != null && !stopbtn.disabledProperty().getValue()) {
			thread.interrupt();
			logger.info("线程是否被中断：" + thread.isInterrupted());
			if (ComponentGetter.sqlStatement != null) {
				try {
					ComponentGetter.sqlStatement.cancel();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
			if (tmpSqlConn != null) {
				tmpSqlConn.closeConn();
				tmpSqlConn.getConn();
			}

		}
	}

}
