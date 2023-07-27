package net.tenie.Sqlucky.sdk;

import net.tenie.Sqlucky.sdk.po.SheetDataValue;

/**
 * 底部窗口的显示页
 * 
 * @author tenie
 *
 */
public interface SqluckyBottomSheet {
	public void show(int idx, boolean disable);

	public void show();

//	当窗口失去焦点 3秒后关闭(移除)
	public void showAndDelayRemoveTab();

	public SheetDataValue getTableData();

//	public Button getSaveBtn();
//
//	public Button getDetailBtn();

}
