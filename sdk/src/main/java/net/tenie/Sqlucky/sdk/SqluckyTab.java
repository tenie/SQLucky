package net.tenie.Sqlucky.sdk;
 
import java.io.File;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.po.DocumentPo;

public interface SqluckyTab  {
	public SqluckyCodeAreaHolder getSqlCodeArea();
	public DocumentPo getDocumentPo();
	public VBox getVbox();
	public  void mainTabPaneAddMyTab() ;
	public String getTitle();
	public String getTabSqlText();
	
	public void setFile(File file);
	public File getFile();
	public Region getIcon();
	public void setIcon(Region icon);
	
	public String getFileText();
	public void setFileText(String text);
	
	public boolean isModify();
	
	public void saveTextAction() ;
}
