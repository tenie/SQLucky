package net.tenie.fx.config;

import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import net.tenie.fx.component.ImageViewGenerator;
//import net.tenie.fx.component.ShareComponnet;
import net.tenie.lib.po.DbConnectionPo;

/*   @author tenie */
public final class DBConns {
	private static LinkedHashSet<String> set = new LinkedHashSet<String>();
	private static Map<String, DbConnectionPo> dbs = new HashMap<String, DbConnectionPo>();
	private static JFXComboBox<Label> cb;

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

	// 选择连接框的选项添加
	public static void flushChoiceBox(JFXComboBox<Label> box) {
		if (box != null) {
			cb = box;
		} else {
//			cb = (JFXComboBox<Label>) ShareComponnet.name("ComponentGenerator.ChoiceBox.conns");
		}
		if (cb == null) {
			return;
		}
		String currentStr = "";
		Label currentLabel = null;
		if (cb.getItems() != null) {
			if (cb.getValue() != null) {
				currentStr = cb.getValue().getText();
				cb.getItems().clear();
			}
		}
		ObservableList<Label> list = FXCollections.observableArrayList();
		list.add(new Label(""));
		for (String key : DBConns.allNames()) {
			DbConnectionPo po = dbs.get(key);
			String name = po.getConnName();
			Label lb = new Label(name);
			if (po.isAlive()) {
				lb.setGraphic(ImageViewGenerator.svgImageDefActive("link"));
			} else {
				lb.setGraphic(ImageViewGenerator.svgImageUnactive("unlink"));
			}
			if (name.equals(currentStr)) {
				currentLabel = lb;
			}
			list.add(lb);
		}

		cb.setItems(list);
		if (currentLabel != null) {
			cb.getSelectionModel().select(currentLabel);
		}

	}

	public static void flushChoiceBox() {
		flushChoiceBox(cb);
	}

	// 选择连接框的选项修改选项
	public static void changeChoiceBox(String idx) {
		if (cb != null && idx.length() > 0) {
			cb.getSelectionModel().select(Integer.valueOf(idx));
			;
		}
	}

	public static void add(String name, DbConnectionPo o) {
		set.add(name);
		dbs.put(name, o);
		flushChoiceBox();
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
		flushChoiceBox();
	}

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
	public static Connection aliveConn() {
		Connection conn = null;
		for (String key : set) {
			DbConnectionPo po = dbs.get(key);
			if (po.isAlive()) {
				conn = po.getConn();
			}
		}
		return conn;
	}

}
