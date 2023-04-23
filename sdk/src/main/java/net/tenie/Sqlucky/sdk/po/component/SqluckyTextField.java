package net.tenie.Sqlucky.sdk.po.component;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;

public class SqluckyTextField {
	private AnchorPane pane;
	private TextField txt;
	
	
	public SqluckyTextField(TextField tf) {
		this.txt = tf;
		initTextField();
	}
	public SqluckyTextField() {
		initTextField();
	}
	
	public void initTextField() {
		if(txt == null) {
			txt = new TextField();
			txt.setPrefWidth(200);
			txt.setPrefHeight(25);
			txt.setMaxHeight(25);
			txt.getStyleClass().add("myTextField");
			
//			int x = 0;
//			txt.setLayoutX(x);
//			txt.setLayoutY(1);
		}
		
		
		Button clean = new Button(); 
		 
		AnchorPane.setRightAnchor(clean, 10.0);
		AnchorPane.setTopAnchor(clean, 4.0); 
		clean.setMaxSize(12, 12);
		
		clean.setGraphic(IconGenerator.svgImageUnactive("times-circle" , 14));
		clean.getStyleClass().add("myCleanBtn");
		clean.setVisible(false); //clean 按钮默认不显示, 只有在鼠标进入搜索框才显示
		
		clean.setOnAction(e->{
			txt.clear();
		});
		pane = new AnchorPane();
		pane.setPrefHeight(txt.getPrefHeight() + 1);
		pane.setMinHeight(txt.getMinHeight() + 1);
		pane.setOnMouseEntered(e->{
			clean.setVisible(true);
		});
		pane.setOnMouseExited(e->{
			clean.setVisible(false);
		});
		
		pane.getChildren().addAll( txt , clean); 
	
	}


	public AnchorPane getPane() {
		return pane;
	}


	public void setPane(AnchorPane pane) {
		this.pane = pane;
	}


	public TextField getTxt() {
		return txt;
	}


	public void setTxt(TextField txt) {
		this.txt = txt;
	}
	
	
	
}
