package net.tenie.Sqlucky.sdk;

import org.fxmisc.richtext.CodeArea;

import javafx.scene.input.KeyEvent;

public interface SqluckyCodeAreaHolder {
	public void highLighting(String str);

	public void highLighting();

	public void errorHighLighting(int begin, int length, String str);

	public void changeCodeAreaLineNoThemeHelper();

	public CodeArea getCodeArea();

	public void callPopup();
	public   void codePopup(KeyEvent e) ;
	public void  hideAutoComplete();
	public void showAutoComplete(double x , double y , String str);
	
	public void nextBookmark(boolean tf);
	
}
