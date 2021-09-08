package net.tenie.plugin.sdk;

import javafx.scene.control.Accordion;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

public interface AppComponent {
	TabPane dataTab();
	TabPane mainTabPane();
	Stage primaryStage();
	Accordion infoAccordion();
	
	void addTitledPane(TitledPane tp);
}
