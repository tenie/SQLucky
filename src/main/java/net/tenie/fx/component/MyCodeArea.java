package net.tenie.fx.component;

import org.fxmisc.richtext.CodeArea;

public class MyCodeArea extends CodeArea {
	private MyLineNumberFactory mylineNumber;

	public MyLineNumberFactory getMylineNumber() {
		return mylineNumber;
	}

	public void setMylineNumber(MyLineNumberFactory mylineNumber) {
		this.mylineNumber = mylineNumber;
	}
	 
}
