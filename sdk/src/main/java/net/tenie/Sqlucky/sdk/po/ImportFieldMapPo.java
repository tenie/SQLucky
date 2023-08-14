package net.tenie.Sqlucky.sdk.po;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.tenie.Sqlucky.sdk.db.PoDao;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;

/**
 * excel到处到表的字段数据结构
 * 
 * @author tenie
 *
 */
public class ImportFieldMapPo {
	private String tableName;
	private Long id;
	private String type;

	private Date createdTime;
	private Date updatedTime;

	private List<ImportFieldMapDetailPo> detailList;

//	private ImportFieldMapPo(List<ImportFieldPo> vals, String tableName, String type) {
//		this.tableName = tableName;
//		this.type = type;
//
//		detailList = new ArrayList<>();
//		if (vals != null && vals.size() > 0) {
//			vals.parallelStream().forEach(v -> {
//				ImportFieldMapDetailPo dpo = new ImportFieldMapDetailPo(v, id);
//				detailList.add(dpo);
//			});
//		}
//	}

	public ImportFieldMapPo() {
	}

	private ImportFieldMapPo(String tableName, String type) {
		this.tableName = tableName;
		this.type = type;
	}

	public static void save(String tableName, String type, List<ImportFieldPo> vals) {
		ImportFieldMapPo po = new ImportFieldMapPo(tableName, type);
		Connection conn = SqluckyAppDB.getConn();
		try {
			Long idVal = PoDao.insertReturnID(conn, po);
			po.setId(idVal);

			// detail save
			List<ImportFieldMapDetailPo> detailList = new ArrayList<>();
			if (vals != null && vals.size() > 0) {
				vals.parallelStream().forEach(v -> {
					ImportFieldMapDetailPo dpo = new ImportFieldMapDetailPo(v, po.getId());
					try {
						PoDao.insert(conn, dpo);
					} catch (Exception e) {
						e.printStackTrace();
					}
					detailList.add(dpo);
				});
			}
			po.setDetailList(detailList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}

	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public List<ImportFieldMapDetailPo> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<ImportFieldMapDetailPo> detailList) {
		this.detailList = detailList;
	}

}
