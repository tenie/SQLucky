package net.tenie.Sqlucky.sdk.po;

import javafx.scene.layout.HBox;

public class SqlcukyTitledPaneInfoPo {
	private String name;
	private HBox  btnsBox;
	
	public SqlcukyTitledPaneInfoPo(String Name, HBox  btnsBox) {
		this.name = Name;
		this.btnsBox= btnsBox;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HBox getBtnsBox() {
		return btnsBox;
	}

	public void setBtnsBox(HBox btnsBox) {
		this.btnsBox = btnsBox;
	}
	 
	
	
}
