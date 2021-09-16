package net.tenie.sdkImp;

import javafx.scene.control.Accordion;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.ImageViewGenerator; 

public class SqluckyComponent implements AppComponent {

	@Override
	public TabPane dataTabPane() { 
		return ComponentGetter.dataTabPane;
	}

	@Override
	public TabPane mainTabPane() {
		return ComponentGetter.mainTabPane;
	}

	@Override
	public Stage primaryStage() {
		 
		return ComponentGetter.primaryStage;
	}

	@Override
	public Accordion infoAccordion() { 
		return  ComponentGetter.infoAccordion;
	}

	@Override
	public void addTitledPane(TitledPane tp) {
		Accordion ad = ComponentGetter.infoAccordion;
		if(ad != null) {
			ad.getPanes().add(tp);
		} 
	}

	@Override
	public void addIconBySvg(String name, String svg) {
		ImageViewGenerator.addSvgStr(name, svg);		
	}

}
