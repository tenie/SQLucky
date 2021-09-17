package net.tenie.Sqlucky.sdk;

import javafx.scene.control.Accordion;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.po.DocumentPo;

public interface AppComponent {
	
	void addTitledPane(TitledPane tp);
	void addIconBySvg(String name, String svg);
	SqluckyTab sqluckyTab();
	SqluckyTab sqluckyTab(String TabName);
	SqluckyTab sqluckyTab(DocumentPo po);
	
	public Region getIconUnactive(String name);
	public Region getIconDefActive(String name);
	
}
