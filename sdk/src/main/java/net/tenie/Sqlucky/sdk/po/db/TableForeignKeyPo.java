package net.tenie.Sqlucky.sdk.po.db;

/**
 * 创建表格外键: ALTER TABLE 子表 ADD CONSTRAINT 外键名称  FOREIGN KEY (ID)  REFERENCES 主表;
 * 指定 子表 中的字段(id), 和 主表 中的主键做外键关联
 * 删除外键 : ALTER TABLE 子表  DROP CONSTRAINT 外键名称 ;
 * @author tenie
 *
 *select * from syscat.references where CONSTNAME = '外键名' and TABNAME='表名';
 */
public class TableForeignKeyPo {
	private String tabName; // TABNAME
	private String constname; // CONSTNAME 	约束名称 外键名称
	private String fkColnames; // FK_COLNAMES	外键字段
	private String refTabname; // REFTABNAME 引用表名称 (主表)
	private String pkColnames; //	PK_COLNAMES	引用表字段名称(就是主表的主键)
	private String refKeyname;  // REFKEYNAME 主表的主键名称
	
	private String tabSchema; //Schema

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getConstname() {
		return constname;
	}

	public void setConstname(String constname) {
		this.constname = constname;
	}

	public String getFkColnames() {
		return fkColnames;
	}

	public void setFkColnames(String fkColnames) {
		this.fkColnames = fkColnames;
	}

	public String getRefTabname() {
		return refTabname;
	}

	public void setRefTabname(String refTabname) {
		this.refTabname = refTabname;
	}

	public String getPkColnames() {
		return pkColnames;
	}

	public void setPkColnames(String pkColnames) {
		this.pkColnames = pkColnames;
	}

	public String getRefKeyname() {
		return refKeyname;
	}

	public void setRefKeyname(String refKeyname) {
		this.refKeyname = refKeyname;
	}

	public String getTabSchema() {
		return tabSchema;
	}

	public void setTabSchema(String tabSchema) {
		this.tabSchema = tabSchema;
	}

	@Override
	public String toString() {
		return "TableForeignKeyPo [tabName=" + tabName + ", constname=" + constname + ", fkColnames=" + fkColnames
				+ ", refTabname=" + refTabname + ", pkColnames=" + pkColnames + ", refKeyname=" + refKeyname
				+ ", tabSchema=" + tabSchema + "]";
	}
	
	
}
