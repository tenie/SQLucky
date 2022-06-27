package net.tenie.Sqlucky.sdk;
 
import java.io.File;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.component.FindReplaceTextPanel;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
/**
 * 文本页面
 * @author tenie
 *
 */
public interface SqluckyTab  {
	public SqluckyCodeAreaHolder getSqlCodeArea();
	public DocumentPo getDocumentPo();
	
	// 放查找面板, 文本area 的容器
	public VBox getVbox();
	// 查找面板
//	public void saveFindReplacePanel(FindReplaceTextPanel panel);
	public FindReplaceTextPanel getFindReplacePanel();
	public void setFindReplacePanel(FindReplaceTextPanel findReplacePanel);
	// 
	public void cleanFindReplacePanel();
	
	public  void mainTabPaneAddSqlTab() ;
	public  void mainTabPaneAddTextTab() ;
	// 主界面上存在否
	public boolean existTab();
	// 存在, 就显示出来
	public boolean existTabShow(); 
	// 显示的Tab 是否是当前的对象
	public boolean isShowing(); 
	
	public String getTitle();
	public String getAreaText();
	
	public void setFile(File file);
	public File getFile();
	public Region getIcon();
	public void setIcon(Region icon);
	
	public String getFileText();
	public void setFileText(String text);
	
	public boolean isModify();
	
	public void saveTextAction() ;
}
