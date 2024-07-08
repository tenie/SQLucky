package net.tenie.fx.Action.sqlExecute;

import java.util.List;

import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.db.ProcedureFieldPo;
import net.tenie.Sqlucky.sdk.po.db.SqlData;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

public class RunSqlStatePo {
	// 新tab页插入的位置
	private  Integer tidx = null;
	// 参数
	private  String sqlStr = null; 
	private  SqluckyConnector sqlConn = null;
	private  List<ProcedureFieldPo> callProcedureFields = null;

	private  Boolean isCreateFunc = false;
	// 执行语句的时候, 对其余tab不做操作, 正常情况会把非锁定的tab关闭
	private  Boolean isRefresh = false;
	// 查询后锁定下面的tab, 避免执行其他语句后被关闭
	private  Boolean isLock =false;   
	private  Boolean isCallFunc = false;
	private  Boolean isCurrentLine = false;
	
	private Long statusKey;
	// 查询限制行数
	private Integer selectLimit;

	// 要执行的sqls
	private List<SqlData> allsqls;


	public RunSqlStatePo(String sql, SqluckyConnector sqlConn) {
		this.sqlStr = sql;
		this.sqlConn = sqlConn;
	}
	
	
	public Integer getTidx() {
		if(tidx == null ) {
			return -1;
		}
		return tidx;
	}

	public void setTidx(Integer tidx) {
		this.tidx = tidx;
	}
	public void setTidx(String val) {
		if(StrUtils.isNotNullOrEmpty(val)) {
			this.tidx = Integer.valueOf(val);
		}else {
			this.tidx = -1;
		}
//		this.tidx = tidx;
	}

	public String getSqlStr() {
		return sqlStr;
	}

	public void setSqlStr(String sqlstr) {
		this.sqlStr = sqlstr;
	}


	public Boolean getIsCreateFunc() {
		return isCreateFunc;
	}

	public void setIsCreateFunc(Boolean isCreateFunc) {
		this.isCreateFunc = isCreateFunc;
	}

	public SqluckyConnector getSqlConn() {
		return sqlConn;
	}

	public void setSqlConn(SqluckyConnector val) {
		this.sqlConn = val;
	}

	public Boolean getIsRefresh() {
		return isRefresh;
	}

	public void setIsRefresh(Boolean isRefresh) {
		this.isRefresh = isRefresh;
	}

	public Boolean getIsLock() {
		return isLock;
	}

	public void setIsLock(Boolean isLock) {
		this.isLock = isLock;
	}

	public Boolean getIsCallFunc() {
		return isCallFunc;
	}

	public void setIsCallFunc(Boolean isCallFunc) {
		this.isCallFunc = isCallFunc;
	}

	public List<ProcedureFieldPo> getCallProcedureFields() {
		return callProcedureFields;
	}

	public void setCallProcedureFields(List<ProcedureFieldPo> callProcedureFields) {
		this.callProcedureFields = callProcedureFields;
	}

 

	public Boolean getIsCurrentLine() {
		return isCurrentLine;
	}

	public void setIsCurrentLine(Boolean isCurrentLine) {
		this.isCurrentLine = isCurrentLine;
	}

	public Long getStatusKey() {
		return statusKey;
	}

	public void setStatusKey(Long statusKey) {
		this.statusKey = statusKey;
	}

	public Boolean getCreateFunc() {
		return isCreateFunc;
	}

	public void setCreateFunc(Boolean createFunc) {
		isCreateFunc = createFunc;
	}

	public Boolean getRefresh() {
		return isRefresh;
	}

	public void setRefresh(Boolean refresh) {
		isRefresh = refresh;
	}

	public Boolean getLock() {
		return isLock;
	}

	public void setLock(Boolean lock) {
		isLock = lock;
	}

	public Boolean getCallFunc() {
		return isCallFunc;
	}

	public void setCallFunc(Boolean callFunc) {
		isCallFunc = callFunc;
	}

	public Boolean getCurrentLine() {
		return isCurrentLine;
	}

	public void setCurrentLine(Boolean currentLine) {
		isCurrentLine = currentLine;
	}

	public Integer getSelectLimit() {
		return selectLimit;
	}

	public void setSelectLimit(Integer selectLimit) {
		this.selectLimit = selectLimit;
	}

	public List<SqlData> getAllsqls() {
		return allsqls;
	}

	public void setAllsqls(List<SqlData> allsqls) {
		this.allsqls = allsqls;
	}
}
