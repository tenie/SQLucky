package net.tenie.Sqlucky.sdk.po.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javafx.scene.control.TableView;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.utility.TableViewUtil;


/**
 *  数据库里表对象的属性字段， 如表名等
 *  table view 共用
 * @author tenie
 *
 */
public class TablePo {
	private long id;
	private String tableName;
	private String tableRemarks;
	private String tableSchema;
	private String tableType;

	// 创建语句
	private String ddl;
	// 字段
	private LinkedHashSet<TableFieldPo> fields;
	// 主键
	private List<TablePrimaryKeysPo> primaryKeys; 
	// 外键
	private List<TableForeignKeyPo> foreignKeys;
	// 索引 
	private List<TableIndexPo> indexs;
	
	private Boolean dbObj = true;
	
	// 获取TableView ,foreignKey
	TableView<ResultSetRowPo> foreignKeyTable;
	TableView<ResultSetRowPo> indexTableView;
	
	public TablePo() {}
	
	public TablePo(String name ) {
		tableName = name;
	}
	 // 用于自动补全创建TablePo， 方便遍历表格实现对表名称输入自动补全， noDbObj表示创建的TablePo 是自定义的自动补全字符串
	public static TablePo noDbObj(String name ) {
		TablePo po = new TablePo(name);
		po.setDbObj(false);
		return po;
	}
	
	
	// show Index
	public TableView<ResultSetRowPo> indexTableView(){
		if(indexTableView == null) {
			List<String> colName = new ArrayList<>();
			colName.add("INDEX NAME");
			colName.add("TABLE NAME");
			colName.add("INDEX SCHEMA");
			colName.add("COL NAMES"); 
			List<Map<String, String>> vals = toMapByIndex();
			var sheetDaV = TableViewUtil.dataToSheet(colName, vals, null);
			// 获取TableView
		    indexTableView = sheetDaV.getInfoTable();
		}
		
		
		return indexTableView;
	}
	
	// show foreign key 
	public TableView<ResultSetRowPo> foreignKeyTableView() {
		if (foreignKeyTable == null) {
			List<String> foreignKeyColnames = new ArrayList<>();

			foreignKeyColnames.add("TABLE NAME");
			foreignKeyColnames.add("FOREIGN KEY NAME");
			foreignKeyColnames.add("PK COLNAMES");
			foreignKeyColnames.add("REF TABLE NAME");
			foreignKeyColnames.add("REF KEY NAME");
			foreignKeyColnames.add("PK COLNAMES");
			foreignKeyColnames.add("Drop FOREIGN KEY");

			List<Map<String, String>> foreignKeyVals = toMapByFK();

//			var foreignKeysheetDaV = TableViewUtil.dataToSheet(foreignKeyColnames, foreignKeyVals, null);
//			// 获取TableView
//			foreignKeyTable = foreignKeysheetDaV.getInfoTable();
			foreignKeyTable = TableViewUtil.dbTableIndexFkTableView(foreignKeyColnames, foreignKeyVals);
		}
		return foreignKeyTable;
	}
	
	// 将List中的对象转换为MAP 后返回一个新的list
	private List<Map<String, String>> toMapByIndex() {
		List<Map<String, String>> vals = new ArrayList<>();
		if (indexs != null) {
			for (TableIndexPo idxpo : indexs) {
				String iname = idxpo.getIndname();
				String tname = idxpo.getTabname();
				String she = idxpo.getIndschema();
				String cols = idxpo.getColnames();

				Map<String, String> tmpMap = new HashMap<>();
				tmpMap.put("INDEX NAME", iname);
				tmpMap.put("TABLE NAME", tname);
				tmpMap.put("INDEX SCHEMA", she);
				tmpMap.put("COL NAMES", cols);
				vals.add(tmpMap);
			}

		}

		return vals;
	} 
	// 将List中的对象转换为MAP 后返回一个新的list
	private List<Map<String, String>> toMapByFK() {
		List<Map<String, String>> vals = new ArrayList<>();
		 foreignKeys = new ArrayList<>();
		 TableForeignKeyPo fkpoval  = new TableForeignKeyPo();
		 fkpoval.setTabName("1");
		 fkpoval.setConstname("12");
		 fkpoval.setFkColnames("13");
		 fkpoval.setRefTabname("14");
		 fkpoval.setRefKeyname("15");
		 fkpoval.setPkColnames("11");
		 foreignKeys.add(fkpoval);
		if (foreignKeys != null) {
			 
			for (TableForeignKeyPo fkpo : foreignKeys) {
				String TabName = fkpo.getTabName();
				String Constname = fkpo.getConstname();
				String FkColnames = fkpo.getFkColnames();
				String RefTabname = fkpo.getRefTabname();
				String refKeyname = fkpo.getRefKeyname();
				String pkColnames = fkpo.getPkColnames();

				Map<String, String> tmpMap = new HashMap<>();
				tmpMap.put("TABLE NAME", TabName);
				tmpMap.put("FOREIGN KEY NAME", Constname);
				tmpMap.put("PK COLNAMES", FkColnames);

				tmpMap.put("REF TABLE NAME", RefTabname);
				tmpMap.put("REF KEY NAME", refKeyname);
				tmpMap.put("PK COLNAMES", pkColnames);
				tmpMap.put("Drop FOREIGN KEY", "");
				vals.add(tmpMap);
			}
		}
		return vals;
	} 

	public String getDdl() {
		return ddl;
	}

	public void setDdl(String ddl) {
		this.ddl = ddl;
	}

	public List<TablePrimaryKeysPo> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(List<TablePrimaryKeysPo> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public String getTableSchema() {
		return tableSchema;
	}

	public void setTableSchema(String tableSchema) {
		this.tableSchema = tableSchema;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTableRemarks() {
		return tableRemarks;
	}

	public void setTableRemarks(String tableRemarks) {
		this.tableRemarks = tableRemarks;
	}

	public LinkedHashSet<TableFieldPo> getFields() {
		return fields;
	}

	public void setFields(LinkedHashSet<TableFieldPo> fields) {
		this.fields = fields;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

 
	public List<TableForeignKeyPo> getForeignKeys() {
		return foreignKeys;
	}

	public void setForeignKeys(List<TableForeignKeyPo> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

	public List<TableIndexPo> getIndexs() {
		return indexs;
	}

	public void setIndexs(List<TableIndexPo> indexs) {
		this.indexs = indexs;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public Boolean getDbObj() {
		return dbObj;
	}

	public void setDbObj(Boolean dbObj) {
		this.dbObj = dbObj;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result + ((tableSchema == null) ? 0 : tableSchema.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TablePo other = (TablePo) obj;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		if (tableSchema == null) {
			if (other.tableSchema != null)
				return false;
		} else if (!tableSchema.equals(other.tableSchema))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TablePo [id=" + id + ", tableName=" + tableName + ", tableRemarks=" + tableRemarks + ", tableSchema="
				+ tableSchema + ", tableType=" + tableType + ", fields=" + fields + ", primaryKeys=" + primaryKeys
				+ ", foreignKeys=" + foreignKeys + ", indexs=" + indexs + ", ddl=" + ddl + ", dbObj=" + dbObj + "]";
	}

}
