package net.tenie.Sqlucky.sdk;

import org.fxmisc.richtext.CodeArea;

public interface SqluckyCodeArea {
	public SqluckyLineNumberNode getMylineNumber();
	public void setMylineNumber(SqluckyLineNumberNode mylineNumber);
	
	public CodeArea getCodeArea();
}
