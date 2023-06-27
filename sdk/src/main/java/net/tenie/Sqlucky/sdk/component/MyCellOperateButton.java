package net.tenie.Sqlucky.sdk.component;

import java.util.function.Consumer;

import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;

public class MyCellOperateButton {
	private String buttonName;
	private Consumer<ResultSetRowPo> btnCaller;

	public MyCellOperateButton(String buttonName, Consumer<ResultSetRowPo> btnCaller) {
		this.buttonName = buttonName;
		this.btnCaller = btnCaller;
	}

	public String getButtonName() {
		return buttonName;
	}

	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}

	public Consumer<ResultSetRowPo> getBtnCaller() {
		return btnCaller;
	}

	public void setBtnCaller(Consumer<ResultSetRowPo> btnCaller) {
		this.btnCaller = btnCaller;
	}

}
