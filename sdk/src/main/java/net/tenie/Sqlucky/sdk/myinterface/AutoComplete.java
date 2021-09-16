package net.tenie.Sqlucky.sdk.myinterface;

import java.util.Collection;

import net.tenie.Sqlucky.sdk.component.CodeArea.MyCodeArea;
import net.tenie.Sqlucky.sdk.po.TablePo;

public interface AutoComplete {
	public   void hide();
	public   void backSpaceHide(MyCodeArea codeArea);
	public   boolean isShow();
	public   void showPop(double x, double y, String fStr);
	public   Integer getMyTabId();
	public   void cacheTablePo(TablePo tabpo);
	public   void cacheTextWord();
	public   Collection<TablePo> getCacheTableFields();
	public   void codeAreaReplaceString( String selectVal , TablePo tabpo );
}
