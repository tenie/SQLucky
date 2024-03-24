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

	public void showFindReplaceTextBox(boolean showReplace, String findText){
		if( sqluckyEditor == null ){
			return;
		}
		Consumer<VBox> hiddenBox = v->{
			sqluckyEditor.getChildren().remove(v);
		};

		if(StrUtils.isNullOrEmpty(findText)){
			findText = sqluckyEditor.getCodeArea().getSelectedText();
		}

		if(sqluckyEditor.getFindReplaceTextBox() == null){
			FindReplaceTextBox findReplaceText = new FindReplaceTextBox(showReplace, findText,  sqluckyEditor, hiddenBox);
			sqluckyEditor.setFindReplaceTextBox(findReplaceText);
			VBox fdbox = findReplaceText.getfindReplaceBox();
			sqluckyEditor.setFdbox(fdbox);
			sqluckyEditor.getChildren().add(0,sqluckyEditor.getFdbox());
		}else {
			sqluckyEditor.getFindReplaceTextBox().showHiddenReplaceBox(showReplace);
			sqluckyEditor.getFindReplaceTextBox().setText(findText);
			if(! sqluckyEditor.getChildren().contains(sqluckyEditor.getFdbox())){
				sqluckyEditor.getChildren().add(0,sqluckyEditor.getFdbox());
			}
		}
	}

	public   void  hiddenFindReplaceBox(){
		if( sqluckyEditor == null ){
			return;
		}
		sqluckyEditor.getChildren().remove(sqluckyEditor.getFdbox());
	}


	public SqluckyEditor getSqluckyEditor() {
		return sqluckyEditor;
	}

	public void setSqluckyEditor(SqluckyEditor sqluckyEditor) {
		this.sqluckyEditor = sqluckyEditor;
	}
}
