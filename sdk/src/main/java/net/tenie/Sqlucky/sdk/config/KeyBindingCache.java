package net.tenie.Sqlucky.sdk.config;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import net.tenie.Sqlucky.sdk.db.PoDao;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.KeyBindingItemPo;
import net.tenie.Sqlucky.sdk.po.db.KeysBindingPO;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

/**
 * 菜单按钮会缓存起来, 使用按钮的快捷键设置来设置全局的快捷键设置
 * 
 * @author tenie
 *
 */
public class KeyBindingCache {
	static List<KeyBindingItemPo> items = new ArrayList<>();

	public static void add(KeyBindingItemPo po) {
		items.add(po);
	}

	/**
	 * 通过按键key 找对象
	 * 
	 * @param key
	 * @return
	 */
	public static KeyBindingItemPo findByKey(String key) {
		for (KeyBindingItemPo po : items) {
			if (po.getKeys().equals(key)) {
				return po;
			}
		}

		return null;
	}

	/**
	 * 通过要执行的名称找对象
	 * 
	 * @param actionName
	 * @return
	 */
	public static KeyBindingItemPo findByActionName(String actionName) {
		for (KeyBindingItemPo po : items) {
			if (po.getActionName().equals(actionName)) {
				return po;
			}
		}

		return null;
	}

	/**
	 * 菜单按钮绑定到po对象上
	 * 
	 * Command (or Cmd) ⌘ Shortcut Option (or Alt) ⌥ Caps Lock ⇪ Shift ⇧ Control (or
	 * Ctrl) ⌃
	 * 
	 * @param e
	 * @return
	 * 
	 * @param menuItem
	 */
	public static String macKeyChange(String keyStr) {

		if (CommonUtils.isMacOS()) {
			if (keyStr.contains("⌘")) {
				keyStr = keyStr.replace("⌘", "Shortcut");
			}
			if (keyStr.contains("⌥")) {
				keyStr = keyStr.replace("⌥", "Alt");
			}
			if (keyStr.contains("⌃")) {
				keyStr = keyStr.replace("⌃", "Ctrl");
			}
			if (keyStr.contains("⇧")) {
				keyStr = keyStr.replace("⇧", "Shift");
			}
			if (keyStr.contains("⇪")) {
				keyStr = keyStr.replace("⇪", "Caps Lock");
			}

		}

		return keyStr;
	}

	/**
	 * 快捷键和菜单按钮绑定
	 * 
	 * @param menuItem
	 */
	public static void menuItemBinding(MenuItem menuItem) {
		String menuText = menuItem.getText();
		KeyBindingItemPo po = findByActionName(menuText.trim());
		if (po != null) {
			po.setMenuItem(menuItem);
			String keyStr = po.getKeys();
			keyStr = macKeyChange(keyStr);
			menuItem.setAccelerator(KeyCombination.keyCombination(keyStr));
		}
	}

	/**
	 * 快捷键和菜单按钮绑定, 重载方法使用可变参数
	 * 
	 * @param items
	 */
	public static void allMenuItemBinding(MenuItem... items) {
		if (items != null) {
			for (var item : items) {
				menuItemBinding(item);
			}
		}

	}

	/*
	 * action 重新绑定快捷键
	 */
	public static void rebingdingKey(String ActionName, String key) {
		KeyBindingItemPo po = findByActionName(ActionName);
		key = macKeyChange(key);
		po.setKeys(key);
		var menuItem = po.getMenuItem();
		menuItem.setAccelerator(KeyCombination.keyCombination(po.getKeys()));
	}

	static {
		// 从数据库获取按键对应的函数名称
		KeysBindingPO po = new KeysBindingPO();
		var conn = SqluckyAppDB.getConn();
		try {
			List<KeysBindingPO> ls = PoDao.select(conn, po);
			if (ls != null && ls.size() > 0) {
				for (KeysBindingPO poVal : ls) {
					KeyBindingItemPo item = new KeyBindingItemPo(poVal.getActionName(), poVal.getBinding());
					items.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}

	}

}
