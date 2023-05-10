package net.tenie.Sqlucky.sdk;

import java.util.Collection;

import net.tenie.Sqlucky.sdk.po.db.TablePo;

public interface AutoComplete {
	public   void hide();
	public   void backSpaceHide(SqluckyCodeArea codeArea);
	public   boolean isShow();
	public   void showPop(double x, double y, String fStr);
	public   Integer getMyTabId();
	public   void cacheTablePo(TablePo tabpo);
	public   void cacheTextWord();
	public   Collection<TablePo> getCacheTableFields();
	public   void codeAreaReplaceString( String selectVal , TablePo tabpo );
}
