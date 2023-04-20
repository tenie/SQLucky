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
	private Integer openStatus;  // 是否在文本编辑区域中打开, 1: 打开; 0: 没打开
	private Integer isActivate;  // 是否激活, 在编辑区域打开的状态, 只有openStatus 为1时才有意义
	
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
		this.openStatus = 0;
		this.isActivate = 0;
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

	
	public Integer getOpenStatus() {
		return openStatus;
	}

	public void setOpenStatus(Integer openStatus) {
		this.openStatus = openStatus;
	}

	 
	public Integer getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(Integer isActivate) {
		this.isActivate = isActivate;
	}

	@Override
	public String toString() {
		return "DocumentPo [id=" + id + ", title=" + title + ", text=" + text + ", fileFullName=" + fileFullName
				+ ", encode=" + encode + ", paragraph=" + paragraph + ", file=" + file + ", icon=" + icon + ", type="
				+ type + ", openStatus=" + openStatus + ", isActivate=" + isActivate + "]";
	}
	
	/**
	 * 对象转换为json 字符串
	 * @return
	 */
	public String toJsone() {

		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(this);
//		System.out.println("Java对象转化为JSON对象\n" + jsonObject.toJSONString());// {"name":"公众号编程大道","age":2,"sex":"m"}

		return jsonObject.toJSONString();
	}
	/**
	 * 将json字符串转换对象
	 * @param json
	 * @return
	 */
	public static DocumentPo toPo(String json) { 
		DocumentPo val = JSONObject.parseObject(json, DocumentPo.class); 
		return val;
	}


}
