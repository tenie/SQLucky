package net.tenie.Sqlucky.sdk.config;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import net.tenie.Sqlucky.sdk.db.PoDao;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.KeyBindingItemPo;
import net.tenie.Sqlucky.sdk.po.db.KeysBindingPO;

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
	 * @param menuItem
	 */
	public static void menuItemBinding(MenuItem menuItem) {
		String menuText = menuItem.getText();
		KeyBindingItemPo po = findByActionName(menuText.trim());
		if (po != null) {
			po.setMenuItem(menuItem);
			menuItem.setAccelerator(KeyCombination.keyCombination(po.getKeys()));
		}
	}

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

		// 给函数定义字符串名称
//		if (KeyBindingUtils.keyAction == null) {
//			KeyBindingUtils.keyAction = new HashMap<>();
//			// 注释代码 Ctrl /
//			KeyBindingUtils.keyAction.put("Line Comment", v -> {
//				CommonAction.addAnnotationSQLTextSelectText();
//			});
//
//			// 运行sql Ctrl Enter
//			KeyBindingUtils.keyAction.put("Run SQL", v -> {
//				RunSQLHelper.runSQLMethod();
//			});
//
//			// 运行sql 当前行 Alt R
//			KeyBindingUtils.keyAction.put("Run SQL Current Line", v -> {
//				RunSQLHelper.runCurrentLineSQLMethod();
//			});
//
//			// 新代码编辑 Ctrl T
//			KeyBindingUtils.keyAction.put("Add New Edit Page", v -> {
//				MyAreaTab.addCodeEmptyTabMethod();
//			});
//
//			// 保存代码 Ctrl S
//			KeyBindingUtils.keyAction.put("Save", v -> {
//				CommonAction.saveSqlAction();
//			});
//
//			// 格式化代码 Ctrl Shift F
//			KeyBindingUtils.keyAction.put("Format", v -> {
//				CommonAction.formatSqlText();
//			});
//
//			// 查找 Ctrl F
//			KeyBindingUtils.keyAction.put("Find", v -> {
//				CommonUtility.findReplace(false);
//			});
//
//			// 查找 替换 Ctrl R
//			KeyBindingUtils.keyAction.put("Replace", v -> {
//				CommonUtility.findReplace(true);
//			});
//
//			// 打开文件 Ctrl O
//			KeyBindingUtils.keyAction.put("Open", v -> {
//				CommonAction.openSqlFile();
//			});
//			// 关闭app Ctrl Q
//			KeyBindingUtils.keyAction.put("Exit", v -> {
//				CommonAction.mainPageClose();
//			});
//
//			// 关闭表格 Alt W
//			KeyBindingUtils.keyAction.put("Close Data Table", v -> {
//				CommonAction.closeDataTable();
//			});
//
//			// 字符串大写 Ctrl Shift X
//			KeyBindingUtils.keyAction.put("Upper Case", v -> {
//				CommonAction.UpperCaseSQLTextSelectText();
//			});
//
//			// 字符串小写 Ctrl Shift Y
//			KeyBindingUtils.keyAction.put("Lower Case", v -> {
//				CommonAction.LowerCaseSQLTextSelectText();
//			});
//
//			// 下划线转驼峰 Ctrl Shift R
//			KeyBindingUtils.keyAction.put("Underscore To Hump", v -> {
//				CommonAction.underlineCaseCamel();
//			});
//
//			// 驼峰转下划线 Ctrl Shift T
//			KeyBindingUtils.keyAction.put("Underscore To Hump", v -> {
//				CommonAction.CamelCaseUnderline();
//			});
//
//			// 隐藏副面板 Ctrl H
//			KeyBindingUtils.keyAction.put("Hide/Show All Panels", v -> {
//				CommonAction.hideLeftBottom();
//			});
//
//			// 字体变大 Ctrl =
//			KeyBindingUtils.keyAction.put("Font Size +", v -> {
//				CommonAction.changeFontSize(true);
//			});
//
//			// 字体变小 Ctrl -
//			KeyBindingUtils.keyAction.put("Font Size -", v -> {
//				CommonAction.changeFontSize(false);
//			});
//		}

	}

}
