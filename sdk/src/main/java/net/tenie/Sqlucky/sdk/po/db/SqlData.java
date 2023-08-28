package net.tenie.fx.Po;

public class SqlData {
	public String sql;
	public int begin;
	public int length;
	public boolean isCallfunc = false;

	public SqlData(String s, int i, int len) {
		sql = s;
		begin = i;
		length = len;
	}

}
