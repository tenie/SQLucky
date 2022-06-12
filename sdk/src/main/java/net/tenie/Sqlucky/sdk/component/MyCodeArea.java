package net.tenie.Sqlucky.sdk.component;

import org.fxmisc.richtext.CodeArea;

import net.tenie.Sqlucky.sdk.SqluckyCodeArea;
import net.tenie.Sqlucky.sdk.SqluckyLineNumberNode;

public class MyCodeArea extends CodeArea implements SqluckyCodeArea {
	private SqluckyLineNumberNode mylineNumber;

	public SqluckyLineNumberNode getMylineNumber() {
		return mylineNumber;
	}

	public void setMylineNumber(SqluckyLineNumberNode mylineNumber) {
		this.mylineNumber = mylineNumber;
	}

	@Override
	public CodeArea getCodeArea() { 
		return this;
	}
	 
}
