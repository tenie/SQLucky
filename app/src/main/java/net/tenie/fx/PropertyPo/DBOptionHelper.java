package net.tenie.fx.PropertyPo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.tenie.Sqlucky.sdk.po.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.TablePo;

/*   @author tenie */
public class DBOptionHelper {

	// 返回 指定schema中所有的表
	static public List<TablePo> getTabsName(DbConnectionPo po, String schemasName) {
		Map<String, DbSchemaPo> map = po.getSchemas();
		DbSchemaPo spo = map.get(schemasName);
		List<TablePo> tbs = new ArrayList<>();
		if (spo != null) {
			tbs = spo.getTabs();
//			if ( tbs == null) {
				try {
					tbs = po.getExportDDL().allTableObj(po.getConn(), schemasName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				spo.setTabs(tbs);
//			}
		}

		return tbs;
	}

	// 返回 指定schema中所有的view
	static public List<TablePo> getViewsName(DbConnectionPo po, String schemasName) {
		Map<String, DbSchemaPo> map = po.getSchemas();
		DbSchemaPo spo = map.get(schemasName);
		List<TablePo> views = new ArrayList<TablePo>();
		if (  spo != null) {
//			views = spo.getViews();
//			if (  views == null) {
				try {
					views = po.getExportDDL().allViewObj(po.getConn(), schemasName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				spo.setViews(views);
//			}
		}

		return views;
	}

	// 返回 指定schema中所有的Function
	static public List<FuncProcTriggerPo> getFunctions(DbConnectionPo po, String schemasName) {
		Map<String, DbSchemaPo> map = po.getSchemas();
		DbSchemaPo spo = map.get(schemasName);
		List<FuncProcTriggerPo> val = new ArrayList<>();
		if ( spo != null) {
//			val = spo.getFunctions();
//			if ( val == null) {
				try {
					val = po.getExportDDL().allFunctionObj(po.getConn(), schemasName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				spo.setFunctions(val);
//			}
		}

		return val;
	}

	// 返回 指定schema中所有的Procedures
	static public List<FuncProcTriggerPo> getProcedures(DbConnectionPo po, String schemasName) {
		Map<String, DbSchemaPo> map = po.getSchemas();
		DbSchemaPo spo = map.get(schemasName);
		List<FuncProcTriggerPo> val = new ArrayList<>();
		if ( spo != null) {
//			val = spo.getProcedures();
//			if ( val == null) {
				try {
					val = po.getExportDDL().allProcedureObj(po.getConn(), schemasName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				spo.setProcedures(val);
//			}
		}

		return val;
	}

	// 返回 指定schema中所有的 trigger
	static public List<FuncProcTriggerPo> getTriggers(DbConnectionPo po, String schemasName) {
		Map<String, DbSchemaPo> map = po.getSchemas();
		DbSchemaPo spo = map.get(schemasName);
		List<FuncProcTriggerPo> val =new ArrayList<>();
		if ( spo != null) {
//			val = spo.getTriggers();
//			if (  val == null) {
				try {
					val = po.getExportDDL().allTriggerObj(po.getConn(), schemasName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				spo.setTriggers(val);
//			}
		}

		return val;
	}
	
	// 返回 指定schema中所有的 INDEX
	static public List<FuncProcTriggerPo> getIndexs(DbConnectionPo po, String schemasName ) {
		Map<String, DbSchemaPo> map = po.getSchemas();
		DbSchemaPo spo = map.get(schemasName);
		List<FuncProcTriggerPo> val = new ArrayList<>();
		if ( spo != null) {
//			val = spo.getTriggers();
//			if (  val == null) {
				try {
					val = po.getExportDDL().allIndexObj(po.getConn(), schemasName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//TODO 缓存
//				spo.setTriggers(val);
//			}
		}

		return val;
	}
	
	// 返回 指定schema中所有的 Sequence
	static public List<FuncProcTriggerPo> getSequences(DbConnectionPo po, String schemasName, boolean isNew) {
		Map<String, DbSchemaPo> map = po.getSchemas();
		DbSchemaPo spo = map.get(schemasName);
		List<FuncProcTriggerPo> val = new ArrayList<>();
		if (isNew || spo != null) {
//			val = spo.getTriggers();
//			if (isNew || val == null) {
				try {
					val = po.getExportDDL().allSequenceObj(po.getConn(), schemasName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//TODO 缓存
//				spo.setTriggers(val);
//			}
		}

		return val;
	}
		
	
	

//	 获取表的建表语句
	public static String getCreateTableSQL(DbConnectionPo cp, String schema, String tab) {
		String ddl = cp.getExportDDL().exportCreateTable(cp.getConn(), schema, tab);
		return ddl;
	}

//	 获取视图的语句
	public static String getViewSQL(DbConnectionPo cp, String schema, String viewName) {
		String ddl = cp.getExportDDL().exportCreateView(cp.getConn(), schema, viewName);
		return ddl;

	}

//	 获取函数的语句
	public static String getFunctionSQL(DbConnectionPo cp, String schema, String funcName) {
		String ddl = cp.getExportDDL().exportCreateFunction(cp.getConn(), schema, funcName);
		return ddl;

	}

//	 获取函数的语句
	public static String getProceduresSQL(DbConnectionPo cp, String schema, String name) {
		String ddl = cp.getExportDDL().exportCreateProcedure(cp.getConn(), schema, name);
		return ddl;

	}

}
