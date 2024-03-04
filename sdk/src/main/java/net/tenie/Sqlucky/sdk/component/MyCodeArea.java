package net.tenie.Sqlucky.sdk.component;

import org.fxmisc.richtext.CodeArea;

import net.tenie.Sqlucky.sdk.SqluckyLineNumberNode;

public class MyCodeArea extends CodeArea {
	private SqluckyLineNumberNode mylineNumber;
	private String titleName;
	public SqluckyLineNumberNode getMylineNumber() {
		return mylineNumber;
	}

	public void setMylineNumber(SqluckyLineNumberNode mylineNumber) {
		this.mylineNumber = mylineNumber;
	}

	public CodeArea getCodeArea() {
		return this;
	}

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
}
