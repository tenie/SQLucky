package net.tenie.plugin.DataModel.xmlCDM;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

//<Model xmlns:a="attribute" xmlns:c="collection" xmlns:o="object">
//@JacksonXmlRootElement(namespace = "object", localName = "Column")
//@JsonIgnoreProperties
public class xmlPdmOColumn {
	
//	private String id;
	@JacksonXmlProperty(namespace = "attribute", localName = "ObjectID")
	private String objectId;
	

	@JacksonXmlProperty(namespace = "attribute", localName = "Name")
	private String name;

	@JacksonXmlProperty(namespace = "attribute", localName = "Code")
	private String code;

	@JacksonXmlProperty(namespace = "attribute", localName = "Comment")
	private String comment;

	@JacksonXmlProperty(namespace = "attribute", localName = "DataType")
	private String dataType;

	@JacksonXmlProperty(namespace = "attribute", localName = "Length")
	private String length;
	
	// 是否必填
	@JacksonXmlProperty(namespace = "attribute", localName = "Mandatory")
	private String mandatory;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

 
	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	@Override
	public String toString() {
		return "xmlPdmOColumn [objectId=" + objectId + ", name=" + name + ", code=" + code + ", comment=" + comment
				+ ", dataType=" + dataType + ", length=" + length + "]";
	}

//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}
    
    
    
}
