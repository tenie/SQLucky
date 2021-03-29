package net.tenie.fx.component;

import javafx.scene.control.TextField;

public class TextFieldFactory {
	
	// 只能输入数字的输入框
	public static TextField numTextField(int limit) {
		 TextField rows = new TextField() {
	            @Override
	            public void replaceText(int start, int end, String text) {
	                if (!text.matches("[a-z, A-Z]")) {
	                    super.replaceText(start, end, text);   
	                }
//	                label.setText("Enter a numeric value");
	            }
	 
	            @Override
	            public void replaceSelection(String text) {
	                if (!text.matches("[a-z, A-Z]")) {
	                    super.replaceSelection(text);
	                }
	            }
	        };
	     return rows;
	}
}
