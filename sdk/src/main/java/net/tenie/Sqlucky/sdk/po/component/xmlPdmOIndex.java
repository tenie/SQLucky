package net.tenie.Sqlucky.sdk.po.component;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

//<Model xmlns:a="attribute" xmlns:c="collection" xmlns:o="object">
@JacksonXmlRootElement(namespace = "object", localName = "Column")
public class xmlPdmOIndex {

	@JacksonXmlProperty(namespace = "attribute", localName = "ObjectID")
	private String objectId;

	@JacksonXmlProperty(namespace = "attribute", localName = "Name")
	private String name;

	@JacksonXmlProperty(namespace = "attribute", localName = "Code")
	private String code;

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

	@Override
	public String toString() {
		return "xmlPdmOIndex [objectId=" + objectId + ", name=" + name + ", code=" + code + "]";
	}
    
    
	
    
}
