package net.tenie.Sqlucky.sdk;
 
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.po.DocumentPo;

public interface SqluckyTab  {
	public SqluckyCodeAreaHolder getSqlCodeArea();
	public DocumentPo getDocumentPo();
	public VBox getVbox();
	public  void mainTabPaneAddMyTab() ;
	public String getTitle();
	public String getTabSqlText();
}
