package net.tenie.Sqlucky.sdk.component;

import java.util.function.Consumer;

import javafx.scene.control.Button;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;

public class MyCellOperateButton {
	private Button btn;
	private Consumer<ResultSetRowPo> btnCaller;
	
	public MyCellOperateButton(Button btn, Consumer<ResultSetRowPo> btnCaller) {
		this.btn = btn;
		this.btnCaller = btnCaller;
	}
	public Button getBtn() {
		return btn;
	}
	public void setBtn(Button btn) {
		this.btn = btn;
	}
	public Consumer<ResultSetRowPo> getBtnCaller() {
		return btnCaller;
	}
	public void setBtnCaller(Consumer<ResultSetRowPo> btnCaller) {
		this.btnCaller = btnCaller;
	}
	
	
}
