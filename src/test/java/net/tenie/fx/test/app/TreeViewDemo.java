package net.tenie.fx.test.app;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *  demo
 * 
 * @author tenie
 *
 */
public class TreeViewDemo extends Application {

	@Override
	public void start(Stage primaryStage) {
		Accordion ad = new Accordion();
		TitledPane tp = new TitledPane();
		tp.setText("??");
		
		TitledPane tp2 = new TitledPane();
		tp2.setText("2222?");
		
		ad.getPanes().add(tp);
		ad.getPanes().add(tp2);
		
		
	    VBox treeContainer = new VBox();
	    tp.setContent(treeContainer);

	    TreeItem<String> hiddenRootItem = new TreeItem<String>();
	    TreeView<String> tree = new TreeView<String>(hiddenRootItem);
	    tree.setShowRoot(false);

	    treeContainer.getChildren().add(tree);

	    for (int j = 0; j < 3; ++j) {
	        TreeItem<String> rootItem = new TreeItem<String>("Item " + j);
	        rootItem.setExpanded(true);
	        String[] names = {"SubItem1", "SubItem2", "SubItem3", "SubItem4", "SubItem5", "SubItem6", "SubItem7",};
	        for (int i = 0; i < names.length; i++) {
	            TreeItem<String> item = new TreeItem<String>(names[i]);
	            rootItem.getChildren().add(item);
	        }
	        hiddenRootItem.getChildren().add(rootItem);
	    }

//	    StackPane root = new StackPane();
//	    root.getChildren().add(treeContainer);
	    Scene scene = new Scene(ad, 300, 250);
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
