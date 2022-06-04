package net.tenie.Sqlucky.sdk;

import javafx.scene.control.Button;
import net.tenie.Sqlucky.sdk.po.MyTabDataValue;

/**
 * 底部窗口的显示页
 * @author tenie
 *
 */
public interface SqluckyBottomSheet {
	public void show();
	public MyTabDataValue getTableData();
	public Button getSaveBtn();
	public Button getDetailBtn();
	
}
