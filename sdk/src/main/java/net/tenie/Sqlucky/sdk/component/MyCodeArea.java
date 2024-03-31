package net.tenie.Sqlucky.sdk.component;

import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.editor.FindReplaceTextBox;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.fxmisc.richtext.CodeArea;

import net.tenie.Sqlucky.sdk.SqluckyLineNumberNode;

import java.util.function.Consumer;

public class MyCodeArea extends CodeArea {
	private SqluckyLineNumberNode mylineNumber;
	private String titleName;
	private SqluckyEditor sqluckyEditor;
	private FindReplaceTextBox findReplaceTextBox;
	public MyCodeArea(SqluckyEditor sqluckyEditor){
		this.sqluckyEditor = sqluckyEditor;
	}

	public SqluckyLineNumberNode getMylineNumber() {
		return mylineNumber;
	}

	public void setMylineNumber(SqluckyLineNumberNode mylineNumber) {
		this.mylineNumber = mylineNumber;
	}

	public CodeArea getCodeArea() {
		return this;
	}

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public FindReplaceTextBox showFindReplaceTextBox(boolean showReplace, String findText){
		if( sqluckyEditor == null ){
			return null;
		}
		Consumer<VBox> hiddenBox = v->{
			sqluckyEditor.getChildren().remove(v);
		};

		if(StrUtils.isNullOrEmpty(findText)){
			findText = sqluckyEditor.getCodeArea().getSelectedText();
		}

		if(this.findReplaceTextBox == null){
			this.findReplaceTextBox = new FindReplaceTextBox(showReplace, findText,  sqluckyEditor, hiddenBox);
//			VBox fdbox = findReplaceTextBox.getfindReplaceBox();
//			sqluckyEditor.setFdbox(fdbox);
//			sqluckyEditor.getChildren().add(0,sqluckyEditor.getFdbox());
			sqluckyEditor.getChildren().add(0, this.findReplaceTextBox );
		}else {
			this.findReplaceTextBox.showHiddenReplaceBox(showReplace);
			this.findReplaceTextBox.setText(findText);
//			if(! sqluckyEditor.getChildren().contains(sqluckyEditor.getFdbox())){
//				sqluckyEditor.getChildren().add(0,sqluckyEditor.getFdbox());
//			}
			if(! sqluckyEditor.getChildren().contains(this.findReplaceTextBox)){
				sqluckyEditor.getChildren().add(0,this.findReplaceTextBox);
			}
		}

		return  this.findReplaceTextBox;
	}

	public   void  hiddenFindReplaceBox(){
		if( sqluckyEditor == null ){
			return;
		}
		sqluckyEditor.getChildren().remove(this.findReplaceTextBox);
	}

	public boolean findIsShowing(){
//		if( sqluckyEditor.getChildren().contains(this.findReplaceTextBox)){
//			this.findReplaceTextBox
//		}
		if(findReplaceTextBox == null ){
			return false;
		}
		return  sqluckyEditor.getChildren().contains(this.findReplaceTextBox);
	}


	public SqluckyEditor getSqluckyEditor() {
		return sqluckyEditor;
	}

	public void setSqluckyEditor(SqluckyEditor sqluckyEditor) {
		this.sqluckyEditor = sqluckyEditor;
	}


	public FindReplaceTextBox getFindReplaceTextBox() {
		return findReplaceTextBox;
	}


}
