package net.tenie.Sqlucky.sdk.po;

import java.io.Serializable;

/**
 * 脚本po
 * @author tenie
 *
 */
public class DocumentPo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String title;
	private String text;
	private String fileName;
	private String encode;
	private Integer paragraph;
	private boolean isDir;
	
	public DocumentPo() {
		this.id = null;
		this.title = "";
		this.text = "";
		this.fileName = "";
		this.encode = "UTF-8";
		this.paragraph = 0;
		this.isDir = false;
		
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
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Integer getParagraph() {
		return paragraph;
	}
	public void setParagraph(Integer paragraph) {
		this.paragraph = paragraph;
	}

	
	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + (isDir ? 1231 : 1237);
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
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (isDir != other.isDir)
			return false;
		return true;
	}

 
	


}
