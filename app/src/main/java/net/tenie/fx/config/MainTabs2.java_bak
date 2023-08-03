package net.tenie.fx.config;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

/*   @author tenie */
public class MainTabs2 {
	private static LinkedHashSet<String> set = new LinkedHashSet<String>();
	private static Map<String, MainTabInfo> data = new HashMap<String, MainTabInfo>();
	private static Tab actTab;

	public static void setBoxIdx(Tab t, String idx) {
//		 CommonUtility.tabText(nwTab);// t.getText()
		setBoxIdx(CommonUtils.tabText(t), idx);
	}

	public static void setBoxIdx(String name, String idx) {
		MainTabInfo tmp = data.get(name);
		if (tmp != null) {
			tmp.setTabConnIdx(idx);
		}

	}

	public static Tab getActTab() {
		TabPane myTabPane = ComponentGetter.mainTabPane;
		actTab = myTabPane.getSelectionModel().getSelectedItem();
		return actTab;
	}

	public static MainTabInfo get(Tab nwTab) {
		String name = CommonUtils.tabText(nwTab);// nwTab.getText();
		return get(name);
	}

	public static void add(Tab nwTab) {
		add(nwTab, "");
	}

	public static void add(Tab nwTab, String cn) {
		MainTabInfo tb = new MainTabInfo();
		String name =  CommonUtils.tabText(nwTab);// nwTab.getText();
		tb.setTabName(name);
		tb.setTabConnIdx(cn);
		tb.setTab(nwTab);
		add(name, tb);

	}

	public static void add(String name, MainTabInfo tb) {
		set.add(name);
		data.put(name, tb);
	}

	public static MainTabInfo get(String name) {
		MainTabInfo tabinfo = data.get(name);
		return tabinfo;
	}

	public static void del(String name) {
		set.remove(name);
		data.remove(name);
	}

	public static void clear() {
		set.clear();
		data.clear();
	}

	public static int size() {
		return set.size();
	}
}
