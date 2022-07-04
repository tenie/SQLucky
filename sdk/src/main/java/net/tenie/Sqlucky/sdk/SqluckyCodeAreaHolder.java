package net.tenie.Sqlucky.sdk;

import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;

public interface SqluckyCodeAreaHolder {
	public void highLighting(String str);

	public void highLighting();

	public void errorHighLighting(int begin, String str);

	public void changeCodeAreaLineNoThemeHelper();

	public MyCodeArea getCodeArea();

	public void callPopup();
	public   void codePopup(KeyEvent e) ;
//	隐藏自动补全
	public void  hideAutoComplete();
//	显示自动补全
	public void showAutoComplete(double x , double y , String str);
	
	public void nextBookmark(boolean tf);
	public StackPane getCodeAreaPane() ;
	public StackPane getCodeAreaPane(String text, boolean editable) ;
	
}
