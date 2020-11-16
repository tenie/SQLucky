package net.tenie.fx.component.container;

import org.controlsfx.control.MaskerPane;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.SqlCodeAreaHighLighting;
import net.tenie.fx.utility.EventAndListener.CommonEventHandler;

/*   @author tenie */
public class DataViewTab {

	// 创建Tab
	public static Tab createTab(TabPane dataTab, String tabName) {
		Tab tb = new Tab(tabName);
		tb.setOnCloseRequest(CommonEventHandler.dataTabCloseReq(dataTab, tb));
		tb.setContextMenu(tableViewMenu(tb));
		return tb;
	}

	public static Tab createTab(String tabName) {
		Tab tb = new Tab(tabName);
		tb.setOnCloseRequest(CommonEventHandler.dataTabCloseReq(ComponentGetter.dataTab, tb));
		tb.setContextMenu(tableViewMenu(tb));
		return tb;
	}

	// 表, 视图 等 数据库对象的ddl语句
	public static void showDdlPanel(String title, String ddl) {
		Tab tb = createTab(title);
		StackPane sp = new SqlCodeAreaHighLighting().getObj(ddl, false);
		tb.setContent(sp);

		ComponentGetter.dataTab.getTabs().add(tb);
		CommonAction.showDetailPane();
		ComponentGetter.dataTab.getSelectionModel().select(tb);
	}

	// 右键菜单
	public static ContextMenu tableViewMenu(Tab tb) {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem closeAll = new MenuItem("Close ALl");
		closeAll.setOnAction(e -> {
			int size = ComponentGetter.dataTab.getTabs().size();
			ComponentGetter.dataTab.getTabs().remove(0, size);
		});

		MenuItem closeOther = new MenuItem("Close Other");
		closeOther.setOnAction(e -> {
			int size = ComponentGetter.dataTab.getTabs().size();
			if (size > 1) {
				ComponentGetter.dataTab.getTabs().remove(0, size);
				ComponentGetter.dataTab.getTabs().add(tb);
			}

		});

		contextMenu.getItems().addAll(closeAll, closeOther);
		return contextMenu;
	}

	public static Tab maskTab(String waittbName) {
		Tab waitTb = createTab(ComponentGetter.dataTab, waittbName);
		MaskerPane masker = new MaskerPane();
		waitTb.setContent(masker);
		return waitTb;
	}

	public static void ifEmptyAddNewEmptyTab(TabPane dataTab, String tabName) {
		if (dataTab.getTabs().size() == 0) {
			addEmptyTab(dataTab, tabName);
		}
	}

	public static Tab addEmptyTab(TabPane dataTab, String tabName) {
		Tab tb = createTab(dataTab, tabName);
		dataTab.getTabs().add(tb);

		return tb;
	}

}
