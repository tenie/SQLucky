package net.tenie.fx.component.TreeItem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/*   @author tenie */
public class MyTreeItem<T> extends TreeItem<T> {
	private ObservableList<T> subTreeItems = FXCollections.observableArrayList();

	public MyTreeItem(T value) {
		super(value);
	}

	public ObservableList<T> getSubTreeItems() {
		return subTreeItems;
	}

	public void setSubTreeItems(ObservableList<T> subTreeItems) {
		this.subTreeItems = subTreeItems;
	}

}
