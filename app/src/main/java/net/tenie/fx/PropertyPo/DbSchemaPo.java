package net.tenie.fx.PropertyPo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tenie.Sqlucky.sdk.po.TablePo;

/*   @author tenie */
public class DbSchemaPo {
	private String schemaName;
	private List<TablePo> tabs;
	private Map<String, TablePo> mTabs = new HashMap<>();

	private List<TablePo> views;
	private Map<String, TablePo> mViews = new HashMap<>();

	private List<FuncProcTriggerPo> functions;
	private Map<String, FuncProcTriggerPo> mFunctions = new HashMap<>();

	private List<FuncProcTriggerPo> procedures;
	private Map<String, FuncProcTriggerPo> mProcedures = new HashMap<>();

	private List<FuncProcTriggerPo> triggers;
	private Map<String, FuncProcTriggerPo> mTriggers = new HashMap<>();

	public DbSchemaPo() {
	}

	public DbSchemaPo(String schemaName, List<TablePo> tabs) {
		super();
		this.schemaName = schemaName;
		this.tabs = tabs;

		if (tabs != null && tabs.size() > 0) {
			for (int i = 0; i < tabs.size(); i++) {
				TablePo tpo = tabs.get(i);
				mTabs.put(tpo.getTableName(), tpo);
			}
		}

	}

	public TablePo getTable(String name) {
		return mTabs.get(name);
	}

	public TablePo getView(String name) {
		return mViews.get(name);
	}

	public Map<String, TablePo> getmTabs() {
		return mTabs;
	}

	public void setmTabs(Map<String, TablePo> mTabs) {
		this.mTabs = mTabs;
	}

	public Map<String, TablePo> getmViews() {
		return mViews;
	}

	public void setmViews(Map<String, TablePo> mViews) {
		this.mViews = mViews;
	}

	public Map<String, FuncProcTriggerPo> getmFunctions() {
		return mFunctions;
	}

	public void setmFunctions(Map<String, FuncProcTriggerPo> mFunctions) {
		this.mFunctions = mFunctions;
	}

	public Map<String, FuncProcTriggerPo> getmProcedures() {
		return mProcedures;
	}

	public void setmProcedures(Map<String, FuncProcTriggerPo> mProcedures) {
		this.mProcedures = mProcedures;
	}

	public Map<String, FuncProcTriggerPo> getmTriggers() {
		return mTriggers;
	}

	public void setmTriggers(Map<String, FuncProcTriggerPo> mTriggers) {
		this.mTriggers = mTriggers;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public List<TablePo> getTabs() {
		return tabs;
	}

	public void setTabs(List<TablePo> tabs) {
		this.tabs = tabs;
	}

	public List<TablePo> getViews() {
		return views;
	}

	public void setViews(List<TablePo> views) {
		this.views = views;
	}

	public List<FuncProcTriggerPo> getFunctions() {
		return functions;
	}

	public void setFunctions(List<FuncProcTriggerPo> functions) {
		this.functions = functions;
	}

	public List<FuncProcTriggerPo> getProcedures() {
		return procedures;
	}

	public void setProcedures(List<FuncProcTriggerPo> procedures) {
		this.procedures = procedures;
	}

	public List<FuncProcTriggerPo> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<FuncProcTriggerPo> triggers) {
		this.triggers = triggers;
	}

}
