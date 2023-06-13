package net.tenie.Sqlucky.sdk.component;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import net.tenie.Sqlucky.sdk.config.KeyBindingCache;
import net.tenie.Sqlucky.sdk.po.KeyBindingItemPo;

public class MyTooltipTool extends Tooltip {
	private String msg;

	public static Tooltip instance(String msg) {
		MyTooltipTool mtt = new MyTooltipTool(msg);
		return mtt;
	}

	public MyTooltipTool(String msg) {
		super("");

		this.msg = msg;
		setShowDelay(new Duration(100));

		this.setOnShowing(e -> {
			KeyBindingItemPo po = KeyBindingCache.findByActionName(msg);
			String keyStr = "";
			if (po != null) {
				keyStr = po.getKeys();
			}

			String str = String.format("%-30s %s", msg, keyStr);
			this.setText(str);
		});

	}
}
