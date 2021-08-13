package net.tenie.lib.db.h2;
/*   @author tenie */
public class H2SqlTextSavePo {
	private String title;
	private String text;
	private String fileName;
	private String encode;
	private int paragraph;
	private Integer scriptId;
	
	
	public Integer getScriptId() {
		return scriptId;
	}
	public void setScriptId(Integer scriptId) {
		this.scriptId = scriptId;
	}
	public String getEncode() {
		return encode;
	}
	public void setEncode(String encode) {
		this.encode = encode;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	 
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getParagraph() {
		return paragraph;
	}
	public void setParagraph(int paragraph) {
		this.paragraph = paragraph;
	}
	
	
}
