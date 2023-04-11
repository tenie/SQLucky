package net.tenie.Sqlucky.sdk.ui;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;

public class SqluckyStage {
	private Stage	stage; // = new Stage();
	private Scene scene ; // = new Scene(stackPane);
	private  StackPane stackPane ;
	
	public SqluckyStage(Node pane) {
		 stage =  new Stage();
		 
		 stackPane = new StackPane( pane);
		 scene = new Scene(stackPane);
		 stage.setScene(scene);
		 
		 //css
		 CommonUtility.loadCss(scene);
		 // 图标
		 stage.getIcons().add(ComponentGetter.LogoIcons);
		 
		 stage.focusedProperty().addListener(e->{
			if(stage.isFocused()) {
				ComponentGetter.currentStackPane = stackPane;
			} 
			
		});
			
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public StackPane getStackPane() {
		return stackPane;
	}

	public void setStackPane(StackPane stackPane) {
		this.stackPane = stackPane;
	}
	
	
	
}
