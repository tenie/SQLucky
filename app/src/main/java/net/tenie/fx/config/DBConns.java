package net.tenie.fx.config;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;

/**
 * 
 * @author tenie
 *
 */
public final class DBConns {
	private static LinkedHashSet<String> set = new LinkedHashSet<String>();
	private static Map<String, SqluckyConnector> dbs = new HashMap<String, SqluckyConnector>();
	private static ComboBox<Label> cb;

	public static void flushChoiceBoxGraphic() {
		if (cb != null && cb.getItems() != null) {
			Platform.runLater(() -> {
				cb.getItems().forEach(item -> {
					if (item.getText().length() > 0) {
						SqluckyConnector po = dbs.get(item.getText());
						if (po != null && po.isAlive()) {
							item.setGraphic(IconGenerator.svgImageDefActive("link"));
						} else {
							item.setGraphic(IconGenerator.svgImageUnactive("unlink"));
						}
					}
				});
			});
		}
	}
	public static void flushChoiceBox() {
		flushChoiceBox(cb);
	}

	// 选中combox的元素
	public static void flushChoiceBox(String name) {
		flushChoiceBox() ;
		selectComboBoxItem(name);
		
	}
	// 选中combox的元素
	public static void selectComboBoxItem( String name) {
		ObservableList<Label> ls = cb.getItems();
		for(Label lb : ls) {
			if(lb.getText().equals(name)) {
				Platform.runLater(()->{
					cb.getSelectionModel().select(lb);
				}); 
				break;
			}
		}
	}

	// 选择连接框的选项添加
	public static void flushChoiceBox(ComboBox<Label> box) {
		if (box != null) {
			cb = box;
		}
		if (cb == null) {
			return;
		}
		
		ObservableList<Label> list= getChoiceBoxItems();

		cb.setItems(list);

	}
	
	public static ObservableList<Label> getChoiceBoxItems(){
		ObservableList<Label> list = FXCollections.observableArrayList();
		ObservableList<Label> aliveList = FXCollections.observableArrayList();
		aliveList.add(new Label(""));
		for (String key : DBConns.allNames()) {
			SqluckyConnector po = dbs.get(key);
			String name = po.getConnName();
			Label lb = new Label(name);
			if (po.isAlive()) {
				lb.setGraphic(IconGenerator.svgImageDefActive("link"));
				aliveList.add(lb);
			} else {
				lb.setGraphic(IconGenerator.svgImageUnactive("unlink"));
				list.add(lb);
			}
			
			
		}
		aliveList.addAll(list);
		return aliveList;
	}

	
	// 选择连接框的选项修改选项
	public static void changeChoiceBox(String idx) {
		if (cb != null && idx.length() > 0) {
			cb.getSelectionModel().select(Integer.valueOf(idx));

		}
	}

	public static void add(String name, SqluckyConnector o) {
		set.add(name);
		dbs.put(name, o);
		flushChoiceBox(cb);
	}
	
	public static void addAll(List<SqluckyConnector> ls) {
		for(var sc: ls) {
			var name = sc.getConnName();
			set.add(name);
			dbs.put(name, sc);
		}
		Platform.runLater(()->{
			flushChoiceBox(cb);
		});
	}

	public static boolean conaction(String name) {
		return dbs.containsKey(name);
	}

	public static void clear() {
		set.clear();
		dbs.clear();
	}

	public static void remove(String name) {
		set.remove(name);
		dbs.remove(name);
		flushChoiceBox(cb);
	}
	
//	public static void update(String oldName, String newName,) {
//		set.remove(name);
//		dbs.remove(name);
//		flushChoiceBox(cb);
//	}
	
	

	public static SqluckyConnector get(String name) {
		return dbs.get(name);
	}

	public static LinkedHashSet<String> allNames() {
		return set;
	}

	public static Map<String, SqluckyConnector> all() {
		return dbs;
	}

	public static boolean isEmpty() {
		return dbs.isEmpty();
	}
	
	// 当前选中的数据库连接名称
	public static String getCurrentConnectName() {
		Label lb = ComponentGetter.connComboBox.getValue();
		String str = "";
		if( lb != null) {
		   str = lb.getText(); 
		}
		return str;
	}
	public static SqluckyConnector getCurrentConnectPO() {
		String connName = getCurrentConnectName();
		return DBConns.get(connName);
	}
	public static Map<String, SqluckyConnector> getDbs() {
		return dbs;
	}
	

}
