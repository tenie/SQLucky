package net.tenie.fx.config;

import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import net.tenie.fx.PropertyPo.DbConnectionPo;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.ImageViewGenerator;

/*   @author tenie */
public final class DBConns {
	private static LinkedHashSet<String> set = new LinkedHashSet<String>();
	private static Map<String, DbConnectionPo> dbs = new HashMap<String, DbConnectionPo>();
	private static ComboBox<Label> cb;

	public static void flushChoiceBoxGraphic() {
		if (cb != null && cb.getItems() != null) {
			Platform.runLater(() -> {
				cb.getItems().forEach(item -> {
					if (item.getText().length() > 0) {
						DbConnectionPo po = dbs.get(item.getText());
						if (po != null && po.isAlive()) {
							item.setGraphic(ImageViewGenerator.svgImageDefActive("link"));
						} else {
							item.setGraphic(ImageViewGenerator.svgImageUnactive("unlink"));
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
				cb.getSelectionModel().select(lb);
				break;
			}
		}
	}

	// 选择连接框的选项添加
	public static void flushChoiceBox(ComboBox<Label> box) {
		if (box != null) {
			cb = box;
		} else {
//			cb = (ComboBox<Label>) ShareComponnet.name("ComponentGenerator.ChoiceBox.conns");
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
			DbConnectionPo po = dbs.get(key);
			String name = po.getConnName();
			Label lb = new Label(name);
			if (po.isAlive()) {
				lb.setGraphic(ImageViewGenerator.svgImageDefActive("link"));
				aliveList.add(lb);
			} else {
				lb.setGraphic(ImageViewGenerator.svgImageUnactive("unlink"));
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

	public static void add(String name, DbConnectionPo o) {
		set.add(name);
		dbs.put(name, o);
		flushChoiceBox(cb);
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
	
	

	public static DbConnectionPo get(String name) {
		return dbs.get(name);
	}

	public static LinkedHashSet<String> allNames() {
		return set;
	}

	public static Map<String, DbConnectionPo> all() {
		return dbs;
	}

	public static boolean isEmpty() {
		return dbs.isEmpty();
	}

	// TEST
//	public static Connection aliveConn() {
//		Connection conn = null;
//		for (String key : set) {
//			DbConnectionPo po = dbs.get(key);
//			if (po.isAlive()) {
//				conn = po.getConn();
//			}
//		}
//		return conn;
//	}
	
	// 当前选中的数据库连接名称
	public static String getCurrentConnectName() {
		Label lb = ComponentGetter.connComboBox.getValue();
		String str = "";
		if( lb != null) {
		   str = lb.getText(); 
		}
		return str;
	}
	public static DbConnectionPo getCurrentConnectPO() {
		String connName = getCurrentConnectName();
		return DBConns.get(connName);
	}

}
