package net.tenie.Sqlucky.sdk;

import javafx.scene.control.Button;
import net.tenie.Sqlucky.sdk.po.BottomSheetDataValue;

/**
 * 底部窗口的显示页
 * @author tenie
 *
 */
public interface SqluckyBottomSheet {
	public void show();
	public BottomSheetDataValue getTableData();
	public Button getSaveBtn();
	public Button getDetailBtn();
	
	
}
