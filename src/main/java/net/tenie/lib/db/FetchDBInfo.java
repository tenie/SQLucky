package net.tenie.lib.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.tenie.fx.PropertyPo.TableFieldPo;
import net.tenie.fx.PropertyPo.TablePo;
import net.tenie.fx.PropertyPo.myEntry;

import java.util.Set;
/* 
 *  * @author tenie 
 *  
 */
public abstract class FetchDBInfo {
	private  String schema = "";
	private  List<TablePo> tables ;
	private  List<String> seq ;
	private  List<String> triggers;
	private  List<String> procedures;  //存储过程
	private  List<String> functions;    // 函数
	private  List<String> indexs; 
	private  List<String> views;
	private  List<myEntry<String, String>> foreignKeys;  // 外键, 包含 表面和键名
	
	 
	
	//构造	
	public FetchDBInfo(String s){
		schema = s;
	}
	
	
	
	public abstract List<myEntry<String, String>> getForeignKeys(Connection conn);
	public List<myEntry<String, String>>getForeignKeys() {
		return foreignKeys;
	} 

	public void setForeignKey(List<myEntry<String, String>> foreignKey) {
		this.foreignKeys = foreignKey;
	} 

	// 视图
	public abstract List<String> getViews(Connection conn);
	public List<String> getViews() {
		return views;
	} 
	public void setViews(List<String> views) {
		this.views = views;
	}


	// 索引
	public abstract  List<String> getIndexs(Connection conn, String type);
	public abstract  List<String> getIndexs(Connection conn);
	public List<String> getIndex() {
		return indexs;
	}
	public void setIndex(List<String> index) {
		this.indexs = index;
	}



	// 获取 存储过程
	public abstract List<String> getProcedure(Connection conn);
	public List<String> getProcedures() {
		return procedures;
	} 
	public void setProcedures(List<String> procedure) {
		this.procedures = procedure;
	}
	
	

	// 获取 函数
	public abstract List<String> getFunctions(Connection conn);
	public List<String> getFunctions() {
		return functions;
	} 
	public void setFunctions(List<String> function) {
		this.functions = function;
	}

	public List<String> getTriggers() {
		return triggers;
	}
	// 获取触发器
	public abstract List<String> getTriggers(Connection conn);


	public void setTriggers(List<String> triggers) {
		this.triggers = triggers;
	}



	public List<String> getSeq() {
		return seq;
	}
	// 获取序列
	public abstract List<String> getSeq(Connection conn);
	//public abstract List<String> getSeq(Connection conn, String name);

	public void setSeq(List<String> seq) {
		this.seq = seq;
	}
	
	
	public List<TablePo> getTables() {
		if(tables == null) {
			return new ArrayList<TablePo>();
		}
		return tables;
	}  
	public void setTables(List<TablePo> tables) {
		this.tables = tables;
	}
	
	// 获取表信息
	public abstract  List<TablePo> getTables(Connection conn) ;
	// 建表语句, 加字段语句, 删字段语句, 修改字段语句
	public abstract String createTab(TablePo  tab ); 
	public abstract String createTab(Connection conn, TablePo  tab )throws SQLException;
	public abstract String alterTabAddColumn(TableFieldPo po);
	public abstract String alterTabAddColumn(Connection conn,  TableFieldPo po)throws SQLException;
	public abstract String alterTabDropColumn(TableFieldPo po); 
	public abstract String alterTabDropColumn(Connection conn, TableFieldPo po)throws SQLException; 
	public abstract String alterTabModifyColumn(TableFieldPo po); 
	public abstract String alterTabModifyColumn(Connection conn, TableFieldPo po)throws SQLException;  
	public abstract String alterTabAddPriMaryKey(String table, String kn, String fields);
	public abstract String alterTabAddPriMaryKey(Connection conn, String table, String kn, String fields)throws SQLException;  
	// 删除 表
	public abstract String dropTab(Connection conn, String tab)throws SQLException;
	public abstract String dropTab(String tab);
	// 删除view
	public abstract String dropView(Connection conn, String view)throws SQLException;
	public abstract String dropView( String view);
	// 删除sequence
	public abstract String dropSeq( String Seq);
	public abstract String dropSeq(Connection conn, String Seq)throws SQLException;
	// 删除tirgger
	public abstract String dropTirgger( String Tirgger);
	public abstract String dropTirgger(Connection conn, String Tirgger)throws SQLException;
	// 删除 procedure
	public abstract String dropProcedure(String  procedure);
	public abstract String dropProcedure(Connection conn, String  procedure)throws SQLException;
	// 删除function
	public abstract String dropFunction( String  fun);
	public abstract String dropFunction(Connection conn, String  fun)throws SQLException;
	// 删除 index 
	public abstract String dropIndex(String  name);
	public abstract String dropIndex(Connection conn, String  name)throws SQLException;
	// 删除  foreign key 
	public abstract String dropForeignKey( String table, String  name); 
	public abstract String dropForeignKey(Connection conn, String table, String  name)throws SQLException;
	public abstract String execDropForeignKey(Connection conn, String  name)throws SQLException;
	
	
	//// 删除  primary  key 
	public abstract String dropPrimaryKey( String table);
	public abstract String dropPrimaryKey(Connection conn, String table)throws SQLException;
	
	
	// 找到没有的表
	public abstract List<TablePo> findNewTab(List<TablePo>  source, List<TablePo>  tag);
	public abstract List<TablePo> findMyTab(List<TablePo> source, List<TablePo> tag);
	// 找到字段差异的(少或长度不同)
	public abstract List<TableFieldPo> findNewField(List<TablePo>  source, List<TablePo>  tag);
	
     // 批量生成reorg
	public abstract  List<String> batchReorg(Set<String> ls);
	
     
     // 数据迁移
	public abstract  void moveData(Connection soconn, Connection tagconn, TablePo soTab);
     
     // 统计表数据行数
	public abstract  Integer statisticRow(Connection tagconn,  TablePo soTab);
     
     // DB2 seq导出
	public abstract  List<String>  exportAllSeqs(Connection tagconn);
	public abstract  String exportSeq(Connection tagconn, String seq); 
     // DB2 Triggers导出
	public abstract  List<String>  exportAllTriggers(Connection tagconn );  
	public abstract   String  exportTrigger(Connection tagconn, String Trigger);  
     // DB2 functions procedures 导出
	public abstract  List<String>  exportAllProcedures(Connection tagconn  ); 
	public abstract  String exportProcedure(Connection tagconn,String name);
	
	public abstract  List<String>  exportAllFunctions(Connection tagconn); 
	public abstract  String exportFunction(Connection tagconn, String name);  
	
	// 导出index
	public abstract  List<String>  exportAllIndexs(Connection conn);
	public abstract  String exportIndex(Connection conn, String name); 
	
	// 导出View
	public abstract  List<String>  exportAllViews(Connection conn);
	public abstract  String exportView(Connection conn, String name); 
	
	// 导出Foreign key
	public abstract  List<String>  exportAllForeignKeys(Connection conn);
	public abstract  String exportForeignKey(Connection conn, String name); 
	
}
