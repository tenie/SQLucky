package net.tenie.fx.component;

import org.fxmisc.richtext.CodeArea;

public class MyCodeArea extends CodeArea {
	private MyLineNumberNode mylineNumber;

	public MyLineNumberNode getMylineNumber() {
		return mylineNumber;
	}

	public void setMylineNumber(MyLineNumberNode mylineNumber) {
		this.mylineNumber = mylineNumber;
	}
	 
}
