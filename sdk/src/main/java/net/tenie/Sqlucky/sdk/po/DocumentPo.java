package net.tenie.Sqlucky.sdk.po;

import java.io.File;
import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

import javafx.scene.layout.Region;

/**
 * 脚本po
 * @author tenie
 *
 */
public class DocumentPo implements Serializable {

	public static int IS_SQL = 1;
	public static int IS_TEXT = 2;
	
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String title;          // 文件名
	private String text;
	private String fileFullName;       //文件全路径名称
	private String encode;
	private Integer paragraph;
	private File file;
	private Region icon;
	private int type;
	
	public DocumentPo() {
		this.id = null;
		this.title = "";
		this.text = "";
		this.fileFullName = "";
		this.encode = "UTF-8";
		this.paragraph = 0;
		this.file = null;
		this.icon = null;
		this.type = IS_SQL;
		
	}
	
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
	public String getFileFullName() {
		return fileFullName;
	}
	public void setFileFullName(String fileName) {
		this.fileFullName = fileName;
	}
	public Integer getParagraph() {
		return paragraph;
	}
	public void setParagraph(Integer paragraph) {
		this.paragraph = paragraph;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((fileFullName == null) ? 0 : fileFullName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocumentPo other = (DocumentPo) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (fileFullName == null) {
			if (other.fileFullName != null)
				return false;
		} else if (!fileFullName.equals(other.fileFullName))
			return false;
		return true;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Region getIcon() {
		return icon;
	}

	public void setIcon(Region icon) {
		this.icon = icon;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "DocumentPo [id=" + id + ", title=" + title + ", text=" + text + ", fileFullName=" + fileFullName
				+ ", encode=" + encode + ", paragraph=" + paragraph + ", file=" + file + ", icon=" + icon + ", type="
				+ type + "] \n";
	}

	
	 
	  public String toJsone() { 
	       
		    JSONObject jsonObject = (JSONObject) JSONObject.toJSON(this);
	        System.out.println("Java对象转化为JSON对象\n" + jsonObject.toJSONString());//{"name":"公众号编程大道","age":2,"sex":"m"}
 
		  return jsonObject.toJSONString();
	  }
	


}
