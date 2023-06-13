package net.tenie.Sqlucky.sdk.po;

import javafx.scene.control.MenuItem;

public class KeyBindingItemPo {
	private String ActionName;
	private String Keys;
	private MenuItem menuItem;

	public KeyBindingItemPo() {

	}

	public KeyBindingItemPo(String ActionName, String Keys) {
		this.ActionName = ActionName;
		this.Keys = Keys;
	}

	public String getActionName() {
		return ActionName;
	}

	public void setActionName(String actionName) {
		ActionName = actionName;
	}

	public String getKeys() {
		return Keys;
	}

	public void setKeys(String keys) {
		Keys = keys;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}

}
