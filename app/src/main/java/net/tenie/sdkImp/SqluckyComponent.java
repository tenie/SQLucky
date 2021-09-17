package net.tenie.sdkImp;

import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.fx.component.MyTab;
import net.tenie.lib.tools.IconGenerator; 

public class SqluckyComponent implements AppComponent { 

	@Override
	public void addTitledPane(TitledPane tp) {
		Accordion ad = ComponentGetter.infoAccordion;
		if(ad != null) {
			ad.getPanes().add(tp);
		} 
	}

	@Override
	public void addIconBySvg(String name, String svg) {
		IconGenerator.addSvgStr(name, svg);		
	}

	@Override
	public SqluckyTab sqluckyTab() { 
		return new MyTab();
	}

	@Override
	public SqluckyTab sqluckyTab(String TabName) { 
		return new MyTab(TabName);
	}

	@Override
	public SqluckyTab sqluckyTab(DocumentPo po) { 
		return new MyTab(po);
	}

	@Override
	public Region getIconUnactive(String name) {
		return IconGenerator.svgImageUnactive( name); 
	}

	@Override
	public Region getIconDefActive(String name) {
		return IconGenerator.svgImageDefActive( name); 
	}

}
