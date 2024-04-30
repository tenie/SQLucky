package net.tenie.Sqlucky.sdk.component;

import javafx.scene.control.IndexRange;
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

	/**
	 * 显示查找替换组件
	 * @param showReplace
	 * @param findText
	 * @return
	 */
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

	/**
	 * 隐藏查找替换组件
	 */
	public   void  hiddenFindReplaceBox(){
		if( sqluckyEditor == null ){
			return;
		}
		sqluckyEditor.getChildren().remove(this.findReplaceTextBox);
	}

	/**
	 * 判断查找组件是否已经显示状态
	 * @return
	 */
	public boolean findIsShowing(){
//		if( sqluckyEditor.getChildren().contains(this.findReplaceTextBox)){
//			this.findReplaceTextBox
//		}
		if(findReplaceTextBox == null ){
			return false;
		}
		return  sqluckyEditor.getChildren().contains(this.findReplaceTextBox);
	}

	/**
	 * 获取光标所在的单词
	 * @param codeArea
	 * @param codeAreaAnchor
	 * @param endStrVal
	 * @return
	 */
	public static IndexRange getAnchorWord(MyCodeArea codeArea , int codeAreaAnchor, String endStrVal){
		int anchorIdx = codeAreaAnchor;
		int startIdx = anchorIdx -1;
		int endIdx = anchorIdx + 1;

		boolean tf = true;
		// 包含这些字符中, 就停止查找
		String endString = ". \t\n;,/\\:;'\"`";
		if(StrUtils.isNotNullOrEmpty(endStrVal)){
			endString = endStrVal;
		}

		// 头部位置的查找
		while (tf){
			if(startIdx <0 ){
				break;
			}
			String  startStr = codeArea.getText(startIdx,anchorIdx);
			if(endString.contains(startStr)){
				tf = false;
			}else{
				anchorIdx = startIdx;
				startIdx--;
			}
		}

		// 尾部位置的查找
		tf = true;
		anchorIdx = codeAreaAnchor;
		while (tf){
			if( endIdx > codeArea.getLength()){
				break;
			}
			String  endStr = codeArea.getText(anchorIdx,endIdx);
			if(endString.contains(endStr)){
				tf = false;
			}else{
				anchorIdx = endIdx;
				endIdx++;

			}
		}
		if( startIdx+1 < endIdx-1){
			IndexRange indexRange = new IndexRange(startIdx+1, endIdx-1);
			return indexRange;
		}
		return null;
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
