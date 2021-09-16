package net.tenie.fx.component;

import org.fxmisc.richtext.CodeArea;

import net.tenie.Sqlucky.sdk.component.CodeArea.MyLineNumberNode;

public class MyCodeArea2 extends CodeArea {
	private MyLineNumberNode mylineNumber;

	public MyLineNumberNode getMylineNumber() {
		return mylineNumber;
	}

	public void setMylineNumber(MyLineNumberNode mylineNumber) {
		this.mylineNumber = mylineNumber;
	}
	 
}
