package net.tenie.fx.PropertyPo;

import java.io.Serializable;

import javafx.scene.Node;
import net.tenie.fx.component.TreeItem.ConnItemContainer;
import net.tenie.fx.component.TreeItem.ConnItemDbObjects;

/**
 * 脚本po
 * @author tenie
 *
 */
public class ScriptPo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String title;
	private String text;
	private String fileName;
	private String encode;
	private int paragraph;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
