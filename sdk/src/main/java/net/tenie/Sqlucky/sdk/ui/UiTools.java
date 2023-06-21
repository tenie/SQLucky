package net.tenie.Sqlucky.sdk.ui;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class UiTools {
	/**
	 * 给TextField 添加清空按钮
	 * 
	 * @param txt
	 * @return
	 */
	public static AnchorPane textFieldAddCleanBtn(TextField txt) {
		AnchorPane filterPane = new AnchorPane();
		filterPane.setPrefHeight(30);
		filterPane.setMinHeight(30);

		Button clean = new Button();

		AnchorPane.setRightAnchor(clean, 9.9);
		AnchorPane.setTopAnchor(clean, 5.0);
		clean.setMaxSize(12, 12);

		clean.setGraphic(IconGenerator.svgImageUnactive("times-circle", 14));
		clean.getStyleClass().add("myCleanBtn");
		clean.setVisible(false); // clean 按钮默认不显示, 只有在鼠标进入搜索框才显示

		clean.setOnAction(e -> {
			txt.clear();
		});

		filterPane.setOnMouseEntered(e -> {
			clean.setVisible(true);
		});
		filterPane.setOnMouseExited(e -> {
			clean.setVisible(false);
		});

		filterPane.getChildren().addAll(txt, clean);

		return filterPane;
	}
}
