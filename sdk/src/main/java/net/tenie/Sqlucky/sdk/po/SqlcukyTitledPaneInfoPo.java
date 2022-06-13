package net.tenie.Sqlucky.sdk.po;

import javafx.scene.layout.Pane;

public class SqlcukyTitledPaneInfoPo {
	private String name;
	private Pane  btnsBox;
	
	public SqlcukyTitledPaneInfoPo(String Name, Pane  btnsBox) {
		this.name = Name;
		this.btnsBox= btnsBox;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Pane getBtnsBox() {
		return btnsBox;
	}

	public void setBtnsBox(Pane btnsBox) {
		this.btnsBox = btnsBox;
	}
	 
	
	
}
