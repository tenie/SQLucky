package net.tenie.Sqlucky.sdk.po.db;
 
//public class SqlData {
//	public String sql;
//	public int begin;
//	public int length;
//	public boolean isCallfunc = false;
//
//	public SqlData(String sqlVal, int beginVal, int lengthVal) {
//		sql = sqlVal;
//		begin = beginVal;
//		length = lengthVal;
//	}
//
//}

public record SqlData(String sql, int begin, int length  ) {}
